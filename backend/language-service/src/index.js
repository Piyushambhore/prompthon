const app = require('./app');

const PORT = process.env.PORT || 5002;

const server = app.listen(PORT, '0.0.0.0', () => {
  console.log(`[Language Service] Server running on port ${PORT}`);
  console.log(`[Language Service] Health check: http://localhost:${PORT}/health`);
  console.log(`[Language Service] API: http://localhost:${PORT}/api/translate-guidance`);
});

process.on('SIGTERM', () => {
  console.log('SIGTERM signal received: closing HTTP server');
  server.close(() => {
    console.log('HTTP server closed');
  });
});

module.exports = server;
