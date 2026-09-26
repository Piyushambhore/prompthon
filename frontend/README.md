# JanSaarthi Frontend UI Documentation

Welcome to the **JanSaarthi Frontend UI** package. This directory contains the pure Jetpack Compose Material 3 UI screens for the JanSaarthi Citizen Welfare Platform.

---

## Architecture Principles
- **Frontend UI Only**: Pure declarative Jetpack Compose Material 3 implementation.
- **Zero Backend / Database / Firebase / APIs**: Uses temporary local/mock state with interactive reactive state (`remember { mutableStateOf(...) }`).
- **High Accessibility Across Generations**:
  - **Children & Youths**: Clean visual badges, modern layout, intuitive iconography.
  - **Adults & Working Citizens**: Real-time application tracker, Direct Benefit Transfer (DBT) schedules, document vault.
  - **Senior Citizens & Rural Communities**: High contrast color palette (Govt Navy `#0A3871`, Saffron `#FF671F`, India Green `#046A38`), 48-56dp+ touch targets, font-scale toggling (A / A+), and Voice Search simulation.
- **Multilingual Support**: Real-time switching between 11 Indian languages (Hindi, English, Marathi, Tamil, Telugu, Bengali, Gujarati, Kannada, Malayalam, Punjabi, Odia).

---

## 1. JanSaarthi Home Screen (`frontend/JanSaarthiHomeScreen.kt`)

The Home Screen delivers a comprehensive, citizen-first single-window dashboard featuring **11 mandated core sections**:

### 1. Namaste / Citizen Greeting & Header
- **Greeting**: Warm time-appropriate greeting ("नमस्ते, Rajesh Sharma" / "Namaste, Citizen").
- **Citizen Avatar**: Profile indicator with verification badge (`Verified Citizen` / Guest mode pill).
- **Resident State Pill**: Displays the citizen's active state (e.g. `📍 Maharashtra`) with an instant `✎` quick-change trigger.
- **Accessibility Controls**: One-tap quick language badge (`HI`, `EN`, `MR`...) and high-contrast font size adjuster.
- **Notification Bell**: Live unread count badge linking to official alerts.

### 2. Search Government Schemes
- **Accessible Search Bar**: Elevated Material 3 text field with leading search icon and trailing clear `X` button.
- **Voice Search Mock**: Saffron microphone button tailored for senior citizens and neo-literate rural users.
- **Interactive Quick Filter Chips**: One-touch filter pills (`🌾 Farmers`, `🏥 Healthcare`, `👵 Pension`, `🎓 Scholarship`, `🏠 Housing`).
- **Instant Reactive Filtering**: Filters schemes live across titles, departments, and benefit descriptions.

### 3. "Find Schemes For Me" (Hero CTA Banner)
- **High-Impact Hero Card**: Deep slate and navy gradient with golden tricolor accents.
- **Value Proposition**: "Answer simple profile questions to discover 100% matched Central & State benefits for you and your family."
- **Trust Badges**: `⚡ 2-Minute Check`, `🔒 100% Private (DPDP Act 2023)`, `📜 Official NIC Catalog`.
- **Primary CTA Button**: 50dp high-contrast saffron button: `Check My Scheme Eligibility →` (navigates directly to the 12-section Eligibility Profile screen).

### 4. Browse All Categories
- **Domain Cards with Scheme Counts**:
  - 🌾 *Agriculture & Farmers* (18 schemes)
  - 🏥 *Healthcare & Arogya* (12 schemes)
  - 🏠 *Housing & Shelter* (8 schemes)
  - 🎓 *Education & Scholarships* (24 schemes)
  - 👵 *Social Assistance & Pensions* (10 schemes)
  - 🍲 *Food & Ration Security* (6 schemes)
  - 👩‍👧 *Women & Child Welfare* (14 schemes)
  - 💼 *Employment & MSME* (9 schemes)
- **Interactive Filtering**: Tapping a category filters all scheme lists on screen, with a `Clear Filter` option.

### 5. Central Government Schemes
- **Flagship Nationwide Programs**:
  - **PM-KISAN** (Pradhan Mantri Kisan Samman Nidhi - ₹6,000/yr direct income support via DBT).
  - **Ayushman Bharat (PM-JAY)** (₹5 Lakhs free cashless healthcare coverage per family).
  - **Pradhan Mantri Awas Yojana (PMAY)** (Pucca house financial assistance).
- **National Badges**: `🇮🇳 Central Government` and `⚡ DBT Active`.
- **Interactive Save Action**: Bookmark (★) button on every card.

### 6. State / UT Schemes
- **Resident-Specific Initiatives**: Tailored dynamically to the citizen's selected state (e.g., Maharashtra: *Mukhyamantri Majhi Ladki Bahin Yojana*, *Namo Shetkari Mahasanman Nidhi*, *Mahatma Jyotirao Phule Jan Arogya Yojana*).
- **Header Action**: Shows `🏛️ [State Name] State Schemes` with direct `Change State ✎` button.
- **State Badges**: `🏛️ State Level • Maharashtra Resident`.

### 7. Recently Viewed
- **Quick Recall Carousel**: Horizontal cards showing recently explored schemes.
- **Context Details**: Includes timestamp pills ("1 hour ago", "Yesterday"), category icons, and "Resume →" triggers.

### 8. Saved Schemes (Bookmarks)
- **Interactive Bookmark System**: Real-time local state (`savedSchemeIds`) allowing users to bookmark or remove schemes across the app.
- **Populated View**: Clean cards with scheme details, direct benefit amounts, and unsave buttons.
- **Empty State**: Senior-friendly illustration explaining how to bookmark schemes for offline review.

### 9. My Applications Tracker
- **Multi-Stage Stepper Cards**:
  - Displays Application IDs (e.g., `APP-PMK-2024-89210`).
  - 4-Stage visual progress bar: `Submitted` → `Aadhaar & Land Verified` → `District Sanction` → `DBT Disbursal`.
  - Next schedule reminder: "Next ₹2,000 DBT release scheduled on 15 Oct 2024".

