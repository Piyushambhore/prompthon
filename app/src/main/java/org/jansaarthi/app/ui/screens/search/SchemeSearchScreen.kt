package org.jansaarthi.app.ui.screens.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import org.jansaarthi.app.data.model.AppLanguage
import org.jansaarthi.app.ui.components.AccessibilityBar
import org.jansaarthi.app.ui.components.GovernmentBanner
import org.jansaarthi.app.ui.theme.*

/**
 * ----------------------------------------------------------------------------
 * JanSaarthi Global Government Scheme Search Screen (Frontend UI Only)
 * ----------------------------------------------------------------------------
 * A unified, multi-dimensional search engine for citizens across India.
 *
 * Core Search Capabilities across 6 Dimensions:
 * 1. Scheme Name: Bilingual English + Hindi + Acronyms (e.g. PM-KISAN, Ladki Bahin, Ayushman)
 * 2. Category: Welfare domains (Agriculture, Healthcare, Women & Child, Education, Housing, etc.)
 * 3. Ministry: Issuing authorities (Ministry of Agriculture, Health Authority, Rural Dev, etc.)
 * 4. State/UT: Jurisdiction filters (National, Maharashtra, NCT of Delhi, Tamil Nadu, Bihar, etc.)
 * 5. Benefit: Direct monetary figures & assistance modes (₹6,000 DBT, ₹1,500/mo, ₹5 Lakhs cover)
 * 6. Eligibility Keyword: Target groups & criteria (Farmer, Women, Student, Artisan, BPL, etc.)
 *
 * Added Features:
 * - Interactive Recent Searches (removable chips + Clear All action)
 * - Suggested / Trending Searches (curated one-tap search pills)
 * - Multi-criteria Filters modal sheet & horizontal quick-filters
 * - Clear Filters action (1-tap reset)
 * - High-accessibility No Results screen with search tips and instant reset
 * - Scheme preview modal with direct action CTA
 *
 * Frontend UI Only: Realistic local mock data. Zero backend, database, or API.
 */

// -------------------------------------------------------------
// ENUMS & MODELS FOR SEARCH ENGINE
// -------------------------------------------------------------

enum class SearchGovLevel(val labelEn: String, val labelHi: String, val icon: ImageVector) {
    ALL("All Levels", "सभी स्तर", Icons.Default.AllInclusive),
    CENTRAL("Central Govt", "केंद्र सरकार", Icons.Default.AccountBalance),
    STATE("State Govt", "राज्य सरकार", Icons.Default.Apartment),
    UT("Union Territory", "केंद्र शासित प्रदेश", Icons.Default.LocationCity)
}

enum class BenefitFilterType(val labelEn: String, val labelHi: String, val icon: ImageVector) {
    ALL("All Benefits", "सभी लाभ", Icons.Default.AllInclusive),
    DBT_CASH("Cash / DBT", "नकद / डीबीटी", Icons.Default.CurrencyRupee),
    HEALTHCARE("Health Cover", "स्वास्थ्य सुरक्षा", Icons.Default.HealthAndSafety),
    LOANS("Enterprise Loan", "व्यवसाय ऋण", Icons.Default.BusinessCenter),
    HOUSING("Housing Grant", "आवास सहायता", Icons.Default.Home),
    SUBSIDY("Subsidy / In-Kind", "सब्सिडी व उपकरण", Icons.Default.Handyman),
    FREE_SERVICE("Free Travel / Pass", "मुफ़्त यात्रा व सेवा", Icons.Default.DirectionsBus)
}

enum class BeneficiaryGroup(val labelEn: String, val labelHi: String) {
    ALL("All Citizens", "सभी नागरिक"),
    FARMERS("Farmers & Ag", "किसान"),
    WOMEN("Women & Girls", "महिलाएं व बेटियां"),
    STUDENTS("Students & Youth", "विद्यार्थी व युवा"),
    ARTISANS("Artisans & Craftsmen", "कारीगर व शिल्पकार"),
    SENIOR_CITIZENS("Senior Citizens (60+)", "वरिष्ठ नागरिक"),
    RURAL_FAMILIES("Rural Families", "ग्रामीण परिवार"),
    STREET_VENDORS("Street Vendors / MSME", "लघु उद्यमी व विक्रेता")
}

data class SearchGovernmentScheme(
    val id: String,
    val nameEn: String,
    val nameHi: String,
    val acronyms: List<String>,
    val level: SearchGovLevel,
    val stateOrUt: String,
    val ministryOrDeptEn: String,
    val ministryOrDeptHi: String,
    val category: String,
    val categoryIcon: ImageVector,
    val categoryColor: Color,
    val benefitSummaryEn: String,
    val benefitSummaryHi: String,
    val benefitType: BenefitFilterType,
    val financialAmountValue: Long,
    val eligibilityKeywords: List<String>,
    val eligibilitySummaryEn: String,
    val eligibilitySummaryHi: String,
    val targetBeneficiary: BeneficiaryGroup,
    val isDbtEnabled: Boolean = true,
    val applicationMode: String,
    val officialPortal: String,
    val documentsNeeded: List<String>
) {
    fun getName(lang: AppLanguage): String = if (lang == AppLanguage.HINDI) nameHi else nameEn
    fun getMinistry(lang: AppLanguage): String = if (lang == AppLanguage.HINDI) ministryOrDeptHi else ministryOrDeptEn
    fun getBenefit(lang: AppLanguage): String = if (lang == AppLanguage.HINDI) benefitSummaryHi else benefitSummaryEn
    fun getEligibility(lang: AppLanguage): String = if (lang == AppLanguage.HINDI) eligibilitySummaryHi else eligibilitySummaryEn
}

data class MatchedSearchAttributes(
    val matchedByName: Boolean = false,
    val matchedByCategory: Boolean = false,
    val matchedByMinistry: Boolean = false,
    val matchedByState: Boolean = false,
    val matchedByBenefit: Boolean = false,
    val matchedByEligibility: Boolean = false
) {
    val anyMatched: Boolean
        get() = matchedByName || matchedByCategory || matchedByMinistry || matchedByState || matchedByBenefit || matchedByEligibility

    fun getMatchTags(lang: AppLanguage): List<String> {
        val tags = mutableListOf<String>()
        if (matchedByName) tags.add(if (lang == AppLanguage.HINDI) "योजना नाम" else "Scheme Name")
        if (matchedByCategory) tags.add(if (lang == AppLanguage.HINDI) "श्रेणी" else "Category")
        if (matchedByMinistry) tags.add(if (lang == AppLanguage.HINDI) "मंत्रालय" else "Ministry")
        if (matchedByState) tags.add(if (lang == AppLanguage.HINDI) "राज्य/यूटी" else "State/UT")
        if (matchedByBenefit) tags.add(if (lang == AppLanguage.HINDI) "लाभ विवरण" else "Benefit")
        if (matchedByEligibility) tags.add(if (lang == AppLanguage.HINDI) "पात्रता" else "Eligibility")
        return tags
    }
}

// -------------------------------------------------------------
// REALISTIC MOCK DATASET ACROSS 6 SEARCH DIMENSIONS
// -------------------------------------------------------------

