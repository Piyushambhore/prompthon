package com.jansaarthi.schemes.models

import kotlinx.serialization.Serializable

/** Request body for POST /api/explain */
@Serializable
data class ExplainRequest(
    val text: String
)

/** Response body for POST /api/explain */
@Serializable
data class ExplainResponse(
    val summary: String,
    val reason: String,
    val nextSteps: List<String>,
    val warning: String
)
