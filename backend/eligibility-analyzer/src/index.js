const app = require('./app');

const PORT = process.env.PORT || 5001;

const server = app.listen(PORT, '0.0.0.0', () => {
  console.log(`[Eligibility Analyzer] Server running on port ${PORT}`);
  console.log(`[Eligibility Analyzer] Health check available at http://localhost:${PORT}/health`);
  console.log(`[Eligibility Analyzer] API available at http://localhost:${PORT}/api`);
});

// Graceful shutdown handling
process.on('SIGTERM', () => {
  console.log('SIGTERM signal received: closing HTTP server');
  server.close(() => {
    console.log('HTTP server closed');
  });
});

module.exports = server;
