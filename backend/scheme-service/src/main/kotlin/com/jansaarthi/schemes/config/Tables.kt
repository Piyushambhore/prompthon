package com.jansaarthi.schemes.config

import org.jetbrains.exposed.sql.Table

/**
 * Exposed table definitions matching the PostgreSQL schema:
 *
 *   schemes              — master scheme data
 *   eligibility_rules    — per-scheme eligibility conditions
 *   required_documents   — documents needed for each scheme
 */

object SchemesTable : Table("schemes") {
    val id              = varchar("id", 20)
    val name            = varchar("name", 500)
    val department      = varchar("department", 500)
    val description     = text("description")
    val benefits        = text("benefits")
    val state           = varchar("state", 100)       // "ALL" for central schemes
    val category        = varchar("category", 100)     // scheme category (education, agriculture …)
    val officialUrl     = varchar("official_url", 1000)
    val officialSource  = varchar("official_source", 1000)
    val lastVerifiedAt  = varchar("last_verified_at", 50)

    override val primaryKey = PrimaryKey(id)
}

object EligibilityRulesTable : Table("eligibility_rules") {
    val id           = integer("id").autoIncrement()
    val schemeId     = varchar("scheme_id", 20).references(SchemesTable.id)
    val ruleField    = varchar("field", 50)            // age, annualIncome, state, occupation, student, category
    val ruleOperator = varchar("operator", 20)         // eq, neq, gte, lte, between, in, bool_eq
    val ruleValue    = varchar("value", 500)

    override val primaryKey = PrimaryKey(id)
}

object RequiredDocumentsTable : Table("required_documents") {
    val id           = integer("id").autoIncrement()
    val schemeId     = varchar("scheme_id", 20).references(SchemesTable.id)
    val documentName = varchar("document_name", 500)
    val mandatory    = bool("mandatory").default(true)

    override val primaryKey = PrimaryKey(id)
}
