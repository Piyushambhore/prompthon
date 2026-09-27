package org.jansaarthi.app.ui.screens.categories

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import org.jansaarthi.app.ui.localization.getJanSaarthiStrings
import org.jansaarthi.app.ui.theme.*

/**
 * ----------------------------------------------------------------------------
 * JanSaarthi Government Scheme Categories Screen (Frontend UI Only)
 * ----------------------------------------------------------------------------
 * Showcases all 15 official myScheme broad categories in a 2-column responsive grid:
 * 1. Agriculture, Rural & Environment
 * 2. Banking, Financial Services & Insurance
 * 3. Business & Entrepreneurship
 * 4. Education & Learning
 * 5. Health & Wellness
 * 6. Housing & Shelter
 * 7. Public Safety, Law & Justice
 * 8. Science, IT & Communications
 * 9. Skills & Employment
 * 10. Social Welfare & Empowerment
 * 11. Sports & Culture
 * 12. Transport & Infrastructure
 * 13. Travel & Tourism
 * 14. Utility & Sanitation
 * 15. Women & Child
 *
 * Each card features:
 * - 2-column responsive layout
 * - Suitable Material 3 icons
 * - Subtle unique background and border tints
 * - Category name (English + localized where active)
 * - Clear, accessible short description
 * - Scheme-count badge / placeholder
 * - Interactive click handling with Category Preview modal
 */

data class GovernmentSchemeCategory(
    val id: String,
    val number: Int,
    val nameEn: String,
    val nameHi: String,
    val descriptionEn: String,
    val descriptionHi: String,
    val schemeCount: Int,
    val icon: ImageVector,
    val accentColor: Color,
    val tintColor: Color,
    val borderColor: Color,
    val sampleSchemes: List<String>
) {
    fun getName(language: AppLanguage): String =
        if (language == AppLanguage.HINDI) nameHi else nameEn

    fun getDescription(language: AppLanguage): String =
        if (language == AppLanguage.HINDI) descriptionHi else descriptionEn
}

