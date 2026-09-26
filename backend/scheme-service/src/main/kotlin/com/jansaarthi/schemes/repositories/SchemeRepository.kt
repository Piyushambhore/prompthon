package com.jansaarthi.schemes.repositories

import com.jansaarthi.schemes.config.EligibilityRulesTable
import com.jansaarthi.schemes.config.RequiredDocumentsTable
import com.jansaarthi.schemes.config.SchemesTable
import com.jansaarthi.schemes.models.SchemeDetailResponse
import com.jansaarthi.schemes.models.SchemeListItem
import com.jansaarthi.schemes.models.StatePortalInfo
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Data-access layer for scheme information.
 *
 * Hides the Exposed ORM behind a clean interface so the database
 * implementation can be swapped (e.g. to an in-memory store for
 * tests or a different RDBMS) without changing the service layer.
 */
class SchemeRepository {

    // ── List / filter ──────────────────────────────────────────────
    fun findAll(state: String? = null, category: String? = null): List<SchemeListItem> = transaction {
        val query = SchemesTable.selectAll()

        state?.let { s ->
            query.andWhere {
                (SchemesTable.state eq s) or (SchemesTable.state eq "ALL")
            }
        }

        category?.let { c ->
            query.andWhere { SchemesTable.category eq c }
        }

        query.map { it.toListItem() }
    }

    // ── Single scheme detail ───────────────────────────────────────
    fun findById(schemeId: String): SchemeDetailResponse? = transaction {
        val row = SchemesTable.selectAll()
            .where { SchemesTable.id eq schemeId }
            .singleOrNull() ?: return@transaction null

        val eligibility = EligibilityRulesTable.selectAll()
            .where { EligibilityRulesTable.schemeId eq schemeId }
            .map { describeRule(it) }

        val documents = RequiredDocumentsTable.selectAll()
            .where { RequiredDocumentsTable.schemeId eq schemeId }
            .map { it[RequiredDocumentsTable.documentName] }

        SchemeDetailResponse(
            schemeId         = row[SchemesTable.id],
            schemeName       = row[SchemesTable.name],
            department       = row[SchemesTable.department],
            description      = row[SchemesTable.description],
            benefits         = row[SchemesTable.benefits],
            eligibility      = eligibility,
            requiredDocuments = documents,
            officialUrl      = row[SchemesTable.officialUrl]
        )
    }

    // ── Schemes + rules (used by EligibilityService) ───────────────
    fun findSchemesWithRules(): List<SchemeWithRules> = transaction {
        SchemesTable.selectAll().map { row ->
            val sid = row[SchemesTable.id]

            val rules = EligibilityRulesTable.selectAll()
                .where { EligibilityRulesTable.schemeId eq sid }
                .map { r ->
                    EligibilityRule(
                        field    = r[EligibilityRulesTable.ruleField],
                        operator = r[EligibilityRulesTable.ruleOperator],
                        value    = r[EligibilityRulesTable.ruleValue]
                    )
                }

            val docs = RequiredDocumentsTable.selectAll()
                .where { RequiredDocumentsTable.schemeId eq sid }
                .map { d ->
                    DocumentRequirement(
                        documentName = d[RequiredDocumentsTable.documentName],
                        mandatory    = d[RequiredDocumentsTable.mandatory]
                    )
                }

            SchemeWithRules(
                schemeId          = sid,
                schemeName        = row[SchemesTable.name],
                department        = row[SchemesTable.department],
                state             = row[SchemesTable.state],
                category          = row[SchemesTable.category],
                officialUrl       = row[SchemesTable.officialUrl],
                rules             = rules,
                requiredDocuments = docs
            )
        }
    }

    fun countSchemes(): Long = transaction {
        SchemesTable.selectAll().count()
    }

    // ── Helpers ────────────────────────────────────────────────────

    private fun ResultRow.toListItem() = SchemeListItem(
        schemeId    = this[SchemesTable.id],
        schemeName  = this[SchemesTable.name],
        department  = this[SchemesTable.department],
        description = this[SchemesTable.description],
        category    = this[SchemesTable.category],
        state       = this[SchemesTable.state],
        officialUrl = this[SchemesTable.officialUrl]
    )

    private fun describeRule(row: ResultRow): String {
        val field    = row[EligibilityRulesTable.ruleField]
        val operator = row[EligibilityRulesTable.ruleOperator]
        val value    = row[EligibilityRulesTable.ruleValue]

        return when (field) {
            "age" -> when (operator) {
                "gte"     -> "Applicant must be at least $value years old"
                "lte"     -> "Applicant must be at most $value years old"
                "between" -> {
                    val (lo, hi) = value.split(",").map { it.trim() }
                    "Applicant age must be between $lo and $hi years"
                }
                else      -> "Age requirement: $operator $value"
            }
            "annualIncome" -> when (operator) {
                "lte" -> "Annual family income must not exceed ₹$value"
                "gte" -> "Annual family income must be at least ₹$value"
                else  -> "Income requirement: $operator ₹$value"
            }
            "state" -> when {
                value.equals("ALL", ignoreCase = true) -> "Available across all states and union territories"
                else -> "Applicant must belong to $value"
            }
            "occupation" -> "Applicable occupation(s): ${value.replace(",", ", ")}"
            "student"    -> if (value == "true") "Applicant must be a student" else "Applicant must not be a student"
            "category"   -> when {
                value.equals("ALL", ignoreCase = true) -> "Open to all categories"
                else -> "Applicant must belong to category: ${value.replace(",", ", ")}"
            }
            else -> "$field $operator $value"
        }
    }

