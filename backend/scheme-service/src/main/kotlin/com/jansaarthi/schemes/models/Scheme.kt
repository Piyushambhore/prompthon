package com.jansaarthi.schemes.models

import kotlinx.serialization.Serializable

/** Full scheme details returned by GET /api/schemes/{schemeId} */
@Serializable
data class SchemeDetailResponse(
    val schemeId: String,
    val schemeName: String,
    val department: String,
    val description: String,
    val benefits: String,
    val eligibility: List<String>,
    val requiredDocuments: List<String>,
    val officialUrl: String
)

/** Lightweight scheme summary returned by GET /api/schemes */
@Serializable
data class SchemeListItem(
    val schemeId: String,
    val schemeName: String,
    val department: String,
    val description: String,
    val category: String,
    val state: String,
    val officialUrl: String
)

/** Official state government portal directory */
@Serializable
data class StatePortalInfo(
    val state: String,
    val portalName: String,
    val url: String,
    val category: String,
    val description: String
)