val GLOBAL_SEARCH_SCHEMES_MOCK: List<SearchGovernmentScheme> = listOf(
    SearchGovernmentScheme(
        id = "SCH-01",
        nameEn = "PM Kisan Samman Nidhi",
        nameHi = "प्रधानमंत्री किसान सम्मान निधि (पीएम-किसान)",
        acronyms = listOf("PM-KISAN", "PMKISAN", "KISAN", "PM KISAN"),
        level = SearchGovLevel.CENTRAL,
        stateOrUt = "National (All States & UTs)",
        ministryOrDeptEn = "Ministry of Agriculture & Farmers Welfare",
        ministryOrDeptHi = "कृषि एवं किसान कल्याण मंत्रालय",
        category = "Agriculture, Rural & Environment",
        categoryIcon = Icons.Default.Agriculture,
        categoryColor = Color(0xFF15803D),
        benefitSummaryEn = "₹6,000/year via direct DBT in 3 equal installments of ₹2,000",
        benefitSummaryHi = "₹6,000 प्रति वर्ष सीधे बैंक खाते में 3 समान किस्तों में (डीबीटी)",
        benefitType = BenefitFilterType.DBT_CASH,
        financialAmountValue = 6000,
        eligibilityKeywords = listOf("farmer", "landholder", "cultivable", "kisan", "agriculture", "land", "annadata", "small farmer"),
        eligibilitySummaryEn = "Landholding farmer families with cultivable land. Aadhaar & e-KYC mandatory.",
        eligibilitySummaryHi = "खेती योग्य भूमि वाले किसान परिवार। आधार और ई-केवाईसी अनिवार्य।",
        targetBeneficiary = BeneficiaryGroup.FARMERS,
        isDbtEnabled = true,
        applicationMode = "Online at pmkisan.gov.in or local CSC Centre",
        officialPortal = "https://pmkisan.gov.in",
        documentsNeeded = listOf("Aadhaar Card", "Land Ownership (7/12 / Khasra)", "Bank Passbook")
    ),
    SearchGovernmentScheme(
        id = "SCH-02",
        nameEn = "Ayushman Bharat PM-JAY (Golden Card)",
        nameHi = "आयुष्मान भारत प्रधानमंत्री जन आरोग्य योजना",
        acronyms = listOf("PMJAY", "AB-PMJAY", "AYUSHMAN", "GOLDEN CARD"),
        level = SearchGovLevel.CENTRAL,
        stateOrUt = "National (All States & UTs)",
        ministryOrDeptEn = "National Health Authority / Ministry of Health",
        ministryOrDeptHi = "राष्ट्रीय स्वास्थ्य प्राधिकरण / स्वास्थ्य मंत्रालय",
        category = "Health & Wellness",
        categoryIcon = Icons.Default.HealthAndSafety,
        categoryColor = Color(0xFF0F766E),
        benefitSummaryEn = "₹5,00,000 cashless family hospitalisation cover per year",
        benefitSummaryHi = "प्रति परिवार प्रति वर्ष ₹5,00,000 तक का मुफ़्त कैशलेस अस्पताल इलाज",
        benefitType = BenefitFilterType.HEALTHCARE,
        financialAmountValue = 500000,
        eligibilityKeywords = listOf("health", "hospital", "illness", "treatment", "cashless", "secc", "senior citizen", "70 years", "ayushman card", "surgery"),
        eligibilitySummaryEn = "SECC-deprived rural/urban families and all senior citizens aged 70+ years.",
        eligibilitySummaryHi = "एसईसीसी के पात्र गरीब परिवार एवं 70 वर्ष से अधिक आयु के सभी वरिष्ठ नागरिक।",
        targetBeneficiary = BeneficiaryGroup.SENIOR_CITIZENS,
        isDbtEnabled = false,
        applicationMode = "Instant Ayushman card via beneficiary.nha.gov.in or hospital helpdesk",
        officialPortal = "https://pmjay.gov.in",
        documentsNeeded = listOf("Aadhaar Card", "Ration Card (NFSA)")
    ),
    SearchGovernmentScheme(
        id = "SCH-03",
        nameEn = "Mukhyamantri Majhi Ladki Bahin Yojana",
        nameHi = "मुख्यमंत्री माझी लाडकी बहीण योजना",
        acronyms = listOf("LADKI BAHIN", "LADLI BEHAN", "MAJHI LADKI"),
        level = SearchGovLevel.STATE,
        stateOrUt = "Maharashtra",
        ministryOrDeptEn = "Department of Women & Child Development (Govt of Maharashtra)",
        ministryOrDeptHi = "महिला व बाल विकास विभाग (महाराष्ट्र शासन)",
        category = "Women & Child Development",
        categoryIcon = Icons.Default.FamilyRestroom,
        categoryColor = Color(0xFFBE185D),
        benefitSummaryEn = "₹1,500 monthly direct cash transfer (₹18,000/year) into woman's bank account",
        benefitSummaryHi = "₹1,500 प्रति माह सीधे बैंक खाते में (वार्षिक ₹18,000) महिला स्वावलंबन सहायता",
        benefitType = BenefitFilterType.DBT_CASH,
        financialAmountValue = 18000,
        eligibilityKeywords = listOf("women", "ladki", "bahin", "maharashtra", "monthly", "1500", "cash", "housewife", "dbt", "nari"),
        eligibilitySummaryEn = "Women aged 21-65 years residing in Maharashtra with annual family income under ₹2.5 Lakhs.",
        eligibilitySummaryHi = "महाराष्ट्र की 21-65 वर्ष की महिलाएं, पारिवारिक वार्षिक आय ₹2.5 लाख से कम।",
        targetBeneficiary = BeneficiaryGroup.WOMEN,
        isDbtEnabled = true,
        applicationMode = "Nari Shakti Doot App or Gram Panchayat / Setu Kendra",
        officialPortal = "https://ladakibahin.maharashtra.gov.in",
        documentsNeeded = listOf("Aadhaar Card", "Maharashtra Domicile / Ration Card", "Bank Passbook seeded with DBT")
    ),
    SearchGovernmentScheme(
        id = "SCH-04",
        nameEn = "Pradhan Mantri Awas Yojana - Gramin",
        nameHi = "प्रधानमंत्री आवास योजना - ग्रामीण (पीएमएवाई-जी)",
        acronyms = listOf("PMAY", "PMAY-G", "AWAS YOJANA"),
        level = SearchGovLevel.CENTRAL,
        stateOrUt = "National (All States & UTs)",
        ministryOrDeptEn = "Ministry of Rural Development",
        ministryOrDeptHi = "ग्रामीण विकास मंत्रालय",
        category = "Housing & Shelter",
        categoryIcon = Icons.Default.Home,
        categoryColor = Color(0xFFB45309),
        benefitSummaryEn = "₹1,20,000 financial grant + ₹12,000 for toilet + 90 days MGNREGA wages",
        benefitSummaryHi = "पक्का मकान निर्माण हेतु ₹1,20,000 अनुदान + ₹12,000 शौचालय सहायता",
        benefitType = BenefitFilterType.HOUSING,
        financialAmountValue = 132000,
        eligibilityKeywords = listOf("house", "housing", "pucca makan", "kutcha", "homeless", "rural", "awas", "shelter", "mgnrega"),
        eligibilitySummaryEn = "Houseless rural families and households living in zero/one room kutcha houses.",
        eligibilitySummaryHi = "कच्चे मकानों में रहने वाले या बेघर ग्रामीण परिवार (आवास+ सूची)।",
        targetBeneficiary = BeneficiaryGroup.RURAL_FAMILIES,
        isDbtEnabled = true,
        applicationMode = "Gram Sabha verification from official Awas+ waiting list",
        officialPortal = "https://pmayg.nic.in",
        documentsNeeded = listOf("Aadhaar Card", "Bank Passbook", "MGNREGA Job Card", "Land Possession Proof")
    ),
    SearchGovernmentScheme(
        id = "SCH-05",
        nameEn = "PM Vishwakarma Scheme",
        nameHi = "प्रधानमंत्री विश्वकर्मा योजना",
        acronyms = listOf("PM-VISHWAKARMA", "VISHWAKARMA", "TOOLKIT"),
        level = SearchGovLevel.CENTRAL,
        stateOrUt = "National (All States & UTs)",
        ministryOrDeptEn = "Ministry of Micro, Small & Medium Enterprises",
        ministryOrDeptHi = "सूक्ष्म, लघु एवं मध्यम उद्यम मंत्रालय",
        category = "Skills & Employment",
        categoryIcon = Icons.Default.Handyman,
        categoryColor = Color(0xFF334155),
        benefitSummaryEn = "₹15,000 modern toolkit e-voucher + collateral-free loans up to ₹3 Lakhs at 5%",
        benefitSummaryHi = "₹15,000 आधुनिक टूलकिट ई-वाउचर + ₹3 लाख तक का 5% ब्याज पर रियायती ऋण",
        benefitType = BenefitFilterType.SUBSIDY,
        financialAmountValue = 315000,
        eligibilityKeywords = listOf("artisan", "carpenter", "tailor", "blacksmith", "potter", "cobbler", "barber", "toolkit", "craftsman", "tools"),
        eligibilitySummaryEn = "Traditional artisans and craftsmen working in 18 identified trade sectors.",
        eligibilitySummaryHi = "18 पारंपरिक व्यवसायों (बढ़ई, दर्जी, लोहार, कुम्हार, मोची) के शिल्पी व कारीगर।",
        targetBeneficiary = BeneficiaryGroup.ARTISANS,
        isDbtEnabled = true,
        applicationMode = "Free biometric enrollment at nearest CSC centre",
        officialPortal = "https://pmvishwakarma.gov.in",
        documentsNeeded = listOf("Aadhaar Card", "Bank Account Passbook", "Ration Card", "Trade Proof")
    ),
    SearchGovernmentScheme(
        id = "SCH-06",
        nameEn = "Sukanya Samriddhi Yojana (SSY)",
        nameHi = "सुकन्या समृद्धि योजना",
        acronyms = listOf("SSY", "SUKANYA", "BETI BACHAO"),
        level = SearchGovLevel.CENTRAL,
        stateOrUt = "National (All States & UTs)",
        ministryOrDeptEn = "Ministry of Finance",
        ministryOrDeptHi = "वित्त मंत्रालय",
        category = "Women & Child Development",
        categoryIcon = Icons.Default.FamilyRestroom,
        categoryColor = Color(0xFFBE185D),
        benefitSummaryEn = "8.2% sovereign tax-free interest + 100% tax exemption under Section 80C",
        benefitSummaryHi = "8.2% कर-मुक्त चक्रवृद्धि ब्याज + धारा 80C के तहत पूर्ण कर छूट",
        benefitType = BenefitFilterType.DBT_CASH,
        financialAmountValue = 1500000,
        eligibilityKeywords = listOf("girl", "daughter", "beti", "sukanya", "savings", "interest", "tax exemption", "education", "child"),
        eligibilitySummaryEn = "Parents/guardians of girl child from birth up to 10 years of age.",
        eligibilitySummaryHi = "जन्म से 10 वर्ष तक की आयु की बालिका के माता-पिता या कानूनी अभिभावक।",
        targetBeneficiary = BeneficiaryGroup.WOMEN,
        isDbtEnabled = false,
        applicationMode = "Open account at any Post Office or authorized commercial bank branch",
        officialPortal = "https://www.indiapost.gov.in",
        documentsNeeded = listOf("Birth Certificate of girl child", "Guardian's Aadhaar & PAN Card", "Address Proof")
    ),
    SearchGovernmentScheme(
        id = "SCH-07",
        nameEn = "Pradhan Mantri Mudra Yojana (PMMY)",
        nameHi = "प्रधानमंत्री मुद्रा योजना",
        acronyms = listOf("PMMY", "MUDRA", "SHISHU", "KISHORE", "TARUN"),
        level = SearchGovLevel.CENTRAL,
        stateOrUt = "National (All States & UTs)",
        ministryOrDeptEn = "Ministry of Finance",
        ministryOrDeptHi = "वित्त मंत्रालय",
        category = "Business & Entrepreneurship",
        categoryIcon = Icons.Default.BusinessCenter,
        categoryColor = Color(0xFF9A3412),
        benefitSummaryEn = "Collateral-free enterprise loans up to ₹10 Lakhs (Shishu, Kishore, Tarun)",
        benefitSummaryHi = "बिना किसी गारंटी के ₹10 लाख तक का व्यवसाय ऋण (शिशु, किशोर, तरुण)",
        benefitType = BenefitFilterType.LOANS,
        financialAmountValue = 1000000,
        eligibilityKeywords = listOf("loan", "business", "enterprise", "shop", "msme", "credit", "mudra", "startup", "collateral free"),
        eligibilitySummaryEn = "Non-corporate, non-farm small and micro entrepreneurs in trading, services or manufacturing.",
        eligibilitySummaryHi = "विनिर्माण, व्यापार एवं सेवा क्षेत्र में कार्यरत गैर-कॉर्पोरेट लघु व सूक्ष्म उद्यमी।",
        targetBeneficiary = BeneficiaryGroup.STREET_VENDORS,
        isDbtEnabled = false,
        applicationMode = "Apply on udyamimitra.in portal or visit any public/private bank branch",
        officialPortal = "https://www.mudra.org.in",
        documentsNeeded = listOf("Udyam Registration", "PAN Card & Aadhaar", "Project Report", "Bank Statement")
    ),
    SearchGovernmentScheme(
        id = "SCH-08",
        nameEn = "Delhi Free Bus Travel for Women (Pink Ticket)",
        nameHi = "दिल्ली महिला मुफ़्त बस यात्रा योजना (गुलाबी टिकट)",
        acronyms = listOf("PINK TICKET", "DELHI BUS", "FREE TRAVEL"),
        level = SearchGovLevel.UT,
        stateOrUt = "NCT of Delhi",
        ministryOrDeptEn = "Transport Department (Govt of NCT of Delhi)",
        ministryOrDeptHi = "परिवहन विभाग (दिल्ली सरकार)",
        category = "Transport & Travel",
        categoryIcon = Icons.Default.DirectionsBus,
        categoryColor = Color(0xFF1E293B),
        benefitSummaryEn = "100% free bus travel on all DTC and Cluster AC/non-AC buses in Delhi",
        benefitSummaryHi = "दिल्ली में सभी डीटीसी एवं क्लस्टर एसी/नॉन-एसी बसों में 100% मुफ़्त यात्रा",
        benefitType = BenefitFilterType.FREE_SERVICE,
        financialAmountValue = 18000,
        eligibilityKeywords = listOf("bus", "travel", "delhi", "pink ticket", "women", "commute", "dtc", "transport", "free ticket"),
        eligibilitySummaryEn = "All women and girl passengers traveling in DTC and Cluster city buses across Delhi.",
        eligibilitySummaryHi = "दिल्ली में डीटीसी एवं क्लस्टर बसों में यात्रा करने वाली सभी महिलाएं एवं छात्राएं।",
        targetBeneficiary = BeneficiaryGroup.WOMEN,
        isDbtEnabled = false,
        applicationMode = "Directly board any DTC/Cluster bus and collect free Pink Ticket from conductor",
        officialPortal = "https://transport.delhi.gov.in",
        documentsNeeded = listOf("No pre-registration or documents needed on board")
    ),
    SearchGovernmentScheme(
        id = "SCH-09",
        nameEn = "PM SVANidhi (Street Vendors AtmaNirbhar)",
        nameHi = "पीएम स्वनिधि योजना (स्ट्रीट वेंडर्स आत्मनिर्भर)",
        acronyms = listOf("PM-SVANIDHI", "SVANIDHI", "STREET VENDOR"),
        level = SearchGovLevel.CENTRAL,
        stateOrUt = "National (All States & UTs)",
        ministryOrDeptEn = "Ministry of Housing & Urban Affairs",
        ministryOrDeptHi = "आवासन एवं शहरी कार्य मंत्रालय",
        category = "Business & Entrepreneurship",
        categoryIcon = Icons.Default.BusinessCenter,
        categoryColor = Color(0xFF0284C7),
        benefitSummaryEn = "Collateral-free working capital loan ₹10,000 to ₹50,000 with 7% interest subsidy",
        benefitSummaryHi = "बिना गारंटी ₹10,000 से ₹50,000 तक का कार्यशील ऋण एवं 7% ब्याज सब्सिडी",
        benefitType = BenefitFilterType.LOANS,
        financialAmountValue = 50000,
        eligibilityKeywords = listOf("street vendor", "hawker", "thela", "urban", "loan", "cashback", "svanidhi", "vendor"),
        eligibilitySummaryEn = "Urban street vendors vending on or before March 24, 2020 holding Vending Certificate/ID.",
        eligibilitySummaryHi = "शहरी पथ विक्रेता (रेहड़ी-पटरी वाले) जिनके पास निकाय वेंडिंग प्रमाणपत्र है।",
        targetBeneficiary = BeneficiaryGroup.STREET_VENDORS,
        isDbtEnabled = true,
        applicationMode = "pmsvanidhi.mohua.gov.in portal or ULB / CSC helpdesk",
        officialPortal = "https://pmsvanidhi.mohua.gov.in",
        documentsNeeded = listOf("Aadhaar Card", "Vending Certificate / LOR", "Bank Passbook")
    ),
    SearchGovernmentScheme(
        id = "SCH-10",
        nameEn = "Kanya Utthan Yojana (Higher Education)",
        nameHi = "मुख्यमंत्री कन्या उत्थान योजना",
        acronyms = listOf("KANYA UTTHAN", "MEDHASOFT", "BIHAR DEGREE"),
        level = SearchGovLevel.STATE,
        stateOrUt = "Bihar",
        ministryOrDeptEn = "Education Department (Govt of Bihar)",
        ministryOrDeptHi = "शिक्षा विभाग (बिहार सरकार)",
        category = "Education & Learning",
        categoryIcon = Icons.Default.School,
        categoryColor = Color(0xFF1D4ED8),
        benefitSummaryEn = "₹50,000 direct grant on graduation + ₹25,000 on passing Class 12",
        benefitSummaryHi = "स्नातक उत्तीर्ण होने पर ₹50,000 एवं 12वीं उत्तीर्ण पर ₹25,000 नकद प्रोत्साहन",
        benefitType = BenefitFilterType.DBT_CASH,
        financialAmountValue = 50000,
        eligibilityKeywords = listOf("student", "graduation", "degree", "bihar", "female student", "scholarship", "college", "12th pass", "girl education"),
        eligibilitySummaryEn = "Unmarried female students residing in Bihar graduating from recognized state universities.",
        eligibilitySummaryHi = "बिहार की निवासी अविवाहित छात्राएं जिन्होंने राज्य के मान्यता प्राप्त विश्वविद्यालय से स्नातक उत्तीर्ण किया हो।",
        targetBeneficiary = BeneficiaryGroup.STUDENTS,
        isDbtEnabled = true,
        applicationMode = "Submit registration on medhasoft.bih.nic.in with university roll number",
        officialPortal = "https://medhasoft.bih.nic.in",
        documentsNeeded = listOf("Bihar Domicile Certificate", "Graduation Marksheet/Degree", "Student's Bank Passbook")
    ),
    SearchGovernmentScheme(
        id = "SCH-11",
        nameEn = "Kalaignar Magalir Urimai Thittam",
        nameHi = "कलैग्नार महिला अधिकार योजना",
        acronyms = listOf("KMUT", "TAMIL NADU WOMEN", "MAGALIR URIMAI"),
        level = SearchGovLevel.STATE,
        stateOrUt = "Tamil Nadu",
        ministryOrDeptEn = "Special Programme Implementation Dept (Govt of Tamil Nadu)",
        ministryOrDeptHi = "विशेष कार्यक्रम क्रियान्वयन विभाग (तमिलनाडु)",
        category = "Women & Child Development",
        categoryIcon = Icons.Default.FamilyRestroom,
        categoryColor = Color(0xFFBE185D),
        benefitSummaryEn = "₹1,000 monthly basic income transfer (₹12,000/year) to female heads of family",
        benefitSummaryHi = "₹1,000 प्रति माह (वार्षिक ₹12,000) महिला परिवार प्रमुख को सम्मान निधि",
        benefitType = BenefitFilterType.DBT_CASH,
        financialAmountValue = 12000,
        eligibilityKeywords = listOf("tamil nadu", "women", "monthly 1000", "head of family", "housewife", "urimai thittam", "basic income"),
        eligibilitySummaryEn = "Women heads of families aged 21+ residing in Tamil Nadu with family income < ₹2.5 Lakhs.",
        eligibilitySummaryHi = "तमिलनाडु की 21+ वर्ष की महिला मुखिया, पारिवारिक वार्षिक आय ₹2.5 लाख से कम।",
        targetBeneficiary = BeneficiaryGroup.WOMEN,
        isDbtEnabled = true,
        applicationMode = "Enroll at special village/ward revenue camps with biometric e-KYC",
        officialPortal = "https://kmut.tn.gov.in",
        documentsNeeded = listOf("Tamil Nadu Smart Family Card", "Aadhaar Card", "Electricity Consumer Number")
    ),
    SearchGovernmentScheme(
        id = "SCH-12",
        nameEn = "Gruha Lakshmi Scheme",
        nameHi = "गृह लक्ष्मी योजना",
        acronyms = listOf("GRUHA LAKSHMI", "KARNATAKA WOMEN", "SEVA SINDHU"),
        level = SearchGovLevel.STATE,
        stateOrUt = "Karnataka",
        ministryOrDeptEn = "Women & Child Development Dept (Govt of Karnataka)",
        ministryOrDeptHi = "महिला एवं बाल विकास विभाग (कर्नाटक सरकार)",
        category = "Women & Child Development",
        categoryIcon = Icons.Default.FamilyRestroom,
        categoryColor = Color(0xFFBE185D),
        benefitSummaryEn = "₹2,000 monthly financial aid (₹24,000/year) to female head of household",
        benefitSummaryHi = "₹2,000 प्रति माह (वार्षिक ₹24,000) महिला गृहस्वामिनी को प्रत्यक्ष बैंक अंतरण",
        benefitType = BenefitFilterType.DBT_CASH,
        financialAmountValue = 24000,
        eligibilityKeywords = listOf("karnataka", "women", "monthly 2000", "gruha lakshmi", "ration card", "dbt", "bpl", "guarantee"),
        eligibilitySummaryEn = "Women identified as head of household in Karnataka BPL, APL, or Antyodaya ration cards.",
        eligibilitySummaryHi = "कर्नाटक के बीपीएल, एपीएल या अंत्योदय राशन कार्ड में मुखिया के रूप में दर्ज महिलाएं।",
        targetBeneficiary = BeneficiaryGroup.WOMEN,
        isDbtEnabled = true,
        applicationMode = "Apply through Seva Sindhu portal or at Karnataka-One / Grama-One centres",
        officialPortal = "https://sevasindhugs.karnataka.gov.in",
        documentsNeeded = listOf("Ration Card (BPL/APL)", "Aadhaar Card of Woman & Husband", "Bank Passbook")
    ),
    SearchGovernmentScheme(
        id = "SCH-13",
        nameEn = "Ladli Beti Financial Security Scheme",
        nameHi = "लाडली बेटी वित्तीय सुरक्षा योजना",
        acronyms = listOf("LADLI BETI", "JK BETI", "J&K SOCIAL WELFARE"),
        level = SearchGovLevel.UT,
        stateOrUt = "Jammu & Kashmir (UT)",
        ministryOrDeptEn = "Social Welfare Department (UT of Jammu & Kashmir)",
        ministryOrDeptHi = "समाज कल्याण विभाग (केंद्र शासित प्रदेश जम्मू-कश्मीर)",
        category = "Women & Child Development",
        categoryIcon = Icons.Default.FamilyRestroom,
        categoryColor = Color(0xFFBE185D),
        benefitSummaryEn = "₹1,000/month government contribution for 14 years + maturity ₹6.5 Lakhs",
        benefitSummaryHi = "सरकार द्वारा 14 वर्षों तक ₹1,000/माह बचत जमा + 21 वर्ष की आयु पर ₹6.5 लाख परिपक्वता",
        benefitType = BenefitFilterType.DBT_CASH,
        financialAmountValue = 650000,
        eligibilityKeywords = listOf("jammu kashmir", "girl child", "ladli beti", "daughter", "social welfare", "j&k bank", "birth"),
        eligibilitySummaryEn = "Girl children born on or after 01/04/2015 in J&K UT with family income under ₹75,000/year.",
        eligibilitySummaryHi = "जम्मू-कश्मीर में जन्म लेने वाली बालिकाएं, पारिवारिक वार्षिक आय ₹75,000 से कम।",
        targetBeneficiary = BeneficiaryGroup.WOMEN,
        isDbtEnabled = true,
        applicationMode = "Submit application through Child Development Project Officer (CDPO) in J&K",
        officialPortal = "https://jksocialwelfare.nic.in",
        documentsNeeded = listOf("Birth Certificate of child", "J&K Domicile Certificate", "Income Certificate (< ₹75,000)")
    ),
    SearchGovernmentScheme(
        id = "SCH-14",
        nameEn = "Puducherry Free School Uniforms & Cycles",
        nameHi = "पुदुचेरी मुफ़्त स्कूल वर्दी, पाठ्यपुस्तक एवं साइकिल योजना",
        acronyms = listOf("PUDUCHERRY SCHOOL", "FREE BICYCLE", "UNIFORM"),
        level = SearchGovLevel.UT,
        stateOrUt = "Puducherry (UT)",
        ministryOrDeptEn = "Directorate of School Education (Govt of Puducherry)",
        ministryOrDeptHi = "स्कूली शिक्षा निदेशालय (पुदुचेरी सरकार)",
        category = "Education & Learning",
        categoryIcon = Icons.Default.School,
        categoryColor = Color(0xFF1D4ED8),
        benefitSummaryEn = "Free school uniforms, textbooks, footwear and free bicycles for Class 9 students",
        benefitSummaryHi = "मुफ़्त स्कूल यूनिफॉर्म, पाठ्यपुस्तकें, जूते एवं कक्षा 9 के विद्यार्थियों को मुफ़्त साइकिल",
        benefitType = BenefitFilterType.SUBSIDY,
        financialAmountValue = 12000,
        eligibilityKeywords = listOf("puducherry", "student", "school", "bicycle", "class 9", "uniform", "textbook", "free books"),
        eligibilitySummaryEn = "Students enrolled in Government and Government-aided schools across Puducherry UT.",
        eligibilitySummaryHi = "पुदुचेरी के सरकारी एवं सहायता प्राप्त स्कूलों में अध्ययनरत कक्षा 1 से 12 के छात्र-छात्राएं।",
        targetBeneficiary = BeneficiaryGroup.STUDENTS,
        isDbtEnabled = false,
        applicationMode = "Automatic annual distribution through school headmaster office",
        officialPortal = "https://schooledn.py.gov.in",
        documentsNeeded = listOf("School Student ID Card", "Ration Card", "Aadhaar Card")
    ),
    SearchGovernmentScheme(
        id = "SCH-15",
        nameEn = "Namo Shetkari Mahasanman Nidhi",
        nameHi = "नमो शेतकरी महासन्मान निधी योजना",
        acronyms = listOf("NAMO SHETKARI", "MAHADBT", "SHETKARI 6000"),
        level = SearchGovLevel.STATE,
        stateOrUt = "Maharashtra",
        ministryOrDeptEn = "Department of Agriculture (Govt of Maharashtra)",
        ministryOrDeptHi = "कृषि विभाग (महाराष्ट्र शासन)",
        category = "Agriculture, Rural & Environment",
        categoryIcon = Icons.Default.Agriculture,
        categoryColor = Color(0xFF15803D),
        benefitSummaryEn = "₹6,000/year state top-up grant (Combined ₹12,000/yr with PM-KISAN)",
        benefitSummaryHi = "₹6,000 प्रति वर्ष राज्य टॉप-अप अनुदान (पीएम-किसान संग कुल ₹12,000/वर्ष)",
        benefitType = BenefitFilterType.DBT_CASH,
        financialAmountValue = 6000,
        eligibilityKeywords = listOf("farmer", "maharashtra", "shetkari", "kisan", "pm kisan top up", "dbt", "12000", "landholder"),
        eligibilitySummaryEn = "Registered PM-KISAN beneficiary farmers holding agricultural land in Maharashtra.",
        eligibilitySummaryHi = "महाराष्ट्र के पंजीकृत पीएम-किसान लाभार्थी एवं भूमिधारक किसान।",
        targetBeneficiary = BeneficiaryGroup.FARMERS,
        isDbtEnabled = true,
        applicationMode = "Automatic verification for existing PM-KISAN beneficiaries via MahaDBT",
        officialPortal = "https://mahadbt.maharashtra.gov.in",
        documentsNeeded = listOf("PM-KISAN Registration ID", "Maharashtra 7/12 Land Record", "Aadhaar Card")
    )
)

