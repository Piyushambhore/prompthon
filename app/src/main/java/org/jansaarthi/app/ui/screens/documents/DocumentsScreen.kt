package org.jansaarthi.app.ui.screens.documents

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
import androidx.compose.material.icons.automirrored.filled.Accessible
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import org.jansaarthi.app.ui.theme.*

/**
 * ----------------------------------------------------------------------------
 * JanSaarthi Complete Documents Section Screen (Frontend UI Only)
 * ----------------------------------------------------------------------------
 * Dedicated citizen document vault & eligibility credential manager.
 *
 * Core Sections & Capabilities:
 * - My Documents: Digital credential vault with DigiLocker sync
 * - Required Documents: Mandatory government qualification certificates
 * - Ready vs Missing: Real-time filtering, dynamic readiness score, and progress indicators
 * - Document Cards with:
 *     1. Document Name (Bilingual)
 *     2. Required / Optional badge
 *     3. Ready / Missing status badge
 *     4. View button (Opens simulated verified document credential preview)
 *     5. Add button (Simulated DigiLocker fetch / local upload to flip state)
 *
 * Frontend UI Only: Uses reactive local mock state with instant interactive updates.
 * Zero backend, database, Firebase, or real file storage.
 */

// -------------------------------------------------------------
// DATA MODELS FOR CITIZEN WELFARE DOCUMENTS
// -------------------------------------------------------------

enum class DocumentRequirementType(val labelEn: String, val labelHi: String) {
    REQUIRED("Required", "अनिवार्य"),
    OPTIONAL("Optional", "वैकल्पिक")
}

enum class DocumentStatus(val labelEn: String, val labelHi: String, val color: Color, val bgColor: Color) {
    READY("Ready", "तैयार", Color(0xFF15803D), Color(0xFFDCFCE7)),
    MISSING("Missing", "अनुपलब्ध", Color(0xFFB91C1C), Color(0xFFFEE2E2))
}

data class CitizenDocumentItem(
    val id: String,
    val nameEn: String,
    val nameHi: String,
    val requirementType: DocumentRequirementType,
    val status: DocumentStatus,
    val icon: ImageVector,
    val docNumberMasked: String?,
    val issuingAuthorityEn: String,
    val issuingAuthorityHi: String,
    val verifiedSource: String, // e.g., "DigiLocker Verified", "MahaBhulekh", "UIDAI"
    val issueDate: String?,
    val validityDate: String?,
    val usedInSchemesCount: Int,
    val descriptionEn: String,
    val descriptionHi: String
)

// -------------------------------------------------------------
// REALISTIC INITIAL MOCK DATASET
// -------------------------------------------------------------

