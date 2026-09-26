package org.jansaarthi.app.data.repository

import kotlinx.coroutines.flow.StateFlow
import org.jansaarthi.app.data.model.AuthSession
import org.jansaarthi.app.data.model.OtpChallenge
import org.jansaarthi.app.data.model.OtpType

/**
 * Authentication & Citizen Session Repository contract.
 */
interface AuthRepository {
    val currentSession: StateFlow<AuthSession?>

    val currentLanguage: StateFlow<org.jansaarthi.app.data.model.AppLanguage>

    suspend fun loginWithPassword(identifier: String, password: String): Result<AuthSession>
    suspend fun sendOtp(destination: String, type: OtpType = OtpType.SMS): Result<OtpChallenge>
    suspend fun loginWithOtp(challengeId: String, otpCode: String, destination: String): Result<AuthSession>
    suspend fun signUpCitizen(
        fullName: String,
        mobileNumber: String,
        email: String?,
        password: String?,
        otpCode: String?
    ): Result<AuthSession>
    suspend fun requestPasswordReset(identifier: String): Result<OtpChallenge>
    suspend fun resetPassword(challengeId: String, otpCode: String, newPassword: String): Result<Unit>
    suspend fun continueAsGuest(): AuthSession
    suspend fun updateSelectedState(stateName: String, districtName: String? = null)
    suspend fun setLanguage(language: org.jansaarthi.app.data.model.AppLanguage)
    suspend fun logout()
}
