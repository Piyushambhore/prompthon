const { hasScheme, getAllSchemeIds } = require('../services/schemeDatabase');

const VALID_STATUSES = ['eligible', 'ineligible', 'partial'];

/**
 * Validates request payload for /api/generate-checklist
 */
function validateChecklistRequest(req, res, next) {
  const { schemeId, userEligibilityStatus, language } = req.body;

  // 1. Validate schemeId presence and type
  if (!schemeId || typeof schemeId !== 'string' || schemeId.trim() === '') {
    return res.status(400).json({
      error: 'Invalid or missing "schemeId". It must be a non-empty string.'
    });
  }

  // 2. Validate scheme exists in database
  if (!hasScheme(schemeId)) {
    const available = getAllSchemeIds().join(', ');
    return res.status(400).json({
      error: `Scheme '${schemeId}' does not exist in the database. Available schemes are: ${available}`
    });
  }

  // 3. Validate userEligibilityStatus
  if (!userEligibilityStatus || typeof userEligibilityStatus !== 'string') {
    return res.status(400).json({
      error: 'Invalid or missing "userEligibilityStatus". Must be one of: eligible, ineligible, partial.'
    });
  }

  const normalizedStatus = userEligibilityStatus.toLowerCase().trim();
  if (!VALID_STATUSES.includes(normalizedStatus)) {
    return res.status(400).json({
      error: `Unsupported "userEligibilityStatus": '${userEligibilityStatus}'. Must be one of: eligible, ineligible, partial.`
    });
  }

  // 4. Validate language if provided
  if (language !== undefined && language !== null && (typeof language !== 'string' || language.trim() === '')) {
    return res.status(400).json({
      error: '"language" must be a non-empty string if provided.'
    });
  }

  req.body.userEligibilityStatus = normalizedStatus;
  req.body.language = language ? language.toLowerCase().trim() : 'en';

  next();
}

module.exports = {
  validateChecklistRequest
};
