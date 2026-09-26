package org.jansaarthi.app.ui.screens.profile

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.launch
import org.jansaarthi.app.data.model.AppLanguage
import org.jansaarthi.app.ui.components.AccessibilityBar
import org.jansaarthi.app.ui.components.GovernmentBanner
import org.jansaarthi.app.ui.components.JanSaarthiBottomNav
import org.jansaarthi.app.ui.localization.getJanSaarthiStrings
import org.jansaarthi.app.ui.theme.*

/**
 * ----------------------------------------------------------------------------
 * JanSaarthi Profile & Settings Screen (Frontend UI Only)
 * ----------------------------------------------------------------------------
 * Centralised citizen profile hub and settings panel:
 * - Citizen avatar card with name, mobile, state, and quick stats
 * - Full menu list: My Profile, Eligibility Profile, State/UT, Language,
 *   Notifications, Accessibility/Easy Mode, Saved Schemes, My Applications,
 *   My Documents, Help & Support, About JanSaarthi, and Logout
 * - Every row navigates to the correct destination
 * - Logout confirmation AlertDialog with session-clear callback
 * - Language picker Dialog (English / Hindi; more coming soon)
 * - About JanSaarthi info dialog
 * - Notification & Easy Mode in-memory toggles
 *
 * Frontend UI Only - zero backend, database, or network dependencies.
 */

// ---------------------------------------------------------------------------
// DATA MODELS
// ---------------------------------------------------------------------------

private data class ProfileMenuItem(
    val id: String,
    val icon: ImageVector,
    val labelEn: String,
    val labelHi: String,
    val subtitleEn: String = "",
    val subtitleHi: String = "",
    val badgeCount: Int = 0,
    val isDestructive: Boolean = false,
    val trailingContent: ProfileMenuTrailing = ProfileMenuTrailing.Arrow
)

private sealed class ProfileMenuTrailing {
    data object Arrow : ProfileMenuTrailing()
    data object Toggle : ProfileMenuTrailing()
    data object Badge : ProfileMenuTrailing()
    data object None : ProfileMenuTrailing()
}

