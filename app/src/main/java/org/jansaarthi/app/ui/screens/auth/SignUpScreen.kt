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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import org.jansaarthi.app.ui.screens.dialogs.TermsPrivacyDialog
import org.jansaarthi.app.ui.theme.*

@Composable
fun SignUpScreen(
    viewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit,
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
    val uiState by viewModel.signUpState.collectAsState()

    var showTermsDialog by remember { mutableStateOf(false) }

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
            // Accessibility Bar
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
                    onClick = onNavigateToLogin,
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

            // Main Registration Card
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
                    // Header
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = strings.citizenRegistration,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = GovNavyPrimary,
                                fontSize = (22 * fontSizeMultiplier).sp
                            )
                        )
                        Text(
                            text = strings.signUpSubtitle,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color(0xFF475569),
                                fontSize = (14 * fontSizeMultiplier).sp
                            )
                        )
                    }

                    // Privacy Shield Notice (No sensitive data required)
                    Surface(
                        color = Color(0xFFF0FDF4),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFFBBF7D0))
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = null,
                                tint = GovGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = strings.privacyPledge,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF14532D),
                                    fontSize = (12 * fontSizeMultiplier).sp,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }

                    HorizontalDivider(color = Color(0xFFE2E8F0))

                    // Field 1: Full Name
                    JanSaarthiTextField(
                        value = uiState.fullName,
                        onValueChange = viewModel::onSignUpNameChange,
                        label = strings.fullNameLabel,
                        placeholder = strings.fullNamePlaceholder,
                        leadingIcon = Icons.Default.Person,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        fontSizeMultiplier = fontSizeMultiplier
                    )

                    // Field 2: Mobile Number
                    JanSaarthiTextField(
                        value = uiState.mobileNumber,
                        onValueChange = viewModel::onSignUpMobileChange,
                        label = strings.mobileNumberLabel,
                        placeholder = "98765 43210",
                        leadingIcon = Icons.Default.Phone,
                        prefixText = "+91",
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        fontSizeMultiplier = fontSizeMultiplier
                    )

                    // Field 3: Email (Optional)
                    JanSaarthiTextField(
                        value = uiState.email,
                        onValueChange = viewModel::onSignUpEmailChange,
                        label = strings.emailOptionalLabel,
                        placeholder = "name@example.com",
                        leadingIcon = Icons.Default.AlternateEmail,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        fontSizeMultiplier = fontSizeMultiplier
                    )

                    // Security Setup Selection
                    Text(
                        text = strings.chooseSecurityMethod,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = GovNavyPrimary,
                            fontSize = (15 * fontSizeMultiplier).sp
                        )
                    )

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
                            Surface(
                                onClick = { viewModel.setSignUpSecurityMode(false) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp),
                                shape = RoundedCornerShape(8.dp),
                                color = if (!uiState.isOtpSecurity) Color.White else Color.Transparent,
                                shadowElevation = if (!uiState.isOtpSecurity) 2.dp else 0.dp
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = if (!uiState.isOtpSecurity) GovNavyPrimary else Color(0xFF64748B),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = strings.createPasswordTab,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = if (!uiState.isOtpSecurity) FontWeight.Bold else FontWeight.Medium,
                                            color = if (!uiState.isOtpSecurity) GovNavyPrimary else Color(0xFF64748B),
                                            fontSize = (13 * fontSizeMultiplier).sp
                                        )
                                    )
                                }
                            }

                            Surface(
                                onClick = { viewModel.setSignUpSecurityMode(true) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp),
                                shape = RoundedCornerShape(8.dp),
                                color = if (uiState.isOtpSecurity) Color.White else Color.Transparent,
                                shadowElevation = if (uiState.isOtpSecurity) 2.dp else 0.dp
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Sms,
                                        contentDescription = null,
                                        tint = if (uiState.isOtpSecurity) GovNavyPrimary else Color(0xFF64748B),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = strings.directOtpTab,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = if (uiState.isOtpSecurity) FontWeight.Bold else FontWeight.Medium,
                                            color = if (uiState.isOtpSecurity) GovNavyPrimary else Color(0xFF64748B),
                                            fontSize = (13 * fontSizeMultiplier).sp
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // Security Method 1: Password + Confirm Password
                    if (!uiState.isOtpSecurity) {
                        JanSaarthiPasswordField(
                            value = uiState.password,
                            onValueChange = viewModel::onSignUpPasswordChange,
                            label = strings.passwordLabel,
                            placeholder = strings.passwordPlaceholder,
                            leadingIcon = Icons.Default.Lock,
                            passwordVisible = uiState.isPasswordVisible,
                            onPasswordVisibilityToggle = viewModel::toggleSignUpPasswordVisibility,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            fontSizeMultiplier = fontSizeMultiplier
                        )

                        JanSaarthiPasswordField(
                            value = uiState.confirmPassword,
                            onValueChange = viewModel::onSignUpConfirmPasswordChange,
                            label = strings.confirmPasswordLabel,
                            placeholder = strings.confirmPasswordPlaceholder,
                            leadingIcon = Icons.Default.LockReset,
                            passwordVisible = uiState.isConfirmPasswordVisible,
                            onPasswordVisibilityToggle = viewModel::toggleSignUpConfirmPasswordVisibility,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { focusManager.clearFocus() }
                            ),
                            fontSizeMultiplier = fontSizeMultiplier
                        )
                    }

                    // Security Method 2: OTP Verification
                    if (uiState.isOtpSecurity) {
                        if (!uiState.otpSent) {
                            JanSaarthiPrimaryButton(
                                text = strings.sendOtpButton,
                                leadingIcon = Icons.Default.Sms,
                                isLoading = uiState.isLoading,
                                onClick = viewModel::requestSignUpOtp,
                                fontSizeMultiplier = fontSizeMultiplier
                            )
                        } else {
                            OtpInputField(
                                otpValue = uiState.otpValue,
                                onOtpChange = viewModel::onSignUpOtpChange,
                                onResendOtp = viewModel::requestSignUpOtp,
                                isHindi = isHindi,
                                fontSizeMultiplier = fontSizeMultiplier
                            )
                        }
                    }

                    // Terms and Privacy Checkbox (with large touch target)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.onTermsAcceptedChange(!uiState.termsAccepted) }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = uiState.termsAccepted,
                            onCheckedChange = viewModel::onTermsAcceptedChange,
                            colors = CheckboxDefaults.colors(
                                checkedColor = GovNavyPrimary,
                                uncheckedColor = Color(0xFF64748B)
                            ),
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${strings.termsAgreementPre} ",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color(0xFF334155),
                                        fontSize = (13 * fontSizeMultiplier).sp
                                    )
                                )
                                Text(
                                    text = strings.termsOfService,
                                    modifier = Modifier
                                        .clickable { showTermsDialog = true }
                                        .padding(2.dp),
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = GovNavyPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = (13 * fontSizeMultiplier).sp
                                    )
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${strings.andWord} ",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color(0xFF334155),
                                        fontSize = (13 * fontSizeMultiplier).sp
                                    )
                                )
                                Text(
                                    text = strings.privacyPolicy,
                                    modifier = Modifier
                                        .clickable { showTermsDialog = true }
                                        .padding(2.dp),
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = GovNavyPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = (13 * fontSizeMultiplier).sp
                                    )
                                )
                            }
                        }
                    }

                    // Validation Error Banner
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

                    // Create Account Primary Button
                    JanSaarthiPrimaryButton(
                        text = strings.createAccountButton,
                        leadingIcon = Icons.Default.Check,
                        isLoading = uiState.isLoading,
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.submitSignUp()
                        },
                        fontSizeMultiplier = fontSizeMultiplier
                    )
                }
            }

            // Already Registered -> Login Prompt
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
                        text = "${strings.alreadyRegistered} ",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF334155),
                            fontSize = (15 * fontSizeMultiplier).sp
                        )
                    )
                    Text(
                        text = strings.loginHere,
                        modifier = Modifier
                            .clickable { onNavigateToLogin() }
                            .padding(4.dp),
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = GovSaffron,
                            fontWeight = FontWeight.Bold,
                            fontSize = (15 * fontSizeMultiplier).sp
                        )
                    )
                }
            }

            // Trust footer
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "${strings.citizenHelpdesk}: ${strings.tollFreeNumber}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF475569),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = (12 * fontSizeMultiplier).sp
                    )
                )
                Text(
                    text = strings.dpdpCompliant,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFF94A3B8),
                        fontSize = (11 * fontSizeMultiplier).sp,
                        textAlign = TextAlign.Center
                    )
                )
            }
        }
    }

    // Terms & Privacy Dialog
    if (showTermsDialog) {
        TermsPrivacyDialog(
            onDismiss = { showTermsDialog = false },
            isHindi = isHindi,
            fontSizeMultiplier = fontSizeMultiplier
        )
    }
}
