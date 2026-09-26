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
| **State Portals Directory**| `/api/schemes/state-portals` | `GET` | ✅ Running |
| **Scheme Details** | `/api/schemes/{schemeId}` | `GET` | ✅ Running |
| **Eligibility Engine** | `/api/schemes/check-eligibility` | `POST` | ✅ Running |
| **Document Readiness** | `/api/documents/check-readiness` | `POST` | ✅ Running |
| **Bulk Document Check** | `/api/documents/check-readiness/bulk` | `POST` | ✅ Running |
| **Required Documents** | `/api/documents/required/{schemeId}` | `GET` | ✅ Running |
| **Notice Explainer** | `/api/explain` | `POST` | ✅ Running |
| **DigiLocker Auth** | `/api/profile/digilocker/auth` | `GET` | ✅ Running |
| **DigiLocker Callback** | `/api/profile/digilocker/callback` | `POST` | ✅ Running |
| **DigiLocker Documents**| `/api/profile/digilocker/documents` | `GET` | ✅ Running |
| **Farmer Profile Flow** | `/api/profile/farmer` | `POST` | ✅ Running |
| **Unified Profile Build**| `/api/profile/build` | `POST` | ✅ Running |
| **End-to-End Eligibility**| `/api/profile/check-eligibility` | `POST` | ✅ Running |

---

## 1. Scheme Discovery & Catalog Service

Provides citizens and the frontend client with access to verified government scheme listings.

- **All Schemes Catalog (`GET /api/schemes`)**:
  - Fetches complete list of available welfare schemes.
  - Pre-seeded with **35 realistic Indian central and state government schemes** across education, agriculture, health, housing, financial security, and social welfare:
    - **Central Government (20 schemes)**: PM-KISAN, PM Fasal Bima Yojana, Kisan Credit Card, Ayushman Bharat (PM-JAY), PMAY-U, MGNREGA, Stand Up India, Atal Pension Yojana, PM Ujjwala Yojana, PM Jan Dhan Yojana, etc.
    - **Maharashtra (3 schemes)**: Rajarshi Chhatrapati Shahu Maharaj Shikshan Shulkh Scholarship, Dr. Panjabrao Deshmukh Vasatigruh Nirvah Bhatta Yojna, Sanjay Gandhi Niradhar Anudan Yojana.
    - **Karnataka (3 schemes)**: Vidyasiri Food & Accommodation Scheme, Gruha Lakshmi Scheme, Yuva Nidhi Scheme.
    - **Kerala (2 schemes)**: E-Grantz 3.0 Post-Matric Educational Assistance, Karunya Arogya Suraksha Padhathi (KASP).
    - **Gujarat (2 schemes)**: Mukhyamantri Yuva Swavalamban Yojana (MYSY), Kisan Suryodaya Yojana.
    - **Telangana (3 schemes)**: Telangana ePASS Post-Matric Scholarship, Rythu Bharosa / Rythu Bandhu, Aasara Pension Scheme.
    - **Haryana (2 schemes)**: Mukhyamantri Parivar Samridhi Yojana (MMPSY), Haryana Post-Matric Scholarship for SC/BC.
- **Dynamic Query Filtering**:
  - Filter by State: `?state=Maharashtra` (returns Central schemes marked `ALL` + Maharashtra state schemes: 23 schemes).
  - Filter by Category: `?category=agriculture` (supports education, agriculture, housing, pension, health, financial).
  - Combined Filters: `?state=Karnataka&category=education`.
- **Scheme Details (`GET /api/schemes/{schemeId}`)**:
  - Full scheme overview: Department, benefits description, official application URL, last verified timestamp.
  - Human-readable eligibility criteria list generated from underlying database rule predicates.
  - Complete list of required documents.

---

## 2. Official State Portals Directory Service

Gives citizens direct access to verified, authoritative state digital infrastructure.

- **Endpoint (`GET /api/schemes/state-portals`)**:
  - Supports query filtering: `?state=Maharashtra`, `?state=Karnataka`, etc.
  - Provides **17 registered official state portals** with direct links and descriptions:

