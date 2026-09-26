# 🇮🇳 JanSaarthi — Microservice API & Frontend Integration Specification
> **Document Purpose:** Shared contract for all team members and AI assistants (Claude, Cursor, Copilot).  
> **Target Audience:** Backend developers, Frontend developers, and AI agents modifying `backend/*`.

---

## 🤖 Instructions for AI Assistants (Claude / Cursor / ChatGPT)
If you are an AI assistant helping a team member update or refactor any backend microservice in `backend/`:
1. **Adhere strictly** to the **Port assignments**, **Route paths**, **Request payloads**, and **Response schemas** defined in this document.
2. **Ensure CORS includes:** `http://localhost:3000`, `http://localhost:5173`, `http://127.0.0.1:3000`, and `http://127.0.0.1:5173`.
3. **Use `camelCase`** for all JSON fields (e.g., `schemeId`, `annualIncome`, `isEligible`, `requiredDocuments`).
4. **Always provide a `GET /health` endpoint** returning `{ "status": "UP", "service": "<service-name>" }`.
5. **Never break existing fallback mechanisms** (heuristic / rule-based engines must remain functional if Gemini/LLM keys are missing).

---

## 🗺️ System Topology & Port Allocations

All services run locally during development. Do not alter these assigned ports:

| Service Directory | Port | Runtime | Primary Function | Health Check |
|---|---|---|---|---|
| `backend/scheme-service` | **8080** | Kotlin / Ktor | Core 35 Schemes, DigiLocker Auth & Docs, Farmer Flow, Rule Eligibility | `http://localhost:8080/health` |
| `backend/eligibility-analyzer` | **5001** | Node.js / Express | AI-driven Deep Scheme Evaluation & Rejection Letter Explainer | `http://localhost:5001/health` |
| `backend/language-service` | **5002** | Node.js / Express | 10 Indic Regional Languages Guidance Translation (Bhashini/Gemini) | `http://localhost:5002/health` |
| `backend/document-generator` | **5003** | Node.js / Express | Document Checklist Generation ordered by Rejection Risk Priority | `http://localhost:5003/health` |
| `backend/scheme-ingestion` | **5004** | Node.js / Express | Gazette Circular Scraper, Policy Diffing, Human-in-the-Loop Review | `http://localhost:5004/health` |
| `frontend` | **3000** | React / Vite | Web Application Dashboard | `http://localhost:3000` |

---

## 🛡️ Universal Backend Standards (Required for ALL Services)

### 1. Mandatory CORS Configuration (Node.js & Express)
Every Node.js Express service (`eligibility-analyzer`, `language-service`, `document-generator`, `scheme-ingestion`) must use this standardized CORS setup in its `app.js`:

```javascript
const allowedOrigins = [
  'http://localhost:3000',
  'http://localhost:5173',
  'http://127.0.0.1:3000',
  'http://127.0.0.1:5173',
  ...(process.env.CORS_ORIGIN ? process.env.CORS_ORIGIN.split(',').map(s => s.trim()) : [])
];

app.use(cors({
  origin: function (origin, callback) {
    if (!origin) return callback(null, true); // Allow curl, mobile, server-to-server
    if (allowedOrigins.includes(origin)) {
      return callback(null, true);
    }
    return callback(new Error(`CORS policy blocked access from: ${origin}`), false);
  },
  methods: ['GET', 'POST', 'PUT', 'DELETE', 'OPTIONS'],
  allowedHeaders: ['Content-Type', 'Authorization']
}));
```

### 2. Standardized Error Response Format
When returning HTTP `4xx` or `5xx`, always use this structure so the frontend can display unified toast notifications:
```json
{
  "error": "Clear human-readable error message explaining what failed",
  "code": "INVALID_INPUT | NOT_FOUND | RATE_LIMITED | SERVER_ERROR",
  "details": [] 
}
```

### 3. Graceful Fallbacks (Zero Downtime)
If external AI APIs (Google Gemini, OpenAI, Bhashini) fail, return HTTP 200 with fallback rule-based heuristics and `"isFallback": true`, rather than crashing with HTTP 500.

---

## 📡 Microservice Contract Specifications

---

### 1. Scheme Service (`backend/scheme-service`) — Port 8080
**Tech:** Kotlin, Ktor, Exposed, PostgreSQL / H2  
**Owner:** Core Backend Team  

#### Endpoints:

#### `GET /health`
- **Response `200`:**
  ```json
  {
    "status": "UP",
    "service": "scheme-service",
    "timestamp": "2026-09-26T14:00:00Z"
  }
  ```

