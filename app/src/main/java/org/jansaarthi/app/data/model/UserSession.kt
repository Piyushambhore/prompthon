package org.jansaarthi.app.data.model

import java.util.UUID

/**
 * Citizen Profile model.
 * Note: Only non-sensitive basic information is collected.
 * Sensitive data (Aadhaar, PAN, Bank info) is never requested during authentication.
 */
data class CitizenProfile(
    val id: String = UUID.randomUUID().toString(),
    val fullName: String,
    val mobileNumber: String,
    val email: String? = null,
    val selectedState: String? = null,
    val selectedDistrict: String? = null,
    val isGuest: Boolean = false,
    val createdAtEpochMs: Long = System.currentTimeMillis()
) {
    /**
     * Determines whether the initial profile onboarding is complete.
     * Government welfare services require at minimum the resident state/domicile.
     */
    val isProfileSetupComplete: Boolean
        get() = !selectedState.isNullOrBlank()
}

/**
 * Authenticated Session containing tokens and citizen profile.
 * Ready for Firebase Authentication (IdToken, RefreshToken) or REST API (Bearer token).
 */
data class AuthSession(
    val profile: CitizenProfile,
    val accessToken: String,
    val refreshToken: String? = null,
    val tokenExpiryEpochMs: Long = System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000L), // 7 days
    val isGuest: Boolean = false
) {
    val isExpired: Boolean
        get() = !isGuest && System.currentTimeMillis() > tokenExpiryEpochMs
}

/**
 * Authentication result states for view models and clean UI handling
 */
sealed interface AuthResult<out T> {
    data class Success<T>(val data: T) : AuthResult<T>
    data class Error(val message: String, val code: String? = null) : AuthResult<Nothing>
    data object Loading : AuthResult<Nothing>
}

/**
 * OTP Dispatch details for backend verification handshake
 */
data class OtpChallenge(
    val challengeId: String = UUID.randomUUID().toString(),
    val destination: String,
    val expiresInSeconds: Int = 120,
    val verificationType: OtpType = OtpType.SMS
)

enum class OtpType {
    SMS,
    EMAIL
}
