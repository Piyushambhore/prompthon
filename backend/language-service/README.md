# Multi-Language Support Service — Person B1

Microservice for the **Government Scheme Guidance Platform (GSGP)** to convert scheme guidance and eligibility feedback into 10 Indian regional languages using simplified words that anyone can easily understand.

---

## 🚀 Features

- **Indic Language Translation (`POST /api/translate-guidance`)**:
  - Supports 10 official Indian languages: Hindi (`hi`), Tamil (`ta`), Telugu (`te`), Kannada (`kn`), Malayalam (`ml`), Marathi (`mr`), Gujarati (`gu`), Bengali (`bn`), Odia (`or`), Assamese (`as`).
- **Context-Adaptive Tone**:
  - `eligibility`: Matter-of-fact, clear.
  - `rejection`: Empathetic, supportive, encouraging.
  - `appeal`: Formal yet accessible.
  - `document_checklist`: Step-by-step, actionable.
- **Readability & Simplification**: Strips bureaucratic government jargon and produces translations at a 12th-grade reading level.
- **Resilience & Caching**:
  - 24-hour in-memory translation cache (using `node-cache`) to minimize API costs and latency.
  - Graceful fallback: If LLM is unavailable or fails, returns original text with `confidence: 0`.
  - Rate limiting: Max 50 translations per minute per IP.
  - CORS restricted to `http://localhost:3000`.
  - 2MB max payload limit.

---

## 🛠️ Setup & Running

### 1. Install Dependencies
```bash
cd backend/language-service
npm install
```

### 2. Configure Environment Variables
Copy `.env.example` to `.env`:
```bash
cp .env.example .env
```
Fill in your API keys (optional; runs with built-in translations if keys are not yet configured):
```env
PORT=5002
NODE_ENV=development
CORS_ORIGIN=http://localhost:3000
TRANSLATION_API_KEY=your_key_here
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

## 📡 API Specification

### Endpoint: `POST /api/translate-guidance`

#### Request Format
```json
{
  "text": "Your application was approved. Please visit the local district office with your original certificates.",
  "targetLanguage": "hi",
  "context": "eligibility"
}
```

#### Response Format
```json
{
  "originalText": "Your application was approved. Please visit the local district office with your original certificates.",
  "translatedText": "आपका आवेदन स्वीकृत हो गया है। कृपया अपने मूल प्रमाणपत्रों के साथ स्थानीय जिला कार्यालय जाएं।",
  "targetLanguage": "hi",
  "confidence": 92,
  "formatting": {
    "isSimplified": true,
    "readabilityScore": 94,
    "usedSimpleWords": true
  }
}
```

---

## 📋 `curl` Examples for Each Supported Language

### 1. Hindi (`hi`)
```bash
curl -X POST http://localhost:5002/api/translate-guidance \
  -H "Content-Type: application/json" \
  -d '{
    "text": "You meet all the eligibility criteria for the scheme. Please keep your required documents ready.",
    "targetLanguage": "hi",
    "context": "eligibility"
  }'
```

### 2. Tamil (`ta`)
```bash
curl -X POST http://localhost:5002/api/translate-guidance \
  -H "Content-Type: application/json" \
  -d '{
    "text": "You meet all the eligibility criteria for the scheme. Please keep your required documents ready.",
    "targetLanguage": "ta",
    "context": "eligibility"
  }'
```

### 3. Telugu (`te`)
```bash
curl -X POST http://localhost:5002/api/translate-guidance \
  -H "Content-Type: application/json" \
  -d '{
    "text": "You meet all the eligibility criteria for the scheme. Please keep your required documents ready.",
    "targetLanguage": "te",
    "context": "eligibility"
  }'
```

### 4. Kannada (`kn`)
```bash
curl -X POST http://localhost:5002/api/translate-guidance \
  -H "Content-Type: application/json" \
  -d '{
    "text": "You meet all the eligibility criteria for the scheme. Please keep your required documents ready.",
    "targetLanguage": "kn",
    "context": "eligibility"
  }'
```

### 5. Malayalam (`ml`)
```bash
curl -X POST http://localhost:5002/api/translate-guidance \
  -H "Content-Type: application/json" \
  -d '{
    "text": "You meet all the eligibility criteria for the scheme. Please keep your required documents ready.",
    "targetLanguage": "ml",
    "context": "eligibility"
  }'
```

### 6. Marathi (`mr`)
```bash
curl -X POST http://localhost:5002/api/translate-guidance \
  -H "Content-Type: application/json" \
  -d '{
    "text": "You meet all the eligibility criteria for the scheme. Please keep your required documents ready.",
    "targetLanguage": "mr",
    "context": "eligibility"
  }'
```

### 7. Gujarati (`gu`)
```bash
curl -X POST http://localhost:5002/api/translate-guidance \
  -H "Content-Type: application/json" \
  -d '{
    "text": "You meet all the eligibility criteria for the scheme. Please keep your required documents ready.",
    "targetLanguage": "gu",
    "context": "eligibility"
  }'
```

### 8. Bengali (`bn`)
```bash
curl -X POST http://localhost:5002/api/translate-guidance \
  -H "Content-Type: application/json" \
  -d '{
    "text": "You meet all the eligibility criteria for the scheme. Please keep your required documents ready.",
    "targetLanguage": "bn",
    "context": "eligibility"
  }'
```

### 9. Odia (`or`)
```bash
curl -X POST http://localhost:5002/api/translate-guidance \
  -H "Content-Type: application/json" \
  -d '{
    "text": "You meet all the eligibility criteria for the scheme. Please keep your required documents ready.",
    "targetLanguage": "or",
    "context": "eligibility"
  }'
```

### 10. Assamese (`as`)
```bash
curl -X POST http://localhost:5002/api/translate-guidance \
  -H "Content-Type: application/json" \
  -d '{
    "text": "You meet all the eligibility criteria for the scheme. Please keep your required documents ready.",
    "targetLanguage": "as",
    "context": "eligibility"
  }'
```

---

### Rejection Guidance Example (Empathetic Context)
```bash
curl -X POST http://localhost:5002/api/translate-guidance \
  -H "Content-Type: application/json" \
  -d '{
    "text": "Your application was rejected due to missing land revenue records.",
    "targetLanguage": "mr",
    "context": "rejection"
  }'
```

### Health Check
```bash
curl -X GET http://localhost:5002/health
```

---

## 🔒 Scope & Compliance

- Files are strictly confined to `/backend/language-service`.
- Did not touch `/frontend`, `/backend/scheme-discovery`, or root configuration files.
- Remember: API keys must never be committed to git.
