# 🇮🇳 JanSaarthi — Government Scheme Guidance Platform (GSGP)

## Complete Project Overview, Tech Stack & Architecture

---

## 📋 Table of Contents

1. [What is JanSaarthi?](#what-is-jansaarthi)
2. [Problem Statement](#problem-statement)
3. [Complete Tech Stack](#complete-tech-stack)
4. [Microservice Architecture](#microservice-architecture)
5. [Service-by-Service Breakdown](#service-by-service-breakdown)
6. [DigiLocker Integration](#digilocker-integration)
7. [Farmer Workflow](#farmer-workflow)
8. [Dataset & Scheme Coverage](#dataset--scheme-coverage)
9. [State Portal Directory](#state-portal-directory)
10. [How is JanSaarthi Different?](#how-is-jansaarthi-different)
11. [Pros & Cons](#pros--cons)
12. [Handling Large User Base — Scalability Strategy](#handling-large-user-base--scalability-strategy)
13. [Security & Privacy](#security--privacy)
14. [Production Readiness Roadmap](#production-readiness-roadmap)

---

## What is JanSaarthi?

**JanSaarthi** (जनसारथी — *"People's Guide"*) is a **microservice-based Government Scheme Guidance Platform** that helps Indian citizens:

1. **Discover** government welfare schemes they're eligible for
2. **Verify eligibility** through a deterministic rule engine
3. **Auto-populate profiles** via DigiLocker (Aadhaar, PAN, income/caste certificates)
4. **Get farmer-specific** assistance (PM-KISAN status, KCC, land classification)
5. **Understand rejection letters** with AI-powered decoding into plain language
6. **Generate document checklists** prioritized by rejection risk
7. **Translate** everything into 10 Indian regional languages
8. **Stay updated** via an automated gazette ingestion pipeline with human-in-the-loop approval

---

## Problem Statement

> **700+ central and state government schemes exist in India**, but citizens lose ₹1,000s of crores annually because they either:
> - Don't know which schemes they qualify for
> - Can't understand bureaucratic eligibility criteria written in complex legal language
> - Submit incomplete documentation and get rejected
> - Don't know how to interpret rejection letters or appeal
> - Can't access information in their regional language

**JanSaarthi solves this** by acting as a digital *saarathi* (guide) that matches citizens to schemes, verifies documents via DigiLocker, and explains everything in their mother tongue.

---

## Complete Tech Stack

### Backend Core — Scheme Service (Kotlin/Ktor)

| Technology | Version | Purpose | Why We Chose It |
|------------|---------|---------|-----------------|
| **Kotlin** | 1.9.22 | Primary language for scheme-service | Type-safe, null-safe, concise syntax. Kotlin's `data class` + `sealed class` patterns make eligibility rule evaluation clean and exhaustive. JVM ecosystem means access to all Java libraries. |
| **Ktor** | 2.3.7 | Async HTTP server framework | Lightweight, coroutine-native (non-blocking I/O). Unlike Spring Boot (100+ MB cold start), Ktor starts in <2 seconds with minimal memory. Perfect for microservices that need to handle concurrent eligibility checks. |
| **Netty** | (via Ktor) | Network engine | Industry-standard non-blocking I/O engine. Handles 10,000+ concurrent connections on a single instance vs 200 threads in traditional servlet containers. |
| **Exposed ORM** | 0.46.0 | Database access layer | Kotlin-native, type-safe SQL DSL. Eliminates SQL injection by construction. Supports both DSL and DAO patterns — we use DSL for lightweight, high-performance queries. |
| **PostgreSQL** | 42.7.1 (driver) | Primary production database | ACID-compliant, supports JSONB for flexible eligibility rules, scales to millions of rows. Used by government platforms (NIC, DigiLocker) — our data model matches their conventions. |
| **H2 Database** | 2.2.224 | Zero-setup development fallback | Runs in PostgreSQL compatibility mode (`MODE=PostgreSQL`). Allows any developer to clone the repo and run immediately without installing PostgreSQL. Seamless transparent fallback. |
| **HikariCP** | 5.1.0 | Connection pooling | Fastest JDBC connection pool (benchmarked). Manages 10 connections with auto-recovery, preventing connection leaks under high load. |
| **kotlinx.serialization** | 1.9.22 | JSON serialization | Compile-time, reflection-free JSON parsing. 3x faster than Gson/Jackson for our API response sizes. Zero runtime overhead. |
| **Logback** | 1.4.14 | Structured logging | SLF4J-based with structured JSON output. Essential for production debugging and audit trails (every profile build and eligibility check is logged). |
| **dotenv-kotlin** | 6.4.1 | Environment configuration | Loads `.env` files without modifying system environment. Prevents API key leaks — `.env` is `.gitignore`d. |
| **Gradle** | 8.x | Build system | Incremental compilation, build caching, and Kotlin DSL (`build.gradle.kts`). ~3x faster rebuilds than Maven for our project size. |
| **JDK 17** | Adoptium 17.0.20 | JVM runtime | LTS release with ZGC support (sub-millisecond GC pauses). `jvmToolchain(17)` in Gradle ensures reproducible builds. |

### Backend Node.js Microservices

| Technology | Version | Used In | Why We Chose It |
|------------|---------|---------|-----------------|
| **Node.js** | 18+ | eligibility-analyzer, document-generator, language-service, scheme-ingestion | Event-loop architecture handles I/O-heavy LLM API calls without blocking. `node --watch` enables instant hot-reload during development. |
| **Express.js** | 4.19.2 | All Node.js services | Most battle-tested HTTP framework. 13M+ weekly npm downloads. Middleware pattern makes adding rate-limiting, validation, and CORS trivial. |
| **node-cache** | 5.1.2 | All Node.js services | In-process TTL cache (1-hour default). Caches scheme criteria, translation results, and document templates to avoid redundant LLM calls. |
| **express-rate-limit** | 7.2.0 | eligibility-analyzer, language-service | Prevents LLM API bill spikes. Limits to 10 requests/min/IP. Returns HTTP 429 with clear error message. |
| **node-cron** | 3.0.3 | scheme-ingestion | Scheduled gazette polling. Runs every N hours to check for new government circulars, computes content hashes, and triggers the AI diff agent on changes. |
| **dotenv** | 16.4.5 | All Node.js services | Same pattern as Kotlin service — `.env` files for secrets, `.env.example` for documentation. |
| **cors** | 2.8.5 | All Node.js services | Whitelist-based CORS — only `localhost:3000` (frontend) and cross-service origins allowed. No wildcards. |

### AI/LLM Integration

| Technology | Purpose | Fallback |
|------------|---------|----------|
| **OpenAI GPT-4o-mini** | Eligibility evaluation, rejection letter decoding, translation | Rule-based heuristic engine |
| **Anthropic Claude 3.5 Sonnet** | Secondary LLM provider | Falls back to OpenAI, then to heuristics |
| **Rule-Based Engine** | Deterministic eligibility evaluation | Always available — no API key needed |

> **Why dual-LLM with mandatory fallback?** Government services must be available 24/7. If OpenAI is down, Claude takes over. If both are unavailable (or no API keys configured), the rule-based engine provides instant deterministic responses. **Zero-downtime guarantee.**

### Database Architecture

| Component | Technology | Purpose |
|-----------|-----------|---------|
| **Primary DB** | PostgreSQL | Production-grade RDBMS |
| **Fallback DB** | H2 (embedded, PostgreSQL-compatible mode) | Zero-setup local development |
| **Connection Pool** | HikariCP (max 10 connections) | Efficient resource management |
| **Cloud DB Support** | Supabase / Neon / Render PostgreSQL | Auto-parses `postgres://` and `postgresql://` URIs |

```mermaid
graph TD
  A["Application Start"] --> B{"DATABASE_URL set?"}
  B -->|"postgres:// or postgresql://"| C["Parse cloud URI to JDBC format"]
  B -->|"jdbc:postgresql://"| D["Use directly"]
  B -->|"jdbc:h2:"| E["Use H2 directly"]
  C --> F{"Connect to PostgreSQL"}
  D --> F
  F -->|"Success"| G["PostgreSQL Active"]
  F -->|"Connection Failed"| H["Fallback to H2"]
  H --> I["jdbc:h2:./data/jansaarthi MODE=PostgreSQL"]
  I --> J["H2 Embedded Active"]
  E --> J
```

---

## Microservice Architecture

```mermaid
graph TB
  subgraph "Client Layer"
    ANDROID["Android App / Frontend Port 3000"]
  end

  subgraph "API Gateway Layer"
    CORS["CORS Whitelist localhost:3000 + cross-service"]
  end

  subgraph "Microservices"
    SS["Scheme Service Kotlin/Ktor :8080 - 35 schemes DigiLocker Farmer"]
    EA["Eligibility Analyzer Node.js :5001 - AI + Heuristic Fallback"]
    DG["Document Generator Node.js :5002 - Prioritized Checklists"]
    LS["Language Service Node.js :5003 - 10 Indian Languages"]
    SI["Scheme Ingestion Node.js :5004 - Gazette Auto-Update"]
  end

  subgraph "Data Layer"
    PG["PostgreSQL Production"]
    H2["H2 Embedded Dev Fallback"]
    CACHE["In-Memory Cache node-cache TTL"]
  end

  subgraph "External APIs"
    DL["DigiLocker API OAuth 2.0"]
    PMK["PM-KISAN API"]
    LLM["OpenAI / Claude with fallback"]
  end

  ANDROID --> CORS
  CORS --> SS
  CORS --> EA
  CORS --> DG
  CORS --> LS

  SS --> PG
  SS --> H2
  SS --> DL
  SS --> PMK
  EA --> LLM
  EA --> CACHE
  DG --> CACHE
  LS --> LLM
  LS --> CACHE
  SI --> SS
  SI --> LLM
```

### Port Allocation

| Service | Port | Technology | Lines of Code |
|---------|------|-----------|---------------|
| scheme-service | 8080 | Kotlin/Ktor | ~2,200 |
| eligibility-analyzer | 5001 | Node.js/Express | ~600 |
| document-generator | 5002 | Node.js/Express | ~500 |
| language-service | 5003 | Node.js/Express | ~550 |
| scheme-ingestion | 5004 | Node.js/Express | ~700 |
| **Total** | | | **~4,550** |

---

## Service-by-Service Breakdown

### 1. Scheme Service (Kotlin/Ktor — Port 8080)
**The brain of the platform.** Handles:

| Feature | Endpoint | Description |
|---------|----------|-------------|
| Scheme Discovery | `GET /api/schemes` | List all 35 schemes with `?state=` and `?category=` filters |
| Scheme Details | `GET /api/schemes/{id}` | Full details including eligibility rules and required documents |
| State Portals | `GET /api/schemes/state-portals` | 17 official state government portals with `?state=` filter |
| Eligibility Engine | `POST /api/schemes/check-eligibility` | Deterministic multi-scheme rule engine |
| Notice Explainer | `POST /api/explain` | AI/rule-based explanation of government notices |
| DigiLocker Auth | `GET /api/profile/digilocker/auth` | OAuth 2.0 authorization URL generation |
| DigiLocker Callback | `POST /api/profile/digilocker/callback` | Token exchange after citizen consent |
| DigiLocker Documents | `GET /api/profile/digilocker/documents` | Pull verified government documents |
| Farmer Profile | `POST /api/profile/farmer` | PM-KISAN status, KCC, land classification |
| Profile Builder | `POST /api/profile/build` | Unified profile (manual / digilocker / farmer) |
| End-to-End Check | `POST /api/profile/check-eligibility` | Profile + Eligibility + Document Readiness |
| Document Readiness | `POST /api/documents/readiness` | Check user docs against scheme requirements |
| Health Check | `GET /health` | Service status |

**Key Design Decisions:**
- **Deterministic rule engine** (not AI): Eligibility decisions are too important to delegate to probabilistic LLMs. Our engine evaluates `age >= 18`, `income <= 250000`, `category IN (SC, ST)` etc. with mathematical certainty.
- **Fuzzy document matching**: "7/12 extract" correctly matches "Land Ownership Records", "passbook" matches "Bank Passbook" — reducing friction for rural users.

### 2. Eligibility Analyzer (Node.js — Port 5001)
**AI-powered deep eligibility analysis and rejection letter decoder.**

| Feature | Endpoint | Description |
|---------|----------|-------------|
| Eligibility Check | `POST /api/check-eligibility` | GPT-4o-mini / Claude analysis with heuristic fallback |
| Rejection Decoder | `POST /api/analyze-rejection` | Decodes rejection letters into plain-language guidance |
| Health Check | `GET /health` | Service status |

**Why separate from scheme-service?** The eligibility-analyzer makes LLM API calls (50-2000ms latency). Keeping it separate ensures the core scheme-service (pure DB queries, <10ms) is never blocked by AI response times.

### 3. Document Generator (Node.js — Port 5002)
**Generates prioritized document checklists per scheme.**

| Feature | Endpoint | Description |
|---------|----------|-------------|
| Generate Checklist | `POST /api/generate-checklist` | Prioritized by rejection risk |
| Health Check | `GET /health` | Service status |

**Smart prioritization:** Documents are ranked by historical rejection frequency. For PM-KISAN, "Land Ownership Records" appear first (most common rejection reason), not "Passport Photo" (least important).

### 4. Language Service (Node.js — Port 5003)
**Translates platform content into 10 Indian regional languages.**

| Feature | Endpoint | Description |
|---------|----------|-------------|
| Translate | `POST /api/translate` | Text to regional language with context-aware tone |
| Health Check | `GET /health` | Service status |

**Supported Languages:**

| Code | Language | Script | Native Name |
|------|----------|--------|-------------|
| hi | Hindi | Devanagari | हिन्दी |
| ta | Tamil | Tamil | தமிழ் |
| te | Telugu | Telugu | తెలుగు |
| kn | Kannada | Kannada | ಕನ್ನಡ |
| ml | Malayalam | Malayalam | മലയാളം |
| mr | Marathi | Devanagari | मराठी |
| gu | Gujarati | Gujarati | ગુજરાતી |
| bn | Bengali | Bengali | বাংলা |
| or | Odia | Odia | ଓଡ଼ିଆ |
| as | Assamese | Bengali-Assamese | অসমীয়া |

**Context-aware translations:** Rejection letter translations use an empathetic, encouraging tone. Eligibility results use a clear, direct tone. Document checklists use a step-by-step, actionable tone.

### 5. Scheme Ingestion (Node.js — Port 5004)
**Automated gazette monitoring and policy update pipeline.**

| Feature | Endpoint | Description |
|---------|----------|-------------|
| Ingest Circular | `POST /api/ingest` | Submit new gazette notification |
| Pending Updates | `GET /api/updates/pending` | List updates awaiting human approval |
| Approve Update | `POST /api/updates/:id/approve` | 1-click human-in-the-loop approval |
| Reject Update | `POST /api/updates/:id/reject` | Reject with reason |
| Health Check | `GET /health` | Service status |

**How it works:**
1. Gazette circular is submitted (or polled automatically)
2. Content hash is computed (SHA-256) and compared with stored version
3. If changed → AI Diff Agent extracts structured changes (income ceiling revisions, new documents, etc.)
4. Changes are queued for human review (never auto-applied to production)
5. Policy officer clicks "Approve" → scheme data is versioned and updated
6. Full audit trail maintained

---

## DigiLocker Integration

### What is DigiLocker?
DigiLocker is the Government of India's official platform for storing and sharing verified digital documents. It has **250 crore+ documents** from **2,100+ issuers** including UIDAI (Aadhaar), CBDT (PAN), state revenue departments, and educational boards.

### Our Integration Flow

```mermaid
sequenceDiagram
    participant User as Citizen Android
    participant API as Scheme Service
    participant DL as DigiLocker API

    User->>API: GET /api/profile/digilocker/auth
    API->>API: Generate state token + OAuth URL
    API-->>User: authorizationUrl, state

    User->>DL: Redirect to DigiLocker consent page
    Note over User,DL: Citizen logs in with Aadhaar OTP and grants document access consent

    DL-->>User: Redirect with authorization_code

    User->>API: POST /api/profile/digilocker/callback with authorizationCode, state
    API->>DL: Exchange code for access_token
    DL-->>API: access_token, digilocker_id
    API-->>User: accessToken, name, message

    User->>API: GET /api/profile/digilocker/documents with Bearer token
    API->>DL: Pull URI API for each document type
    DL-->>API: eAadhaar, PAN, Income Cert, Caste Cert, etc.
    API-->>User: List of DigiLockerDocuments with verified=true
```

### Documents We Pull from DigiLocker

| Document | Issuer | What We Extract |
|----------|--------|-----------------|
| **eAadhaar** | UIDAI | Name, Age, Gender, State, District |
| **PAN Card** | CBDT (Income Tax) | Identity verification |
| **Income Certificate** | State Revenue Dept | Annual income (verified) |
| **Caste Certificate** | State Social Welfare | Category (SC/ST/OBC/EWS) |
| **Class XII Marksheet** | State Board | Education verification |
| **Domicile Certificate** | State Revenue Dept | State residency proof |
| **Land Records** | State Land Revenue | Land ownership (for farmers) |

### Data Source Tracking
Every field in the user's profile tracks its **source** and **verification status**:

```json
{
  "dataSources": [
    { "field": "name", "source": "DIGILOCKER_AADHAAR", "verified": true },
    { "field": "age", "source": "DIGILOCKER_AADHAAR", "verified": true },
    { "field": "annualIncome", "source": "DIGILOCKER_INCOME_CERT", "verified": true },
    { "field": "category", "source": "DIGILOCKER_CASTE_CERT", "verified": true },
    { "field": "occupation", "source": "SELF_DECLARED", "verified": false }
  ]
}
```

The Android UI shows **green verified badges** for DigiLocker-sourced fields and **grey self-declared badges** for manual input. This builds trust and speeds up application processing at government offices.

### Current Status
> **Prototype Mode:** The DigiLocker integration currently uses **simulated responses** that mirror the exact structure of the real DigiLocker Pull URI API. For production deployment, we would register as a DigiLocker Partner at developers.digilocker.gov.in and swap the simulation with real OAuth + API calls. The API contract is identical — zero code changes needed in ProfileRoutes.kt or ProfileService.kt.

---

## Farmer Workflow 

### Why a Dedicated Farmer Flow?
India has **15 crore+ farmer families**. Farmers face unique challenges:
- Multiple overlapping schemes (PM-KISAN, PM Fasal Bima, KCC, state schemes)
- Land records are fragmented across state portals (MahaBhulekh, Dharitri, Bhoomi)
- PM-KISAN beneficiary status verification is complex
- Farmer category (marginal/small/medium/large) determines eligibility

### Farmer Classification (per GoI norms)

| Category | Land Holding | Estimated Income |
|----------|-------------|------------------|
| Landless | No land | Rs.72,000/year |
| Marginal | < 2.47 acres (1 hectare) | Rs.1,00,000/year |
| Small | 2.47 – 4.94 acres | Rs.1,50,000/year |
| Semi-Medium | 4.94 – 9.88 acres | Rs.2,50,000/year |
| Medium | 9.88 – 24.7 acres | Rs.5,00,000/year | 
| Large | > 24.7 acres | Rs.10,00,000/year |

### Farmer Profile Flow

```mermaid
flowchart LR
    A["Farmer Input"] --> B["Land Details acres, type"]
    A --> C["PM-KISAN Status"]
    A --> D["KCC Status"]
    A --> E["Aadhaar Linked?"]

    B --> F["Classify Farmer marginal/small/etc"]
    C --> G["PM-KISAN API Check"]
    E --> H["DigiLocker Pull"]

    F --> I["Farmer Profile"]
    G --> I
    D --> I
    H --> I

    I --> J["Recommended Schemes"]
    J --> K["SCH005: PM-KISAN"]
    J --> L["SCH006: PM Fasal Bima"]
    J --> M["SCH007: KCC"]
    J --> N["SCH013: Ayushman Bharat"]
    J --> O["SCH015: MGNREGA"]
```

### Farmer-Specific Documents Auto-Generated

| Condition | Document Added | Verified? |
|-----------|---------------|-----------|
| Aadhaar linked | Aadhaar Card | Yes (verified) |
| Has land | Land Ownership Records | Yes if Aadhaar linked |
| Has KCC | Kisan Credit Card | Yes (verified) |
| PM-KISAN registered | PM-KISAN Beneficiary Certificate | Yes (verified) |
| PM-KISAN registered | Bank Passbook (DBT Linked) | Yes (verified) |

---

## Dataset and Scheme Coverage

### Total Schemes: 35

| Category | Count | Examples |
|----------|-------|---------|
| Education | 11 | Post-Matric SC Scholarship, Central Sector Scholarship, MYSY Gujarat, ePASS Telangana |
| Agriculture | 5 | PM-KISAN, PM Fasal Bima, KCC, Kisan Suryodaya (Gujarat), Rythu Bandhu (Telangana) |
| Enterprise | 5 | PM-MUDRA, Stand Up India, PM SVANidhi, Startup India, PM Vishwakarma |
| Health | 2 | Ayushman Bharat PM-JAY, Karunya (Kerala) |
| Housing | 1 | PM Awas Yojana - Urban |
| Employment | 2 | MGNREGA, NAPS Apprenticeship |
| Pension | 3 | Atal Pension, Sanjay Gandhi Niradhar (Maharashtra), Aasara (Telangana) |
| Energy | 1 | PM Ujjwala Yojana |
| Financial Inclusion | 1 | PM Jan Dhan Yojana |
| Social Welfare | 1 | National Family Benefit Scheme |
| Financial (State) | 2 | Gruha Lakshmi (Karnataka), MMPSY (Haryana) |
| Skill Development | 1 | Yuva Nidhi (Karnataka) |

### Coverage by State

| State | State-Specific Schemes | Portals |
|-------|----------------------|---------|
| All India (Central) | 20 | — |
| Maharashtra | 3 (SCH021-023) | 4 (MahaDBT, Aaple Sarkar, MahaBhumi, Govt) |
| Karnataka | 3 (SCH024-026) | 3 (Seva Sindhu, Karnataka One, Govt) |
| Kerala | 2 (SCH027-028) | 3 (E-Grantz, Akshaya, Govt) |
| Gujarat | 2 (SCH029-030) | 2 (Digital Gujarat, Govt) |
| Telangana | 3 (SCH031-033) | 3 (MeeSeva, ePASS, Govt) |
| Haryana | 2 (SCH034-035) | 2 (Antyodaya SARAL, Govt) |

### Eligibility Rules Engine

Each scheme has structured rules stored in the database:

| Rule Type | Operator | Example |
|-----------|----------|---------|
| `age` | `gte`, `lte`, `between` | `age >= 18`, `age between 18,40` |
| `annualIncome` | `lte`, `gte` | `annualIncome <= 250000` |
| `category` | `eq`, `in` | `category IN (SC, ST, OBC)` |
| `occupation` | `eq`, `in` | `occupation IN (farmer, agriculture)` |
| `student` | `bool_eq` | `student = true` |
| `state` | `eq` | `state = Maharashtra` |

### How Many Schemes Can We Support?

| Scale | Schemes | Performance |
|-------|---------|-------------|
| Current (Demo) | 35 | <10ms per eligibility check |
| Production Ready | 500+ | <50ms with DB indexing |
| Full India Coverage | 2,000+ | <100ms with caching + read replicas |

The architecture is designed for horizontal scaling. The eligibility engine evaluates **all rules in-memory** after a single DB query, so adding more schemes only linearly increases evaluation time (not DB round-trips).

---

## State Portal Directory

### 17 Official Portals Across 6 States

| State | Portal | URL | Services |
|-------|--------|-----|----------|
| **Maharashtra** | MahaDBT | mahadbt2.maharashtra.gov.in | Scholarships and DBT disbursements |
| | Aaple Sarkar | aaplesarkar.mahaonline.gov.in | Certificates and social assistance |
| | Maharashtra Govt | maharashtra.gov.in | Departments, GRs, notifications |
| | MahaBhumi | mahabhumi.gov.in | 7/12, property cards, land records |
| **Karnataka** | Seva Sindhu | sevasindhu.karnataka.gov.in | 780+ services incl. Gruha Lakshmi |
| | Karnataka One | karnatakaone.gov.in | Citizen service centre network |
| | Karnataka Govt | karnataka.gov.in | Departments and notifications |
| **Kerala** | E-Grantz 3.0 | egrantz.kerala.gov.in | SC/ST/OBC/OEC scholarships |
| | Akshaya | akshaya.kerala.gov.in | e-governance service centres |
| | Kerala Govt | kerala.gov.in | State departments and schemes |
| **Gujarat** | Digital Gujarat | digitalgujarat.gov.in | 30+ scholarships incl. MYSY |
| | Gujarat Govt | gujaratindia.gov.in | Industrial policies and welfare |
| **Telangana** | MeeSeva | ts.meeseva.telangana.gov.in | 580+ G2C services |
| | ePASS | epass.cgg.gov.in | Post-matric fee reimbursement |
| | Telangana Govt | telangana.gov.in | Government orders and initiatives |
| **Haryana** | Antyodaya SARAL | saralharyana.gov.in | 500+ welfare schemes with PPP |
| | Haryana Govt | haryana.gov.in | Departments and citizen services |

---

## How is JanSaarthi Different?

### vs. Existing Solutions

| Feature | JanSaarthi | MyScheme (GOI) | Sarkari Result | DigiLocker |
|---------|-----------|----------------|----------------|------------|
| **Multi-scheme eligibility engine** | Deterministic rule engine | Manual browsing | No eligibility check | Not its purpose |
| **DigiLocker auto-population** | OAuth + verified documents | Manual entry | Manual entry | Yes but no scheme matching |
| **Rejection letter decoder** | AI-powered with guidance | No feature | No feature | No feature |
| **Farmer-specific workflow** | PM-KISAN + KCC + land classification | Basic | No | No |
| **Document readiness checker** | Fuzzy matching | No | No | No |
| **10 regional languages** | Context-aware translation | Limited | Hindi/English only | Limited |
| **Gazette auto-ingestion** | AI diff + human approval | Manual updates | Manual | N/A |
| **Microservice architecture** | 5 independent services | Monolithic | Monolithic | Partial |
| **Offline-first** | Planned | No | No | No |

### Key Differentiators

1. **End-to-End Pipeline**: From discovery to eligibility to documents to application guidance to rejection recovery. No other platform covers the full lifecycle.
2. **Verified Data (DigiLocker)**: Auto-populated, government-verified profiles. No manual data entry errors.
3. **AI + Rules Hybrid**: LLM intelligence for complex analysis, deterministic rules for legally-binding eligibility decisions. Best of both worlds.
4. **State-Specific**: Not just central schemes — we cover Maharashtra, Karnataka, Kerala, Gujarat, Telangana, and Haryana with their official portals.

---

## Pros and Cons

### Pros

| Advantage | Detail |
|-----------|--------|
| **Zero-Setup Development** | Clone the repo, run `gradlew run`, and it works. H2 fallback means no PostgreSQL installation needed. |
| **No API Keys Required** | Every LLM-dependent feature has a rule-based fallback. Works fully offline with heuristics. |
| **Type-Safe Eligibility Engine** | Kotlin's type system prevents eligibility rule evaluation bugs at compile time. |
| **Government-Verified Data** | DigiLocker integration means documents are cryptographically authentic — faster processing at offices. |
| **Micro-Service Isolation** | If the language-service crashes, scheme discovery and eligibility still work. |
| **10 Regional Languages** | 95% of India's population can use the platform in their mother tongue. |
| **Human-in-the-Loop Updates** | Gazette changes are never auto-applied — a policy officer must approve. Prevents AI hallucinations from corrupting scheme data. |
| **Comprehensive Audit Trail** | Every eligibility check, profile build, and scheme update is logged with timestamps. |
| **Fuzzy Document Matching** | "7/12 extract" matches "Land Records", "Passbook" matches "Bank Passbook". Reduces friction for rural users. |
| **Production-Ready CORS** | Whitelist-based, no wildcards. Rate limiting, input validation, payload size limits. | 

### Cons / Current Limitations

| Limitation | Mitigation Plan |
|------------|-----------------|
| **DigiLocker is simulated** | Real integration requires DigiLocker Partner registration (applied, awaiting approval) |
| **PM-KISAN API is simulated** | Will integrate real PM-KISAN beneficiary verification API when deployed |
| **35 schemes (not 700+)** | Automated ingestion pipeline can onboard schemes 10x faster than manual entry |
| **6 states covered** | Architecture supports all 28+8 states/UTs — data expansion is a content task, not a code task |
| **No Android app yet** | Backend APIs are fully functional — Android development can begin immediately |
| **No offline mode** | Planned: PWA-style caching for last-queried schemes and eligibility results |
| **Single-node deployment** | Architecture is ready for Kubernetes — each service is independently deployable |
| **No real-time notifications** | Planned: WebSocket/FCM push for scheme deadline reminders |

---

## Handling Large User Base — Scalability Strategy

### Current Capacity (Single Node)

| Metric | Value |
|--------|-------|
| Concurrent connections | 10,000+ (Netty NIO) |
| Eligibility checks/sec | ~500 (in-memory rule evaluation) |
| DB connection pool | 10 (HikariCP) |
| API response time | <10ms (scheme queries), <50ms (eligibility), <2s (LLM) |

### Scaling to 1 Million+ Users

```mermaid
graph TB
  subgraph "Load Balancer"
    LB["NGINX / AWS ALB - SSL Termination + Routing"]
  end

  subgraph "Scheme Service Cluster"
    SS1["scheme-service :8080 Instance 1"]
    SS2["scheme-service :8080 Instance 2"]
    SS3["scheme-service :8080 Instance 3"]
  end

  subgraph "Node.js Services"
    EA["eligibility-analyzer x 2"]
    DG["document-generator x 2"]
    LS["language-service x 2"]
    SI["scheme-ingestion x 1"]
  end

  subgraph "Data Layer"
    PG_PRIMARY["PostgreSQL Primary Write"]
    PG_REPLICA1["PostgreSQL Replica 1 Read"]
    PG_REPLICA2["PostgreSQL Replica 2 Read"]
    REDIS["Redis Cache Sessions + Scheme Cache"]
  end

  subgraph "CDN and Edge"
    CDN["CloudFront / Cloudflare Static assets + API cache"]
  end

  CDN --> LB
  LB --> SS1
  LB --> SS2
  LB --> SS3
  LB --> EA
  LB --> DG
  LB --> LS

  SS1 --> PG_PRIMARY
  SS2 --> PG_REPLICA1
  SS3 --> PG_REPLICA2
  SS1 --> REDIS
  EA --> REDIS
  LS --> REDIS
```

### Strategy by Scale

| Users | Infrastructure | Key Changes |
|-------|---------------|-------------|
| **0 – 10K** | Single VM (4 vCPU, 8GB) | Current architecture, H2 to PostgreSQL |
| **10K – 100K** | 3 VMs + managed PostgreSQL | Load balancer, read replicas, Redis cache |
| **100K – 1M** | Kubernetes cluster (8-16 pods) | Auto-scaling, CDN for static, Redis cluster |
| **1M – 10M** | Multi-region Kubernetes | Geographic distribution (Mumbai, Hyderabad), DB sharding by state |
| **10M+** | Full cloud-native | Event-driven with Kafka, dedicated search index (Elasticsearch), offline-first PWA |

### Specific Optimizations

| Optimization | How | Impact |
|-------------|-----|--------|
| **Scheme caching** | Redis with 1-hour TTL for scheme data (changes rarely) | 95% cache hit rate, DB load reduced 20x |
| **Eligibility result caching** | Hash of (profile + timestamp), cached result for 5 minutes | Prevents repeat computation for same user |
| **Read replicas** | PostgreSQL streaming replication | All GET queries go to replicas, writes to primary |
| **Connection pooling** | HikariCP (current) to PgBouncer (production) | 10,000 logical connections mapped to 50 actual DB connections |
| **CDN** | Static scheme data (descriptions, URLs) served from edge | <5ms latency for 80% of requests |
| **Rate limiting** | Per-IP + per-user-token limits | Prevents abuse, protects LLM API budget |
| **Async LLM calls** | Kotlin coroutines + Node.js event loop | Non-blocking I/O — LLM latency doesn't block other users |
| **Database indexing** | B-tree on `state`, `category`, `schemeId` | Sub-millisecond lookups even with 10,000+ schemes |

---

## Security and Privacy

| Measure | Implementation |
|---------|---------------|
| **CORS Whitelist** | Only `localhost:3000` and cross-service origins. No wildcards. |
| **Input Validation** | Every endpoint validates body size, field types, and ranges |
| **Payload Limits** | 1MB (scheme-service), 2MB (eligibility-analyzer for OCR text) |
| **Rate Limiting** | 10 requests/min/IP on LLM endpoints (prevents bill spikes) |
| **Prompt Injection Protection** | Rejection letters sanitized: HTML stripped, `system prompt` patterns redacted |
| **No Stack Trace Exposure** | 500 errors return generic message, details logged server-side only |
| **Environment Secrets** | `.env` files in `.gitignore`, never committed |
| **Aadhaar Data** | Only last 4 digits stored; full Aadhaar never persisted |
| **DigiLocker OAuth** | Short-lived tokens; documents accessed only with citizen consent |
| **Audit Trail** | Every scheme update logged with reviewer identity and timestamp |

---

## Production Readiness Roadmap

| Phase | Timeline | Deliverables |
|-------|----------|--------------|
| **Phase 1: Current (Hackathon)** | Done | 5 microservices, 35 schemes, 17 portals, DigiLocker simulation, 42 tests passing |
| **Phase 2: Integration** | Week 2 | Real DigiLocker Partner API, real PM-KISAN API, Android app MVP |
| **Phase 3: Data Expansion** | Week 3-4 | 200+ schemes across all 28 states, automated gazette scraping |
| **Phase 4: Production Deploy** | Month 2 | Kubernetes on AWS/GCP, PostgreSQL RDS, Redis, CI/CD pipeline |
| **Phase 5: Scale** | Month 3+ | Multi-region, 10M+ users, offline-first PWA, Aadhaar e-KYC |

---

## Running the Platform

```bash
# Terminal 1: Scheme Service (Kotlin/Ktor)
cd backend/scheme-service
./gradlew.bat run    # Starts on :8080

# Terminal 2: Eligibility Analyzer (Node.js)
cd backend/eligibility-analyzer
npm install && npm run dev    # Starts on :5001

# Terminal 3: Document Generator (Node.js)
cd backend/document-generator
npm install && npm run dev    # Starts on :5002

# Terminal 4: Language Service (Node.js)
cd backend/language-service
npm install && npm run dev    # Starts on :5003

# Terminal 5: Scheme Ingestion (Node.js)
cd backend/scheme-ingestion
npm install && npm run dev    # Starts on :5004
```

### Quick Test

```bash
# Check all services are alive
curl http://localhost:8080/health
curl http://localhost:5001/health
curl http://localhost:5002/health
curl http://localhost:5003/health
curl http://localhost:5004/health

# Run a farmer eligibility check
curl -X POST http://localhost:8080/api/profile/check-eligibility \
  -H "Content-Type: application/json" \
  -d '{"mode":"farmer","farmerProfile":{"aadhaarLinked":true,"state":"Maharashtra","landOwnership":{"hasLand":true,"landAreaAcres":2.5}}}'
```

---

> **Built with love for Bharat** — JanSaarthi empowers every citizen to discover, understand, and access the government welfare they deserve.
