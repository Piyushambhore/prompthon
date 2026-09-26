# JanSaarthi — Current Implemented Features

> **Microservice**: Government Scheme Eligibility & Guidance Service (`backend/scheme-service`)  
> **Tech Stack**: Kotlin 1.9, Ktor 2.3, Netty, Exposed ORM, HikariCP, PostgreSQL & Embedded H2  
> **Base URL**: `http://localhost:8080`

---

## 📋 Feature Matrix Overview

| Feature Category | Endpoint | Method | Status |
| :--- | :--- | :---: | :---: |
| **System Health** | `/health` | `GET` | ✅ Running |
| **Scheme Discovery** | `/api/schemes` | `GET` | ✅ Running |
| **Scheme Details** | `/api/schemes/{schemeId}` | `GET` | ✅ Running |
| **Eligibility Engine** | `/api/schemes/check-eligibility` | `POST` | ✅ Running |
| **Document Readiness** | `/api/documents/check-readiness` | `POST` | ✅ Running |
| **Bulk Document Check** | `/api/documents/check-readiness/bulk` | `POST` | ✅ Running |
| **Required Documents** | `/api/documents/required/{schemeId}` | `GET` | ✅ Running |
| **Notice Explainer** | `/api/explain` | `POST` | ✅ Running |

---

## 1. Scheme Discovery & Catalog Service

Provides citizens and the frontend client with access to verified government scheme listings.

- **All Schemes Catalog (`GET /api/schemes`)**:
  - Fetches complete list of available welfare schemes.
  - Pre-seeded with **22 realistic Indian central and state government schemes** across education, agriculture, health, housing, financial security, and social welfare.
- **Dynamic Query Filtering**:
  - Filter by State: `?state=Maharashtra` (returns Central schemes marked `ALL` + state-specific schemes).
  - Filter by Category: `?category=education` (supports education, agriculture, housing, pension, health, etc.).
  - Combined Filters: `?state=Maharashtra&category=education`.
- **Scheme Details (`GET /api/schemes/{schemeId}`)**:
  - Full scheme overview: Department, benefits description, official application URL, last verified timestamp.
  - Human-readable eligibility criteria list generated from underlying database rule predicates.
  - Complete list of required documents.

---

## 2. Citizen Eligibility Checking Engine

Deterministic, transparent rule engine matching citizen profiles to eligible government benefits.

- **Profile Evaluation (`POST /api/schemes/check-eligibility`)**:
  - Takes citizen demographic profile:
    - `age` (Int)
    - `state` & `district` (String)
    - `occupation` (String: farmer, student, daily wage worker, etc.)
    - `annualIncome` (Double/Int in ₹)
    - `category` (String: SC, ST, OBC, General)
    - `isStudent` (Boolean)
- **Multi-Condition Rule Engine**:
  - Age constraints: `gte` (min age), `lte` (max age), `between` (e.g. 18–40).
  - Income thresholds: `lte` (e.g. annual family income $\le$ ₹2,50,000).
  - Category matching: Handles specific affirmative action schemes.
  - Occupation matching: Multi-occupation support with CSV values.
  - State jurisdiction: Central schemes (`ALL`) + resident state.
- **Explainable Results**:
  - Returns `potentiallyEligible: true/false`.
  - Gives **human-readable reasons** detailing exactly why the applicant qualifies (e.g., *"Applicant age (19) meets minimum age requirement of 18 years"*, *"Applicant is within the applicable income range"*).
  - Identifies missing documentation upfront to prevent rejected applications.

---

## 3. Document Readiness Assessment Service

Addresses the #1 cause of government application rejections: missing or incomplete documentation.

- **Single Scheme Readiness (`POST /api/documents/check-readiness`)**:
  - Compares the citizen's available documents against the scheme's mandatory requirements.
  - Computes a dynamic **Readiness Percentage** (e.g., $33\%$, $66\%$, $100\%$).
  - Itemizes every document with `mandatory: true/false` and `available: true/false`.
  - Produces an actionable `missingMandatory` checklist so citizens know exactly which certificates to obtain before applying.
- **Bulk Readiness Checker (`POST /api/documents/check-readiness/bulk`)**:
  - Evaluates citizen document readiness across multiple candidate schemes in a single request.
  - Enables Android/web client to display comparative readiness badges across scheme cards.
- **Required Documents Checklist (`GET /api/documents/required/{schemeId}`)**:
  - Quick lookup of mandatory and optional documents needed for any scheme.

---

## 4. Official Notice Explainer (AI + Fallback)

Helps citizens understand complex, jargon-heavy official government correspondence, notices, and rejection letters.

- **Endpoint (`POST /api/explain`)**:
  - Input: Raw notice or rejection letter text.
  - Output: Simplified plain-language summary, core reason for action, actionable next steps, and official verification warning.
- **Pluggable AI Architecture (`AIProvider` interface)**:
  - **OpenAI Provider (`OpenAIProvider`)**: Integrates with LLMs when `AI_API_KEY` is provided in environment.
  - **Rule-Based Provider (`RuleBasedProvider`)**: Zero-dependency deterministic NLP engine that extracts:
    - Common rejection grounds (income mismatch, Aadhaar discrepancy, document invalidity).
    - Compliance deadlines (e.g. *"within 15 days"*).
    - Official issuing bodies.
  - Guaranteed fallback ensures 100% uptime even without third-party API keys or internet access.

---

## 5. Resilient Database & Infrastructure

- **Dual-Mode Database Engine**:
  - **PostgreSQL**: Production-ready connection via HikariCP pool.
  - **Embedded H2 Fallback**: Automatically activates if PostgreSQL is not running locally, allowing instant execution without installing external dependencies.
- **Automatic Migration & Seeding**:
  - Idempotent table creation on startup (`schemes`, `eligibility_rules`, `required_documents`).
  - Pre-seeds 22 diverse Indian government schemes on first boot.
- **Robust API Middleware**:
  - Content Negotiation with Kotlinx Serialization.
  - CORS configuration tailored for local client development.
  - Centralized StatusPages error handling returning sanitized JSON error envelopes.
