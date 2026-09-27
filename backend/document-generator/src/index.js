const app = require('./app');

const PORT = process.env.PORT || 5003;

const server = app.listen(PORT, '0.0.0.0', () => {
  console.log(`[Document Generator] Server running on port ${PORT}`);
  console.log(`[Document Generator] Health check: http://localhost:${PORT}/health`);
  console.log(`[Document Generator] API: http://localhost:${PORT}/api/generate-checklist`);
});

process.on('SIGTERM', () => {
  console.log('SIGTERM signal received: closing HTTP server');
  server.close(() => {
    console.log('HTTP server closed');
  });
});

module.exports = server;
