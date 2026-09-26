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
     * Robust fuzzy match for document names so variations, abbreviations,
     * and aliases match accurately:
     *   "aadhaar"             matches "aadhaar card"
     *   "land record"         matches "land ownership records"
     *   "7/12 extract"        matches "land ownership records"
     *   "kcc"                 matches "kisan credit card"
     *   "passbook"            matches "bank passbook"
     */
    private fun matches(userDoc: String, requiredDoc: String): Boolean {
        if (userDoc == requiredDoc) return true
        if (requiredDoc.contains(userDoc) || userDoc.contains(requiredDoc)) return true

        // Domain-specific aliases
        if ((userDoc.contains("7/12") || userDoc.contains("land")) && requiredDoc.contains("land")) return true
        if (userDoc.contains("kcc") && requiredDoc.contains("credit")) return true
        if (userDoc.contains("passbook") && requiredDoc.contains("passbook")) return true
        if (userDoc.contains("aadhaar") && requiredDoc.contains("aadhaar")) return true
        if (userDoc.contains("pan") && requiredDoc.contains("pan")) return true

        // Token-level comparison with punctuation stripped
        val cleanUser = userDoc.replace(Regex("[^a-zA-Z0-9 ]"), " ").trim().lowercase()
        val cleanReq  = requiredDoc.replace(Regex("[^a-zA-Z0-9 ]"), " ").trim().lowercase()
        if (cleanReq.contains(cleanUser) || cleanUser.contains(cleanReq)) return true

        val userTokens = cleanUser.split(Regex("\\s+")).filter { it.length > 2 }
        val reqTokens  = cleanReq.split(Regex("\\s+")).filter { it.length > 2 }
        if (userTokens.isEmpty() || reqTokens.isEmpty()) return false

        val matchingTokens = userTokens.count { ut -> reqTokens.any { rt -> rt.contains(ut) || ut.contains(rt) } }
        return matchingTokens >= 1 && (matchingTokens.toDouble() / userTokens.size >= 0.5 || matchingTokens.toDouble() / reqTokens.size >= 0.4)
    }
}
