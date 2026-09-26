package org.jansaarthi.app.data.source

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.jansaarthi.app.data.model.AppLanguage
import org.jansaarthi.app.data.model.AuthSession
import org.jansaarthi.app.data.model.CitizenProfile

/**
 * Local Session Manager for persisting authenticated citizen state.
 * Structured to seamlessly upgrade to EncryptedSharedPreferences or Jetpack DataStore.
 */
class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _currentSessionFlow = MutableStateFlow<AuthSession?>(loadSessionFromPrefs())
    val currentSessionFlow: StateFlow<AuthSession?> = _currentSessionFlow.asStateFlow()

    private val _currentLanguageFlow = MutableStateFlow<AppLanguage>(loadLanguageFromPrefs())
    val currentLanguageFlow: StateFlow<AppLanguage> = _currentLanguageFlow.asStateFlow()

    fun saveLanguage(language: AppLanguage) {
        prefs.edit().putString(KEY_LANGUAGE, language.code).apply()
        _currentLanguageFlow.value = language
    }

    fun getLanguage(): AppLanguage {
        return _currentLanguageFlow.value
    }

    private fun loadLanguageFromPrefs(): AppLanguage {
        val code = prefs.getString(KEY_LANGUAGE, AppLanguage.ENGLISH.code)
        return AppLanguage.fromCode(code)
    }

    fun saveSession(session: AuthSession) {
        prefs.edit().apply {
            putString(KEY_USER_ID, session.profile.id)
            putString(KEY_FULL_NAME, session.profile.fullName)
            putString(KEY_MOBILE, session.profile.mobileNumber)
            putString(KEY_EMAIL, session.profile.email)
            putString(KEY_STATE, session.profile.selectedState)
            putString(KEY_DISTRICT, session.profile.selectedDistrict)
            putBoolean(KEY_IS_GUEST, session.isGuest)
            putString(KEY_ACCESS_TOKEN, session.accessToken)
            putString(KEY_REFRESH_TOKEN, session.refreshToken)
            putLong(KEY_EXPIRY_MS, session.tokenExpiryEpochMs)
            apply()
        }
        _currentSessionFlow.value = session
    }

    fun getSession(): AuthSession? {
        return _currentSessionFlow.value
    }

    fun updateSelectedState(stateName: String, districtName: String? = null) {
        val current = _currentSessionFlow.value ?: return
        val updatedProfile = current.profile.copy(
            selectedState = stateName,
            selectedDistrict = districtName
        )
        val updatedSession = current.copy(profile = updatedProfile)

        prefs.edit().apply {
            putString(KEY_STATE, stateName)
            putString(KEY_DISTRICT, districtName)
            apply()
        }
        _currentSessionFlow.value = updatedSession
    }

    fun clearSession() {
        prefs.edit().clear().apply()
        _currentSessionFlow.value = null
    }

    fun isProfileSetupComplete(): Boolean {
        val session = _currentSessionFlow.value ?: return false
        return session.profile.isProfileSetupComplete
    }

    private fun loadSessionFromPrefs(): AuthSession? {
        val userId = prefs.getString(KEY_USER_ID, null) ?: return null
        val token = prefs.getString(KEY_ACCESS_TOKEN, null) ?: return null
        val isGuest = prefs.getBoolean(KEY_IS_GUEST, false)

        val profile = CitizenProfile(
            id = userId,
            fullName = prefs.getString(KEY_FULL_NAME, "") ?: "",
            mobileNumber = prefs.getString(KEY_MOBILE, "") ?: "",
            email = prefs.getString(KEY_EMAIL, null),
            selectedState = prefs.getString(KEY_STATE, null),
            selectedDistrict = prefs.getString(KEY_DISTRICT, null),
            isGuest = isGuest
        )

        return AuthSession(
            profile = profile,
            accessToken = token,
            refreshToken = prefs.getString(KEY_REFRESH_TOKEN, null),
            tokenExpiryEpochMs = prefs.getLong(KEY_EXPIRY_MS, 0L),
            isGuest = isGuest
        )
    }

    companion object {
        private const val PREFS_NAME = "jansaarthi_auth_prefs"
        private const val KEY_USER_ID = "key_user_id"
        private const val KEY_FULL_NAME = "key_full_name"
        private const val KEY_MOBILE = "key_mobile"
        private const val KEY_EMAIL = "key_email"
        private const val KEY_STATE = "key_state"
        private const val KEY_DISTRICT = "key_district"
        private const val KEY_IS_GUEST = "key_is_guest"
        private const val KEY_ACCESS_TOKEN = "key_access_token"
        private const val KEY_REFRESH_TOKEN = "key_refresh_token"
        private const val KEY_EXPIRY_MS = "key_expiry_ms"
        private const val KEY_LANGUAGE = "key_app_language"
    }
}
