package com.jansaarthi.schemes.repositories

import com.jansaarthi.schemes.config.RequiredDocumentsTable
import com.jansaarthi.schemes.config.SchemesTable
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Data-access layer for document requirements.
 *
 * Queries the existing [RequiredDocumentsTable] and [SchemesTable]
 * to provide document information without duplicating SchemeRepository logic.
 */
class DocumentRepository {

    /** Get all required documents for a single scheme. Returns null if scheme doesn't exist. */
    fun getRequiredDocuments(schemeId: String): SchemeDocumentInfo? = transaction {
        findSchemeDocuments(schemeId)
    }

    /** Get required documents for multiple schemes in a single transaction. */
    fun getRequiredDocumentsForSchemes(schemeIds: List<String>): Map<String, SchemeDocumentInfo> = transaction {
        schemeIds.mapNotNull { id ->
            findSchemeDocuments(id)?.let { id to it }
        }.toMap()
    }

    // ── Internal (must be called within a transaction) ─────────────

    private fun findSchemeDocuments(schemeId: String): SchemeDocumentInfo? {
        val scheme = SchemesTable.selectAll()
            .where { SchemesTable.id eq schemeId }
            .singleOrNull() ?: return null

        val docs = RequiredDocumentsTable.selectAll()
            .where { RequiredDocumentsTable.schemeId eq schemeId }
            .map { row ->
                DocumentRequirement(
                    documentName = row[RequiredDocumentsTable.documentName],
                    mandatory    = row[RequiredDocumentsTable.mandatory]
                )
            }

        return SchemeDocumentInfo(
            schemeId   = schemeId,
            schemeName = scheme[SchemesTable.name],
            documents  = docs
        )
    }
}

/** Scheme + its document requirements (used by DocumentService). */
data class SchemeDocumentInfo(
    val schemeId: String,
    val schemeName: String,
    val documents: List<DocumentRequirement>
)
