package org.jansaarthi.app.ui.screens.saved

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.TrendingUp
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
import kotlinx.coroutines.launch
import org.jansaarthi.app.data.model.AppLanguage
import org.jansaarthi.app.ui.components.AccessibilityBar
import org.jansaarthi.app.ui.components.GovernmentBanner
import org.jansaarthi.app.ui.theme.*

/**
 * ----------------------------------------------------------------------------
 * JanSaarthi Saved Schemes Screen (Frontend UI Only)
 * ----------------------------------------------------------------------------
 * Dedicated citizen bookmarks and watchlist manager:
 * - Displays all saved / bookmarked government welfare schemes
 * - 1-tap interactive bookmark removal with Undo Snackbar
 * - High-accessibility empty state with search tips and "Explore Schemes" CTA
 * - Open Scheme Details preview dialog with full benefits and eligibility
 * - In-screen search and category filters for large watchlists
 * - 1-tap bookmarking from suggested popular schemes in the empty state
 *
 * Frontend UI Only: Operates strictly with local in-memory/mock state.
 * Zero database, backend, or network dependencies.
 */

// -------------------------------------------------------------
// DATA MODELS FOR SAVED SCHEMES
// -------------------------------------------------------------

enum class SavedGovLevel(val labelEn: String, val labelHi: String) {
    CENTRAL("Central Govt", "केंद्र सरकार"),
    STATE("State Govt", "राज्य सरकार"),
    UT("Union Territory", "केंद्र शासित प्रदेश")
}

data class SavedSchemeItem(
    val id: String,
    val nameEn: String,
    val nameHi: String,
    val acronym: String,
    val level: SavedGovLevel,
    val stateOrUt: String,
    val ministryOrDeptEn: String,
    val ministryOrDeptHi: String,
    val category: String,
    val categoryIcon: ImageVector,
    val categoryColor: Color,
    val benefitHighlightEn: String,
    val benefitHighlightHi: String,
    val financialAmountValue: Long,
    val eligibilitySummaryEn: String,
    val eligibilitySummaryHi: String,
    val targetGroupEn: String,
    val targetGroupHi: String,
    val isDbtEnabled: Boolean = true,
    val savedTimestamp: String,
    val applicationDeadline: String,
    val officialPortal: String,
    val requiredDocuments: List<String>
) {
    fun getName(lang: AppLanguage): String = if (lang == AppLanguage.HINDI) nameHi else nameEn
    fun getMinistry(lang: AppLanguage): String = if (lang == AppLanguage.HINDI) ministryOrDeptHi else ministryOrDeptEn
    fun getBenefit(lang: AppLanguage): String = if (lang == AppLanguage.HINDI) benefitHighlightHi else benefitHighlightEn
    fun getEligibility(lang: AppLanguage): String = if (lang == AppLanguage.HINDI) eligibilitySummaryHi else eligibilitySummaryEn
    fun getTargetGroup(lang: AppLanguage): String = if (lang == AppLanguage.HINDI) targetGroupHi else targetGroupEn
}

// -------------------------------------------------------------
// REALISTIC INITIAL IN-MEMORY MOCK SAVED SCHEMES DATASET
// -------------------------------------------------------------

