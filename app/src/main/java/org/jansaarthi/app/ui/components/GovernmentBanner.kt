package org.jansaarthi.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jansaarthi.app.data.model.AppLanguage
import org.jansaarthi.app.ui.localization.getJanSaarthiStrings
import org.jansaarthi.app.ui.theme.GovGreen
import org.jansaarthi.app.ui.theme.GovNavyPrimary
import org.jansaarthi.app.ui.theme.GovSaffron

/**
 * Top Government Trust Header with National Tricolor ribbon and JanSaarthi Identity
 */
@Composable
fun GovernmentBanner(
    modifier: Modifier = Modifier,
    language: AppLanguage = AppLanguage.ENGLISH,
    isHindi: Boolean = (language == AppLanguage.HINDI),
    fontSizeMultiplier: Float = 1.0f
) {
    val activeLang = if (language != AppLanguage.ENGLISH) language else if (isHindi) AppLanguage.HINDI else AppLanguage.ENGLISH
    val strings = getJanSaarthiStrings(activeLang)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(GovNavyPrimary)
    ) {
        // Tricolor top accent strip (Saffron, White, Green)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(GovSaffron)
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(Color.White)
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(GovGreen)
            )
        }

        // Official identification row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AccountBalance,
                    contentDescription = "Government Initiative",
                    tint = Color(0xFFFFD54F),
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = strings.govIndia,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = Color(0xFFE2E8F0),
                        fontSize = (12 * fontSizeMultiplier).sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.5.sp
                    )
                )
            }

            Surface(
                color = Color(0x33FFFFFF),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Verified Portal",
                        tint = Color(0xFF81C784),
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = strings.officialPortal,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFFF1F5F9),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }
    }
}

/**
 * App Logo Header with Brand and Tagline
 */
@Composable
fun JanSaarthiHeader(
    modifier: Modifier = Modifier,
    language: AppLanguage = AppLanguage.ENGLISH,
    isHindi: Boolean = (language == AppLanguage.HINDI),
    fontSizeMultiplier: Float = 1.0f
) {
    val activeLang = if (language != AppLanguage.ENGLISH) language else if (isHindi) AppLanguage.HINDI else AppLanguage.ENGLISH
    val strings = getJanSaarthiStrings(activeLang)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // High-contrast emblem badge
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(GovNavyPrimary, Color(0xFF07243B))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AccountBalance,
                contentDescription = "JanSaarthi Emblem",
                tint = Color.White,
                modifier = Modifier.size(30.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "JanSaarthi ",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = GovNavyPrimary,
                        fontSize = (24 * fontSizeMultiplier).sp
                    )
                )
                Surface(
                    color = GovSaffron,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = if (activeLang == AppLanguage.ENGLISH) "जन सारथी" else activeLang.nativeName,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = (11 * fontSizeMultiplier).sp
                        )
                    )
                }
            }
            Text(
                text = strings.appSubtitle,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF475569),
                    fontSize = (13 * fontSizeMultiplier).sp,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}
