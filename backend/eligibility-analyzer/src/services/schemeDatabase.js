const NodeCache = require('node-cache');

// Cache criteria for 1 hour (3600 seconds)
const criteriaCache = new NodeCache({ stdTTL: 3600, checkperiod: 600 });

// Preloaded official schemes database
const SCHEMES_DATABASE = {
  'pm-kisan': {
    schemeId: 'pm-kisan',
    name: 'Pradhan Mantri Kisan Samman Nidhi (PM-KISAN)',
    helpline: '155261 / 011-24300606',
    department: 'Ministry of Agriculture and Farmers Welfare',
    criteria: [
      'Must be a small or marginal landholding farmer family',
      'Cultivable landholding in the family name as per state land records',
      'Excludes institutional landholders and families holding constitutional posts',
      'Excludes serving or retired government employees (except Group D/Multi-Tasking Staff)',
      'Excludes income tax payers in the last assessment year'
    ],
    requiredDocuments: [
      'Aadhaar Card',
      'Landholding Ownership Documents / Jamabandi / RoR',
      'Bank Account Passbook (Aadhaar linked)',
      'Active Mobile Number'
    ],
    alternativeSchemes: [
      'pm-kusum',
      'kisan-credit-card',
      'pm-fasal-bima'
    ]
  },
  'pmay': {
    schemeId: 'pmay',
    name: 'Pradhan Mantri Awas Yojana (PMAY)',
    helpline: '1800-11-6163 / 1800-11-3377',
    department: 'Ministry of Housing and Urban Affairs',
    criteria: [
      'Beneficiary family must not own a pucca house anywhere in India',
      'Annual household income up to ₹3,00,000 for EWS, up to ₹6,00,000 for LIG',
      'Female ownership or co-ownership of the house is mandatory for EWS/LIG categories',
      'Applicant must be at least 18 years of age'
    ],
    requiredDocuments: [
      'Aadhaar Card of all family members',
      'Income Certificate / Salary Slip / Form 16',
      'Affidavit proving no pucca house ownership',
      'Bank Statement (last 6 months)',
      'Land or Property Documents (if applying for construction)'
    ],
    alternativeSchemes: [
      'pmay-gramin',
      'state-housing-board-allotment',
      'credit-linked-subsidy-scheme'
    ]
  },
  'pm-mudra': {
    schemeId: 'pm-mudra',
    name: 'Pradhan Mantri MUDRA Yojana (PMMY)',
    helpline: '1800-180-1111 / 1800-11-0001',
    department: 'Department of Financial Services',
    criteria: [
      'Non-farm small/micro enterprises in manufacturing, trading, or services',
      'Applicant must be an Indian citizen above 18 years',
      'Loan requirements: Shishu (up to ₹50,000), Kishore (₹50,000 to ₹5,00,000), Tarun (₹5,00,000 to ₹10,00,000)',
      'Applicant must not be a defaulter to any bank or financial institution'
    ],
    requiredDocuments: [
      'Identity Proof (Voter ID / Aadhaar / PAN / Passport)',
      'Proof of Residence',
      'Business Registration Certificate or Quotations for Machinery/Goods',
      'Bank Account Statement (last 6 months)',
      'Passport size photographs'
    ],
    alternativeSchemes: [
      'standup-india',
      'pmegp',
      'credit-guarantee-scheme'
    ]
  },
  'post-matric-scholarship': {
    schemeId: 'post-matric-scholarship',
    name: 'Post Matric Scholarship for SC/ST/OBC/EWS Students',
    helpline: '0120-6619540',
    department: 'Ministry of Social Justice and Empowerment',
    criteria: [
      'Must belong to eligible category (SC/ST/OBC/Minority/EWS)',
      'Annual family income must not exceed ₹2,50,000',
      'Enrolled in recognized post-matriculation or post-secondary course',
      'Must have passed previous qualifying examination with minimum requisite percentage'
    ],
    requiredDocuments: [
      'Caste / Category Certificate',
      'Income Certificate issued by designated revenue authority',
      'Previous Class Marksheet',
      'College Fee Receipt & Bonafide Certificate',
      'Aadhaar-seeded Bank Account Passbook'
    ],
    alternativeSchemes: [
      'national-means-cum-merit-scholarship',
      'central-sector-scholarship',
      'pm-young-achievers-scholarship'
    ]
  },
  'ayushman-bharat': {
    schemeId: 'ayushman-bharat',
    name: 'Ayushman Bharat - Pradhan Mantri Jan Arogya Yojana (PM-JAY)',
    helpline: '14555 / 1800-111-565',
    department: 'National Health Authority',
    criteria: [
      'Family must be listed in SECC 2011 database under deprived rural or identified urban occupational categories',
      'Active RSBY cardholders or eligible state entitlement list members',
      'No cap on family size or age of family members'
    ],
    requiredDocuments: [
      'Aadhaar Card or Ration Card',
      'PMJAY Family ID / SECC Confirmation letter',
      'Government Photo ID'
    ],
    alternativeSchemes: [
      'state-health-insurance-schemes',
      'pradhan-mantri-suraksha-bima-yojana',
      'esic-scheme'
    ]
  }
};

/**
 * Normalizes schemeId string for robust lookup
 */
function normalizeSchemeId(id) {
  if (!id) return '';
  return id.toLowerCase().trim().replace(/[^a-z0-9_-]/g, '-');
}

/**
 * Retrieves scheme details with caching
 */
function getSchemeDetails(schemeId) {
  const normId = normalizeSchemeId(schemeId);
  const cached = criteriaCache.get(`scheme_${normId}`);
  if (cached) {
    return cached;
  }

  // Check built-in schemes
  let scheme = SCHEMES_DATABASE[normId];
  if (!scheme) {
    // Generate generalized scheme metadata for any unknown scheme
    scheme = {
      schemeId: schemeId,
      name: schemeId.replace(/[-_]/g, ' ').toUpperCase(),
      helpline: '1800-11-2020 (National Citizen Helpdesk)',
      department: 'Government Welfare Administration',
      criteria: [
        'Must satisfy age and citizenship requirements',
        'Must meet income eligibility ceilings for welfare assistance',
        'Proper identity and residency verification documentation'
      ],
      requiredDocuments: [
        'Government Issued Photo ID (Aadhaar / Voter ID)',
        'Income Certificate from competent revenue officer',
        'Proof of Residence / Domicile Certificate',
        'Bank Account Details (linked to Aadhaar)'
      ],
      alternativeSchemes: [
        'pm-kisan',
        'pm-mudra',
        'ayushman-bharat'
      ]
    };
  }

  criteriaCache.set(`scheme_${normId}`, scheme);
  return scheme;
}

/**
 * Returns official helpline for a scheme
 */
function getOfficialHelpline(schemeId) {
  const scheme = getSchemeDetails(schemeId);
  return scheme ? scheme.helpline : '1800-11-2020';
}

/**
 * Returns alternative schemes for a scheme
 */
function getAlternativeSchemes(schemeId) {
  const scheme = getSchemeDetails(schemeId);
  return scheme && scheme.alternativeSchemes ? scheme.alternativeSchemes : ['pm-mudra', 'pm-kisan'];
}

module.exports = {
  criteriaCache,
  getSchemeDetails,
  getOfficialHelpline,
  getAlternativeSchemes,
  normalizeSchemeId
};
