package com.jansaarthi.schemes.models

import kotlinx.serialization.Serializable

// ── Requests ───────────────────────────────────────────────────────

/** Check document readiness for a single scheme. */
@Serializable
data class DocumentCheckRequest(
    val schemeId: String,
    val availableDocuments: List<String>
)

/** Check document readiness across multiple schemes at once. */
@Serializable
data class BulkDocumentCheckRequest(
    val availableDocuments: List<String>,
    val schemeIds: List<String>
)

// ── Responses ──────────────────────────────────────────────────────

/** Full readiness report for one scheme. */
@Serializable
data class DocumentReadinessResponse(
    val schemeId: String,
    val schemeName: String,
    val totalRequired: Int,
    val totalAvailable: Int,
    val readinessPercentage: Int,
    val documents: List<DocumentStatus>,
    val missingMandatory: List<String>
)

/** Per-document availability status. */
@Serializable
data class DocumentStatus(
    val documentName: String,
    val mandatory: Boolean,
    val available: Boolean
)

/** Simple list of required documents for a scheme. */
@Serializable
data class RequiredDocumentsResponse(
    val schemeId: String,
    val schemeName: String,
    val documents: List<RequiredDocumentItem>
)

@Serializable
data class RequiredDocumentItem(
    val documentName: String,
    val mandatory: Boolean
)