### 10. My Documents (Digital Vault / DigiLocker)
- **Verified Credentials Vault**:
  - *Aadhaar Card* (`Verified & DBT Linked 🟢`)
  - *Ration Card (NFSA)* (`Active Beneficiary 🟢`)
  - *Income Certificate* (`Valid till 31 Mar 2025 🟡`)
  - *7/12 Land Record Extract* (`Digitally Signed 🟢`)
- **DigiLocker Sync**: Interactive button to trigger mock credential synchronization.

### 11. Government Updates & Bulletins
- **Official Public Gazettes**:
  - DBT disbursement announcements (`PM-KISAN ₹20,000 Cr Disbursed`).
  - Application deadline extensions (`National Scholarship Portal Pre-Matric extended`).
  - Public health camps (`Ayushman Arogya Mandirs Free Screening`).
- **Issuing Authorities**: `Press Information Bureau (PIB)`, `Ministry of Health`, `Ministry of Agriculture`.

---

## 2. Eligibility Profile Screen (`frontend/EligibilityProfileScreen.kt`)

Contains the complete **12-section citizen qualification assessment**:
1. Date of Birth / Age (Digit input with Age in years & optional DOB)
2. Gender Selection (Female, Male, Other)
3. State & District (All 36 States & UTs with contextual District filtering)
4. Social Category (General, OBC, SC, ST, EWS)
5. Minority Status & Community
6. Disability Status (Divyangjan / PwD categories)
7. Student Status & Education Level
8. Employment Status
9. Farmer Status & Landholding size
10. Annual Family Income bracket
11. Rural / Urban Residence
12. High-contrast 56dp Action Buttons (`Continue to Applicable Schemes`)

---

## 3. Government Scheme Categories Screen (`frontend/CategoriesScreen.kt`)

The Categories screen provides a complete, accessible directory organizing the entire catalog into the **15 official myScheme broad domains**:

| # | Official Category Name | Icon | Accent & Tint | Scheme Count | Sample Flagship Initiatives |
|---|------------------------|------|---------------|:------------:|-----------------------------|
| 1 | **Agriculture, Rural & Environment** | `Agriculture` | Emerald Green (`#15803D` / `#F0FDF4`) | 64 | PM-KISAN, PM Fasal Bima, PMKSY Irrigation |
| 2 | **Banking, Financial Services & Insurance** | `AccountBalance` | Royal Indigo (`#3730A3` / `#EEF2FF`) | 38 | PM Jan Dhan, PM Suraksha Bima, Atal Pension |
| 3 | **Business & Entrepreneurship** | `BusinessCenter` | Warm Amber (`#9A3412` / `#FFF7ED`) | 42 | PM Mudra, Stand-Up India, PMEGP Subsidy |
| 4 | **Education & Learning** | `School` | Royal Blue (`#1D4ED8` / `#EFF6FF`) | 55 | National Scholarship Portal, PM POSHAN, Samagra Shiksha |
| 5 | **Health & Wellness** | `HealthAndSafety` | Teal Cyan (`#0F766E` / `#F0FDFA`) | 49 | Ayushman Bharat PM-JAY, Jan Aushadhi, Mission Indradhanush |
| 6 | **Housing & Shelter** | `Home` | Amber Gold (`#B45309` / `#FFFBEB`) | 27 | PM Awas Yojana (Gramin & Urban), CLSS Subsidy |
| 7 | **Public Safety, Law & Justice** | `Gavel` | Crimson Red (`#991B1B` / `#FEF2F2`) | 19 | Tele-Law Portal, NALSA Free Legal Aid, 112 Helpline |
| 8 | **Science, IT & Communications** | `Devices` | Deep Purple (`#6D28D9` / `#FAF5FF`) | 24 | BharatNet Optical Fiber, PMGDISHA, INSPIRE Grants |
| 9 | **Skills & Employment** | `Handyman` | Slate Neutral (`#334155` / `#F8FAFC`) | 51 | PM Kaushal Vikas (PMKVY), MGNREGA, PM Vishwakarma |
| 10 | **Social Welfare & Empowerment** | `Elderly` | Deep Violet (`#581C87` / `#F5F3FF`) | 68 | Old Age Pension, ADIP Divyang Aids, PM-DAKSH |
| 11 | **Sports & Culture** | `EmojiEvents` | Saffron Orange (`#C2410C` / `#FFF7ED`) | 22 | Khelo India, TOPS Olympic Podium, Folk Artist Grants |
| 12 | **Transport & Infrastructure** | `DirectionsBus` | Dark Slate (`#1E293B` / `#F1F5F9`) | 16 | PM Gram Sadak (PMGSY), FAME India EV Subsidies |
| 13 | **Travel & Tourism** | `Flight` | Deep Cyan (`#0E7490` / `#ECFEFF`) | 18 | PRASHAD Pilgrimage, Swadesh Darshan 2.0, UDAN |
| 14 | **Utility & Sanitation** | `WaterDrop` | Ocean Sky (`#0369A1` / `#F0F9FF`) | 33 | Jal Jeevan (Har Ghar Jal), Swachh Bharat, PM Ujjwala |
| 15 | **Women & Child** | `FamilyRestroom` | Rose Magenta (`#BE185D` / `#FDF2F8`) | 45 | Beti Bachao Beti Padhao, Sukanya Samriddhi, Poshan 2.0 |

### Key Features & Design
- **2-Column Responsive Cards**: Balanced grid (`GridCells.Fixed(2)`) optimized for mobile screens.
- **Material 3 Icons**: Distinctive official icons representing each sector clearly.
- **Subtle Unique Colors**: Each category features an accessible soft tint background and accent-matched border stroke.
- **Scheme Count Placeholder**: Visual badge showing scheme volume (summing to 569+ total verified initiatives).
- **Interactive Click State**: Tapping any card opens an interactive Category Preview Modal detailing key flagship schemes, total volume, and deep exploration CTAs.
- **Instant Search / Filter**: Filter categories in real time by English name, Hindi name, description, or scheme keywords.
- **Accessibility & Multilingual**: Supports high contrast, A/A+ dynamic text resizing, and real-time English/Hindi language toggle.

