require('dotenv').config();
const express = require('express');
const cors = require('cors');
const healthRoutes = require('./routes/health');
const checklistRoutes = require('./routes/checklist');
const { requestTimeout } = require('./middleware/timeout');

const app = express();

// Configure CORS allowed origins (frontend dev ports)
const allowedOrigins = process.env.CORS_ORIGIN
  ? process.env.CORS_ORIGIN.split(',').map(s => s.trim())
  : ['http://localhost:3000', 'http://localhost:5173', 'http://127.0.0.1:3000', 'http://127.0.0.1:5173'];

app.use(cors({
  origin: function (origin, callback) {
    if (!origin) return callback(null, true);
    if (allowedOrigins.indexOf(origin) !== -1) {
      return callback(null, true);
    }
    const msg = `The CORS policy for this site does not allow access from Origin: ${origin}`;
    return callback(new Error(msg), false);
  },
  methods: ['GET', 'POST', 'OPTIONS'],
  allowedHeaders: ['Content-Type', 'Authorization']
}));

// Apply 15 seconds request timeout
const timeoutMs = Number(process.env.REQUEST_TIMEOUT_MS) || 15000;
app.use(requestTimeout(timeoutMs));

// Payload size limit
app.use(express.json({ limit: '2mb' }));
app.use(express.urlencoded({ extended: true, limit: '2mb' }));

// Health check endpoint at /health
app.use('/', healthRoutes);

// API routes mounted at /api
app.use('/api', checklistRoutes);

// 404 handler
app.use((req, res) => {
  res.status(404).json({ error: `Cannot ${req.method} ${req.originalUrl}` });
});

// Global error handler
app.use((err, req, res, next) => {
  if (err.type === 'entity.too.large') {
    return res.status(413).json({ error: 'Payload exceeds maximum allowed limit of 2MB.' });
  }

  if (err.message && err.message.includes('CORS policy')) {
    return res.status(403).json({ error: err.message });
  }

  if (err instanceof SyntaxError && err.status === 400 && 'body' in err) {
    return res.status(400).json({ error: 'Malformed JSON payload.' });
  }

  console.error('[Document Generator Unhandled Error]:', err);
  return res.status(500).json({ error: 'An unexpected internal server error occurred.' });
});

module.exports = app;
