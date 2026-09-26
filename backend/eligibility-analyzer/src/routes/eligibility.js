const express = require('express');
const router = express.Router();
const { validateCheckEligibility, validateAnalyzeRejection } = require('../middleware/validation');
const { llmRateLimiter } = require('../middleware/rateLimiter');
const { checkEligibility, analyzeRejection } = require('../services/llmService');

/**
 * POST /api/check-eligibility
 * Checks eligibility of a user profile against a specific scheme
 */
router.post('/check-eligibility', llmRateLimiter, validateCheckEligibility, async (req, res) => {
  try {
    const { schemeId, userProfile } = req.body;
    const result = await checkEligibility(schemeId, userProfile);
    return res.status(200).json(result);
  } catch (error) {
    console.error('Error during eligibility evaluation:', error);
    return res.status(500).json({
      error: 'An internal error occurred while evaluating scheme eligibility.',
      details: process.env.NODE_ENV === 'development' ? error.message : undefined
    });
  }
});

/**
 * POST /api/analyze-rejection
 * Decodes and analyzes application rejection letters with actionable guidance
 */
router.post('/analyze-rejection', llmRateLimiter, validateAnalyzeRejection, async (req, res) => {
  try {
    const { schemeId, rejectionLetter, userContext } = req.body;
    const result = await analyzeRejection(schemeId, rejectionLetter, userContext);
    return res.status(200).json(result);
  } catch (error) {
    console.error('Error during rejection analysis:', error);
    return res.status(500).json({
      error: 'An internal error occurred while decoding rejection letter.',
      details: process.env.NODE_ENV === 'development' ? error.message : undefined
    });
  }
});

module.exports = router;
