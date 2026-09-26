package org.jansaarthi.app.ui.screens.applications

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import org.jansaarthi.app.data.model.AppLanguage
import org.jansaarthi.app.ui.components.AccessibilityBar
import org.jansaarthi.app.ui.components.GovernmentBanner
import org.jansaarthi.app.ui.components.JanSaarthiBottomNav
import org.jansaarthi.app.ui.localization.getJanSaarthiStrings
import org.jansaarthi.app.ui.theme.*

/**
 * ----------------------------------------------------------------------------
 * JanSaarthi My Applications Screen (Frontend UI Only)
 * ----------------------------------------------------------------------------
 * Dedicated citizen welfare application tracking dashboard.
 *
 * Mandated Tabs:
 * - All (सभी)
 * - Draft (प्रारूप)
 * - Applied (प्रस्तुत)
 * - Under Process (प्रक्रियाधीन)
 * - Approved (स्वीकृत)
 * - Rejected (अस्वीकृत)
 * - Completed (पूर्ण)
 *
 * Each Application Card shows:
 * - Scheme Name (Bilingual)
 * - Application Date
 * - Reference Number Placeholder (with 1-click copy action)
 * - Status Badge (High-contrast, color-coded)
 * - Last Updated timestamp & snippet
 * - View Details action (Opens rich multi-stage progress tracker)
 *
 * Frontend UI Only: Uses local mock data with interactive filtering.
 * Zero backend, database, Firebase, or external API code.
 */

// -------------------------------------------------------------
// ENUMS & MODELS FOR CITIZEN APPLICATIONS
// -------------------------------------------------------------

enum class ApplicationStatusType(
    val titleEn: String,
    val titleHi: String,
    val color: Color,
    val bgColor: Color,
    val icon: ImageVector
) {
    DRAFT("Draft", "प्रारूप", Color(0xFF475569), Color(0xFFF1F5F9), Icons.Default.EditNote),
    APPLIED("Applied", "आवेदित", Color(0xFF0369A1), Color(0xFFE0F2FE), Icons.AutoMirrored.Filled.Send),
    UNDER_PROCESS("Under Process", "प्रक्रियाधीन", Color(0xFFB45309), Color(0xFFFEF3C7), Icons.Default.HourglassTop),
    APPROVED("Approved", "स्वीकृत", Color(0xFF15803D), Color(0xFFDCFCE7), Icons.Default.CheckCircle),
    REJECTED("Rejected", "अस्वीकृत", Color(0xFFB91C1C), Color(0xFFFEE2E2), Icons.Default.Cancel),
    COMPLETED("Completed", "पूर्ण", Color(0xFF581C87), Color(0xFFF3E8FF), Icons.Default.TaskAlt)
}

enum class ApplicationTab(
    val titleEn: String,
    val titleHi: String,
    val statusFilter: ApplicationStatusType?
) {
    ALL("All", "सभी", null),
    DRAFT("Draft", "प्रारूप", ApplicationStatusType.DRAFT),
    APPLIED("Applied", "आवेदित", ApplicationStatusType.APPLIED),
    UNDER_PROCESS("Under Process", "प्रक्रियाधीन", ApplicationStatusType.UNDER_PROCESS),
    APPROVED("Approved", "स्वीकृत", ApplicationStatusType.APPROVED),
    REJECTED("Rejected", "अस्वीकृत", ApplicationStatusType.REJECTED),
    COMPLETED("Completed", "पूर्ण", ApplicationStatusType.COMPLETED)
}

data class CitizenApplicationItem(
    val id: String,
    val referenceNumber: String,
    val schemeNameEn: String,
    val schemeNameHi: String,
    val departmentEn: String,
    val departmentHi: String,
    val applicationDate: String,
    val lastUpdated: String,
    val status: ApplicationStatusType,
    val currentStageIndex: Int, // 0 to 3
    val stages: List<String>,
    val benefitAmountHighlight: String,
    val officialNodalRemark: String,
    val nextScheduledEvent: String,
    val applicantName: String = "Rajesh Kumar Sharma"
)

