package org.jansaarthi.app.ui.screens.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jansaarthi.app.data.model.ALL_INDIAN_STATES_AND_UTS
import org.jansaarthi.app.data.model.AppLanguage
import org.jansaarthi.app.data.model.IndianState
import org.jansaarthi.app.ui.components.AccessibilityBar
import org.jansaarthi.app.ui.components.GovernmentBanner
import org.jansaarthi.app.ui.components.JanSaarthiPrimaryButton
import org.jansaarthi.app.ui.localization.getJanSaarthiStrings
import org.jansaarthi.app.ui.screens.auth.AuthViewModel
import org.jansaarthi.app.ui.theme.*

@Composable
fun StateSelectionScreen(
    viewModel: AuthViewModel,
    onNavigateBack: (() -> Unit)? = null,
    currentLanguage: AppLanguage,
    onSelectLanguage: (AppLanguage) -> Unit,
    isLargeFont: Boolean,
    onFontScaleToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = getJanSaarthiStrings(currentLanguage)
    val isHindi = currentLanguage == AppLanguage.HINDI
    val fontSizeMultiplier = if (isLargeFont) 1.15f else 1.0f
    val currentSession by viewModel.currentSession.collectAsState()
    val savedStateName = currentSession?.profile?.selectedState

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilterTab by remember { mutableStateOf("ALL") } // ALL, STATES, UTS, POPULAR

    // Pre-select already saved state from user profile, if available
    var selectedState by remember(savedStateName) {
        mutableStateOf(
            ALL_INDIAN_STATES_AND_UTS.find { it.nameEn.equals(savedStateName, ignoreCase = true) }
        )
    }

    // Filter states based on search and category tab
    val filteredStates = remember(searchQuery, selectedFilterTab) {
        ALL_INDIAN_STATES_AND_UTS.filter { state ->
            val matchesTab = when (selectedFilterTab) {
                "STATES" -> !state.isUnionTerritory
                "UTS" -> state.isUnionTerritory
                "POPULAR" -> state.isPopular
                else -> true
            }

            val query = searchQuery.trim().lowercase()
            val matchesQuery = query.isEmpty() ||
                    state.nameEn.lowercase().contains(query) ||
                    state.nameHi.lowercase().contains(query) ||
                    state.code.lowercase().contains(query) ||
                    state.capitalEn.lowercase().contains(query) ||
                    state.capitalHi.lowercase().contains(query)

            matchesTab && matchesQuery
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GovBackground)
    ) {
        // Government Banner
        GovernmentBanner(language = currentLanguage, fontSizeMultiplier = fontSizeMultiplier)

        // Main Container
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Accessibility controls (Language + Text size)
            AccessibilityBar(
                currentLanguage = currentLanguage,
                onSelectLanguage = onSelectLanguage,
                isLargeFont = isLargeFont,
                onFontScaleToggle = onFontScaleToggle
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Navigation row (with back button if opened from Profile to change state)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (onNavigateBack != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onNavigateBack() }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = GovNavyPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isHindi) "वापस प्रोफाइल पर जाएं" else "Back to Profile",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = GovNavyPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                } else {
                    Surface(
                        color = GovNavyContainer,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = strings.stepProfileSetup,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = GovNavyPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = (11 * fontSizeMultiplier).sp
                            )
                        )
                    }
                }

                // Total State count indicator
                Surface(
                    color = Color(0xFFE2E8F0),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (isHindi) "28 राज्य • 8 यूटी (कुल 36)" else "28 States • 8 UTs (36 Total)",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF334155),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title and Subtitle
            Text(
                text = strings.stateSelectionTitle,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = GovNavyPrimary,
                    fontSize = (21 * fontSizeMultiplier).sp
                )
            )

            Text(
                text = strings.stateSelectionSubtitle,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF475569),
                    fontSize = (13 * fontSizeMultiplier).sp,
                    lineHeight = 18.sp
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Search State / UT Input Field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                placeholder = {
                    Text(
                        text = strings.searchStatePlaceholder,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF94A3B8),
                            fontSize = (14 * fontSizeMultiplier).sp
                        )
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = GovNavyPrimary
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear search",
                                tint = Color(0xFF64748B)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = GovNavyPrimary,
                    unfocusedBorderColor = GovBorder
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Filter Tabs Row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = selectedFilterTab == "ALL",
                        onClick = { selectedFilterTab = "ALL" },
                        label = { Text(strings.allStatesTab) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GovNavyPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
                item {
                    FilterChip(
                        selected = selectedFilterTab == "STATES",
                        onClick = { selectedFilterTab = "STATES" },
                        label = { Text(strings.statesOnlyTab) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GovNavyPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
                item {
                    FilterChip(
                        selected = selectedFilterTab == "UTS",
                        onClick = { selectedFilterTab = "UTS" },
                        label = { Text(strings.utsOnlyTab) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GovNavyPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
                item {
                    FilterChip(
                        selected = selectedFilterTab == "POPULAR",
                        onClick = { selectedFilterTab = "POPULAR" },
                        label = { Text(strings.popularTab) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GovNavyPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Search Count / Selection status summary
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isHindi)
                        "${filteredStates.size} राज्य/यूटी उपलब्ध"
                    else
                        "Showing ${filteredStates.size} States & UTs",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.SemiBold
                    )
                )

                if (selectedState != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = GovGreen,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${strings.selectedStateLabel}: ${if (isHindi) selectedState!!.nameHi else selectedState!!.nameEn}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = GovNavyPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Scrollable List of All Indian States and UTs (Large touch targets, min 68dp)
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredStates, key = { it.code }) { state ->
                    val isSelected = selectedState?.code == state.code

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedState = state },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) Color(0xFFEBF5FF) else Color.White
                        ),
                        border = BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) GovNavyPrimary else Color(0xFFE2E8F0)
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = if (isSelected) 3.dp else 1.dp
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // State Code Emblem
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) GovNavyPrimary else Color(0xFFF1F5F9),
                                modifier = Modifier.size(46.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = state.code,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (isSelected) Color.White else GovNavyPrimary,
                                            fontSize = (16 * fontSizeMultiplier).sp
                                        )
                                    )
                                }
                            }

                            // State details
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = if (isHindi) state.nameHi else state.nameEn,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = GovTextPrimary,
                                            fontSize = (16 * fontSizeMultiplier).sp
                                        )
                                    )

                                    if (state.isUnionTerritory) {
                                        Surface(
                                            color = Color(0xFFFEF3C7),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = strings.unionTerritoryBadge,
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp),
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = Color(0xFF92400E),
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                        }
                                    }
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = if (isHindi) state.nameEn else state.nameHi,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Color(0xFF475569),
                                            fontSize = (13 * fontSizeMultiplier).sp
                                        )
                                    )
                                    Text(
                                        text = "•",
                                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCBD5E1))
                                    )
                                    Text(
                                        text = "${strings.capitalLabel}: ${if (isHindi) state.capitalHi else state.capitalEn}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Color(0xFF64748B),
                                            fontSize = (12 * fontSizeMultiplier).sp
                                        )
                                    )
                                }

                                Text(
                                    text = "${strings.zoneLabel}: ${if (isHindi) state.zoneHi else state.zoneEn}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFF94A3B8),
                                        fontSize = 11.sp
                                    )
                                )
                            }

                            // Selected Indicator Radio/Check
                            if (isSelected) {
                                Surface(
                                    color = GovNavyPrimary,
                                    shape = RoundedCornerShape(20.dp),
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            } else {
                                Icon(
                                    imageVector = Icons.Default.RadioButtonUnchecked,
                                    contentDescription = "Unselected",
                                    tint = Color(0xFFCBD5E1),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bottom Sticky Confirm Button
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    JanSaarthiPrimaryButton(
                        text = if (selectedState != null) {
                            val stateLabel = if (isHindi) selectedState!!.nameHi else selectedState!!.nameEn
                            "${strings.confirmStateButton}: $stateLabel"
                        } else {
                            strings.stateSelectionTitle
                        },
                        enabled = selectedState != null,
                        leadingIcon = Icons.Default.CheckCircle,
                        onClick = {
                            selectedState?.let { state ->
                                viewModel.selectState(state.nameEn)
                            }
                        },
                        fontSizeMultiplier = fontSizeMultiplier
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = strings.changeStateLaterNote,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF64748B),
                                fontSize = (11 * fontSizeMultiplier).sp
                            )
                        )
                    }
                }
            }
        }
    }
}
