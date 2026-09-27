const app = require('./app');
const crawlerPoller = require('./services/crawlerPoller');

const PORT = process.env.PORT || 5004;

const server = app.listen(PORT, '0.0.0.0', () => {
  console.log(`[Scheme Ingestion Pipeline] Running on port ${PORT}`);
  console.log(`[Scheme Ingestion Pipeline] Health: http://localhost:${PORT}/health`);
  console.log(`[Scheme Ingestion Pipeline] Ingestion API: http://localhost:${PORT}/api/ingestion`);

  // Start background poller cron
  crawlerPoller.startScheduler();
});

process.on('SIGTERM', () => {
  console.log('SIGTERM signal received: stopping poller & closing server');
  crawlerPoller.stopScheduler();
  server.close(() => {
    console.log('HTTP server closed');
  });
});

module.exports = server;