// -------------------------------------------------------------
// REALISTIC LOCAL MOCK DATASET
// -------------------------------------------------------------

val INITIAL_CITIZEN_APPLICATIONS_MOCK: List<CitizenApplicationItem> = listOf(
    CitizenApplicationItem(
        id = "APP-01",
        referenceNumber = "APP-PMK-2024-89210",
        schemeNameEn = "Pradhan Mantri Kisan Samman Nidhi (PM-KISAN)",
        schemeNameHi = "प्रधानमंत्री किसान सम्मान निधि",
        departmentEn = "Department of Agriculture & Farmers Welfare",
        departmentHi = "कृषि एवं किसान कल्याण विभाग (भारत सरकार)",
        applicationDate = "12-Jul-2024",
        lastUpdated = "2 days ago • District Sanction in progress",
        status = ApplicationStatusType.UNDER_PROCESS,
        currentStageIndex = 2,
        stages = listOf("Application Submitted", "Aadhaar & Land Verified", "District Sanction Approval", "DBT Disbursal"),
        benefitAmountHighlight = "₹6,000 / year (₹2,000 per tranche)",
        officialNodalRemark = "Taluka Agriculture Officer verified Survey No. 48/2 land records. District approval queue pending.",
        nextScheduledEvent = "Next 18th Installment release expected on 15-Oct-2024."
    ),
    CitizenApplicationItem(
        id = "APP-02",
        referenceNumber = "APP-ABPMJAY-6631",
        schemeNameEn = "Ayushman Bharat Pradhan Mantri Jan Arogya Yojana (PM-JAY)",
        schemeNameHi = "आयुष्मान भारत - प्रधानमंत्री जन आरोग्य योजना",
        departmentEn = "National Health Authority (NHA)",
        departmentHi = "राष्ट्रीय स्वास्थ्य प्राधिकरण (NHA)",
        applicationDate = "04-May-2024",
        lastUpdated = "1 week ago • Golden Ayushman Card Active",
        status = ApplicationStatusType.APPROVED,
        currentStageIndex = 3,
        stages = listOf("Online E-KYC Submitted", "Ration Card Family Matched", "State Health Agency Approval", "Ayushman Card Generated"),
        benefitAmountHighlight = "₹5,00,000 Cashless Family Health Cover",
        officialNodalRemark = "Biometric e-KYC authenticated successfully at Empanelled District Hospital. Card valid nationwide.",
        nextScheduledEvent = "Annual entitlement active till 31-Dec-2025."
    ),
    CitizenApplicationItem(
        id = "APP-03",
        referenceNumber = "MH-MLBY-2024-41092",
        schemeNameEn = "Mukhyamantri Majhi Ladki Bahin Yojana",
        schemeNameHi = "मुख्यमंत्री माझी लाड़की बहिन योजना",
        departmentEn = "Women & Child Development Department (Maharashtra)",
        departmentHi = "महिला एवं बाल विकास विभाग (महाराष्ट्र शासन)",
        applicationDate = "15-Jul-2024",
        lastUpdated = "Yesterday • ₹1,500 monthly DBT credited",
        status = ApplicationStatusType.COMPLETED,
        currentStageIndex = 3,
        stages = listOf("Form Submitted at Anganwadi", "Aadhaar Domicile Scrutiny", "District Collector Sanction", "Direct Benefit Disbursal"),
        benefitAmountHighlight = "₹1,500 / month (₹18,000 annually)",
        officialNodalRemark = "Aadhaar bank account seeded successfully with NPCI mapper. August and September disbursements completed.",
        nextScheduledEvent = "Next monthly payment scheduled on 05-Oct-2024."
    ),
    CitizenApplicationItem(
        id = "APP-04",
        referenceNumber = "APP-PMAYG-2024-1189",
        schemeNameEn = "Pradhan Mantri Awas Yojana - Gramin (PMAY-G)",
        schemeNameHi = "प्रधानमंत्री आवास योजना - ग्रामीण",
        departmentEn = "Ministry of Rural Development",
        departmentHi = "ग्रामीण विकास मंत्रालय (भारत सरकार)",
        applicationDate = "18-Aug-2024",
        lastUpdated = "4 days ago • Gram Sabha geo-tagging queued",
        status = ApplicationStatusType.APPLIED,
        currentStageIndex = 1,
        stages = listOf("Awaas+ Registration", "Gram Panchayat Geo-tagging", "Block Sanction Order", "Stage-wise Construction Grants"),
        benefitAmountHighlight = "₹1,20,000 Housing Subsidy + 90 Days MGNREGA",
        officialNodalRemark = "Application queued for Village Gram Sevak on-site geo-tagging inspection.",
        nextScheduledEvent = "Geo-tagging inspection scheduled for next week."
    ),
    CitizenApplicationItem(
        id = "APP-05",
        referenceNumber = "DRAFT-NSP-2024-902",
        schemeNameEn = "National Post-Matric Scholarship Scheme",
        schemeNameHi = "राष्ट्रीय पोस्ट-मैट्रिक छात्रवृत्ति योजना",
        departmentEn = "Ministry of Social Justice & Empowerment",
        departmentHi = "सामाजिक न्याय एवं अधिकारिता मंत्रालय",
        applicationDate = "20-Aug-2024",
        lastUpdated = "5 days ago • College fee receipt pending",
        status = ApplicationStatusType.DRAFT,
        currentStageIndex = 0,
        stages = listOf("Draft In Progress", "Institute Verification", "District Nodal Approval", "PFMS Direct Transfer"),
        benefitAmountHighlight = "100% Tuition Fee Reimbursement + ₹10,000 Allowance",
        officialNodalRemark = "Draft saved locally. Please attach current academic year fee receipt and submit before deadline.",
        nextScheduledEvent = "Application portal closes on 31-Oct-2024."
    ),
    CitizenApplicationItem(
        id = "APP-06",
        referenceNumber = "MH-NSMN-2024-3011",
        schemeNameEn = "Namo Shetkari Mahasanman Nidhi Yojana",
        schemeNameHi = "नमो शेतकरी महासन्मान निधी योजना",
        departmentEn = "Department of Agriculture (Govt of Maharashtra)",
        departmentHi = "कृषि विभाग (महाराष्ट्र शासन)",
        applicationDate = "01-Jun-2024",
        lastUpdated = "3 days ago • Matched with PM-KISAN beneficiary register",
        status = ApplicationStatusType.APPROVED,
        currentStageIndex = 3,
        stages = listOf("Farmer Linkage", "MahaDBT Validation", "State Treasury Sanction", "DBT Credit Active"),
        benefitAmountHighlight = "₹6,000 / year supplementary state grant",
        officialNodalRemark = "Automatic linkage verified with central PM-KISAN database. Disbursal synchronized with central tranches.",
        nextScheduledEvent = "Next installment release aligned with PM-KISAN 18th release."
    ),
    CitizenApplicationItem(
        id = "APP-07",
        referenceNumber = "APP-SURYA-2024-771",
        schemeNameEn = "PM Surya Ghar: Muft Bijli Yojana (Solar Rooftop)",
        schemeNameHi = "पीएम सूर्य घर: मुफ़्त बिजली योजना",
        departmentEn = "Ministry of New & Renewable Energy",
        departmentHi = "नवीन और नवीकरणीय ऊर्जा मंत्रालय",
        applicationDate = "10-Jun-2024",
        lastUpdated = "10 days ago • Feasibility inspection rejected",
        status = ApplicationStatusType.REJECTED,
        currentStageIndex = 1,
        stages = listOf("Consumer Application", "Discom Technical Feasibility", "Vendor Installation", "CFA Subsidy Disbursal"),
        benefitAmountHighlight = "Up to ₹78,000 Central Rooftop Subsidy",
        officialNodalRemark = "Discom technical officer cited roof shading constraints and incompatible sanctioned load. Appeal can be lodged within 30 days.",
        nextScheduledEvent = "Right to appeal before Superintending Engineer active till 15-Oct-2024."
    ),
    CitizenApplicationItem(
        id = "APP-08",
        referenceNumber = "UT-DTC-PINK-2024-88",
        schemeNameEn = "Delhi Free Bus Travel Scheme for Women (Pink Ticket)",
        schemeNameHi = "दिल्ली मुफ़्त बस यात्रा योजना (गुलाबी टिकट)",
        departmentEn = "Transport Department (Govt of NCT of Delhi)",
        departmentHi = "परिवहन विभाग (दिल्ली सरकार)",
        applicationDate = "01-Jan-2024",
        lastUpdated = "Active • Universal single-journey passes",
        status = ApplicationStatusType.COMPLETED,
        currentStageIndex = 3,
        stages = listOf("Universal Entitlement", "On-board Conductor Request", "Pink Pass Issued", "Free Commute Completed"),
        benefitAmountHighlight = "100% Free Public Bus Travel across Delhi NCT",
        officialNodalRemark = "Universal social protection pass without income ceiling. Valid across all DTC and Cluster buses.",
        nextScheduledEvent = "Continuous statutory welfare service."
    )
)

