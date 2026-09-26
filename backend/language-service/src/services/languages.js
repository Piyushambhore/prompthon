/**
 * Supported Indian regional languages mapping and metadata
 */
const SUPPORTED_LANGUAGES = {
  hi: { code: 'hi', name: 'Hindi', nativeName: 'हिन्दी', script: 'Devanagari' },
  ta: { code: 'ta', name: 'Tamil', nativeName: 'தமிழ்', script: 'Tamil' },
  te: { code: 'te', name: 'Telugu', nativeName: 'తెలుగు', script: 'Telugu' },
  kn: { code: 'kn', name: 'Kannada', nativeName: 'ಕನ್ನಡ', script: 'Kannada' },
  ml: { code: 'ml', name: 'Malayalam', nativeName: 'മലയാളം', script: 'Malayalam' },
  mr: { code: 'mr', name: 'Marathi', nativeName: 'मराठी', script: 'Devanagari' },
  gu: { code: 'gu', name: 'Gujarati', nativeName: 'ગુજરાતી', script: 'Gujarati' },
  bn: { code: 'bn', name: 'Bengali', nativeName: 'বাংলা', script: 'Bengali' },
  or: { code: 'or', name: 'Odia', nativeName: 'ଓଡ଼ିଆ', script: 'Odia' },
  as: { code: 'as', name: 'Assamese', nativeName: 'অসমীয়া', script: 'Bengali-Assamese' }
};

const SUPPORTED_CONTEXTS = [
  'eligibility',
  'rejection',
  'appeal',
  'document_checklist'
];

/**
 * Context style directives for the translator
 */
const CONTEXT_DIRECTIVES = {
  eligibility: 'Style: Matter-of-fact, completely clear, and direct. Explain requirements and qualification status simply so anyone can evaluate their standing without ambiguity.',
  rejection: 'Style: Empathetic, supportive, and encouraging. Reassure the citizen that a rejection is not the end of the road, and explain what went wrong and how they can fix it in a gentle, motivating tone.',
  appeal: 'Style: Formal yet accessible. Provide empowering, precise wording suitable for approaching an appeals officer or grievance portal without confusing legal jargon.',
  document_checklist: 'Style: Step-by-step, actionable, and structured. Present instructions like a simple checklist so the citizen knows exactly which document to get, from where, and how to verify it.'
};

module.exports = {
  SUPPORTED_LANGUAGES,
  SUPPORTED_CONTEXTS,
  CONTEXT_DIRECTIVES
};
