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
 * JanSaarthi Profile & Settings Screen (Frontend UI Only)
 * Centralized citizen profile hub and settings panel with full localization support.
 */

private data class ProfileMenuItem(
    val id: String,
    val icon: ImageVector,
    val label: String,
    val subtitle: String = "",
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

    // ---------------------------------------------------------------------------
    // BUILD MENU SECTIONS
    // ---------------------------------------------------------------------------

    val accountItems = listOf(
        ProfileMenuItem(
            id = "my_profile",
            icon = Icons.Filled.Person,
            label = strings.profileTitle,
            subtitle = citizenName
        ),
        ProfileMenuItem(
            id = "eligibility_profile",
            icon = Icons.Filled.Checklist,
            label = strings.checkEligibility,
            subtitle = if (isHindi) "बेहतर योजनाएं खोजने के लिए जानकारी अपडेट करें" else "Update details to find better schemes"
        ),
        ProfileMenuItem(
            id = "state_selection",
            icon = Icons.Filled.LocationOn,
            label = strings.residentStateSettingLabel,
            subtitle = "$citizenState · $citizenDistrict"
        )
    )

    val prefsItems = listOf(
        ProfileMenuItem(
            id = "language",
            icon = Icons.Filled.Translate,
            label = strings.languagePreferenceLabel,
            subtitle = currentLanguage.nativeName
        ),
        ProfileMenuItem(
            id = "notifications",
            icon = Icons.Filled.Notifications,
            label = strings.notificationsSettingLabel,
            subtitle = if (notificationsEnabled) (if (isHindi) "चालू" else "Enabled") else (if (isHindi) "बंद" else "Disabled"),
            trailingContent = ProfileMenuTrailing.Toggle
        ),
        ProfileMenuItem(
            id = "easy_mode",
            icon = Icons.Filled.Accessibility,
            label = strings.easyModeLabel,
            subtitle = strings.easyModeDesc,
            trailingContent = ProfileMenuTrailing.Toggle
        )
    )

    val dataItems = listOf(
        ProfileMenuItem(
            id = "saved_schemes",
            icon = Icons.Filled.Bookmark,
            label = if (isHindi) "सहेजी गई योजनाएं" else "Saved Schemes",
            subtitle = if (isHindi) "आपकी बुकमार्क की गई कल्याण योजनाएं" else "Your bookmarked welfare schemes",
            badgeCount = 4,
            trailingContent = ProfileMenuTrailing.Badge
        ),
        ProfileMenuItem(
            id = "my_applications",
            icon = Icons.Filled.Assignment,
            label = strings.navApplications,
            subtitle = if (isHindi) "अपने योजना आवेदनों को ट्रैक करें" else "Track your scheme applications",
            badgeCount = 2,
            trailingContent = ProfileMenuTrailing.Badge
        ),
        ProfileMenuItem(
            id = "my_documents",
            icon = Icons.Filled.Folder,
            label = strings.navDocuments,
            subtitle = if (isHindi) "अपने अपलोड किए गए दस्तावेज़ प्रबंधित करें" else "Manage your uploaded documents"
        )
    )

    val supportItems = listOf(
        ProfileMenuItem(
            id = "help_support",
            icon = Icons.AutoMirrored.Filled.Help,
            label = strings.helpTitle,
            subtitle = strings.tollFreeHelplineLabel
        ),
        ProfileMenuItem(
            id = "about",
            icon = Icons.AutoMirrored.Filled.MenuBook,
            label = strings.aboutJanSaarthiTitle,
            subtitle = "v1.0.0 · ${strings.officialPortal}"
        )
    )

    val logoutItem = ProfileMenuItem(
        id = "logout",
        icon = Icons.AutoMirrored.Filled.Logout,
        label = strings.signOutButton,
        subtitle = if (isHindi) "अपने खाते से साइन आउट करें" else "Sign out from your account",
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
                    text = strings.logoutDialogTitle,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = strings.logoutDialogMessage,
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
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .heightIn(max = 500.dp)
                ) {
                    Text(
                        text = strings.selectLanguageTitle,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = "${strings.languagePreferenceLabel}:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Column(
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .verticalScroll(rememberScrollState())
                    ) {
                        AppLanguage.values().forEach { lang ->
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
                                                "${strings.languagePreferenceLabel}: ${lang.nativeName}"
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
                                        text = lang.nativeName,
                                        fontSize = 15.sp,
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                    )
                                    Text(
                                        text = lang.englishName,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
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
                    Text(
                        text = strings.aboutJanSaarthiDesc,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                    )
                    HorizontalDivider()
                    listOf(
                        (if (isHindi) "संस्करण" else "Version") to "1.0.0",
                        (if (isHindi) "प्रायोजन" else "Powered By") to strings.govIndia,
                        (if (isHindi) "हेल्पलाइन" else "Helpline") to strings.tollFreeNumber,
                        (if (isHindi) "डेटा सुरक्षा" else "Privacy") to strings.dpdpCompliant
                    ).forEach { (label, value) ->
                        Column {
                            Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                            Text(text = value, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
                        }
                    }
                    HorizontalDivider()
                    Text(
                        text = strings.prototypeDisclaimer,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
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
                onNavigateHome = onNavigateHome,
                onNavigateSchemes = onNavigateSchemes,
                onNavigateApplications = onNavigateApplications,
                onNavigateDocuments = onNavigateDocuments,
                onNavigateProfile = { /* already here */ }
            )
        },
        topBar = {
            Column {
                GovernmentBanner(language = currentLanguage)
                AccessibilityBar(
                    currentLanguage = currentLanguage,
                    onSelectLanguage = onSelectLanguage,
                    isLargeFont = isLargeFont,
                    onFontScaleToggle = onFontScaleToggle
                )
                TopAppBar(
                    title = {
                        Text(
                            text = strings.settingsTitleLabel,
                            fontWeight = FontWeight.Bold,
                            fontSize = if (isLargeFont) 20.sp else 18.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = strings.navBack)
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
                strings = strings,
                isLargeFont = isLargeFont,
                onEditProfile = onOpenMyProfile
            )

            Spacer(modifier = Modifier.height(8.dp))

            // -----------------------------------------------------------------
            // ACCOUNT SECTION
            // -----------------------------------------------------------------
            SectionLabel(label = strings.accountDetailsLabel, isLargeFont = isLargeFont)
            MenuCard(
                items = accountItems,
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
            SectionLabel(label = strings.settingsTitleLabel, isLargeFont = isLargeFont)
            MenuCard(
                items = prefsItems,
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
                                        "${strings.notificationsSettingLabel}: ON"
                                    else
                                        "${strings.notificationsSettingLabel}: OFF"
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
            SectionLabel(label = strings.navDocuments, isLargeFont = isLargeFont)
            MenuCard(
                items = dataItems,
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
            SectionLabel(label = strings.helpTitle, isLargeFont = isLargeFont)
            MenuCard(
                items = supportItems,
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
                isLargeFont = isLargeFont,
                notificationsOn = notificationsEnabled,
                easyModeOn = easyModeEnabled,
                onItemClick = { showLogoutDialog = true }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "${strings.appTitle} v1.0.0 · ${strings.govIndia}",
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
    strings: org.jansaarthi.app.ui.localization.JanSaarthiStrings,
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
                    text = if (isGuest) strings.guestModeActive else citizenMobile,
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
                QuickStat(Icons.Filled.Bookmark, "4", strings.savedBadge, isLargeFont)
                VerticalDivider(modifier = Modifier.height(36.dp), color = Color.White.copy(0.3f))
                QuickStat(Icons.Filled.Assignment, "2", strings.navApplications, isLargeFont)
                VerticalDivider(modifier = Modifier.height(36.dp), color = Color.White.copy(0.3f))
                QuickStat(Icons.Filled.Folder, "6", strings.navDocuments, isLargeFont)
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
                    text = strings.profileTitle,
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

@Composable
private fun MenuCard(
    items: List<ProfileMenuItem>,
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
    isLargeFont: Boolean,
    notificationsOn: Boolean,
    easyModeOn: Boolean,
    onClick: () -> Unit
) {
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
                text = item.label,
                fontSize = if (isLargeFont) 16.sp else 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = labelColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (item.subtitle.isNotEmpty()) {
                Text(
                    text = item.subtitle,
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
