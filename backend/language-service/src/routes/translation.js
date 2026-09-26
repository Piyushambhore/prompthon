const express = require('express');
const router = express.Router();
const { validateTranslationRequest } = require('../middleware/validation');
const { translationRateLimiter } = require('../middleware/rateLimiter');
const { translateGuidance } = require('../services/translationService');
const { SUPPORTED_LANGUAGES, SUPPORTED_CONTEXTS } = require('../services/languages');

/**
 * POST /api/translate-guidance
 * Translates scheme guidance and eligibility feedback into regional Indian languages
 */
router.post('/translate-guidance', translationRateLimiter, validateTranslationRequest, async (req, res) => {
  try {
    const { text, targetLanguage, context } = req.body;
    const result = await translateGuidance(text, targetLanguage, context);
    return res.status(200).json(result);
  } catch (error) {
    console.error('[Translation Router Error]:', error);
    return res.status(500).json({
      error: 'An internal error occurred while processing the translation.',
      details: process.env.NODE_ENV === 'development' ? error.message : undefined
    });
  }
});

/**
 * GET /api/languages
 * Helper endpoint returning list of supported languages and contexts
 */
router.get('/languages', (req, res) => {
  res.status(200).json({
    languages: SUPPORTED_LANGUAGES,
    contexts: SUPPORTED_CONTEXTS
  });
});

module.exports = router;
