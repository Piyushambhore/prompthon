require('dotenv').config();
const express = require('express');
const cors = require('cors');
const healthRoutes = require('./routes/health');
const ingestionRoutes = require('./routes/ingestion');

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
    return callback(new Error(`CORS blocked Origin: ${origin}`), false);
  },
  methods: ['GET', 'POST', 'OPTIONS'],
  allowedHeaders: ['Content-Type', 'Authorization']
}));

app.use(express.json({ limit: '2mb' }));
app.use(express.urlencoded({ extended: true, limit: '2mb' }));

app.use('/', healthRoutes);
app.use('/api/ingestion', ingestionRoutes);

// 404 handler
app.use((req, res) => {
  res.status(404).json({ error: `Cannot ${req.method} ${req.originalUrl}` });
});

// Error handler
app.use((err, req, res, next) => {
  console.error('[Ingestion App Error]:', err);
  return res.status(500).json({ error: err.message || 'Internal server error' });
});

module.exports = app;
