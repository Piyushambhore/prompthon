package org.jansaarthi.app.ui.screens.home

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Announcement
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import org.jansaarthi.app.data.model.AppLanguage
import org.jansaarthi.app.data.model.SchemeLevel
import org.jansaarthi.app.data.model.WelfareScheme
import org.jansaarthi.app.data.model.getApplicableSchemesForState
import org.jansaarthi.app.ui.components.AccessibilityBar
import org.jansaarthi.app.ui.components.GovernmentBanner
import org.jansaarthi.app.ui.components.JanSaarthiBottomNav
import org.jansaarthi.app.ui.components.LanguageSelectorDialog
import org.jansaarthi.app.ui.localization.getJanSaarthiStrings
import org.jansaarthi.app.ui.theme.*

/**
 * ----------------------------------------------------------------------------
 * JanSaarthi Home Screen - Modern Government Welfare Portal (Frontend UI Only)
 * ----------------------------------------------------------------------------
 * Designed for extreme simplicity, high contrast, and accessibility across all ages:
 * - Children & Young Adults (clear visuals, clean cards, modern feel)
 * - Adults & Working Citizens (quick search, DBT status, application tracking)
 * - Senior Citizens & Rural Users (large touch targets, voice prompt mock, high legibility)
 *
 * Contains 11 Mandated Core Sections:
 * 1. Namaste / Citizen Greeting (Warm personal greeting, State pill, notification bell)
 * 2. Search Government Schemes (Accessible search input with Voice search & clear button)
 * 3. Find Schemes For Me (Hero card CTA guiding user to Eligibility Profile assessment)
 * 4. Browse All Categories (Material 3 icon cards with active scheme count badges)
 * 5. Central Government Schemes (Nationwide flagship DBT welfare initiatives)
 * 6. State / UT Schemes (Tailored dynamically to resident state e.g., Maharashtra)
 * 7. Recently Viewed (Horizontal carousel of recently inspected schemes)
 * 8. Saved Schemes (Interactive bookmark system with save/unsave toggle)
 * 9. My Applications (Live multi-stage status tracker with verification steps & DBT dates)
 * 10. My Documents (DigiLocker / Digital Vault with verified document cards)
 * 11. Government Updates (Official bulletins, DBT release dates & deadline extensions)
 */

// -------------------------------------------------------------
// UI DATA MODELS FOR LOCAL FRONTEND MOCK STATE
// -------------------------------------------------------------

data class WelfareCategoryItem(
    val id: String,
    val icon: ImageVector,
    val titleKey: String,
    val count: Int,
    val color: Color
)

data class SchemeApplicationStatus(
    val applicationId: String,
    val schemeId: String,
    val schemeTitle: String,
    val department: String,
    val currentStageIndex: Int, // 0 to 3
    val stages: List<String>,
    val statusBadge: String,
    val statusColor: Color,
    val nextScheduleText: String,
    val lastUpdated: String
)

data class CitizenDocumentItem(
    val id: String,
    val title: String,
    val docNumberMasked: String,
    val issuer: String,
    val status: String,
    val isVerified: Boolean,
    val icon: ImageVector
)

data class GovernmentUpdateItem(
    val id: String,
    val title: String,
    val summary: String,
    val authority: String,
    val date: String,
    val isUrgent: Boolean,
    val categoryTag: String
)

data class RecentlyViewedItem(
    val schemeId: String,
    val title: String,
    val category: String,
    val benefitHighlight: String,
    val level: SchemeLevel,
    val viewedTimeAgo: String,
    val icon: ImageVector
)

// -------------------------------------------------------------
// MAIN HOME SCREEN COMPOSABLE
// -------------------------------------------------------------

