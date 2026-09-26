package com.jansaarthi.schemes.services

import com.jansaarthi.schemes.models.*
import com.jansaarthi.schemes.repositories.DocumentRepository
import com.jansaarthi.schemes.repositories.SchemeDocumentInfo

/**
 * Document readiness service.
 *
 * Compares a user's available documents against scheme requirements
 * and produces a clear readiness report:
 *
 *   ✓ Aadhaar Card
 *   ✓ Bank Passbook
 *   ✗ Income Certificate
 *   ✗ Caste Certificate
 *
 *   "You are missing 2 mandatory documents."
 *
 * Uses fuzzy matching so "Aadhaar" matches "Aadhaar Card", etc.
 */
class DocumentService(private val documentRepository: DocumentRepository) {

    /** Check document readiness for a single scheme. */
    fun checkReadiness(
        schemeId: String,
        availableDocuments: List<String>
    ): DocumentReadinessResponse? {
        val schemeInfo = documentRepository.getRequiredDocuments(schemeId)
            ?: return null
        return buildReadinessResponse(schemeInfo, availableDocuments)
    }

    /** Check readiness across multiple schemes in one call. */
    fun checkReadinessBulk(
        schemeIds: List<String>,
        availableDocuments: List<String>
    ): List<DocumentReadinessResponse> {
        val schemesInfo = documentRepository.getRequiredDocumentsForSchemes(schemeIds)
        return schemesInfo.values.map { buildReadinessResponse(it, availableDocuments) }
    }

    /** List required documents for a scheme (without readiness check). */
    fun getRequiredDocuments(schemeId: String): RequiredDocumentsResponse? {
        val schemeInfo = documentRepository.getRequiredDocuments(schemeId)
            ?: return null
        return RequiredDocumentsResponse(
            schemeId   = schemeInfo.schemeId,
            schemeName = schemeInfo.schemeName,
            documents  = schemeInfo.documents.map {
                RequiredDocumentItem(
                    documentName = it.documentName,
                    mandatory    = it.mandatory
                )
            }
        )
    }

    // ── Readiness calculation ──────────────────────────────────────

    private fun buildReadinessResponse(
        schemeInfo: SchemeDocumentInfo,
        availableDocuments: List<String>
    ): DocumentReadinessResponse {
        val normalised = availableDocuments.map { normalise(it) }.toSet()

        val statuses = schemeInfo.documents.map { doc ->
            DocumentStatus(
                documentName = doc.documentName,
                mandatory    = doc.mandatory,
                available    = normalised.any { matches(it, normalise(doc.documentName)) }
            )
        }

        val totalRequired  = statuses.size
        val totalAvailable = statuses.count { it.available }
        val readiness      = if (totalRequired > 0) (totalAvailable * 100) / totalRequired else 100

        val missingMandatory = statuses
            .filter { it.mandatory && !it.available }
            .map { it.documentName }

        return DocumentReadinessResponse(
            schemeId            = schemeInfo.schemeId,
            schemeName          = schemeInfo.schemeName,
            totalRequired       = totalRequired,
            totalAvailable      = totalAvailable,
            readinessPercentage = readiness,
            documents           = statuses,
            missingMandatory    = missingMandatory
        )
    }

    // ── Fuzzy document matching ────────────────────────────────────

    private fun normalise(s: String): String = s.lowercase().trim()

    /**
     * Fuzzy match so users don't need to type exact document names.
     *
     *   "aadhaar"             matches "aadhaar card"
     *   "income certificate"  matches "income certificate"
     *   "bank passbook"       matches "bank passbook"
     *   "college cert"        matches "college certificate"
     */
    private fun matches(userDoc: String, requiredDoc: String): Boolean {
        if (userDoc == requiredDoc) return true
        if (requiredDoc.contains(userDoc) || userDoc.contains(requiredDoc)) return true

        // Token-level: all user tokens must appear in the required document name
        val userTokens     = userDoc.split(Regex("[\\s\\-_]+")).filter { it.isNotBlank() }
        val requiredTokens = requiredDoc.split(Regex("[\\s\\-_]+")).filter { it.isNotBlank() }
        return userTokens.all { ut -> requiredTokens.any { rt -> rt.contains(ut) } }
    }
}
