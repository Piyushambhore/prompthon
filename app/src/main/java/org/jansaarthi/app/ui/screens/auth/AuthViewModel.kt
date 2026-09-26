package org.jansaarthi.app.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.jansaarthi.app.data.model.AuthSession
import org.jansaarthi.app.data.model.OtpChallenge
import org.jansaarthi.app.data.model.OtpType
import org.jansaarthi.app.data.repository.AuthRepository

sealed interface AuthNavEvent {
    data object NavigateToLogin : AuthNavEvent
    data object NavigateToSignUp : AuthNavEvent
    data object NavigateToForgotPassword : AuthNavEvent
    data object NavigateToStateSelection : AuthNavEvent
    data object NavigateToHome : AuthNavEvent
    data class ShowToast(val message: String) : AuthNavEvent
}

data class LoginUiState(
    val identifier: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isOtpMode: Boolean = false,
    val otpValue: String = "",
    val otpSent: Boolean = false,
    val otpChallenge: OtpChallenge? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

data class SignUpUiState(
    val fullName: String = "",
    val mobileNumber: String = "",
    val email: String = "",
    val isOtpSecurity: Boolean = false,
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val otpValue: String = "",
    val otpSent: Boolean = false,
    val otpChallenge: OtpChallenge? = null,
    val termsAccepted: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

data class ForgotPasswordUiState(
    val identifier: String = "",
    val otpChallenge: OtpChallenge? = null,
    val otpValue: String = "",
    val newPassword: String = "",
    val confirmNewPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val step: Int = 1, // 1: Input Identifier, 2: OTP & New Password, 3: Success
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    val currentSession: StateFlow<AuthSession?> = authRepository.currentSession
    val currentLanguage: StateFlow<org.jansaarthi.app.data.model.AppLanguage> = authRepository.currentLanguage

    fun setLanguage(language: org.jansaarthi.app.data.model.AppLanguage) {
        viewModelScope.launch {
            authRepository.setLanguage(language)
        }
    }

    private val _loginState = MutableStateFlow(LoginUiState())
    val loginState: StateFlow<LoginUiState> = _loginState.asStateFlow()

    private val _signUpState = MutableStateFlow(SignUpUiState())
    val signUpState: StateFlow<SignUpUiState> = _signUpState.asStateFlow()

    private val _forgotPasswordState = MutableStateFlow(ForgotPasswordUiState())
    val forgotPasswordState: StateFlow<ForgotPasswordUiState> = _forgotPasswordState.asStateFlow()

    private val _navEvents = MutableSharedFlow<AuthNavEvent>()
    val navEvents: SharedFlow<AuthNavEvent> = _navEvents.asSharedFlow()

    // ----------------- LOGIN ACTIONS -----------------

    fun onLoginIdentifierChange(value: String) {
        _loginState.value = _loginState.value.copy(identifier = value, errorMessage = null)
    }

    fun onLoginPasswordChange(value: String) {
        _loginState.value = _loginState.value.copy(password = value, errorMessage = null)
    }

    fun toggleLoginPasswordVisibility() {
        _loginState.value = _loginState.value.copy(isPasswordVisible = !_loginState.value.isPasswordVisible)
    }

    fun setLoginMode(isOtp: Boolean) {
        _loginState.value = _loginState.value.copy(
            isOtpMode = isOtp,
            errorMessage = null,
            otpSent = false,
            otpValue = ""
        )
    }

    fun onLoginOtpChange(value: String) {
        _loginState.value = _loginState.value.copy(otpValue = value, errorMessage = null)
    }

    fun requestLoginOtp() {
        val identifier = _loginState.value.identifier.trim()
        if (identifier.isEmpty()) {
            _loginState.value = _loginState.value.copy(errorMessage = "Please enter mobile number or email.")
            return
        }

        viewModelScope.launch {
            _loginState.value = _loginState.value.copy(isLoading = true, errorMessage = null)
            val type = if (identifier.all { it.isDigit() }) OtpType.SMS else OtpType.EMAIL
            authRepository.sendOtp(identifier, type)
                .onSuccess { challenge ->
                    _loginState.value = _loginState.value.copy(
                        isLoading = false,
                        otpSent = true,
                        otpChallenge = challenge
                    )
                }
                .onFailure { error ->
                    _loginState.value = _loginState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to dispatch OTP."
                    )
                }
        }
    }

    fun submitLogin() {
        val state = _loginState.value
        viewModelScope.launch {
            _loginState.value = state.copy(isLoading = true, errorMessage = null)

            val result = if (state.isOtpMode) {
                val challengeId = state.otpChallenge?.challengeId ?: "DEFAULT"
                authRepository.loginWithOtp(challengeId, state.otpValue, state.identifier)
            } else {
                authRepository.loginWithPassword(state.identifier, state.password)
            }

            result.onSuccess { session ->
                _loginState.value = state.copy(isLoading = false)
                handlePostAuthNavigation(session)
            }.onFailure { error ->
                _loginState.value = state.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "Authentication failed. Please verify your details."
                )
            }
        }
    }

    fun continueAsGuest() {
        viewModelScope.launch {
            _loginState.value = _loginState.value.copy(isLoading = true)
            val guestSession = authRepository.continueAsGuest()
            _loginState.value = _loginState.value.copy(isLoading = false)
            handlePostAuthNavigation(guestSession)
        }
    }

    // ----------------- SIGN UP ACTIONS -----------------

    fun onSignUpNameChange(name: String) {
        _signUpState.value = _signUpState.value.copy(fullName = name, errorMessage = null)
    }

    fun onSignUpMobileChange(mobile: String) {
        if (mobile.length <= 10 && mobile.all { it.isDigit() }) {
            _signUpState.value = _signUpState.value.copy(mobileNumber = mobile, errorMessage = null)
        }
    }

    fun onSignUpEmailChange(email: String) {
        _signUpState.value = _signUpState.value.copy(email = email, errorMessage = null)
    }

    fun setSignUpSecurityMode(isOtp: Boolean) {
        _signUpState.value = _signUpState.value.copy(isOtpSecurity = isOtp, errorMessage = null)
    }

    fun onSignUpPasswordChange(pass: String) {
        _signUpState.value = _signUpState.value.copy(password = pass, errorMessage = null)
    }

    fun onSignUpConfirmPasswordChange(confirm: String) {
        _signUpState.value = _signUpState.value.copy(confirmPassword = confirm, errorMessage = null)
    }

    fun toggleSignUpPasswordVisibility() {
        _signUpState.value = _signUpState.value.copy(isPasswordVisible = !_signUpState.value.isPasswordVisible)
    }

    fun toggleSignUpConfirmPasswordVisibility() {
        _signUpState.value = _signUpState.value.copy(isConfirmPasswordVisible = !_signUpState.value.isConfirmPasswordVisible)
    }

    fun onSignUpOtpChange(otp: String) {
        _signUpState.value = _signUpState.value.copy(otpValue = otp, errorMessage = null)
    }

    fun onTermsAcceptedChange(accepted: Boolean) {
        _signUpState.value = _signUpState.value.copy(termsAccepted = accepted, errorMessage = null)
    }

    fun requestSignUpOtp() {
        val mobile = _signUpState.value.mobileNumber.trim()
        if (mobile.length != 10) {
            _signUpState.value = _signUpState.value.copy(errorMessage = "Please enter a valid 10-digit mobile number.")
            return
        }

        viewModelScope.launch {
            _signUpState.value = _signUpState.value.copy(isLoading = true, errorMessage = null)
            authRepository.sendOtp(mobile, OtpType.SMS)
                .onSuccess { challenge ->
                    _signUpState.value = _signUpState.value.copy(
                        isLoading = false,
                        otpSent = true,
                        otpChallenge = challenge
                    )
                }
                .onFailure { error ->
                    _signUpState.value = _signUpState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to send verification OTP."
                    )
                }
        }
    }

    fun submitSignUp() {
        val state = _signUpState.value

        // Client-side validations
        if (state.fullName.trim().length < 3) {
            _signUpState.value = state.copy(errorMessage = "Please enter your full legal name (minimum 3 characters).")
            return
        }
        if (state.mobileNumber.trim().length != 10) {
            _signUpState.value = state.copy(errorMessage = "Please enter a valid 10-digit mobile number.")
            return
        }
        if (!state.isOtpSecurity) {
            if (state.password.length < 6) {
                _signUpState.value = state.copy(errorMessage = "Password must be at least 6 characters.")
                return
            }
            if (state.password != state.confirmPassword) {
                _signUpState.value = state.copy(errorMessage = "Passwords do not match.")
                return
            }
        } else {
            if (!state.otpSent || state.otpValue.length != 6) {
                _signUpState.value = state.copy(errorMessage = "Please enter the 6-digit OTP received.")
                return
            }
        }
        if (!state.termsAccepted) {
            _signUpState.value = state.copy(errorMessage = "Please accept the Terms & Privacy Policy to proceed.")
            return
        }

        viewModelScope.launch {
            _signUpState.value = state.copy(isLoading = true, errorMessage = null)

            val result = authRepository.signUpCitizen(
                fullName = state.fullName,
                mobileNumber = state.mobileNumber,
                email = state.email.ifBlank { null },
                password = if (!state.isOtpSecurity) state.password else null,
                otpCode = if (state.isOtpSecurity) state.otpValue else null
            )

            result.onSuccess { session ->
                _signUpState.value = state.copy(isLoading = false)
                handlePostAuthNavigation(session)
            }.onFailure { error ->
                _signUpState.value = state.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "Registration failed. Please try again."
                )
            }
        }
    }

    // ----------------- FORGOT PASSWORD ACTIONS -----------------

    fun onForgotPasswordIdentifierChange(id: String) {
        _forgotPasswordState.value = _forgotPasswordState.value.copy(identifier = id, errorMessage = null)
    }

    fun onForgotPasswordOtpChange(otp: String) {
        _forgotPasswordState.value = _forgotPasswordState.value.copy(otpValue = otp, errorMessage = null)
    }

    fun onForgotPasswordNewPasswordChange(pass: String) {
        _forgotPasswordState.value = _forgotPasswordState.value.copy(newPassword = pass, errorMessage = null)
    }

    fun onForgotPasswordConfirmPasswordChange(confirm: String) {
        _forgotPasswordState.value = _forgotPasswordState.value.copy(confirmNewPassword = confirm, errorMessage = null)
    }

    fun toggleForgotPasswordVisibility() {
        _forgotPasswordState.value = _forgotPasswordState.value.copy(isPasswordVisible = !_forgotPasswordState.value.isPasswordVisible)
    }

    fun requestPasswordResetOtp() {
        val identifier = _forgotPasswordState.value.identifier.trim()
        if (identifier.length < 6) {
            _forgotPasswordState.value = _forgotPasswordState.value.copy(errorMessage = "Please enter your registered mobile number or email.")
            return
        }

        viewModelScope.launch {
            _forgotPasswordState.value = _forgotPasswordState.value.copy(isLoading = true, errorMessage = null)
            authRepository.requestPasswordReset(identifier)
                .onSuccess { challenge ->
                    _forgotPasswordState.value = _forgotPasswordState.value.copy(
                        isLoading = false,
                        otpChallenge = challenge,
                        step = 2
                    )
                }
                .onFailure { error ->
                    _forgotPasswordState.value = _forgotPasswordState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to generate reset OTP."
                    )
                }
        }
    }

    fun submitPasswordReset() {
        val state = _forgotPasswordState.value
        if (state.otpValue.length != 6) {
            _forgotPasswordState.value = state.copy(errorMessage = "Please enter the complete 6-digit OTP.")
            return
        }
        if (state.newPassword.length < 6) {
            _forgotPasswordState.value = state.copy(errorMessage = "New password must be at least 6 characters.")
            return
        }
        if (state.newPassword != state.confirmNewPassword) {
            _forgotPasswordState.value = state.copy(errorMessage = "Passwords do not match.")
            return
        }

        viewModelScope.launch {
            _forgotPasswordState.value = state.copy(isLoading = true, errorMessage = null)
            val challengeId = state.otpChallenge?.challengeId ?: "DEFAULT"
            authRepository.resetPassword(challengeId, state.otpValue, state.newPassword)
                .onSuccess {
                    _forgotPasswordState.value = state.copy(isLoading = false, step = 3)
                }
                .onFailure { error ->
                    _forgotPasswordState.value = state.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Could not reset password. Please check your OTP."
                    )
                }
        }
    }

    fun resetForgotPasswordFlow() {
        _forgotPasswordState.value = ForgotPasswordUiState()
    }

    // ----------------- STATE SELECTION & ONBOARDING -----------------

    fun selectState(stateName: String, districtName: String? = null) {
        viewModelScope.launch {
            authRepository.updateSelectedState(stateName, districtName)
            _navEvents.emit(AuthNavEvent.NavigateToHome)
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _navEvents.emit(AuthNavEvent.NavigateToLogin)
        }
    }

    /**
     * Requirement: After successful login/signup navigate to State Selection
     * if profile setup is incomplete, otherwise Home.
     */
    private suspend fun handlePostAuthNavigation(session: AuthSession) {
        if (!session.profile.isProfileSetupComplete) {
            _navEvents.emit(AuthNavEvent.NavigateToStateSelection)
        } else {
            _navEvents.emit(AuthNavEvent.NavigateToHome)
        }
    }
}

class AuthViewModelFactory(
    private val authRepository: AuthRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
