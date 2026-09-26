const { SUPPORTED_LANGUAGES, SUPPORTED_CONTEXTS } = require('../services/languages');

/**
 * Validates request payload for /api/translate-guidance
 */
function validateTranslationRequest(req, res, next) {
  const { text, targetLanguage, context } = req.body;

  // Validate text
  if (!text || typeof text !== 'string') {
    return res.status(400).json({
      error: 'Invalid or missing "text". It must be a non-empty string.'
    });
  }

  const trimmedText = text.trim();
  if (trimmedText.length < 10 || trimmedText.length > 5000) {
    return res.status(400).json({
      error: `Invalid "text" length (${trimmedText.length} characters). Must be between 10 and 5000 characters.`
    });
  }

  // Validate targetLanguage
  if (!targetLanguage || typeof targetLanguage !== 'string') {
    return res.status(400).json({
      error: 'Invalid or missing "targetLanguage". It must be a supported language code.'
    });
  }

  const normalizedLang = targetLanguage.toLowerCase().trim();
  if (!SUPPORTED_LANGUAGES[normalizedLang]) {
    const allowed = Object.keys(SUPPORTED_LANGUAGES).join(', ');
    return res.status(400).json({
      error: `Unsupported "targetLanguage": '${targetLanguage}'. Supported language codes are: ${allowed}`
    });
  }

  // Validate context
  if (!context || typeof context !== 'string') {
    return res.status(400).json({
      error: 'Invalid or missing "context". It must be a string.'
    });
  }

  const normalizedContext = context.toLowerCase().trim();
  if (!SUPPORTED_CONTEXTS.includes(normalizedContext)) {
    const allowedContexts = SUPPORTED_CONTEXTS.join(', ');
    return res.status(400).json({
      error: `Unsupported "context": '${context}'. Must be one of: ${allowedContexts}`
    });
  }

  // Attach normalized values for downstream handlers
  req.body.text = trimmedText;
  req.body.targetLanguage = normalizedLang;
  req.body.context = normalizedContext;

  next();
}

module.exports = {
  validateTranslationRequest
};