@Composable
fun JanSaarthiHomeScreen(
    citizenName: String = "Rajesh Sharma",
    currentState: String = "Maharashtra",
    isGuest: Boolean = false,
    onChangeState: () -> Unit = {},
    currentLanguage: AppLanguage = AppLanguage.HINDI,
    onSelectLanguage: (AppLanguage) -> Unit = {},
    isLargeFont: Boolean = false,
    onFontScaleToggle: () -> Unit = {},
    onOpenEligibilityProfile: () -> Unit = {},
    onOpenFindSchemesForMe: () -> Unit = {},
    onOpenCategories: () -> Unit = {},
    onOpenMinistries: () -> Unit = {},
    onOpenAllSchemes: () -> Unit = {},
    onOpenDocuments: () -> Unit = {},
    onOpenApplications: () -> Unit = {},
    onOpenSearch: (String) -> Unit = {},
    onOpenSavedSchemes: () -> Unit = {},
    onOpenProfileSettings: () -> Unit = {},
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val strings = getJanSaarthiStrings(currentLanguage)
    val fontSizeMultiplier = if (isLargeFont) 1.15f else 1.0f

    // -------------------------------------------------------------
    // LOCAL INTERACTIVE STATE (FRONTEND ONLY - NO BACKEND/DATABASE)
    // -------------------------------------------------------------
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var selectedSchemeScope by remember { mutableStateOf("ALL") } // ALL, CENTRAL, STATE

    // Saved/Bookmarked schemes set
    var savedSchemeIds by remember {
        mutableStateOf(setOf("CEN-01", "CEN-02", "MH-01"))
    }

    // Modal dialog controls
    var selectedSchemeForDetails by remember { mutableStateOf<WelfareScheme?>(null) }
    var showProfileModal by remember { mutableStateOf(false) }
    var showLanguageDialogFromProfile by remember { mutableStateOf(false) }
    var showNotificationDialog by remember { mutableStateOf(false) }
    var showDigiLockerSyncDialog by remember { mutableStateOf(false) }
    var selectedDocForPreview by remember { mutableStateOf<CitizenDocumentItem?>(null) }
    var selectedUpdateForReading by remember { mutableStateOf<GovernmentUpdateItem?>(null) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    // Schemes for State
    val applicableSchemes = remember(currentState) {
        getApplicableSchemesForState(currentState)
    }

    val centralSchemes = remember(applicableSchemes) {
        applicableSchemes.filter { it.level == SchemeLevel.CENTRAL }
    }

    val stateSchemes = remember(applicableSchemes) {
        applicableSchemes.filter { it.level == SchemeLevel.STATE }
    }

    // Filtered Schemes based on Search, Category, and Scope
    val filteredSchemes = remember(applicableSchemes, selectedSchemeScope, selectedCategory, searchQuery, currentLanguage) {
        applicableSchemes.filter { scheme ->
            val matchesScope = when (selectedSchemeScope) {
                "CENTRAL" -> scheme.level == SchemeLevel.CENTRAL
                "STATE" -> scheme.level == SchemeLevel.STATE
                else -> true
            }

            val matchesCategory = selectedCategory == null || scheme.category.equals(selectedCategory, ignoreCase = true)

            val query = searchQuery.trim().lowercase()
            val matchesSearch = query.isEmpty() ||
                    scheme.getTitle(currentLanguage).lowercase().contains(query) ||
                    scheme.titleEn.lowercase().contains(query) ||
                    scheme.getDepartment(currentLanguage).lowercase().contains(query) ||
                    scheme.getBenefit(currentLanguage).lowercase().contains(query)

            matchesScope && matchesCategory && matchesSearch
        }
    }

    // Category Definitions with Material 3 Icons
    val categories = remember(strings) {
        listOf(
            WelfareCategoryItem("Agriculture", Icons.Default.Agriculture, strings.categoryFarmers, 18, Color(0xFF15803D)),
            WelfareCategoryItem("Healthcare", Icons.Default.HealthAndSafety, strings.categoryHealthcare, 12, Color(0xFF0284C7)),
            WelfareCategoryItem("Housing", Icons.Default.Home, strings.categoryHousing, 8, Color(0xFFD97706)),
            WelfareCategoryItem("Education", Icons.Default.School, strings.categoryEducation, 24, Color(0xFF7C3AED)),
            WelfareCategoryItem("Social Assistance", Icons.Default.Elderly, strings.categoryPensions, 10, Color(0xFF0F766E)),
            WelfareCategoryItem("Food Security", Icons.Default.Fastfood, strings.categoryFood, 6, Color(0xFFE11D48)),
            WelfareCategoryItem("Women & Child", Icons.Default.FamilyRestroom, if (currentLanguage == AppLanguage.HINDI) "महिला एवं बाल" else "Women & Child", 14, Color(0xFFBE185D)),
            WelfareCategoryItem("Employment", Icons.Default.BusinessCenter, if (currentLanguage == AppLanguage.HINDI) "रोजगार व व्यवसाय" else "Employment & MSME", 9, Color(0xFF475569))
        )
    }

    // Mock Applications Data
    val myApplications = remember {
        listOf(
            SchemeApplicationStatus(
                applicationId = "APP-PMK-2024-89210",
                schemeId = "CEN-01",
                schemeTitle = "PM Kisan Samman Nidhi",
                department = "Ministry of Agriculture",
                currentStageIndex = 2,
                stages = listOf("Submitted", "Aadhaar & Land Verified", "District Sanction", "DBT Disbursal"),
                statusBadge = "Sanction In Progress",
                statusColor = Color(0xFFD97706),
                nextScheduleText = "Next ₹2,000 DBT release scheduled on 15 Oct 2024",
                lastUpdated = "Updated 2 days ago"
            ),
            SchemeApplicationStatus(
                applicationId = "APP-ABPMJAY-6631",
                schemeId = "CEN-02",
                schemeTitle = "Ayushman Bharat PM-JAY",
                department = "National Health Authority",
                currentStageIndex = 3,
                stages = listOf("Application", "e-KYC Completed", "Approved", "Golden Card Active"),
                statusBadge = "Approved & Active",
                statusColor = Color(0xFF15803D),
                nextScheduleText = "₹5 Lakh family cashless healthcare wallet active",
                lastUpdated = "Updated 1 week ago"
            )
        )
    }

    // Mock Vault Documents
    val myDocuments = remember {
        listOf(
            CitizenDocumentItem(
                id = "DOC-01",
                title = "Aadhaar Card",
                docNumberMasked = "•••• •••• 8291",
                issuer = "UIDAI • Govt of India",
                status = "Verified & DBT Linked",
                isVerified = true,
                icon = Icons.Default.Fingerprint
            ),
            CitizenDocumentItem(
                id = "DOC-02",
                title = "Ration Card (NFSA)",
                docNumberMasked = "RC-MH-994821",
                issuer = "Food & Civil Supplies Dept",
                status = "Active Beneficiary",
                isVerified = true,
                icon = Icons.Default.Fastfood
            ),
            CitizenDocumentItem(
                id = "DOC-03",
                title = "Income Certificate",
                docNumberMasked = "INC/2024/7721",
                issuer = "Revenue Office, Pune",
                status = "Valid till 31 Mar 2025",
                isVerified = true,
                icon = Icons.Default.Description
            ),
            CitizenDocumentItem(
                id = "DOC-04",
                title = "7/12 Land Record Extract",
                docNumberMasked = "SR-402/Plot-1B",
                issuer = "Bhulekh Portal, Maharashtra",
                status = "Digitally Signed",
                isVerified = true,
                icon = Icons.Default.Agriculture
            )
        )
    }

    // Mock Government Updates
    val governmentUpdates = remember {
        listOf(
            GovernmentUpdateItem(
                id = "UPD-01",
                title = "PM-KISAN 18th Installment: ₹20,000 Cr Direct Benefit Disbursed",
                summary = "Hon'ble Prime Minister released the 18th tranche directly to bank accounts of over 9.5 crore registered farmers across India.",
                authority = "Press Information Bureau (PIB)",
                date = "Yesterday, 4:30 PM",
                isUrgent = false,
                categoryTag = "DBT Announcement"
            ),
            GovernmentUpdateItem(
                id = "UPD-02",
                title = "National Scholarship Portal: Pre-Matric Application Extended",
                summary = "Last date for minority and SC/ST student pre-matric scholarship verification extended to October 31, 2024.",
                authority = "Ministry of Social Justice",
                date = "2 days ago",
                isUrgent = true,
                categoryTag = "Deadline Extension"
            ),
            GovernmentUpdateItem(
                id = "UPD-03",
                title = "Ayushman Arogya Mandirs Free Preventive Screening Camp",
                summary = "Over 45,000 centres conducting comprehensive health checkups for hypertension and diabetes free of cost.",
                authority = "Ministry of Health & Family Welfare",
                date = "3 days ago",
                isUrgent = false,
                categoryTag = "Health Camp"
            )
        )
    }

    // Mock Recently Viewed Items
    val recentlyViewedItems = remember {
        listOf(
            RecentlyViewedItem(
                schemeId = "CEN-01",
                title = "PM Kisan Samman Nidhi",
                category = "Agriculture",
                benefitHighlight = "₹6,000 / year via DBT",
                level = SchemeLevel.CENTRAL,
                viewedTimeAgo = "1 hour ago",
                icon = Icons.Default.Agriculture
            ),
            RecentlyViewedItem(
                schemeId = "CEN-02",
                title = "Ayushman Bharat PM-JAY",
                category = "Healthcare",
                benefitHighlight = "₹5 Lakh cashless health cover",
                level = SchemeLevel.CENTRAL,
                viewedTimeAgo = "Yesterday",
                icon = Icons.Default.HealthAndSafety
            ),
            RecentlyViewedItem(
                schemeId = "MH-01",
                title = "Mukhyamantri Majhi Ladki Bahin Yojana",
                category = "Women & Child",
                benefitHighlight = "₹1,500 monthly DBT",
                level = SchemeLevel.STATE,
                viewedTimeAgo = "2 days ago",
                icon = Icons.Default.FamilyRestroom
            )
        )
    }

    // Toggle saved scheme
    val toggleSavedScheme: (String) -> Unit = { id ->
        savedSchemeIds = if (savedSchemeIds.contains(id)) {
            savedSchemeIds - id
        } else {
            savedSchemeIds + id
        }
    }

    val isSearchOrFilterActive = searchQuery.isNotBlank() || selectedCategory != null

    // -------------------------------------------------------------
    // MAIN LAYOUT
    // -------------------------------------------------------------
    Scaffold(
        topBar = {
            Column {
                // National Tricolor Banner
                GovernmentBanner(language = currentLanguage, fontSizeMultiplier = fontSizeMultiplier)

                // App Bar with Citizen Greeting & Quick Actions
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
                        // 1. Namaste / Citizen Greeting & Avatar
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onOpenProfileSettings() }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(if (isGuest) Color(0xFFE2E8F0) else GovNavyContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isGuest) Icons.Default.Public else Icons.Default.Person,
                                    contentDescription = strings.profileTitle,
                                    tint = GovNavyPrimary,
                                    modifier = Modifier.size(26.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = if (isGuest) {
                                        strings.welcomeCitizen
                                    } else {
                                        val greeting = if (currentLanguage == AppLanguage.HINDI) "नमस्ते" else "Namaste"
                                        "$greeting, $citizenName"
                                    },
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GovNavyPrimary,
                                        fontSize = (15 * fontSizeMultiplier).sp
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                // Resident State Pill with quick change
                                Surface(
                                    color = Color(0xFFFFF7ED),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, Color(0xFFFFEDD5)),
                                    modifier = Modifier.clickable { onChangeState() }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.LocationOn,
                                            contentDescription = null,
                                            tint = GovSaffron,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = currentState,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = Color(0xFF9A3412),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            )
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "✎",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = GovNavyPrimary,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        // Right Actions: Font Toggle + Language Pill + Notification Bell + Profile
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // Font Size Toggle Pill (A / A+)
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (isLargeFont) GovNavyPrimary else Color(0xFFF1F5F9),
                                border = BorderStroke(1.dp, if (isLargeFont) GovNavyPrimary else Color(0xFFCBD5E1)),
                                modifier = Modifier.clickable { onFontScaleToggle() }
                            ) {
                                Text(
                                    text = if (isLargeFont) "A+" else "A",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isLargeFont) Color.White else GovNavyPrimary,
                                        fontSize = 11.sp
                                    )
                                )
                            }

                            // Quick Language Indicator Pill
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xFFF1F5F9),
                                border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                                modifier = Modifier.clickable { showLanguageDialogFromProfile = true }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Translate,
                                        contentDescription = "Language",
                                        tint = GovNavyPrimary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = currentLanguage.code.uppercase(),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = GovNavyPrimary,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }

                            // Notification Bell with Badge
                            IconButton(onClick = { showNotificationDialog = true }) {
                                BadgedBox(
                                    badge = {
                                        Badge(
                                            containerColor = GovSaffron,
                                            contentColor = Color.White
                                        ) {
                                            Text("${governmentUpdates.size}", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Notifications,
                                        contentDescription = strings.notificationTitle,
                                        tint = GovNavyPrimary
                                    )
                                }
                            }

                            // Profile Avatar Action
                            IconButton(onClick = { onOpenProfileSettings() }) {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = strings.profileTitle,
                                    tint = GovNavyPrimary
                                )
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            JanSaarthiBottomNav(
                selectedTab = 0,
                currentLanguage = currentLanguage,
                pendingApplications = 2,
                onNavigateHome = { /* Already on Home */ },
                onNavigateSchemes = onOpenCategories, // "in schmes it should be categaires"
                onNavigateApplications = onOpenApplications,
                onNavigateDocuments = onOpenDocuments,
                onNavigateProfile = onOpenProfileSettings
            )
        },
        modifier = modifier
            .fillMaxSize()
            .background(GovBackground)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Guest Notice (if in guest mode)
            if (isGuest) {
                item {
                    Surface(
                        color = Color(0xFFFFF8E1),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFFFFE082)),
                        modifier = Modifier.fillMaxWidth()
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
                                    text = strings.guestModeActive,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFB78103)
                                    )
                                )
                                Text(
                                    text = strings.guestModeSubtitle,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF6B4800),
                                        fontSize = 12.sp
                                    )
                                )
                            }
                            Button(
                                onClick = onLogout,
                                colors = ButtonDefaults.buttonColors(containerColor = GovNavyPrimary),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = strings.signInButton,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // SECTION 2: SEARCH GOVERNMENT SCHEMES
            // -------------------------------------------------------------
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI) "🔍 सरकारी योजनाएं खोजें" else "🔍 Search Government Schemes",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = GovNavyPrimary,
                                fontSize = (14 * fontSizeMultiplier).sp
                            )
                        )
                        TextButton(
                            onClick = { onOpenSearch(searchQuery) },
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) "विस्तृत खोज ↗" else "Global Search ↗",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = GovSaffron,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 54.dp),
                        placeholder = {
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI)
                                    "योजना, मंत्रालय या लाभ खोजें (उदा: किसान, आवास, छात्रवृत्ति)..."
                                else
                                    "Search 500+ schemes (e.g. Kisan, Awas, Health, Pension)...",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color(0xFF94A3B8),
                                    fontSize = (13 * fontSizeMultiplier).sp
                                )
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = GovNavyPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        trailingIcon = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Clear",
                                            tint = Color(0xFF64748B)
                                        )
                                    }
                                }
                                // Voice Search Mock (Highly accessible for senior citizens & rural users)
                                IconButton(onClick = {
                                    searchQuery = if (currentLanguage == AppLanguage.HINDI) "किसान सम्मान निधि" else "Kisan Samman"
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Mic,
                                        contentDescription = "Voice Search",
                                        tint = GovSaffron
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = GovNavyPrimary,
                            unfocusedBorderColor = GovBorder
                        )
                    )

                    // Quick Search Keywords / Tags
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val quickTags = listOf(
                            "🌾 " + (if (currentLanguage == AppLanguage.HINDI) "किसान" else "Farmers"),
                            "🏥 " + (if (currentLanguage == AppLanguage.HINDI) "स्वास्थ्य" else "Healthcare"),
                            "👵 " + (if (currentLanguage == AppLanguage.HINDI) "पेंशन" else "Pension"),
                            "🎓 " + (if (currentLanguage == AppLanguage.HINDI) "छात्रवृत्ति" else "Scholarship"),
                            "🏠 " + (if (currentLanguage == AppLanguage.HINDI) "आवास" else "Housing")
                        )

                        items(quickTags) { tag ->
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color.White,
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier.clickable {
                                    val cleaned = tag.substring(2).trim()
                                    searchQuery = if (searchQuery == cleaned) "" else cleaned
                                }
                            ) {
                                Text(
                                    text = tag,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = GovNavyPrimary,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // SECTION: LIVE SEARCH & CATEGORY FILTER RESULTS (WHEN ACTIVE)
            // -------------------------------------------------------------
            if (isSearchOrFilterActive) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI)
                                    "🎯 खोज व फ़िल्टर परिणाम (${filteredSchemes.size})"
                                else
                                    "🎯 Matched Schemes (${filteredSchemes.size})",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GovNavyPrimary,
                                    fontSize = (16 * fontSizeMultiplier).sp
                                )
                            )

                            TextButton(onClick = {
                                searchQuery = ""
                                selectedCategory = null
                            }) {
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) "फ़िल्टर हटाएं" else "Clear All",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = GovSaffron,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }

                        if (filteredSchemes.isEmpty()) {
                            Surface(
                                color = Color.White,
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SearchOff,
                                        contentDescription = null,
                                        tint = Color(0xFF94A3B8),
                                        modifier = Modifier.size(36.dp)
                                    )
                                    Text(
                                        text = if (currentLanguage == AppLanguage.HINDI)
                                            "कोई योजना नहीं मिली"
                                        else
                                            "No matching schemes found",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF64748B)
                                        )
                                    )
                                }
                            }
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                filteredSchemes.forEach { scheme ->
                                    val isSaved = savedSchemeIds.contains(scheme.id)
                                    SchemeDisplayCard(
                                        scheme = scheme,
                                        currentLanguage = currentLanguage,
                                        currentState = currentState,
                                        fontSizeMultiplier = fontSizeMultiplier,
                                        isSaved = isSaved,
                                        onToggleSave = { toggleSavedScheme(scheme.id) },
                                        onClick = { selectedSchemeForDetails = scheme }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // SECTION 3: "FIND SCHEMES FOR ME" (HERO CTA BANNER)
            // -------------------------------------------------------------
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenFindSchemesForMe() }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF0F172A), // Deep Slate
                                        Color(0xFF0A3871)  // Gov Navy
                                    )
                                )
                            )
                            .padding(18.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Surface(
                                        color = GovSaffron,
                                        shape = CircleShape,
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.AutoAwesome,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }

                                    Text(
                                        text = if (currentLanguage == AppLanguage.HINDI) "🎯 मेरे लिए योजनाएं खोजें" else "🎯 Find Schemes For Me",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.White,
                                            fontSize = (17 * fontSizeMultiplier).sp
                                        )
                                    )
                                }

                                Surface(
                                    color = Color(0x33FFFFFF),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = if (currentLanguage == AppLanguage.HINDI) "⚡ 2 मिनट जांच" else "⚡ 2 Min Check",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color(0xFFFDE68A),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }

                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI)
                                    "अपनी आयु, श्रेणी, आय और व्यवसाय अनुसार 100% सत्यापित केंद्र व राज्य सरकारी योजनाएं तुरंत प्राप्त करें।"
                                else
                                    "Answer simple profile questions to discover all Central & State welfare benefits you and your family are entitled to.",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color(0xFFE2E8F0),
                                    fontSize = (13 * fontSizeMultiplier).sp,
                                    lineHeight = 18.sp
                                )
                            )

                            // Quick Guarantee Badges
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    color = Color(0x22FFFFFF),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = null,
                                            tint = Color(0xFF86EFAC),
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "100% Private (DPDP Act)",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = Color(0xFFCBD5E1),
                                                fontSize = 10.sp
                                            )
                                        )
                                    }
                                }

                                Surface(
                                    color = Color(0x22FFFFFF),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Verified,
                                            contentDescription = null,
                                            tint = Color(0xFFFDE68A),
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Official NIC Catalog",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = Color(0xFFCBD5E1),
                                                fontSize = 10.sp
                                            )
                                        )
                                    }
                                }
                            }

                            // Prominent CTA Button (Min 52dp touch target for accessibility)
                            Button(
                                onClick = onOpenFindSchemesForMe,
                                colors = ButtonDefaults.buttonColors(containerColor = GovSaffron),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = if (currentLanguage == AppLanguage.HINDI)
                                            "मेरी पात्रता जांचें (Check Eligibility) →"
                                        else
                                            "Check My Scheme Eligibility →",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            fontSize = (14 * fontSizeMultiplier).sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // SECTION 4: BROWSE ALL CATEGORIES
            // -------------------------------------------------------------
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) "📁 सभी श्रेणियां ब्राउज़ करें" else "📁 Browse All Categories",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GovNavyPrimary,
                                    fontSize = (16 * fontSizeMultiplier).sp
                                )
                            )
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) "विषयानुसार सरकारी कल्याण योजनाएं" else "Explore welfare initiatives by domain",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF64748B),
                                    fontSize = 11.sp
                                )
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (selectedCategory != null) {
                                TextButton(onClick = { selectedCategory = null }) {
                                    Text(
                                        text = if (currentLanguage == AppLanguage.HINDI) "फ़िल्टर हटाएं" else "Clear Filter",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = GovSaffron,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                            TextButton(onClick = onOpenCategories) {
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) "सभी 15 श्रेणियां →" else "View All 15 →",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = GovNavyPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            TextButton(onClick = onOpenMinistries) {
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) "मंत्रालय 🏛️" else "Ministries 🏛️",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFFC2410C),
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }

                    // Horizontal Scrollable Category Cards with Clean Material 3 Design
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(categories) { cat ->
                            val isSelected = selectedCategory.equals(cat.id, ignoreCase = true)

                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) GovNavyPrimary else Color.White
                                ),
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) GovNavyPrimary else Color(0xFFE2E8F0)
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 3.dp else 1.dp),
                                modifier = Modifier
                                    .width(135.dp)
                                    .clickable {
                                        selectedCategory = if (isSelected) null else cat.id
                                    }
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Surface(
                                        color = if (isSelected) Color(0x33FFFFFF) else cat.color.copy(alpha = 0.12f),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.size(38.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = cat.icon,
                                                contentDescription = null,
                                                tint = if (isSelected) Color.White else cat.color,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                    }

                                    Text(
                                        text = cat.titleKey,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = if (isSelected) Color.White else GovTextPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = (12 * fontSizeMultiplier).sp
                                        ),
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Surface(
                                            color = if (isSelected) Color(0x22FFFFFF) else Color(0xFFF1F5F9),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = "${cat.count} " + (if (currentLanguage == AppLanguage.HINDI) "योजनाएं" else "Schemes"),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = if (isSelected) Color(0xFFCBD5E1) else Color(0xFF64748B),
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // SECTION 5: CENTRAL GOVERNMENT SCHEMES
            // -------------------------------------------------------------
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "🇮🇳",
                                    fontSize = 18.sp
                                )
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) "केंद्र सरकार की योजनाएं" else "Central Government Schemes",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GovNavyPrimary,
                                        fontSize = (16 * fontSizeMultiplier).sp
                                    )
                                )
                            }
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) "संपूर्ण भारत में सभी नागरिकों के लिए उपलब्ध" else "Nationwide Flagship Initiatives • 100% Central Funding",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF64748B),
                                    fontSize = 11.sp
                                )
                            )
                        }

                        Surface(
                            color = Color(0xFFEFF6FF),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "${centralSchemes.size} " + (if (currentLanguage == AppLanguage.HINDI) "योजनाएं" else "Schemes"),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = GovNavyPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    // Render Central Scheme Cards
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        centralSchemes.take(3).forEach { scheme ->
                            val isSaved = savedSchemeIds.contains(scheme.id)
                            SchemeDisplayCard(
                                scheme = scheme,
                                currentLanguage = currentLanguage,
                                currentState = currentState,
                                fontSizeMultiplier = fontSizeMultiplier,
                                isSaved = isSaved,
                                onToggleSave = { toggleSavedScheme(scheme.id) },
                                onClick = { selectedSchemeForDetails = scheme }
                            )
                        }

                        OutlinedButton(
                            onClick = onOpenCategories,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, GovNavyPrimary)
                        ) {
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) "📁 सभी योजना श्रेणियां देखें (15 श्रेणियां) →" else "📁 Browse All Scheme Categories (15 Domains) →",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GovNavyPrimary
                                )
                            )
                        }

                        OutlinedButton(
                            onClick = onOpenMinistries,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, Color(0xFFC2410C))
                        ) {
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) "🏛️ 15 केंद्रीय मंत्रालय व विभाग ब्राउज़ करें →" else "🏛️ Browse 15 Union Ministries & Departments →",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFC2410C)
                                )
                            )
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // QUICK CITIZEN ACCESS HUB (4 Clean Cards)
            // -------------------------------------------------------------
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI) "⚡ त्वरित नागरिक सेवाएं" else "⚡ Quick Citizen Services",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = GovNavyPrimary,
                            fontSize = (15 * fontSizeMultiplier).sp
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // 1. Categories
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onOpenCategories() }
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Surface(
                                    color = GovNavyContainer,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.GridView,
                                            contentDescription = null,
                                            tint = GovNavyPrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) "योजना श्रेणियां" else "Categories",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GovTextPrimary,
                                        fontSize = (12 * fontSizeMultiplier).sp
                                    )
                                )
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) "15 राष्ट्रीय श्रेणियां" else "15 Domains",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF64748B),
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }

                        // 2. Applications
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onOpenApplications() }
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Surface(
                                    color = GovSaffronLight,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.Assignment,
                                            contentDescription = null,
                                            tint = GovSaffron,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) "आवेदन ट्रैकर" else "Applications",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GovTextPrimary,
                                        fontSize = (12 * fontSizeMultiplier).sp
                                    )
                                )
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) "2 सक्रिय डीबीटी" else "2 Active DBT",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = GovGreen,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // 3. Vault Documents
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onOpenDocuments() }
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Surface(
                                    color = GovGreenLight,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.FolderOpen,
                                            contentDescription = null,
                                            tint = GovGreen,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) "दस्तावेज़ वॉल्ट" else "Doc Vault",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GovTextPrimary,
                                        fontSize = (12 * fontSizeMultiplier).sp
                                    )
                                )
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) "डिजीलॉकर सत्यापित" else "DigiLocker Synced",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF64748B),
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }

                        // 4. Saved Schemes
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onOpenSavedSchemes() }
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Surface(
                                    color = Color(0xFFFFF7ED),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Bookmark,
                                            contentDescription = null,
                                            tint = GovSaffron,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) "सहेजी गई योजनाएं" else "Saved Schemes",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GovTextPrimary,
                                        fontSize = (12 * fontSizeMultiplier).sp
                                    )
                                )
                                Text(
                                    text = "${savedSchemeIds.size} " + (if (currentLanguage == AppLanguage.HINDI) "योजनाएं" else "Bookmarked"),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF64748B),
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // SECTION 6: STATE / UT SCHEMES
            // -------------------------------------------------------------
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "🏛️",
                                    fontSize = 18.sp
                                )
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) "$currentState राज्य योजनाएं" else "$currentState State Schemes",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF9A3412),
                                        fontSize = (16 * fontSizeMultiplier).sp
                                    )
                                )
                            }
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI)
                                    "केवल $currentState के निवासियों के लिए विशेष योजनाएं"
                                else
                                    "Tailored welfare benefits for residents of $currentState",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF78350F),
                                    fontSize = 11.sp
                                )
                            )
                        }

                        // Change State button
                        Surface(
                            color = Color(0xFFFFF7ED),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFFFFEDD5)),
                            modifier = Modifier.clickable { onChangeState() }
                        ) {
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) "राज्य बदलें ✎" else "Change ✎",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFFC2410C),
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    // Render State Scheme Cards
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        stateSchemes.forEach { scheme ->
                            val isSaved = savedSchemeIds.contains(scheme.id)
                            SchemeDisplayCard(
                                scheme = scheme,
                                currentLanguage = currentLanguage,
                                currentState = currentState,
                                fontSizeMultiplier = fontSizeMultiplier,
                                isSaved = isSaved,
                                onToggleSave = { toggleSavedScheme(scheme.id) },
                                onClick = { selectedSchemeForDetails = scheme }
                            )
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // SECTION 7: RECENTLY VIEWED
            // -------------------------------------------------------------
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                tint = GovNavyPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) "हाल ही में देखी गई योजनाएं" else "Recently Viewed Schemes",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GovNavyPrimary,
                                    fontSize = (15 * fontSizeMultiplier).sp
                                )
                            )
                        }

                        Text(
                            text = "${recentlyViewedItems.size} " + (if (currentLanguage == AppLanguage.HINDI) "हालिया" else "Recent"),
                            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF64748B))
                        )
                    }

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(recentlyViewedItems) { item ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier
                                    .width(220.dp)
                                    .clickable {
                                        // Open matching scheme details
                                        val match = applicableSchemes.find { it.id == item.schemeId }
                                            ?: applicableSchemes.firstOrNull()
                                        selectedSchemeForDetails = match
                                    }
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            color = GovNavyContainer,
                                            shape = RoundedCornerShape(6.dp),
                                            modifier = Modifier.size(30.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = item.icon,
                                                    contentDescription = null,
                                                    tint = GovNavyPrimary,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }

                                        Text(
                                            text = item.viewedTimeAgo,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = Color(0xFF94A3B8),
                                                fontSize = 10.sp
                                            )
                                        )
                                    }

                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = GovTextPrimary,
                                            fontSize = (13 * fontSizeMultiplier).sp
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Text(
                                        text = item.benefitHighlight,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = GovGreen,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 11.sp
                                        )
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        Text(
                                            text = if (currentLanguage == AppLanguage.HINDI) "विवरण देखें →" else "Resume →",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = GovNavyPrimary,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // SECTION 8: SAVED SCHEMES
            // -------------------------------------------------------------
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bookmark,
                                contentDescription = null,
                                tint = GovSaffron,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) "सहेजी गई योजनाएं (Saved Schemes)" else "Saved Schemes",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GovNavyPrimary,
                                    fontSize = (15 * fontSizeMultiplier).sp
                                )
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Surface(
                                color = Color(0xFFFFF7ED),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "${savedSchemeIds.size} " + (if (currentLanguage == AppLanguage.HINDI) "सहेजी गई" else "Saved"),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFFC2410C),
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }

                            TextButton(
                                onClick = onOpenSavedSchemes,
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) "सूची देखें ↗" else "View All ↗",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = GovNavyPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }

                    val savedSchemes = remember(savedSchemeIds, applicableSchemes) {
                        applicableSchemes.filter { savedSchemeIds.contains(it.id) }
                    }

                    if (savedSchemes.isEmpty()) {
                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.BookmarkBorder,
                                    contentDescription = null,
                                    tint = Color(0xFF94A3B8),
                                    modifier = Modifier.size(36.dp)
                                )
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI)
                                        "कोई योजना सहेजी नहीं गई है"
                                    else
                                        "No saved schemes yet",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF64748B)
                                    )
                                )
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI)
                                        "किसी भी योजना कार्ड पर बुकमार्क (★) आइकन दबाकर उसे यहां सहेजें।"
                                    else
                                        "Tap the bookmark icon on any scheme card to save it for quick offline reference.",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF94A3B8),
                                        textAlign = TextAlign.Center
                                    )
                                )
                            }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            savedSchemes.forEach { scheme ->
                                Surface(
                                    color = Color.White,
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedSchemeForDetails = scheme }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Surface(
                                            color = GovNavyContainer,
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.size(38.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = scheme.icon,
                                                    contentDescription = null,
                                                    tint = GovNavyPrimary,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = scheme.getTitle(currentLanguage),
                                                style = MaterialTheme.typography.titleSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = GovTextPrimary,
                                                    fontSize = (13 * fontSizeMultiplier).sp
                                                ),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = scheme.getBenefit(currentLanguage),
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = GovGreen,
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontSize = 11.sp
                                                ),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }

                                        // Remove from Saved
                                        IconButton(onClick = { toggleSavedScheme(scheme.id) }) {
                                            Icon(
                                                imageVector = Icons.Default.Bookmark,
                                                contentDescription = "Unsave",
                                                tint = GovSaffron,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // SECTION 9: MY APPLICATIONS (LIVE MULTI-STAGE PROGRESS TRACKER)
            // -------------------------------------------------------------
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenApplications() },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Assignment,
                                contentDescription = null,
                                tint = GovNavyPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) "मेरे आवेदन (My Applications)" else "My Applications Tracker",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GovNavyPrimary,
                                    fontSize = (15 * fontSizeMultiplier).sp
                                )
                            )
                        }

                        Text(
                            text = "${myApplications.size} " + (if (currentLanguage == AppLanguage.HINDI) "सक्रिय" else "Active"),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = GovGreen,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        myApplications.forEach { app ->
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Column {
                                            Text(
                                                text = app.schemeTitle,
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = GovNavyPrimary,
                                                    fontSize = (14 * fontSizeMultiplier).sp
                                                )
                                            )
                                            Text(
                                                text = "ID: ${app.applicationId} • ${app.department}",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = Color(0xFF64748B),
                                                    fontSize = 11.sp
                                                )
                                            )
                                        }

                                        Surface(
                                            color = app.statusColor.copy(alpha = 0.12f),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = app.statusBadge,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = app.statusColor,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 10.sp
                                                )
                                            )
                                        }
                                    }

                                    // Stepper Progress Indicators
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        LinearProgressIndicator(
                                            progress = { (app.currentStageIndex + 1) / app.stages.size.toFloat() },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(6.dp)
                                                .clip(RoundedCornerShape(3.dp)),
                                            color = if (app.currentStageIndex == 3) GovGreen else GovSaffron,
                                            trackColor = Color(0xFFE2E8F0)
                                        )

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            app.stages.forEachIndexed { idx, stageName ->
                                                val isReached = idx <= app.currentStageIndex
                                                Text(
                                                    text = stageName,
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        color = if (isReached) GovNavyPrimary else Color(0xFF94A3B8),
                                                        fontWeight = if (isReached) FontWeight.Bold else FontWeight.Normal,
                                                        fontSize = 9.sp
                                                    )
                                                )
                                            }
                                        }
                                    }

                                    // Next Schedule Alert
                                    Surface(
                                        color = Color(0xFFF0FDF4),
                                        shape = RoundedCornerShape(8.dp),
                                        border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = GovGreen,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Text(
                                                text = app.nextScheduleText,
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = Color(0xFF166534),
                                                    fontWeight = FontWeight.Medium,
                                                    fontSize = 11.sp
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // SECTION 10: MY DOCUMENTS (DIGILOCKER / VAULT)
            // -------------------------------------------------------------
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.clickable { onOpenDocuments() }) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Folder,
                                    contentDescription = null,
                                    tint = GovNavyPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) "मेरे दस्तावेज़ (Digital Vault)" else "My Documents & Vault",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GovNavyPrimary,
                                        fontSize = (15 * fontSizeMultiplier).sp
                                    )
                                )
                            }
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI)
                                    "डिजीलॉकर से जुड़े सत्यापित दस्तावेज़ • 14 योजनाओं में सीधे मान्य"
                                else
                                    "DigiLocker verified documents • Auto-qualified for 14 schemes",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF64748B),
                                    fontSize = 11.sp
                                )
                            )
                        }

                        // Sync DigiLocker Button
                        OutlinedButton(
                            onClick = { showDigiLockerSyncDialog = true },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            border = BorderStroke(1.dp, GovNavyPrimary)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    tint = GovNavyPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "DigiLocker",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GovNavyPrimary,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }

                    // Document Vault Grid / Cards
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        myDocuments.forEach { doc ->
                            Surface(
                                color = Color.White,
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedDocForPreview = doc }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Surface(
                                        color = Color(0xFFF1F5F9),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = doc.icon,
                                                contentDescription = null,
                                                tint = GovNavyPrimary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = doc.title,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = GovTextPrimary,
                                                fontSize = (13 * fontSizeMultiplier).sp
                                            )
                                        )
                                        Text(
                                            text = "${doc.docNumberMasked} • ${doc.issuer}",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = Color(0xFF64748B),
                                                fontSize = 11.sp
                                            )
                                        )
                                    }

                                    Surface(
                                        color = Color(0xFFDCFCE7),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "✔ Verified",
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = Color(0xFF15803D),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // SECTION 11: GOVERNMENT UPDATES
            // -------------------------------------------------------------
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Announcement,
                                contentDescription = null,
                                tint = Color(0xFFB45309),
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) "📢 सरकारी अपडेट एवं घोषणाएं" else "📢 Government Updates & Bulletins",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GovNavyPrimary,
                                    fontSize = (15 * fontSizeMultiplier).sp
                                )
                            )
                        }

                        Surface(
                            color = Color(0xFFFFFBEB),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "PIB Official",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFFB45309),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        governmentUpdates.forEach { update ->
                            Surface(
                                color = if (update.isUrgent) Color(0xFFFFFBEB) else Color.White,
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(
                                    1.dp,
                                    if (update.isUrgent) Color(0xFFFDE68A) else Color(0xFFE2E8F0)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedUpdateForReading = update }
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            color = if (update.isUrgent) Color(0xFFFEF3C7) else Color(0xFFEFF6FF),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = update.categoryTag,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = if (update.isUrgent) Color(0xFF92400E) else GovNavyPrimary,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 9.sp
                                                )
                                            )
                                        }

                                        Text(
                                            text = update.date,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = Color(0xFF94A3B8),
                                                fontSize = 10.sp
                                            )
                                        )
                                    }

                                    Text(
                                        text = update.title,
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = GovTextPrimary,
                                            fontSize = (13 * fontSizeMultiplier).sp
                                        )
                                    )

                                    Text(
                                        text = update.summary,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Color(0xFF475569),
                                            fontSize = 11.sp,
                                            lineHeight = 15.sp
                                        ),
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Source: ${update.authority}",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = Color(0xFF94A3B8),
                                                fontSize = 10.sp
                                            )
                                        )

                                        Text(
                                            text = if (currentLanguage == AppLanguage.HINDI) "पूरा पढ़ें →" else "Read More →",
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
                }
            }

            // -------------------------------------------------------------
            // CITIZEN HELPDESK & OFFICIAL ATTRIBUTION FOOTER
            // -------------------------------------------------------------
            item {
                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                                contentDescription = null,
                                tint = GovNavyPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = strings.citizenHelpdesk,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GovNavyPrimary
                                )
                            )
                        }

                        Text(
                            text = strings.tollFreeNumber,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color(0xFF0F766E),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        )

                        Text(
                            text = strings.dpdpCompliant,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF64748B),
                                fontSize = 11.sp
                            )
                        )

                        Text(
                            text = strings.nicAttribution,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF94A3B8),
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // -------------------------------------------------------------
    // MODAL DIALOGS
    // -------------------------------------------------------------

    // 1. SCHEME DETAILS MODAL
    selectedSchemeForDetails?.let { scheme ->
        val isCentral = scheme.level == SchemeLevel.CENTRAL
        val isSaved = savedSchemeIds.contains(scheme.id)

        Dialog(onDismissRequest = { selectedSchemeForDetails = null }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.88f)
                    .padding(vertical = 12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(18.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = if (isCentral) GovNavyContainer else Color(0xFFFFEDD5),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = scheme.icon,
                                        contentDescription = null,
                                        tint = if (isCentral) GovNavyPrimary else Color(0xFFC2410C),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Surface(
                                        color = if (isCentral) Color(0xFFEFF6FF) else Color(0xFFFEF3C7),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = if (isCentral) "🇮🇳 ${strings.centralBadge}" else "🏛️ $currentState ${strings.stateBadge}",
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = if (isCentral) GovNavyPrimary else Color(0xFF92400E),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }

                                    if (scheme.isDbtLinked) {
                                        Surface(
                                            color = Color(0xFFDCFCE7),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = "⚡ DBT Active",
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp),
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = Color(0xFF166534),
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                        }
                                    }
                                }

                                Text(
                                    text = scheme.getTitle(currentLanguage),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GovNavyPrimary,
                                        fontSize = 16.sp
                                    )
                                )
                            }
                        }

                        IconButton(
                            onClick = { selectedSchemeForDetails = null },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = strings.closeButton,
                                tint = Color(0xFF64748B)
                            )
                        }
                    }

                    Text(
                        text = scheme.getDepartment(currentLanguage),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF64748B),
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    HorizontalDivider(
                        color = Color(0xFFE2E8F0),
                        modifier = Modifier.padding(vertical = 10.dp)
                    )

                    // Scrollable Sections: Eligibility, Benefits, Documents, Steps
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // 1. Eligibility
                        item {
                            Surface(
                                color = Color(0xFFF0FDF4),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = Color(0xFF15803D),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = strings.eligibilityHeader,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF15803D)
                                            )
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = scheme.details.eligibility.get(currentLanguage),
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = Color(0xFF166534),
                                            fontSize = 13.sp,
                                            lineHeight = 18.sp
                                        )
                                    )
                                }
                            }
                        }

                        // 2. Benefits
                        item {
                            Surface(
                                color = Color(0xFFEFF6FF),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.MonetizationOn,
                                            contentDescription = null,
                                            tint = GovNavyPrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = strings.benefitsHeader,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = GovNavyPrimary
                                            )
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = scheme.details.benefitsDetailed.get(currentLanguage),
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = Color(0xFF1E3A8A),
                                            fontSize = 13.sp,
                                            lineHeight = 18.sp
                                        )
                                    )
                                }
                            }
                        }

                        // 3. Required Documents
                        item {
                            Surface(
                                color = Color(0xFFFAF5FF),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color(0xFFE9D5FF)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Description,
                                            contentDescription = null,
                                            tint = Color(0xFF7E22CE),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = strings.documentsHeader,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF7E22CE)
                                            )
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    scheme.details.requiredDocuments.forEach { doc ->
                                        Row(
                                            modifier = Modifier.padding(vertical = 2.dp),
                                            verticalAlignment = Alignment.Top,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text("•", color = Color(0xFF6B21A8), fontWeight = FontWeight.Bold)
                                            Text(
                                                text = doc.get(currentLanguage),
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    color = Color(0xFF3B0764),
                                                    fontSize = 12.sp
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // 4. Application Steps
                        item {
                            Surface(
                                color = Color(0xFFFFF7ED),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color(0xFFFFEDD5)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.FormatListNumbered,
                                            contentDescription = null,
                                            tint = Color(0xFFC2410C),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = strings.stepsHeader,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFC2410C)
                                            )
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    scheme.details.applicationSteps.forEachIndexed { index, step ->
                                        Row(
                                            modifier = Modifier.padding(vertical = 3.dp),
                                            verticalAlignment = Alignment.Top,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Surface(
                                                color = Color(0xFFFDBA74),
                                                shape = CircleShape,
                                                modifier = Modifier.size(18.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Text(
                                                        text = "${index + 1}",
                                                        style = MaterialTheme.typography.labelSmall.copy(
                                                            color = Color(0xFF7C2D12),
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    )
                                                }
                                            }
                                            Text(
                                                text = step.get(currentLanguage),
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    color = Color(0xFF431407),
                                                    fontSize = 12.sp,
                                                    lineHeight = 16.sp
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Action Buttons (Bookmark Toggle + Apply Online + Close)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { toggleSavedScheme(scheme.id) },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = null,
                                tint = if (isSaved) GovSaffron else Color(0xFF64748B),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Button(
                            onClick = {
                                try {
                                    val browserIntent = Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse(scheme.details.officialPortalUrl)
                                    )
                                    context.startActivity(browserIntent)
                                } catch (e: Exception) {
                                    // Fallback handled safely
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GovNavyPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = strings.applyOnlineButton,
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // 2. DIGILOCKER SYNC DIALOG
    if (showDigiLockerSyncDialog) {
        AlertDialog(
            onDismissRequest = { showDigiLockerSyncDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    tint = GovNavyPrimary,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = "DigiLocker Digital Sync",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Text(
                    text = "Syncing your verified credentials directly with UIDAI and National DigiLocker repository. All 4 documents are up to date and verified.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF475569))
                )
            },
            confirmButton = {
                Button(
                    onClick = { showDigiLockerSyncDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = GovNavyPrimary)
                ) {
                    Text("OK (Synced)")
                }
            }
        )
    }

    // 3. DOCUMENT PREVIEW DIALOG
    selectedDocForPreview?.let { doc ->
        AlertDialog(
            onDismissRequest = { selectedDocForPreview = null },
            icon = {
                Icon(
                    imageVector = doc.icon,
                    contentDescription = null,
                    tint = GovNavyPrimary,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = doc.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Document Number: ${doc.docNumberMasked}", fontWeight = FontWeight.Bold)
                    Text("Issuing Authority: ${doc.issuer}")
                    Text("Status: ${doc.status}", color = GovGreen, fontWeight = FontWeight.SemiBold)
                    Text("Digital Signature: Valid & Authenticated by JanSaarthi", fontSize = 11.sp, color = Color(0xFF64748B))
                }
            },
            confirmButton = {
                Button(
                    onClick = { selectedDocForPreview = null },
                    colors = ButtonDefaults.buttonColors(containerColor = GovNavyPrimary)
                ) {
                    Text(strings.closeButton)
                }
            }
        )
    }

    // 4. GOVERNMENT UPDATE READING MODAL
    selectedUpdateForReading?.let { update ->
        AlertDialog(
            onDismissRequest = { selectedUpdateForReading = null },
            title = {
                Text(
                    text = update.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = GovNavyPrimary
                    )
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        color = Color(0xFFEFF6FF),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "${update.categoryTag} • ${update.date}",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = GovNavyPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Text(
                        text = update.summary,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF334155),
                            lineHeight = 20.sp
                        )
                    )
                    Text(
                        text = "Issued by: ${update.authority} (Government of India)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { selectedUpdateForReading = null },
                    colors = ButtonDefaults.buttonColors(containerColor = GovNavyPrimary)
                ) {
                    Text(strings.closeButton)
                }
            }
        )
    }

    // 5. PROFILE & SETTINGS MODAL
    if (showProfileModal) {
        Dialog(onDismissRequest = { showProfileModal = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = null,
                                tint = GovNavyPrimary,
                                modifier = Modifier.size(28.dp)
                            )
                            Text(
                                text = strings.profileTitle,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GovNavyPrimary,
                                    fontSize = 18.sp
                                )
                            )
                        }
                        IconButton(onClick = { showProfileModal = false }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = strings.closeButton,
                                tint = Color(0xFF64748B)
                            )
                        }
                    }

                    HorizontalDivider(color = Color(0xFFE2E8F0))

                    // Citizen Info
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "${strings.profileName}: $citizenName",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = GovTextPrimary
                            )
                        )
                        Text(
                            text = "Resident State: $currentState",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF475569))
                        )
                    }

                    // Language Option
                    Surface(
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
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
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = currentLanguage.nativeName,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GovNavyPrimary
                                    )
                                )
                            }
                            Button(
                                onClick = { showLanguageDialogFromProfile = true },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = GovNavyPrimary),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = strings.changeLanguageButton,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }

                    // Change State Option
                    Surface(
                        color = Color(0xFFFFF7ED),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFFFFEDD5)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = GovSaffron,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = currentState,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF7C2D12)
                                    )
                                )
                            }
                            OutlinedButton(
                                onClick = {
                                    showProfileModal = false
                                    onChangeState()
                                },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = strings.changeResidentStateButton,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFFC2410C),
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }

                    // Eligibility Profile Option
                    Surface(
                        color = Color(0xFFEFF6FF),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AssignmentInd,
                                    contentDescription = null,
                                    tint = GovNavyPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) "पात्रता प्रोफाइल" else "Eligibility Profile",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GovNavyPrimary
                                    )
                                )
                            }
                            Button(
                                onClick = {
                                    showProfileModal = false
                                    onOpenEligibilityProfile()
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = GovNavyPrimary),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) "संपादित करें" else "Edit",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }

                    // Sign Out
                    OutlinedButton(
                        onClick = {
                            showProfileModal = false
                            showLogoutDialog = true
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth(),
                        border = BorderStroke(1.dp, GovError),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = GovError)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(strings.signOutButton)
                    }
                }
            }
        }
    }

    // 6. LANGUAGE SELECTOR DIALOG
    if (showLanguageDialogFromProfile) {
        LanguageSelectorDialog(
            currentLanguage = currentLanguage,
            onSelectLanguage = {
                onSelectLanguage(it)
                showLanguageDialogFromProfile = false
            },
            onDismiss = { showLanguageDialogFromProfile = false }
        )
    }

    // 7. NOTIFICATIONS DIALOG
    if (showNotificationDialog) {
        Dialog(onDismissRequest = { showNotificationDialog = false }) {
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
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
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
                                imageVector = Icons.Default.Notifications,
                                contentDescription = null,
                                tint = GovNavyPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = strings.notificationTitle,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GovNavyPrimary,
                                    fontSize = 17.sp
                                )
                            )
                        }

                        IconButton(onClick = { showNotificationDialog = false }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = strings.closeButton,
                                tint = Color(0xFF64748B)
                            )
                        }
                    }

                    HorizontalDivider(color = Color(0xFFE2E8F0))

                    governmentUpdates.forEach { update ->
                        Surface(
                            color = Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = update.title,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GovNavyPrimary,
                                        fontSize = 12.sp
                                    )
                                )
                                Text(
                                    text = update.summary,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF64748B),
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = { showNotificationDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = GovNavyPrimary),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(strings.closeButton)
                        }
                    }
                }
            }
        }
    }

    // 8. LOGOUT CONFIRMATION DIALOG
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(
                    text = strings.confirmSignOutTitle,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Text(text = strings.confirmSignOutMessage)
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GovError)
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
}

