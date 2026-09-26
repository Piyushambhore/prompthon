package org.jansaarthi.app.ui.screens.guest

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jansaarthi.app.ui.components.AccessibilityBar
import org.jansaarthi.app.ui.components.GovernmentBanner
import org.jansaarthi.app.data.model.NATIONWIDE_CENTRAL_SCHEMES
import org.jansaarthi.app.data.model.WelfareScheme
import org.jansaarthi.app.ui.theme.*

val sampleWelfareSchemes: List<WelfareScheme> = NATIONWIDE_CENTRAL_SCHEMES

@Composable
fun GuestServicesScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    isHindi: Boolean,
    onLanguageChange: (Boolean) -> Unit,
    isLargeFont: Boolean,
    onFontScaleToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val fontSizeMultiplier = if (isLargeFont) 1.15f else 1.0f

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GovBackground)
    ) {
        // Government Banner
        GovernmentBanner(isHindi = isHindi, fontSizeMultiplier = fontSizeMultiplier)

        // Top Navigation Bar
        Surface(
            color = Color.White,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = GovNavyPrimary
                        )
                    }
                    Text(
                        text = if (isHindi) "सार्वजनिक कल्याण योजनाएं" else "Public Welfare Services",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = GovNavyPrimary,
                            fontSize = (17 * fontSizeMultiplier).sp
                        )
                    )
                }

                Button(
                    onClick = onNavigateToLogin,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GovNavyPrimary),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (isHindi) "लॉगिन करें" else "Sign In",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = (13 * fontSizeMultiplier).sp
                        )
                    )
                }
            }
        }

        // Accessibility Bar
        AccessibilityBar(
            isHindi = isHindi,
            onLanguageChange = onLanguageChange,
            isLargeFont = isLargeFont,
            onFontScaleToggle = onFontScaleToggle,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // Guest Notice Banner
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            color = Color(0xFFFFF8E1),
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, Color(0xFFFFE082))
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = Color(0xFFE65100),
                    modifier = Modifier.size(22.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isHindi) "अतिथि मोड में देख रहे हैं" else "Viewing in Guest Mode",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFB78103),
                            fontSize = (13 * fontSizeMultiplier).sp
                        )
                    )
                    Text(
                        text = if (isHindi)
                            "आवेदन करने अथवा प्रमाण पत्र डाउनलोड करने हेतु कृपया लॉगिन अथवा पंजीकरण करें।"
                        else
                            "To submit applications or download verified certificates, please sign in or register.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF6B4800),
                            fontSize = (12 * fontSizeMultiplier).sp
                        )
                    )
                }
            }
        }

        // List of Public Services
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = if (isHindi) "लोकप्रिय नागरिक योजनाएं" else "Popular Citizen Welfare Programs",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = GovNavyPrimary,
                        fontSize = (16 * fontSizeMultiplier).sp
                    ),
                    modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                )
            }

            items(sampleWelfareSchemes) { scheme ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToLogin() }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Surface(
                            color = GovNavyContainer,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.size(50.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = scheme.icon,
                                    contentDescription = null,
                                    tint = GovNavyPrimary,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Surface(
                                color = Color(0xFFF1F5F9),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = scheme.tag,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFF475569),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isHindi) scheme.titleHi else scheme.titleEn,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GovTextPrimary,
                                    fontSize = (15 * fontSizeMultiplier).sp
                                )
                            )
                            Text(
                                text = if (isHindi) scheme.benefitHi else scheme.benefitEn,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = GovGreen,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = (13 * fontSizeMultiplier).sp
                                )
                            )
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "View Details",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
