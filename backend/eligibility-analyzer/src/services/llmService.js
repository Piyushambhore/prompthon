const { getSchemeDetails, getOfficialHelpline, getAlternativeSchemes } = require('./schemeDatabase');

/**
 * Sanitizes input text before feeding into LLM
 * - Strips script/HTML tags
 * - Removes non-printable control characters
 * - Collapses excessive repeated whitespace and newlines
 * - Neutralizes basic system prompt injection patterns
 */
function sanitizeText(rawText) {
  if (typeof rawText !== 'string') return '';
  return rawText
    .replace(/<[^>]*>?/gm, '') // Strip HTML tags
    .replace(/[\u0000-\u0008\u000B\u000C\u000E-\u001F\u007F-\u009F]/g, '') // Strip control chars
    .replace(/(?:system\s*prompt|ignore\s+previous\s+instructions|disregard\s+all\s+rules)/gi, '[REDACTED_DIRECTIVE]')
    .replace(/\r\n/g, '\n')
    .replace(/\n{3,}/g, '\n\n')
    .trim();
}

/**
 * Call OpenAI Chat Completion API
 */
async function callOpenAI(messages, apiKey, model = process.env.OPENAI_MODEL || 'gpt-4o-mini') {
  const response = await fetch('https://api.openai.com/v1/chat/completions', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${apiKey}`
    },
    body: JSON.stringify({
      model,
      messages,
      temperature: 0.2,
      response_format: { type: 'json_object' }
    })
  });

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(`OpenAI API error (${response.status}): ${errorText}`);
  }

  const data = await response.json();
  const content = data.choices?.[0]?.message?.content;
  return JSON.parse(content);
}

/**
 * Call Anthropic Claude Messages API
 */
async function callClaude(systemPrompt, userPrompt, apiKey, model = process.env.CLAUDE_MODEL || 'claude-3-5-sonnet-20241022') {
  const response = await fetch('https://api.anthropic.com/v1/messages', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'x-api-key': apiKey,
      'anthropic-version': '2023-06-01'
    },
    body: JSON.stringify({
      model,
      system: systemPrompt + ' You MUST respond with ONLY valid JSON adhering strictly to the requested schema. No markdown formatting or code blocks.',
      messages: [{ role: 'user', content: userPrompt }],
      max_tokens: 1500,
      temperature: 0.2
    })
  });

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(`Claude API error (${response.status}): ${errorText}`);
  }

  const data = await response.json();
  const text = data.content?.[0]?.text || '{}';
  // Strip potential markdown fence wrappers if any
  const cleanedText = text.replace(/```json\s*/gi, '').replace(/```/g, '').trim();
  return JSON.parse(cleanedText);
}

/**
 * Rule-based fallback evaluator for eligibility
 */
function evaluateEligibilityFallback(scheme, userProfile) {
  const passedCriteria = [];
  const failedCriteria = [];

  const age = Number(userProfile.age);
  const income = Number(userProfile.income);

  // Age checks
  if (age >= 18) {
    passedCriteria.push('Age requirement satisfied (18 years or older)');
  } else {
    failedCriteria.push('Applicant is below 18 years of age');
  }

  // Scheme-specific heuristics
  const schemeId = scheme.schemeId.toLowerCase();

  if (schemeId.includes('kisan')) {
    if (income <= 300000) {
      passedCriteria.push('Income within small/marginal farmer threshold');
    } else {
      failedCriteria.push('Annual income exceeds small and marginal agricultural bracket');
    }
    passedCriteria.push('No constitutional or high-tax exclusion reported');
  } else if (schemeId.includes('pmay')) {
    if (income <= 600000) {
      passedCriteria.push('Income within EWS/LIG ceiling (up to ₹6,00,000)');
    } else {
      failedCriteria.push('Annual income exceeds PMAY EWS/LIG eligibility ceiling');
    }
  } else if (schemeId.includes('scholarship')) {
    if (income <= 250000) {
      passedCriteria.push('Income within annual ceiling of ₹2,50,000');
    } else {
      failedCriteria.push('Annual family income exceeds scholarship ceiling of ₹2,50,000');
    }
    if (userProfile.caste && ['sc', 'st', 'obc', 'minority', 'ews'].includes(userProfile.caste.toLowerCase())) {
      passedCriteria.push(`Eligible reserved category: ${userProfile.caste}`);
    } else {
      failedCriteria.push('Category does not match affirmative criteria or was not specified');
    }
  } else {
    if (income <= 500000) {
      passedCriteria.push('Annual income fits standard public welfare thresholds');
    } else {
      failedCriteria.push('Income exceeds public assistance standard limits');
    }
  }

  const total = passedCriteria.length + failedCriteria.length;
  const score = total > 0 ? Math.round((passedCriteria.length / total) * 100) : 50;
  const eligible = failedCriteria.length === 0 && score >= 75;

  let nextSteps = '';
  if (eligible) {
    nextSteps = `You meet the primary criteria for ${scheme.name}. Gather your required documents and submit your application through the official government portal or local facilitation center.`;
  } else {
    nextSteps = `You do not fully meet the criteria due to: ${failedCriteria.join('; ')}. Consider reviewing alternative schemes or rectifying documentation.`;
  }

  return {
    schemeId: scheme.schemeId,
    eligible,
    eligibilityScore: score,
    failedCriteria,
    passedCriteria,
    nextSteps,
    requiredDocuments: scheme.requiredDocuments || [
      'Aadhaar Card',
      'Income Certificate',
      'Bank Account Details'
    ]
  };
}

/**
 * Rule-based fallback analyzer for rejection letter
 */
function analyzeRejectionFallback(schemeId, rejectionLetter, userContext) {
  const text = rejectionLetter.toLowerCase();
  const helpline = getOfficialHelpline(schemeId);
  const alternatives = getAlternativeSchemes(schemeId);

  let category = 'incomplete';
  let reason = 'Application rejected due to incomplete or unverified information.';
  let whatWentWrong = 'The processing officer could not verify critical application details from the submitted papers.';
  let canReapply = true;
  let suggestedCorrections = [
    'Obtain updated certificates with matching personal details',
    'Ensure all attached documents are clearly legible and self-attested'
  ];
  let nextSteps = [
    'Contact the scheme grievance desk or helpline',
    'Prepare missing documentation for reapplication',
    'Submit fresh application with rectified files'
  ];

  if (text.includes('document') || text.includes('aadhaar') || text.includes('certificate') || text.includes('missing') || text.includes('proof') || text.includes('affidavit')) {
    category = 'document_missing';
    reason = 'Key required documents were missing, unverified, or mismatched in the official registry.';
    whatWentWrong = 'Submitted documents lacked required verification stamp, had name/DOB discrepancies, or were omitted during upload.';
    suggestedCorrections = [
      'Re-scan original identity and income certificates in high resolution',
      'Verify that name and date of birth match your Aadhaar record precisely',
      'Attach official revenue department affidavit if required'
    ];
  } else if (text.includes('income') || text.includes('age') || text.includes('ineligible') || text.includes('criterion') || text.includes('not meet') || text.includes('land')) {
    category = 'ineligible';
    reason = 'The applicant profile exceeds maximum eligibility thresholds or fails core requirements.';
    whatWentWrong = 'The applicant does not satisfy statutory requirements such as income ceilings, age brackets, or asset ownership criteria.';
    canReapply = false;
    suggestedCorrections = [
      'Verify if any deductions or family member eligibility adjustments apply',
      'Explore schemes targeted at higher income or alternative occupational groups'
    ];
    nextSteps = [
      'Explore the recommended alternative government schemes listed below',
      'Consult the official district helpline for eligibility appeals'
    ];
  } else if (text.includes('deadline') || text.includes('time') || text.includes('expired') || text.includes('portal') || text.includes('format') || text.includes('procedure')) {
    category = 'procedural';
    reason = 'Procedural defect or submission past the mandated verification timeframe.';
    whatWentWrong = 'Application submission missed procedural guidelines or time limits set by the nodal authority.';
    suggestedCorrections = [
      'Submit within the designated open application window',
      'Follow exact prescribed PDF/JPEG formatting guidelines'
    ];
  }

  return {
    rejectionReason: reason,
    reasonCategory: category,
    whatWentWrong,
    canReapply,
    suggestedCorrections,
    nextSteps,
    alternativeSchemes: alternatives,
    officialHelpline: helpline
  };
}

/**
 * Analyze user eligibility using LLM with fallback
 */
async function checkEligibility(schemeId, userProfile) {
  const scheme = getSchemeDetails(schemeId);

  const openaiKey = process.env.OPENAI_API_KEY;
  const claudeKey = process.env.CLAUDE_API_KEY;

  const systemPrompt = `You are an expert Government Scheme Eligibility Evaluator for India's Government Scheme Guidance Platform (GSGP).
Evaluate the applicant's profile against the given scheme eligibility criteria.
Return ONLY a valid JSON object matching this exact schema:
{
  "schemeId": "${scheme.schemeId}",
  "eligible": boolean,
  "eligibilityScore": number between 0 and 100,
  "failedCriteria": ["string explaining specifically why a criterion failed"],
  "passedCriteria": ["string explaining specifically which criterion was satisfied"],
  "nextSteps": "string with clear, actionable advice on how to proceed or appeal",
  "requiredDocuments": ["string list of official documents needed based on their status"]
}`;

  const userPrompt = `Scheme Details:
- Scheme Name: ${scheme.name}
- Official Criteria: ${JSON.stringify(scheme.criteria, null, 2)}
- Standard Documents: ${JSON.stringify(scheme.requiredDocuments, null, 2)}

User Profile:
${JSON.stringify(userProfile, null, 2)}

Evaluate whether the user is eligible, calculate an accurate score (0-100), extract all passed criteria, failed criteria, required documents, and next steps in simple, direct language.`;

  // Try LLM if keys provided
  if (openaiKey && openaiKey !== 'your_openai_api_key_here') {
    try {
      const messages = [
        { role: 'system', content: systemPrompt },
        { role: 'user', content: userPrompt }
      ];
      const result = await callOpenAI(messages, openaiKey);
      return {
        schemeId: scheme.schemeId,
        eligible: Boolean(result.eligible),
        eligibilityScore: Math.min(100, Math.max(0, Number(result.eligibilityScore) || 0)),
        failedCriteria: Array.isArray(result.failedCriteria) ? result.failedCriteria : [],
        passedCriteria: Array.isArray(result.passedCriteria) ? result.passedCriteria : [],
        nextSteps: typeof result.nextSteps === 'string' ? result.nextSteps : '',
        requiredDocuments: Array.isArray(result.requiredDocuments) ? result.requiredDocuments : scheme.requiredDocuments
      };
    } catch (err) {
      console.warn('OpenAI evaluation failed, attempting Claude / fallback:', err.message);
    }
  }

  if (claudeKey && claudeKey !== 'your_claude_api_key_here') {
    try {
      const result = await callClaude(systemPrompt, userPrompt, claudeKey);
      return {
        schemeId: scheme.schemeId,
        eligible: Boolean(result.eligible),
        eligibilityScore: Math.min(100, Math.max(0, Number(result.eligibilityScore) || 0)),
        failedCriteria: Array.isArray(result.failedCriteria) ? result.failedCriteria : [],
        passedCriteria: Array.isArray(result.passedCriteria) ? result.passedCriteria : [],
        nextSteps: typeof result.nextSteps === 'string' ? result.nextSteps : '',
        requiredDocuments: Array.isArray(result.requiredDocuments) ? result.requiredDocuments : scheme.requiredDocuments
      };
    } catch (err) {
      console.warn('Claude evaluation failed, using intelligent fallback:', err.message);
    }
  }

  // Fallback to intelligent rule-based evaluator
  return evaluateEligibilityFallback(scheme, userProfile);
}

/**
 * Analyze rejection letter using LLM with fallback
 */
async function analyzeRejection(schemeId, rejectionLetter, userContext) {
  const sanitizedLetter = sanitizeText(rejectionLetter);
  const helpline = getOfficialHelpline(schemeId);
  const alternatives = getAlternativeSchemes(schemeId);

  const openaiKey = process.env.OPENAI_API_KEY;
  const claudeKey = process.env.CLAUDE_API_KEY;

  const validCategories = ['document_missing', 'ineligible', 'procedural', 'incomplete'];

  const systemPrompt = `You are an expert Government Scheme Rejection Decoder for the Government Scheme Guidance Platform (GSGP).
Analyze the provided government application rejection letter. Provide simple, jargon-free, actionable guidance for citizens.
Return ONLY a valid JSON object matching this exact schema:
{
  "rejectionReason": "string - clear concise summary of rejection reason",
  "reasonCategory": "string - MUST be EXACTLY one of: document_missing, ineligible, procedural, incomplete",
  "whatWentWrong": "string - simple explanation of what went wrong without bureaucratic jargon",
  "canReapply": boolean - true if applicant can fix issues and reapply, false if permanently ineligible,
  "suggestedCorrections": ["string - specific actionable steps/corrections to resolve issues"],
  "nextSteps": ["string - chronological next actions for citizen"],
  "alternativeSchemes": ["string - list of 2-4 alternative schemes applicant might qualify for"],
  "officialHelpline": "${helpline}"
}`;

  const userPrompt = `Scheme ID: ${schemeId}
Applicant Context: ${JSON.stringify(userContext || {}, null, 2)}
Official Helpline: ${helpline}
Pre-identified Alternative Schemes: ${JSON.stringify(alternatives)}

Rejection Letter Content:
"""
${sanitizedLetter}
"""

Decode the letter accurately into the specified JSON format.`;

  if (openaiKey && openaiKey !== 'your_openai_api_key_here') {
    try {
      const messages = [
        { role: 'system', content: systemPrompt },
        { role: 'user', content: userPrompt }
      ];
      const result = await callOpenAI(messages, openaiKey);
      const category = validCategories.includes(result.reasonCategory) ? result.reasonCategory : 'incomplete';
      return {
        rejectionReason: String(result.rejectionReason || 'Application rejected by authorities'),
        reasonCategory: category,
        whatWentWrong: String(result.whatWentWrong || 'Application details could not be verified.'),
        canReapply: Boolean(result.canReapply),
        suggestedCorrections: Array.isArray(result.suggestedCorrections) ? result.suggestedCorrections : [],
        nextSteps: Array.isArray(result.nextSteps) ? result.nextSteps : [],
        alternativeSchemes: Array.isArray(result.alternativeSchemes) && result.alternativeSchemes.length > 0 ? result.alternativeSchemes : alternatives,
        officialHelpline: helpline
      };
    } catch (err) {
      console.warn('OpenAI rejection analysis failed, attempting Claude / fallback:', err.message);
    }
  }

  if (claudeKey && claudeKey !== 'your_claude_api_key_here') {
    try {
      const result = await callClaude(systemPrompt, userPrompt, claudeKey);
      const category = validCategories.includes(result.reasonCategory) ? result.reasonCategory : 'incomplete';
      return {
        rejectionReason: String(result.rejectionReason || 'Application rejected by authorities'),
        reasonCategory: category,
        whatWentWrong: String(result.whatWentWrong || 'Application details could not be verified.'),
        canReapply: Boolean(result.canReapply),
        suggestedCorrections: Array.isArray(result.suggestedCorrections) ? result.suggestedCorrections : [],
        nextSteps: Array.isArray(result.nextSteps) ? result.nextSteps : [],
        alternativeSchemes: Array.isArray(result.alternativeSchemes) && result.alternativeSchemes.length > 0 ? result.alternativeSchemes : alternatives,
        officialHelpline: helpline
      };
    } catch (err) {
      console.warn('Claude rejection analysis failed, using fallback:', err.message);
    }
  }

  return analyzeRejectionFallback(schemeId, sanitizedLetter, userContext);
}

module.exports = {
  sanitizeText,
  checkEligibility,
  analyzeRejection
};
