const express = require('express');
const router = express.Router();
const { validateChecklistRequest } = require('../middleware/validation');
const { generateChecklist } = require('../services/documentService');
const { getAllSchemeIds, getSchemeDocConfig } = require('../services/schemeDatabase');

/**
 * POST /api/generate-checklist
 * Generates step-by-step document checklist ordered by rejection risk priority
 */
router.post('/generate-checklist', validateChecklistRequest, async (req, res) => {
  try {
    const { schemeId, userEligibilityStatus, language } = req.body;
    const checklist = await generateChecklist(schemeId, userEligibilityStatus, language);
    return res.status(200).json(checklist);
  } catch (error) {
    console.error('[Document Generator Error]:', error);
    return res.status(500).json({
      error: 'An internal error occurred while generating document checklist.',
      details: process.env.NODE_ENV === 'development' ? error.message : undefined
    });
  }
});

/**
 * GET /api/schemes
 * Lists all registered schemes in the documentation database
 */
router.get('/schemes', (req, res) => {
  const ids = getAllSchemeIds();
  const schemes = ids.map(id => {
    const s = getSchemeDocConfig(id);
    return {
      schemeId: s.schemeId,
      schemeName: s.schemeName,
      totalDocuments: s.documents.length,
      estimatedCompletionTime: s.estimatedCompletionTime
    };
  });
  res.status(200).json({ schemes });
});

module.exports = router;