| State | Portal Name | Official URL | Primary Purpose |
| :--- | :--- | :--- | :--- |
| **Maharashtra** | **MahaDBT** | `https://mahadbt2.maharashtra.gov.in` | Scholarships & DBT scheme applications |
| **Maharashtra** | **Aaple Sarkar** | `https://aaplesarkar.mahaonline.gov.in` | Certificates + broader citizen schemes |
| **Maharashtra** | **Maharashtra State Govt** | `https://maharashtra.gov.in` | Departments, GRs, official notifications |
| **Maharashtra** | **MahaBhumi** | `https://mahabhumi.gov.in` | Land records (7/12, property card) |
| **Karnataka** | **Seva Sindhu** | `https://sevasindhu.karnataka.gov.in` | 780+ services incl. welfare schemes |
| **Karnataka** | **Karnataka One** | `https://karnatakaone.gov.in` | Citizen service centre network |
| **Karnataka** | **Karnataka Govt** | `https://karnataka.gov.in` | State departments & notifications |
| **Kerala** | **E-Grantz 3.0** | `https://egrantz.kerala.gov.in` | Post-matric scholarships (SC/ST/OBC/OEC) |
| **Kerala** | **Akshaya** | `https://akshaya.kerala.gov.in` | e-governance service centre network |
| **Kerala** | **Kerala Govt** | `https://kerala.gov.in` | State departments & schemes info |
| **Gujarat** | **Digital Gujarat** | `https://digitalgujarat.gov.in` | 30+ scholarships incl. MYSY in one place |
| **Gujarat** | **Gujarat Govt** | `https://gujaratindia.gov.in` | State departments & notifications |
| **Telangana** | **MeeSeva Telangana** | `https://ts.meeseva.telangana.gov.in` | 580+ G2C services incl. schemes |
| **Telangana** | **ePASS Telangana** | `https://epass.cgg.gov.in` | SC/ST/BC/minority scholarships |
| **Telangana** | **Telangana Govt** | `https://telangana.gov.in` | State departments & schemes info |
| **Haryana** | **Antyodaya SARAL** | `https://saralharyana.gov.in` | 500+ schemes & services, single window |
| **Haryana** | **Haryana Govt** | `https://haryana.gov.in` | State departments & notifications |

---

## 3. Citizen Eligibility Checking Engine

Deterministic, transparent rule engine matching citizen profiles to eligible government benefits.

- **Profile Evaluation (`POST /api/schemes/check-eligibility`)**:
  - Takes citizen demographic profile:
    - `age` (Int)
    - `state` & `district` (String)
    - `occupation` (String: farmer, student, daily wage worker, etc.)
    - `annualIncome` (Double/Int in ₹)
    - `category` (String: SC, ST, OBC, General, EWS)
    - `isStudent` (Boolean)
- **Multi-Condition Rule Engine**:
  - Age constraints: `gte` (min age), `lte` (max age), `between` (e.g. 18–35).
  - Income thresholds: `lte` (e.g. annual family income $\le$ ₹2,50,000).
  - Category matching: Handles affirmative action schemes (SC/ST/OBC/EWS).
  - Occupation matching: Multi-occupation support with CSV values.
  - State jurisdiction: Central schemes (`ALL`) + resident state.
- **Explainable Results**:
  - Returns `potentiallyEligible: true/false`.
  - Gives **human-readable reasons** detailing exactly why the applicant qualifies (e.g., *"Applicant age (19) meets minimum age requirement of 18 years"*, *"Applicant is within the applicable income range"*).
  - Identifies missing documentation upfront to prevent rejected applications.

---

## 4. Document Readiness Assessment Service

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
- **Smart Fuzzy Matcher**:
  - Normalizes aliases and government acronyms (e.g. `7/12 Extract` ↔ `Land Ownership Records`, `KCC` ↔ `Kisan Credit Card`, `Passbook` ↔ `Bank Passbook`, `Aadhaar` ↔ `Aadhaar Card`).

---

## 5. DigiLocker OAuth & Verified Document Extraction

Eliminates tedious and error-prone manual data entry by pulling authentic, tamper-proof government documents directly from DigiLocker.