    // ── Official State Portals Directory ───────────────────────────
    fun findStatePortals(state: String? = null): List<StatePortalInfo> {
        val allPortals = listOf(
            // Maharashtra
            StatePortalInfo("Maharashtra", "MahaDBT", "https://mahadbt2.maharashtra.gov.in", "Scholarships & DBT Schemes", "Centralised Direct Benefit Transfer portal for education scholarships and agricultural welfare disbursements"),
            StatePortalInfo("Maharashtra", "Aaple Sarkar", "https://aaplesarkar.mahaonline.gov.in", "Citizen Certificates & Services", "Single window delivery of citizen certificates (income, caste, domicile) and social assistance schemes"),
            StatePortalInfo("Maharashtra", "Maharashtra State Govt", "https://maharashtra.gov.in", "Official Government Portal", "Official portal for all state departments, Government Resolutions (GRs), and public notifications"),
            StatePortalInfo("Maharashtra", "MahaBhumi", "https://mahabhumi.gov.in", "Land Records & Revenue", "Digital land record portal providing 7/12 extracts, 8A extracts, and property card title verification"),

            // Karnataka
            StatePortalInfo("Karnataka", "Seva Sindhu", "https://sevasindhu.karnataka.gov.in", "Citizen Services & Welfare", "Integrated delivery portal offering 780+ government services including Gruha Lakshmi and Yuva Nidhi"),
            StatePortalInfo("Karnataka", "Karnataka One", "https://karnatakaone.gov.in", "Citizen Service Centres", "One-stop service center network for utility bills, municipal applications, and government schemes"),
            StatePortalInfo("Karnataka", "Karnataka Govt", "https://karnataka.gov.in", "Official Government Portal", "Official gateway to state departments, legislative gazettes, and policy notifications"),

            // Kerala
            StatePortalInfo("Kerala", "E-Grantz 3.0", "https://egrantz.kerala.gov.in", "Scholarships & Education", "Web-based scholarship distribution portal for SC, ST, OBC, and OEC post-matric students"),
            StatePortalInfo("Kerala", "Akshaya", "https://akshaya.kerala.gov.in", "e-Governance Network", "Grassroots e-governance service center network providing assisted access to all government benefits"),
            StatePortalInfo("Kerala", "Kerala Govt", "https://kerala.gov.in", "Official Government Portal", "Official portal detailing department operations, state welfare programs, and notifications"),

            // Gujarat
            StatePortalInfo("Gujarat", "Digital Gujarat", "https://digitalgujarat.gov.in", "Unified Citizen Portal", "Consolidated single-window for 30+ scholarships including MYSY, citizen certificates, and revenue services"),
            StatePortalInfo("Gujarat", "Gujarat Govt", "https://gujaratindia.gov.in", "Official Government Portal", "Official state portal providing department information, industrial policies, and welfare guidelines"),

            // Telangana
            StatePortalInfo("Telangana", "MeeSeva Telangana", "https://ts.meeseva.telangana.gov.in", "Citizen Services & Schemes", "Comprehensive portal hosting 580+ G2C services, pension disbursements, and certificates"),
            StatePortalInfo("Telangana", "ePASS Telangana", "https://epass.cgg.gov.in", "Scholarships & Fee Reimbursement", "Dedicated post-matric scholarship portal for SC, ST, BC, and minority students"),
            StatePortalInfo("Telangana", "Telangana Govt", "https://telangana.gov.in", "Official Government Portal", "Official state government portal with department details, government orders, and state initiatives"),

            // Haryana
            StatePortalInfo("Haryana", "Antyodaya SARAL", "https://saralharyana.gov.in", "Single-Window Welfare", "Single platform for 500+ welfare schemes and citizen services aligned with Parivar Pehchan Patra (PPP)"),
            StatePortalInfo("Haryana", "Haryana Govt", "https://haryana.gov.in", "Official Government Portal", "Official portal for Haryana government departments, gazette orders, and citizen services")
        )

        return if (state.isNullOrBlank()) {
            allPortals
        } else {
            allPortals.filter { it.state.equals(state.trim(), ignoreCase = true) }
        }
    }
}

// ── Value objects consumed by EligibilityService ────────────────────
data class EligibilityRule(
    val field: String,
    val operator: String,
    val value: String
)

data class DocumentRequirement(
    val documentName: String,
    val mandatory: Boolean
)

data class SchemeWithRules(
    val schemeId: String,
    val schemeName: String,
    val department: String,
    val state: String,
    val category: String,
    val officialUrl: String,
    val rules: List<EligibilityRule>,
    val requiredDocuments: List<DocumentRequirement>
)