val INITIAL_CITIZEN_DOCUMENTS_MOCK: List<CitizenDocumentItem> = listOf(
    CitizenDocumentItem(
        id = "DOC-01",
        nameEn = "Aadhaar Card",
        nameHi = "आधार कार्ड",
        requirementType = DocumentRequirementType.REQUIRED,
        status = DocumentStatus.READY,
        icon = Icons.Default.Badge,
        docNumberMasked = "XXXX-XXXX-4819",
        issuingAuthorityEn = "Unique Identification Authority of India (UIDAI)",
        issuingAuthorityHi = "भारतीय विशिष्ट पहचान प्राधिकरण (UIDAI)",
        verifiedSource = "DigiLocker Verified",
        issueDate = "12-Aug-2015",
        validityDate = "Lifelong / Active",
        usedInSchemesCount = 28,
        descriptionEn = "Primary national biometric identity required for Aadhaar-based DBT direct benefit transfers.",
        descriptionHi = "प्रत्यक्ष लाभ अंतरण (DBT) एवं सभी सरकारी योजनाओं हेतु प्राथमिक राष्ट्रीय पहचान पत्र।"
    ),
    CitizenDocumentItem(
        id = "DOC-02",
        nameEn = "Income Certificate",
        nameHi = "आय प्रमाण पत्र",
        requirementType = DocumentRequirementType.REQUIRED,
        status = DocumentStatus.READY,
        icon = Icons.Default.MonetizationOn,
        docNumberMasked = "INC/2024/78219",
        issuingAuthorityEn = "Revenue Department / Office of Tehsildar",
        issuingAuthorityHi = "राजस्व विभाग / तहसीलदार कार्यालय",
        verifiedSource = "State e-District Portal",
        issueDate = "15-May-2024",
        validityDate = "Valid till 31-Mar-2025",
        usedInSchemesCount = 19,
        descriptionEn = "Certifies combined family annual income (< ₹2.5 Lakhs) for economic qualification.",
        descriptionHi = "पारिवारिक वार्षिक आय प्रमाणित करने वाला अधिकृत दस्तावेज़ (वार्षिक आय ₹2.5 लाख से कम)।"
    ),
    CitizenDocumentItem(
        id = "DOC-03",
        nameEn = "Domicile / Residence Certificate",
        nameHi = "अधिवास प्रमाण पत्र (Domicile)",
        requirementType = DocumentRequirementType.REQUIRED,
        status = DocumentStatus.MISSING,
        icon = Icons.Default.HomeWork,
        docNumberMasked = null,
        issuingAuthorityEn = "Sub-Divisional Magistrate (SDM) / Tehsildar",
        issuingAuthorityHi = "उप-विभागीय दंडाधिकारी (SDM) / तहसीलदार",
        verifiedSource = "Pending Verification",
        issueDate = null,
        validityDate = null,
        usedInSchemesCount = 14,
        descriptionEn = "Confirms permanent residence in the state; required for state-sponsored welfare grants.",
        descriptionHi = "राज्य के स्थायी निवासी होने का प्रमाण; राज्य सरकार की योजनाओं हेतु आवश्यक।"
    ),
    CitizenDocumentItem(
        id = "DOC-04",
        nameEn = "7/12 Land Record Extract (RoR)",
        nameHi = "7/12 भू-अभिलेख / खसरा-खतौनी",
        requirementType = DocumentRequirementType.REQUIRED,
        status = DocumentStatus.READY,
        icon = Icons.Default.Landscape,
        docNumberMasked = "SURVEY-48/2-A",
        issuingAuthorityEn = "Department of Land Revenue (MahaBhulekh / Bhulekh)",
        issuingAuthorityHi = "भूमि अभिलेख विभाग (महाभूलेख / भूलेख)",
        verifiedSource = "Digitally Signed e-RoR",
        issueDate = "04-Jan-2024",
        validityDate = "Continuous Record",
        usedInSchemesCount = 8,
        descriptionEn = "Official land parcel ownership record proving agricultural landholding for farmer subsidies.",
        descriptionHi = "कृषि भूमि स्वामित्व का आधिकारिक प्रमाण; पीएम-किसान एवं किसान योजनाओं हेतु अनिवार्य।"
    ),
    CitizenDocumentItem(
        id = "DOC-05",
        nameEn = "Bank Account Passbook (Aadhaar Seeded)",
        nameHi = "बैंक खाता पासबुक (आधार लिंक)",
        requirementType = DocumentRequirementType.REQUIRED,
        status = DocumentStatus.READY,
        icon = Icons.Default.AccountBalance,
        docNumberMasked = "SBI-A/C-****9021",
        issuingAuthorityEn = "State Bank of India (Public Sector Bank)",
        issuingAuthorityHi = "भारतीय स्टेट बैंक (सार्वजनिक बैंक)",
        verifiedSource = "NPCI Aadhaar Mapper Verified",
        issueDate = "10-Feb-2021",
        validityDate = "Active & KYC Compliant",
        usedInSchemesCount = 24,
        descriptionEn = "Aadhaar-seeded bank account for zero-leakage Direct Benefit Transfer (DBT) payments.",
        descriptionHi = "डीबीटी के माध्यम से सीधे सरकारी सहायता राशि प्राप्त करने हेतु आधार लिंक बैंक खाता।"
    ),
    CitizenDocumentItem(
        id = "DOC-06",
        nameEn = "Caste / Category Certificate",
        nameHi = "जाति प्रमाण पत्र",
        requirementType = DocumentRequirementType.REQUIRED,
        status = DocumentStatus.MISSING,
        icon = Icons.Default.Groups,
        docNumberMasked = null,
        issuingAuthorityEn = "District Caste Scrutiny Committee / SDM",
        issuingAuthorityHi = "जिला जाति पड़ताल समिति / उप-विभागीय अधिकारी",
        verifiedSource = "Pending Verification",
        issueDate = null,
        validityDate = null,
        usedInSchemesCount = 16,
        descriptionEn = "Validates affirmative action reservation category (SC / ST / OBC / SEBC) for special subsidies.",
        descriptionHi = "आरक्षित वर्ग (एससी, एसटी, ओबीसी, ईडब्ल्यूएस) की विशेष योजनाओं हेतु जाति प्रमाण।"
    ),
    CitizenDocumentItem(
        id = "DOC-07",
        nameEn = "Ration Card (NFSA / Antyodaya)",
        nameHi = "राशन कार्ड (राष्ट्रीय खाद्य सुरक्षा)",
        requirementType = DocumentRequirementType.REQUIRED,
        status = DocumentStatus.READY,
        icon = Icons.Default.FoodBank,
        docNumberMasked = "NFSA-RC-3982109",
        issuingAuthorityEn = "Food, Civil Supplies & Consumer Affairs Department",
        issuingAuthorityHi = "खाद्य, नागरिक आपूर्ति एवं उपभोक्ता मामले विभाग",
        verifiedSource = "Annavitran National Portal",
        issueDate = "18-Oct-2022",
        validityDate = "Active Family Card",
        usedInSchemesCount = 12,
        descriptionEn = "Identifies household food security category and proves family unit composition.",
        descriptionHi = "मुफ़्त खाद्यान्न, पारिवारिक इकाई और सामाजिक सुरक्षा योजनाओं हेतु प्रमुख प्रमाण।"
    ),
    CitizenDocumentItem(
        id = "DOC-08",
        nameEn = "Disability Certificate (UDID)",
        nameHi = "दिव्यांगता प्रमाण पत्र (यूडीआईडी कार्ड)",
        requirementType = DocumentRequirementType.OPTIONAL,
        status = DocumentStatus.MISSING,
        icon = Icons.AutoMirrored.Filled.Accessible,
        docNumberMasked = null,
        issuingAuthorityEn = "Department of Empowerment of Persons with Disabilities",
        issuingAuthorityHi = "दिव्यांगजन सशक्तिकरण विभाग (भारत सरकार)",
        verifiedSource = "Not Applicable / Not Added",
        issueDate = null,
        validityDate = null,
        usedInSchemesCount = 6,
        descriptionEn = "Unique Disability Identity Card (UDID) for assistive device grants and disability pensions.",
        descriptionHi = "दिव्यांग पेंशन, सहायक उपकरण और विशेष भत्तों हेतु विशिष्ट दिव्यांगता पहचान पत्र।"
    ),
    CitizenDocumentItem(
        id = "DOC-09",
        nameEn = "PM-KISAN Farmer Registration ID",
        nameHi = "पीएम-किसान पंजीकरण आईडी",
        requirementType = DocumentRequirementType.OPTIONAL,
        status = DocumentStatus.READY,
        icon = Icons.Default.Agriculture,
        docNumberMasked = "MH-PMK-982145",
        issuingAuthorityEn = "Ministry of Agriculture & Farmers Welfare",
        issuingAuthorityHi = "कृषि एवं किसान कल्याण मंत्रालय",
        verifiedSource = "pmkisan.gov.in Database",
        issueDate = "28-Feb-2020",
        validityDate = "e-KYC Verified",
        usedInSchemesCount = 5,
        descriptionEn = "Auto-qualifies farmers for state top-up grants and seasonal fertilizer subsidies.",
        descriptionHi = "राज्य किसान सम्मान और कृषि इनपुट सब्सिडी हेतु किसान पंजीकरण संख्या।"
    ),
    CitizenDocumentItem(
        id = "DOC-10",
        nameEn = "Birth Certificate / Age Proof",
        nameHi = "जन्म प्रमाण पत्र / आयु प्रमाण",
        requirementType = DocumentRequirementType.REQUIRED,
        status = DocumentStatus.READY,
        icon = Icons.Default.Cake,
        docNumberMasked = "BIRTH/REG/1988/442",
        issuingAuthorityEn = "Municipal Corporation / Registrar of Births & Deaths",
        issuingAuthorityHi = "नगर निगम / जन्म एवं मृत्यु पंजीयक कार्यालय",
        verifiedSource = "DigiLocker Verified",
        issueDate = "05-Jun-1988",
        validityDate = "Permanent Record",
        usedInSchemesCount = 15,
        descriptionEn = "Verifies age qualifications for senior citizen pensions, youth scholarships, and child welfare.",
        descriptionHi = "वृद्धावस्था पेंशन, युवा छात्रवृत्ति एवं बाल कल्याण हेतु आयु प्रमाण।"
    ),
    CitizenDocumentItem(
        id = "DOC-11",
        nameEn = "Educational Marksheet / Degree",
        nameHi = "शैक्षणिक अंकतालिका / डिग्री प्रमाण",
        requirementType = DocumentRequirementType.OPTIONAL,
        status = DocumentStatus.MISSING,
        icon = Icons.Default.School,
        docNumberMasked = null,
        issuingAuthorityEn = "State Board of Secondary Education / University",
        issuingAuthorityHi = "राज्य माध्यमिक शिक्षा बोर्ड / विश्वविद्यालय",
        verifiedSource = "Pending Linking",
        issueDate = null,
        validityDate = null,
        usedInSchemesCount = 9,
        descriptionEn = "Required for Post-Matric scholarships, skill training stipends, and youth apprenticeship.",
        descriptionHi = "मैट्रिक-उपरांत छात्रवृत्ति, कौशल विकास प्रशिक्षण और वजीफा योजनाओं हेतु आवश्यक।"
    ),
    CitizenDocumentItem(
        id = "DOC-12",
        nameEn = "MGNREGA Job Card",
        nameHi = "मनरेगा जॉब कार्ड",
        requirementType = DocumentRequirementType.OPTIONAL,
        status = DocumentStatus.MISSING,
        icon = Icons.Default.Engineering,
        docNumberMasked = null,
        issuingAuthorityEn = "Ministry of Rural Development / Gram Panchayat",
        issuingAuthorityHi = "ग्रामीण विकास मंत्रालय / ग्राम पंचायत",
        verifiedSource = "NREGA MIS Portal",
        issueDate = null,
        validityDate = null,
        usedInSchemesCount = 7,
        descriptionEn = "Guarantees 100 days of rural wage employment and unlocks rural housing grant priorities.",
        descriptionHi = "100 दिन का सुनिश्चित ग्रामीण रोजगार और ग्रामीण आवास योजना में वरीयता हेतु कार्ड।"
    )
)

