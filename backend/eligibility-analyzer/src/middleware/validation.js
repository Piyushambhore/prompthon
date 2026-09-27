/**
 * Middleware to validate check-eligibility request
 */
function validateCheckEligibility(req, res, next) {
  const { schemeId, userProfile } = req.body;

  if (!schemeId || typeof schemeId !== 'string' || schemeId.trim() === '') {
    return res.status(400).json({
      error: 'Invalid or missing "schemeId". It must be a non-empty string.'
    });
  }

  if (!userProfile || typeof userProfile !== 'object' || Array.isArray(userProfile)) {
    return res.status(400).json({
      error: 'Invalid or missing "userProfile". It must be an object.'
    });
  }

  // Normalize schemeId aliases if provided from scheme-service
  const SCHEME_ALIASES = {
    'sch001': 'post-matric-scholarship',
    'sch002': 'pmay',
    'sch003': 'ayushman-bharat',
    'sch004': 'pm-mudra',
    'sch005': 'pm-kisan',
    'sch011': 'ayushman-bharat',
    'sch019': 'pm-mudra',
    'sch021': 'pmay',
    'sch022': 'pmay'
  };
  const normSchemeKey = schemeId.toLowerCase().trim();
  if (SCHEME_ALIASES[normSchemeKey]) {
    req.body.schemeId = SCHEME_ALIASES[normSchemeKey];
  }

  // Support both annualIncome and income
  let incomeVal = userProfile.income !== undefined ? userProfile.income : userProfile.annualIncome;
  // Support both occupation and employmentStatus
  let empStatus = userProfile.employmentStatus || userProfile.occupation || 'Employed';
  // Support optional educationLevel and gender with defaults
  let eduLevel = userProfile.educationLevel || 'Not Specified';
  let genderVal = userProfile.gender || 'Any';

  const {
    age,
    caste,
    state,
    additionalDetails
  } = userProfile;

  if (typeof age !== 'number' || isNaN(age) || age < 0 || age > 130) {
    return res.status(400).json({
      error: 'Invalid or missing "userProfile.age". It must be a valid number between 0 and 130.'
    });
  }

  if (typeof incomeVal !== 'number' || isNaN(incomeVal) || incomeVal < 0) {
    return res.status(400).json({
      error: 'Invalid or missing "userProfile.income". It must be a non-negative number.'
    });
  }

  if (caste !== null && caste !== undefined && typeof caste !== 'string') {
    return res.status(400).json({
      error: '"userProfile.caste" must be a string or null.'
    });
  }

  if (!state || typeof state !== 'string' || state.trim() === '') {
    return res.status(400).json({
      error: 'Invalid or missing "userProfile.state". It must be a non-empty string.'
    });
  }

  if (additionalDetails !== undefined && additionalDetails !== null && typeof additionalDetails !== 'object') {
    return res.status(400).json({
      error: '"userProfile.additionalDetails" must be an object.'
    });
  }

  // Populate normalized fields on userProfile
  userProfile.income = incomeVal;
  userProfile.annualIncome = incomeVal;
  userProfile.employmentStatus = empStatus;
  userProfile.occupation = empStatus;
  userProfile.educationLevel = eduLevel;
  userProfile.gender = genderVal;

  next();
}

/**
 * Middleware to validate analyze-rejection request
 */
function validateAnalyzeRejection(req, res, next) {
  const { schemeId, rejectionLetter, userContext } = req.body;

  if (!schemeId || typeof schemeId !== 'string' || schemeId.trim() === '') {
    return res.status(400).json({
      error: 'Invalid or missing "schemeId". It must be a non-empty string.'
    });
  }

  if (!rejectionLetter || typeof rejectionLetter !== 'string') {
    return res.status(400).json({
      error: 'Invalid or missing "rejectionLetter". It must be a string.'
    });
  }

  const trimmedLength = rejectionLetter.trim().length;
  if (trimmedLength < 50 || trimmedLength > 5000) {
    return res.status(400).json({
      error: `Invalid "rejectionLetter" length (${trimmedLength} characters). Must be between 50 and 5000 characters.`
    });
  }

  if (userContext !== undefined && userContext !== null) {
    if (typeof userContext !== 'object' || Array.isArray(userContext)) {
      return res.status(400).json({
        error: '"userContext" must be an object containing appliedDate and optional applicationId.'
      });
    }

    if (userContext.appliedDate !== undefined && typeof userContext.appliedDate !== 'string') {
      return res.status(400).json({
        error: '"userContext.appliedDate" must be a string if provided.'
      });
    }

    if (userContext.applicationId !== null && userContext.applicationId !== undefined && typeof userContext.applicationId !== 'string') {
      return res.status(400).json({
        error: '"userContext.applicationId" must be a string or null.'
      });
    }
  }

  next();
}

module.exports = {
  validateCheckEligibility,
  validateAnalyzeRejection
};
