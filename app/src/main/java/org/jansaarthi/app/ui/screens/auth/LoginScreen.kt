package org.jansaarthi.app.ui.screens.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jansaarthi.app.data.model.AppLanguage
import org.jansaarthi.app.ui.components.*
import org.jansaarthi.app.ui.localization.getJanSaarthiStrings
import org.jansaarthi.app.ui.theme.*

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onNavigateToSignUp: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    currentLanguage: AppLanguage,
    onSelectLanguage: (AppLanguage) -> Unit,
    isLargeFont: Boolean,
    onFontScaleToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = getJanSaarthiStrings(currentLanguage)
    val isHindi = currentLanguage == AppLanguage.HINDI
    val fontSizeMultiplier = if (isLargeFont) 1.15f else 1.0f
    val focusManager = LocalFocusManager.current
    val uiState by viewModel.loginState.collectAsState()

    val isMobileInput = uiState.identifier.all { it.isDigit() } && uiState.identifier.isNotEmpty()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GovBackground)
    ) {
        // Government Banner
        GovernmentBanner(language = currentLanguage, fontSizeMultiplier = fontSizeMultiplier)

        // Main Scrollable Area
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Accessibility controls (Language & Text Size)
            AccessibilityBar(
                currentLanguage = currentLanguage,
                onSelectLanguage = onSelectLanguage,
                isLargeFont = isLargeFont,
                onFontScaleToggle = onFontScaleToggle
            )

            // Header Identity & Title
            JanSaarthiHeader(language = currentLanguage, fontSizeMultiplier = fontSizeMultiplier)

            // Main Form Card (Elevated white surface with clean border)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Title
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = strings.citizenLogin,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = GovNavyPrimary,
                                fontSize = (22 * fontSizeMultiplier).sp
                            )
                        )
                        Text(
                            text = strings.loginSubtitle,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color(0xFF475569),
                                fontSize = (14 * fontSizeMultiplier).sp
                            )
                        )
                    }

                    // Segmented Control: Password vs OTP
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp)
                        ) {
                            // Password Tab
                            Surface(
                                onClick = { viewModel.setLoginMode(false) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp),
                                shape = RoundedCornerShape(8.dp),
                                color = if (!uiState.isOtpMode) Color.White else Color.Transparent,
                                shadowElevation = if (!uiState.isOtpMode) 2.dp else 0.dp
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = if (!uiState.isOtpMode) GovNavyPrimary else Color(0xFF64748B),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = strings.withPasswordTab,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = if (!uiState.isOtpMode) FontWeight.Bold else FontWeight.Medium,
                                            color = if (!uiState.isOtpMode) GovNavyPrimary else Color(0xFF64748B),
                                            fontSize = (14 * fontSizeMultiplier).sp
                                        )
                                    )
                                }
                            }

                            // OTP Tab
                            Surface(
                                onClick = { viewModel.setLoginMode(true) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp),
                                shape = RoundedCornerShape(8.dp),
                                color = if (uiState.isOtpMode) Color.White else Color.Transparent,
                                shadowElevation = if (uiState.isOtpMode) 2.dp else 0.dp
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Sms,
                                        contentDescription = null,
                                        tint = if (uiState.isOtpMode) GovNavyPrimary else Color(0xFF64748B),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = strings.withOtpTab,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = if (uiState.isOtpMode) FontWeight.Bold else FontWeight.Medium,
                                            color = if (uiState.isOtpMode) GovNavyPrimary else Color(0xFF64748B),
                                            fontSize = (14 * fontSizeMultiplier).sp
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // Field 1: Mobile Number or Email
                    JanSaarthiTextField(
                        value = uiState.identifier,
                        onValueChange = viewModel::onLoginIdentifierChange,
                        label = strings.mobileOrEmailLabel,
                        placeholder = strings.mobileOrEmailPlaceholder,
                        leadingIcon = if (isMobileInput) Icons.Default.Phone else Icons.Default.AlternateEmail,
                        prefixText = if (isMobileInput && uiState.identifier.length <= 10) "+91" else null,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = if (!uiState.isOtpMode) ImeAction.Next else ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) },
                            onDone = { focusManager.clearFocus() }
                        ),
                        fontSizeMultiplier = fontSizeMultiplier
                    )

                    // Method 1: Password Login
                    if (!uiState.isOtpMode) {
                        JanSaarthiPasswordField(
                            value = uiState.password,
                            onValueChange = viewModel::onLoginPasswordChange,
                            label = strings.passwordLabel,
                            placeholder = strings.passwordPlaceholder,
                            leadingIcon = Icons.Default.Lock,
                            passwordVisible = uiState.isPasswordVisible,
                            onPasswordVisibilityToggle = viewModel::toggleLoginPasswordVisibility,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                    viewModel.submitLogin()
                                }
                            ),
                            fontSizeMultiplier = fontSizeMultiplier
                        )

                        // Forgot Password Link
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = strings.forgotPassword,
                                modifier = Modifier
                                    .clickable { onNavigateToForgotPassword() }
                                    .padding(vertical = 4.dp),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = GovNavyPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = (14 * fontSizeMultiplier).sp
                                )
                            )
                        }

                        // Error Banner if present
                        if (uiState.errorMessage != null) {
                            Surface(
                                color = GovErrorContainer,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = uiState.errorMessage!!,
                                    modifier = Modifier.padding(12.dp),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = GovError,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = (13 * fontSizeMultiplier).sp
                                    )
                                )
                            }
                        }

                        // Submit Login Button
                        JanSaarthiPrimaryButton(
                            text = strings.loginButton,
                            leadingIcon = Icons.AutoMirrored.Filled.ArrowForward,
                            isLoading = uiState.isLoading,
                            onClick = {
                                focusManager.clearFocus()
                                viewModel.submitLogin()
                            },
                            fontSizeMultiplier = fontSizeMultiplier
                        )
                    }

                    // Method 2: OTP Login
                    if (uiState.isOtpMode) {
                        if (!uiState.otpSent) {
                            // Step 1: Request OTP
                            JanSaarthiPrimaryButton(
                                text = strings.sendOtpButton,
                                leadingIcon = Icons.Default.Sms,
                                isLoading = uiState.isLoading,
                                onClick = viewModel::requestLoginOtp,
                                fontSizeMultiplier = fontSizeMultiplier
                            )
                        } else {
                            // Step 2: Enter and verify OTP
                            OtpInputField(
                                otpValue = uiState.otpValue,
                                onOtpChange = viewModel::onLoginOtpChange,
                                onResendOtp = viewModel::requestLoginOtp,
                                isHindi = isHindi,
                                fontSizeMultiplier = fontSizeMultiplier
                            )

                            if (uiState.errorMessage != null) {
                                Surface(
                                    color = GovErrorContainer,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = uiState.errorMessage!!,
                                        modifier = Modifier.padding(12.dp),
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = GovError,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = (13 * fontSizeMultiplier).sp
                                        )
                                    )
                                }
                            }

                            JanSaarthiPrimaryButton(
                                text = strings.verifyAndLoginButton,
                                enabled = uiState.otpValue.length == 6,
                                isLoading = uiState.isLoading,
                                onClick = viewModel::submitLogin,
                                fontSizeMultiplier = fontSizeMultiplier
                            )

                            // Option to change entered mobile
                            TextButton(
                                onClick = { viewModel.setLoginMode(true) },
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Text(
                                    text = strings.changeNumber,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = GovNavyPrimary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }
                    }

                    // Section Divider
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = Color(0xFFCBD5E1)
                        )
                        Text(
                            text = strings.orDivider,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF64748B),
                                fontWeight = FontWeight.Bold,
                                fontSize = (12 * fontSizeMultiplier).sp
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = Color(0xFFCBD5E1)
                        )
                    }

                    // Continue as Guest Button (Accessible with large touch target)
                    JanSaarthiSecondaryButton(
                        text = strings.continueAsGuest,
                        supportingText = strings.continueAsGuestSubtitle,
                        leadingIcon = Icons.Default.Public,
                        onClick = viewModel::continueAsGuest,
                        fontSizeMultiplier = fontSizeMultiplier
                    )
                }
            }

            // Register / Sign Up Navigation Prompt
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${strings.newToJanSaarthi} ",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF334155),
                            fontSize = (15 * fontSizeMultiplier).sp
                        )
                    )
                    Text(
                        text = strings.registerSignUp,
                        modifier = Modifier
                            .clickable { onNavigateToSignUp() }
                            .padding(4.dp),
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = GovSaffron,
                            fontWeight = FontWeight.Bold,
                            fontSize = (15 * fontSizeMultiplier).sp
                        )
                    )
                }
            }

            // Trust & Helpline Footer
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SupportAgent,
                        contentDescription = "Citizen Helpline",
                        tint = GovNavyPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "${strings.citizenHelpdesk}: ${strings.tollFreeNumber}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF475569),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = (12 * fontSizeMultiplier).sp
                        )
                    )
                }
                Text(
                    text = strings.nicAttribution,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFF94A3B8),
                        fontSize = (11 * fontSizeMultiplier).sp,
                        textAlign = TextAlign.Center
                    )
                )
            }
        }
    }
}