val INITIAL_SAVED_SCHEMES_MOCK: List<SavedSchemeItem> = listOf(
    SavedSchemeItem(
        id = "SAV-01",
        nameEn = "PM Kisan Samman Nidhi",
        nameHi = "प्रधानमंत्री किसान सम्मान निधि (पीएम-किसान)",
        acronym = "PM-KISAN",
        level = SavedGovLevel.CENTRAL,
        stateOrUt = "National (All States & UTs)",
        ministryOrDeptEn = "Ministry of Agriculture & Farmers Welfare",
        ministryOrDeptHi = "कृषि एवं किसान कल्याण मंत्रालय",
        category = "Agriculture & Rural",
        categoryIcon = Icons.Default.Agriculture,
        categoryColor = Color(0xFF15803D),
        benefitHighlightEn = "₹6,000/year via direct DBT in 3 equal installments of ₹2,000",
        benefitHighlightHi = "₹6,000 प्रति वर्ष 3 समान किस्तों में सीधे बैंक खाते में (डीबीटी)",
        financialAmountValue = 6000,
        eligibilitySummaryEn = "Landholding farmer families with cultivable land. Aadhaar & e-KYC mandatory.",
        eligibilitySummaryHi = "खेती योग्य भूमि वाले किसान परिवार। आधार और ई-केवाईसी अनिवार्य।",
        targetGroupEn = "Farmers",
        targetGroupHi = "किसान",
        isDbtEnabled = true,
        savedTimestamp = "Saved 2 days ago",
        applicationDeadline = "Open Throughout Year",
        officialPortal = "https://pmkisan.gov.in",
        requiredDocuments = listOf("Aadhaar Card", "Land Ownership (7/12 / Khasra)", "Bank Passbook")
    ),
    SavedSchemeItem(
        id = "SAV-02",
        nameEn = "Ayushman Bharat PM-JAY (Golden Card)",
        nameHi = "आयुष्मान भारत प्रधानमंत्री जन आरोग्य योजना",
        acronym = "PM-JAY",
        level = SavedGovLevel.CENTRAL,
        stateOrUt = "National (All States & UTs)",
        ministryOrDeptEn = "National Health Authority / Ministry of Health",
        ministryOrDeptHi = "राष्ट्रीय स्वास्थ्य प्राधिकरण / स्वास्थ्य मंत्रालय",
        category = "Health & Wellness",
        categoryIcon = Icons.Default.HealthAndSafety,
        categoryColor = Color(0xFF0F766E),
        benefitHighlightEn = "₹5,00,000 cashless family hospitalisation cover per year",
        benefitHighlightHi = "प्रति परिवार प्रति वर्ष ₹5,00,000 तक का मुफ़्त कैशलेस अस्पताल इलाज",
        financialAmountValue = 500000,
        eligibilitySummaryEn = "SECC-deprived rural/urban families and all senior citizens aged 70+ years.",
        eligibilitySummaryHi = "एसईसीसी के पात्र गरीब परिवार एवं 70 वर्ष से अधिक आयु के सभी वरिष्ठ नागरिक।",
        targetGroupEn = "Senior Citizens (70+) & Low Income",
        targetGroupHi = "वरिष्ठ नागरिक एवं गरीब परिवार",
        isDbtEnabled = false,
        savedTimestamp = "Saved 4 days ago",
        applicationDeadline = "Open Throughout Year",
        officialPortal = "https://pmjay.gov.in",
        requiredDocuments = listOf("Aadhaar Card", "Ration Card (NFSA)")
    ),
    SavedSchemeItem(
        id = "SAV-03",
        nameEn = "Mukhyamantri Majhi Ladki Bahin Yojana",
        nameHi = "मुख्यमंत्री माझी लाडकी बहीण योजना",
        acronym = "LADKI-BAHIN",
        level = SavedGovLevel.STATE,
        stateOrUt = "Maharashtra",
        ministryOrDeptEn = "Department of Women & Child Development (Govt of Maharashtra)",
        ministryOrDeptHi = "महिला व बाल विकास विभाग (महाराष्ट्र शासन)",
        category = "Women & Child",
        categoryIcon = Icons.Default.FamilyRestroom,
        categoryColor = Color(0xFFBE185D),
        benefitHighlightEn = "₹1,500 monthly direct cash transfer (₹18,000/year) into woman's bank account",
        benefitHighlightHi = "₹1,500 प्रति माह सीधे बैंक खाते में (वार्षिक ₹18,000) महिला स्वावलंबन सहायता",
        financialAmountValue = 18000,
        eligibilitySummaryEn = "Women aged 21-65 years residing in Maharashtra with annual family income under ₹2.5 Lakhs.",
        eligibilitySummaryHi = "महाराष्ट्र की 21-65 वर्ष की महिलाएं, पारिवारिक वार्षिक आय ₹2.5 लाख से कम।",
        targetGroupEn = "Women (21-65 yrs)",
        targetGroupHi = "महिलाएं (21-65 वर्ष)",
        isDbtEnabled = true,
        savedTimestamp = "Saved 1 week ago",
        applicationDeadline = "Active Scheme Cycle",
        officialPortal = "https://ladakibahin.maharashtra.gov.in",
        requiredDocuments = listOf("Aadhaar Card", "Maharashtra Domicile / Ration Card", "Bank Passbook seeded with DBT")
    ),
    SavedSchemeItem(
        id = "SAV-04",
        nameEn = "Pradhan Mantri Awas Yojana - Gramin",
        nameHi = "प्रधानमंत्री आवास योजना - ग्रामीण (पीएमएवाई-जी)",
        acronym = "PMAY-G",
        level = SavedGovLevel.CENTRAL,
        stateOrUt = "National (All States & UTs)",
        ministryOrDeptEn = "Ministry of Rural Development",
        ministryOrDeptHi = "ग्रामीण विकास मंत्रालय",
        category = "Housing & Shelter",
        categoryIcon = Icons.Default.Home,
        categoryColor = Color(0xFFB45309),
        benefitHighlightEn = "₹1,20,000 financial grant + ₹12,000 for toilet + 90 days MGNREGA wages",
        benefitHighlightHi = "पक्का मकान निर्माण हेतु ₹1,20,000 अनुदान + ₹12,000 शौचालय सहायता",
        financialAmountValue = 132000,
        eligibilitySummaryEn = "Houseless rural families and households living in zero/one room kutcha houses.",
        eligibilitySummaryHi = "कच्चे मकानों में रहने वाले या बेघर ग्रामीण परिवार (आवास+ सूची)।",
        targetGroupEn = "Rural Families",
        targetGroupHi = "ग्रामीण परिवार",
        isDbtEnabled = true,
        savedTimestamp = "Saved 2 weeks ago",
        applicationDeadline = "Gram Sabha Verification",
        officialPortal = "https://pmayg.nic.in",
        requiredDocuments = listOf("Aadhaar Card", "Bank Passbook", "MGNREGA Job Card", "Land Possession Proof")
    )
)

