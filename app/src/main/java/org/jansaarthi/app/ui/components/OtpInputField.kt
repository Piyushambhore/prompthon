package org.jansaarthi.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.jansaarthi.app.ui.theme.GovNavyPrimary
import org.jansaarthi.app.ui.theme.GovSaffron

/**
 * High-Accessibility 6-Digit OTP Box with Auto-Advance, Resend Timer, and Large Typography
 */
@Composable
fun OtpInputField(
    otpValue: String,
    onOtpChange: (String) -> Unit,
    onResendOtp: () -> Unit,
    modifier: Modifier = Modifier,
    isHindi: Boolean = false,
    fontSizeMultiplier: Float = 1.0f
) {
    var timeLeft by remember { mutableStateOf(45) }
    var canResend by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(timeLeft, canResend) {
        if (!canResend && timeLeft > 0) {
            delay(1000L)
            timeLeft--
        } else if (timeLeft == 0) {
            canResend = true
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Label
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isHindi) "6 अंकों का OTP दर्ज करें" else "Enter 6-digit OTP",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = GovNavyPrimary,
                    fontSize = (15 * fontSizeMultiplier).sp
                )
            )
            Text(
                text = if (isHindi) "एसएमएस द्वारा भेजा गया" else "Sent via SMS",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF64748B),
                    fontSize = (12 * fontSizeMultiplier).sp
                )
            )
        }

        // Invisible BasicTextField overlaying 6 visual boxes
        BasicTextField(
            value = otpValue,
            onValueChange = { newValue ->
                if (newValue.length <= 6 && newValue.all { it.isDigit() }) {
                    onOtpChange(newValue)
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            modifier = Modifier.focusRequester(focusRequester),
            decorationBox = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (i in 0 until 6) {
                        val digit = otpValue.getOrNull(i)?.toString() ?: ""
                        val isCurrent = otpValue.length == i
                        val isFilled = i < otpValue.length

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .padding(horizontal = 3.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    when {
                                        isCurrent -> Color(0xFFF0F7FF)
                                        isFilled -> Color(0xFFFAFAFA)
                                        else -> Color.White
                                    }
                                )
                                .border(
                                    width = if (isCurrent) 2.dp else 1.2.dp,
                                    color = when {
                                        isCurrent -> GovNavyPrimary
                                        isFilled -> Color(0xFF334155)
                                        else -> Color(0xFFCBD5E1)
                                    },
                                    shape = RoundedCornerShape(10.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = digit,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontSize = (22 * fontSizeMultiplier).sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GovNavyPrimary,
                                    textAlign = TextAlign.Center
                                )
                            )
                        }
                    }
                }
            }
        )

        // Resend Timer & Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (canResend) {
                TextButton(
                    onClick = {
                        canResend = false
                        timeLeft = 45
                        onResendOtp()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = GovSaffron)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isHindi) "OTP पुनः भेजें" else "Resend OTP",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = (14 * fontSizeMultiplier).sp
                            )
                        )
                    }
                }
            } else {
                Text(
                    text = if (isHindi) "पुनः OTP भेजें $timeLeft सेकंड में" else "Resend OTP in ${timeLeft}s",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.Medium,
                        fontSize = (13 * fontSizeMultiplier).sp
                    )
                )
            }

            Text(
                text = if (isHindi) "OTP नहीं मिला?" else "Didn't receive?",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF475569),
                    fontSize = (12 * fontSizeMultiplier).sp
                )
            )
        }
    }
}