// -------------------------------------------------------------
// CURATED SUGGESTED & TRENDING SEARCHES
// -------------------------------------------------------------

data class SuggestedSearchTag(
    val query: String,
    val displayEn: String,
    val displayHi: String,
    val emoji: String,
    val dimensionBadge: String
)

val SUGGESTED_SEARCHES_LIST: List<SuggestedSearchTag> = listOf(
    SuggestedSearchTag("PM-KISAN", "₹6,000 Farmer Grant", "₹6,000 किसान सम्मान", "🌾", "Benefit & Name"),
    SuggestedSearchTag("Ladki Bahin", "Ladki Bahin ₹1,500/mo", "लाडकी बहीण ₹1,500", "👩", "Women & DBT"),
    SuggestedSearchTag("Ayushman", "Ayushman ₹5 Lakh Cover", "आयुष्मान ₹5 लाख सुरक्षा", "🏥", "Healthcare"),
    SuggestedSearchTag("PMAY", "PMAY Rural Housing Grant", "पीएम आवास सहायता", "🏠", "Housing"),
    SuggestedSearchTag("Vishwakarma", "Vishwakarma ₹15k Tool", "विश्वकर्मा टूलकिट ₹15,000", "🔨", "Artisans"),
    SuggestedSearchTag("Pink Ticket", "Delhi Free Bus Travel", "दिल्ली मुफ़्त बस यात्रा", "🚌", "UT Concession"),
    SuggestedSearchTag("Sukanya", "Sukanya 8.2% Tax Free", "सुकन्या समृद्धि 8.2%", "👧", "Girl Child"),
    SuggestedSearchTag("Mudra", "Mudra ₹10 Lakh Loan", "मुद्रा ₹10 लाख ऋण", "💼", "MSME Loan"),
    SuggestedSearchTag("Medhasoft", "Girls Degree ₹50,000", "कन्या उत्थान ₹50,000", "🎓", "Education")
)

