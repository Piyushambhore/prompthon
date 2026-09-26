package com.jansaarthi.schemes.models

import kotlinx.serialization.Serializable

/** Incoming user profile for eligibility check requests. */
@Serializable
data class UserProfile(
    val age: Int,
    val state: String,
    val district: String? = null,
    val occupation: String,
    val student: Boolean = false,
    val annualIncome: Long,
    val category: String
)
