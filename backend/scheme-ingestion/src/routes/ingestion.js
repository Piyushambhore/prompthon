const express = require('express');
const router = express.Router();
const schemeStore = require('../services/schemeStore');
const crawlerPoller = require('../services/crawlerPoller');

/**
 * POST /api/ingestion/trigger-poll
 * Simulates receiving or scraping a new government notification / circular
 */
router.post('/trigger-poll', async (req, res) => {
  try {
    const { schemeId, circularTitle, circularContent } = req.body;

    if (!schemeId || !circularContent) {
      return res.status(400).json({
        error: 'Missing required fields: schemeId and circularContent are mandatory.'
      });
    }

    const result = await crawlerPoller.processCircularNotification(
      schemeId,
      circularTitle || 'Ministry Gazette Circular',
      circularContent
    );

    return res.status(200).json({
      message: 'Government circular processed successfully through automated ingestion pipeline.',
      result
    });
  } catch (error) {
    console.error('[Ingestion Trigger Error]:', error);
    return res.status(500).json({
      error: error.message
    });
  }
});

/**
 * GET /api/ingestion/pending-updates
 * Fetches all updates awaiting human-in-the-loop verification
 */
router.get('/pending-updates', (req, res) => {
  const pending = schemeStore.getPendingUpdates();
  return res.status(200).json({
    totalPending: pending.length,
    updates: pending
  });
});

/**
 * POST /api/ingestion/approve/:updateId
 * 1-Click Human Approval: Commits verified changes and increments version
 */
router.post('/approve/:updateId', (req, res) => {
  try {
    const { updateId } = req.params;
    const { reviewerName } = req.body || {};
    const result = schemeStore.approveUpdate(updateId, reviewerName || 'Verified Admin');
    return res.status(200).json({
      message: 'Scheme update successfully approved and committed to live platform.',
      ...result
    });
  } catch (error) {
    return res.status(400).json({
      error: error.message
    });
  }
});

/**
 * POST /api/ingestion/reject/:updateId
 * Rejects an erroneous or duplicate circular update
 */
router.post('/reject/:updateId', (req, res) => {
  try {
    const { updateId } = req.params;
    const { reason, reviewerName } = req.body || {};
    const result = schemeStore.rejectUpdate(updateId, reason, reviewerName || 'Verified Admin');
    return res.status(200).json({
      message: 'Scheme update rejected.',
      ...result
    });
  } catch (error) {
    return res.status(400).json({
      error: error.message
    });
  }
});

/**
 * GET /api/ingestion/schemes
 * Returns current live scheme registry with versions
 */
router.get('/schemes', (req, res) => {
  const schemes = schemeStore.getAllSchemes();
  return res.status(200).json({ schemes });
});

/**
 * GET /api/ingestion/audit-log
 * Full audit history of changes and reviews
 */
router.get('/audit-log', (req, res) => {
  return res.status(200).json({
    auditLog: schemeStore.auditLog
  });
});

module.exports = router;