---

---

## 4. Reusable Government Scheme List Screen (`frontend/SchemeListScreen.kt`)

The Scheme List screen is opened when any category is selected from the Categories screen or Home screen. It provides a citizen-friendly directory allowing users to search, filter, and inspect welfare schemes across Central, State, and UT domains.

> **UI Demonstration Disclaimer**:
> This screen utilizes realistic mock government scheme data designed strictly for prototype evaluation. It does NOT claim to represent live government portal records. Implemented **purely in frontend UI** using Jetpack Compose and Material 3 without any backend, external API, or database dependencies.

### Features & Filters Included
1. **Dynamic Category Header**: Prominently shows active domain name (e.g. `Agriculture, Rural & Environment`) with back navigation and filter trigger.
2. **Accessible Live Search**: Real-time filtering across titles, ministries, benefits, and eligibility criteria.
3. **Jurisdiction Level Filter**: `All Levels`, `🇮🇳 Central`, `🏛️ State`, `🏙️ UT`.
4. **State / UT Filter**: Select between All India (Nationwide) and specific states (Maharashtra, Uttar Pradesh, Tamil Nadu, Bihar, Karnataka, Delhi, etc.).
5. **Ministry Filter**: Filter by 12+ Union and State ministries (Agriculture, Finance, Health, Education, Rural Development, MSME, etc.).
6. **Gender Filter**: `All Genders`, `Female Only`, `Male Only`, `Transgender`.
7. **Age Bracket Filter**: `All Ages`, `Children (0-17)`, `Youth (18-35)`, `Working Adults (36-59)`, `Senior Citizens (60+)`.
8. **Category Domain Filter**: Filter dynamically across any of the 15 official myScheme domains.
9. **Benefit Type Filter**: `All Benefits`, `DBT Cash Transfer`, `Subsidy & Grant`, `Loan & Credit Guarantee`, `Health & Insurance`, `Scholarship & Training`, `In-Kind & Utilities`.
10. **Targeted Eligibility Toggles**: `Direct Benefit Transfer (DBT) Only`, `Low Income / BPL Priority (< ₹2.5 Lakhs)`.
11. **Sort Engine**: Sort by `Most Popular`, `Highest Financial Benefit`, `Newly Launched`, and `Alphabetical (A to Z)`.
12. **Rich Scheme Cards**: Shows title, ministry, benefit highlights banner, eligibility snippet, bookmark toggle, and `View Details & Apply →` CTA.
13. **Interactive Scheme Details Modal**: Full overview, benefits breakdown, required documents list, application process steps, and official portal link simulation.

---

## 5. "All Government Schemes" Screen (`frontend/AllSchemesScreen.kt`)

The **All Government Schemes** screen is a dedicated single-window national directory offering complete visibility across all tiers of government administration in India.

> **UI Demonstration Disclaimer**:
> This screen utilizes realistic mock government scheme data designed strictly for prototype evaluation. It does NOT claim to represent live government portal records. Implemented **purely in frontend UI** using Jetpack Compose and Material 3 without any backend, external API, or database dependencies.

### 1. Primary Government Level Tabs (with live badge counters)
- **All** (24+ initiatives): Comprehensive aggregated view across the entire Indian welfare architecture.
- **Central Government** (12 flagship initiatives): 100% centrally sponsored nationwide schemes (PM-KISAN, Ayushman Bharat PM-JAY, PMMY, PMAY-G, Sukanya Samriddhi, PM Vishwakarma, etc.).
- **State Government** (8 state-specific initiatives): State government funded welfare initiatives customized to specific states (Maharashtra *Mukhyamantri Majhi Ladki Bahin*, *Namo Shetkari*, Tamil Nadu *Kalaignar Magalir Urimai Thittam*, Uttar Pradesh *Kanya Sumangala*, Bihar *Kanya Utthan*, Karnataka *Gruha Lakshmi*, etc.).
- **Union Territory** (4 UT initiatives): Tailored welfare programs across Indian UTs (Delhi *Free Bus Travel Pink Pass*, J&K *Ladli Beti*, Puducherry *Free Student Uniforms & Cycles*, Chandigarh *Senior Citizen Concession*).

### 2. Search & Filter UI
- **Live Search Bar**: Real-time reactive query filtering across scheme names (English & Hindi), ministries, states/UTs, categories, benefits, and eligibility criteria.
- **Quick Target Group Filter Chips**: Instant one-tap segment filtering (`All Citizens`, `🌾 Farmers`, `👩 Women`, `🎓 Youth`, `👵 Seniors`).
- **Comprehensive Filter Modal**:
  - Filter by Government Level (`All`, `Central`, `State`, `UT`)
  - Filter by State / UT (36 States & UTs)
  - Filter by Category (All 15 myScheme domains)
  - Direct Benefit Transfer (DBT) Only toggle
  - Reset / Apply controls
- **Dynamic Sort Engine**: Sort by `Most Popular`, `Highest Financial Benefit`, `Newly Launched`, and `Alphabetical (A to Z)`.