#### `GET /api/schemes`
- **Query Params:** `?state=Maharashtra` (optional), `?category=agriculture` (optional), `?search=kisan` (optional)
- **Response `200`:**
  ```json
  [
    {
      "id": "SCH001",
      "name": "PM-KISAN (Pradhan Mantri Kisan Samman Nidhi)",
      "category": "agriculture",
      "state": "Central",
      "description": "Income support of Rs.6,000 per year in three equal installments to farmer families.",
      "benefits": ["Rs.6,000 per year direct bank transfer in 3 installments of Rs.2,000"],
      "eligibility": {
        "minAge": 18,
        "maxAge": 100,
        "maxIncome": null,
        "occupations": ["farmer"],
        "states": ["All"]
      },
      "requiredDocuments": [
        "Aadhaar Card",
        "Land Ownership Records (7/12 or RoR)",
        "Bank Account Passbook"
      ],
      "applicationUrl": "https://pmkisan.gov.in"
    }
  ]
  ```

#### `GET /api/schemes/{schemeId}`
- **Path Param:** `schemeId` (e.g. `SCH001`)
- **Response `200`:** Single scheme object (as above).
- **Response `404`:** `{ "error": "Scheme not found with ID: SCH001" }`

#### `GET /api/schemes/state-portals`
- **Query Params:** `?state=Maharashtra` (optional)
- **Response `200`:**
  ```json
  [
    {
      "id": "MAHA_01",
      "state": "Maharashtra",
      "portalName": "MahaDBT",
      "url": "https://mahadbt2.maharashtra.gov.in",
      "description": "Direct Benefit Transfer portal for scholarships, agriculture, and welfare schemes",
      "services": ["Scholarships", "Farmer Subsidies", "Social Welfare"]
    }
  ]
  ```

#### `POST /api/schemes/check-eligibility`
Fast rule-based evaluation across all schemes in database.
- **Request Body:**
  ```json
  {
    "age": 25,
    "state": "Maharashtra",
    "occupation": "farmer",
    "annualIncome": 120000,
    "category": "OBC",
    "student": false
  }
  ```
- **Response `200`:**
  ```json
  {
    "eligibleSchemes": [
      {
        "id": "SCH001",
        "name": "PM-KISAN",
        "matchPercentage": 100,
        "reasons": ["Age within range", "Occupation is farmer", "State covered"]
      }
    ],
    "count": 1
  }
  ```

#### `POST /api/profile/digilocker/callback`
Exchanges DigiLocker auth code for verified token.
- **Request Body:**
  ```json
  {
    "authorizationCode": "code_xyz123",
    "state": "state_abc"
  }
  ```
- **Response `200`:**
  ```json
  {
    "accessToken": "dl_token_demo",
    "status": "AUTHENTICATED",
    "issuedAt": "2026-09-26T14:00:00Z"
  }
  ```

#### `GET /api/profile/digilocker/documents`
- **Query Param or Header:** `?token=dl_token_demo` or `Authorization: Bearer dl_token_demo`
- **Response `200`:**
  ```json
  {
    "verified": true,
    "documents": [
      { "type": "Aadhaar Card", "documentNumber": "XXXX-XXXX-1234", "issuer": "UIDAI" },
      { "type": "Caste Certificate", "documentNumber": "CC-2024-5541", "issuer": "Govt of Maharashtra" },
      { "type": "Income Certificate", "documentNumber": "INC-2024-9082", "issuer": "Revenue Dept" }
    ]
  }
  ```

#### `POST /api/profile/farmer`
Enriches farmer profile with land and scheme status.
- **Request Body:**
  ```json
  {
    "aadhaarLinked": true,
    "pmKisanBeneficiary": true,
    "landOwnership": {
      "hasLand": true,
      "landAreaAcres": 2.5
    },
    "state": "Maharashtra"
  }
  ```
- **Response `200`:**
  ```json
  {
    "farmerCategory": "Small Farmer",
    "eligibleAgriSchemes": ["PM-KISAN", "PMFBY", "Kisan Credit Card (KCC)"],
    "autoVerified": true
  }
  ```

---

### 2. Eligibility Analyzer (`backend/eligibility-analyzer`) — Port 5001
**Tech:** Node.js, Express, Google Gemini SDK  
**Purpose:** Deep AI reasoning for complex criteria & decoding government rejection notices.  

#### Endpoints:

#### `GET /health`
- **Response `200`:**
  ```json
  {
    "status": "UP",
    "service": "eligibility-analyzer",
    "port": 5001
  }
  ```

#### `POST /api/check-eligibility`
Performs in-depth AI eligibility evaluation against a specific scheme.
- **Request Body:**
  ```json
  {
    "schemeId": "SCH001",
    "userProfile": {
      "age": 28,
      "state": "Maharashtra",
      "occupation": "farmer",
      "annualIncome": 150000,
      "category": "OBC",
      "isStudent": false,
      "landAreaAcres": 2.0
    }
  }
  ```
