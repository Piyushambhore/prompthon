package org.jansaarthi.app.data.repository

import kotlinx.coroutines.flow.StateFlow
import org.jansaarthi.app.data.model.AuthSession
import org.jansaarthi.app.data.model.OtpChallenge
import org.jansaarthi.app.data.model.OtpType
import org.jansaarthi.app.data.source.AuthRemoteDataSource
import org.jansaarthi.app.data.source.SessionManager

class AuthRepositoryImpl(
    private val remoteDataSource: AuthRemoteDataSource,
    private val sessionManager: SessionManager
) : AuthRepository {

    override val currentSession: StateFlow<AuthSession?> = sessionManager.currentSessionFlow
    override val currentLanguage: StateFlow<org.jansaarthi.app.data.model.AppLanguage> = sessionManager.currentLanguageFlow

    override suspend fun setLanguage(language: org.jansaarthi.app.data.model.AppLanguage) {
        sessionManager.saveLanguage(language)
    }

    override suspend fun loginWithPassword(identifier: String, password: String): Result<AuthSession> {
        val result = remoteDataSource.loginWithPassword(identifier, password)
        result.onSuccess { session ->
            sessionManager.saveSession(session)
        }
        return result
    }

    override suspend fun sendOtp(destination: String, type: OtpType): Result<OtpChallenge> {
        return remoteDataSource.requestOtp(destination, type)
    }

    override suspend fun loginWithOtp(
        challengeId: String,
        otpCode: String,
        destination: String
    ): Result<AuthSession> {
        val result = remoteDataSource.verifyOtpAndLogin(challengeId, otpCode, destination)
        result.onSuccess { session ->
            sessionManager.saveSession(session)
        }
        return result
    }

    override suspend fun signUpCitizen(
        fullName: String,
        mobileNumber: String,
        email: String?,
        password: String?,
        otpCode: String?
    ): Result<AuthSession> {
        val result = remoteDataSource.registerCitizen(fullName, mobileNumber, email, password, otpCode)
        result.onSuccess { session ->
            sessionManager.saveSession(session)
        }
        return result
    }

    override suspend fun requestPasswordReset(identifier: String): Result<OtpChallenge> {
        return remoteDataSource.requestPasswordReset(identifier)
    }

    override suspend fun resetPassword(
        challengeId: String,
        otpCode: String,
        newPassword: String
    ): Result<Unit> {
        return remoteDataSource.resetPassword(challengeId, otpCode, newPassword)
    }

    override suspend fun continueAsGuest(): AuthSession {
        val guestSession = remoteDataSource.createGuestSession()
        sessionManager.saveSession(guestSession)
        return guestSession
    }

    override suspend fun updateSelectedState(stateName: String, districtName: String?) {
        sessionManager.updateSelectedState(stateName, districtName)
    }

    override suspend fun logout() {
        sessionManager.clearSession()
    }
}