### 3. Master Scheme Card (8 Mandated Core Elements)
Every scheme card in the catalog explicitly displays:
1. **Scheme Name**: Bold, legible title in English with Hindi subtitle.
2. **Government Level Badge**: High-contrast jurisdiction pill (`🇮🇳 Central Government`, `🏛️ State Government`, `🏙️ Union Territory`).
3. **State / UT Indicator**: Specific geographical scope (`📍 National (All States & UTs)`, `📍 Maharashtra`, `📍 NCT of Delhi`, `📍 Puducherry`, etc.).
4. **Ministry / Department**: Official issuing department (`Ministry of Agriculture`, `Ministry of Finance`, `Dept. of Women & Child Development`, etc.).
5. **Category**: Domain identifier with distinctive Material 3 icon and themed soft tint (e.g. `Health & Wellness`, `Agriculture`, `Business & Entrepreneurship`).
6. **Short Benefit Highlight**: Concise, tangible outcome pill highlighting financial assistance and frequency (e.g. `₹6,000/year via direct DBT in 3 installments`, `₹5,00,000 cashless family hospitalisation cover`).
7. **Eligibility Indicator**: Visual status badge (`✔ You are Eligible`, `👨‍👩‍👧 Family Benefit`, `⚠ Check Criteria`, `🌐 Universal Citizen`) paired with criteria summary.
8. **Save Icon (Bookmark)**: Interactive bookmark toggle (★) in the card header allowing citizens to save and unsave schemes locally.

### 4. Interactive Scheme Details Modal
Tapping any scheme card opens a comprehensive detail modal featuring:
- Full overview description
- Key scheme highlights & guarantees
- Required document checklist (Aadhaar, Ration Card, Income Certificate, Land Records, etc.)
- Step-by-step application walkthrough
- Simulated official government portal CTA button (`Visit Official Portal ↗`)

---

## 6. "Ministries & Departments" Browsing Screen (`frontend/MinistriesScreen.kt`)

The **Ministries & Departments** browsing screen offers citizens an administrative directory to discover welfare schemes organized by sponsoring Union Ministry and specific operational departments.

> **UI Demonstration Disclaimer**:
> This screen utilizes realistic mock government portfolio data designed strictly for prototype evaluation. It does NOT claim to represent live government portal records. Implemented **purely in frontend UI** using Jetpack Compose and Material 3 without any backend, external API, or database dependencies.

### 1. Key Screen Features
- **Search Ministry Bar**: Live text filtering matching ministry English names, Hindi names, official acronyms (e.g. `MeitY`, `MoA&FW`, `MoHUA`, `MoRD`), department titles, and flagship schemes.
- **Quick Domain Category Filter Chips**: Instant sector filters (`All Ministries`, `🌾 Rural & Agri`, `🏥 Health & Social`, `💼 Finance & MSME`, `🎓 Education & Skills`).
- **Aggregate Directory Statistics**: Displays total active portfolios (15 Union Ministries), count of administrative departments (32+ departments), and total sponsoring welfare volume (320+ schemes).
- **Reusable Scheme List Integration**: When any ministry card is tapped, it opens the reusable `SchemeListScreen` pre-filtered to that ministry's programs, retaining the full filtering, search, and sorting capabilities.

### 2. Ministry Card Elements
Every ministry card displays:
1. **Ministry Official Name**: Bilingual title (English + localized Hindi).
2. **Ministry Acronym & Official Badge**: e.g., `MoA&FW`, `MoF`, `MoHFW`, `MoE`.
3. **Ministry Icon & Themed Color Tone**: Accessible vector icons with matching soft background tints and colored border strokes.
4. **Scheme Count Placeholder Badge**: e.g., `28 Schemes`, `22 Schemes`, `26 Schemes`.
5. **Department Name Section**: Explicitly lists the constituent administrative departments under the ministry (e.g., `Department of Agriculture and Farmers Welfare (DA&FW)`, `Department of Agricultural Research and Education (DARE)`).
6. **Flagship Initiative Pills**: Sample recognized programs sponsored by the ministry (e.g., *PM-KISAN*, *PM Fasal Bima*, *Ayushman Bharat*, *Mudra Yojana*).
7. **Action Triggers**:
   - `Dept Info ℹ`: Opens deep-dive inspection dialog showing department roles and mandates.
   - `Open Scheme List →`: Immediately launches the reusable `SchemeListScreen` for this portfolio.

---

## 7. "Find Schemes For Me" Screen (`frontend/FindSchemesForMeScreen.kt`)

The **Find Schemes For Me** screen is a personalized entitlement engine that evaluates a citizen's profile attributes to calculate matching Central and State welfare programs in real-time.

> **UI Demonstration Disclaimer**:
> This screen utilizes realistic mock eligibility matching rules designed strictly for prototype evaluation. It does NOT claim to represent live government portal records. Implemented **purely in frontend UI** using Jetpack Compose and Material 3 without any backend, external API, or database dependencies.

### 1. The 4 Mandated Sections
1. **Your Profile**:
   - Displays citizen traits as **selectable/filterable chips** (Age, Gender, State, Residence, Landholding, Income bracket, Social Category, Employment).
   - Citizens can interactively tap chips on/off to immediately observe how the matching algorithm adapts.
   - Includes a quick trigger to `Edit Profile ✎` navigating to the full 12-section profile assessment.
2. **Matching Criteria**:
   - Shows active applied rules count (e.g. `8 Active Rules • 100% Profile Match Score`).
   - Algorithmic explanation callouts (e.g. "Includes Central + Maharashtra State schemes", "Targeting Female Landholder benefits", "Income ceiling < ₹2.5L").
   - Quick preset shortcuts: `Select All`, `Farmer Focus`, `Women Focus`.
3. **Schemes You May Be Eligible For**:
   - Ranked list of matched schemes based on currently active traits.
   - Match percentage badges (`100% Match`, `95% Match`, `90% Match`).
   - Dynamic count of verified recommendations.
4. **View All**:
   - `Expand All Results` toggle allowing citizens to inspect lower-ranked and universal welfare schemes.
   - `Master Catalog →` button routing to the consolidated directory (`AllSchemesScreen`).

