# Eligibility Analyzer Service — Person B

Microservice for the **Government Scheme Guidance Platform (GSGP)** to analyze citizen eligibility for government welfare schemes and decode application rejection letters into actionable guidance.

---

## 🚀 Features

- **Eligibility Check (`POST /api/check-eligibility`)**: Evaluates user profile against scheme criteria, returns passed/failed criteria, eligibility score (0-100), required documents, and next steps.
- **Rejection Analysis (`POST /api/analyze-rejection`)**: Decodes rejection letters (50–5000 chars), categorizes reasons (`document_missing`, `ineligible`, `procedural`, `incomplete`), provides jargon-free explanations, corrections, and alternative schemes.
- **AI-Powered with Heuristic Fallback**: Supports OpenAI (`gpt-4o-mini`) and Anthropic Claude (`claude-3-5-sonnet`), with instant fallback if LLM is unavailable.
- **Security & Safeguards**:
  - Rate limiting (max 10 requests/min per IP) to prevent bill spikes.
  - Rejection text sanitization to protect against prompt injection.
  - Strict input validation rejecting missing/malformed fields with HTTP 400.
  - Payload limit configured to 2MB for scanned OCR rejection letters.
  - CORS restricted to `http://localhost:3000`.
  - In-memory criteria caching with TTL to minimize redundant LLM calls.

---

## 🛠️ Setup & Running

### 1. Install Dependencies
```bash
cd backend/eligibility-analyzer
npm install
```

### 2. Configure Environment Variables
Copy `.env.example` to `.env`:
```bash
cp .env.example .env
```
Fill in your API keys (optional; the service runs with smart heuristics if keys are omitted):
```env
PORT=5001
NODE_ENV=development
CORS_ORIGIN=http://localhost:3000
OPENAI_API_KEY=your_openai_api_key_here
CLAUDE_API_KEY=your_claude_api_key_here
```

### 3. Run the Server
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

## 📡 API Endpoints & `curl` Examples

### 1. Health Check
```bash
curl -X GET http://localhost:5001/health
```
**Response:**
```json
{
  "status": "ok"
}
```

---

### 2. Check Scheme Eligibility
**Endpoint**: `POST /api/check-eligibility`

```bash
curl -X POST http://localhost:5001/api/check-eligibility \
  -H "Content-Type: application/json" \
  -d '{
    "schemeId": "pm-kisan",
    "userProfile": {
      "age": 34,
      "income": 180000,
      "educationLevel": "Secondary School",
      "caste": "OBC",
      "gender": "male",
      "state": "Maharashtra",
      "employmentStatus": "Farmer",
      "additionalDetails": {
        "landSizeAcres": 2.5
      }
    }
  }'
```

**Response (HTTP 200):**
```json
{
  "schemeId": "pm-kisan",
  "eligible": true,
  "eligibilityScore": 100,
  "failedCriteria": [],
  "passedCriteria": [
    "Age requirement satisfied (18 years or older)",
    "Income within small/marginal farmer threshold",
    "No constitutional or high-tax exclusion reported"
  ],
  "nextSteps": "You meet the primary criteria for Pradhan Mantri Kisan Samman Nidhi (PM-KISAN). Gather your required documents and submit your application through the official government portal or local facilitation center.",
  "requiredDocuments": [
    "Aadhaar Card",
    "Landholding Ownership Documents / Jamabandi / RoR",
    "Bank Account Passbook (Aadhaar linked)",
    "Active Mobile Number"
  ]
}
```

---

### 3. Analyze Application Rejection
**Endpoint**: `POST /api/analyze-rejection`

```bash
curl -X POST http://localhost:5001/api/analyze-rejection \
  -H "Content-Type: application/json" \
  -d '{
    "schemeId": "pm-kisan",
    "rejectionLetter": "Dear applicant, your application for PM-KISAN benefits with Application ID PMK-2026-9812 has been rejected by the District Agriculture Verification Committee due to missing land possession certificate and discrepancy in Aadhaar seeding with your nominated bank account number.",
    "userContext": {
      "appliedDate": "2026-08-15",
      "applicationId": "PMK-2026-9812"
    }
  }'
```

**Response (HTTP 200):**
```json
{
  "rejectionReason": "Key required documents were missing, unverified, or mismatched in the official registry.",
  "reasonCategory": "document_missing",
  "whatWentWrong": "Submitted documents lacked required verification stamp, had name/DOB discrepancies, or were omitted during upload.",
  "canReapply": true,
  "suggestedCorrections": [
    "Re-scan original identity and income certificates in high resolution",
    "Verify that name and date of birth match your Aadhaar record precisely",
    "Attach official revenue department affidavit if required"
  ],
  "nextSteps": [
    "Contact the scheme grievance desk or helpline",
    "Prepare missing documentation for reapplication",
    "Submit fresh application with rectified files"
  ],
  "alternativeSchemes": [
    "pm-kusum",
    "kisan-credit-card",
    "pm-fasal-bima"
  ],
  "officialHelpline": "155261 / 011-24300606"
}
```

---

## 🔒 Scope & Compliance

- Files are strictly confined to `/backend/eligibility-analyzer`.
- No root files or neighboring services (`/frontend`, `/backend/scheme-discovery`) were accessed or modified.
- `.env` is ignored by `.gitignore` to prevent leaking API credentials.