- **Response `200`:**
  ```json
  {
    "schemeId": "SCH001",
    "eligible": true,
    "confidenceScore": 0.95,
    "reasons": [
      "Applicant is an active farmer residing in an eligible state",
      "Landholding of 2.0 acres falls below the ceiling limit"
    ],
    "missingCriteria": [],
    "recommendedNextSteps": [
      "Ensure Aadhaar is linked to your DBT bank account",
      "Submit land 7/12 record during application"
    ],
    "evaluatedBy": "gemini-flash"
  }
  ```

#### `POST /api/analyze-rejection`
Decodes formal bureaucratic rejection letters into plain-language advice.
- **Request Body:**
  ```json
  {
    "schemeId": "SCH005",
    "rejectionLetter": "Application #MH-8829 rejected under clause 4(b): Income certificate submitted is older than 6 months from the date of advertisement.",
    "userContext": {
      "applicantName": "Ramesh Kumar",
      "state": "Maharashtra"
    }
  }
  ```
- **Response `200`:**
  ```json
  {
    "schemeId": "SCH005",
    "plainEnglishExplanation": "Your application was rejected because your income certificate was expired (older than 6 months).",
    "rootCause": "EXPIRED_DOCUMENT",
    "isRecoverable": true,
    "rectificationSteps": [
      "Visit Aaple Sarkar portal (aaplesarkar.mahaonline.gov.in) or your local Tahsildar office.",
      "Apply for a fresh Current Financial Year Income Certificate (takes 3-5 days).",
      "Submit a grievance/re-appeal with the new certificate within 30 days."
    ],
    "appealDeadlineDays": 30
  }
  ```

---

### 3. Language Service (`backend/language-service`) — Port 5002
**Tech:** Node.js, Express, Bhashini / Gemini Translation Engine  
**Purpose:** Translates scheme details, guidance, and rejection advice into 10 Indic languages.  
*(Supported: `hi` Hindi, `mr` Marathi, `ta` Tamil, `te` Telugu, `kn` Kannada, `ml` Malayalam, `gu` Gujarati, `bn` Bengali, `or` Odia, `pa` Punjabi)*

#### Endpoints:

#### `GET /health`
- **Response `200`:**
  ```json
  {
    "status": "UP",
    "service": "language-service",
    "port": 5002
  }
  ```

#### `GET /api/languages`
- **Response `200`:**
  ```json
  {
    "supportedLanguages": [
      { "code": "hi", "name": "Hindi", "native": "हिन्दी" },
      { "code": "mr", "name": "Marathi", "native": "मराठी" },
      { "code": "ta", "name": "Tamil", "native": "தமிழ்" },
      { "code": "te", "name": "Telugu", "native": "తెలుగు" },
      { "code": "kn", "name": "Kannada", "native": "ಕನ್ನಡ" },
      { "code": "gu", "name": "Gujarati", "native": "ગુજરાતી" }
    ]
  }
  ```

#### `POST /api/translate-guidance`
- **Request Body:**
  ```json
  {
    "text": "Income support of Rs.6,000 per year in three equal installments to farmer families.",
    "targetLanguage": "hi",
    "context": "SCHEME_BENEFIT"
  }
  ```
- **Response `200`:**
  ```json
  {
    "translatedText": "किसान परिवारों को तीन समान किश्तों में प्रति वर्ष 6,000 रुपये की आय सहायता।",
    "sourceLanguage": "en",
    "targetLanguage": "hi",
    "confidence": 0.98
  }
  ```

---

### 4. Document Generator (`backend/document-generator`) — Port 5003
**Tech:** Node.js, Express  
**Purpose:** Creates a step-by-step checklist prioritized by rejection risk.  

#### Endpoints:

#### `GET /health`
- **Response `200`:**
  ```json
  {
    "status": "UP",
    "service": "document-generator",
    "port": 5003
  }
  ```

#### `POST /api/generate-checklist`
- **Request Body:**
  ```json
  {
    "schemeId": "SCH001",
    "userEligibilityStatus": "ELIGIBLE",
    "language": "en"
  }
  ```