### 2. Scheme Result Card Anatomy
Every recommended scheme card displays:
1. **Scheme Name**: Bilingual title in English and regional Hindi.
2. **Why It May Match**: Dedicated callout box detailing the matching traits (e.g., *"Matches your Small & Marginal Farmer status + Rural Maharashtra residence"*).
3. **Government Level**: High-contrast jurisdiction badge (`Central Government`, `State Government`, `Union Territory`).
4. **Benefit Highlight**: Concrete entitlement figure and schedule (e.g. *"₹6,000/year via direct DBT in 3 equal installments"*, *"₹1,500/month direct cash transfer"*).
5. **Eligibility Status**: Status badge (`✔ Highly Eligible`, `👨‍👩‍👧 Family Benefit`, `⚠ Check Criteria`, `🌐 Universal Citizen`).
6. **Save Icon**: Interactive bookmark button (★) toggling local save state.
7. **Action CTA**: `View Details & Apply →` opening a comprehensive modal with requirements and simulated official portal gateway.

---

## 8. Complete Government Scheme Details Screen (`frontend/SchemeDetailsScreen.kt`)

The **Scheme Details** screen is a dedicated single-window citizen portal presenting an exhaustive breakdown of any Central, State, or UT welfare initiative across **all 17 mandated sections**:

> **UI Demonstration Disclaimer**:
> This screen utilizes realistic mock policy data, entitlement rules, and application guides designed strictly for frontend prototype evaluation. It does NOT claim to represent live government portal records. Implemented **purely in frontend UI** using Jetpack Compose and Material 3 without any backend, external API, or database dependencies.

### The 17 Mandated Sections
1. **Scheme Name**: Bilingual title in English and regional Hindi (e.g. *Pradhan Mantri Kisan Samman Nidhi (PM-KISAN)* / *प्रधानमंत्री किसान सम्मान निधि*).
2. **Central / State / UT**: Color-coded jurisdiction badge with official iconography (`Central Government`, `State Government`, `Union Territory`).
3. **State**: Residency and domicile applicability indicator (e.g., `National (All 36 States & UTs)`, `Maharashtra Resident`, `Delhi Resident`).
4. **Ministry / Department**: Complete hierarchy of issuing government authorities (e.g., *Ministry of Agriculture & Farmers Welfare* + *Department of Agriculture and Farmers Welfare (DA&FW)*).
5. **Category**: Themed domain pill with dedicated vector icon and high-contrast color styling (e.g. *Agriculture, Rural & Environment*, *Women & Child*, *Health & Wellness*).
6. **About (Expandable)**: In-depth policy background, objectives, and legislative history with animated expand/collapse accordion.
7. **Benefits (Expandable)**: Highlight entitlement banner with specific monthly/annual cash grant amounts, DBT disbursal schedules, and itemized bullet breakdown.
8. **Eligibility (Expandable)**: Structured dual breakdown distinguishing:
   - *Who is Eligible*: Qualified citizen profiles, landholding limits, age brackets, and income ceilings.
   - *Who is Not Eligible (Exclusions)*: Clear exclusion criteria (e.g. income tax payees, constitutional post holders, institutional owners).
9. **Required Documents (Expandable)**: Visual checklist with document icons (Aadhaar Card, 7/12 Land Extract, Bank Passbook, Domicile Certificate, Ration Card).
10. **How to Apply (Expandable)**: Sequential, numbered step-by-step application walkthrough from portal registration to district approval.
11. **Application Mode (Expandable)**: Omnichannel badges covering Online Web Portal, Village Common Service Centre (CSC) Assisted Counter, Mobile App (Face Authentication), and Bank branches.
12. **Important Dates (Expandable)**: Launch year, annual installment release cycles (April-July, August-November, December-March), and next disbursement schedules.
13. **FAQs (Expandable Accordion)**: Interactive Q&A accordion resolving common citizen queries (e.g. mandatory e-KYC rules, family land limits, checking payment status).
14. **Helpline (Expandable)**: Direct toll-free numbers (`155261`, `14555`), official support email, and CPGRAMS central citizen grievance redressal links.
15. **Official Source (Expandable)**: Verified government portal URL with `Verified ↗` security badge and official gazette citations.
16. **Save Scheme**: Interactive bookmark button in the top action bar and sticky bottom bar, toggling local state with instant visual feedback.
17. **Apply Now**: 50dp high-contrast sticky bottom action bar with `Apply Now →` button, launching a simulated official NIC encrypted gateway confirmation modal.

### Key Interactive Features
- **Quick Scheme Switcher**: Top horizontal scrollable pills to immediately preview different scheme types (Central Agriculture, State Women Empowerment, National Health Assurance).
- **Expandable Accordion Cards**: `ExpandableDetailsSection` with smooth Compose `animateContentSize()` and chevron rotation.
- **Accessible Design**: Full integration with `AccessibilityBar` (font scale magnification and instant multilingual toggle).
- **Zero Backend**: All state managed locally via Compose reactive state holders (`remember { mutableStateOf(...) }`).

---

## 9. Complete Citizen Documents Section Screen (`frontend/DocumentsScreen.kt`)

The **Documents Section** screen is a dedicated digital credential vault and qualification checklist providing citizens with complete visibility over their identity, financial, and residency records.

> **UI Demonstration Disclaimer**:
> This screen operates **purely in frontend UI** using Jetpack Compose Material 3 and local reactive mock state. It does NOT upload, store, or communicate with real cloud file systems, external APIs, or databases.

### Core Sections & Architecture
1. **My Documents**:
   - Master digital credential vault with instant simulated **DigiLocker Bulk Sync ⚡** action.
   - Dynamic document cards with masked credential numbers, issuing authority names, issue dates, and active verification source badges.
2. **Required Documents**:
   - Dedicated filter tab and visual badge distinguishing mandatory government qualification documents (*Aadhaar Card*, *Income Certificate*, *Domicile Certificate*, *7/12 Land Record*, *Ration Card*, *Bank Passbook*) from supplementary records (*UDID Disability Card*, *MGNREGA Job Card*, *Educational Marksheet*).