// -------------------------------------------------------------
// MAIN COMPOSABLE SCREEN
// -------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchemeSearchScreen(
    currentLanguage: AppLanguage,
    onSelectLanguage: (AppLanguage) -> Unit,
    isLargeFont: Boolean,
    onFontScaleToggle: () -> Unit,
    onNavigateBack: () -> Unit,
    initialQuery: String = "",
    onOpenSchemeDetails: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val fontSizeMultiplier = if (isLargeFont) 1.15f else 1.0f
    val focusManager = LocalFocusManager.current

    // Search query & state
    var searchQuery by remember { mutableStateOf(initialQuery) }
    var activeSearchSubmitted by remember { mutableStateOf(initialQuery.isNotBlank()) }

    // Recent searches list (stored in interactive state)
    var recentSearches by remember {
        mutableStateOf(
            listOf(
                "PM-KISAN ₹6,000",
                "Ladki Bahin Maharashtra",
                "Ayushman ₹5 Lakh",
                "Delhi Pink Ticket",
                "Vishwakarma ₹15,000"
            )
        )
    }

    // Filter selections
    var selectedLevel by remember { mutableStateOf(SearchGovLevel.ALL) }
    var selectedCategory by remember { mutableStateOf("ALL") }
    var selectedStateOrUt by remember { mutableStateOf("ALL") }
    var selectedBenefitType by remember { mutableStateOf(BenefitFilterType.ALL) }
    var selectedBeneficiary by remember { mutableStateOf(BeneficiaryGroup.ALL) }

    // Dialog & UI states
    var showFilterSheet by remember { mutableStateOf(false) }
    var selectedSchemeForPreview by remember { mutableStateOf<SearchGovernmentScheme?>(null) }
    var savedSchemeIds by remember { mutableStateOf(setOf("SCH-01", "SCH-02", "SCH-03")) }
    var showVoiceMockDialog by remember { mutableStateOf(false) }

    // Calculate active filter count
    val activeFiltersCount = remember(selectedLevel, selectedCategory, selectedStateOrUt, selectedBenefitType, selectedBeneficiary) {
        var count = 0
        if (selectedLevel != SearchGovLevel.ALL) count++
        if (selectedCategory != "ALL") count++
        if (selectedStateOrUt != "ALL") count++
        if (selectedBenefitType != BenefitFilterType.ALL) count++
        if (selectedBeneficiary != BeneficiaryGroup.ALL) count++
        count
    }

    // List of unique categories & states for filter sheet
    val allCategories = remember {
        listOf("ALL") + GLOBAL_SEARCH_SCHEMES_MOCK.map { it.category }.distinct()
    }
    val allStatesAndUts = remember {
        listOf("ALL", "National (All States & UTs)", "Maharashtra", "NCT of Delhi", "Tamil Nadu", "Bihar", "Karnataka", "Jammu & Kashmir (UT)", "Puducherry (UT)")
    }

    // Reset filters helper function
    val clearAllFilters = {
        selectedLevel = SearchGovLevel.ALL
        selectedCategory = "ALL"
        selectedStateOrUt = "ALL"
        selectedBenefitType = BenefitFilterType.ALL
        selectedBeneficiary = BeneficiaryGroup.ALL
    }

    // Clear everything helper (query + filters)
    val clearSearchAndFilters = {
        searchQuery = ""
        activeSearchSubmitted = false
        clearAllFilters()
    }

    // Helper to submit search query
    val submitSearch = { queryText: String ->
        val trimmed = queryText.trim()
        if (trimmed.isNotEmpty()) {
            searchQuery = trimmed
            activeSearchSubmitted = true
            // Add to recent searches (deduplicated, max 8)
            recentSearches = (listOf(trimmed) + recentSearches.filterNot { it.equals(trimmed, ignoreCase = true) }).take(8)
            focusManager.clearFocus()
        }
    }

    // -------------------------------------------------------------
    // SEARCH & FILTER ENGINE ACROSS ALL 6 DIMENSIONS
    // -------------------------------------------------------------
    val searchResultsWithMatches: List<Pair<SearchGovernmentScheme, MatchedSearchAttributes>> = remember(
        searchQuery,
        selectedLevel,
        selectedCategory,
        selectedStateOrUt,
        selectedBenefitType,
        selectedBeneficiary,
        currentLanguage
    ) {
        val q = searchQuery.trim().lowercase()
        GLOBAL_SEARCH_SCHEMES_MOCK.mapNotNull { scheme ->
            // 1. Level Filter
            val matchesLevel = selectedLevel == SearchGovLevel.ALL || scheme.level == selectedLevel

            // 2. Category Filter
            val matchesCategoryFilter = selectedCategory == "ALL" || scheme.category.equals(selectedCategory, ignoreCase = true)

            // 3. State/UT Filter
            val matchesStateFilter = selectedStateOrUt == "ALL" ||
                    scheme.stateOrUt.contains(selectedStateOrUt, ignoreCase = true) ||
                    (selectedStateOrUt == "National (All States & UTs)" && scheme.level == SearchGovLevel.CENTRAL)

            // 4. Benefit Type Filter
            val matchesBenefitType = selectedBenefitType == BenefitFilterType.ALL || scheme.benefitType == selectedBenefitType

            // 5. Target Beneficiary Filter
            val matchesBeneficiary = selectedBeneficiary == BeneficiaryGroup.ALL || scheme.targetBeneficiary == selectedBeneficiary

            val passesFilters = matchesLevel && matchesCategoryFilter && matchesStateFilter && matchesBenefitType && matchesBeneficiary

            if (!passesFilters) {
                null
            } else if (q.isEmpty()) {
                // If query is empty, return with empty match attributes
                Pair(scheme, MatchedSearchAttributes())
            } else {
                // Multi-dimensional matching checks
                val matchName = scheme.nameEn.lowercase().contains(q) ||
                        scheme.nameHi.lowercase().contains(q) ||
                        scheme.acronyms.any { it.lowercase().contains(q) }

                val matchCat = scheme.category.lowercase().contains(q)

                val matchMinistry = scheme.ministryOrDeptEn.lowercase().contains(q) ||
                        scheme.ministryOrDeptHi.lowercase().contains(q)

                val matchState = scheme.stateOrUt.lowercase().contains(q)

                val matchBenefit = scheme.benefitSummaryEn.lowercase().contains(q) ||
                        scheme.benefitSummaryHi.lowercase().contains(q) ||
                        (q.startsWith("₹") && scheme.benefitSummaryEn.contains(q.substring(1))) ||
                        (q.all { it.isDigit() } && scheme.financialAmountValue.toString().contains(q))

                val matchEligibility = scheme.eligibilityKeywords.any { it.lowercase().contains(q) } ||
                        scheme.eligibilitySummaryEn.lowercase().contains(q) ||
                        scheme.eligibilitySummaryHi.lowercase().contains(q) ||
                        scheme.targetBeneficiary.labelEn.lowercase().contains(q) ||
                        scheme.targetBeneficiary.labelHi.contains(q)

                val matchedAttrs = MatchedSearchAttributes(
                    matchedByName = matchName,
                    matchedByCategory = matchCat,
                    matchedByMinistry = matchMinistry,
                    matchedByState = matchState,
                    matchedByBenefit = matchBenefit,
                    matchedByEligibility = matchEligibility
                )

                if (matchedAttrs.anyMatched) {
                    Pair(scheme, matchedAttrs)
                } else {
                    null
                }
            }
        }
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Government Prototype Banner
                GovernmentBanner()

                // Top App Bar with back navigation & screen title
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
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) "राष्ट्रीय योजना खोज" else "Government Scheme Search",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = (16 * fontSizeMultiplier).sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) "नाम, मंत्रालय, श्रेणी, राज्य, लाभ या पात्रता से खोजें" else "Search across Name, Ministry, Category, State, Benefit & Criteria",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White.copy(alpha = 0.82f),
                                    fontSize = (11 * fontSizeMultiplier).sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Filter Button with Badge in Top Bar
                        BadgedBox(
                            badge = {
                                if (activeFiltersCount > 0) {
                                    Badge(
                                        containerColor = GovSaffron,
                                        contentColor = Color.White
                                    ) {
                                        Text(
                                            text = activeFiltersCount.toString(),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                        ) {
                            IconButton(onClick = { showFilterSheet = true }) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = "Filters",
                                    tint = if (activeFiltersCount > 0) GovSaffron else Color.White
                                )
                            }
                        }
                    }
                }
            }
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
            // SEARCH INPUT BOX (Prominent, High Legibility, Voice Mock)
            // -------------------------------------------------------------
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    border = BorderStroke(1.dp, GovNavyPrimary.copy(alpha = 0.25f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = {
                                searchQuery = it
                                activeSearchSubmitted = it.isNotBlank()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 56.dp),
                            placeholder = {
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI)
                                        "योजना, मंत्रालय, राज्य, लाभ या पात्रता खोजें..."
                                    else
                                        "Search by Scheme, Ministry, State, Benefit or Criteria...",
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
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            trailingIcon = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(onClick = {
                                            searchQuery = ""
                                            activeSearchSubmitted = false
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Clear",
                                                tint = Color(0xFF64748B)
                                            )
                                        }
                                    }
                                    // Voice Search Mock
                                    IconButton(onClick = { showVoiceMockDialog = true }) {
                                        Icon(
                                            imageVector = Icons.Default.Mic,
                                            contentDescription = "Voice Search",
                                            tint = GovSaffron,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = { submitSearch(searchQuery) }),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GovNavyPrimary,
                                unfocusedBorderColor = Color(0xFFCBD5E1),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color(0xFFF8FAFC)
                            )
                        )

                        // 6 Dimension badges indicator banner
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(GovNavyContainer.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = GovNavyPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI)
                                    "खोज दायरा: योजना नाम • मंत्रालय • श्रेणी • राज्य/यूटी • लाभ (₹) • पात्रता"
                                else
                                    "Searches across: Scheme Name • Ministry • Category • State • Benefit • Eligibility",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = GovNavyPrimary,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = (10 * fontSizeMultiplier).sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // RECENT SEARCHES SECTION (Interactive Chips + Clear All)
            // -------------------------------------------------------------
            if (recentSearches.isNotEmpty() && searchQuery.isEmpty()) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
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
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) "हालिया खोज (Recent Searches)" else "Recent Searches",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GovNavyPrimary,
                                        fontSize = (14 * fontSizeMultiplier).sp
                                    )
                                )
                            }

                            TextButton(
                                onClick = { recentSearches = emptyList() },
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = null,
                                        tint = Color(0xFF94A3B8),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = if (currentLanguage == AppLanguage.HINDI) "सभी हटाएं" else "Clear All",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color(0xFF64748B),
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }
                            }
                        }

                        // Flow of recent search chips
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(vertical = 2.dp)
                        ) {
                            items(recentSearches) { recentItem ->
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = Color.White,
                                    border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                                    modifier = Modifier.clickable { submitSearch(recentItem) }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.History,
                                            contentDescription = null,
                                            tint = Color(0xFF64748B),
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Text(
                                            text = recentItem,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = Color(0xFF1E293B),
                                                fontWeight = FontWeight.Medium,
                                                fontSize = (12 * fontSizeMultiplier).sp
                                            )
                                        )
                                        // Individual remove icon
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Remove",
                                            tint = Color(0xFF94A3B8),
                                            modifier = Modifier
                                                .size(14.dp)
                                                .clickable {
                                                    recentSearches = recentSearches.filterNot { it == recentItem }
                                                }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // SUGGESTED & TRENDING SEARCHES SECTION
            // -------------------------------------------------------------
            if (searchQuery.isEmpty()) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                contentDescription = null,
                                tint = GovSaffron,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) "सुझावित व लोकप्रिय खोज (Suggested)" else "Suggested & Trending Searches",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GovNavyPrimary,
                                    fontSize = (14 * fontSizeMultiplier).sp
                                )
                            )
                        }

                        // Horizontal carousel of curated trending pills
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(vertical = 4.dp)
                        ) {
                            items(SUGGESTED_SEARCHES_LIST) { tag ->
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = Color(0xFFFFF7ED),
                                    border = BorderStroke(1.dp, Color(0xFFFFEDD5)),
                                    modifier = Modifier.clickable { submitSearch(tag.query) }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(text = tag.emoji, fontSize = 14.sp)
                                        Text(
                                            text = if (currentLanguage == AppLanguage.HINDI) tag.displayHi else tag.displayEn,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = Color(0xFF9A3412),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = (12 * fontSizeMultiplier).sp
                                            )
                                        )
                                        Surface(
                                            color = Color.White.copy(alpha = 0.8f),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = tag.dimensionBadge,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = Color(0xFFC2410C),
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.SemiBold
                                                ),
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
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
            // FILTERS & QUICK FILTER CHIPS BAR
            // -------------------------------------------------------------
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                                imageVector = Icons.Default.FilterList,
                                contentDescription = null,
                                tint = GovNavyPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) "फ़िल्टर (Filters)" else "Filters & Jurisdictions",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GovNavyPrimary,
                                    fontSize = (14 * fontSizeMultiplier).sp
                                )
                            )

                            if (activeFiltersCount > 0) {
                                Surface(
                                    color = GovSaffron,
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(
                                        text = "$activeFiltersCount Active",
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // "Clear Filters" Button
                            if (activeFiltersCount > 0) {
                                TextButton(
                                    onClick = clearAllFilters,
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.RestartAlt,
                                            contentDescription = null,
                                            tint = GovError,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = if (currentLanguage == AppLanguage.HINDI) "फ़िल्टर हटाएं" else "Clear Filters",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = GovError,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }
                            }

                            // Open Filter Sheet CTA
                            OutlinedButton(
                                onClick = { showFilterSheet = true },
                                shape = RoundedCornerShape(20.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                border = BorderStroke(1.dp, GovNavyPrimary)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = null,
                                    tint = GovNavyPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) "सभी फ़िल्टर" else "All Filters",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = GovNavyPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }

                    // Horizontal Quick-Filter Chips for 1-Tap Toggle
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 2.dp)
                    ) {
                        // Quick Level Chips
                        items(SearchGovLevel.values()) { level ->
                            val isSelected = selectedLevel == level
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedLevel = if (isSelected) SearchGovLevel.ALL else level
                                },
                                label = {
                                    Text(
                                        text = if (currentLanguage == AppLanguage.HINDI) level.labelHi else level.labelEn,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = level.icon,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
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

                        // Quick Benefit Type Chips
                        items(listOf(BenefitFilterType.DBT_CASH, BenefitFilterType.HEALTHCARE, BenefitFilterType.HOUSING, BenefitFilterType.LOANS)) { bType ->
                            val isSelected = selectedBenefitType == bType
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedBenefitType = if (isSelected) BenefitFilterType.ALL else bType
                                },
                                label = {
                                    Text(
                                        text = if (currentLanguage == AppLanguage.HINDI) bType.labelHi else bType.labelEn,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                shape = RoundedCornerShape(16.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GovSaffron,
                                    selectedLabelColor = Color.White,
                                    containerColor = Color.White,
                                    labelColor = Color(0xFFC2410C)
                                )
                            )
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // RESULTS COUNT & ACTIVE FILTER PILLS HEADER
            // -------------------------------------------------------------
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val count = searchResultsWithMatches.size
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI)
                            "उपलब्ध योजनाएं: $count"
                        else
                            "Showing $count schemes found",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = GovNavyPrimary,
                            fontSize = (13 * fontSizeMultiplier).sp
                        )
                    )

                    if (searchQuery.isNotEmpty() || activeFiltersCount > 0) {
                        Text(
                            text = if (searchQuery.isNotEmpty()) "\"$searchQuery\"" else "Filtered",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF64748B),
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                fontSize = 12.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // -------------------------------------------------------------
            // ZERO RESULTS SCREEN (When no schemes match criteria)
            // -------------------------------------------------------------
            if (searchResultsWithMatches.isEmpty()) {
                item {
                    NoSearchResultsCard(
                        searchQuery = searchQuery,
                        activeFiltersCount = activeFiltersCount,
                        currentLanguage = currentLanguage,
                        fontSizeMultiplier = fontSizeMultiplier,
                        onClearSearchAndFilters = clearSearchAndFilters,
                        onSelectSuggestedQuery = { suggestedQuery ->
                            submitSearch(suggestedQuery)
                        }
                    )
                }
            } else {
                // -------------------------------------------------------------
                // MATCHING SCHEME RESULT CARDS
                // -------------------------------------------------------------
                items(searchResultsWithMatches, key = { it.first.id }) { (scheme, matchAttrs) ->
                    val isSaved = savedSchemeIds.contains(scheme.id)
                    SearchSchemeResultCard(
                        scheme = scheme,
                        matchAttrs = matchAttrs,
                        isSaved = isSaved,
                        onToggleSave = {
                            savedSchemeIds = if (isSaved) {
                                savedSchemeIds - scheme.id
                            } else {
                                savedSchemeIds + scheme.id
                            }
                        },
                        onViewDetails = {
                            selectedSchemeForPreview = scheme
                            onOpenSchemeDetails?.invoke(scheme.id)
                        },
                        currentLanguage = currentLanguage,
                        fontSizeMultiplier = fontSizeMultiplier
                    )
                }
            }

            // Bottom space for comfortable scrolling
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // -------------------------------------------------------------
    // MULTI-DIMENSIONAL FILTER SHEET / DIALOG
    // -------------------------------------------------------------
    if (showFilterSheet) {
        SchemeFilterModalSheet(
            selectedLevel = selectedLevel,
            onSelectLevel = { selectedLevel = it },
            selectedCategory = selectedCategory,
            onSelectCategory = { selectedCategory = it },
            allCategories = allCategories,
            selectedStateOrUt = selectedStateOrUt,
            onSelectStateOrUt = { selectedStateOrUt = it },
            allStatesAndUts = allStatesAndUts,
            selectedBenefitType = selectedBenefitType,
            onSelectBenefitType = { selectedBenefitType = it },
            selectedBeneficiary = selectedBeneficiary,
            onSelectBeneficiary = { selectedBeneficiary = it },
            activeFiltersCount = activeFiltersCount,
            currentLanguage = currentLanguage,
            fontSizeMultiplier = fontSizeMultiplier,
            onClearFilters = clearAllFilters,
            onDismiss = { showFilterSheet = false }
        )
    }

    // -------------------------------------------------------------
    // SCHEME DETAILS PREVIEW MODAL
    // -------------------------------------------------------------
    selectedSchemeForPreview?.let { scheme ->
        SchemePreviewModalDialog(
            scheme = scheme,
            isSaved = savedSchemeIds.contains(scheme.id),
            onToggleSave = {
                savedSchemeIds = if (savedSchemeIds.contains(scheme.id)) {
                    savedSchemeIds - scheme.id
                } else {
                    savedSchemeIds + scheme.id
                }
            },
            currentLanguage = currentLanguage,
            fontSizeMultiplier = fontSizeMultiplier,
            onDismiss = { selectedSchemeForPreview = null }
        )
    }

    // -------------------------------------------------------------
    // VOICE SEARCH SIMULATION MODAL
    // -------------------------------------------------------------
    if (showVoiceMockDialog) {
        VoiceSearchMockDialog(
            currentLanguage = currentLanguage,
            onSelectVoiceSample = { voiceText ->
                showVoiceMockDialog = false
                submitSearch(voiceText)
            },
            onDismiss = { showVoiceMockDialog = false }
        )
    }
}

// -------------------------------------------------------------
// NO RESULTS CARD COMPOSABLE
// -------------------------------------------------------------

@Composable
fun NoSearchResultsCard(
    searchQuery: String,
    activeFiltersCount: Int,
    currentLanguage: AppLanguage,
    fontSizeMultiplier: Float,
    onClearSearchAndFilters: () -> Unit,
    onSelectSuggestedQuery: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Friendly Icon Container
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFF1F2)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = Color(0xFFE11D48),
                    modifier = Modifier.size(36.dp)
                )
            }

            // Heading & Subtitle
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = if (currentLanguage == AppLanguage.HINDI)
                        "कोई योजना नहीं मिली"
                    else
                        "No Schemes Found",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = GovNavyPrimary,
                        fontSize = (18 * fontSizeMultiplier).sp,
                        textAlign = TextAlign.Center
                    )
                )

                Text(
                    text = if (searchQuery.isNotEmpty()) {
                        val filterText = if (activeFiltersCount > 0) " (${activeFiltersCount} सक्रिय फ़िल्टर)" else ""
                        val filterTextEn = if (activeFiltersCount > 0) " (${activeFiltersCount} active filters)" else ""
                        if (currentLanguage == AppLanguage.HINDI)
                            "\"$searchQuery\" और चयनित फ़िल्टर$filterText के साथ कोई सरकारी योजना मेल नहीं खाती।"
                        else
                            "No government schemes matched \"$searchQuery\"$filterTextEn with the current filters."
                    } else {
                        val filterText = if (activeFiltersCount > 0) " (${activeFiltersCount} फ़िल्टर)" else ""
                        val filterTextEn = if (activeFiltersCount > 0) " ($activeFiltersCount filters active)" else ""
                        if (currentLanguage == AppLanguage.HINDI)
                            "वर्तमान फ़िल्टर संयोजन$filterText में कोई योजना उपलब्ध नहीं है।"
                        else
                            "No government schemes match the selected filter combination$filterTextEn."
                    },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF64748B),
                        fontSize = (13 * fontSizeMultiplier).sp,
                        textAlign = TextAlign.Center
                    )
                )
            }

            // Practical Search Suggestions Box
            Surface(
                color = Color(0xFFF8FAFC),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI) "💡 खोज सुझाव (Helpful Tips):" else "💡 Search Tips:",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = GovNavyPrimary
                        )
                    )
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI)
                            "• वर्तनी (Spelling) जांचें या अधिक सामान्य शब्द (उदा: किसान, आवास, स्वास्थ्य) खोजें\n• मंत्रालय या राज्य का पूरा नाम लिखने के बजाय छोटा नाम आज़माएं\n• अधिक परिणाम देखने के लिए सक्रिय फ़िल्टर हटाएँ"
                        else
                            "• Check for spelling errors or try broader keywords like 'Farmer', 'Women', 'Health'\n• Search by issuing Ministry or your resident State/UT\n• Try removing active filters to view all nationwide welfare programs",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF475569),
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    )
                }
            }

            // Primary 1-Tap Reset Button
            Button(
                onClick = onClearSearchAndFilters,
                colors = ButtonDefaults.buttonColors(containerColor = GovNavyPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.RestartAlt,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (currentLanguage == AppLanguage.HINDI)
                        "सर्च और सभी फ़िल्टर रीसेट करें"
                    else
                        "Clear Search & All Filters",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                )
            }

            // Suggested Alternatives Recovery Chips
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = if (currentLanguage == AppLanguage.HINDI) "या इन लोकप्रिय योजनाओं को खोजें:" else "Or try these popular schemes:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.SemiBold
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("PM-KISAN", "Ayushman", "Ladki Bahin").forEach { sampleQuery ->
                        OutlinedButton(
                            onClick = { onSelectSuggestedQuery(sampleQuery) },
                            shape = RoundedCornerShape(16.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = sampleQuery,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GovNavyPrimary,
                                    fontSize = 11.sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// SCHEME RESULT CARD WITH 6-DIMENSION HIGHLIGHTS
// -------------------------------------------------------------

@Composable
fun SearchSchemeResultCard(
    scheme: SearchGovernmentScheme,
    matchAttrs: MatchedSearchAttributes,
    isSaved: Boolean,
    onToggleSave: () -> Unit,
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
            .clickable { onViewDetails() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Row: Category Badge + Level Pill + Save Bookmark
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category Chip with Icon
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
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Level Badge (Central / State / UT)
                    val (levelBg, levelText, levelLabel) = when (scheme.level) {
                        SearchGovLevel.CENTRAL -> Triple(
                            Color(0xFFE0F2FE),
                            Color(0xFF0369A1),
                            if (currentLanguage == AppLanguage.HINDI) "केंद्र" else "Central"
                        )
                        SearchGovLevel.STATE -> Triple(
                            Color(0xFFDCFCE7),
                            Color(0xFF15803D),
                            if (currentLanguage == AppLanguage.HINDI) "राज्य" else "State"
                        )
                        SearchGovLevel.UT -> Triple(
                            Color(0xFFF3E8FF),
                            Color(0xFF7E22CE),
                            if (currentLanguage == AppLanguage.HINDI) "यूटी" else "UT"
                        )
                        else -> Triple(Color(0xFFF1F5F9), Color(0xFF475569), "Gov")
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

                    // Save / Bookmark Icon Button
                    IconButton(
                        onClick = onToggleSave,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Save Scheme",
                            tint = if (isSaved) GovSaffron else Color(0xFF94A3B8),
                            modifier = Modifier.size(20.dp)
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
                // Secondary language subtitle
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

            // Ministry / Issuing Department
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

            // Highlighted Benefit Banner (Rupee figures, assistance frequency)
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
                            text = if (currentLanguage == AppLanguage.HINDI) "मुख्य वित्तीय लाभ" else "Key Benefit Highlight",
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

            // Eligibility summary snippet
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    color = Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI) scheme.targetBeneficiary.labelHi else scheme.targetBeneficiary.labelEn,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF334155),
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = scheme.getEligibility(currentLanguage),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF64748B),
                        fontSize = (11 * fontSizeMultiplier).sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }

            // Matched Criteria Attribute Indicators (Highlights why it matched search query)
            val matchedTags = matchAttrs.getMatchTags(currentLanguage)
            if (matchedTags.isNotEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI) "मेल खाया:" else "Matched by:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = GovSaffron,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                    matchedTags.take(3).forEach { tag ->
                        Surface(
                            color = Color(0xFFFFF7ED),
                            shape = RoundedCornerShape(4.dp),
                            border = BorderStroke(1.dp, Color(0xFFFFEDD5))
                        ) {
                            Text(
                                text = "✔ $tag",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFFC2410C),
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 9.sp
                                ),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
            }

            // Footer Actions: Mode & "View Details →" CTA
            HorizontalDivider(color = Color(0xFFF1F5F9))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Application Mode tag
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = if (scheme.isDbtEnabled) Icons.Default.CheckCircle else Icons.Default.Info,
                        contentDescription = null,
                        tint = if (scheme.isDbtEnabled) Color(0xFF15803D) else Color(0xFF0284C7),
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = if (scheme.isDbtEnabled) "Direct DBT" else "Cashless / Grant",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF475569),
                            fontWeight = FontWeight.Medium,
                            fontSize = 10.sp
                        )
                    )
                }

                // View Details Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier.clickable { onViewDetails() }
                ) {
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI) "विवरण देखें" else "View Details",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = GovNavyPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = GovNavyPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// MULTI-DIMENSIONAL FILTER MODAL SHEET
// -------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchemeFilterModalSheet(
    selectedLevel: SearchGovLevel,
    onSelectLevel: (SearchGovLevel) -> Unit,
    selectedCategory: String,
    onSelectCategory: (String) -> Unit,
    allCategories: List<String>,
    selectedStateOrUt: String,
    onSelectStateOrUt: (String) -> Unit,
    allStatesAndUts: List<String>,
    selectedBenefitType: BenefitFilterType,
    onSelectBenefitType: (BenefitFilterType) -> Unit,
    selectedBeneficiary: BeneficiaryGroup,
    onSelectBeneficiary: (BeneficiaryGroup) -> Unit,
    activeFiltersCount: Int,
    currentLanguage: AppLanguage,
    fontSizeMultiplier: Float,
    onClearFilters: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Sheet Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI) "फ़िल्टर विकल्प" else "Search Filters",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = GovNavyPrimary,
                            fontSize = (18 * fontSizeMultiplier).sp
                        )
                    )
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI) "स्तर, श्रेणी, राज्य व लाभ प्रकार चुनें" else "Filter by Level, Category, State/UT & Benefit",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF64748B))
                    )
                }

                if (activeFiltersCount > 0) {
                    TextButton(onClick = onClearFilters) {
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI) "सभी रीसेट करें" else "Reset All",
                            color = GovError,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            HorizontalDivider(color = Color(0xFFE2E8F0))

            // Scrollable filter criteria sections
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section 1: Government Level
                FilterSectionHeader(
                    title = if (currentLanguage == AppLanguage.HINDI) "1. सरकारी स्तर (Government Level)" else "1. Government Level",
                    icon = Icons.Default.AccountBalance
                )
                FlowRowLayout(horizontalSpacing = 8.dp, verticalSpacing = 8.dp) {
                    SearchGovLevel.values().forEach { level ->
                        val isSelected = selectedLevel == level
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSelectLevel(level) },
                            label = {
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) level.labelHi else level.labelEn,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            shape = RoundedCornerShape(16.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GovNavyPrimary,
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFFF8FAFC),
                                labelColor = GovNavyPrimary
                            )
                        )
                    }
                }

                // Section 2: Benefit Type
                FilterSectionHeader(
                    title = if (currentLanguage == AppLanguage.HINDI) "2. लाभ का प्रकार (Benefit Assistance Type)" else "2. Benefit Assistance Type",
                    icon = Icons.Default.CurrencyRupee
                )
                FlowRowLayout(horizontalSpacing = 8.dp, verticalSpacing = 8.dp) {
                    BenefitFilterType.values().forEach { bType ->
                        val isSelected = selectedBenefitType == bType
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSelectBenefitType(bType) },
                            label = {
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) bType.labelHi else bType.labelEn,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            shape = RoundedCornerShape(16.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GovSaffron,
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFFF8FAFC),
                                labelColor = Color(0xFFC2410C)
                            )
                        )
                    }
                }

                // Section 3: State / UT Jurisdiction
                FilterSectionHeader(
                    title = if (currentLanguage == AppLanguage.HINDI) "3. राज्य / केंद्र शासित प्रदेश (State / UT)" else "3. State / UT Jurisdiction",
                    icon = Icons.Default.LocationOn
                )
                FlowRowLayout(horizontalSpacing = 8.dp, verticalSpacing = 8.dp) {
                    allStatesAndUts.forEach { stateItem ->
                        val isSelected = selectedStateOrUt == stateItem
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSelectStateOrUt(stateItem) },
                            label = {
                                Text(
                                    text = if (stateItem == "ALL") (if (currentLanguage == AppLanguage.HINDI) "सभी राज्य व यूटी" else "All States & UTs") else stateItem,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            shape = RoundedCornerShape(16.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF0F766E),
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFFF8FAFC),
                                labelColor = Color(0xFF0F766E)
                            )
                        )
                    }
                }

                // Section 4: Target Beneficiary Group
                FilterSectionHeader(
                    title = if (currentLanguage == AppLanguage.HINDI) "4. लक्षित लाभार्थी समूह (Beneficiary Group)" else "4. Target Beneficiary Group",
                    icon = Icons.Default.FamilyRestroom
                )
                FlowRowLayout(horizontalSpacing = 8.dp, verticalSpacing = 8.dp) {
                    BeneficiaryGroup.values().forEach { group ->
                        val isSelected = selectedBeneficiary == group
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSelectBeneficiary(group) },
                            label = {
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) group.labelHi else group.labelEn,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            shape = RoundedCornerShape(16.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF7C3AED),
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFFF8FAFC),
                                labelColor = Color(0xFF7C3AED)
                            )
                        )
                    }
                }

                // Section 5: Welfare Domain / Category
                FilterSectionHeader(
                    title = if (currentLanguage == AppLanguage.HINDI) "5. कल्याण श्रेणी (Welfare Category)" else "5. Welfare Domain / Category",
                    icon = Icons.Default.Category
                )
                FlowRowLayout(horizontalSpacing = 8.dp, verticalSpacing = 8.dp) {
                    allCategories.forEach { cat ->
                        val isSelected = selectedCategory == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSelectCategory(cat) },
                            label = {
                                Text(
                                    text = if (cat == "ALL") (if (currentLanguage == AppLanguage.HINDI) "सभी श्रेणियां" else "All Categories") else cat,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            shape = RoundedCornerShape(16.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GovNavyPrimary,
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFFF8FAFC),
                                labelColor = GovNavyPrimary
                            )
                        )
                    }
                }
            }

            // Bottom CTA row in modal sheet
            HorizontalDivider(color = Color(0xFFE2E8F0))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        onClearFilters()
                        onDismiss()
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI) "रीसेट करें" else "Clear All",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF475569)
                    )
                }

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = GovNavyPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1.5f)
                ) {
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI) "फ़िल्टर लागू करें" else "Apply Filters",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun FilterSectionHeader(title: String, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = GovNavyPrimary,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = GovNavyPrimary,
                fontSize = 13.sp
            )
        )
    }
}