val OFFICIAL_15_CATEGORIES = listOf(
    GovernmentSchemeCategory(
        id = "CAT-01",
        number = 1,
        nameEn = "Agriculture, Rural & Environment",
        nameHi = "कृषि, ग्रामीण एवं पर्यावरण",
        descriptionEn = "Farm subsidies, crop insurance, irrigation, solar pumps, soil health and rural livelihoods.",
        descriptionHi = "कृषि सब्सिडी, फसल बीमा, सिंचाई, सौर पंप, मृदा स्वास्थ्य एवं ग्रामीण आजीविका।",
        schemeCount = 64,
        icon = Icons.Default.Agriculture,
        accentColor = Color(0xFF15803D), // Emerald Green
        tintColor = Color(0xFFF0FDF4),
        borderColor = Color(0xFFBBF7D0),
        sampleSchemes = listOf("PM-KISAN Samman Nidhi", "PM Fasal Bima Yojana", "PM Krishi Sinchayee Yojana", "Soil Health Card Scheme")
    ),
    GovernmentSchemeCategory(
        id = "CAT-02",
        number = 2,
        nameEn = "Banking, Financial Services & Insurance",
        nameHi = "बैंकिंग, वित्तीय सेवाएं एवं बीमा",
        descriptionEn = "Zero-balance accounts, micro-insurance, accidental cover, low-interest credit and pensions.",
        descriptionHi = "जन धन खाते, सूक्ष्म बीमा, दुर्घटना सुरक्षा, रियायती ऋण एवं गारंटीड पेंशन।",
        schemeCount = 38,
        icon = Icons.Default.AccountBalance,
        accentColor = Color(0xFF3730A3), // Royal Indigo
        tintColor = Color(0xFFEEF2FF),
        borderColor = Color(0xFFC7D2FE),
        sampleSchemes = listOf("PM Jan Dhan Yojana", "PM Suraksha Bima Yojana", "PM Jeevan Jyoti Bima", "Atal Pension Yojana")
    ),
    GovernmentSchemeCategory(
        id = "CAT-03",
        number = 3,
        nameEn = "Business & Entrepreneurship",
        nameHi = "व्यवसाय एवं उद्यमिता",
        descriptionEn = "Collateral-free MSME loans, startup seed funds, khadi village industries and credit guarantees.",
        descriptionHi = "बिना गारंटी एमएसएमई ऋण, स्टार्टअप सीड फंड, खादी ग्रामोद्योग एवं क्रेडिट गारंटी।",
        schemeCount = 42,
        icon = Icons.Default.BusinessCenter,
        accentColor = Color(0xFF9A3412), // Warm Amber
        tintColor = Color(0xFFFFF7ED),
        borderColor = Color(0xFFFFEDD5),
        sampleSchemes = listOf("PM Mudra Yojana", "Stand-Up India Scheme", "PMEGP Credit Linked Subsidy", "CGTMSE Collateral Support")
    ),
    GovernmentSchemeCategory(
        id = "CAT-04",
        number = 4,
        nameEn = "Education & Learning",
        nameHi = "शिक्षा एवं शिक्षण",
        descriptionEn = "Pre & post-matric scholarships, fee waivers, higher education fellowships and midday meals.",
        descriptionHi = "छात्रवृत्तियां, शिक्षण शुल्क प्रतिपूर्ति, उच्च शिक्षा फेलोशिप एवं मध्याह्न भोजन।",
        schemeCount = 55,
        icon = Icons.Default.School,
        accentColor = Color(0xFF1D4ED8), // Royal Blue
        tintColor = Color(0xFFEFF6FF),
        borderColor = Color(0xFFBFDBFE),
        sampleSchemes = listOf("National Scholarship Portal (NSP)", "PM POSHAN Scheme", "Samagra Shiksha Abhiyan", "Merit-cum-Means Scholarships")
    ),
    GovernmentSchemeCategory(
        id = "CAT-05",
        number = 5,
        nameEn = "Health & Wellness",
        nameHi = "स्वास्थ्य एवं कल्याण",
        descriptionEn = "Cashless secondary & tertiary hospital care, maternal aid, generic medicines and vaccinations.",
        descriptionHi = "₹5 लाख कैशलेस अस्पताल इलाज, मातृत्व सहायता, जन औषधि एवं संपूर्ण टीकाकरण।",
        schemeCount = 49,
        icon = Icons.Default.HealthAndSafety,
        accentColor = Color(0xFF0F766E), // Teal Cyan
        tintColor = Color(0xFFF0FDFA),
        borderColor = Color(0xFF99F6E4),
        sampleSchemes = listOf("Ayushman Bharat PM-JAY", "PM Matru Vandana Yojana", "Pradhan Mantri Jan Aushadhi", "Mission Indradhanush")
    ),
    GovernmentSchemeCategory(
        id = "CAT-06",
        number = 6,
        nameEn = "Housing & Shelter",
        nameHi = "आवास एवं आश्रय",
        descriptionEn = "Financial assistance and interest subsidies for constructing pucca homes in rural and urban areas.",
        descriptionHi = "ग्रामीण व शहरी क्षेत्रों में पक्का मकान निर्माण हेतु वित्तीय सहायता एवं ब्याज सब्सिडी।",
        schemeCount = 27,
        icon = Icons.Default.Home,
        accentColor = Color(0xFFB45309), // Amber Gold
        tintColor = Color(0xFFFFFBEB),
        borderColor = Color(0xFFFDE68A),
        sampleSchemes = listOf("PM Awas Yojana (Gramin)", "PM Awas Yojana (Urban)", "Credit Linked Subsidy Scheme (CLSS)")
    ),
    GovernmentSchemeCategory(
        id = "CAT-07",
        number = 7,
        nameEn = "Public Safety, Law & Justice",
        nameHi = "सार्वजनिक सुरक्षा, कानून एवं न्याय",
        descriptionEn = "Free legal aid, digital tele-law consultations, victim relief, legal awareness and emergency 112.",
        descriptionHi = "मुफ़्त कानूनी सहायता, टेली-लॉ परामर्श, पीड़ित मुआवजा एवं 112 आपातकालीन सेवा।",
        schemeCount = 19,
        icon = Icons.Default.Gavel,
        accentColor = Color(0xFF991B1B), // Crimson Red
        tintColor = Color(0xFFFEF2F2),
        borderColor = Color(0xFFFECACA),
        sampleSchemes = listOf("Tele-Law Citizen Portal", "NALSA Free Legal Aid", "Emergency Response Support (112)", "Victim Compensation Fund")
    ),
    GovernmentSchemeCategory(
        id = "CAT-08",
        number = 8,
        nameEn = "Science, IT & Communications",
        nameHi = "विज्ञान, आईटी एवं संचार",
        descriptionEn = "Rural digital literacy, high-speed broadband, tech research grants and electronics manufacturing.",
        descriptionHi = "डिजिटल साक्षरता, ग्रामीण भारतनेट ब्रॉडबैंड, विज्ञान अनुसंधान अनुदान एवं आईटी प्रोत्साहन।",
        schemeCount = 24,
        icon = Icons.Default.Devices,
        accentColor = Color(0xFF6D28D9), // Purple
        tintColor = Color(0xFFFAF5FF),
        borderColor = Color(0xFFE9D5FF),
        sampleSchemes = listOf("BharatNet Optical Fiber", "PMGDISHA Digital Literacy", "INSPIRE Science Fellowships", "Electronics PLI Scheme")
    ),
    GovernmentSchemeCategory(
        id = "CAT-09",
        number = 9,
        nameEn = "Skills & Employment",
        nameHi = "कौशल विकास एवं रोजगार",
        descriptionEn = "Free certified vocational training, apprentice stipends, artisan toolkits and 100 days wage work.",
        descriptionHi = "मुफ़्त प्रमाणित कौशल प्रशिक्षण, अप्रेंटिसशिप स्टाइपेंड, टूलकिट एवं 100 दिन का गारंटीड रोजगार।",
        schemeCount = 51,
        icon = Icons.Default.Handyman,
        accentColor = Color(0xFF334155), // Slate Neutral
        tintColor = Color(0xFFF8FAFC),
        borderColor = Color(0xFFCBD5E1),
        sampleSchemes = listOf("PM Kaushal Vikas Yojana (PMKVY)", "MGNREGA Rural Wage Work", "PM Vishwakarma Artisan Support", "National Apprenticeship")
    ),
    GovernmentSchemeCategory(
        id = "CAT-10",
        number = 10,
        nameEn = "Social Welfare & Empowerment",
        nameHi = "सामाजिक कल्याण एवं सशक्तीकरण",
        descriptionEn = "Disability assistive aids, senior citizen pensions, destitute support and SC/ST/OBC empowerment.",
        descriptionHi = "दिव्यांग सहायक उपकरण, वृद्धावस्था पेंशन, विधवा सहायता एवं एससी/एसटी/ओबीसी उत्थान।",
        schemeCount = 68,
        icon = Icons.Default.Elderly,
        accentColor = Color(0xFF581C87), // Deep Violet
        tintColor = Color(0xFFF5F3FF),
        borderColor = Color(0xFFDDD6FE),
        sampleSchemes = listOf("Indira Gandhi National Old Age Pension", "ADIP Scheme for Divyangjan", "PM-DAKSH Skill Empowerment", "Pre-Matric SC/ST Scholarships")
    ),
    GovernmentSchemeCategory(
        id = "CAT-11",
        number = 11,
        nameEn = "Sports & Culture",
        nameHi = "खेल एवं संस्कृति",
        descriptionEn = "Athlete coaching scholarships, Khelo India facilities, folk artist stipends and heritage grants.",
        descriptionHi = "एथलीट कोचिंग छात्रवृत्तियां, खेलो इंडिया बुनियादी ढांचा, लोक कलाकार पेंशन एवं विरासत संरक्षण।",
        schemeCount = 22,
        icon = Icons.Default.EmojiEvents,
        accentColor = Color(0xFFC2410C), // Saffron Orange
        tintColor = Color(0xFFFFF7ED),
        borderColor = Color(0xFFFED7AA),
        sampleSchemes = listOf("Khelo India Sports Program", "Target Olympic Podium Scheme (TOPS)", "National Sports Development Fund", "Cultural Talent Search")
    ),
    GovernmentSchemeCategory(
        id = "CAT-12",
        number = 12,
        nameEn = "Transport & Infrastructure",
        nameHi = "परिवहन एवं बुनियादी ढांचा",
        descriptionEn = "All-weather village road connections, electric mobility incentives (FAME) and driver training.",
        descriptionHi = "पक्की ग्रामीण सड़कें (पीएमजीएसवाई), इलेक्ट्रिक वाहन सब्सिडी (FAME) एवं परिवहन चालक प्रशिक्षण।",
        schemeCount = 16,
        icon = Icons.Default.DirectionsBus,
        accentColor = Color(0xFF1E293B), // Dark Slate
        tintColor = Color(0xFFF1F5F9),
        borderColor = Color(0xFFE2E8F0),
        sampleSchemes = listOf("PM Gram Sadak Yojana (PMGSY)", "FAME India EV Subsidies", "National Highway Fastag Concessions")
    ),
    GovernmentSchemeCategory(
        id = "CAT-13",
        number = 13,
        nameEn = "Travel & Tourism",
        nameHi = "यात्रा एवं पर्यटन",
        descriptionEn = "Pilgrimage destination upgrades, eco-tourism homestay assistance, guide training and regional flights.",
        descriptionHi = "तीर्थस्थल पुनरुद्धार, ग्रामीण होमस्टे सब्सिडी, टूर गाइड प्रशिक्षण एवं सस्ती उड़ान योजना।",
        schemeCount = 18,
        icon = Icons.Default.Flight,
        accentColor = Color(0xFF0E7490), // Deep Cyan
        tintColor = Color(0xFFECFEFF),
        borderColor = Color(0xFFA5F3FC),
        sampleSchemes = listOf("PRASHAD Pilgrimage Rejuvenation", "Swadesh Darshan 2.0 Theme Circuits", "UDAN Regional Aviation Scheme")
    ),
    GovernmentSchemeCategory(
        id = "CAT-14",
        number = 14,
        nameEn = "Utility & Sanitation",
        nameHi = "उपयोगिता एवं स्वच्छता",
        descriptionEn = "Piped tap water to every village home, household toilets, clean LPG gas and free rooftop solar.",
        descriptionHi = "हर घर नल से जल, स्वच्छ शौचालय प्रोत्साहन, मुफ़्त एलपीजी गैस एवं रूफटॉप सोलर बिजली।",
        schemeCount = 33,
        icon = Icons.Default.WaterDrop,
        accentColor = Color(0xFF0369A1), // Ocean Sky
        tintColor = Color(0xFFF0F9FF),
        borderColor = Color(0xFFBAE6FD),
        sampleSchemes = listOf("Jal Jeevan Mission (Har Ghar Jal)", "Swachh Bharat Mission (Grameen)", "PM Ujjwala Yojana (LPG)", "PM Surya Ghar (Muft Bijli)")
    ),
    GovernmentSchemeCategory(
        id = "CAT-15",
        number = 15,
        nameEn = "Women & Child",
        nameHi = "महिला एवं बाल कल्याण",
        descriptionEn = "Girl child education savings, maternal cash benefits, safety helplines and anganwadi nutrition.",
        descriptionHi = "सुकन्या समृद्धि बचत, मातृत्व नकद लाभ, महिला हेल्पलाइन एवं आंगनवाड़ी संपूर्ण पोषण।",
        schemeCount = 45,
        icon = Icons.Default.FamilyRestroom,
        accentColor = Color(0xFFBE185D), // Rose Magenta
        tintColor = Color(0xFFFDF2F8),
        borderColor = Color(0xFFFBCFE8),
        sampleSchemes = listOf("Beti Bachao Beti Padhao", "Sukanya Samriddhi Account", "Mission Poshan 2.0 Anganwadi", "PM Matru Vandana Yojana")
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    currentLanguage: AppLanguage = AppLanguage.HINDI,
    onSelectLanguage: (AppLanguage) -> Unit = {},
    isLargeFont: Boolean = false,
    onFontScaleToggle: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onSelectCategory: (GovernmentSchemeCategory) -> Unit = {},
    onNavigateHome: () -> Unit = {},
    onNavigateSchemes: () -> Unit = {},
    onNavigateApplications: () -> Unit = {},
    onNavigateDocuments: () -> Unit = {},
    onNavigateProfile: () -> Unit = {},
    onNavigateAllSchemesList: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val strings = getJanSaarthiStrings(currentLanguage)
    val fontSizeMultiplier = if (isLargeFont) 1.15f else 1.0f

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryForModal by remember { mutableStateOf<GovernmentSchemeCategory?>(null) }

    // Filter categories based on search query (English and Hindi names + descriptions)
    val filteredCategories = remember(searchQuery, currentLanguage) {
        val q = searchQuery.trim().lowercase()
        if (q.isEmpty()) {
            OFFICIAL_15_CATEGORIES
        } else {
            OFFICIAL_15_CATEGORIES.filter { cat ->
                cat.nameEn.lowercase().contains(q) ||
                        cat.nameHi.lowercase().contains(q) ||
                        cat.descriptionEn.lowercase().contains(q) ||
                        cat.descriptionHi.lowercase().contains(q) ||
                        cat.sampleSchemes.any { it.lowercase().contains(q) }
            }
        }
    }

    val totalSchemesCount = remember { OFFICIAL_15_CATEGORIES.sumOf { it.schemeCount } }

    Scaffold(
        topBar = {
            Column {
                // National Tricolor Banner
                GovernmentBanner(language = currentLanguage, fontSizeMultiplier = fontSizeMultiplier)

                // Top App Bar
                Surface(
                    color = Color.White,
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            IconButton(onClick = onNavigateBack) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = GovNavyPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) "योजना श्रेणियां" else "Scheme Categories",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GovNavyPrimary,
                                        fontSize = (17 * fontSizeMultiplier).sp
                                    )
                                )
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI)
                                        "15 आधिकारिक राष्ट्रीय श्रेणियां • myScheme"
                                    else
                                        "15 Official National Categories • myScheme",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF64748B),
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        // Category count badge & Full List trigger
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                color = GovNavyContainer,
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                                modifier = Modifier.clickable { onNavigateAllSchemesList() }
                            ) {
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) "समस्त सूची ↗" else "All List ↗",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = GovNavyPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                )
                            }

                            Surface(
                                color = Color(0xFFEFF6FF),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, Color(0xFFBFDBFE))
                            ) {
                                Text(
                                    text = "15 / 15",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
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
        },
        bottomBar = {
            JanSaarthiBottomNav(
                selectedTab = 1,
                currentLanguage = currentLanguage,
                pendingApplications = 2,
                onNavigateHome = onNavigateHome,
                onNavigateSchemes = onNavigateSchemes,
                onNavigateApplications = onNavigateApplications,
                onNavigateDocuments = onNavigateDocuments,
                onNavigateProfile = onNavigateProfile
            )
        },
        modifier = modifier
            .fillMaxSize()
            .background(GovBackground)
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(
                start = 14.dp,
                end = 14.dp,
                top = innerPadding.calculateTopPadding() + 8.dp,
                bottom = innerPadding.calculateBottomPadding() + 20.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Item 1: Accessibility Controls
            item(span = { GridItemSpan(2) }) {
                AccessibilityBar(
                    currentLanguage = currentLanguage,
                    onSelectLanguage = onSelectLanguage,
                    isLargeFont = isLargeFont,
                    onFontScaleToggle = onFontScaleToggle
                )
            }

            // Item 2: National Overview Banner
            item(span = { GridItemSpan(2) }) {
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    shadowElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Surface(
                                    color = GovNavyContainer,
                                    shape = CircleShape,
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Category,
                                            contentDescription = null,
                                            tint = GovNavyPrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) "समग्र राष्ट्रीय वर्गीकरण" else "Official Classification",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GovNavyPrimary,
                                        fontSize = (14 * fontSizeMultiplier).sp
                                    )
                                )
                            }

                            Surface(
                                color = Color(0xFFDCFCE7),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "$totalSchemesCount+ " + (if (currentLanguage == AppLanguage.HINDI) "योजनाएं" else "Schemes"),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFF15803D),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI)
                                "भारत सरकार के myScheme पोर्टल अनुसार सभी 15 आधिकारिक व्यापक श्रेणियों में विभाजित योजनाएं। कार्ड पर टैप करके योजनाएं देखें।"
                            else
                                "Explore Central and State welfare initiatives organized across all 15 official myScheme broad domains. Tap any card to inspect.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF475569),
                                fontSize = (12 * fontSizeMultiplier).sp,
                                lineHeight = 16.sp
                            )
                        )
                    }
                }
            }

            // Item 3: Search Bar
            item(span = { GridItemSpan(2) }) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI)
                                "श्रेणी खोजें (उदा: कृषि, स्वास्थ्य, शिक्षा, आवास)..."
                            else
                                "Filter 15 categories (e.g. Agriculture, Health, Housing)...",
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
                            tint = GovNavyPrimary
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
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = GovNavyPrimary,
                        unfocusedBorderColor = GovBorder
                    )
                )
            }

            // Empty Search State
            if (filteredCategories.isEmpty()) {
                item(span = { GridItemSpan(2) }) {
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SearchOff,
                                contentDescription = null,
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(40.dp)
                            )
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI)
                                    "कोई श्रेणी नहीं मिली"
                                else
                                    "No matching categories found",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF64748B)
                                )
                            )
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI)
                                    "कृपया कोई अन्य शब्द लिखकर खोजें।"
                                else
                                    "Try searching with another keyword like 'Health' or 'Farmer'.",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF94A3B8))
                            )
                        }
                    }
                }
            } else {
                // 2-Column Responsive Category Cards
                items(filteredCategories, key = { it.id }) { category ->
                    CategoryCardItem(
                        category = category,
                        currentLanguage = currentLanguage,
                        fontSizeMultiplier = fontSizeMultiplier,
                        onClick = {
                            onSelectCategory(category)
                        }
                    )
                }
            }
        }
    }

    // Category Detail Modal Dialog
    selectedCategoryForModal?.let { category ->
        Dialog(onDismissRequest = { selectedCategoryForModal = null }) {
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
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                color = category.tintColor,
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, category.borderColor),
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = category.icon,
                                        contentDescription = null,
                                        tint = category.accentColor,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            Column {
                                Surface(
                                    color = category.tintColor,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "Category #${category.number}",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = category.accentColor,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        )
                                    )
                                }
                                Text(
                                    text = category.getName(currentLanguage),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GovNavyPrimary,
                                        fontSize = 15.sp
                                    )
                                )
                            }
                        }

                        IconButton(
                            onClick = { selectedCategoryForModal = null },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = strings.closeButton,
                                tint = Color(0xFF64748B)
                            )
                        }
                    }

                    HorizontalDivider(color = Color(0xFFE2E8F0))

                    // Description
                    Text(
                        text = category.getDescription(currentLanguage),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF334155),
                            lineHeight = 18.sp
                        )
                    )

                    // Scheme Count Badge
                    Surface(
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) "कुल उपलब्ध योजनाएं:" else "Total Available Schemes:",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF64748B))
                            )
                            Text(
                                text = "${category.schemeCount} " + (if (currentLanguage == AppLanguage.HINDI) "योजनाएं" else "Schemes"),
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = category.accentColor
                                )
                            )
                        }
                    }

                    // Key Flagship Schemes Under this Category
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI) "प्रमुख प्रमुख योजनाएं:" else "Key Flagship Initiatives:",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = GovNavyPrimary
                        )
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        category.sampleSchemes.forEach { schemeName ->
                            Surface(
                                color = Color(0xFFF8FAFC),
                                shape = RoundedCornerShape(6.dp),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = category.accentColor,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = schemeName,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = GovTextPrimary,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { selectedCategoryForModal = null },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(strings.closeButton)
                        }

                        Button(
                            onClick = {
                                selectedCategoryForModal = null
                                onSelectCategory(category)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GovNavyPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1.4f)
                        ) {
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) "योजनाएं देखें →" else "Explore →",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 2-Column Responsive Card for Each Scheme Category
 */
@Composable
fun CategoryCardItem(
    category: GovernmentSchemeCategory,
    currentLanguage: AppLanguage,
    fontSizeMultiplier: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = category.tintColor),
        border = BorderStroke(1.dp, category.borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 210.dp)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Row: Category Icon + Scheme Count Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, category.borderColor),
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = category.icon,
                            contentDescription = null,
                            tint = category.accentColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, category.borderColor)
                ) {
                    Text(
                        text = "${category.schemeCount}",
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = category.accentColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Category Name
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.getName(currentLanguage),
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = GovNavyPrimary,
                        fontSize = (13 * fontSizeMultiplier).sp,
                        lineHeight = 17.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Short Description
                Text(
                    text = category.getDescription(currentLanguage),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF475569),
                        fontSize = (10.5f * fontSizeMultiplier).sp,
                        lineHeight = 14.sp
                    ),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Bottom CTA
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${category.schemeCount} " + (if (currentLanguage == AppLanguage.HINDI) "योजनाएं" else "Schemes"),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = category.accentColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                )

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Explore",
                    tint = category.accentColor,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}
