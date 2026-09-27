require('dotenv').config();
const express = require('express');
const cors = require('cors');
const healthRoutes = require('./routes/health');
const eligibilityRoutes = require('./routes/eligibility');

const app = express();

// Configure CORS allowed origins (frontend dev ports)
const allowedOrigins = process.env.CORS_ORIGIN 
  ? process.env.CORS_ORIGIN.split(',').map(s => s.trim()) 
  : ['http://localhost:3000', 'http://localhost:5173', 'http://127.0.0.1:3000', 'http://127.0.0.1:5173'];

app.use(cors({
  origin: function (origin, callback) {
    // Allow requests with no origin (like mobile apps, curl, or server-to-server)
    if (!origin) return callback(null, true);
    if (allowedOrigins.indexOf(origin) !== -1) {
      return callback(null, true);
    }
    const msg = `The CORS policy for this site does not allow access from the specified Origin: ${origin}`;
    return callback(new Error(msg), false);
  },
  methods: ['GET', 'POST', 'OPTIONS'],
  allowedHeaders: ['Content-Type', 'Authorization']
}));

// Payload size limit: 2MB for rejection letter OCR text
app.use(express.json({ limit: '2mb' }));
app.use(express.urlencoded({ extended: true, limit: '2mb' }));

// Health check endpoint at root /health and /api/health
app.use('/', healthRoutes);
app.use('/api', healthRoutes);
app.get('/api', (req, res) => res.status(200).json({ status: 'ok', service: 'eligibility-analyzer' }));

// API routes mounted at /api
app.use('/api', eligibilityRoutes);

// 404 handler
app.use((req, res) => {
  res.status(404).json({ error: `Cannot ${req.method} ${req.originalUrl}` });
});

// Global error handler
app.use((err, req, res, next) => {
  if (err.type === 'entity.too.large') {
    return res.status(413).json({ error: 'Payload exceeds maximum allowed limit of 2MB.' });
  }

  // Handle CORS errors
  if (err.message && err.message.includes('CORS policy')) {
    return res.status(403).json({ error: err.message });
  }

  // Handle malformed JSON body
  if (err instanceof SyntaxError && err.status === 400 && 'body' in err) {
    return res.status(400).json({ error: 'Malformed JSON payload.' });
  }

  console.error('Unhandled Server Error:', err);
  return res.status(500).json({ error: 'An unexpected internal server error occurred.' });
});

module.exports = app;
