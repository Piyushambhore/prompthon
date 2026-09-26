package org.jansaarthi.app.ui.screens.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import org.jansaarthi.app.ui.components.JanSaarthiPrimaryButton
import org.jansaarthi.app.ui.theme.GovNavyPrimary

@Composable
fun TermsPrivacyDialog(
    onDismiss: () -> Unit,
    isHindi: Boolean = false,
    fontSizeMultiplier: Float = 1.0f
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
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
                            imageVector = Icons.Default.Policy,
                            contentDescription = null,
                            tint = GovNavyPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = if (isHindi) "नियम एवं गोपनीयता नीति" else "Terms & Privacy Policy",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = GovNavyPrimary,
                                fontSize = (18 * fontSizeMultiplier).sp
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

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFE2E8F0))

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = if (isHindi)
                            "1. नागरिक डेटा संरक्षण प्रतिज्ञा (DPDP अधिनियम)"
                        else
                            "1. Citizen Data Protection Commitment",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = GovNavyPrimary,
                            fontSize = (15 * fontSizeMultiplier).sp
                        )
                    )
                    Text(
                        text = if (isHindi)
                            "JanSaarthi पोर्टल पर प्रदान की गई आपकी व्यक्तिगत जानकारी (नाम, मोबाइल नंबर, ईमेल) केवल सरकारी कल्याणकारी योजनाओं एवं सेवाओं के सत्यापन के लिए सुरक्षित रूप से उपयोग की जाती है।"
                        else
                            "All personal information (Name, Mobile, Email) provided on JanSaarthi is securely handled strictly for verification and delivery of government welfare schemes and public services.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = (14 * fontSizeMultiplier).sp,
                            color = Color(0xFF334155),
                            lineHeight = 22.sp
                        )
                    )

                    Text(
                        text = if (isHindi)
                            "2. OTP एवं सुरक्षा प्रोटोकॉल"
                        else
                            "2. OTP & Authentication Security",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = GovNavyPrimary,
                            fontSize = (15 * fontSizeMultiplier).sp
                        )
                    )
                    Text(
                        text = if (isHindi)
                            "OTP कभी भी किसी अन्य व्यक्ति या अनधिकृत प्रतिनिधि के साथ साझा न करें। सरकारी अधिकारी कभी भी कॉल या संदेश पर आपसे OTP नहीं मांगते।"
                        else
                            "Never share your OTP with anyone. Government officials or representatives will never call or message asking for your secret OTP or password.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = (14 * fontSizeMultiplier).sp,
                            color = Color(0xFF334155),
                            lineHeight = 22.sp
                        )
                    )

                    Text(
                        text = if (isHindi)
                            "3. कल्याणकारी सेवाओं का लाभ"
                        else
                            "3. Citizen Service Access",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = GovNavyPrimary,
                            fontSize = (15 * fontSizeMultiplier).sp
                        )
                    )
                    Text(
                        text = if (isHindi)
                            "पंजीकरण के बाद आप केंद्र एवं राज्य सरकार की 500 से अधिक योजनाओं के लिए सीधे आवेदन कर सकते हैं और स्थिति को ट्रैक कर सकते हैं।"
                        else
                            "Once registered, citizens can directly apply for and track the progress of over 500 Central and State Government welfare programs seamlessly.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = (14 * fontSizeMultiplier).sp,
                            color = Color(0xFF334155),
                            lineHeight = 22.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                JanSaarthiPrimaryButton(
                    text = if (isHindi) "मैंने समझ लिया और स्वीकार किया" else "I Understand & Accept",
                    onClick = onDismiss,
                    fontSizeMultiplier = fontSizeMultiplier
                )
            }
        }
    }
}
