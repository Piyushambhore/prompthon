package org.jansaarthi.app.ui.screens.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jansaarthi.app.data.model.AppLanguage
import org.jansaarthi.app.ui.components.*
import org.jansaarthi.app.ui.localization.getJanSaarthiStrings
import org.jansaarthi.app.ui.theme.*

@Composable
fun ForgotPasswordScreen(
    viewModel: AuthViewModel,
    onNavigateBack: () -> Unit,
    currentLanguage: AppLanguage,
    onSelectLanguage: (AppLanguage) -> Unit,
    isLargeFont: Boolean,
    onFontScaleToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = getJanSaarthiStrings(currentLanguage)
    val isHindi = currentLanguage == AppLanguage.HINDI
    val fontSizeMultiplier = if (isLargeFont) 1.15f else 1.0f
    val uiState by viewModel.forgotPasswordState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GovBackground)
    ) {
        GovernmentBanner(language = currentLanguage, fontSizeMultiplier = fontSizeMultiplier)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AccessibilityBar(
                currentLanguage = currentLanguage,
                onSelectLanguage = onSelectLanguage,
                isLargeFont = isLargeFont,
                onFontScaleToggle = onFontScaleToggle
            )

            // Top Navigation row with back button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = strings.loginHere,
                        tint = GovNavyPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = strings.loginHere,
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = GovNavyPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = (15 * fontSizeMultiplier).sp
                    )
                )
            }

            // Main Reset Card
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            color = GovNavyContainer,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.LockReset,
                                    contentDescription = null,
                                    tint = GovNavyPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = strings.resetPasswordTitle,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GovNavyPrimary,
                                    fontSize = (20 * fontSizeMultiplier).sp
                                )
                            )
                            Text(
                                text = if (isHindi) "चरण ${uiState.step} / 3" else "Step ${uiState.step} of 3",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF64748B),
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }

                    HorizontalDivider(color = Color(0xFFE2E8F0))

                    when (uiState.step) {
                        1 -> {
                            Text(
                                text = strings.resetPasswordSubtitle,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color(0xFF334155),
                                    fontSize = (14 * fontSizeMultiplier).sp
                                )
                            )

                            JanSaarthiTextField(
                                value = uiState.identifier,
                                onValueChange = viewModel::onForgotPasswordIdentifierChange,
                                label = strings.mobileOrEmailLabel,
                                placeholder = strings.mobileOrEmailPlaceholder,
                                leadingIcon = Icons.Default.Phone,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Email,
                                    imeAction = ImeAction.Done
                                ),
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
                                text = strings.sendOtpButton,
                                isLoading = uiState.isLoading,
                                onClick = viewModel::requestPasswordResetOtp,
                                fontSizeMultiplier = fontSizeMultiplier
                            )
                        }

                        2 -> {
                            Text(
                                text = "${uiState.identifier} • ${strings.enterOtpLabel}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color(0xFF334155),
                                    fontSize = (14 * fontSizeMultiplier).sp
                                )
                            )

                            OtpInputField(
                                otpValue = uiState.otpValue,
                                onOtpChange = viewModel::onForgotPasswordOtpChange,
                                onResendOtp = viewModel::requestPasswordResetOtp,
                                isHindi = isHindi,
                                fontSizeMultiplier = fontSizeMultiplier
                            )

                            JanSaarthiPasswordField(
                                value = uiState.newPassword,
                                onValueChange = viewModel::onForgotPasswordNewPasswordChange,
                                label = strings.passwordLabel,
                                placeholder = strings.passwordPlaceholder,
                                leadingIcon = Icons.Default.Lock,
                                passwordVisible = uiState.isPasswordVisible,
                                onPasswordVisibilityToggle = viewModel::toggleForgotPasswordVisibility,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Next
                                ),
                                fontSizeMultiplier = fontSizeMultiplier
                            )

                            JanSaarthiPasswordField(
                                value = uiState.confirmNewPassword,
                                onValueChange = viewModel::onForgotPasswordConfirmPasswordChange,
                                label = strings.confirmPasswordLabel,
                                placeholder = strings.confirmPasswordPlaceholder,
                                leadingIcon = Icons.Default.LockReset,
                                passwordVisible = uiState.isPasswordVisible,
                                onPasswordVisibilityToggle = viewModel::toggleForgotPasswordVisibility,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Done
                                ),
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
                                text = strings.updatePasswordButton,
                                isLoading = uiState.isLoading,
                                onClick = viewModel::submitPasswordReset,
                                fontSizeMultiplier = fontSizeMultiplier
                            )
                        }

                        3 -> {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = GovGreen,
                                    modifier = Modifier.size(60.dp)
                                )
                                Text(
                                    text = strings.resetSuccessTitle,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GovTextPrimary,
                                        fontSize = (18 * fontSizeMultiplier).sp
                                    )
                                )
                                Text(
                                    text = strings.resetSuccessSubtitle,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color(0xFF475569),
                                        fontSize = (14 * fontSizeMultiplier).sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                JanSaarthiPrimaryButton(
                                    text = strings.proceedToLogin,
                                    onClick = {
                                        viewModel.resetForgotPasswordFlow()
                                        onNavigateBack()
                                    },
                                    fontSizeMultiplier = fontSizeMultiplier
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