// Curated suggestions to bookmark if user's list becomes empty
val SUGGESTED_TO_BOOKMARK_MOCK: List<SavedSchemeItem> = listOf(
    SavedSchemeItem(
        id = "SUG-01",
        nameEn = "PM Vishwakarma Scheme",
        nameHi = "प्रधानमंत्री विश्वकर्मा योजना",
        acronym = "PM-VISHWAKARMA",
        level = SavedGovLevel.CENTRAL,
        stateOrUt = "National (All States & UTs)",
        ministryOrDeptEn = "Ministry of MSME",
        ministryOrDeptHi = "सूक्ष्म, लघु एवं मध्यम उद्यम मंत्रालय",
        category = "Skills & Artisan",
        categoryIcon = Icons.Default.Handyman,
        categoryColor = Color(0xFF334155),
        benefitHighlightEn = "₹15,000 modern toolkit voucher + collateral-free loans up to ₹3 Lakhs at 5%",
        benefitHighlightHi = "₹15,000 आधुनिक टूलकिट ई-वाउचर + ₹3 लाख तक का 5% ब्याज पर रियायती ऋण",
        financialAmountValue = 315000,
        eligibilitySummaryEn = "Traditional artisans working in 18 identified trades (Carpenters, Masons, Tailors, etc.).",
        eligibilitySummaryHi = "18 पारंपरिक व्यवसायों (बढ़ई, दर्जी, लोहार, कुम्हार) के शिल्पी व कारीगर।",
        targetGroupEn = "Artisans & Craftsmen",
        targetGroupHi = "कारीगर व शिल्पकार",
        isDbtEnabled = true,
        savedTimestamp = "Popular Scheme",
        applicationDeadline = "Open at CSCs",
        officialPortal = "https://pmvishwakarma.gov.in",
        requiredDocuments = listOf("Aadhaar Card", "Bank Passbook", "Ration Card")
    ),
    SavedSchemeItem(
        id = "SUG-02",
        nameEn = "Delhi Free Bus Travel (Pink Ticket)",
        nameHi = "दिल्ली महिला मुफ़्त बस यात्रा योजना (गुलाबी टिकट)",
        acronym = "PINK-TICKET",
        level = SavedGovLevel.UT,
        stateOrUt = "NCT of Delhi",
        ministryOrDeptEn = "Transport Department (Govt of NCT of Delhi)",
        ministryOrDeptHi = "परिवहन विभाग (दिल्ली सरकार)",
        category = "Transport & Travel",
        categoryIcon = Icons.Default.DirectionsBus,
        categoryColor = Color(0xFF1E293B),
        benefitHighlightEn = "100% free bus travel on all DTC and Cluster AC/non-AC buses in Delhi",
        benefitHighlightHi = "दिल्ली में सभी डीटीसी एवं क्लस्टर एसी/नॉन-एसी बसों में 100% मुफ़्त यात्रा",
        financialAmountValue = 18000,
        eligibilitySummaryEn = "All women and girl passengers traveling in DTC and Cluster city buses.",
        eligibilitySummaryHi = "दिल्ली में डीटीसी एवं क्लस्टर बसों में यात्रा करने वाली सभी महिलाएं एवं छात्राएं।",
        targetGroupEn = "Women & Girls",
        targetGroupHi = "महिलाएं व छात्राएं",
        isDbtEnabled = false,
        savedTimestamp = "Popular Scheme",
        applicationDeadline = "No Deadline",
        officialPortal = "https://transport.delhi.gov.in",
        requiredDocuments = listOf("No pre-registration needed")
    )
)

