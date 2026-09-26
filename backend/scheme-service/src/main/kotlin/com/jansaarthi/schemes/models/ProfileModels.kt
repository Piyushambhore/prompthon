package com.jansaarthi.schemes.models

import kotlinx.serialization.Serializable

// ── DigiLocker OAuth ───────────────────────────────────────────────

/** Initiate DigiLocker login — returned to the Android client. */
@Serializable
data class DigiLockerAuthResponse(
    val authorizationUrl: String,
    val state: String,
    val message: String
)

/** Token exchange request after DigiLocker redirect. */
@Serializable
data class DigiLockerCallbackRequest(
    val authorizationCode: String,
    val state: String
)

/** Token exchange response. */
@Serializable
data class DigiLockerTokenResponse(
    val accessToken: String,
    val digiLockerId: String,
    val name: String,
    val message: String
)

// ── Pulled documents & profile ─────────────────────────────────────

/**
 * A single document fetched from DigiLocker.
 * [verified] = true means the document is government-issued and authentic.
 */
@Serializable
data class DigiLockerDocument(
    val documentType: String,          // e.g. "AADHAAR", "PAN", "INCOME_CERT"
    val documentName: String,          // human-readable name
    val issuer: String,                // issuing authority
    val verified: Boolean = true,
    val issuedDate: String? = null,
    val documentUri: String? = null    // DigiLocker URI to view original
)

/**
 * Profile auto-populated from DigiLocker-linked data sources.
 * Each field tracks its [source] so the UI can show verified vs self-declared badges.
 */
@Serializable
data class VerifiedProfile(
    val name: String,
    val age: Int,
    val gender: String? = null,
    val state: String,
    val district: String? = null,
    val category: String? = null,
    val annualIncome: Long? = null,
    val occupation: String? = null,
    val isStudent: Boolean = false,
    val documents: List<DigiLockerDocument>,
    val dataSources: List<DataSourceInfo>
)

/** Tracks where each piece of profile data came from. */
@Serializable
data class DataSourceInfo(
    val field: String,          // e.g. "age", "state", "annualIncome"
    val source: String,         // e.g. "DIGILOCKER_AADHAAR", "PM_KISAN", "SELF_DECLARED"
    val verified: Boolean
)

// ── Farmer-specific ────────────────────────────────────────────────

/** Farmer profile request — used when occupation = farmer. */
@Serializable
data class FarmerProfileRequest(
    val aadhaarLinked: Boolean = false,
    val pmKisanBeneficiary: Boolean? = null,
    val landOwnership: LandInfo? = null,
    val kisanCreditCard: Boolean? = null,
    val state: String,
    val district: String? = null
)

@Serializable
data class LandInfo(
    val hasLand: Boolean,
    val landAreaAcres: Double? = null,
    val landType: String? = null        // irrigated, rainfed, mixed
)

/** Farmer-enriched profile response with scheme recommendations. */
@Serializable
data class FarmerProfileResponse(
    val profile: VerifiedProfile,
    val farmerCategory: String,          // marginal, small, semi-medium, medium, large
    val pmKisanStatus: PmKisanStatus,
    val recommendedSchemeIds: List<String>
)

@Serializable
data class PmKisanStatus(
    val registered: Boolean,
    val beneficiaryStatus: String,       // active, pending_verification, not_registered
    val lastInstallment: String? = null  // "2025-04-01" or null
)

// ── Unified profile input (supports both manual + DigiLocker) ──────

@Serializable
data class ProfileBuildRequest(
    val mode: String = "manual",           // "manual", "digilocker", "farmer"
    // Manual fields (used when mode = "manual")
    val age: Int? = null,
    val state: String? = null,
    val district: String? = null,
    val occupation: String? = null,
    val annualIncome: Long? = null,
    val category: String? = null,
    val isStudent: Boolean = false,
    val availableDocuments: List<String> = emptyList(),
    // DigiLocker fields (used when mode = "digilocker")
    val digiLockerToken: String? = null,
    // Farmer fields (used when mode = "farmer")
    val farmerProfile: FarmerProfileRequest? = null
)

/** Full response: verified profile + eligibility + document readiness. */
@Serializable
data class ProfileEligibilityResponse(
    val profile: VerifiedProfile,
    val mode: String,
    val eligibleSchemeCount: Int,
    val averageDocumentReadiness: Int,
    val message: String
)

/** Comprehensive response: verified profile + full eligibility engine results + document readiness. */
@Serializable
data class FullProfileEligibilityResult(
    val profile: VerifiedProfile,
    val mode: String,
    val eligibility: EligibilityResponse,
    val documentReadiness: List<DocumentReadinessResponse>,
    val verifiedFieldCount: Int,
    val totalFieldCount: Int,
    val message: String
)