// ---------------------------------------------------------------------------
// SCREEN COMPOSABLE
// ---------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSettingsScreen(
    currentLanguage: AppLanguage = AppLanguage.ENGLISH,
    onSelectLanguage: (AppLanguage) -> Unit = {},
    isLargeFont: Boolean = false,
    onFontScaleToggle: () -> Unit = {},
    citizenName: String = "Ramesh Kumar",
    citizenMobile: String = "+91 98765 43210",
    citizenState: String = "Maharashtra",
    citizenDistrict: String = "Pune",
    isGuest: Boolean = false,
    onNavigateBack: () -> Unit = {},
    onOpenMyProfile: () -> Unit = {},
    onOpenEligibilityProfile: () -> Unit = {},
    onOpenStateSelection: () -> Unit = {},
    onOpenSavedSchemes: () -> Unit = {},
    onOpenMyApplications: () -> Unit = {},
    onOpenMyDocuments: () -> Unit = {},
    onOpenHelpSupport: () -> Unit = {},
    onNavigateHome: () -> Unit = {},
    onNavigateSchemes: () -> Unit = {},
    onNavigateApplications: () -> Unit = {},
    onNavigateDocuments: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val strings = getJanSaarthiStrings(currentLanguage)
    val isHindi = currentLanguage == AppLanguage.HINDI
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // In-memory toggles (no backend)
    var notificationsEnabled by remember { mutableStateOf(true) }
    var easyModeEnabled by remember { mutableStateOf(isLargeFont) }

    // Dialog visibility
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    val selectedLangCode = currentLanguage.code

    // ---------------------------------------------------------------------------
    // BUILD MENU SECTIONS
    // ---------------------------------------------------------------------------

    val accountItems = listOf(
        ProfileMenuItem(
            id = "my_profile",
            icon = Icons.Filled.Person,
            labelEn = "My Profile",
            labelHi = "मेरी प्रोफाइल",
            subtitleEn = citizenName,
            subtitleHi = citizenName
        ),
        ProfileMenuItem(
            id = "eligibility_profile",
            icon = Icons.Filled.Checklist,
            labelEn = "Eligibility Profile",
            labelHi = "पात्रता प्रोफाइल",
            subtitleEn = "Update details to find better schemes",
            subtitleHi = "बेहतर योजनाएं खोजने के लिए जानकारी अपडेट करें"
        ),
        ProfileMenuItem(
            id = "state_selection",
            icon = Icons.Filled.LocationOn,
            labelEn = "State / UT",
            labelHi = "राज्य / केंद्र शासित प्रदेश",
            subtitleEn = "$citizenState · $citizenDistrict",
            subtitleHi = "$citizenState · $citizenDistrict"
        )
    )

    val prefsItems = listOf(
        ProfileMenuItem(
            id = "language",
            icon = Icons.Filled.Translate,
            labelEn = "Language",
            labelHi = "भाषा",
            subtitleEn = "${currentLanguage.nativeName} (${currentLanguage.englishName})"
        ),
        ProfileMenuItem(
            id = "notifications",
            icon = Icons.Filled.Notifications,
            labelEn = "Notifications",
            labelHi = "सूचनाएं",
            subtitleEn = if (notificationsEnabled) "Enabled" else "Disabled",
            subtitleHi = if (notificationsEnabled) "चालू" else "बंद",
            trailingContent = ProfileMenuTrailing.Toggle
        ),
        ProfileMenuItem(
            id = "easy_mode",
            icon = Icons.Filled.Accessibility,
            labelEn = "Accessibility / Easy Mode",
            labelHi = "सुगमता / आसान मोड",
            subtitleEn = if (easyModeEnabled) "Large text · High contrast" else "Standard view",
            subtitleHi = if (easyModeEnabled) "बड़ा टेक्स्ट · उच्च कंट्रास्ट" else "मानक दृश्य",
            trailingContent = ProfileMenuTrailing.Toggle
        )
    )

    val dataItems = listOf(
        ProfileMenuItem(
            id = "saved_schemes",
            icon = Icons.Filled.Bookmark,
            labelEn = "Saved Schemes",
            labelHi = "सहेजी गई योजनाएं",
            subtitleEn = "Your bookmarked welfare schemes",
            subtitleHi = "आपकी बुकमार्क की गई कल्याण योजनाएं",
            badgeCount = 4,
            trailingContent = ProfileMenuTrailing.Badge
        ),
        ProfileMenuItem(
            id = "my_applications",
            icon = Icons.Filled.Assignment,
            labelEn = "My Applications",
            labelHi = "मेरे आवेदन",
            subtitleEn = "Track your scheme applications",
            subtitleHi = "अपने योजना आवेदनों को ट्रैक करें",
            badgeCount = 2,
            trailingContent = ProfileMenuTrailing.Badge
        ),
        ProfileMenuItem(
            id = "my_documents",
            icon = Icons.Filled.Folder,
            labelEn = "My Documents",
            labelHi = "मेरे दस्तावेज़",
            subtitleEn = "Manage your uploaded documents",
            subtitleHi = "अपने अपलोड किए गए दस्तावेज़ प्रबंधित करें"
        )
    )

    val supportItems = listOf(
        ProfileMenuItem(
            id = "help_support",
            icon = Icons.AutoMirrored.Filled.Help,
            labelEn = "Help & Support",
            labelHi = "सहायता और समर्थन",
            subtitleEn = "FAQs, grievances, helpline",
            subtitleHi = "अक्सर पूछे जाने वाले प्रश्न, शिकायतें, हेल्पलाइन"
        ),
        ProfileMenuItem(
            id = "about",
            icon = Icons.AutoMirrored.Filled.MenuBook,
            labelEn = "About JanSaarthi",
            labelHi = "जनसारथी के बारे में",
            subtitleEn = "v1.0.0 · Open-source welfare guide",
            subtitleHi = "v1.0.0 · ओपन-सोर्स कल्याण मार्गदर्शिका"
        )
    )

    val logoutItem = ProfileMenuItem(
        id = "logout",
        icon = Icons.AutoMirrored.Filled.Logout,
        labelEn = "Logout",
        labelHi = "लॉग आउट",
        subtitleEn = "Sign out from your account",
        subtitleHi = "अपने खाते से साइन आउट करें",
        isDestructive = true,
        trailingContent = ProfileMenuTrailing.None
    )

    // ---------------------------------------------------------------------------
    // DIALOGS
    // ---------------------------------------------------------------------------

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = {
                Text(
                    text = strings.confirmSignOutTitle,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = strings.confirmSignOutMessage,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                )
            },
            confirmButton = {
                Button(
                    onClick = { showLogoutDialog = false; onLogout() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(strings.signOutButton)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(strings.cancelAction)
                }
            }
        )
    }

    if (showLanguageDialog) {
        Dialog(onDismissRequest = { showLanguageDialog = false }) {
            Card(shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(8.dp)) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = strings.selectLanguageTitle,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = "${strings.languageLabel} (${AppLanguage.entries.size} Official Languages)",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 360.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(AppLanguage.entries) { lang ->
                            val isSelected = lang == currentLanguage
                            val bgColor by animateColorAsState(
                                targetValue = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                                label = "langBg"
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(bgColor)
                                    .clickable {
                                        showLanguageDialog = false
                                        onSelectLanguage(lang)
                                        scope.launch {
                                            snackbarHostState.showSnackbar(
                                                "${lang.nativeName} (${lang.englishName})"
                                            )
                                        }
                                    }
                                    .padding(horizontal = 8.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = isSelected, onClick = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "${lang.nativeName} (${lang.englishName})",
                                        fontSize = 14.sp,
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                    )
                                    Text(
                                        text = lang.scriptSample,
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    TextButton(onClick = { showLanguageDialog = false }, modifier = Modifier.align(Alignment.End)) {
                        Text(strings.closeButton)
                    }
                }
            }
        }
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            icon = {
                Icon(imageVector = Icons.Filled.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            },
            title = {
                Text(text = strings.aboutJanSaarthiTitle, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf(
                        "Version" to "1.0.0",
                        "Portal" to strings.officialPortal,
                        "Government" to strings.govIndia,
                        "Privacy" to strings.privacyPledge
                    ).forEach { (label, value) ->
                        Column {
                            Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                            Text(text = value, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
                        }
                    }
                    HorizontalDivider()
                    Text(
                        text = strings.nicAttribution,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text(strings.closeButton)
                }
            }
        )
    }

    // ---------------------------------------------------------------------------
    // SCAFFOLD
    // ---------------------------------------------------------------------------

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            JanSaarthiBottomNav(
                selectedTab = 4,
                currentLanguage = currentLanguage,
                isEasyMode = isLargeFont,
                onNavigateHome = onNavigateHome,
                onNavigateSchemes = onNavigateSchemes,
                onNavigateApplications = onNavigateApplications,
                onNavigateDocuments = onNavigateDocuments,
                onNavigateProfile = { /* already here */ }
            )
        },
        topBar = {
            Column {
                GovernmentBanner()
                AccessibilityBar(
                    currentLanguage = currentLanguage,
                    onSelectLanguage = onSelectLanguage,
                    isLargeFont = isLargeFont,
                    onFontScaleToggle = onFontScaleToggle
                )
                TopAppBar(
                    title = {
                        Text(
                            text = if (isHindi) "प्रोफाइल और सेटिंग्स" else "Profile & Settings",
                            fontWeight = FontWeight.Bold,
                            fontSize = if (isLargeFont) 20.sp else 18.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
        ) {
            // -----------------------------------------------------------------
            // AVATAR / HERO CARD
            // -----------------------------------------------------------------
            ProfileAvatarCard(
                citizenName = citizenName,
                citizenMobile = citizenMobile,
                citizenState = citizenState,
                isGuest = isGuest,
                isHindi = isHindi,
                isLargeFont = isLargeFont,
                onEditProfile = onOpenMyProfile
            )

            Spacer(modifier = Modifier.height(8.dp))

            // -----------------------------------------------------------------
            // ACCOUNT SECTION
            // -----------------------------------------------------------------
            SectionLabel(label = if (isHindi) "खाता" else "ACCOUNT", isLargeFont = isLargeFont)
            MenuCard(
                items = accountItems,
                isHindi = isHindi,
                isLargeFont = isLargeFont,
                notificationsOn = notificationsEnabled,
                easyModeOn = easyModeEnabled,
                onItemClick = { item ->
                    when (item.id) {
                        "my_profile" -> onOpenMyProfile()
                        "eligibility_profile" -> onOpenEligibilityProfile()
                        "state_selection" -> onOpenStateSelection()
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // -----------------------------------------------------------------
            // PREFERENCES SECTION
            // -----------------------------------------------------------------
            SectionLabel(label = if (isHindi) "प्राथमिकताएं" else "PREFERENCES", isLargeFont = isLargeFont)
            MenuCard(
                items = prefsItems,
                isHindi = isHindi,
                isLargeFont = isLargeFont,
                notificationsOn = notificationsEnabled,
                easyModeOn = easyModeEnabled,
                onItemClick = { item ->
                    when (item.id) {
                        "language" -> showLanguageDialog = true
                        "notifications" -> {
                            notificationsEnabled = !notificationsEnabled
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    if (notificationsEnabled)
                                        if (isHindi) "सूचनाएं चालू की गईं" else "Notifications enabled"
                                    else
                                        if (isHindi) "सूचनाएं बंद की गईं" else "Notifications disabled"
                                )
                            }
                        }
                        "easy_mode" -> {
                            easyModeEnabled = !easyModeEnabled
                            onFontScaleToggle()
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // -----------------------------------------------------------------
            // MY DATA SECTION
            // -----------------------------------------------------------------
            SectionLabel(label = if (isHindi) "मेरा डेटा" else "MY DATA", isLargeFont = isLargeFont)
            MenuCard(
                items = dataItems,
                isHindi = isHindi,
                isLargeFont = isLargeFont,
                notificationsOn = notificationsEnabled,
                easyModeOn = easyModeEnabled,
                onItemClick = { item ->
                    when (item.id) {
                        "saved_schemes" -> onOpenSavedSchemes()
                        "my_applications" -> onOpenMyApplications()
                        "my_documents" -> onOpenMyDocuments()
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // -----------------------------------------------------------------
            // SUPPORT SECTION
            // -----------------------------------------------------------------
            SectionLabel(label = if (isHindi) "सहायता" else "SUPPORT", isLargeFont = isLargeFont)
            MenuCard(
                items = supportItems,
                isHindi = isHindi,
                isLargeFont = isLargeFont,
                notificationsOn = notificationsEnabled,
                easyModeOn = easyModeEnabled,
                onItemClick = { item ->
                    when (item.id) {
                        "help_support" -> onOpenHelpSupport()
                        "about" -> showAboutDialog = true
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // -----------------------------------------------------------------
            // LOGOUT
            // -----------------------------------------------------------------
            MenuCard(
                items = listOf(logoutItem),
                isHindi = isHindi,
                isLargeFont = isLargeFont,
                notificationsOn = notificationsEnabled,
                easyModeOn = easyModeEnabled,
                onItemClick = { showLogoutDialog = true }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = if (isHindi) "जनसारथी v1.0.0 · भारत सरकार" else "JanSaarthi v1.0.0 · Government of India",
                fontSize = if (isLargeFont) 13.sp else 11.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
        }
    }
}

// ---------------------------------------------------------------------------
// HERO AVATAR CARD
// ---------------------------------------------------------------------------

@Composable
private fun ProfileAvatarCard(
    citizenName: String,
    citizenMobile: String,
    citizenState: String,
    isGuest: Boolean,
    isHindi: Boolean,
    isLargeFont: Boolean,
    onEditProfile: () -> Unit
) {
    val initials = citizenName
        .split(" ")
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .joinToString("")
        .ifEmpty { "?" }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFF0D47A1), Color(0xFF1565C0), Color(0xFF1976D2))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Initials circle
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.18f))
                    .border(2.dp, Color.White.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = initials, fontSize = 30.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = citizenName,
                fontSize = if (isLargeFont) 22.sp else 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(Icons.Filled.Phone, null, tint = Color.White.copy(0.8f), modifier = Modifier.size(14.dp))
                Text(
                    text = if (isGuest) (if (isHindi) "अतिथि उपयोगकर्ता" else "Guest User") else citizenMobile,
                    fontSize = if (isLargeFont) 14.sp else 13.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
            Spacer(modifier = Modifier.height(3.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(Icons.Filled.LocationOn, null, tint = Color.White.copy(0.8f), modifier = Modifier.size(14.dp))
                Text(
                    text = citizenState,
                    fontSize = if (isLargeFont) 14.sp else 13.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quick stats
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.12f))
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuickStat(Icons.Filled.Bookmark, "4", if (isHindi) "सहेजी गई" else "Saved", isLargeFont)
                VerticalDivider(modifier = Modifier.height(36.dp), color = Color.White.copy(0.3f))
                QuickStat(Icons.Filled.Assignment, "2", if (isHindi) "आवेदन" else "Applied", isLargeFont)
                VerticalDivider(modifier = Modifier.height(36.dp), color = Color.White.copy(0.3f))
                QuickStat(Icons.Filled.Folder, "6", if (isHindi) "दस्तावेज़" else "Docs", isLargeFont)
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onEditProfile,
                modifier = Modifier.fillMaxWidth(0.65f),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.6f)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Icon(Icons.Filled.Edit, null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isHindi) "प्रोफाइल संपादित करें" else "Edit Profile",
                    fontSize = if (isLargeFont) 14.sp else 13.sp
                )
            }
        }
    }
}

@Composable
private fun QuickStat(icon: ImageVector, count: String, label: String, isLargeFont: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, tint = Color.White.copy(0.9f), modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(count, fontSize = if (isLargeFont) 18.sp else 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text(label, fontSize = if (isLargeFont) 12.sp else 10.sp, color = Color.White.copy(0.75f))
    }
}

// ---------------------------------------------------------------------------
// SECTION LABEL
// ---------------------------------------------------------------------------

@Composable
private fun SectionLabel(label: String, isLargeFont: Boolean) {
    Text(
        text = label,
        fontSize = if (isLargeFont) 12.sp else 11.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        letterSpacing = 1.2.sp,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 4.dp)
    )
}

// ---------------------------------------------------------------------------
// MENU CARD + ROW
// ---------------------------------------------------------------------------

@Composable
private fun MenuCard(
    items: List<ProfileMenuItem>,
    isHindi: Boolean,
    isLargeFont: Boolean,
    notificationsOn: Boolean,
    easyModeOn: Boolean,
    onItemClick: (ProfileMenuItem) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(1.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            items.forEachIndexed { idx, item ->
                MenuRow(
                    item = item,
                    isHindi = isHindi,
                    isLargeFont = isLargeFont,
                    notificationsOn = notificationsOn,
                    easyModeOn = easyModeOn,
                    onClick = { onItemClick(item) }
                )
                if (idx < items.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(start = 56.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(0.4f)
                    )
                }
            }
        }
    }
}

@Composable
private fun MenuRow(
    item: ProfileMenuItem,
    isHindi: Boolean,
    isLargeFont: Boolean,
    notificationsOn: Boolean,
    easyModeOn: Boolean,
    onClick: () -> Unit
) {
    val label = if (isHindi) item.labelHi else item.labelEn
    val subtitle = if (isHindi) item.subtitleHi else item.subtitleEn
    val iconTint = if (item.isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
    val labelColor = if (item.isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
    val isOn = when (item.id) {
        "notifications" -> notificationsOn
        "easy_mode" -> easyModeOn
        else -> false
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(iconTint.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(item.icon, null, tint = iconTint, modifier = Modifier.size(20.dp))
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = if (isLargeFont) 16.sp else 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = labelColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (subtitle.isNotEmpty()) {
                Text(
                    text = subtitle,
                    fontSize = if (isLargeFont) 13.sp else 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        when (item.trailingContent) {
            ProfileMenuTrailing.Arrow -> Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                null,
                tint = MaterialTheme.colorScheme.onSurface.copy(0.3f),
                modifier = Modifier.size(18.dp)
            )
            ProfileMenuTrailing.Toggle -> Switch(
                checked = isOn,
                onCheckedChange = { onClick() },
                modifier = Modifier.height(24.dp),
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = MaterialTheme.colorScheme.primary
                )
            )
            ProfileMenuTrailing.Badge -> {
                if (item.badgeCount > 0) {
                    Box(
                        modifier = Modifier
                            .defaultMinSize(minWidth = 24.dp, minHeight = 24.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.badgeCount.toString(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(0.3f),
                    modifier = Modifier.size(18.dp)
                )
            }
            ProfileMenuTrailing.None -> { /* no trailing */ }
        }
    }
}