- **OAuth 2.0 Authorization Flow**:
  - `GET /api/profile/digilocker/auth`: Generates secure authorization URL for DigiLocker consent screen with state token.
  - `POST /api/profile/digilocker/callback`: Exchanges authorization code for access tokens.
- **Issued Document Extraction (`GET /api/profile/digilocker/documents`)**:
  - Pulls verified government credentials from official issuers:
    - **Aadhaar Card** (UIDAI) — demographic verification (Name, Age, Gender, State, District).
    - **Income Certificate** (State Revenue Dept) — verified annual income.
    - **Caste Certificate** (Social Justice Dept) — verified category (SC/ST/OBC).
    - **Class X/XII Marksheet** (State Board / CBSE) — educational qualification.
    - **Domicile Certificate** (State Revenue) — state residency proof.
- **Data Source Provenance Tracking**:
  - Every extracted profile attribute tracks its verified origin (`source: "DIGILOCKER_AADHAAR"`, `"DIGILOCKER_INCOME_CERT"`, etc.).
  - Mobile UI displays **Green Verified Badges** for official fields and **Grey Badges** for self-declared fields.

---

## 6. Farmer-Specific Workflow & Land Record Integration

Tailored flow designed specifically for agricultural workers and farmers who often lack digital literacy or standard employment records.

- **Endpoint (`POST /api/profile/farmer`)**:
  - Ingests landholding details, PM-KISAN enrollment, and Kisan Credit Card (KCC) status.
- **Landholding Classification (per Government of India Norms)**:
  - **Marginal Farmer**: Land < 2.47 acres (< 1 hectare).
  - **Small Farmer**: 2.47 to 4.94 acres (1–2 hectares).
  - **Semi-Medium Farmer**: 4.94 to 9.88 acres (2–4 hectares).
  - **Medium Farmer**: 9.88 to 24.7 acres (4–10 hectares).
  - **Large Farmer**: > 24.7 acres (> 10 hectares).
  - **Landless Agricultural Labourer**: 0 acres.
- **PM-KISAN Status & DBT Verification**:
  - Verifies registration status (`active`, `pending_verification`, `not_registered`) and latest installment disbursement.
  - Linking PM-KISAN / KCC automatically confirms verified DBT bank account and land title extract (7/12).
- **Targeted Agriculture Scheme Recommendations**:
  - Auto-recommends schemes tailored to farmer category: PM-KISAN, PM Fasal Bima Yojana, Kisan Credit Card, MGNREGA, PM Ujjwala Yojana, Ayushman Bharat.

---

## 7. Unified Profile & End-to-End Eligibility Pipeline

A single pipeline that harmonizes **Manual input**, **DigiLocker verified documents**, and **Farmer workflows**.

- **Endpoints**:
  - `POST /api/profile/build`: Assembles a `VerifiedProfile` with data provenance.
  - `POST /api/profile/check-eligibility`: **Full end-to-end execution**:
    1. Builds unified profile according to selected mode (`manual`, `digilocker`, `farmer`).
    2. Runs deterministic eligibility engine across all schemes.
    3. Evaluates document readiness against all eligible schemes using verified vault + declared documents.
    4. Computes verification statistics (`verifiedFieldCount / totalFieldCount`).
    5. Returns unified report ready for client display.

---

## 8. Official Notice Explainer (AI + Fallback)

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

## 9. Resilient Database & Infrastructure

- **Dual-Mode Database Engine**:
  - **PostgreSQL**: Production-ready connection via HikariCP pool with auto-parsing for cloud URIs (Supabase, Neon, Render, Railway).
  - **Embedded H2 Fallback**: Automatically activates if PostgreSQL is not running locally, allowing instant execution without installing external dependencies.
- **Automatic Migration & Seeding**:
  - Idempotent table creation on startup (`schemes`, `eligibility_rules`, `required_documents`).
  - Pre-seeds 35 diverse Indian government schemes (Central + Maharashtra, Karnataka, Kerala, Gujarat, Telangana, Haryana) on first boot.
- **Robust API Middleware**:
  - Content Negotiation with Kotlinx Serialization.
  - CORS configuration tailored for local client development.
  - Centralized StatusPages error handling returning sanitized JSON error envelopes.
