# Hybrid Automated Ingestion Pipeline — GSGP

Automated scheme update detection, AI-powered policy diffing, and 1-click human verification pipeline for the **Government Scheme Guidance Platform (GSGP)**.

---

## 🚀 How the Pipeline Works

```
[ Government Portals / RSS / Gazette ]
               │ (Scheduled Daily Cron Poller)
               ▼
[ Content Hash & ETag Diff Detection ]
               │ (Detects Content Mismatch)
               ▼
[ AI Diff Agent ] ──► Compares old vs. new circular, extracts structured parameter changes
               │
               ▼
[ Pending Updates Queue ] ──► Stored with status: "pending_review"
               │
               ▼
[ Human-in-the-Loop 1-Click Approval ] ──► Admin clicks "Approve & Publish"
               │
               ▼
[ Scheme Registry Live Update & Cache Eviction ]
```

---

## 🛠️ Setup & Running

### 1. Install Dependencies
```bash
cd backend/scheme-ingestion
npm install
```

### 2. Configure Environment
```bash
cp .env.example .env
```
Fill in configuration values:
```env
PORT=5004
NODE_ENV=development
CORS_ORIGIN=http://localhost:3000
POLL_CRON_SCHEDULE=0 */6 * * *
OPENAI_API_KEY=your_openai_api_key_here
```

### 3. Run Pipeline
- Start service:
  ```bash
  npm start
  ```
- Run automated pipeline test suite:
  ```bash
  npm test
  ```

---

## 📡 API Endpoints & `curl` Examples

### 1. Trigger Poller / Ingest Circular Notification
**Endpoint**: `POST /api/ingestion/trigger-poll`

```bash
curl -X POST http://localhost:5004/api/ingestion/trigger-poll \
  -H "Content-Type: application/json" \
  -d '{
    "schemeId": "pm-kisan",
    "circularTitle": "MoA Gazette Circular No. 44/2026: PM-KISAN Annual Revision",
    "circularContent": "OFFICIAL NOTIFICATION: The Ministry of Agriculture and Farmers Welfare hereby notifies that the annual family income ceiling under the PM-KISAN scheme is revised to ₹3,50,000 effective immediately. Additionally, all beneficiaries are required to submit Aadhaar Biometric e-KYC Verification before the 17th disbursement installment."
  }'
```

**Response (HTTP 200):**
```json
{
  "message": "Government circular processed successfully through automated ingestion pipeline.",
  "result": {
    "status": "queued_for_review",
    "update": {
      "updateId": "upd_1790421395116_d7334cb9",
      "status": "pending_review",
      "schemeId": "pm-kisan",
      "changes": {
        "incomeCeiling": {
          "old": 300000,
          "new": 350000,
          "changed": true
        },
        "addedDocuments": ["Aadhaar Biometric e-KYC Verification"]
      },
      "summaryOfChanges": "Income ceiling revised to ₹3,50,000. Mandatory e-KYC authentication added.",
      "confidence": 88
    }
  }
}
```

---

### 2. View Pending Updates Awaiting Human Approval
**Endpoint**: `GET /api/ingestion/pending-updates`

```bash
curl -X GET http://localhost:5004/api/ingestion/pending-updates
```

---

### 3. 1-Click Human Approval (Commit to Live Registry)
**Endpoint**: `POST /api/ingestion/approve/:updateId`

```bash
curl -X POST http://localhost:5004/api/ingestion/approve/upd_1790421395116_d7334cb9 \
  -H "Content-Type: application/json" \
  -d '{
    "reviewerName": "Senior Policy Officer"
  }'
```

**Response (HTTP 200):**
```json
{
  "message": "Scheme update successfully approved and committed to live platform.",
  "scheme": {
    "schemeId": "pm-kisan",
    "version": 2,
    "criteria": {
      "maxIncome": 350000
    },
    "requiredDocuments": [
      "Aadhaar Card",
      "Landholding Ownership Documents (RoR / Jamabandi)",
      "Aadhaar-Seeded Bank Passbook",
      "Aadhaar Biometric e-KYC Verification"
    ]
  }
}
```

---

### 4. Audit Log & Change History
**Endpoint**: `GET /api/ingestion/audit-log`

```bash
curl -X GET http://localhost:5004/api/ingestion/audit-log
```
