package com.jansaarthi.schemes.models

import kotlinx.serialization.Serializable

/** Standardised error response returned for 400/404/500 responses. */
@Serializable
data class ErrorResponse(
    val error: String,
    val details: List<String> = emptyList()
)