// -------------------------------------------------------------
// SCHEME PREVIEW MODAL DIALOG
// -------------------------------------------------------------

@Composable
fun SchemePreviewModalDialog(
    scheme: SearchGovernmentScheme,
    isSaved: Boolean,
    onToggleSave: () -> Unit,
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
                // Top header with close & save
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
                        IconButton(onClick = onToggleSave) {
                            Icon(
                                imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Save",
                                tint = if (isSaved) GovSaffron else Color(0xFF94A3B8)
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

                // Content scrollable
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
                                text = if (currentLanguage == AppLanguage.HINDI) "वित्तीय व अन्य लाभ:" else "Key Benefits:",
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

                    // Documents Needed
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI) "आवश्यक दस्तावेज़:" else "Required Documents:",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = GovNavyPrimary
                            )
                        )
                        scheme.documentsNeeded.forEach { doc ->
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

                    // Application Mode
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI) "आवेदन कैसे करें:" else "How to Apply:",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = GovNavyPrimary
                            )
                        )
                        Text(
                            text = scheme.applicationMode,
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF475569))
                        )
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
                        text = if (currentLanguage == AppLanguage.HINDI) "समझ गया (Close)" else "Close Preview",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// VOICE SEARCH SIMULATION MODAL
// -------------------------------------------------------------

