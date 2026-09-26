package org.jansaarthi.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import org.jansaarthi.app.data.model.AppLanguage
import org.jansaarthi.app.ui.localization.getJanSaarthiStrings
import org.jansaarthi.app.ui.theme.GovNavyContainer
import org.jansaarthi.app.ui.theme.GovNavyPrimary
import org.jansaarthi.app.ui.theme.GovTextPrimary

/**
 * Top Accessibility Bar: One-tap Language Switcher and Font Resizer (A / A+)
 * Specially designed for multilingual citizens & elderly readability.
 */
@Composable
fun AccessibilityBar(
    currentLanguage: AppLanguage,
    onSelectLanguage: (AppLanguage) -> Unit,
    isLargeFont: Boolean,
    onFontScaleToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }

    AccessibilityBar(
        currentLanguage = currentLanguage,
        onLanguageClick = { showDialog = true },
        isLargeFont = isLargeFont,
        onFontScaleToggle = onFontScaleToggle,
        modifier = modifier
    )

    if (showDialog) {
        LanguageSelectorDialog(
            currentLanguage = currentLanguage,
            onSelectLanguage = {
                onSelectLanguage(it)
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }
}

/**
 * Backward compatibility overload for legacy boolean isHindi callers
 */
@Composable
fun AccessibilityBar(
    isHindi: Boolean,
    onLanguageChange: (Boolean) -> Unit,
    isLargeFont: Boolean,
    onFontScaleToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentLang = if (isHindi) AppLanguage.HINDI else AppLanguage.ENGLISH
    AccessibilityBar(
        currentLanguage = currentLang,
        onSelectLanguage = { selected ->
            onLanguageChange(selected == AppLanguage.HINDI)
        },
        isLargeFont = isLargeFont,
        onFontScaleToggle = onFontScaleToggle,
        modifier = modifier
    )
}

@Composable
fun AccessibilityBar(
    currentLanguage: AppLanguage,
    onLanguageClick: () -> Unit,
    isLargeFont: Boolean,
    onFontScaleToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = getJanSaarthiStrings(currentLanguage)

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color(0xFFF1F5F9),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Language selector button
            Surface(
                onClick = onLanguageClick,
                shape = RoundedCornerShape(8.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFCBD5E1))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Translate,
                        contentDescription = "Language",
                        tint = GovNavyPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${currentLanguage.nativeName} (${currentLanguage.code.uppercase()})",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = GovNavyPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    )
                    Text(
                        text = "▾",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            // High Accessibility Font Resizer (A / A+)
            Surface(
                onClick = onFontScaleToggle,
                shape = RoundedCornerShape(8.dp),
                color = if (isLargeFont) Color(0xFFE0F2FE) else Color.White,
                border = BorderStroke(1.dp, if (isLargeFont) GovNavyPrimary else Color(0xFFCBD5E1))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FormatSize,
                        contentDescription = "Text Size",
                        tint = GovNavyPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = if (isLargeFont) strings.largeSize else strings.normalSize,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = GovNavyPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }
    }
}

/**
 * Interactive Modal for choosing from all supported Indian Languages.
 * Shows native script alongside English names.
 */
@Composable
fun LanguageSelectorDialog(
    currentLanguage: AppLanguage,
    onSelectLanguage: (AppLanguage) -> Unit,
    onDismiss: () -> Unit
) {
    val strings = getJanSaarthiStrings(currentLanguage)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
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
                            imageVector = Icons.Default.Translate,
                            contentDescription = null,
                            tint = GovNavyPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = strings.selectLanguageTitle,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = GovNavyPrimary,
                                fontSize = 18.sp
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

                Text(
                    text = "Select your preferred language. All JanSaarthi screens and schemes will immediately update.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF64748B),
                        fontSize = 12.sp
                    )
                )

                HorizontalDivider(color = Color(0xFFE2E8F0))

                // Grid of Supported Official Languages
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 380.dp)
                ) {
                    items(AppLanguage.entries.toTypedArray()) { language ->
                        val isSelected = currentLanguage == language

                        Surface(
                            onClick = {
                                onSelectLanguage(language)
                                onDismiss()
                            },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) GovNavyContainer else Color(0xFFF8FAFC),
                            border = BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) GovNavyPrimary else Color(0xFFCBD5E1)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = language.nativeName,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) GovNavyPrimary else GovTextPrimary,
                                            fontSize = 15.sp
                                        )
                                    )
                                    Text(
                                        text = language.englishName,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Color(0xFF64748B),
                                            fontSize = 11.sp
                                        )
                                    )
                                }

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = GovNavyPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFFE2E8F0))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(
                            text = strings.closeButton,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }
}