- **Response `200`:**
  ```json
  {
    "schemeId": "SCH001",
    "schemeName": "PM-KISAN",
    "documents": [
      {
        "documentName": "Land Ownership Record (7/12 / RoR)",
        "priority": "CRITICAL",
        "rejectionRisk": "HIGH",
        "rejectionReasonIfMissing": "72% of PM-KISAN rejections are due to land record mismatch with Aadhaar name.",
        "howToObtain": "Download digital copy from Mahabhumi portal (mahabhumi.gov.in) or visit Talathi office.",
        "requiredFormat": "PDF, under 2MB"
      },
      {
        "documentName": "Aadhaar Card",
        "priority": "MANDATORY",
        "rejectionRisk": "MEDIUM",
        "rejectionReasonIfMissing": "DBT will fail if Aadhaar is not active.",
        "howToObtain": "Download e-Aadhaar from uidai.gov.in",
        "requiredFormat": "PDF or JPEG"
      }
    ],
    "estimatedPreparationTimeDays": 3
  }
  ```

---

### 5. Scheme Ingestion (`backend/scheme-ingestion`) — Port 5004
**Tech:** Node.js, Express  
**Purpose:** Scrapes state gazettes, runs AI diff against existing schemes, and enables human-in-the-loop review.  

#### Endpoints:

#### `GET /health`
- **Response `200`:** `{ "status": "UP", "service": "scheme-ingestion", "port": 5004 }`

#### `GET /api/updates/pending`
Returns circulars awaiting government officer verification.
- **Response `200`:**
  ```json
  [
    {
      "updateId": "UPD-2026-001",
      "schemeId": "SCH001",
      "schemeName": "PM-KISAN",
      "detectedChange": "Annual income ceiling removed for marginal tenant farmers",
      "sourceGazette": "Gazette Notification No. 441/2026",
      "confidence": 0.94,
      "status": "PENDING_APPROVAL"
    }
  ]
  ```

#### `POST /api/updates/{updateId}/approve`
- **Response `200`:**
  ```json
  {
    "updateId": "UPD-2026-001",
    "status": "APPROVED",
    "appliedToDatabase": true,
    "timestamp": "2026-09-26T14:15:00Z"
  }
  ```

---

## 💻 Frontend Reverse Proxy Setup (Vite / Next.js)

To prevent all CORS problems and decouple frontend code from backend port numbers, the frontend dev should configure `vite.config.ts` like this:

```typescript
// frontend/vite.config.ts
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    proxy: {
      // 1. Core Scheme Service & DigiLocker (:8080)
      '/api/schemes': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/api/profile': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      // 2. AI Eligibility Analyzer (:5001)
      '/api/analyzer': {
        target: 'http://localhost:5001',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api\/analyzer/, '/api')
      },
      // 3. Indic Language Service (:5002)
      '/api/language': {
        target: 'http://localhost:5002',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api\/language/, '/api')
      },
      // 4. Document Checklist Generator (:5003)
      '/api/documents': {
        target: 'http://localhost:5003',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api\/documents/, '/api')
      },
      // 5. Scheme Ingestion Pipeline (:5004)
      '/api/ingestion': {
        target: 'http://localhost:5004',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api\/ingestion/, '/api')
      }
    }
  }
});
```

---

## 📋 Copy-Paste Prompt for Your Teammate to Give to Claude

Copy and send this exact text to your teammate:

```markdown
Hey! Here is our project's standardized API specification:
[file: API_INTEGRATION_SPEC.md]

Please inspect our microservice in `backend/<your-service-name>` and ensure:
1. Port number matches exactly (check `index.js`, `package.json`, and `.env`).
2. CORS allows origins: `http://localhost:3000`, `http://localhost:5173`, `http://127.0.0.1:3000`.
3. All endpoint routes, parameter names, and returned JSON keys match the exact shapes in API_INTEGRATION_SPEC.md.
4. JSON fields use `camelCase` (e.g. `schemeId`, not `scheme_id`).
5. A working `GET /health` endpoint is implemented and returns `{ "status": "UP" }`.
6. Run `npm test` to make sure all existing tests still pass.
```

---

## ⚡ 10-Second Service Verification Script

Run this PowerShell command in the project root to instantly verify all 5 microservices are running on their assigned ports with valid health checks:

```powershell
$services = @(
    @{ Name = "scheme-service"; Port = 8080 },
    @{ Name = "eligibility-analyzer"; Port = 5001 },
    @{ Name = "language-service"; Port = 5002 },
    @{ Name = "document-generator"; Port = 5003 },
    @{ Name = "scheme-ingestion"; Port = 5004 }
)

Write-Host "Verifying JanSaarthi Microservices Health..." -ForegroundColor Cyan
foreach ($s in $services) {
    try {
        $resp = Invoke-RestMethod -Uri "http://localhost:$($s.Port)/health" -TimeoutSec 3 -ErrorAction Stop
        Write-Host "  ✅ $($s.Name) on port $($s.Port) is UP" -ForegroundColor Green
    } catch {
        Write-Host "  ❌ $($s.Name) on port $($s.Port) is OFFLINE or UNREACHABLE" -ForegroundColor Red
    }
}
```