// -------------------------------------------------------------
// MAIN MY APPLICATIONS COMPOSABLE
// -------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApplicationsScreen(
    currentLanguage: AppLanguage = AppLanguage.HINDI,
    onSelectLanguage: (AppLanguage) -> Unit = {},
    isLargeFont: Boolean = false,
    onFontScaleToggle: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigateHome: () -> Unit = {},
    onNavigateSchemes: () -> Unit = {},
    onNavigateDocuments: () -> Unit = {},
    onNavigateProfile: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val strings = getJanSaarthiStrings(currentLanguage)
    val fontSizeMultiplier = if (isLargeFont) 1.15f else 1.0f
    val clipboardManager = LocalClipboardManager.current

    // -------------------------------------------------------------
    // LOCAL REACTIVE STATE (Zero backend / Mock only)
    // -------------------------------------------------------------
    var selectedTab by remember { mutableStateOf(ApplicationTab.ALL) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedApplicationForDetails by remember { mutableStateOf<CitizenApplicationItem?>(null) }
    var showCopyFeedbackToast by remember { mutableStateOf<String?>(null) }

    // Derived Statistics
    val allApplications = INITIAL_CITIZEN_APPLICATIONS_MOCK
    val totalCount = allApplications.size
    val underProcessCount = remember { allApplications.count { it.status == ApplicationStatusType.UNDER_PROCESS } }
    val approvedCount = remember { allApplications.count { it.status == ApplicationStatusType.APPROVED } }
    val completedCount = remember { allApplications.count { it.status == ApplicationStatusType.COMPLETED } }
    val draftCount = remember { allApplications.count { it.status == ApplicationStatusType.DRAFT } }

    // Filtered Applications List
    val filteredApplications = remember(selectedTab, searchQuery) {
        allApplications.filter { app ->
            // Tab filtering
            val matchesTab = selectedTab.statusFilter == null || app.status == selectedTab.statusFilter

            // Search query filtering
            val matchesSearch = if (searchQuery.isBlank()) {
                true
            } else {
                val q = searchQuery.trim().lowercase()
                app.schemeNameEn.lowercase().contains(q) ||
                        app.schemeNameHi.lowercase().contains(q) ||
                        app.referenceNumber.lowercase().contains(q) ||
                        app.departmentEn.lowercase().contains(q) ||
                        app.departmentHi.lowercase().contains(q)
            }

            matchesTab && matchesSearch
        }
    }

    Scaffold(
        bottomBar = {
            JanSaarthiBottomNav(
                selectedTab = 2,
                currentLanguage = currentLanguage,
                isEasyMode = isLargeFont,
                onNavigateHome = onNavigateHome,
                onNavigateSchemes = onNavigateSchemes,
                onNavigateApplications = { /* already here */ },
                onNavigateDocuments = onNavigateDocuments,
                onNavigateProfile = onNavigateProfile
            )
        },
        topBar = {
            Column {
                // National Identity Banner
                GovernmentBanner(language = currentLanguage, fontSizeMultiplier = fontSizeMultiplier)

                // Top Bar
                Surface(
                    color = Color.White,
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            IconButton(onClick = onNavigateBack) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = strings.navBack,
                                    tint = GovNavyPrimary
                                )
                            }

                            Column {
                                Text(
                                    text = strings.navApplications,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GovNavyPrimary,
                                        fontSize = (16 * fontSizeMultiplier).sp
                                    )
                                )
                                Text(
                                    text = "$totalCount ${strings.allSchemesLabel} • $underProcessCount ${strings.filterStatusUnderProcess}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF64748B),
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        // Help / Status Guide Icon Button
                        IconButton(onClick = {
                            showCopyFeedbackToast = strings.aboutJanSaarthiDesc
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Help,
                                contentDescription = strings.helpTitle,
                                tint = GovNavyPrimary
                            )
                        }
                    }
                }
            }
        },
        modifier = modifier
            .fillMaxSize()
            .background(GovBackground)
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(
                start = 14.dp,
                end = 14.dp,
                top = innerPadding.calculateTopPadding() + 8.dp,
                bottom = innerPadding.calculateBottomPadding() + 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // -------------------------------------------------------------
            // ACCESSIBILITY CONTROLS
            // -------------------------------------------------------------
            item {
                AccessibilityBar(
                    currentLanguage = currentLanguage,
                    onSelectLanguage = onSelectLanguage,
                    isLargeFont = isLargeFont,
                    onFontScaleToggle = onFontScaleToggle
                )
            }

            // -------------------------------------------------------------
            // SUMMARY METRICS CAROUSEL CARD
            // -------------------------------------------------------------
            item {
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
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) "आवेदन अवलोकन (Applications Tracker)" else "Applications Overview",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GovNavyPrimary,
                                    fontSize = (14 * fontSizeMultiplier).sp
                                )
                            )

                            Surface(
                                color = Color(0xFFEFF6FF),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) "लाइव ट्रैकर" else "Live Tracking",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = GovNavyPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.5.sp
                                    )
                                )
                            }
                        }

                        // 4 Metric Badges
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Under Process
                            Surface(
                                color = Color(0xFFFEF3C7),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedTab = ApplicationTab.UNDER_PROCESS }
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "$underProcessCount",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFB45309)
                                        )
                                    )
                                    Text(
                                        text = if (currentLanguage == AppLanguage.HINDI) "प्रक्रियाधीन" else "Process",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color(0xFF92400E),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }
                            }

                            // Approved
                            Surface(
                                color = Color(0xFFDCFCE7),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedTab = ApplicationTab.APPROVED }
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "$approvedCount",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF15803D)
                                        )
                                    )
                                    Text(
                                        text = if (currentLanguage == AppLanguage.HINDI) "स्वीकृत" else "Approved",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color(0xFF166534),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }
                            }

                            // Completed
                            Surface(
                                color = Color(0xFFF3E8FF),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedTab = ApplicationTab.COMPLETED }
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "$completedCount",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF7E22CE)
                                        )
                                    )
                                    Text(
                                        text = if (currentLanguage == AppLanguage.HINDI) "पूर्ण (DBT)" else "Completed",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color(0xFF581C87),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }
                            }

                            // Draft
                            Surface(
                                color = Color(0xFFF1F5F9),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedTab = ApplicationTab.DRAFT }
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "$draftCount",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF475569)
                                        )
                                    )
                                    Text(
                                        text = if (currentLanguage == AppLanguage.HINDI) "प्रारूप" else "Drafts",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color(0xFF334155),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // SEARCH BAR & THE 7 MANDATED TABS
            // -------------------------------------------------------------
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        placeholder = {
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI)
                                    "योजना नाम या संदर्भ संख्या खोजें..."
                                else
                                    "Search scheme name or reference number...",
                                fontSize = 13.sp,
                                color = Color(0xFF94A3B8)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = GovNavyPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear",
                                        tint = Color(0xFF64748B),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = GovNavyPrimary,
                            unfocusedBorderColor = Color(0xFFE2E8F0)
                        )
                    )

                    // 7 Mandated Tabs: All, Draft, Applied, Under Process, Approved, Rejected, Completed
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(ApplicationTab.values()) { tab ->
                            val isSelected = selectedTab == tab
                            val count = when (tab) {
                                ApplicationTab.ALL -> allApplications.size
                                ApplicationTab.DRAFT -> allApplications.count { it.status == ApplicationStatusType.DRAFT }
                                ApplicationTab.APPLIED -> allApplications.count { it.status == ApplicationStatusType.APPLIED }
                                ApplicationTab.UNDER_PROCESS -> allApplications.count { it.status == ApplicationStatusType.UNDER_PROCESS }
                                ApplicationTab.APPROVED -> allApplications.count { it.status == ApplicationStatusType.APPROVED }
                                ApplicationTab.REJECTED -> allApplications.count { it.status == ApplicationStatusType.REJECTED }
                                ApplicationTab.COMPLETED -> allApplications.count { it.status == ApplicationStatusType.COMPLETED }
                            }

                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedTab = tab },
                                label = {
                                    Text(
                                        text = "${if (currentLanguage == AppLanguage.HINDI) tab.titleHi else tab.titleEn} ($count)",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 11.5.sp
                                        )
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GovNavyPrimary,
                                    selectedLabelColor = Color.White,
                                    containerColor = Color.White,
                                    labelColor = Color(0xFF334155)
                                ),
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) GovNavyPrimary else Color(0xFFE2E8F0)
                                )
                            )
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // APPLICATION CARDS LIST
            // -------------------------------------------------------------
            if (filteredApplications.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SearchOff,
                                contentDescription = null,
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI)
                                    "कोई आवेदन नहीं मिला"
                                else
                                    "No Applications Found",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF475569)
                                )
                            )
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI)
                                    "कृपया अन्य फ़िल्टर टैब चुनें।"
                                else
                                    "No applications match the current tab filter.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF64748B),
                                    textAlign = TextAlign.Center
                                )
                            )
                        }
                    }
                }
            } else {
                items(filteredApplications, key = { it.id }) { appItem ->
                    ApplicationCard(
                        item = appItem,
                        currentLanguage = currentLanguage,
                        fontSizeMultiplier = fontSizeMultiplier,
                        onCopyReference = {
                            clipboardManager.setText(AnnotatedString(appItem.referenceNumber))
                            showCopyFeedbackToast = if (currentLanguage == AppLanguage.HINDI)
                                "संदर्भ संख्या ${appItem.referenceNumber} कॉपी की गई!"
                            else
                                "Reference ${appItem.referenceNumber} copied to clipboard!"
                        },
                        onViewDetails = { selectedApplicationForDetails = appItem }
                    )
                }
            }
        }
    }

    // -------------------------------------------------------------
    // MODAL DIALOG: APPLICATION DETAILS & MULTI-STAGE PROGRESS
    // -------------------------------------------------------------
    selectedApplicationForDetails?.let { appItem ->
        Dialog(onDismissRequest = { selectedApplicationForDetails = null }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                tonalElevation = 6.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                color = appItem.status.bgColor,
                                shape = CircleShape,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = appItem.status.icon,
                                        contentDescription = null,
                                        tint = appItem.status.color,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Column {
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) appItem.schemeNameHi else appItem.schemeNameEn,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GovNavyPrimary,
                                        fontSize = 14.5.sp
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "Ref: ${appItem.referenceNumber}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF64748B),
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        IconButton(onClick = { selectedApplicationForDetails = null }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF64748B))
                        }
                    }

                    HorizontalDivider(color = Color(0xFFE2E8F0))

                    // Multi-stage Progress Stepper
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI) "आवेदन प्रगति चरण:" else "Application Progress Stages:",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        appItem.stages.forEachIndexed { index, stageName ->
                            val isCompleted = index <= appItem.currentStageIndex
                            val isCurrent = index == appItem.currentStageIndex

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    color = if (isCompleted) appItem.status.color else Color(0xFFE2E8F0),
                                    shape = CircleShape,
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        if (isCompleted) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(12.dp)
                                            )
                                        } else {
                                            Text(
                                                text = "${index + 1}",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = Color(0xFF64748B),
                                                    fontSize = 10.sp
                                                )
                                            )
                                        }
                                    }
                                }

                                Text(
                                    text = stageName,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (isCurrent) GovNavyPrimary else if (isCompleted) Color(0xFF334155) else Color(0xFF94A3B8),
                                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }
                    }

                    // Key Information Sheet
                    Surface(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) "आवेदन तिथि:" else "Applied On:",
                                    style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF64748B))
                                )
                                Text(
                                    text = appItem.applicationDate,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E293B)
                                    )
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) "अंतिम अपडेट:" else "Last Updated:",
                                    style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF64748B))
                                )
                                Text(
                                    text = appItem.lastUpdated,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF1E293B),
                                        fontSize = 11.sp
                                    )
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) "लाभ राशि:" else "Benefit Entitlement:",
                                    style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF64748B))
                                )
                                Text(
                                    text = appItem.benefitAmountHighlight,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF15803D),
                                        fontSize = 11.5.sp
                                    )
                                )
                            }
                        }
                    }

                    // Official Nodal Officer Remarks
                    Surface(
                        color = Color(0xFFEFF6FF),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) "विभागीय टिप्पणी (Official Remarks):" else "Official Nodal Remarks:",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GovNavyPrimary
                                )
                            )
                            Text(
                                text = appItem.officialNodalRemark,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF334155),
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            )
                        }
                    }

                    // Next Event / Action
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Event,
                            contentDescription = null,
                            tint = GovSaffron,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = appItem.nextScheduledEvent,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF64748B),
                                fontSize = 11.sp
                            )
                        )
                    }

                    Button(
                        onClick = { selectedApplicationForDetails = null },
                        colors = ButtonDefaults.buttonColors(containerColor = GovNavyPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI) "बंद करें" else "Close Tracker",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }

    // -------------------------------------------------------------
    // TOAST / FEEDBACK MESSAGE
    // -------------------------------------------------------------
    showCopyFeedbackToast?.let { msg ->
        LaunchedEffect(msg) {
            kotlinx.coroutines.delay(2200)
            showCopyFeedbackToast = null
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Surface(
                color = Color(0xFF0F172A),
                shape = RoundedCornerShape(10.dp),
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color(0xFF60A5FA),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = msg,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// INDIVIDUAL APPLICATION CARD COMPONENT
// -------------------------------------------------------------

@Composable
fun ApplicationCard(
    item: CitizenApplicationItem,
    currentLanguage: AppLanguage,
    fontSizeMultiplier: Float,
    onCopyReference: () -> Unit,
    onViewDetails: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Top Row: Scheme Name & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI) item.schemeNameHi else item.schemeNameEn,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = GovNavyPrimary,
                            fontSize = (15 * fontSizeMultiplier).sp,
                            lineHeight = 20.sp
                        )
                    )
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI) item.schemeNameEn else item.departmentHi,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF64748B),
                            fontSize = 11.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Status Badge
                Surface(
                    color = item.status.bgColor,
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, item.status.color.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = item.status.icon,
                            contentDescription = null,
                            tint = item.status.color,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI) item.status.titleHi else item.status.titleEn,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = item.status.color,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.5.sp
                            )
                        )
                    }
                }
            }

            // Reference Number Row with 1-click Copy Action
            Surface(
                color = Color(0xFFF8FAFC),
                shape = RoundedCornerShape(6.dp),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCopyReference() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tag,
                            contentDescription = null,
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Ref: ${item.referenceNumber}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF1E293B),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.5.sp
                            )
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy Reference",
                        tint = GovNavyPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            // Application Date & Last Updated
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Application Date
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI)
                            "आवेदन: ${item.applicationDate}"
                        else
                            "Applied: ${item.applicationDate}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF475569),
                            fontSize = 11.sp
                        )
                    )
                }

                // Last Updated
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Update,
                        contentDescription = null,
                        tint = GovSaffron,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = item.lastUpdated,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF64748B),
                            fontSize = 10.5.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            HorizontalDivider(color = Color(0xFFF1F5F9))

            // View Details Button
            Button(
                onClick = onViewDetails,
                colors = ButtonDefaults.buttonColors(containerColor = GovNavyPrimary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp),
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI) "विवरण देखें (View Details) →" else "View Details →",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }
        }
    }
}