// -------------------------------------------------------------
// FILTER TAB ENUM
// -------------------------------------------------------------

enum class DocumentFilterTab(val titleEn: String, val titleHi: String, val icon: ImageVector) {
    ALL("All", "सभी", Icons.Default.FolderCopy),
    READY("Ready", "तैयार", Icons.Default.CheckCircle),
    REQUIRED("Required", "अनिवार्य", Icons.AutoMirrored.Filled.FactCheck),
    MISSING("Missing", "अनुपलब्ध", Icons.Default.Warning),
    OPTIONAL("Optional", "वैकल्पिक", Icons.Default.BookmarkBorder)
}

// -------------------------------------------------------------
// MAIN DOCUMENTS SECTION COMPOSABLE
// -------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentsScreen(
    currentLanguage: AppLanguage = AppLanguage.HINDI,
    onSelectLanguage: (AppLanguage) -> Unit = {},
    isLargeFont: Boolean = false,
    onFontScaleToggle: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigateHome: () -> Unit = {},
    onNavigateSchemes: () -> Unit = {},
    onNavigateApplications: () -> Unit = {},
    onNavigateProfile: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val fontSizeMultiplier = if (isLargeFont) 1.15f else 1.0f

    // -------------------------------------------------------------
    // LOCAL REACTIVE STATE (Zero backend / Mock only)
    // -------------------------------------------------------------
    var documentList by remember { mutableStateOf(INITIAL_CITIZEN_DOCUMENTS_MOCK) }
    var selectedFilterTab by remember { mutableStateOf(DocumentFilterTab.ALL) }
    var searchQuery by remember { mutableStateOf("") }

    // Dialog & Sheet States
    var selectedDocForPreview by remember { mutableStateOf<CitizenDocumentItem?>(null) }
    var selectedDocForAdd by remember { mutableStateOf<CitizenDocumentItem?>(null) }
    var showDigiLockerSyncDialog by remember { mutableStateOf(false) }
    var showSuccessToastMessage by remember { mutableStateOf<String?>(null) }

    // Derived Statistics
    val readyCount = remember(documentList) { documentList.count { it.status == DocumentStatus.READY } }
    val missingCount = remember(documentList) { documentList.count { it.status == DocumentStatus.MISSING } }
    val requiredCount = remember(documentList) { documentList.count { it.requirementType == DocumentRequirementType.REQUIRED } }
    val requiredReadyCount = remember(documentList) {
        documentList.count { it.requirementType == DocumentRequirementType.REQUIRED && it.status == DocumentStatus.READY }
    }
    val totalCount = documentList.size
    val readinessPercentage = if (totalCount > 0) (readyCount.toFloat() / totalCount * 100).toInt() else 0

    // Filtered Document List
    val filteredDocuments = remember(documentList, selectedFilterTab, searchQuery) {
        documentList.filter { doc ->
            // Tab filtering
            val matchesTab = when (selectedFilterTab) {
                DocumentFilterTab.ALL -> true
                DocumentFilterTab.READY -> doc.status == DocumentStatus.READY
                DocumentFilterTab.MISSING -> doc.status == DocumentStatus.MISSING
                DocumentFilterTab.REQUIRED -> doc.requirementType == DocumentRequirementType.REQUIRED
                DocumentFilterTab.OPTIONAL -> doc.requirementType == DocumentRequirementType.OPTIONAL
            }

            // Search query filtering
            val matchesSearch = if (searchQuery.isBlank()) {
                true
            } else {
                val q = searchQuery.trim().lowercase()
                doc.nameEn.lowercase().contains(q) ||
                        doc.nameHi.lowercase().contains(q) ||
                        doc.issuingAuthorityEn.lowercase().contains(q) ||
                        doc.issuingAuthorityHi.lowercase().contains(q) ||
                        doc.descriptionEn.lowercase().contains(q) ||
                        (doc.docNumberMasked?.lowercase()?.contains(q) == true)
            }

            matchesTab && matchesSearch
        }
    }

    Scaffold(
        bottomBar = {
            JanSaarthiBottomNav(
                selectedTab = 3,
                currentLanguage = currentLanguage,
                onNavigateHome = onNavigateHome,
                onNavigateSchemes = onNavigateSchemes,
                onNavigateApplications = onNavigateApplications,
                onNavigateDocuments = { /* already here */ },
                onNavigateProfile = onNavigateProfile
            )
        },
        topBar = {
            Column {
                // National Identity Banner
                GovernmentBanner(language = currentLanguage, fontSizeMultiplier = fontSizeMultiplier)

                // App Top Bar
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
                                    contentDescription = "Back",
                                    tint = GovNavyPrimary
                                )
                            }

                            Column {
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) "मेरे दस्तावेज़ (Digital Vault)" else "My Documents",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GovNavyPrimary,
                                        fontSize = (16 * fontSizeMultiplier).sp
                                    )
                                )
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI)
                                        "$readyCount तैयार • $missingCount अनुपलब्ध"
                                    else
                                        "$readyCount Ready • $missingCount Missing",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF64748B),
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        // DigiLocker Fast-Sync Action Button
                        OutlinedButton(
                            onClick = { showDigiLockerSyncDialog = true },
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, GovNavyPrimary),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudSync,
                                    contentDescription = null,
                                    tint = GovNavyPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "DigiLocker ⚡",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GovNavyPrimary,
                                        fontSize = 11.sp
                                    )
                                )
                            }
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
            // ACCESSIBILITY BAR
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
            // SECTION 1: READINESS SUMMARY & STATISTICS CARD
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
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) "दस्तावेज़ तत्परता स्कोर" else "Document Readiness Score",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GovNavyPrimary,
                                        fontSize = (14 * fontSizeMultiplier).sp
                                    )
                                )
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI)
                                        "अनिवार्य दस्तावेज़: $requiredReadyCount / $requiredCount पूर्ण"
                                    else
                                        "Required Documents: $requiredReadyCount of $requiredCount Ready",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF64748B),
                                        fontSize = 11.5.sp
                                    )
                                )
                            }

                            // Score Circle
                            Surface(
                                color = if (readinessPercentage >= 70) Color(0xFFDCFCE7) else Color(0xFFFEF3C7),
                                shape = CircleShape,
                                modifier = Modifier.size(48.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "$readinessPercentage%",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (readinessPercentage >= 70) Color(0xFF15803D) else Color(0xFFB45309),
                                            fontSize = 13.sp
                                        )
                                    )
                                }
                            }
                        }

                        // Progress Bar
                        LinearProgressIndicator(
                            progress = { readinessPercentage / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = if (readinessPercentage >= 70) Color(0xFF16A34A) else GovSaffron,
                            trackColor = Color(0xFFE2E8F0),
                        )

                        // 3 Key Badges Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Ready Counter Chip
                            Surface(
                                color = Color(0xFFF0FDF4),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedFilterTab = DocumentFilterTab.READY }
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "$readyCount",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF15803D)
                                        )
                                    )
                                    Text(
                                        text = if (currentLanguage == AppLanguage.HINDI) "तैयार (Ready)" else "Ready",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color(0xFF166534),
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }
                            }

                            // Missing Counter Chip
                            Surface(
                                color = Color(0xFFFEF2F2),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color(0xFFFECACA)),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedFilterTab = DocumentFilterTab.MISSING }
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "$missingCount",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFDC2626)
                                        )
                                    )
                                    Text(
                                        text = if (currentLanguage == AppLanguage.HINDI) "अनुपलब्ध (Missing)" else "Missing",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color(0xFF991B1B),
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }
                            }

                            // Required Counter Chip
                            Surface(
                                color = Color(0xFFEFF6FF),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedFilterTab = DocumentFilterTab.REQUIRED }
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "$requiredCount",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = GovNavyPrimary
                                        )
                                    )
                                    Text(
                                        text = if (currentLanguage == AppLanguage.HINDI) "अनिवार्य (Req)" else "Required",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = GovNavyPrimary,
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }
                            }
                        }

                        // Guidance Callout
                        Surface(
                            color = Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = GovNavyPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI)
                                        "सत्यापित दस्तावेज़ 28+ सरकारी योजनाओं में स्वत: आवेदन हेतु सीधे मान्य हैं।"
                                    else
                                        "Verified documents are pre-approved for instant DBT & 28+ welfare schemes.",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF475569),
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // SECTION 2: SEARCH & FILTER TABS
            // -------------------------------------------------------------
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Accessible Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        placeholder = {
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI)
                                    "दस्तावेज़ या जारीकर्ता खोजें..."
                                else
                                    "Search document name, issuer...",
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

                    // Horizontal Filter Tabs: All, Ready, Required, Missing, Optional
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(DocumentFilterTab.values()) { tab ->
                            val isSelected = selectedFilterTab == tab
                            val count = when (tab) {
                                DocumentFilterTab.ALL -> totalCount
                                DocumentFilterTab.READY -> readyCount
                                DocumentFilterTab.REQUIRED -> requiredCount
                                DocumentFilterTab.MISSING -> missingCount
                                DocumentFilterTab.OPTIONAL -> totalCount - requiredCount
                            }

                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedFilterTab = tab },
                                leadingIcon = {
                                    Icon(
                                        imageVector = tab.icon,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                },
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
            // SECTION 3: DOCUMENT CARDS LIST
            // -------------------------------------------------------------
            if (filteredDocuments.isEmpty()) {
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
                                    "कोई दस्तावेज़ नहीं मिला"
                                else
                                    "No Documents Found",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF475569)
                                )
                            )
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI)
                                    "कृपया अन्य फ़िल्टर चुनें या खोज शब्द बदलें।"
                                else
                                    "Try adjusting your search query or selecting a different filter tab.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF64748B),
                                    textAlign = TextAlign.Center
                                )
                            )
                        }
                    }
                }
            } else {
                items(filteredDocuments, key = { it.id }) { doc ->
                    DocumentCard(
                        doc = doc,
                        currentLanguage = currentLanguage,
                        fontSizeMultiplier = fontSizeMultiplier,
                        onViewClick = { selectedDocForPreview = doc },
                        onAddClick = { selectedDocForAdd = doc },
                        onRemoveClick = {
                            // Toggle mock state: from READY to MISSING
                            documentList = documentList.map {
                                if (it.id == doc.id) {
                                    it.copy(
                                        status = DocumentStatus.MISSING,
                                        docNumberMasked = null,
                                        verifiedSource = "Removed from Vault",
                                        issueDate = null
                                    )
                                } else it
                            }
                            showSuccessToastMessage = if (currentLanguage == AppLanguage.HINDI)
                                "${doc.nameHi} को अनुपलब्ध चिह्नित किया गया"
                            else
                                "${doc.nameEn} marked as missing"
                        }
                    )
                }
            }

            // -------------------------------------------------------------
            // SECTION 4: PROTOTYPE NOTICE & DIGILOCKER SECURITY FOOTER
            // -------------------------------------------------------------
            item {
                Surface(
                    color = Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = GovNavyPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "DigiLocker & DPDP Act 2023 Compliant",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GovNavyPrimary
                                )
                            )
                        }
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI)
                                "यह केवल यूआई प्रोटोटाइप प्रदर्शन है। वास्तविक दस्तावेज़ फ़ाइलें या अपलोड सर्वर से नहीं जुड़े हैं।"
                            else
                                "Frontend demonstration only with local mock state. No real files or cloud storage connected yet.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF64748B),
                                fontSize = 10.5.sp
                            )
                        )
                    }
                }
            }
        }
    }

    // -------------------------------------------------------------
    // MODAL DIALOG: DOCUMENT VIEW / PREVIEW CREDENTIAL
    // -------------------------------------------------------------
    selectedDocForPreview?.let { doc ->
        Dialog(onDismissRequest = { selectedDocForPreview = null }) {
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
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                color = if (doc.status == DocumentStatus.READY) Color(0xFFDCFCE7) else Color(0xFFFEE2E2),
                                shape = CircleShape,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = doc.icon,
                                        contentDescription = null,
                                        tint = if (doc.status == DocumentStatus.READY) Color(0xFF15803D) else Color(0xFFB91C1C),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) doc.nameHi else doc.nameEn,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GovNavyPrimary,
                                        fontSize = 15.sp
                                    )
                                )
                                Text(
                                    text = if (doc.status == DocumentStatus.READY) "✔ Verified DigiLocker Credential" else "⚠ Document Not Yet Added",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (doc.status == DocumentStatus.READY) Color(0xFF15803D) else Color(0xFFB91C1C),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }

                        IconButton(onClick = { selectedDocForPreview = null }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF64748B))
                        }
                    }

                    HorizontalDivider(color = Color(0xFFE2E8F0))

                    if (doc.status == DocumentStatus.READY) {
                        // Simulated Digital Certificate Sheet
                        Surface(
                            color = Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "DOCUMENT NUMBER:",
                                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF64748B))
                                    )
                                    Text(
                                        text = doc.docNumberMasked ?: "N/A",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = GovNavyPrimary
                                        )
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "ISSUING AUTHORITY:",
                                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF64748B))
                                    )
                                    Text(
                                        text = if (currentLanguage == AppLanguage.HINDI) doc.issuingAuthorityHi else doc.issuingAuthorityEn,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Color(0xFF1E293B),
                                            fontWeight = FontWeight.Medium,
                                            textAlign = TextAlign.End
                                        ),
                                        modifier = Modifier.widthIn(max = 180.dp)
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "ISSUED ON:",
                                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF64748B))
                                    )
                                    Text(
                                        text = doc.issueDate ?: "N/A",
                                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF1E293B))
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "VALIDITY / STATUS:",
                                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF64748B))
                                    )
                                    Text(
                                        text = doc.validityDate ?: "Active",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Color(0xFF15803D),
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }

                                HorizontalDivider(color = Color(0xFFE2E8F0))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VerifiedUser,
                                        contentDescription = null,
                                        tint = Color(0xFF15803D),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "Digitally signed credential via ${doc.verifiedSource}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color(0xFF15803D),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.5.sp
                                        )
                                    )
                                }
                            }
                        }

                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI)
                                "यह दस्तावेज़ ${doc.usedInSchemesCount} कल्याणकारी योजनाओं में सीधे मान्य है।"
                            else
                                "This document is linked and pre-verified for ${doc.usedInSchemesCount} active schemes.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF475569),
                                fontSize = 11.5.sp
                            )
                        )
                    } else {
                        // Missing document explanation & how to obtain
                        Surface(
                            color = Color(0xFFFFFBEB),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) "यह दस्तावेज़ आवश्यक क्यों है?" else "Why is this document needed?",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF92400E)
                                    )
                                )
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) doc.descriptionHi else doc.descriptionEn,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF78350F),
                                        lineHeight = 16.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI)
                                        "जारीकर्ता प्राधिकरण: ${doc.issuingAuthorityHi}"
                                    else
                                        "Issuing Authority: ${doc.issuingAuthorityEn}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GovNavyPrimary
                                    )
                                )
                            }
                        }

                        Button(
                            onClick = {
                                selectedDocForPreview = null
                                selectedDocForAdd = doc
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GovNavyPrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) "+ दस्तावेज़ जोड़ें / लिंक करें" else "+ Add / Link Document",
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Button(
                        onClick = { selectedDocForPreview = null },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI) "बंद करें" else "Close",
                            color = Color(0xFF334155),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    // -------------------------------------------------------------
    // MODAL DIALOG: ADD DOCUMENT (SIMULATED CHANNELS)
    // -------------------------------------------------------------
    selectedDocForAdd?.let { doc ->
        Dialog(onDismissRequest = { selectedDocForAdd = null }) {
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
                                imageVector = Icons.Default.AddCircle,
                                contentDescription = null,
                                tint = GovNavyPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Column {
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) "दस्तावेज़ जोड़ें" else "Add Document",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GovNavyPrimary
                                    )
                                )
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) doc.nameHi else doc.nameEn,
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF64748B))
                                )
                            }
                        }

                        IconButton(onClick = { selectedDocForAdd = null }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF64748B))
                        }
                    }

                    HorizontalDivider(color = Color(0xFFE2E8F0))

                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI)
                            "दस्तावेज़ जोड़ने हेतु माध्यम चुनें (लोकल मॉक सिमुलेशन):"
                        else
                            "Choose an option to add this document (Simulated frontend channels):",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF334155))
                    )

                    // Option 1: DigiLocker Instant Fetch
                    Surface(
                        color = Color(0xFFEFF6FF),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // Simulate DigiLocker Fetch
                                documentList = documentList.map {
                                    if (it.id == doc.id) {
                                        it.copy(
                                            status = DocumentStatus.READY,
                                            docNumberMasked = "DLK/${(1000..9999).random()}/${(10000..99999).random()}",
                                            verifiedSource = "DigiLocker Instant Fetch",
                                            issueDate = "Today (Verified)",
                                            validityDate = "Valid"
                                        )
                                    } else it
                                }
                                selectedDocForAdd = null
                                showSuccessToastMessage = if (currentLanguage == AppLanguage.HINDI)
                                    "${doc.nameHi} डिजीलॉकर से सफलतापूर्वक जोड़ा गया!"
                                else
                                    "${doc.nameEn} successfully linked via DigiLocker!"
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                color = GovNavyPrimary,
                                shape = CircleShape,
                                modifier = Modifier.size(34.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.CloudDownload,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) "डिजीलॉकर से तुरंत प्राप्त करें ⚡" else "Fetch from DigiLocker ⚡",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GovNavyPrimary,
                                        fontSize = 13.5.sp
                                    )
                                )
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) "100% सरकारी सत्यापित एवं तत्काल मान्य" else "1-Click instant official verification",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF475569),
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }

                    // Option 2: Upload File From Device
                    Surface(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // Simulate Local Device Upload
                                documentList = documentList.map {
                                    if (it.id == doc.id) {
                                        it.copy(
                                            status = DocumentStatus.READY,
                                            docNumberMasked = "UPL/${(1000..9999).random()}",
                                            verifiedSource = "Device Upload (Self-Certified)",
                                            issueDate = "Today",
                                            validityDate = "Pending Physical Verification"
                                        )
                                    } else it
                                }
                                selectedDocForAdd = null
                                showSuccessToastMessage = if (currentLanguage == AppLanguage.HINDI)
                                    "${doc.nameHi} फ़ाइल अपलोड की गई!"
                                else
                                    "${doc.nameEn} simulated file uploaded!"
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                color = Color(0xFFE2E8F0),
                                shape = CircleShape,
                                modifier = Modifier.size(34.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.UploadFile,
                                        contentDescription = null,
                                        tint = Color(0xFF334155),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) "डिवाइस से फ़ाइल अपलोड करें 📁" else "Upload from Device 📁",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E293B),
                                        fontSize = 13.5.sp
                                    )
                                )
                                Text(
                                    text = "PDF, JPG, PNG (Simulated local state)",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF64748B),
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }

                    // Option 3: Camera Scan
                    Surface(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // Simulate Camera Scan
                                documentList = documentList.map {
                                    if (it.id == doc.id) {
                                        it.copy(
                                            status = DocumentStatus.READY,
                                            docNumberMasked = "CAM/SCAN/${(100..999).random()}",
                                            verifiedSource = "Camera OCR Scan",
                                            issueDate = "Today",
                                            validityDate = "Self-Certified"
                                        )
                                    } else it
                                }
                                selectedDocForAdd = null
                                showSuccessToastMessage = if (currentLanguage == AppLanguage.HINDI)
                                    "${doc.nameHi} कैमरा स्कैन द्वारा जोड़ा गया!"
                                else
                                    "${doc.nameEn} scanned via camera!"
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                color = Color(0xFFE2E8F0),
                                shape = CircleShape,
                                modifier = Modifier.size(34.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.CameraAlt,
                                        contentDescription = null,
                                        tint = Color(0xFF334155),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) "कैमरा से स्कैन करें 📷" else "Scan with Camera 📷",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E293B),
                                        fontSize = 13.5.sp
                                    )
                                )
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) "भौतिक दस्तावेज़ की फ़ोटो लें" else "Photo of physical certificate",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF64748B),
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
    // MODAL DIALOG: DIGILOCKER BULK SYNC
    // -------------------------------------------------------------
    if (showDigiLockerSyncDialog) {
        Dialog(onDismissRequest = { showDigiLockerSyncDialog = false }) {
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
                                imageVector = Icons.Default.CloudSync,
                                contentDescription = null,
                                tint = GovNavyPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "DigiLocker Sync",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GovNavyPrimary
                                )
                            )
                        }

                        IconButton(onClick = { showDigiLockerSyncDialog = false }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF64748B))
                        }
                    }

                    HorizontalDivider(color = Color(0xFFE2E8F0))

                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI)
                            "डिजीलॉकर से आपके आधार से जुड़े सभी सरकारी प्रमाण पत्रों को एक साथ सिंक्रनाइज़ करें।"
                        else
                            "Sync all Aadhaar-linked issued government certificates directly from the National DigiLocker ecosystem.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF334155),
                            lineHeight = 17.sp
                        )
                    )

                    Surface(
                        color = Color(0xFFF0FDF4),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "🔒 100% Official Gov Gateway (Simulated)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF15803D),
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "Ministry of Electronics and Information Technology (MeitY)",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF64748B),
                                    fontSize = 10.5.sp
                                )
                            )
                        }
                    }

                    Button(
                        onClick = {
                            // Turn all missing to ready
                            documentList = documentList.map { doc ->
                                if (doc.status == DocumentStatus.MISSING) {
                                    doc.copy(
                                        status = DocumentStatus.READY,
                                        docNumberMasked = "DLK/SYNC/${(1000..9999).random()}",
                                        verifiedSource = "DigiLocker Auto-Sync",
                                        issueDate = "Today",
                                        validityDate = "Valid"
                                    )
                                } else doc
                            }
                            showDigiLockerSyncDialog = false
                            showSuccessToastMessage = if (currentLanguage == AppLanguage.HINDI)
                                "सभी अनुपलब्ध दस्तावेज़ डिजीलॉकर से सिंक्रनाइज़ किए गए!"
                            else
                                "All missing documents successfully synced from DigiLocker!"
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GovNavyPrimary)
                    ) {
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI) "सभी दस्तावेज़ सिंक करें ⚡" else "Sync All Missing Documents ⚡",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                }
            }
        }
    }

    // -------------------------------------------------------------
    // SUCCESS SNACKBAR / CONFIRMATION MODAL
    // -------------------------------------------------------------
    showSuccessToastMessage?.let { msg ->
        LaunchedEffect(msg) {
            kotlinx.coroutines.delay(2500)
            showSuccessToastMessage = null
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
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF4ADE80),
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
// INDIVIDUAL DOCUMENT CARD COMPONENT
// -------------------------------------------------------------

@Composable
fun DocumentCard(
    doc: CitizenDocumentItem,
    currentLanguage: AppLanguage,
    fontSizeMultiplier: Float,
    onViewClick: () -> Unit,
    onAddClick: () -> Unit,
    onRemoveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(
            1.dp,
            if (doc.status == DocumentStatus.READY) Color(0xFFE2E8F0) else Color(0xFFFECACA)
        ),
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
            // Top Row: Badges (Required/Optional) and Status (Ready/Missing)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Requirement Badge (Required vs Optional)
                Surface(
                    color = when (doc.requirementType) {
                        DocumentRequirementType.REQUIRED -> Color(0xFFFEF2F2)
                        DocumentRequirementType.OPTIONAL -> Color(0xFFF1F5F9)
                    },
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(
                        1.dp,
                        when (doc.requirementType) {
                            DocumentRequirementType.REQUIRED -> Color(0xFFFECACA)
                            DocumentRequirementType.OPTIONAL -> Color(0xFFCBD5E1)
                        }
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = when (doc.requirementType) {
                                DocumentRequirementType.REQUIRED -> Icons.Default.PriorityHigh
                                DocumentRequirementType.OPTIONAL -> Icons.Default.Remove
                            },
                            contentDescription = null,
                            tint = when (doc.requirementType) {
                                DocumentRequirementType.REQUIRED -> Color(0xFFB91C1C)
                                DocumentRequirementType.OPTIONAL -> Color(0xFF475569)
                            },
                            modifier = Modifier.size(11.dp)
                        )
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI) doc.requirementType.labelHi else doc.requirementType.labelEn,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = when (doc.requirementType) {
                                    DocumentRequirementType.REQUIRED -> Color(0xFFB91C1C)
                                    DocumentRequirementType.OPTIONAL -> Color(0xFF475569)
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                // Status Badge (Ready vs Missing)
                Surface(
                    color = doc.status.bgColor,
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, doc.status.color.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = when (doc.status) {
                                DocumentStatus.READY -> Icons.Default.Check
                                DocumentStatus.MISSING -> Icons.Default.Warning
                            },
                            contentDescription = null,
                            tint = doc.status.color,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI) doc.status.labelHi else doc.status.labelEn,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = doc.status.color,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.5.sp
                            )
                        )
                    }
                }
            }

            // Middle Row: Document Icon, Name, and Issuer / Number
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    color = if (doc.status == DocumentStatus.READY) Color(0xFFEFF6FF) else Color(0xFFFEF2F2),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = doc.icon,
                            contentDescription = null,
                            tint = if (doc.status == DocumentStatus.READY) GovNavyPrimary else Color(0xFFDC2626),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI) doc.nameHi else doc.nameEn,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = GovTextPrimary,
                            fontSize = (14.5 * fontSizeMultiplier).sp
                        )
                    )
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI) doc.nameEn else doc.nameHi,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF64748B),
                            fontSize = 11.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    if (doc.status == DocumentStatus.READY && doc.docNumberMasked != null) {
                        Surface(
                            color = Color(0xFFF1F5F9),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "${doc.docNumberMasked} • ${doc.verifiedSource}",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF334155),
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    } else {
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI)
                                "जारीकर्ता: ${doc.issuingAuthorityHi}"
                            else
                                "Issuer: ${doc.issuingAuthorityEn}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Description / Scheme Linking Snippet
            Text(
                text = if (currentLanguage == AppLanguage.HINDI) doc.descriptionHi else doc.descriptionEn,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF475569),
                    fontSize = (11.5 * fontSizeMultiplier).sp,
                    lineHeight = 15.sp
                )
            )

            HorizontalDivider(color = Color(0xFFF1F5F9))

            // Action Buttons Row: View button & Add button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // View Button
                OutlinedButton(
                    onClick = onViewClick,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, GovNavyPrimary),
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = null,
                            tint = GovNavyPrimary,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI) "देखें (View)" else "View",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = GovNavyPrimary
                            )
                        )
                    }
                }

                // Add Button (or Update/Remove if ready)
                if (doc.status == DocumentStatus.MISSING) {
                    Button(
                        onClick = onAddClick,
                        colors = ButtonDefaults.buttonColors(containerColor = GovNavyPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) "जोड़ें (Add)" else "Add",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    }
                } else {
                    // Option to remove / test flipping state back to missing
                    OutlinedButton(
                        onClick = onRemoveClick,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                        modifier = Modifier
                            .weight(0.7f)
                            .height(38.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI) "हटाएं" else "Remove",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF64748B),
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }
    }
}
