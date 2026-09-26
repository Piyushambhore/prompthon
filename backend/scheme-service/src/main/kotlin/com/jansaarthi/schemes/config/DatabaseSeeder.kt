package com.jansaarthi.schemes.config

import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Seeds the database with a DEMO dataset of well-known Indian government
 * schemes.  This data is for prototype/hackathon purposes only.
 *
 * ⚠️  IMPORTANT — before any public deployment, replace this demo data
 *    with verified information sourced directly from official government
 *    portals and gazette notifications.
 */
object DatabaseSeeder {

    fun seed() {
        transaction {
            if (SchemesTable.selectAll().count() > 0L) return@transaction

            // ── Education schemes ──────────────────────────────────────
            insertScheme(
                id = "SCH001",
                name = "Post-Matric Scholarship for SC Students",
                department = "Ministry of Social Justice and Empowerment",
                description = "Scholarship for Scheduled Caste students pursuing post-matriculation or post-secondary education at recognised institutions.",
                benefits = "Full tuition fee reimbursement, maintenance allowance, and other non-refundable fees as per norms.",
                state = "ALL", category = "education",
                officialUrl = "https://scholarships.gov.in",
                officialSource = "National Scholarship Portal",
                lastVerifiedAt = "2025-01-15",
                rules = listOf(
                    Triple("student", "bool_eq", "true"),
                    Triple("category", "in", "SC"),
                    Triple("annualIncome", "lte", "250000")
                ),
                documents = listOf(
                    "Aadhaar Card" to true,
                    "Income Certificate" to true,
                    "Caste Certificate" to true,
                    "Previous Year Marksheet" to true,
                    "Bank Passbook" to true,
                    "Passport Size Photograph" to true
                )
            )

            insertScheme(
                id = "SCH002",
                name = "Pre-Matric Scholarship for SC Students",
                department = "Ministry of Social Justice and Empowerment",
                description = "Financial assistance for Scheduled Caste students studying in classes 9 and 10.",
                benefits = "Monthly scholarship amount and annual ad-hoc grant for books and stationery.",
                state = "ALL", category = "education",
                officialUrl = "https://scholarships.gov.in",
                officialSource = "National Scholarship Portal",
                lastVerifiedAt = "2025-01-15",
                rules = listOf(
                    Triple("student", "bool_eq", "true"),
                    Triple("category", "in", "SC"),
                    Triple("annualIncome", "lte", "250000"),
                    Triple("age", "lte", "18")
                ),
                documents = listOf(
                    "Aadhaar Card" to true,
                    "Income Certificate" to true,
                    "Caste Certificate" to true,
                    "School ID Card" to true,
                    "Bank Passbook" to true
                )
            )

            insertScheme(
                id = "SCH003",
                name = "Central Sector Scholarship for College and University Students",
                department = "Ministry of Education",
                description = "Merit-based scholarship for economically weaker students who scored above 80th percentile in Class XII board exams.",
                benefits = "Annual scholarship of ₹10,000 for graduation and ₹20,000 for post-graduation courses.",
                state = "ALL", category = "education",
                officialUrl = "https://scholarships.gov.in",
                officialSource = "National Scholarship Portal",
                lastVerifiedAt = "2025-01-15",
                rules = listOf(
                    Triple("student", "bool_eq", "true"),
                    Triple("annualIncome", "lte", "800000")
                ),
                documents = listOf(
                    "Aadhaar Card" to true,
                    "Income Certificate" to true,
                    "Previous Year Marksheet" to true,
                    "College Admission Proof" to true,
                    "Bank Passbook" to true
                )
            )

            insertScheme(
                id = "SCH004",
                name = "Post-Matric Scholarship for OBC Students",
                department = "Ministry of Social Justice and Empowerment",
                description = "Scholarship for Other Backward Classes students pursuing post-matriculation education.",
                benefits = "Tuition fee reimbursement and maintenance allowance as per government norms.",
                state = "ALL", category = "education",
                officialUrl = "https://scholarships.gov.in",
                officialSource = "National Scholarship Portal",
                lastVerifiedAt = "2025-01-15",
                rules = listOf(
                    Triple("student", "bool_eq", "true"),
                    Triple("category", "in", "OBC"),
                    Triple("annualIncome", "lte", "100000")
                ),
                documents = listOf(
                    "Aadhaar Card" to true,
                    "Income Certificate" to true,
                    "OBC Certificate" to true,
                    "Previous Year Marksheet" to true,
                    "Bank Passbook" to true
                )
            )

            // ── Agriculture schemes ────────────────────────────────────
            insertScheme(
                id = "SCH005",
                name = "PM Kisan Samman Nidhi",
                department = "Ministry of Agriculture and Farmers Welfare",
                description = "Direct income support of ₹6,000 per year to eligible small and marginal farmer families.",
                benefits = "₹6,000 per year paid in three equal instalments of ₹2,000 directly to bank accounts.",
                state = "ALL", category = "agriculture",
                officialUrl = "https://pmkisan.gov.in",
                officialSource = "PM-KISAN Portal",
                lastVerifiedAt = "2025-02-01",
                rules = listOf(
                    Triple("occupation", "in", "farmer,agriculture,farming"),
                    Triple("age", "gte", "18")
                ),
                documents = listOf(
                    "Aadhaar Card" to true,
                    "Land Ownership Records" to true,
                    "Bank Passbook" to true
                )
            )

            insertScheme(
                id = "SCH006",
                name = "PM Fasal Bima Yojana",
                department = "Ministry of Agriculture and Farmers Welfare",
                description = "Crop insurance scheme providing financial support to farmers in case of crop loss due to natural calamities, pests, or diseases.",
                benefits = "Insurance coverage and financial support for crop loss at subsidised premium rates.",
                state = "ALL", category = "agriculture",
                officialUrl = "https://pmfby.gov.in",
                officialSource = "PMFBY Portal",
                lastVerifiedAt = "2025-02-01",
                rules = listOf(
                    Triple("occupation", "in", "farmer,agriculture,farming"),
                    Triple("age", "gte", "18")
                ),
                documents = listOf(
                    "Aadhaar Card" to true,
                    "Land Records" to true,
                    "Bank Passbook" to true,
                    "Crop Sowing Certificate" to true
                )
            )

            insertScheme(
                id = "SCH007",
                name = "Kisan Credit Card",
                department = "Ministry of Agriculture and Farmers Welfare",
                description = "Provides farmers with timely access to credit for agricultural and allied activities at concessional interest rates.",
                benefits = "Short-term credit at 4% interest rate (with prompt repayment incentive) for crop cultivation and other agricultural needs.",
                state = "ALL", category = "agriculture",
                officialUrl = "https://pmkisan.gov.in",
                officialSource = "PM-KISAN Portal",
                lastVerifiedAt = "2025-02-01",
                rules = listOf(
                    Triple("occupation", "in", "farmer,agriculture,farming"),
                    Triple("age", "gte", "18")
                ),
                documents = listOf(
                    "Aadhaar Card" to true,
                    "Land Records" to true,
                    "Bank Passbook" to true,
                    "Passport Size Photograph" to true
                )
            )

            // ── Enterprise / Self-employment schemes ───────────────────
            insertScheme(
                id = "SCH008",
                name = "PM Mudra Yojana",
                department = "Ministry of Finance",
                description = "Provides micro-loans up to ₹10 lakh to non-corporate, non-farm small and micro enterprises for business growth.",
                benefits = "Collateral-free loans under three categories — Shishu (up to ₹50,000), Kishore (₹50,001–₹5 lakh), and Tarun (₹5–₹10 lakh).",
                state = "ALL", category = "enterprise",
                officialUrl = "https://www.mudra.org.in",
                officialSource = "MUDRA Portal",
                lastVerifiedAt = "2025-01-20",
                rules = listOf(
                    Triple("age", "gte", "18"),
                    Triple("occupation", "in", "self-employed,business,entrepreneur,shopkeeper,trader")
                ),
                documents = listOf(
                    "Aadhaar Card" to true,
                    "PAN Card" to true,
                    "Business Plan" to true,
                    "Address Proof" to true,
                    "Bank Statements (last 6 months)" to false
                )
            )

            insertScheme(
                id = "SCH009",
                name = "Stand Up India",
                department = "Ministry of Finance",
                description = "Facilitates bank loans between ₹10 lakh and ₹1 crore to SC/ST and women entrepreneurs for setting up greenfield enterprises.",
                benefits = "Composite loan covering term loan and working capital, with up to 25% margin money from eligible schemes.",
                state = "ALL", category = "enterprise",
                officialUrl = "https://www.standupmitra.in",
                officialSource = "Stand Up India Portal",
                lastVerifiedAt = "2025-01-20",
                rules = listOf(
                    Triple("age", "gte", "18"),
                    Triple("category", "in", "SC,ST")
                ),
                documents = listOf(
                    "Aadhaar Card" to true,
                    "PAN Card" to true,
                    "Caste Certificate" to true,
                    "Business Plan" to true,
                    "Address Proof" to true
                )
            )

            insertScheme(
                id = "SCH010",
                name = "PM SVANidhi — Street Vendor's AtmaNirbhar Nidhi",
                department = "Ministry of Housing and Urban Affairs",
                description = "Working capital loan for street vendors to resume livelihood activities that were impacted during the COVID-19 lockdown.",
                benefits = "Initial working capital loan of up to ₹10,000 with 7% interest subsidy and incentive for digital transactions.",
                state = "ALL", category = "enterprise",
                officialUrl = "https://pmsvanidhi.mohua.gov.in",
                officialSource = "PM SVANidhi Portal",
                lastVerifiedAt = "2025-01-20",
                rules = listOf(
                    Triple("occupation", "in", "street vendor,vendor,hawker"),
                    Triple("age", "gte", "18")
                ),
                documents = listOf(
                    "Aadhaar Card" to true,
                    "Vending Certificate or Letter of Recommendation" to true,
                    "Bank Passbook" to true
                )
            )

            insertScheme(
                id = "SCH011",
                name = "Startup India Scheme",
                department = "Department for Promotion of Industry and Internal Trade",
                description = "Supports innovation and startups through tax exemptions, easier compliance, and access to funding via the Fund of Funds.",
                benefits = "Three-year income tax exemption, self-certification for labour and environment laws, and access to government-backed Fund of Funds.",
                state = "ALL", category = "enterprise",
                officialUrl = "https://www.startupindia.gov.in",
                officialSource = "Startup India Portal",
                lastVerifiedAt = "2025-01-20",
                rules = listOf(
                    Triple("age", "gte", "18"),
                    Triple("occupation", "in", "entrepreneur,startup,self-employed,business")
                ),
                documents = listOf(
                    "Aadhaar Card" to true,
                    "PAN Card" to true,
                    "Business Registration Certificate" to true,
                    "DPIIT Recognition Certificate" to false
                )
            )

            insertScheme(
                id = "SCH012",
                name = "PM Vishwakarma Yojana",
                department = "Ministry of Micro, Small and Medium Enterprises",
                description = "End-to-end support for traditional artisans and craftspeople through skills training, modern tools, credit access, and market linkage.",
                benefits = "Skill training stipend, toolkit incentive of up to ₹15,000, and collateral-free credit up to ₹3 lakh at concessional interest.",
                state = "ALL", category = "enterprise",
                officialUrl = "https://pmvishwakarma.gov.in",
                officialSource = "PM Vishwakarma Portal",
                lastVerifiedAt = "2025-01-25",
                rules = listOf(
                    Triple("age", "gte", "18"),
                    Triple("occupation", "in", "artisan,craftsman,carpenter,blacksmith,goldsmith,potter,sculptor,cobbler,tailor,weaver,mason,barber,washerman")
                ),
                documents = listOf(
                    "Aadhaar Card" to true,
                    "Bank Passbook" to true,
                    "PAN Card" to false,
                    "Skill Certificate" to false
                )
            )

            // ── Health scheme ──────────────────────────────────────────
            insertScheme(
                id = "SCH013",
                name = "Ayushman Bharat — Pradhan Mantri Jan Arogya Yojana (PM-JAY)",
                department = "Ministry of Health and Family Welfare",
                description = "Health insurance scheme providing coverage of ₹5 lakh per family per year for secondary and tertiary hospitalisation.",
                benefits = "Cashless and paperless treatment at empanelled hospitals with coverage up to ₹5 lakh per family per year.",
                state = "ALL", category = "health",
                officialUrl = "https://pmjay.gov.in",
                officialSource = "PM-JAY Portal",
                lastVerifiedAt = "2025-02-01",
                rules = listOf(
                    Triple("annualIncome", "lte", "500000")
                ),
                documents = listOf(
                    "Aadhaar Card" to true,
                    "Ration Card" to true,
                    "Income Certificate" to true
                )
            )

            // ── Housing scheme ─────────────────────────────────────────
            insertScheme(
                id = "SCH014",
                name = "PM Awas Yojana — Urban (PMAY-U)",
                department = "Ministry of Housing and Urban Affairs",
                description = "Affordable housing for the urban poor through interest subsidy on home loans under the Credit Linked Subsidy Scheme.",
                benefits = "Interest subsidy of 3%–6.5% on home loans for EWS/LIG/MIG categories for a tenure of 20 years.",
                state = "ALL", category = "housing",
                officialUrl = "https://pmaymis.gov.in",
                officialSource = "PMAY-U MIS Portal",
                lastVerifiedAt = "2025-01-15",
                rules = listOf(
                    Triple("annualIncome", "lte", "1800000"),
                    Triple("age", "gte", "18")
                ),
                documents = listOf(
                    "Aadhaar Card" to true,
                    "Income Certificate" to true,
                    "Address Proof" to true,
                    "Bank Passbook" to true
                )
            )

            // ── Employment / Skill Development ─────────────────────────
            insertScheme(
                id = "SCH015",
                name = "MGNREGA — Mahatma Gandhi National Rural Employment Guarantee Act",
                department = "Ministry of Rural Development",
                description = "Guarantees 100 days of wage employment per year to every rural household whose adult members volunteer to do unskilled manual work.",
                benefits = "Guaranteed 100 days of employment per year with daily wages as per state notification.",
                state = "ALL", category = "employment",
                officialUrl = "https://nrega.nic.in",
                officialSource = "MGNREGA Portal",
                lastVerifiedAt = "2025-02-01",
                rules = listOf(
                    Triple("age", "gte", "18")
                ),
                documents = listOf(
                    "Aadhaar Card" to true,
                    "Bank Passbook" to true,
                    "Passport Size Photograph" to true
                )
            )

            insertScheme(
                id = "SCH016",
                name = "National Apprenticeship Promotion Scheme (NAPS)",
                department = "Ministry of Skill Development and Entrepreneurship",
                description = "Promotes apprenticeship training in establishments by sharing the cost of stipend with employers.",
                benefits = "Government shares 25% of the prescribed stipend (up to ₹1,500 per month) with employers for each apprentice.",
                state = "ALL", category = "skill-development",
                officialUrl = "https://www.apprenticeshipindia.gov.in",
                officialSource = "Apprenticeship India Portal",
                lastVerifiedAt = "2025-01-20",
                rules = listOf(
                    Triple("age", "between", "14,21")
                ),
                documents = listOf(
                    "Aadhaar Card" to true,
                    "Educational Certificates" to true,
                    "Bank Passbook" to true
                )
            )

            // ── Pension scheme ─────────────────────────────────────────
            insertScheme(
                id = "SCH017",
                name = "Atal Pension Yojana",
                department = "Ministry of Finance",
                description = "Guaranteed pension scheme for unorganised sector workers providing fixed monthly pension of ₹1,000–₹5,000 after age 60.",
                benefits = "Fixed monthly pension ranging from ₹1,000 to ₹5,000 based on contribution, with government co-contribution.",
                state = "ALL", category = "pension",
                officialUrl = "https://www.npscra.nsdl.co.in/scheme-details.php",
                officialSource = "NPS Trust / PFRDA",
                lastVerifiedAt = "2025-01-20",
                rules = listOf(
                    Triple("age", "between", "18,40")
                ),
                documents = listOf(
                    "Aadhaar Card" to true,
                    "Bank Account Details" to true,
                    "Mobile Number Proof" to true
                )
            )

            // ── Energy scheme ──────────────────────────────────────────
            insertScheme(
                id = "SCH018",
                name = "PM Ujjwala Yojana",
                department = "Ministry of Petroleum and Natural Gas",
                description = "Provides free LPG connections to women from Below Poverty Line households to replace unclean cooking fuels.",
                benefits = "Free LPG connection with a financial support of ₹1,600 per connection and option for interest-free EMI for stove and first refill.",
                state = "ALL", category = "energy",
                officialUrl = "https://www.pmuy.gov.in",
                officialSource = "PMUY Portal",
                lastVerifiedAt = "2025-01-25",
                rules = listOf(
                    Triple("annualIncome", "lte", "200000"),
                    Triple("age", "gte", "18")
                ),
                documents = listOf(
                    "Aadhaar Card" to true,
                    "BPL Certificate" to true,
                    "Bank Passbook" to true,
                    "Passport Size Photograph" to true
                )
            )

            // ── Financial Inclusion ────────────────────────────────────
            insertScheme(
                id = "SCH019",
                name = "PM Jan Dhan Yojana",
                department = "Ministry of Finance",
                description = "National mission for financial inclusion ensuring access to financial services such as bank accounts, credit, insurance, and pension.",
                benefits = "Zero-balance savings account with RuPay debit card, accidental insurance cover of ₹2 lakh, and overdraft facility up to ₹10,000.",
                state = "ALL", category = "financial-inclusion",
                officialUrl = "https://pmjdy.gov.in",
                officialSource = "PMJDY Portal",
                lastVerifiedAt = "2025-02-01",
                rules = listOf(
                    Triple("age", "gte", "10")
                ),
                documents = listOf(
                    "Aadhaar Card" to true,
                    "Address Proof" to true
                )
            )

            // ── Social Welfare ─────────────────────────────────────────
            insertScheme(
                id = "SCH020",
                name = "National Family Benefit Scheme",
                department = "Ministry of Rural Development",
                description = "Lump-sum grant to bereaved households below the poverty line on the death of the primary breadwinner.",
                benefits = "One-time lump-sum grant of ₹20,000 to the bereaved household.",
                state = "ALL", category = "social-welfare",
                officialUrl = "https://nsap.nic.in",
                officialSource = "National Social Assistance Programme",
                lastVerifiedAt = "2025-01-20",
                rules = listOf(
                    Triple("annualIncome", "lte", "200000"),
                    Triple("age", "gte", "18")
                ),
                documents = listOf(
                    "Aadhaar Card" to true,
                    "Death Certificate of Breadwinner" to true,
                    "Income Certificate" to true,
                    "BPL Certificate" to true,
                    "Bank Passbook" to true
                )
            )

            // ── State-specific: Maharashtra ────────────────────────────
            insertScheme(
                id = "SCH021",
                name = "Rajarshi Chhatrapati Shahu Maharaj Shikshan Shulkh Scholarship (Maharashtra)",
                department = "Directorate of Higher Education, Maharashtra",
                description = "Tuition fee and examination fee scholarship for economically backward students from OBC, SC, ST, and EWS categories in Maharashtra.",
                benefits = "Full tuition and examination fee reimbursement for eligible students at recognised institutions in Maharashtra.",
                state = "Maharashtra", category = "education",
                officialUrl = "https://mahadbt.maharashtra.gov.in",
                officialSource = "MahaDBT Portal",
                lastVerifiedAt = "2025-01-10",
                rules = listOf(
                    Triple("student", "bool_eq", "true"),
                    Triple("state", "eq", "Maharashtra"),
                    Triple("category", "in", "OBC,SC,ST,EWS"),
                    Triple("annualIncome", "lte", "800000")
                ),
                documents = listOf(
                    "Aadhaar Card" to true,
                    "Domicile Certificate" to true,
                    "Income Certificate" to true,
                    "Caste Certificate" to true,
                    "Previous Year Marksheet" to true,
                    "Bank Passbook" to true
                )
            )

            // ── State-specific: Karnataka ──────────────────────────────
            insertScheme(
                id = "SCH022",
                name = "Vidyasiri Scholarship (Karnataka)",
                department = "Department of Backward Classes Welfare, Karnataka",
                description = "Post-matric scholarship for students from backward classes in Karnataka to support higher education pursuits.",
                benefits = "Annual scholarship amount and hostel maintenance charges as per category and course norms.",
                state = "Karnataka", category = "education",
                officialUrl = "https://sw.kar.nic.in",
                officialSource = "Karnataka Social Welfare Department",
                lastVerifiedAt = "2025-01-10",
                rules = listOf(
                    Triple("student", "bool_eq", "true"),
                    Triple("state", "eq", "Karnataka"),
                    Triple("annualIncome", "lte", "250000")
                ),
                documents = listOf(
                    "Aadhaar Card" to true,
                    "Income Certificate" to true,
                    "Previous Year Marksheet" to true,
                    "College Admission Proof" to true,
                    "Bank Passbook" to true
                )
            )
        }
    }