3. **Ready & Missing Status Filters**:
   - **Ready**: Highlighted in green (`✔ Ready • Verified`) with verification timestamps and scheme usage counts.
   - **Missing**: Highlighted in amber/red (`⚠ Missing • Action Required`) with explicit explanations of why the document is needed and which department issues it.
   - Real-time statistics card showing:
     - Document Readiness Score progress bar (e.g. `70% Complete`).
     - Interactive counter chips (`7 Ready`, `3 Missing`, `6 Required`).
4. **Interactive Document Card Actions**:
   - **View Button (`View 👁`)**:
     - For **Ready** documents: Launches a simulated verified digital certificate sheet showing document serial numbers, issuing authority stamps, issue/validity dates, and scheme eligibility impact.
     - For **Missing** documents: Displays an informational guide on why the document is required and direct links to simulate adding it.
   - **Add Button (`+ Add`)**:
     - Appears on Missing documents.
     - Opens an omnichannel add sheet providing simulated options:
       1. `⚡ Fetch from DigiLocker` (1-click automated retrieval)
       2. `📁 Upload from Device` (Simulated file picker)
       3. `📷 Scan Physical Document` (Simulated camera OCR scan)
     - Selecting an option immediately flips the local mock state from **Missing** to **Ready**, updates the Readiness Score, and displays a confirmation notification.
   - **Remove Action**:
     - Allows toggling a Ready document back to Missing to facilitate bidirectional testing of the user interface.

---

## 10. Complete My Applications Screen (`frontend/MyApplicationsScreen.kt`)

The **My Applications** screen is a dedicated citizen lifecycle tracker providing real-time visibility into welfare applications across **all 7 mandated tabs**:

> **UI Demonstration Disclaimer**:
> This screen operates **purely in frontend UI** using Jetpack Compose Material 3 and local reactive mock state. It does NOT submit live applications or communicate with external government servers, databases, or APIs.

### The 7 Mandated Tabs
1. **All (`सभी`)**: Consolidated view of all active, draft, and completed citizen submissions.
2. **Draft (`प्रारूप`)**: Incomplete applications saved locally awaiting pending document uploads or fees.
3. **Applied (`आवेदित`)**: Freshly submitted applications queued for initial clerical or digital intake.
4. **Under Process (`प्रक्रियाधीन`)**: Active applications progressing through block, taluka, or district officer scrutiny (e.g. land survey verification, field inspection).
5. **Approved (`स्वीकृत`)**: Sanctioned applications where formal entitlement or identity card has been generated.
6. **Rejected (`अस्वीकृत`)**: Applications disqualified due to income ceiling, existing benefits, or incomplete documentation, showing specific nodal rejection remarks and appeal rights.
7. **Completed (`पूर्ण`)**: Finalized schemes with recurring or lump-sum Direct Benefit Transfer (DBT) disbursements successfully active.

### Application Card Anatomy
Each application card in the list displays:
- **Scheme Name**: Bilingual title in English and regional Hindi (e.g. *Pradhan Mantri Kisan Samman Nidhi (PM-KISAN)* / *प्रधानमंत्री किसान सम्मान निधि*).
- **Application Date**: Formatted date of original submission (e.g., `Applied: 12-Jul-2024`).
- **Reference Number Placeholder**: High-visibility reference string (e.g. `Ref: APP-PMK-2024-89210`) with a 1-touch copy action.
- **Status Badge**: Color-coded high-contrast pill with dedicated icons for each state (`Draft`, `Applied`, `Under Process`, `Approved`, `Rejected`, `Completed`).
- **Last Updated**: Real-time progress milestone snippet (e.g., `Last updated: 2 days ago • District Sanction in progress`).
- **View Details Button**: Prominent action button opening a comprehensive modal showing:
  - Multi-stage visual progress stepper (Stage 1 to 4).
  - Official nodal officer remarks and inspection notes.
  - Financial benefit amounts and upcoming DBT disbursal calendar schedules.
  - Sponsoring ministry contacts and acknowledgement slip actions.

---

## 11. Global Government Scheme Search Screen (`frontend/SchemeSearchScreen.kt`)

The **Global Government Scheme Search** screen provides a single-window search engine across India's welfare ecosystem. It supports deep queries across **6 search dimensions**:

> **UI Demonstration Disclaimer**:
> This screen operates **purely in frontend UI** using Jetpack Compose Material 3 and local reactive mock state. It does NOT communicate with external government servers, databases, or APIs.

### The 6 Core Search Dimensions
1. **Scheme Name**: Full names, localized Hindi titles, and common acronyms (e.g. `PM-KISAN`, `Ayushman Bharat`, `Ladki Bahin`, `PMAY`, `Mudra`, `SSY`, `Vishwakarma`).
2. **Category**: Comprehensive welfare domains (e.g. `Agriculture`, `Health & Wellness`, `Women & Child Development`, `Education & Learning`, `Housing & Shelter`, `Skills & Employment`, `Business & Entrepreneurship`, `Transport & Travel`).
3. **Ministry**: Issuing central and state authorities (e.g. `Ministry of Agriculture`, `National Health Authority`, `Ministry of Finance`, `Ministry of Rural Development`, `Department of Women & Child Development`, `Transport Department`).
4. **State / UT**: Multi-level jurisdictions (`National (All States & UTs)`, `Maharashtra`, `NCT of Delhi`, `Tamil Nadu`, `Bihar`, `Karnataka`, `Uttar Pradesh`, `Jammu & Kashmir`, `Puducherry`).
5. **Benefit**: Direct financial assistance amounts and modalities (`₹6,000/year DBT`, `₹1,500 monthly`, `₹5,00,000 cashless cover`, `₹10 Lakh loan`, `₹15,000 toolkit`, `Free bus pink ticket`).
6. **Eligibility Keyword**: Beneficiary demographic and criteria terms (`Farmer`, `Women`, `Girl Child`, `Student`, `Artisan`, `Senior Citizen 70+`, `Street Vendor`, `BPL`, `Rural`, `Landholder`).

