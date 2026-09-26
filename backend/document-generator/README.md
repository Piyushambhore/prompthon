# Document Checklist Generator Service — Person C

Microservice for the **Government Scheme Guidance Platform (GSGP)** to generate step-by-step document acquisition checklists with examples, format requirements, common mistake prevention, and submission workflows.

---

## 🚀 Features

- **Document Checklist Generation (`POST /api/generate-checklist`)**:
  - Scheme validation against built-in government scheme registry.
  - Priority ordering: Documents with the highest rejection risk are placed first.
  - Plain-language step-by-step acquisition guides.
  - Common pitfalls and mistakes to avoid during submission.
  - Acceptable alternate documents if primary records are missing.
  - Full submission process workflow (Online / Offline / Both) with estimated processing and completion times.
- **Performance & Reliability**:
  - In-memory template caching with 1-hour TTL (`node-cache`) to minimize LLM calls.
  - 15-second request timeout safeguard.
  - Strict input validation returning HTTP 400 for unknown schemes or malformed statuses.
  - CORS restricted to `http://localhost:3000`.
  - 2MB max payload limit.

---

## 🛠️ Setup & Running

### 1. Install Dependencies
```bash
cd backend/document-generator
npm install
```

### 2. Configure Environment Variables
Copy `.env.example` to `.env`:
```bash
cp .env.example .env
```
Fill in configuration values (optional; operates with verified knowledge base defaults if keys are omitted):
```env
PORT=5003
NODE_ENV=development
CORS_ORIGIN=http://localhost:3000
REQUEST_TIMEOUT_MS=15000
OPENAI_API_KEY=your_openai_api_key_here
```

### 3. Run the Service
- Development mode (watch):
  ```bash
  npm run dev
  ```
- Production mode:
  ```bash
  npm start
  ```
- Run automated tests:
  ```bash
  npm test
  ```

---

## 📡 API Specification & `curl` Examples

### 1. Health Check
```bash
curl -X GET http://localhost:5003/health
```
**Response:**
```json
{
  "status": "ok"
}
```

---

### 2. Generate Checklist for PM-KISAN
```bash
curl -X POST http://localhost:5003/api/generate-checklist \
  -H "Content-Type: application/json" \
  -d '{
    "schemeId": "pm-kisan",
    "userEligibilityStatus": "eligible",
    "language": "en"
  }'
```

**Response (HTTP 200):**
```json
{
  "schemeId": "pm-kisan",
  "totalDocumentsNeeded": 3,
  "estimatedCompletionTime": "2-3 days",
  "documents": [
    {
      "documentId": "doc_land_records",
      "documentName": "Landholding Ownership Certificate (Khatauni / Jamabandi / RoR)",
      "purpose": "Verifies applicant has cultivable landholding in their own name as required by statutory scheme criteria.",
      "format": "PDF",
      "maxFileSize": "2MB",
      "acceptedFormats": ["PDF", "JPG"],
      "issuingAuthority": "Revenue Department / District Tehsildar / Land Records Portal (Bhulekh)",
      "validityPeriod": "1 year or latest mutation extract",
      "stepByStepGuide": [
        "Step 1: Download your latest computerized Record of Rights (RoR/Khatauni) from your state land records portal (e.g. Mahabhulekh, UP Bhulekh, AnyRoR).",
        "Step 2: Ensure the land is strictly under agricultural classification and the applicant’s name matches the Aadhaar record verbatim.",
        "Step 3: If mutation or inheritance is in progress, obtain a certified certified land holding extract signed by the Village Patwari / Talathi."
      ],
      "commonMistakes": [
        "Uploading unverified or blurry photos of old handwritten physical title deeds",
        "Submitting joint family property documents where applicant is not named directly",
        "Name spelling mismatch between land deed and Aadhaar"
      ],
      "alternateDocuments": [
        "Patta Passbook issued by Revenue Department",
        "Registered Agricultural Land Mutation Extract"
      ],
      "exampleImageUrl": "https://assets.gsgp.gov.in/docs/examples/land_khatauni_sample.png"
    }
  ],
  "submissionProcess": {
    "mode": "both",
    "steps": [
      "Visit the official PM-KISAN portal (pmkisan.gov.in) or your local Common Service Center (CSC)",
      "Click on \"New Farmer Registration\" and input your Aadhaar number for authentication",
      "Upload verified land records (Khatauni/Jamabandi/RoR) and upload bank passbook copy",
      "Submit the application and collect your application acknowledgement reference number",
      "Track physical land verification status at the local Tehsil/Block Agriculture Office"
    ],
    "estimatedProcessingTime": "15-30 working days"
  },
  "helplineForDocumentation": "155261 / 011-24300606 (PM-KISAN Nodal Desk)"
}
```

---

### 3. Generate Checklist for PMAY (Housing Scheme)
```bash
curl -X POST http://localhost:5003/api/generate-checklist \
  -H "Content-Type: application/json" \
  -d '{
    "schemeId": "pmay",
    "userEligibilityStatus": "eligible",
    "language": "en"
  }'
```

---

### 4. Generate Checklist for PM-MUDRA (Business Loan)
```bash
curl -X POST http://localhost:5003/api/generate-checklist \
  -H "Content-Type: application/json" \
  -d '{
    "schemeId": "pm-mudra",
    "userEligibilityStatus": "eligible",
    "language": "en"
  }'
```

---

### 5. Validation Error Example (Unknown Scheme)
```bash
curl -X POST http://localhost:5003/api/generate-checklist \
  -H "Content-Type: application/json" \
  -d '{
    "schemeId": "unknown-scheme",
    "userEligibilityStatus": "eligible"
  }'
```
**Response (HTTP 400):**
```json
{
  "error": "Scheme 'unknown-scheme' does not exist in the database. Available schemes are: pm-kisan, pmay, pm-mudra, post-matric-scholarship, ayushman-bharat"
}
```

---

## 🔒 Scope & Compliance

- Files are strictly confined to `/backend/document-generator`.
- Did not touch `/frontend`, `/backend/scheme-discovery`, or root files.
- API keys are never committed to version control.
