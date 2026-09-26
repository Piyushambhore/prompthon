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

  const {
    age,
    income,
    educationLevel,
    caste,
    gender,
    state,
    employmentStatus,
    additionalDetails
  } = userProfile;

  if (typeof age !== 'number' || isNaN(age) || age < 0 || age > 130) {
    return res.status(400).json({
      error: 'Invalid or missing "userProfile.age". It must be a valid number between 0 and 130.'
    });
  }

  if (typeof income !== 'number' || isNaN(income) || income < 0) {
    return res.status(400).json({
      error: 'Invalid or missing "userProfile.income". It must be a non-negative number.'
    });
  }

  if (!educationLevel || typeof educationLevel !== 'string' || educationLevel.trim() === '') {
    return res.status(400).json({
      error: 'Invalid or missing "userProfile.educationLevel". It must be a non-empty string.'
    });
  }

  if (caste !== null && caste !== undefined && typeof caste !== 'string') {
    return res.status(400).json({
      error: '"userProfile.caste" must be a string or null.'
    });
  }

  if (!gender || typeof gender !== 'string' || gender.trim() === '') {
    return res.status(400).json({
      error: 'Invalid or missing "userProfile.gender". It must be a non-empty string.'
    });
  }

  if (!state || typeof state !== 'string' || state.trim() === '') {
    return res.status(400).json({
      error: 'Invalid or missing "userProfile.state". It must be a non-empty string.'
    });
  }

  if (!employmentStatus || typeof employmentStatus !== 'string' || employmentStatus.trim() === '') {
    return res.status(400).json({
      error: 'Invalid or missing "userProfile.employmentStatus". It must be a non-empty string.'
    });
  }

  if (additionalDetails !== undefined && additionalDetails !== null && typeof additionalDetails !== 'object') {
    return res.status(400).json({
      error: '"userProfile.additionalDetails" must be an object.'
    });
  }

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