// -------------------------------------------------------------
// REUSABLE SCHEME DISPLAY CARD (WITH BOOKMARK TOGGLE)
// -------------------------------------------------------------

@Composable
fun SchemeDisplayCard(
    scheme: WelfareScheme,
    currentLanguage: AppLanguage,
    currentState: String,
    fontSizeMultiplier: Float,
    isSaved: Boolean,
    onToggleSave: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isCentral = scheme.level == SchemeLevel.CENTRAL
    val strings = getJanSaarthiStrings(currentLanguage)

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(
            width = 1.dp,
            color = if (isCentral) Color(0xFFCBD5E1) else Color(0xFFFED7AA)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                color = if (isCentral) GovNavyContainer else Color(0xFFFFEDD5),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = scheme.icon,
                        contentDescription = null,
                        tint = if (isCentral) GovNavyPrimary else Color(0xFFC2410C),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        color = if (isCentral) Color(0xFFEFF6FF) else Color(0xFFFEF3C7),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = if (isCentral) "🇮🇳 ${strings.centralBadge}" else "🏛️ $currentState ${strings.stateBadge}",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isCentral) GovNavyPrimary else Color(0xFF92400E),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    if (scheme.isDbtLinked) {
                        Surface(
                            color = Color(0xFFDCFCE7),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = strings.dbtActive,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF166534),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = scheme.getTitle(currentLanguage),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = GovTextPrimary,
                        fontSize = (15 * fontSizeMultiplier).sp
                    )
                )

                Text(
                    text = scheme.getDepartment(currentLanguage),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF64748B),
                        fontSize = (11 * fontSizeMultiplier).sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = scheme.getBenefit(currentLanguage),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = GovGreen,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = (12 * fontSizeMultiplier).sp
                    )
                )
            }

            // Interactive Bookmark / Save Scheme Action
            IconButton(
                onClick = onToggleSave,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = if (isSaved) "Saved" else "Save Scheme",
                    tint = if (isSaved) GovSaffron else Color(0xFF94A3B8),
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