@Composable
fun VoiceSearchMockDialog(
    currentLanguage: AppLanguage,
    onSelectVoiceSample: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFF7ED)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = null,
                        tint = GovSaffron,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI) "आवाज़ से खोजें (Voice Search)" else "Simulated Voice Search",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = GovNavyPrimary
                        )
                    )
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI)
                            "ग्रामीण व वरिष्ठ नागरिकों के लिए बोलकर खोजने की सुविधा:"
                        else
                            "High accessibility speech prompt for elderly & rural citizens:",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF64748B),
                            textAlign = TextAlign.Center
                        )
                    )
                }

                // Voice Sample Clickable Pills
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val samples = if (currentLanguage == AppLanguage.HINDI) {
                        listOf(
                            "\"किसान सम्मान निधि ₹6,000\"",
                            "\"लाडकी बहीण योजना महाराष्ट्र\"",
                            "\"आयुष्मान 5 लाख मुफ़्त इलाज\"",
                            "\"प्रधानमंत्री आवास योजना ग्रामीण\""
                        )
                    } else {
                        listOf(
                            "\"PM Kisan 6000 rupees\"",
                            "\"Ladki Bahin Maharashtra\"",
                            "\"Ayushman 5 Lakh hospital card\"",
                            "\"Rural Housing PMAY grant\""
                        )
                    }

                    samples.forEach { sample ->
                        Surface(
                            color = Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelectVoiceSample(sample.replace("\"", ""))
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = null,
                                    tint = GovSaffron,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = sample,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = GovNavyPrimary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }
                    }
                }

                TextButton(onClick = onDismiss) {
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI) "रद्द करें" else "Cancel",
                        color = Color(0xFF64748B)
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// HELPER FLOW ROW IMPLEMENTATION (Without requiring external libs)
// -------------------------------------------------------------

