package com.jansaarthi.schemes.models

import kotlinx.serialization.Serializable

/** Top-level response for the eligibility check endpoint. */
@Serializable
data class EligibilityResponse(
    val schemes: List<EligibilitySchemeResult>
)

/** Per-scheme eligibility result — never claims definite approval. */
@Serializable
data class EligibilitySchemeResult(
    val schemeId: String,
    val schemeName: String,
    val department: String,
    val potentiallyEligible: Boolean,
    val reasons: List<String>,
    val missingDocuments: List<String>,
    val officialUrl: String
)