// -------------------------------------------------------------
// MAIN SAVED SCHEMES SCREEN COMPOSABLE
// -------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedSchemesScreen(
    currentLanguage: AppLanguage,
    onSelectLanguage: (AppLanguage) -> Unit,
    isLargeFont: Boolean,
    onFontScaleToggle: () -> Unit,
    onNavigateBack: () -> Unit,
    onExploreSchemes: () -> Unit = {},
    onOpenSchemeDetails: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val fontSizeMultiplier = if (isLargeFont) 1.15f else 1.0f
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // -------------------------------------------------------------
    // LOCAL IN-MEMORY MOCK STATE (FRONTEND ONLY - NO DATABASE)
    // -------------------------------------------------------------
    var savedSchemesList by remember {
        mutableStateOf(INITIAL_SAVED_SCHEMES_MOCK)
    }

    // Recently removed item for 1-tap Undo
    var lastRemovedScheme by remember { mutableStateOf<SavedSchemeItem?>(null) }
    var lastRemovedIndex by remember { mutableIntStateOf(-1) }

    // Search & Filter inside Saved Schemes
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("ALL") }

    // Preview Modal Dialog
    var selectedSchemeForPreview by remember { mutableStateOf<SavedSchemeItem?>(null) }
    var showClearAllConfirmationDialog by remember { mutableStateOf(false) }

    // Categories list for quick filtering
    val availableCategories = remember(savedSchemesList) {
        listOf("ALL") + savedSchemesList.map { it.category }.distinct()
    }

    // Filtered saved schemes
    val filteredSavedSchemes = remember(savedSchemesList, searchQuery, selectedCategoryFilter, currentLanguage) {
        val q = searchQuery.trim().lowercase()
        savedSchemesList.filter { item ->
            val matchesCategory = selectedCategoryFilter == "ALL" || item.category.equals(selectedCategoryFilter, ignoreCase = true)
            val matchesQuery = q.isEmpty() ||
                    item.nameEn.lowercase().contains(q) ||
                    item.nameHi.lowercase().contains(q) ||
                    item.acronym.lowercase().contains(q) ||
                    item.category.lowercase().contains(q) ||
                    item.ministryOrDeptEn.lowercase().contains(q) ||
                    item.benefitHighlightEn.lowercase().contains(q)
            matchesCategory && matchesQuery
        }
    }

    // Function to remove a bookmark with Undo capability
    val removeBookmark = { scheme: SavedSchemeItem ->
        val index = savedSchemesList.indexOfFirst { it.id == scheme.id }
        if (index != -1) {
            lastRemovedScheme = scheme
            lastRemovedIndex = index
            savedSchemesList = savedSchemesList.filterNot { it.id == scheme.id }

            coroutineScope.launch {
                val result = snackbarHostState.showSnackbar(
                    message = if (currentLanguage == AppLanguage.HINDI)
                        "\"${scheme.nameHi}\" को सहेजी गई सूची से हटा दिया गया"
                    else
                        "\"${scheme.nameEn}\" removed from Saved Schemes",
                    actionLabel = if (currentLanguage == AppLanguage.HINDI) "वापस लाएं (Undo)" else "Undo",
                    duration = SnackbarDuration.Short
                )
                if (result == SnackbarResult.ActionPerformed) {
                    // Restore item
                    lastRemovedScheme?.let { restored ->
                        val restoredList = savedSchemesList.toMutableList()
                        val targetIdx = lastRemovedIndex.coerceIn(0, restoredList.size)
                        restoredList.add(targetIdx, restored)
                        savedSchemesList = restoredList
                        lastRemovedScheme = null
                    }
                }
            }
        }
    }

    // Function to add a scheme to saved bookmarks
    val addBookmark = { scheme: SavedSchemeItem ->
        if (savedSchemesList.none { it.id == scheme.id }) {
            savedSchemesList = listOf(scheme) + savedSchemesList
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = if (currentLanguage == AppLanguage.HINDI)
                        "\"${scheme.nameHi}\" सहेजी गई सूची में जोड़ी गई!"
                    else
                        "\"${scheme.nameEn}\" added to Saved Schemes!",
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Government Prototype Banner
                GovernmentBanner()

                // Top App Bar
                Surface(
                    color = GovNavyPrimary,
                    tonalElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = if (currentLanguage == AppLanguage.HINDI) "वापस जाएं" else "Back to Home",
                                tint = Color.White
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) "सहेजी गई योजनाएं" else "Saved Schemes",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = (16 * fontSizeMultiplier).sp
                                    )
                                )
                                // Active count badge
                                Surface(
                                    color = GovSaffron,
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(
                                        text = savedSchemesList.size.toString(),
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI)
                                    "त्वरित समीक्षा व ऑफ़लाइन संदर्भ हेतु बुकमार्क की गई योजनाएं"
                                else
                                    "Your bookmarked government welfare programs for quick reference",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = (11 * fontSizeMultiplier).sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Clear All Bookmarks Button (when list has items)
                        if (savedSchemesList.isNotEmpty()) {
                            IconButton(onClick = { showClearAllConfirmationDialog = true }) {
                                Icon(
                                    imageVector = Icons.Default.DeleteSweep,
                                    contentDescription = "Clear All",
                                    tint = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }
                    }
                }
            }
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(16.dp)
            )
        },
        containerColor = GovBackground,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // -------------------------------------------------------------
            // ACCESSIBILITY CONTROL BAR (Language & Font scaling)
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
            // IN-SCREEN SEARCH & CATEGORY FILTER (When list has items)
            // -------------------------------------------------------------
            if (savedSchemesList.isNotEmpty()) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Quick Search Bar within Saved Schemes
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI)
                                        "सहेजी गई योजनाओं में खोजें..."
                                    else
                                        "Search your saved schemes...",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color(0xFF64748B),
                                        fontSize = (13 * fontSizeMultiplier).sp
                                    )
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
                                            tint = Color(0xFF64748B)
                                        )
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GovNavyPrimary,
                                unfocusedBorderColor = Color(0xFFCBD5E1),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            )
                        )

                        // Horizontal Category Filter Pills
                        if (availableCategories.size > 2) {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                contentPadding = PaddingValues(vertical = 2.dp)
                            ) {
                                items(availableCategories) { cat ->
                                    val isSelected = selectedCategoryFilter == cat
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = {
                                            selectedCategoryFilter = if (isSelected) "ALL" else cat
                                        },
                                        label = {
                                            Text(
                                                text = if (cat == "ALL")
                                                    (if (currentLanguage == AppLanguage.HINDI) "सभी (${savedSchemesList.size})" else "All (${savedSchemesList.size})")
                                                else
                                                    cat,
                                                fontSize = 12.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        shape = RoundedCornerShape(16.dp),
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = GovNavyPrimary,
                                            selectedLabelColor = Color.White,
                                            containerColor = Color.White,
                                            labelColor = GovNavyPrimary
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // Header Count Bar
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI)
                                "कुल सहेजी गई योजनाएं: ${filteredSavedSchemes.size}"
                            else
                                "Showing ${filteredSavedSchemes.size} saved schemes",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = GovNavyPrimary,
                                fontSize = (13 * fontSizeMultiplier).sp
                            )
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.clickable { onExploreSchemes() }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = GovSaffron,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) "और योजनाएं जोड़ें" else "Explore More",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = GovSaffron,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // EMPTY STATE (When no saved schemes exist or query yields 0)
            // -------------------------------------------------------------
            if (savedSchemesList.isEmpty()) {
                item {
                    SavedSchemesEmptyState(
                        currentLanguage = currentLanguage,
                        fontSizeMultiplier = fontSizeMultiplier,
                        onExploreSchemes = onExploreSchemes,
                        suggestedSchemes = SUGGESTED_TO_BOOKMARK_MOCK,
                        onAddBookmark = addBookmark
                    )
                }
            } else if (filteredSavedSchemes.isEmpty()) {
                // When search within saved schemes has no matches
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SearchOff,
                                contentDescription = null,
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(40.dp)
                            )
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI)
                                    "\"$searchQuery\" से कोई सहेजी गई योजना नहीं मिली"
                                else
                                    "No saved schemes match \"$searchQuery\"",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GovNavyPrimary,
                                    textAlign = TextAlign.Center
                                )
                            )
                            TextButton(onClick = {
                                searchQuery = ""
                                selectedCategoryFilter = "ALL"
                            }) {
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) "खोज फ़िल्टर रीसेट करें" else "Clear Search & Filter",
                                    color = GovNavyPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            } else {
                // -------------------------------------------------------------
                // SAVED SCHEME CARDS LIST (With Remove Bookmark & Details CTA)
                // -------------------------------------------------------------
                items(filteredSavedSchemes, key = { it.id }) { scheme ->
                    SavedSchemeCard(
                        scheme = scheme,
                        onRemoveBookmark = { removeBookmark(scheme) },
                        onViewDetails = {
                            selectedSchemeForPreview = scheme
                            onOpenSchemeDetails?.invoke(scheme.id)
                        },
                        currentLanguage = currentLanguage,
                        fontSizeMultiplier = fontSizeMultiplier
                    )
                }
            }

            // Bottom breathing space
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // -------------------------------------------------------------
    // SCHEME DETAILS PREVIEW MODAL DIALOG
    // -------------------------------------------------------------
    selectedSchemeForPreview?.let { scheme ->
        SavedSchemePreviewDialog(
            scheme = scheme,
            onRemoveBookmark = {
                removeBookmark(scheme)
                selectedSchemeForPreview = null
            },
            currentLanguage = currentLanguage,
            fontSizeMultiplier = fontSizeMultiplier,
            onDismiss = { selectedSchemeForPreview = null }
        )
    }

    // -------------------------------------------------------------
    // CLEAR ALL CONFIRMATION DIALOG
    // -------------------------------------------------------------
    if (showClearAllConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showClearAllConfirmationDialog = false },
            title = {
                Text(
                    text = if (currentLanguage == AppLanguage.HINDI) "सभी सहेजी गई योजनाएं हटाएं?" else "Clear All Saved Schemes?",
                    fontWeight = FontWeight.Bold,
                    color = GovNavyPrimary
                )
            },
            text = {
                Text(
                    text = if (currentLanguage == AppLanguage.HINDI)
                        "क्या आप वाकई अपनी बुकमार्क सूची से सभी ${savedSchemesList.size} योजनाओं को हटाना चाहते हैं?"
                    else
                        "Are you sure you want to remove all ${savedSchemesList.size} schemes from your saved watchlist?"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        savedSchemesList = emptyList()
                        showClearAllConfirmationDialog = false
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = if (currentLanguage == AppLanguage.HINDI) "सभी योजनाएं हटा दी गईं" else "All saved schemes cleared",
                                duration = SnackbarDuration.Short
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GovError)
                ) {
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI) "हाँ, हटाएं" else "Yes, Clear All",
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showClearAllConfirmationDialog = false }) {
                    Text(text = if (currentLanguage == AppLanguage.HINDI) "रद्द करें" else "Cancel")
                }
            }
        )
    }
}