    // ── Helper ─────────────────────────────────────────────────────────
    private fun insertScheme(
        id: String,
        name: String,
        department: String,
        description: String,
        benefits: String,
        state: String,
        category: String,
        officialUrl: String,
        officialSource: String,
        lastVerifiedAt: String,
        rules: List<Triple<String, String, String>>,
        documents: List<Pair<String, Boolean>>
    ) {
        SchemesTable.insert {
            it[SchemesTable.id]             = id
            it[SchemesTable.name]           = name
            it[SchemesTable.department]     = department
            it[SchemesTable.description]    = description
            it[SchemesTable.benefits]       = benefits
            it[SchemesTable.state]          = state
            it[SchemesTable.category]       = category
            it[SchemesTable.officialUrl]    = officialUrl
            it[SchemesTable.officialSource] = officialSource
            it[SchemesTable.lastVerifiedAt] = lastVerifiedAt
        }

        rules.forEach { (field, operator, value) ->
            EligibilityRulesTable.insert {
                it[EligibilityRulesTable.schemeId]     = id
                it[EligibilityRulesTable.ruleField]    = field
                it[EligibilityRulesTable.ruleOperator] = operator
                it[EligibilityRulesTable.ruleValue]    = value
            }
        }

        documents.forEach { (docName, mandatory) ->
            RequiredDocumentsTable.insert {
                it[RequiredDocumentsTable.schemeId]     = id
                it[RequiredDocumentsTable.documentName] = docName
                it[RequiredDocumentsTable.mandatory]    = mandatory
            }
        }
    }
}
