package org.jansaarthi.app.ui.screens.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import org.jansaarthi.app.ui.components.JanSaarthiPrimaryButton
import org.jansaarthi.app.ui.components.JanSaarthiTextField
import org.jansaarthi.app.ui.components.OtpInputField
import org.jansaarthi.app.ui.theme.GovGreen
import org.jansaarthi.app.ui.theme.GovNavyPrimary
import org.jansaarthi.app.ui.theme.GovTextPrimary

@Composable
fun ForgotPasswordDialog(
    onDismiss: () -> Unit,
    isHindi: Boolean = false,
    fontSizeMultiplier: Float = 1.0f
) {
    var mobileOrEmail by remember { mutableStateOf("") }
    var step by remember { mutableStateOf(1) } // 1: Input mobile, 2: Enter OTP, 3: Success
    var otpValue by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header with close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LockReset,
                            contentDescription = null,
                            tint = GovNavyPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = if (isHindi) "पासवर्ड रीसेट करें" else "Reset Password",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = GovNavyPrimary,
                                fontSize = (19 * fontSizeMultiplier).sp
                            )
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(0xFF64748B)
                        )
                    }
                }

                HorizontalDivider(color = Color(0xFFE2E8F0))

                when (step) {
                    1 -> {
                        Text(
                            text = if (isHindi)
                                "पासवर्ड रीसेट लिंक या OTP प्राप्त करने के लिए अपना पंजीकृत मोबाइल नंबर या ईमेल दर्ज करें।"
                            else
                                "Enter your registered mobile number or email to receive password reset OTP.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color(0xFF334155),
                                fontSize = (14 * fontSizeMultiplier).sp
                            )
                        )

                        JanSaarthiTextField(
                            value = mobileOrEmail,
                            onValueChange = {
                                mobileOrEmail = it
                                errorMessage = null
                            },
                            label = if (isHindi) "पंजीकृत मोबाइल या ईमेल" else "Registered Mobile or Email",
                            placeholder = if (isHindi) "उदा. 9876543210" else "e.g. 9876543210",
                            leadingIcon = Icons.Default.Phone,
                            isError = errorMessage != null,
                            errorMessage = errorMessage,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            fontSizeMultiplier = fontSizeMultiplier
                        )

                        JanSaarthiPrimaryButton(
                            text = if (isHindi) "OTP प्राप्त करें" else "Send Reset OTP",
                            onClick = {
                                if (mobileOrEmail.trim().length < 6) {
                                    errorMessage = if (isHindi) "कृपया मान्य मोबाइल नंबर या ईमेल दर्ज करें" else "Please enter valid mobile or email"
                                } else {
                                    step = 2
                                }
                            },
                            fontSizeMultiplier = fontSizeMultiplier
                        )
                    }

                    2 -> {
                        Text(
                            text = if (isHindi)
                                "$mobileOrEmail पर भेजा गया OTP दर्ज करें:"
                            else
                                "Enter OTP sent to $mobileOrEmail:",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color(0xFF334155),
                                fontSize = (14 * fontSizeMultiplier).sp
                            )
                        )

                        OtpInputField(
                            otpValue = otpValue,
                            onOtpChange = { otpValue = it },
                            onResendOtp = { /* Mock resend */ },
                            isHindi = isHindi,
                            fontSizeMultiplier = fontSizeMultiplier
                        )

                        JanSaarthiPrimaryButton(
                            text = if (isHindi) "सत्यापित करें एवं नया पासवर्ड बनाएं" else "Verify & Set New Password",
                            enabled = otpValue.length == 6,
                            onClick = { step = 3 },
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
                                modifier = Modifier.size(54.dp)
                            )
                            Text(
                                text = if (isHindi) "सत्यापन सफल!" else "Verification Successful!",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GovTextPrimary,
                                    fontSize = (18 * fontSizeMultiplier).sp
                                )
                            )
                            Text(
                                text = if (isHindi)
                                    "पासवर्ड रीसेट लिंक आपके पंजीकृत नंबर पर भेज दिया गया है।"
                                else
                                    "Password reset link has been dispatched to your verified number.",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color(0xFF475569),
                                    fontSize = (14 * fontSizeMultiplier).sp
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            JanSaarthiPrimaryButton(
                                text = if (isHindi) "लॉगिन पर लौटें" else "Return to Login",
                                onClick = onDismiss,
                                fontSizeMultiplier = fontSizeMultiplier
                            )
                        }
                    }
                }
            }
        }
    }
}