// -------------------------------------------------------------
// SAVED SCHEME CARD COMPONENT
// -------------------------------------------------------------

@Composable
fun SavedSchemeCard(
    scheme: SavedSchemeItem,
    onRemoveBookmark: () -> Unit,
    onViewDetails: () -> Unit,
    currentLanguage: AppLanguage,
    fontSizeMultiplier: Float
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable { onViewDetails() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Row: Category Badge + Level Tag + Active Bookmark Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category Pill with domain icon
                Surface(
                    color = scheme.categoryColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, scheme.categoryColor.copy(alpha = 0.25f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = scheme.categoryIcon,
                            contentDescription = null,
                            tint = scheme.categoryColor,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = scheme.category,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = scheme.categoryColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Level Badge
                    val (levelBg, levelText, levelLabel) = when (scheme.level) {
                        SavedGovLevel.CENTRAL -> Triple(
                            Color(0xFFE0F2FE),
                            Color(0xFF0369A1),
                            if (currentLanguage == AppLanguage.HINDI) "केंद्र" else "Central"
                        )
                        SavedGovLevel.STATE -> Triple(
                            Color(0xFFDCFCE7),
                            Color(0xFF15803D),
                            if (currentLanguage == AppLanguage.HINDI) "राज्य" else "State"
                        )
                        SavedGovLevel.UT -> Triple(
                            Color(0xFFF3E8FF),
                            Color(0xFF7E22CE),
                            if (currentLanguage == AppLanguage.HINDI) "यूटी" else "UT"
                        )
                    }

                    Surface(
                        color = levelBg,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "$levelLabel • ${scheme.stateOrUt}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = levelText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    // Active Bookmark Icon (Tap to Remove with visual feedback)
                    IconButton(
                        onClick = onRemoveBookmark,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = if (currentLanguage == AppLanguage.HINDI) "बुकमार्क हटाएं" else "Remove Bookmark",
                            tint = GovSaffron,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // Scheme Name (Bilingual)
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = scheme.getName(currentLanguage),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = GovNavyPrimary,
                        fontSize = (15 * fontSizeMultiplier).sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (currentLanguage == AppLanguage.HINDI) scheme.nameEn else scheme.nameHi,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF64748B),
                        fontSize = (11 * fontSizeMultiplier).sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Issuing Ministry / Department
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AccountBalance,
                    contentDescription = null,
                    tint = Color(0xFF475569),
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = scheme.getMinistry(currentLanguage),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF475569),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Key Benefit Banner
            Surface(
                color = Color(0xFFF0FDF4),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF16A34A)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CurrencyRupee,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI) "मुख्य वित्तीय सहायता" else "Direct Welfare Benefit",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF15803D),
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            )
                        )
                        Text(
                            text = scheme.getBenefit(currentLanguage),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF14532D),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = (12 * fontSizeMultiplier).sp
                            )
                        )
                    }
                }
            }

            // Target Group & Saved Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "🎯 ${scheme.getTargetGroup(currentLanguage)}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF334155),
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = scheme.savedTimestamp,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFF94A3B8),
                        fontSize = 10.sp
                    )
                )
            }

            HorizontalDivider(color = Color(0xFFF1F5F9))

            // Footer Actions: Remove Bookmark + View Details Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Remove Bookmark Button
                TextButton(
                    onClick = onRemoveBookmark,
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.BookmarkBorder,
                            contentDescription = null,
                            tint = Color(0xFFDC2626),
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI) "बुकमार्क हटाएं" else "Remove",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFFDC2626),
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }

                // View Details CTA Button
                Button(
                    onClick = onViewDetails,
                    colors = ButtonDefaults.buttonColors(containerColor = GovNavyPrimary),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI) "विवरण देखें" else "View Details",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// HIGH-ACCESSIBILITY EMPTY STATE COMPONENT