@Composable
fun FlowRowLayout(
    horizontalSpacing: androidx.compose.ui.unit.Dp = 8.dp,
    verticalSpacing: androidx.compose.ui.unit.Dp = 8.dp,
    content: @Composable () -> Unit
) {
    androidx.compose.ui.layout.Layout(
        content = content
    ) { measurables, constraints ->
        val hSpacingPx = horizontalSpacing.roundToPx()
        val vSpacingPx = verticalSpacing.roundToPx()

        val rows = mutableListOf<List<androidx.compose.ui.layout.Placeable>>()
        val rowWidths = mutableListOf<Int>()
        val rowHeights = mutableListOf<Int>()

        var currentRow = mutableListOf<androidx.compose.ui.layout.Placeable>()
        var currentLineWidth = 0
        var currentLineHeight = 0

        for (measurable in measurables) {
            val placeable = measurable.measure(constraints)
            if (currentRow.isNotEmpty() && currentLineWidth + hSpacingPx + placeable.width > constraints.maxWidth) {
                rows.add(currentRow)
                rowWidths.add(currentLineWidth)
                rowHeights.add(currentLineHeight)
                currentRow = mutableListOf()
                currentLineWidth = 0
                currentLineHeight = 0
            }
            if (currentRow.isNotEmpty()) {
                currentLineWidth += hSpacingPx
            }
            currentRow.add(placeable)
            currentLineWidth += placeable.width
            currentLineHeight = maxOf(currentLineHeight, placeable.height)
        }

        if (currentRow.isNotEmpty()) {
            rows.add(currentRow)
            rowWidths.add(currentLineWidth)
            rowHeights.add(currentLineHeight)
        }

        val totalHeight = (rowHeights.sum() + (rows.size - 1).coerceAtLeast(0) * vSpacingPx)
            .coerceIn(constraints.minHeight, constraints.maxHeight)
        val maxWidth = (rowWidths.maxOrNull() ?: constraints.minWidth)
            .coerceIn(constraints.minWidth, constraints.maxWidth)

        layout(maxWidth, totalHeight) {
            var yOffset = 0
            for (i in rows.indices) {
                var xOffset = 0
                val row = rows[i]
                for (placeable in row) {
                    placeable.placeRelative(x = xOffset, y = yOffset)
                    xOffset += placeable.width + hSpacingPx
                }
                yOffset += rowHeights[i] + vSpacingPx
            }
        }
    }
}
