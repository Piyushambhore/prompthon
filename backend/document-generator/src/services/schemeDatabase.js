/**
 * Government Schemes Document Knowledge Base
 * Contains document requirements, issuing authorities, submission workflows, and rejection priorities.
 */

const SCHEMES_DOCUMENTS_DB = {
  'pm-kisan': {
    schemeId: 'pm-kisan',
    schemeName: 'Pradhan Mantri Kisan Samman Nidhi (PM-KISAN)',
    estimatedCompletionTime: '2-3 days',
    helplineForDocumentation: '155261 / 011-24300606 (PM-KISAN Nodal Desk)',
    submissionProcess: {
      mode: 'both',
      steps: [
        'Visit the official PM-KISAN portal (pmkisan.gov.in) or your local Common Service Center (CSC)',
        'Click on "New Farmer Registration" and input your Aadhaar number for authentication',
        'Upload verified land records (Khatauni/Jamabandi/RoR) and upload bank passbook copy',
        'Submit the application and collect your application acknowledgement reference number',
        'Track physical land verification status at the local Tehsil/Block Agriculture Office'
      ],
      estimatedProcessingTime: '15-30 working days'
    },
    documents: [
      {
        documentId: 'doc_land_records',
        documentName: 'Landholding Ownership Certificate (Khatauni / Jamabandi / RoR)',
        purpose: 'Verifies applicant has cultivable landholding in their own name as required by statutory scheme criteria.',
        format: 'PDF',
        maxFileSize: '2MB',
        acceptedFormats: ['PDF', 'JPG'],
        issuingAuthority: 'Revenue Department / District Tehsildar / Land Records Portal (Bhulekh)',
        validityPeriod: '1 year or latest mutation extract',
        rejectionRiskPriority: 1, // Highest risk of rejection
        stepByStepGuide: [
          'Step 1: Download your latest computerized Record of Rights (RoR/Khatauni) from your state land records portal (e.g. Mahabhulekh, UP Bhulekh, AnyRoR).',
          'Step 2: Ensure the land is strictly under agricultural classification and the applicant’s name matches the Aadhaar record verbatim.',
          'Step 3: If mutation or inheritance is in progress, obtain a certified certified land holding extract signed by the Village Patwari / Talathi.'
        ],
        commonMistakes: [
          'Uploading unverified or blurry photos of old handwritten physical title deeds',
          'Submitting joint family property documents where applicant is not named directly',
          'Name spelling mismatch between land deed and Aadhaar'
        ],
        alternateDocuments: [
          'Patta Passbook issued by Revenue Department',
          'Registered Agricultural Land Mutation Extract'
        ],
        exampleImageUrl: 'https://assets.gsgp.gov.in/docs/examples/land_khatauni_sample.png'
      },
      {
        documentId: 'doc_aadhaar_seed',
        documentName: 'Aadhaar-Seeded Bank Passbook / NPCI Mapping Certificate',
        purpose: 'Ensures direct benefit transfer (DBT) funds successfully deposit into the applicant active account via NPCI Aadhaar bridge.',
        format: 'PDF / Scanned copy',
        maxFileSize: '2MB',
        acceptedFormats: ['PDF', 'JPG', 'PNG'],
        issuingAuthority: 'Authorized Commercial / Regional Rural Bank Branch',
        validityPeriod: 'Active account (within 3 months statement)',
        rejectionRiskPriority: 2,
        stepByStepGuide: [
          'Step 1: Visit your bank branch and request DBT/NPCI Aadhaar seeding consent form.',
          'Step 2: Collect an account statement or attested passbook front page displaying clear Account Number and IFSC.',
          'Step 3: If account is unseeded, submit Aadhaar copy with e-KYC biometric authentication at the bank counter.'
        ],
        commonMistakes: [
          'Submitting bank accounts without active NPCI Aadhaar mapper link',
          'Uploading blurry passbook images where the IFSC or Account Number is illegible',
          'Providing a joint account without applicant being the primary account holder'
        ],
        alternateDocuments: [
          'Bank statement bearing bank manager stamp and signature',
          'Cancelled Cheque showing printed account holder name and IFSC'
        ],
        exampleImageUrl: 'https://assets.gsgp.gov.in/docs/examples/bank_passbook_sample.png'
      },
      {
        documentId: 'doc_aadhaar_card',
        documentName: 'Aadhaar Identity Card',
        purpose: 'Statutory identity proof for Aadhaar-based biometric and demographic verification.',
        format: 'PDF / JPG',
        maxFileSize: '2MB',
        acceptedFormats: ['PDF', 'JPG', 'PNG'],
        issuingAuthority: 'Unique Identification Authority of India (UIDAI)',
        validityPeriod: 'Lifetime',
        rejectionRiskPriority: 3,
        stepByStepGuide: [
          'Step 1: Download latest e-Aadhaar from uidai.gov.in with valid digital signature.',
          'Step 2: Ensure your registered mobile number is active to receive verification OTPs.',
          'Step 3: If personal details have changed, update them at an Aadhaar Seva Kendra first.'
        ],
        commonMistakes: [
          'Uploading cropped or password-protected e-Aadhaar PDFs',
          'Outdated demographic details not matching government revenue records'
        ],
        alternateDocuments: [
          'e-Aadhaar masked PDF copy'
        ],
        exampleImageUrl: 'https://assets.gsgp.gov.in/docs/examples/aadhaar_sample.png'
      }
    ]
  },
  'pmay': {
    schemeId: 'pmay',
    schemeName: 'Pradhan Mantri Awas Yojana (PMAY)',
    estimatedCompletionTime: '3-5 days',
    helplineForDocumentation: '1800-11-6163 / 1800-11-3377',
    submissionProcess: {
      mode: 'both',
      steps: [
        'Register on the PMAY official portal (pmaymis.gov.in) under Citizen Assessment or visit Municipal Office / CSC',
        'Enter Aadhaar details and select appropriate category (EWS/LIG/CLSS)',
        'Fill household income and current accommodation details',
        'Upload required identity, income, and no-pucca-house affidavits',
        'Receive assessment application number for physical verification by municipal surveyor'
      ],
      estimatedProcessingTime: '45-90 working days'
    },
    documents: [
      {
        documentId: 'doc_income_cert',
        documentName: 'Income Certificate / Salary Slip / Form 16',
        purpose: 'Validates that household annual income falls within the EWS (up to ₹3L) or LIG (up to ₹6L) bracket.',
        format: 'PDF',
        maxFileSize: '2MB',
        acceptedFormats: ['PDF', 'JPG'],
        issuingAuthority: 'Revenue Authority / Tehsildar / SDO / Employer',
        validityPeriod: 'Current Financial Year',
        rejectionRiskPriority: 1,
        stepByStepGuide: [
          'Step 1: Apply for an Income Certificate at your local Tehsil or state e-District portal.',
          'Step 2: Submit family ration card and employer payslips or self-employed income declaration.',
          'Step 3: Obtain a digitally signed income certificate with QR code verification.'
        ],
        commonMistakes: [
          'Submitting expired certificates from previous financial years',
          'Discrepancy between declared household income and certificate figures'
        ],
        alternateDocuments: [
          'Form 16 issued by employer',
          'Latest Income Tax Return (ITR-V) acknowledgement'
        ],
        exampleImageUrl: 'https://assets.gsgp.gov.in/docs/examples/income_certificate_sample.png'
      },
      {
        documentId: 'doc_no_pucca_house_affidavit',
        documentName: 'Affidavit Proving No Pucca House Ownership',
        purpose: 'Mandatory declaration that neither applicant nor any immediate family member owns a pucca house anywhere in India.',
        format: 'PDF / Scanned copy',
        maxFileSize: '2MB',
        acceptedFormats: ['PDF', 'JPG'],
        issuingAuthority: 'Notary Public / Executive Magistrate',
        validityPeriod: '6 months',
        rejectionRiskPriority: 2,
        stepByStepGuide: [
          'Step 1: Purchase non-judicial stamp paper (₹10 or ₹100 as prescribed by state rules).',
          'Step 2: Print the prescribed PMAY standard non-possession declaration.',
          'Step 3: Sign in the presence of a registered Notary Public or Executive Magistrate with seal.'
        ],
        commonMistakes: [
          'Missing notary registration stamp or advocate signature',
          'Submitting plain paper declarations without stamp paper'
        ],
        alternateDocuments: [
          'Gram Panchayat / Ward Councilor certificate confirming kutcha house status'
        ],
        exampleImageUrl: 'https://assets.gsgp.gov.in/docs/examples/affidavit_sample.png'
      },
      {
        documentId: 'doc_aadhaar_family',
        documentName: 'Aadhaar Cards of All Family Members',
        purpose: 'Identifies every member of the beneficiary household to prevent duplicate allocations.',
        format: 'PDF',
        maxFileSize: '2MB',
        acceptedFormats: ['PDF', 'JPG'],
        issuingAuthority: 'UIDAI',
        validityPeriod: 'Lifetime',
        rejectionRiskPriority: 3,
        stepByStepGuide: [
          'Step 1: Compile scanned Aadhaar copies of applicant, spouse, and unmarried children.',
          'Step 2: Combine them into a single chronological PDF document.',
          'Step 3: Verify that female head of household name is included for mandatory co-ownership.'
        ],
        commonMistakes: [
          'Missing spouse Aadhaar copy',
          'Omitting minor family members listed in ration card'
        ],
        alternateDocuments: [
          'Voter ID cards of adult members with official Ration Card'
        ],
        exampleImageUrl: 'https://assets.gsgp.gov.in/docs/examples/family_aadhaar_sample.png'
      }
    ]
  },
  'pm-mudra': {
    schemeId: 'pm-mudra',
    schemeName: 'Pradhan Mantri MUDRA Yojana (PMMY)',
    estimatedCompletionTime: '3-4 days',
    helplineForDocumentation: '1800-180-1111 / 1800-11-0001',
    submissionProcess: {
      mode: 'both',
      steps: [
        'Apply on the Udyamimitra portal (udyamimitra.in) or visit any commercial/RRB/cooperative bank',
        'Select loan category: Shishu (up to ₹50k), Kishore (₹50k-₹5L), or Tarun (₹5L-₹10L)',
        'Submit business project report, machinery quotation, and KYC records',
        'Bank conducts field verification and credit bureau appraisal',
        'Loan sanctions and Mudra Card issued upon approval'
      ],
      estimatedProcessingTime: '7-15 working days'
    },
    documents: [
      {
        documentId: 'doc_business_registration',
        documentName: 'Business Registration / Udyam Certificate / Shop & Establishment Act',
        purpose: 'Establishes that the enterprise is genuine, non-farm, and compliant with statutory registration.',
        format: 'PDF',
        maxFileSize: '2MB',
        acceptedFormats: ['PDF', 'JPG'],
        issuingAuthority: 'Ministry of MSME (udyamregistration.gov.in) / Local Municipal Corporation',
        validityPeriod: 'Lifetime',
        rejectionRiskPriority: 1,
        stepByStepGuide: [
          'Step 1: Apply for instant free Udyam Registration at udyamregistration.gov.in using Aadhaar.',
          'Step 2: Download the official Udyam Certificate with QR code.',
          'Step 3: If operating a shop, obtain the local municipal Gumasta / Shop & Establishment license.'
        ],
        commonMistakes: [
          'Entering incorrect NIC business activity code during registration',
          'Uploading expired municipal trade licenses'
        ],
        alternateDocuments: [
          'GST Registration Certificate',
          'FSSAI Registration (for food/beverage businesses)'
        ],
        exampleImageUrl: 'https://assets.gsgp.gov.in/docs/examples/udyam_sample.png'
      },
      {
        documentId: 'doc_quotation_machinery',
        documentName: 'Quotation for Machinery / Equipment / Raw Materials',
        purpose: 'Justifies loan quantum requested and ensures disbursed funds directly purchase capital assets.',
        format: 'PDF',
        maxFileSize: '2MB',
        acceptedFormats: ['PDF', 'JPG'],
        issuingAuthority: 'Authorized Machinery Vendor / Equipment Supplier',
        validityPeriod: '30-60 days from invoice date',
        rejectionRiskPriority: 2,
        stepByStepGuide: [
          'Step 1: Obtain an official proforma invoice or quotation on supplier letterhead.',
          'Step 2: Ensure vendor GSTIN, bank details, and itemized machinery specifications are stated clearly.',
          'Step 3: Attach technical specifications sheet for the equipment.'
        ],
        commonMistakes: [
          'Providing handwritten unsigned informal rate slips',
          'Vendor lacking registered GST number'
        ],
        alternateDocuments: [
          'Signed supplier purchase contract or proforma agreement'
        ],
        exampleImageUrl: 'https://assets.gsgp.gov.in/docs/examples/quotation_sample.png'
      },
      {
        documentId: 'doc_bank_statement',
        documentName: 'Bank Statement (Last 6 Months)',
        purpose: 'Assesses financial transactions, average balance, and checks for past loan defaults.',
        format: 'PDF',
        maxFileSize: '2MB',
        acceptedFormats: ['PDF'],
        issuingAuthority: 'Applicant Current / Savings Bank',
        validityPeriod: 'Issued within last 7 days',
        rejectionRiskPriority: 3,
        stepByStepGuide: [
          'Step 1: Download computer-generated e-statement directly from netbanking or mobile banking app.',
          'Step 2: Ensure statement includes all 6 preceding calendar months.',
          'Step 3: If downloading from branch, verify branch seal and authorized signature.'
        ],
        commonMistakes: [
          'Uploading password-protected bank statement PDFs without providing password',
          'Missing months in the statement duration'
        ],
        alternateDocuments: [
          'Updated Bank Passbook attested by Branch Manager'
        ],
        exampleImageUrl: 'https://assets.gsgp.gov.in/docs/examples/statement_sample.png'
      }
    ]
  },
  'post-matric-scholarship': {
    schemeId: 'post-matric-scholarship',
    schemeName: 'Post Matric Scholarship for SC/ST/OBC/EWS Students',
    estimatedCompletionTime: '2-4 days',
    helplineForDocumentation: '0120-6619540 (National Scholarship Portal Helpdesk)',
    submissionProcess: {
      mode: 'online',
      steps: [
        'Register on National Scholarship Portal (scholarships.gov.in) with One Time Registration (OTR)',
        'Select scheme matching your state and category (SC/ST/OBC/Minority)',
        'Fill educational institution and fee details',
        'Upload caste, income, and previous marksheet certificates',
        'Submit for Level 1 (Institute Nodal Officer) verification followed by State District Officer verification'
      ],
      estimatedProcessingTime: '30-60 working days'
    },
    documents: [
      {
        documentId: 'doc_caste_cert',
        documentName: 'Caste / Community Certificate',
        purpose: 'Validates entitlement under affirmative action welfare scholarship schemes.',
        format: 'PDF',
        maxFileSize: '2MB',
        acceptedFormats: ['PDF', 'JPG'],
        issuingAuthority: 'Sub-Divisional Officer (SDO) / Tehsildar / District Magistrate',
        validityPeriod: 'Permanent / As per state guidelines',
        rejectionRiskPriority: 1,
        stepByStepGuide: [
          'Step 1: Obtain digital caste certificate from state e-District or Revenue portal.',
          'Step 2: Verify that certificate includes barcode/QR code and government digital signature.',
          'Step 3: Ensure student name matches high school certificate exactly.'
        ],
        commonMistakes: [
          'Submitting caste certificates issued in parent name instead of student name',
          'Using temporary receipts instead of finalized official certificate'
        ],
        alternateDocuments: [
          'State Social Welfare Digital Caste Certificate with online verification ID'
        ],
        exampleImageUrl: 'https://assets.gsgp.gov.in/docs/examples/caste_sample.png'
      },
      {
        documentId: 'doc_institute_bonafide',
        documentName: 'Bonafide Student Certificate & Current Course Fee Receipt',
        purpose: 'Proves current active enrollment in recognized post-secondary institution and exact fee breakdown.',
        format: 'PDF',
        maxFileSize: '2MB',
        acceptedFormats: ['PDF', 'JPG'],
        issuingAuthority: 'Head of Institution / College Registrar',
        validityPeriod: 'Current Academic Year',
        rejectionRiskPriority: 2,
        stepByStepGuide: [
          'Step 1: Collect standard NSP bonafide format signed by College Principal / Registrar.',
          'Step 2: Attach official receipt of tuition and examination fee paid for the current academic session.',
          'Step 3: Verify college AISHE code is stamped on the certificate.'
        ],
        commonMistakes: [
          'Submitting provisional admission slip without official stamp and AISHE code',
          'Discrepancy in fee figures uploaded versus receipt total'
        ],
        alternateDocuments: [
          'College Identity Card accompanied by official fee schedule notice'
        ],
        exampleImageUrl: 'https://assets.gsgp.gov.in/docs/examples/bonafide_sample.png'
      },
      {
        documentId: 'doc_prev_marksheet',
        documentName: 'Previous Qualifying Examination Marksheet',
        purpose: 'Demonstrates fulfillment of minimum qualifying percentage criteria.',
        format: 'PDF',
        maxFileSize: '2MB',
        acceptedFormats: ['PDF', 'JPG'],
        issuingAuthority: 'State Education Board / University Registrar / DigiLocker',
        validityPeriod: 'Lifetime',
        rejectionRiskPriority: 3,
        stepByStepGuide: [
          'Step 1: Download verified digital marksheet directly from DigiLocker or university result portal.',
          'Step 2: Ensure roll number and subject marks are visible without smudges.',
          'Step 3: Self-attest the printed copy if physical upload is required.'
        ],
        commonMistakes: [
          'Uploading unverified web result screenshots without roll number or seal',
          'Cutting off percentage/grade summary table'
        ],
        alternateDocuments: [
          'Official Board Pass Certificate or DigiLocker Verified Marksheet'
        ],
        exampleImageUrl: 'https://assets.gsgp.gov.in/docs/examples/marksheet_sample.png'
      }
    ]
  },
  'ayushman-bharat': {
    schemeId: 'ayushman-bharat',
    schemeName: 'Ayushman Bharat - Pradhan Mantri Jan Arogya Yojana (PM-JAY)',
    estimatedCompletionTime: '1-2 days',
    helplineForDocumentation: '14555 / 1800-111-565',
    submissionProcess: {
      mode: 'both',
      steps: [
        'Visit beneficiary.nha.gov.in or any empaneled government/private hospital Ayushman Mitra desk',
        'Enter mobile number and Aadhaar for instant PM-JAY eligibility verification',
        'Complete face-auth or fingerprint e-KYC on the Ayushman app',
        'Download instant Ayushman PVC / Digital Health Card (Ayushman Card)'
      ],
      estimatedProcessingTime: 'Instant to 24 hours'
    },
    documents: [
      {
        documentId: 'doc_ration_card',
        documentName: 'NFSA Ration Card / Family Composition Certificate',
        purpose: 'Confirms family composition under SECC 2011 deprivation criteria.',
        format: 'PDF / JPG',
        maxFileSize: '2MB',
        acceptedFormats: ['PDF', 'JPG'],
        issuingAuthority: 'Food & Civil Supplies Department',
        validityPeriod: 'Active Ration Card',
        rejectionRiskPriority: 1,
        stepByStepGuide: [
          'Step 1: Retrieve your digital ration card from state food portal or DigiLocker.',
          'Step 2: Ensure all family member names seeking coverage are listed on the card.',
          'Step 3: Verify that the ration card category is Antyodaya (AAY) or Priority Household (PHH).'
        ],
        commonMistakes: [
          'Ration card not seeded with Aadhaar numbers of all family members',
          'Submitting expired white/non-subsidized ration cards'
        ],
        alternateDocuments: [
          'PM-JAY entitlement family letter bearing official HH ID'
        ],
        exampleImageUrl: 'https://assets.gsgp.gov.in/docs/examples/ration_card_sample.png'
      },
      {
        documentId: 'doc_aadhaar_ayushman',
        documentName: 'Aadhaar Card (with active biometric/mobile OTP)',
        purpose: 'Instant e-KYC authentication and creation of ABHA Health ID.',
        format: 'PDF / JPG',
        maxFileSize: '2MB',
        acceptedFormats: ['PDF', 'JPG'],
        issuingAuthority: 'UIDAI',
        validityPeriod: 'Lifetime',
        rejectionRiskPriority: 2,
        stepByStepGuide: [
          'Step 1: Carry physical Aadhaar card or e-Aadhaar to the nearest Ayushman Kiosk or hospital.',
          'Step 2: Perform biometric fingerprint or face authentication on the Ayushman app.',
          'Step 3: Receive instant Ayushman Card download upon authentication success.'
        ],
        commonMistakes: [
          'Biometrics locked in mAadhaar app causing authentication rejection',
          'Name discrepancy between Ration Card and Aadhaar'
        ],
        alternateDocuments: [
          'Government photo ID with designated state entitlement voucher'
        ],
        exampleImageUrl: 'https://assets.gsgp.gov.in/docs/examples/aadhaar_sample.png'
      }
    ]
  }
};

/**
 * Normalizes scheme ID
 */
function normalizeSchemeId(id) {
  if (!id) return '';
  return id.toLowerCase().trim().replace(/[^a-z0-9_-]/g, '-');
}

/**
 * Checks if scheme exists in database
 */
function hasScheme(schemeId) {
  const norm = normalizeSchemeId(schemeId);
  return Boolean(SCHEMES_DOCUMENTS_DB[norm]);
}

/**
 * Retrieves scheme document configuration from database
 */
function getSchemeDocConfig(schemeId) {
  const norm = normalizeSchemeId(schemeId);
  return SCHEMES_DOCUMENTS_DB[norm] || null;
}

/**
 * Returns list of all available scheme IDs
 */
function getAllSchemeIds() {
  return Object.keys(SCHEMES_DOCUMENTS_DB);
}

module.exports = {
  SCHEMES_DOCUMENTS_DB,
  hasScheme,
  getSchemeDocConfig,
  getAllSchemeIds,
  normalizeSchemeId
};