// -------------------------------------------------------------

@Composable
fun SavedSchemesEmptyState(
    currentLanguage: AppLanguage,
    fontSizeMultiplier: Float,
    onExploreSchemes: () -> Unit,
    suggestedSchemes: List<SavedSchemeItem>,
    onAddBookmark: (SavedSchemeItem) -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Visual Graphic with amber container
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFF7ED)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.BookmarkBorder,
                    contentDescription = null,
                    tint = GovSaffron,
                    modifier = Modifier.size(42.dp)
                )
            }

            // Headings
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = if (currentLanguage == AppLanguage.HINDI)
                        "कोई सहेजी गई योजना नहीं है"
                    else
                        "No Saved Schemes Yet",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = GovNavyPrimary,
                        fontSize = (18 * fontSizeMultiplier).sp,
                        textAlign = TextAlign.Center
                    )
                )

                Text(
                    text = if (currentLanguage == AppLanguage.HINDI)
                        "आपने अभी तक किसी योजना को बुकमार्क नहीं किया है। योजनाओं को ब्राउज़ करते समय बुकमार्क (🔖) आइकन दबाकर उन्हें यहां सहेजें।"
                    else
                        "You haven't bookmarked any welfare schemes yet. Tap the bookmark icon on any scheme card to save it for quick offline reference.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF64748B),
                        fontSize = (13 * fontSizeMultiplier).sp,
                        textAlign = TextAlign.Center
                    )
                )
            }

            // Educational Tips Box
            Surface(
                color = Color(0xFFF8FAFC),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI) "💡 योजनाएं सहेजने के लाभ:" else "💡 Why Bookmark Schemes?",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = GovNavyPrimary
                        )
                    )
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI)
                            "• सहेजी गई योजनाएं बिना इंटरनेट के भी तुरंत देखने के लिए उपलब्ध रहती हैं\n• आवश्यक दस्तावेज़ों और आवेदन की अंतिम तिथियों को आसानी से ट्रैक करें\n• परिवार के सदस्यों के लिए उपयुक्त योजनाओं की व्यक्तिगत सूची बनाएं"
                        else
                            "• Saved schemes stay readily accessible for fast offline reference\n• Track required documentation and upcoming DBT disbursal cycles\n• Compare eligibility criteria and benefit amounts across programs",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF475569),
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    )
                }
            }

            // Primary Explore Schemes CTA Button
            Button(
                onClick = onExploreSchemes,
                colors = ButtonDefaults.buttonColors(containerColor = GovNavyPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.AllInclusive,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (currentLanguage == AppLanguage.HINDI) "सभी सरकारी योजनाएं देखें" else "Explore All Government Schemes",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                )
            }

            // 1-Tap Quick Bookmark Suggestions
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = if (currentLanguage == AppLanguage.HINDI) "या इन लोकप्रिय योजनाओं को सहेजें:" else "Or quickly bookmark popular schemes:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.SemiBold
                    )
                )

                suggestedSchemes.forEach { suggestedItem ->
                    Surface(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = suggestedItem.getName(currentLanguage),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GovNavyPrimary
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = suggestedItem.getBenefit(currentLanguage),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFF15803D),
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            OutlinedButton(
                                onClick = { onAddBookmark(suggestedItem) },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                border = BorderStroke(1.dp, GovSaffron)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.BookmarkBorder,
                                    contentDescription = null,
                                    tint = GovSaffron,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) "सहेजें" else "Bookmark",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = GovSaffron,
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
// SCHEME PREVIEW MODAL DIALOG
// -------------------------------------------------------------

@Composable
fun SavedSchemePreviewDialog(
    scheme: SavedSchemeItem,
    onRemoveBookmark: () -> Unit,
    currentLanguage: AppLanguage,
    fontSizeMultiplier: Float,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header with category & actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = scheme.categoryColor.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = scheme.category,
                            color = scheme.categoryColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onRemoveBookmark) {
                            Icon(
                                imageVector = Icons.Default.Bookmark,
                                contentDescription = "Remove Bookmark",
                                tint = GovSaffron
                            )
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color(0xFF64748B)
                            )
                        }
                    }
                }

                // Scrollable Scheme Details
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = scheme.getName(currentLanguage),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = GovNavyPrimary,
                            fontSize = (18 * fontSizeMultiplier).sp
                        )
                    )

                    Text(
                        text = "${scheme.level.name} • ${scheme.stateOrUt}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF0369A1),
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    Text(
                        text = "🏛 ${scheme.getMinistry(currentLanguage)}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF334155),
                            fontWeight = FontWeight.Medium
                        )
                    )

                    // Benefit Box
                    Surface(
                        color = Color(0xFFF0FDF4),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) "वित्तीय व अन्य लाभ:" else "Direct Benefits:",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF15803D),
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = scheme.getBenefit(currentLanguage),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color(0xFF14532D),
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }

                    // Eligibility Criteria
                    Surface(
                        color = Color(0xFFFFFBEB),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) "पात्रता शर्तें:" else "Eligibility Criteria:",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFFB45309),
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = scheme.getEligibility(currentLanguage),
                                style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF78350F))
                            )
                        }
                    }

                    // Required Documents
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI) "आवश्यक दस्तावेज़:" else "Required Documents:",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = GovNavyPrimary
                            )
                        )
                        scheme.requiredDocuments.forEach { doc ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF16A34A),
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = doc,
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF334155))
                                )
                            }
                        }
                    }

                    // Official Portal
                    Surface(
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                contentDescription = null,
                                tint = GovNavyPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = scheme.officialPortal,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = GovNavyPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                }

                // Modal Footer
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = GovNavyPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI) "बंद करें (Close)" else "Close",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