### Key UI Features
- **Recent Searches**: Removable history chips with clock icons, individual delete actions, and a 1-tap "Clear All" action.
- **Suggested & Trending Searches**: Curated pills highlighting high-volume welfare searches with thematic emojis and dimension indicators.
- **Multi-criteria Filters**: Bottom sheet modal allowing simultaneous filtering by:
  - Government Level (`All`, `Central Govt`, `State Govt`, `Union Territory`)
  - Benefit Assistance Type (`Cash / DBT`, `Health Cover`, `Enterprise Loan`, `Housing Grant`, `Subsidy / In-Kind`, `Free Travel`)
  - State / UT Jurisdiction (`National`, `Maharashtra`, `Delhi`, `Tamil Nadu`, `Bihar`, etc.)
  - Target Beneficiary Group (`Farmers`, `Women`, `Students`, `Artisans`, `Senior Citizens`, `Rural Families`)
  - Welfare Category (`Agriculture`, `Health`, `Women & Child`, `Education`, `Housing`, etc.)
- **Clear Filters Action**: Quick 1-tap reset button in both the filter drawer and directly above the results list when filters are active.
- **No Results Screen**: High-accessibility zero-state card with friendly graphics, specific query explanation, search tips, popular suggestions, and a prominent "Clear Search & All Filters" action.
- **Scheme Result Cards**: Informative cards showing:
  - Bilingual scheme title (English + Hindi)
  - Category pill with dedicated icon
  - Government level badge (`Central`, `State`, `UT`) and state name
  - Issuing ministry/department name
  - Highlighted benefit box with green rupee badge
  - Eligibility target group pill and summary snippet
  - Matched search attributes badges (`✔ Scheme Name`, `✔ Ministry`, `✔ Benefit`, `✔ Category`, `✔ Eligibility`)
  - Interactive save bookmark toggle
  - "View Details →" action opening comprehensive scheme preview dialog
- **Accessibility Controls**: Integrated language switcher (English / Hindi), font scaling toggle (A / A+), and simulated voice search mock.

---

## 12. Saved Schemes Screen (`frontend/SavedSchemesScreen.kt`)

The **Saved Schemes** screen provides citizens with a dedicated personal bookmarks watchlist for welfare programs.

> **UI Demonstration Disclaimer**:
> This screen operates **purely in frontend UI** using Jetpack Compose Material 3 and local in-memory/mock state. It does NOT write to a local database, SQLite, Room, Firebase, or external government servers.

### Core Capabilities
- **Saved Schemes List**: Displays all bookmarked welfare programs with:
  - Bilingual Scheme Title (English + Hindi)
  - Category Badge with domain icon and theme color
  - Government Level badge (`Central Govt`, `State Govt`, `Union Territory`) and State/UT name
  - Issuing Ministry/Department
  - Direct Welfare Benefit banner with Rupee symbol and disbursement frequency
  - Target demographic group and eligibility criteria snippet
  - Saved timestamp (e.g. `Saved 2 days ago`)
  - Active bookmark toggle with instant visual feedback
  - **"View Details →"** action opening the comprehensive Scheme Details modal
- **Remove Bookmark with Undo**:
  - Tapping the bookmark icon or "Remove" button removes the scheme from the active in-memory list.
  - Triggers an interactive Material 3 Snackbar with an **"Undo"** action to immediately restore removed items.
- **High-Accessibility Empty State**:
  - Displayed when no schemes are saved or all bookmarks have been cleared.
  - Large bookmark illustration with soft amber container.
  - Explanatory text detailing the offline utility and benefit comparison value of saving schemes.
  - **"Explore All Government Schemes"** primary CTA button.
  - **1-Tap Quick Bookmark Suggestions**: Curated list of popular initiatives (`PM Vishwakarma`, `Delhi Free Bus Pink Ticket`) with 1-tap `+ Bookmark` buttons allowing citizens to test adding bookmarks directly from the empty state.
- **Watchlist Search & Category Filter**:
  - Integrated in-screen search bar allowing quick filtering within saved bookmarks.
  - Horizontal category chips (e.g., `All`, `Agriculture`, `Health`, `Women & Child`, `Housing`) for large watchlists.
- **Clear All Watchlist**:
  - Top bar action with confirmation dialog to clear all saved items at once.

---

## Source Locations
- Frontend Source Files:
  - [frontend/SavedSchemesScreen.kt](file:///c:/jansaarthi/frontend/SavedSchemesScreen.kt)
  - [frontend/SchemeSearchScreen.kt](file:///c:/jansaarthi/frontend/SchemeSearchScreen.kt)
  - [frontend/MyApplicationsScreen.kt](file:///c:/jansaarthi/frontend/MyApplicationsScreen.kt)
  - [frontend/DocumentsScreen.kt](file:///c:/jansaarthi/frontend/DocumentsScreen.kt)
  - [frontend/SchemeDetailsScreen.kt](file:///c:/jansaarthi/frontend/SchemeDetailsScreen.kt)
  - [frontend/FindSchemesForMeScreen.kt](file:///c:/jansaarthi/frontend/FindSchemesForMeScreen.kt)
  - [frontend/MinistriesScreen.kt](file:///c:/jansaarthi/frontend/MinistriesScreen.kt)
  - [frontend/AllSchemesScreen.kt](file:///c:/jansaarthi/frontend/AllSchemesScreen.kt)
  - [frontend/SchemeListScreen.kt](file:///c:/jansaarthi/frontend/SchemeListScreen.kt)
  - [frontend/CategoriesScreen.kt](file:///c:/jansaarthi/frontend/CategoriesScreen.kt)
  - [frontend/JanSaarthiHomeScreen.kt](file:///c:/jansaarthi/frontend/JanSaarthiHomeScreen.kt)
  - [frontend/EligibilityProfileScreen.kt](file:///c:/jansaarthi/frontend/EligibilityProfileScreen.kt)
- Android App Integration:
  - [app/src/main/java/org/jansaarthi/app/ui/screens/saved/SavedSchemesScreen.kt](file:///c:/jansaarthi/app/src/main/java/org/jansaarthi/app/ui/screens/saved/SavedSchemesScreen.kt)
  - [app/src/main/java/org/jansaarthi/app/ui/screens/search/SchemeSearchScreen.kt](file:///c:/jansaarthi/app/src/main/java/org/jansaarthi/app/ui/screens/search/SchemeSearchScreen.kt)
  - [app/src/main/java/org/jansaarthi/app/ui/screens/applications/MyApplicationsScreen.kt](file:///c:/jansaarthi/app/src/main/java/org/jansaarthi/app/ui/screens/applications/MyApplicationsScreen.kt)
  - [app/src/main/java/org/jansaarthi/app/ui/screens/documents/DocumentsScreen.kt](file:///c:/jansaarthi/app/src/main/java/org/jansaarthi/app/ui/screens/documents/DocumentsScreen.kt)
  - [app/src/main/java/org/jansaarthi/app/ui/screens/schemes/SchemeDetailsScreen.kt](file:///c:/jansaarthi/app/src/main/java/org/jansaarthi/app/ui/screens/schemes/SchemeDetailsScreen.kt)
  - [app/src/main/java/org/jansaarthi/app/ui/screens/schemes/FindSchemesForMeScreen.kt](file:///c:/jansaarthi/app/src/main/java/org/jansaarthi/app/ui/screens/schemes/FindSchemesForMeScreen.kt)
  - [app/src/main/java/org/jansaarthi/app/ui/screens/ministries/MinistriesScreen.kt](file:///c:/jansaarthi/app/src/main/java/org/jansaarthi/app/ui/screens/ministries/MinistriesScreen.kt)
  - [app/src/main/java/org/jansaarthi/app/ui/screens/schemes/AllSchemesScreen.kt](file:///c:/jansaarthi/app/src/main/java/org/jansaarthi/app/ui/screens/schemes/AllSchemesScreen.kt)
  - [app/src/main/java/org/jansaarthi/app/ui/screens/schemes/SchemeListScreen.kt](file:///c:/jansaarthi/app/src/main/java/org/jansaarthi/app/ui/screens/schemes/SchemeListScreen.kt)
  - [app/src/main/java/org/jansaarthi/app/ui/screens/categories/CategoriesScreen.kt](file:///c:/jansaarthi/app/src/main/java/org/jansaarthi/app/ui/screens/categories/CategoriesScreen.kt)
  - [app/src/main/java/org/jansaarthi/app/ui/screens/profile/ProfileSettingsScreen.kt](file:///c:/jansaarthi/app/src/main/java/org/jansaarthi/app/ui/screens/profile/ProfileSettingsScreen.kt)
  - [app/src/main/java/org/jansaarthi/app/ui/screens/home/JanSaarthiHomeScreen.kt](file:///c:/jansaarthi/app/src/main/java/org/jansaarthi/app/ui/screens/home/JanSaarthiHomeScreen.kt)
  - [app/src/main/java/org/jansaarthi/app/ui/screens/home/CitizenHomeScreen.kt](file:///c:/jansaarthi/app/src/main/java/org/jansaarthi/app/ui/screens/home/CitizenHomeScreen.kt)
  - [app/src/main/java/org/jansaarthi/app/ui/navigation/JanSaarthiNav.kt](file:///c:/jansaarthi/app/src/main/java/org/jansaarthi/app/ui/navigation/JanSaarthiNav.kt)
  - [app/src/main/java/org/jansaarthi/app/MainActivity.kt](file:///c:/jansaarthi/app/src/main/java/org/jansaarthi/app/MainActivity.kt)

---

## 13. Profile & Settings Screen (`frontend/ProfileSettingsScreen.kt`)

The centralized citizen account hub combining identity, preferences, data management, and support.

### Citizen Avatar Card (Hero Section)
- **Initials Avatar**: Auto-generated from citizen's full name, styled on a deep navy gradient background.
- **Name, Mobile & State**: Displayed with icons.
- **Quick Stats Row**: Saved Schemes / Applied / Docs count shown inline within a frosted-glass row.
- **Edit Profile Button**: Navigates to Eligibility Profile screen.
- **Guest Mode**: Displays "Guest User" in place of mobile number.

### Account Section
| Menu Item | Description | Navigation |
|-----------|-------------|------------|
| My Profile | Shows citizen name as subtitle | → Eligibility Profile |
| Eligibility Profile | Update personal details for better scheme matching | → EligibilityProfile Screen |
| State / UT | Shows selected state · district | → StateSelection Screen |

### Preferences Section
| Menu Item | Description | Behaviour |
|-----------|-------------|-----------|
| Language | Shows current language (English / हिन्दी) | Opens language picker dialog |
| Notifications | Toggle with Snackbar feedback | In-memory toggle |
| Accessibility / Easy Mode | Large text + high contrast toggle | Calls `onFontScaleToggle()` |

### My Data Section
| Menu Item | Badge | Navigation |
|-----------|-------|------------|
| Saved Schemes | 4 | → SavedSchemes Screen |
| My Applications | 2 | → MyApplications Screen |
| My Documents | — | → Documents Screen |

### Support Section
| Menu Item | Description |
|-----------|-------------|
| Help & Support | FAQs, grievances, helpline (stub) |
| About JanSaarthi | App info dialog: version, purpose, privacy |

### Logout
- Red destructive row at the bottom.
- Confirmation `AlertDialog` before clearing session via `onLogout()`.

### Technical Notes
- **Navigation**: Accessible from Home Screen top bar (citizen avatar click or profile icon button).
- **Destination**: `JanSaarthiDestination.ProfileSettings` in `JanSaarthiNav.kt`.
- **No Backend**: All toggle states use in-memory `mutableStateOf`; no persistence.
- **Bilingual**: All labels, subtitles, dialogs, and toasts are fully bilingual (EN/HI).






