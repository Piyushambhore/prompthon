package org.jansaarthi.app.ui.screens.schemes

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
import org.jansaarthi.app.ui.localization.getJanSaarthiStrings
import org.jansaarthi.app.ui.theme.*

/**
 * ----------------------------------------------------------------------------
 * JanSaarthi "All Government Schemes" Screen (Frontend UI Only)
 * ----------------------------------------------------------------------------
 * Dedicated master directory providing a single-window portal across:
 * - All Schemes
 * - Central Government Schemes
 * - State Government Schemes
 * - Union Territory Schemes
 *
 * Each Scheme Card explicitly displays:
 * 1. Scheme Name (English + localized Hindi)
 * 2. Government Level Badge (Central / State / UT)
 * 3. State / UT Indicator
 * 4. Ministry / Department
 * 5. Category (with Material icon & domain color)
 * 6. Short Benefit Highlight (with financial amount and frequency)
 * 7. Eligibility Indicator (criteria match snippet & verified badges)
 * 8. Save Icon (interactive bookmark toggle)
 *
 * Frontend UI Only: Uses realistic mock government data with clear prototype disclaimer.
 * No backend, database, Firebase, or external API code.
 */

// -------------------------------------------------------------
// ENUMS & MODELS FOR "ALL SCHEMES" DIRECTORY
// -------------------------------------------------------------

enum class GovLevelTab(val titleEn: String, val titleHi: String, val icon: ImageVector) {
    ALL("All", "सभी", Icons.Default.AllInclusive),
    CENTRAL("Central Government", "केंद्र सरकार", Icons.Default.AccountBalance),
    STATE("State Government", "राज्य सरकार", Icons.Default.Apartment),
    UT("Union Territory", "केंद्र शासित प्रदेश", Icons.Default.LocationCity);

    fun getTitle(strings: org.jansaarthi.app.ui.localization.JanSaarthiStrings): String = when (this) {
        ALL -> strings.allFilterLabel
        CENTRAL -> strings.centralFilterLabel
        STATE -> strings.stateFilterLabel
        UT -> strings.utFilterLabel
    }
}

enum class EligibilityMatchStatus(val labelEn: String, val labelHi: String, val color: Color, val bgColor: Color) {
    ELIGIBLE("✔ You are Eligible", "✔ आप पात्र हैं", Color(0xFF15803D), Color(0xFFDCFCE7)),
    FAMILY("👨‍👩‍👧 Family Benefit", "👨‍👩‍👧 परिवार लाभ", Color(0xFF0369A1), Color(0xFFE0F2FE)),
    CONDITIONAL("⚠ Check Criteria", "⚠ पात्रता जांचें", Color(0xFFB45309), Color(0xFFFEF3C7)),
    UNIVERSAL("🌐 Universal Citizen", "🌐 सार्वभौमिक नागरिक", Color(0xFF4338CA), Color(0xFFEEF2FF));

    fun getLabel(strings: org.jansaarthi.app.ui.localization.JanSaarthiStrings): String = when (this) {
        ELIGIBLE -> "✔ ${strings.eligibleStatusLabel}"
        FAMILY -> "👨‍👩‍👧 ${strings.familyBenefitLabel}"
        CONDITIONAL -> "⚠ ${strings.conditionalMatchLabel}"
        UNIVERSAL -> "🌐 ${strings.universalCitizenLabel}"
    }
}

data class MasterGovernmentScheme(
    val id: String,
    val nameEn: String,
    val nameHi: String,
    val level: GovLevelTab, // CENTRAL, STATE, UT
    val stateOrUtName: String, // "National (All States & UTs)", "Maharashtra", "NCT of Delhi", etc.
    val ministryOrDeptEn: String,
    val ministryOrDeptHi: String,
    val categoryName: String,
    val categoryIcon: ImageVector,
    val categoryColor: Color,
    val shortBenefitEn: String,
    val shortBenefitHi: String,
    val financialAmountValue: Long,
    val eligibilitySummaryEn: String,
    val eligibilitySummaryHi: String,
    val eligibilityStatus: EligibilityMatchStatus,
    val targetGroup: String, // "Farmers", "Women", "Youth", "Senior Citizens", "General"
    val isDbtEnabled: Boolean = true,
    val overviewEn: String,
    val overviewHi: String,
    val keyPoints: List<String>,
    val requiredDocuments: List<String>,
    val applicationProcess: String,
    val officialPortal: String
) {
    fun getName(lang: AppLanguage): String = if (lang == AppLanguage.HINDI) nameHi else nameEn
    fun getMinistry(lang: AppLanguage): String = if (lang == AppLanguage.HINDI) ministryOrDeptHi else ministryOrDeptEn
    fun getBenefit(lang: AppLanguage): String = if (lang == AppLanguage.HINDI) shortBenefitHi else shortBenefitEn
    fun getEligibility(lang: AppLanguage): String = if (lang == AppLanguage.HINDI) eligibilitySummaryHi else eligibilitySummaryEn
    fun getOverview(lang: AppLanguage): String = if (lang == AppLanguage.HINDI) overviewHi else overviewEn
}

// -------------------------------------------------------------
// REALISTIC LOCAL MOCK DATASET
// -------------------------------------------------------------

val ALL_GOVERNMENT_SCHEMES_MOCK: List<MasterGovernmentScheme> = listOf(
    // ----------------- CENTRAL GOVERNMENT SCHEMES -----------------
    MasterGovernmentScheme(
        id = "M-CEN-01",
        nameEn = "PM Kisan Samman Nidhi (PM-KISAN)",
        nameHi = "प्रधानमंत्री किसान सम्मान निधि",
        level = GovLevelTab.CENTRAL,
        stateOrUtName = "National (All States & UTs)",
        ministryOrDeptEn = "Ministry of Agriculture & Farmers Welfare",
        ministryOrDeptHi = "कृषि एवं किसान कल्याण मंत्रालय",
        categoryName = "Agriculture, Rural & Environment",
        categoryIcon = Icons.Default.Agriculture,
        categoryColor = Color(0xFF15803D),
        shortBenefitEn = "₹6,000/year via direct DBT in 3 equal installments of ₹2,000",
        shortBenefitHi = "₹6,000 प्रति वर्ष 3 समान किस्तों में सीधे बैंक खाते में (डीबीटी)",
        financialAmountValue = 6000,
        eligibilitySummaryEn = "Landholding farmer families with cultivable land up to 2 Ha. E-KYC mandatory.",
        eligibilitySummaryHi = "खेती योग्य भूमि वाले किसान परिवार। ई-केवाईसी अनिवार्य।",
        eligibilityStatus = EligibilityMatchStatus.ELIGIBLE,
        targetGroup = "Farmers",
        isDbtEnabled = true,
        overviewEn = "Direct income support of ₹6,000 per annum paid in three equal installments to all landholder farmer families across India.",
        overviewHi = "देशभर के सभी भूमिधारक किसान परिवारों को ₹6,000 प्रति वर्ष की प्रत्यक्ष आय सहायता।",
        keyPoints = listOf("100% funded by Central Government", "Direct PFMS transfer without intermediaries", "Over 11 crore farmers benefited"),
        requiredDocuments = listOf("Aadhaar Card", "Land ownership proof (7/12 / Khasra)", "Bank Account details"),
        applicationProcess = "Register at pmkisan.gov.in or visit any Common Service Centre (CSC).",
        officialPortal = "https://pmkisan.gov.in"
    ),
    MasterGovernmentScheme(
        id = "M-CEN-02",
        nameEn = "Ayushman Bharat PM-JAY (Golden Card)",
        nameHi = "आयुष्मान भारत प्रधानमंत्री जन आरोग्य योजना",
        level = GovLevelTab.CENTRAL,
        stateOrUtName = "National (All States & UTs)",
        ministryOrDeptEn = "Ministry of Health & Family Welfare",
        ministryOrDeptHi = "स्वास्थ्य एवं परिवार कल्याण मंत्रालय",
        categoryName = "Health & Wellness",
        categoryIcon = Icons.Default.HealthAndSafety,
        categoryColor = Color(0xFF0F766E),
        shortBenefitEn = "₹5,00,000 cashless family hospitalisation cover per year across India",
        shortBenefitHi = "प्रति परिवार प्रति वर्ष ₹5,00,000 तक का मुफ़्त कैशलेस अस्पताल इलाज",
        financialAmountValue = 500000,
        eligibilitySummaryEn = "SECC-deprived rural/urban families and all senior citizens aged 70+ years.",
        eligibilitySummaryHi = "एसईसीसी के पात्र गरीब परिवार एवं 70 वर्ष से अधिक आयु के सभी वरिष्ठ नागरिक।",
        eligibilityStatus = EligibilityMatchStatus.FAMILY,
        targetGroup = "General",
        isDbtEnabled = false,
        overviewEn = "World's largest government-financed health assurance program offering cashless secondary and tertiary inpatient treatment.",
        overviewHi = "विश्व की सबसे बड़ी सरकारी स्वास्थ्य योजना, 27,000+ अस्पतालों में कैशलेस इलाज।",
        keyPoints = listOf("Covers 1,949 medical and surgical procedures", "No cap on family size or age", "Pre-existing diseases covered from day one"),
        requiredDocuments = listOf("Aadhaar Card", "Ration Card (NFSA)"),
        applicationProcess = "Generate Ayushman Card instantly on beneficiary.nha.gov.in or at hospital Ayushman desk.",
        officialPortal = "https://pmjay.gov.in"
    ),
    MasterGovernmentScheme(
        id = "M-CEN-03",
        nameEn = "Pradhan Mantri Mudra Yojana (PMMY)",
        nameHi = "प्रधानमंत्री मुद्रा योजना",
        level = GovLevelTab.CENTRAL,
        stateOrUtName = "National (All States & UTs)",
        ministryOrDeptEn = "Ministry of Finance",
        ministryOrDeptHi = "वित्त मंत्रालय",
        categoryName = "Business & Entrepreneurship",
        categoryIcon = Icons.Default.BusinessCenter,
        categoryColor = Color(0xFF9A3412),
        shortBenefitEn = "Collateral-free enterprise loans up to ₹10 Lakhs (Shishu, Kishore, Tarun)",
        shortBenefitHi = "बिना किसी गारंटी के ₹10 लाख तक का व्यवसाय ऋण (शिशु, किशोर, तरुण)",
        financialAmountValue = 1000000,
        eligibilitySummaryEn = "Non-corporate, non-farm small and micro entrepreneurs in trading/services.",
        eligibilitySummaryHi = "विनिर्माण, व्यापार एवं सेवा क्षेत्र में कार्यरत गैर-कॉर्पोरेट लघु व सूक्ष्म उद्यमी।",
        eligibilityStatus = EligibilityMatchStatus.CONDITIONAL,
        targetGroup = "Youth & Entrepreneurs",
        isDbtEnabled = false,
        overviewEn = "Provides funding to non-corporate micro-businesses up to ₹10 Lakhs without requiring collateral security.",
        overviewHi = "सूक्ष्म एवं लघु उद्यमियों को व्यवसाय शुरू करने या विस्तार करने हेतु रियायती ऋण।",
        keyPoints = listOf("Shishu: Loans up to ₹50k", "Kishore: ₹50k to ₹5 Lakhs", "Tarun: ₹5 Lakhs to ₹10 Lakhs"),
        requiredDocuments = listOf("Udyam Registration", "PAN Card & Aadhaar", "Project Report", "Bank Statement"),
        applicationProcess = "Apply on udyamimitra.in or contact any commercial bank branch.",
        officialPortal = "https://www.mudra.org.in"
    ),
    MasterGovernmentScheme(
        id = "M-CEN-04",
        nameEn = "Pradhan Mantri Awas Yojana - Gramin (PMAY-G)",
        nameHi = "प्रधानमंत्री आवास योजना - ग्रामीण",
        level = GovLevelTab.CENTRAL,
        stateOrUtName = "National (All States & UTs)",
        ministryOrDeptEn = "Ministry of Rural Development",
        ministryOrDeptHi = "ग्रामीण विकास मंत्रालय",
        categoryName = "Housing & Shelter",
        categoryIcon = Icons.Default.Home,
        categoryColor = Color(0xFFB45309),
        shortBenefitEn = "₹1,20,000 cash grant + ₹12,000 for toilet + 90 days MGNREGA wages",
        shortBenefitHi = "पक्का मकान निर्माण हेतु ₹1,20,000 अनुदान + ₹12,000 शौचालय + 90 दिन मनरेगा मजदूरी",
        financialAmountValue = 150000,
        eligibilitySummaryEn = "Houseless rural families and households living in zero/one room kutcha houses.",
        eligibilitySummaryHi = "कच्चे मकानों में रहने वाले या बेघर ग्रामीण परिवार (आवास+ सूची)।",
        eligibilityStatus = EligibilityMatchStatus.FAMILY,
        targetGroup = "Rural Families",
        isDbtEnabled = true,
        overviewEn = "Assists rural poor families in constructing disaster-resilient pucca houses with basic amenities.",
        overviewHi = "ग्रामीण भारत में कच्चे घरों में रहने वाले परिवारों को पक्के मकान निर्माण हेतु वित्तीय अनुदान।",
        keyPoints = listOf("Milestone-based geotagged direct payments", "Toilet incentive under Swachh Bharat", "Piped water and electricity converged"),
        requiredDocuments = listOf("Aadhaar Card", "Bank Passbook", "MGNREGA Job Card", "Land possession proof"),
        applicationProcess = "Selection via Gram Sabha verification from official Awas+ waiting list.",
        officialPortal = "https://pmayg.nic.in"
    ),
    MasterGovernmentScheme(
        id = "M-CEN-05",
        nameEn = "Sukanya Samriddhi Yojana (SSY)",
        nameHi = "सुकन्या समृद्धि योजना",
        level = GovLevelTab.CENTRAL,
        stateOrUtName = "National (All States & UTs)",
        ministryOrDeptEn = "Ministry of Finance",
        ministryOrDeptHi = "वित्त मंत्रालय",
        categoryName = "Women & Child",
        categoryIcon = Icons.Default.FamilyRestroom,
        categoryColor = Color(0xFFBE185D),
        shortBenefitEn = "8.2% sovereign tax-free interest + 100% tax exemption under Section 80C",
        shortBenefitHi = "8.2% कर-मुक्त चक्रवृद्धि ब्याज + धारा 80C के तहत पूर्ण कर छूट",
        financialAmountValue = 1500000,
        eligibilitySummaryEn = "Parents/guardians of girl child from birth up to 10 years of age.",
        eligibilitySummaryHi = "जन्म से 10 वर्ष तक की आयु की बालिका के माता-पिता या कानूनी अभिभावक।",
        eligibilityStatus = EligibilityMatchStatus.ELIGIBLE,
        targetGroup = "Girl Child",
        isDbtEnabled = false,
        overviewEn = "Small deposit savings scheme for girl child under Beti Bachao Beti Padhao offering high compounding interest and complete tax immunity.",
        overviewHi = "बालिकाओं की उच्च शिक्षा एवं भविष्य की सुरक्षा हेतु सर्वोच्च ब्याज वाली सुरक्षित बचत योजना।",
        keyPoints = listOf("Min deposit ₹250/year, max ₹1.5 Lakhs", "EEE tax status (Investment, Interest, Maturity)", "Matures at 21 years"),
        requiredDocuments = listOf("Birth Certificate of girl child", "Guardian's Aadhaar & PAN Card", "Address Proof"),
        applicationProcess = "Open account at any Post Office or authorized commercial bank branch.",
        officialPortal = "https://www.indiapost.gov.in"
    ),
    MasterGovernmentScheme(
        id = "M-CEN-06",
        nameEn = "PM Vishwakarma Scheme",
        nameHi = "पीएम विश्वकर्मा योजना",
        level = GovLevelTab.CENTRAL,
        stateOrUtName = "National (All States & UTs)",
        ministryOrDeptEn = "Ministry of Micro, Small and Medium Enterprises",
        ministryOrDeptHi = "सूक्ष्म, लघु एवं मध्यम उद्यम मंत्रालय",
        categoryName = "Skills & Employment",
        categoryIcon = Icons.Default.Handyman,
        categoryColor = Color(0xFF334155),
        shortBenefitEn = "₹15,000 toolkit voucher + collateral-free loans up to ₹3 Lakhs at 5% interest",
        shortBenefitHi = "₹15,000 आधुनिक टूलकिट ई-वाउचर + ₹3 लाख तक का 5% ब्याज पर बिना गारंटी ऋण",
        financialAmountValue = 315000,
        eligibilitySummaryEn = "Traditional artisans working in 18 identified trades (Carpenters, Masons, Tailors, etc.).",
        eligibilitySummaryHi = "18 पारंपरिक व्यवसायों (बढ़ई, दर्जी, लोहार, कुम्हार, मोची) के शिल्पी व कारीगर।",
        eligibilityStatus = EligibilityMatchStatus.ELIGIBLE,
        targetGroup = "Artisans & Craftsmen",
        isDbtEnabled = true,
        overviewEn = "End-to-end holistic support for traditional artisans including recognition ID, skill upgradation stipend, toolkits, and low-interest credit.",
        overviewHi = "हाथ और औजारों से काम करने वाले कारीगरों को पहचान, प्रशिक्षण, टूलकिट व रियायती ऋण सहायता।",
        keyPoints = listOf("Free Vishwakarma ID and certificate", "₹500/day training stipend", "Collateral-free credit in 2 tranches"),
        requiredDocuments = listOf("Aadhaar Card", "Bank Account Passbook", "Ration Card"),
        applicationProcess = "Apply free with biometric verification at nearest CSC center.",
        officialPortal = "https://pmvishwakarma.gov.in"
    ),

    // ----------------- STATE GOVERNMENT SCHEMES -----------------
    MasterGovernmentScheme(
        id = "M-STA-01",
        nameEn = "Mukhyamantri Majhi Ladki Bahin Yojana",
        nameHi = "मुख्यमंत्री माझी लाडकी बहीण योजना",
        level = GovLevelTab.STATE,
        stateOrUtName = "Maharashtra",
        ministryOrDeptEn = "Department of Women & Child Development (Govt of Maharashtra)",
        ministryOrDeptHi = "महिला व बाल विकास विभाग (महाराष्ट्र शासन)",
        categoryName = "Women & Child",
        categoryIcon = Icons.Default.FamilyRestroom,
        categoryColor = Color(0xFFBE185D),
        shortBenefitEn = "₹1,500 monthly direct cash transfer (₹18,000/year) into woman's bank account",
        shortBenefitHi = "₹1,500 प्रति माह सीधे बैंक खाते में (वार्षिक ₹18,000) महिला स्वावलंबन सहायता",
        financialAmountValue = 18000,
        eligibilitySummaryEn = "Women aged 21-65 years residing in Maharashtra with annual family income under ₹2.5 Lakhs.",
        eligibilitySummaryHi = "महाराष्ट्र की 21-65 वर्ष की महिलाएं, पारिवारिक वार्षिक आय ₹2.5 लाख से कम।",
        eligibilityStatus = EligibilityMatchStatus.ELIGIBLE,
        targetGroup = "Women",
        isDbtEnabled = true,
        overviewEn = "Landmark social security and basic income initiative by Maharashtra Government providing unconditional monthly financial support.",
        overviewHi = "महिलाओं के पोषण, स्वास्थ्य व आर्थिक स्वावलंबन हेतु महाराष्ट्र सरकार की मासिक सहायता योजना।",
        keyPoints = listOf("Monthly transfer on 15th via Aadhaar DBT", "Ration card (Yellow/Orange) exempt from income proof", "Over 2.2 crore women registered"),
        requiredDocuments = listOf("Aadhaar Card", "Maharashtra Domicile / Old Ration Card", "Bank Passbook seeded with DBT"),
        applicationProcess = "Apply online via Nari Shakti Doot App or at local Anganwadi/Setu Kendra.",
        officialPortal = "https://ladakibahin.maharashtra.gov.in"
    ),
    MasterGovernmentScheme(
        id = "M-STA-02",
        nameEn = "Namo Shetkari Mahasanman Nidhi Yojana",
        nameHi = "नमो शेतकरी महासन्मान निधी योजना",
        level = GovLevelTab.STATE,
        stateOrUtName = "Maharashtra",
        ministryOrDeptEn = "Department of Agriculture (Govt of Maharashtra)",
        ministryOrDeptHi = "कृषि विभाग (महाराष्ट्र शासन)",
        categoryName = "Agriculture, Rural & Environment",
        categoryIcon = Icons.Default.Agriculture,
        categoryColor = Color(0xFF15803D),
        shortBenefitEn = "₹6,000/year state top-up grant (Combined ₹12,000/yr with PM-KISAN)",
        shortBenefitHi = "₹6,000 प्रति वर्ष राज्य टॉप-अप अनुदान (पीएम-किसान संग कुल ₹12,000/वर्ष)",
        financialAmountValue = 6000,
        eligibilitySummaryEn = "Registered PM-KISAN beneficiary farmers holding agricultural land in Maharashtra.",
        eligibilitySummaryHi = "महाराष्ट्र के पंजीकृत पीएम-किसान लाभार्थी एवं भूमिधारक किसान।",
        eligibilityStatus = EligibilityMatchStatus.ELIGIBLE,
        targetGroup = "Farmers",
        isDbtEnabled = true,
        overviewEn = "Maharashtra Government supplementary income grant matching PM-KISAN, credited three times a year directly to Aadhaar bank accounts.",
        overviewHi = "महाराष्ट्र के किसानों को अतिरिक्त ₹6,000 वार्षिक सहायता देकर कुल ₹12,000 की सुनिश्चित आय।",
        keyPoints = listOf("Direct Aadhaar DBT transfer", "Automatic enrollment for verified PM-KISAN beneficiaries", "Released in 3 seasonal tranches"),
        requiredDocuments = listOf("PM-KISAN Registration ID", "Maharashtra 7/12 Land Record", "Aadhaar Card"),
        applicationProcess = "Existing PM-KISAN beneficiaries in Maharashtra are auto-verified on MahaDBT.",
        officialPortal = "https://mahadbt.maharashtra.gov.in"
    ),
    MasterGovernmentScheme(
        id = "M-STA-03",
        nameEn = "Kalaignar Magalir Urimai Thittam",
        nameHi = "कलैग्नार महिला अधिकार योजना",
        level = GovLevelTab.STATE,
        stateOrUtName = "Tamil Nadu",
        ministryOrDeptEn = "Special Programme Implementation Dept (Govt of Tamil Nadu)",
        ministryOrDeptHi = "विशेष कार्यक्रम क्रियान्वयन विभाग (तमिलनाडु)",
        categoryName = "Women & Child",
        categoryIcon = Icons.Default.VolunteerActivism,
        categoryColor = Color(0xFFBE185D),
        shortBenefitEn = "₹1,000 monthly basic income transfer (₹12,000/year) to female heads of family",
        shortBenefitHi = "₹1,000 प्रति माह (वार्षिक ₹12,000) महिला परिवार प्रमुख को सम्मान निधि",
        financialAmountValue = 12000,
        eligibilitySummaryEn = "Women heads of families aged 21+ residing in Tamil Nadu with family income < ₹2.5 Lakhs.",
        eligibilitySummaryHi = "तमिलनाडु की 21+ वर्ष की महिला मुखिया, पारिवारिक वार्षिक आय ₹2.5 लाख से कम।",
        eligibilityStatus = EligibilityMatchStatus.ELIGIBLE,
        targetGroup = "Women",
        isDbtEnabled = true,
        overviewEn = "Pioneering basic income program in Tamil Nadu recognizing the unpaid domestic labor of female family heads.",
        overviewHi = "महिलाओं के श्रम के सम्मान और आर्थिक सशक्तिकरण हेतु तमिलनाडु की मासिक नकद अंतरण योजना।",
        keyPoints = listOf("Monthly credit on the 15th", "Reaches over 1.15 crore women", "Direct transfer to woman's sole bank account"),
        requiredDocuments = listOf("Tamil Nadu Smart Family Card", "Aadhaar Card", "Electricity Consumer Number"),
        applicationProcess = "Enroll at special village/ward revenue camps with biometric e-KYC.",
        officialPortal = "https://kmut.tn.gov.in"
    ),
    MasterGovernmentScheme(
        id = "M-STA-04",
        nameEn = "Mukhyamantri Kanya Sumangala Yojana",
        nameHi = "मुख्यमंत्री कन्या सुमंगला योजना",
        level = GovLevelTab.STATE,
        stateOrUtName = "Uttar Pradesh",
        ministryOrDeptEn = "Women and Child Development Dept (Govt of Uttar Pradesh)",
        ministryOrDeptHi = "महिला एवं बाल विकास विभाग (उत्तर प्रदेश शासन)",
        categoryName = "Women & Child",
        categoryIcon = Icons.Default.School,
        categoryColor = Color(0xFF1D4ED8),
        shortBenefitEn = "₹25,000 financial package in 6 installments from birth to college graduation",
        shortBenefitHi = "जन्म से लेकर कॉलेज स्नातक तक 6 चरणों में कुल ₹25,000 की वित्तीय सहायता",
        financialAmountValue = 25000,
        eligibilitySummaryEn = "Resident girl children of Uttar Pradesh with annual family income up to ₹3 Lakhs.",
        eligibilitySummaryHi = "उत्तर प्रदेश की स्थायी निवासी बालिकाएं, पारिवारिक वार्षिक आय अधिकतम ₹3 लाख।",
        eligibilityStatus = EligibilityMatchStatus.ELIGIBLE,
        targetGroup = "Girl Child",
        isDbtEnabled = true,
        overviewEn = "Conditional cash transfer program by UP Government to enhance health, nutrition, and schooling of daughters from birth to degree level.",
        overviewHi = "बेटियों की शिक्षा, स्वास्थ्य एवं उज्ज्वल भविष्य हेतु 6 चरणों में चरणबद्ध आर्थिक अनुदान।",
        keyPoints = listOf("Stage 1: ₹5,000 at birth", "Stage 2-5: Vaccinations & school admissions", "Stage 6: ₹7,000 on college admission"),
        requiredDocuments = listOf("UP Domicile Certificate", "Girl's Birth Certificate", "Income Certificate (< ₹3 Lakhs)"),
        applicationProcess = "Apply online via mksy.up.gov.in portal.",
        officialPortal = "https://mksy.up.gov.in"
    ),
    MasterGovernmentScheme(
        id = "M-STA-05",
        nameEn = "Kanya Utthan Yojana (Higher Education)",
        nameHi = "मुख्यमंत्री कन्या उत्थान योजना",
        level = GovLevelTab.STATE,
        stateOrUtName = "Bihar",
        ministryOrDeptEn = "Education Department (Govt of Bihar)",
        ministryOrDeptHi = "शिक्षा विभाग (बिहार सरकार)",
        categoryName = "Education & Learning",
        categoryIcon = Icons.Default.School,
        categoryColor = Color(0xFF1D4ED8),
        shortBenefitEn = "₹50,000 direct grant on graduation + ₹25,000 on passing Class 12",
        shortBenefitHi = "स्नातक उत्तीर्ण होने पर ₹50,000 एवं 12वीं उत्तीर्ण पर ₹25,000 नकद प्रोत्साहन",
        financialAmountValue = 50000,
        eligibilitySummaryEn = "Unmarried female students residing in Bihar graduating from recognized state universities.",
        eligibilitySummaryHi = "बिहार की निवासी अविवाहित छात्राएं जिन्होंने राज्य के मान्यता प्राप्त विश्वविद्यालय से स्नातक उत्तीर्ण किया हो।",
        eligibilityStatus = EligibilityMatchStatus.ELIGIBLE,
        targetGroup = "Female Students",
        isDbtEnabled = true,
        overviewEn = "Flagship Bihar incentive program to encourage female higher education, reduce dropout rates, and eliminate child marriage.",
        overviewHi = "बिहार में बेटियों की उच्च शिक्षा को बढ़ावा देने और बाल विवाह रोकने हेतु एकमुश्त नकद पुरस्कार।",
        keyPoints = listOf("₹50,000 credited directly to student account", "No family income ceiling for degree incentive", "Online verification via MedhaSoft"),
        requiredDocuments = listOf("Bihar Domicile Certificate", "Graduation Marksheet/Degree", "Student's Bank Passbook"),
        applicationProcess = "Submit registration on medhasoft.bih.nic.in with university roll number.",
        officialPortal = "https://medhasoft.bih.nic.in"
    ),
    MasterGovernmentScheme(
        id = "M-STA-06",
        nameEn = "Gruha Lakshmi Scheme",
        nameHi = "गृह लक्ष्मी योजना",
        level = GovLevelTab.STATE,
        stateOrUtName = "Karnataka",
        ministryOrDeptEn = "Women & Child Development Dept (Govt of Karnataka)",
        ministryOrDeptHi = "महिला एवं बाल विकास विभाग (कर्नाटक सरकार)",
        categoryName = "Women & Child",
        categoryIcon = Icons.Default.FamilyRestroom,
        categoryColor = Color(0xFFBE185D),
        shortBenefitEn = "₹2,000 monthly financial aid (₹24,000/year) to female head of household",
        shortBenefitHi = "₹2,000 प्रति माह (वार्षिक ₹24,000) महिला गृहस्वामिनी को प्रत्यक्ष बैंक अंतरण",
        financialAmountValue = 24000,
        eligibilitySummaryEn = "Women identified as head of household in BPL, APL, or Antyodaya ration cards.",
        eligibilitySummaryHi = "कर्नाटक के बीपीएल, एपीएल या अंत्योदय राशन कार्ड में मुखिया के रूप में दर्ज महिलाएं।",
        eligibilityStatus = EligibilityMatchStatus.ELIGIBLE,
        targetGroup = "Women",
        isDbtEnabled = true,
        overviewEn = "One of Karnataka's five guarantee schemes providing continuous monthly income support to female family heads.",
        overviewHi = "कर्नाटक की गारंटी योजना के तहत महिला गृह स्वामिनियों को प्रति माह ₹2,000 की वित्तीय सुरक्षा।",
        keyPoints = listOf("₹2,000 credited monthly via DBT", "Beneficiaries or spouses must not be taxpayers", "Over 1.2 crore women enrolled"),
        requiredDocuments = listOf("Ration Card (BPL/APL)", "Aadhaar Card of Woman & Husband", "Bank Passbook"),
        applicationProcess = "Apply through Seva Sindhu portal or at Karnataka-One / Grama-One centres.",
        officialPortal = "https://sevasindhugs.karnataka.gov.in"
    ),

    // ----------------- UNION TERRITORY SCHEMES -----------------
    MasterGovernmentScheme(
        id = "M-UT-01",
        nameEn = "Delhi Free Bus Travel Scheme for Women (Pink Ticket)",
        nameHi = "दिल्ली महिला मुफ़्त बस यात्रा योजना (गुलाबी टिकट)",
        level = GovLevelTab.UT,
        stateOrUtName = "NCT of Delhi",
        ministryOrDeptEn = "Transport Department (Govt of NCT of Delhi)",
        ministryOrDeptHi = "परिवहन विभाग (राष्ट्रीय राजधानी क्षेत्र दिल्ली सरकार)",
        categoryName = "Transport & Infrastructure",
        categoryIcon = Icons.Default.DirectionsBus,
        categoryColor = Color(0xFF1E293B),
        shortBenefitEn = "100% free bus travel on all DTC and Cluster AC/non-AC buses in Delhi",
        shortBenefitHi = "दिल्ली में सभी डीटीसी एवं क्लस्टर एसी/नॉन-एसी बसों में 100% मुफ़्त यात्रा",
        financialAmountValue = 18000,
        eligibilitySummaryEn = "All women and girl passengers traveling in DTC and Cluster city buses across Delhi.",
        eligibilitySummaryHi = "दिल्ली में डीटीसी एवं क्लस्टर बसों में यात्रा करने वाली सभी महिलाएं एवं छात्राएं।",
        eligibilityStatus = EligibilityMatchStatus.UNIVERSAL,
        targetGroup = "Women",
        isDbtEnabled = false,
        overviewEn = "Promotes women's mobility, workforce participation, and safety across the National Capital Territory with zero-fare single journey Pink passes.",
        overviewHi = "दिल्ली में महिलाओं की सुरक्षित, सुलभ और मुफ़्त सार्वजनिक परिवहन यात्रा हेतु क्रांतिकारी पहल।",
        keyPoints = listOf("No pre-registration or identity card needed", "Single-journey Pink passes issued on board", "Over 150 crore free trips recorded"),
        requiredDocuments = listOf("No documents required on board"),
        applicationProcess = "Simply board any DTC / Cluster bus in Delhi and ask conductor for free Pink Ticket.",
        officialPortal = "https://transport.delhi.gov.in"
    ),
    MasterGovernmentScheme(
        id = "M-UT-02",
        nameEn = "Ladli Beti Financial Security Scheme",
        nameHi = "लाडली बेटी वित्तीय सुरक्षा योजना",
        level = GovLevelTab.UT,
        stateOrUtName = "Jammu & Kashmir (UT)",
        ministryOrDeptEn = "Social Welfare Department (UT of Jammu & Kashmir)",
        ministryOrDeptHi = "समाज कल्याण विभाग (केंद्र शासित प्रदेश जम्मू-कश्मीर)",
        categoryName = "Women & Child",
        categoryIcon = Icons.Default.FamilyRestroom,
        categoryColor = Color(0xFFBE185D),
        shortBenefitEn = "₹1,000/month government contribution into savings account for 14 years + maturity ₹6.5 Lakhs",
        shortBenefitHi = "सरकार द्वारा 14 वर्षों तक ₹1,000/माह बचत जमा + 21 वर्ष की आयु पर ₹6.5 लाख परिपक्वता",
        financialAmountValue = 650000,
        eligibilitySummaryEn = "Girl children born on or after 01/04/2015 in J&K UT with family income under ₹75,000/year.",
        eligibilitySummaryHi = "जम्मू-कश्मीर में जन्म लेने वाली बालिकाएं, पारिवारिक वार्षिक आय ₹75,000 से कम।",
        eligibilityStatus = EligibilityMatchStatus.ELIGIBLE,
        targetGroup = "Girl Child",
        isDbtEnabled = true,
        overviewEn = "Social welfare program to arrest female foeticide, balance child sex ratio, and ensure robust financial foundation for girl children in J&K.",
        overviewHi = "जम्मू-कश्मीर में बालिकाओं के लिंगानुपात सुधार और भविष्य की आर्थिक आत्मनिर्भरता हेतु बचत योजना।",
        keyPoints = listOf("UT deposits ₹1,000 every month for 14 years", "Account locked till girl reaches 21 years", "Disbursed via J&K Bank branches"),
        requiredDocuments = listOf("Birth Certificate of child", "J&K Domicile Certificate", "Income Certificate (< ₹75,000)"),
        applicationProcess = "Submit application through Child Development Project Officer (CDPO) or District Social Welfare Office.",
        officialPortal = "https://jksocialwelfare.nic.in"
    ),
    MasterGovernmentScheme(
        id = "M-UT-03",
        nameEn = "Puducherry Free School Uniforms, Books & Cycles Scheme",
        nameHi = "पुदुचेरी मुफ़्त स्कूल वर्दी, पाठ्यपुस्तक एवं साइकिल योजना",
        level = GovLevelTab.UT,
        stateOrUtName = "Puducherry (UT)",
        ministryOrDeptEn = "Directorate of School Education (Govt of Puducherry)",
        ministryOrDeptHi = "स्कूली शिक्षा निदेशालय (पुदुचेरी सरकार)",
        categoryName = "Education & Learning",
        categoryIcon = Icons.Default.School,
        categoryColor = Color(0xFF1D4ED8),
        shortBenefitEn = "Free school uniforms, textbooks, notebooks, footwear and free bicycles for Class 9 students",
        shortBenefitHi = "मुफ़्त स्कूल यूनिफॉर्म, पाठ्यपुस्तकें, जूते एवं कक्षा 9 के छात्र-छात्राओं को मुफ़्त साइकिल",
        financialAmountValue = 12000,
        eligibilitySummaryEn = "Students enrolled in Government and Government-aided schools across Puducherry UT.",
        eligibilitySummaryHi = "पुदुचेरी के सरकारी एवं सहायता प्राप्त विद्यालयों में अध्ययनरत कक्षा 1 से 12 के विद्यार्थी।",
        eligibilityStatus = EligibilityMatchStatus.ELIGIBLE,
        targetGroup = "Students",
        isDbtEnabled = false,
        overviewEn = "Comprehensive educational support ensuring zero out-of-pocket costs for school education in Puducherry, Karaikal, Mahe, and Yanam.",
        overviewHi = "पुदुचेरी के विद्यार्थियों को प्राथमिक से उच्च माध्यमिक तक मुफ़्त शैक्षिक सामग्री व साइकिल वितरण।",
        keyPoints = listOf("2 sets of uniforms per student per academic year", "Free bicycles for rural students entering Class 9", "Full textbook and notebook kits provided"),
        requiredDocuments = listOf("Student Admission ID", "Aadhaar Card", "Puducherry Resident Domicile"),
        applicationProcess = "Automatic distribution through respective school heads at beginning of school session.",
        officialPortal = "https://schooledn.py.gov.in"
    ),
    MasterGovernmentScheme(
        id = "M-UT-04",
        nameEn = "Chandigarh Senior Citizen Concession & Day Care Welfare",
        nameHi = "चंडीगढ़ वरिष्ठ नागरिक रियायत एवं कल्याण योजना",
        level = GovLevelTab.UT,
        stateOrUtName = "Chandigarh (UT)",
        ministryOrDeptEn = "Department of Social Welfare (Chandigarh Administration)",
        ministryOrDeptHi = "समाज कल्याण विभाग (चंडीगढ़ प्रशासन)",
        categoryName = "Social Welfare & Empowerment",
        categoryIcon = Icons.Default.Elderly,
        categoryColor = Color(0xFF581C87),
        shortBenefitEn = "50% bus fare concession on CTU buses + free OPD healthcare & day-care recreational club access",
        shortBenefitHi = "सीटीयू बसों में 50% किराया छूट + मुफ़्त ओपीडी इलाज व वरिष्ठ नागरिक डे-केयर केंद्र",
        financialAmountValue = 15000,
        eligibilitySummaryEn = "Permanent residents of Chandigarh aged 60 years and above possessing Senior Citizen Smart Card.",
        eligibilitySummaryHi = "चंडीगढ़ के स्थायी निवासी वरिष्ठ नागरिक (आयु 60 वर्ष या अधिक)।",
        eligibilityStatus = EligibilityMatchStatus.ELIGIBLE,
        targetGroup = "Senior Citizens",
        isDbtEnabled = false,
        overviewEn = "Specialized welfare assistance program by Chandigarh Administration ensuring mobility concessions, healthcare priority, and social engagement.",
        overviewHi = "चंडीगढ़ के वृद्धजनों को सम्मानजनक जीवन, रियायती परिवहन और मुफ़्त स्वास्थ्य सेवाएं प्रदान करने की योजना।",
        keyPoints = listOf("Senior Citizen Identity Smart Card issued", "50% discount on all local CTU routes", "Dedicated senior citizen geriatric clinics"),
        requiredDocuments = listOf("Chandigarh Address Proof (Voter ID/Electricity Bill)", "Age Proof (confirming 60+)", "Aadhaar Card"),
        applicationProcess = "Apply for Senior Citizen ID card at Sampark Centres across Chandigarh.",
        officialPortal = "https://chandigarh.gov.in"
    )
)

// -------------------------------------------------------------
// MAIN "ALL SCHEMES" COMPOSABLE
// -------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllSchemesScreen(
    currentLanguage: AppLanguage = AppLanguage.HINDI,
    onSelectLanguage: (AppLanguage) -> Unit = {},
    isLargeFont: Boolean = false,
    onFontScaleToggle: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigateHome: () -> Unit = {},
    onNavigateSchemes: () -> Unit = {},
    onNavigateApplications: () -> Unit = {},
    onNavigateDocuments: () -> Unit = {},
    onNavigateProfile: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val strings = getJanSaarthiStrings(currentLanguage)
    val fontSizeMultiplier = if (isLargeFont) 1.15f else 1.0f

    // -------------------------------------------------------------
    // LOCAL REACTIVE STATES
    // -------------------------------------------------------------
    var selectedTab by remember { mutableStateOf(GovLevelTab.ALL) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("All Categories") }
    var selectedStateFilter by remember { mutableStateOf("All States & UTs") }
    var onlyDbtFilter by remember { mutableStateOf(false) }
    var selectedTargetGroup by remember { mutableStateOf("All") }
    var selectedSortOption by remember { mutableStateOf(SchemeSortOption.POPULARITY) }

    // Saved Bookmarks Set
    var bookmarkedSchemeIds by remember {
        mutableStateOf(setOf("M-CEN-01", "M-STA-01", "M-CEN-02"))
    }

    // Detail Modal Controller
    var selectedSchemeForDetails by remember { mutableStateOf<MasterGovernmentScheme?>(null) }
    var showFilterSheet by remember { mutableStateOf(false) }

    // -------------------------------------------------------------
    // FILTERING ENGINE
    // -------------------------------------------------------------
    val filteredSchemes = remember(
        selectedTab,
        searchQuery,
        selectedCategoryFilter,
        selectedStateFilter,
        onlyDbtFilter,
        selectedTargetGroup,
        selectedSortOption,
        currentLanguage
    ) {
        val q = searchQuery.trim().lowercase()

        ALL_GOVERNMENT_SCHEMES_MOCK.filter { scheme ->
            // Tab Filter (All, Central, State, UT)
            val matchesTab = when (selectedTab) {
                GovLevelTab.ALL -> true
                GovLevelTab.CENTRAL -> scheme.level == GovLevelTab.CENTRAL
                GovLevelTab.STATE -> scheme.level == GovLevelTab.STATE
                GovLevelTab.UT -> scheme.level == GovLevelTab.UT
            }

            // Search Query Filter
            val matchesQuery = q.isEmpty() ||
                    scheme.nameEn.lowercase().contains(q) ||
                    scheme.nameHi.lowercase().contains(q) ||
                    scheme.ministryOrDeptEn.lowercase().contains(q) ||
                    scheme.ministryOrDeptHi.lowercase().contains(q) ||
                    scheme.stateOrUtName.lowercase().contains(q) ||
                    scheme.categoryName.lowercase().contains(q) ||
                    scheme.shortBenefitEn.lowercase().contains(q) ||
                    scheme.shortBenefitHi.lowercase().contains(q) ||
                    scheme.eligibilitySummaryEn.lowercase().contains(q)

            // Category Filter
            val matchesCategory = if (selectedCategoryFilter == "All Categories") true
            else scheme.categoryName.equals(selectedCategoryFilter, ignoreCase = true)

            // State/UT Filter
            val matchesState = if (selectedStateFilter == "All States & UTs") true
            else scheme.stateOrUtName.contains(selectedStateFilter, ignoreCase = true) || scheme.level == GovLevelTab.CENTRAL

            // DBT Filter
            val matchesDbt = !onlyDbtFilter || scheme.isDbtEnabled

            // Target Group
            val matchesTarget = if (selectedTargetGroup == "All") true
            else scheme.targetGroup.contains(selectedTargetGroup, ignoreCase = true)

            matchesTab && matchesQuery && matchesCategory && matchesState && matchesDbt && matchesTarget
        }.let { list ->
            when (selectedSortOption) {
                SchemeSortOption.POPULARITY -> list
                SchemeSortOption.HIGHEST_BENEFIT -> list.sortedByDescending { it.financialAmountValue }
                SchemeSortOption.NEWLY_LAUNCHED -> list.reversed()
                SchemeSortOption.ALPHABETICAL -> list.sortedBy { it.getName(currentLanguage) }
            }
        }
    }

    // Counts for tabs
    val centralCount = remember { ALL_GOVERNMENT_SCHEMES_MOCK.count { it.level == GovLevelTab.CENTRAL } }
    val stateCount = remember { ALL_GOVERNMENT_SCHEMES_MOCK.count { it.level == GovLevelTab.STATE } }
    val utCount = remember { ALL_GOVERNMENT_SCHEMES_MOCK.count { it.level == GovLevelTab.UT } }
    val totalCount = ALL_GOVERNMENT_SCHEMES_MOCK.size

    Scaffold(
        bottomBar = {
            JanSaarthiBottomNav(
                selectedTab = 1,
                currentLanguage = currentLanguage,
                onNavigateHome = onNavigateHome,
                onNavigateSchemes = onNavigateSchemes,
                onNavigateApplications = onNavigateApplications,
                onNavigateDocuments = onNavigateDocuments,
                onNavigateProfile = onNavigateProfile
            )
        },
        topBar = {
            Column {
                // National Banner
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
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.weight(1f)
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
                                    text = strings.allSchemesLabel,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GovNavyPrimary,
                                        fontSize = (16 * fontSizeMultiplier).sp
                                    )
                                )
                                Text(
                                    text = strings.appSubtitle,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF64748B),
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        // Filter Trigger
                        IconButton(onClick = { showFilterSheet = true }) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = strings.filterAction,
                                tint = GovNavyPrimary
                            )
                        }
                    }
                }

                // 4 Mandatory Tabs: All, Central, State, UT
                PrimaryTabRow(
                    selectedTabIndex = selectedTab.ordinal,
                    containerColor = Color.White,
                    contentColor = GovNavyPrimary,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    GovLevelTab.entries.forEach { tab ->
                        val isSelected = selectedTab == tab
                        val count = when (tab) {
                            GovLevelTab.ALL -> totalCount
                            GovLevelTab.CENTRAL -> centralCount
                            GovLevelTab.STATE -> stateCount
                            GovLevelTab.UT -> utCount
                        }

                        Tab(
                            selected = isSelected,
                            onClick = { selectedTab = tab },
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = tab.getTitle(strings),
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = (11.5f * fontSizeMultiplier).sp,
                                        maxLines = 1
                                    )
                                    Surface(
                                        color = if (isSelected) GovNavyPrimary else Color(0xFFF1F5F9),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text(
                                            text = "$count",
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = if (isSelected) Color.White else Color(0xFF64748B),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 9.sp
                                            )
                                        )
                                    }
                                }
                            }
                        )
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
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // 1. Accessibility Controls
            item {
                AccessibilityBar(
                    currentLanguage = currentLanguage,
                    onSelectLanguage = onSelectLanguage,
                    isLargeFont = isLargeFont,
                    onFontScaleToggle = onFontScaleToggle
                )
            }

            // 2. Demonstration Banner (Explicit Prototype Disclaimer)
            item {
                Surface(
                    color = Color(0xFFFFFBEB),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFFB45309),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI)
                                "📌 यूआई प्रदर्शन: यह स्क्रीन प्रोटोटाइप मूल्यांकन हेतु यथार्थवादी स्थानीय मॉक डेटा दर्शाती है। यह लाइव आधिकारिक डेटा नहीं है।"
                            else
                                "📌 UI Demonstration: Uses realistic local mock data for prototype evaluation only. Not live official government records.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF92400E),
                                fontSize = 11.sp,
                                lineHeight = 14.sp
                            )
                        )
                    }
                }
            }

            // 3. Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI)
                                "योजना का नाम, मंत्रालय, राज्य या लाभ खोजें..."
                            else
                                "Search by scheme name, ministry, state, benefit...",
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

            // 4. Quick Target Group Filter Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val targetOptions = listOf("All", "Farmers", "Women", "Youth", "Senior Citizens")

                    items(targetOptions) { target ->
                        val isSelected = selectedTargetGroup == target
                        val label = when (target) {
                            "All" -> if (currentLanguage == AppLanguage.HINDI) "सभी लाभार्थी" else "All Citizens"
                            "Farmers" -> if (currentLanguage == AppLanguage.HINDI) "🌾 किसान" else "🌾 Farmers"
                            "Women" -> if (currentLanguage == AppLanguage.HINDI) "👩 महिला" else "👩 Women"
                            "Youth" -> if (currentLanguage == AppLanguage.HINDI) "🎓 युवा व छात्र" else "🎓 Youth"
                            "Senior Citizens" -> if (currentLanguage == AppLanguage.HINDI) "👵 वरिष्ठ नागरिक" else "👵 Seniors"
                            else -> target
                        }

                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedTargetGroup = target },
                            label = {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GovNavyPrimary,
                                selectedLabelColor = Color.White,
                                containerColor = Color.White,
                                labelColor = GovNavyPrimary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) GovNavyPrimary else Color(0xFFCBD5E1)
                            )
                        )
                    }
                }
            }

            // 5. Results & Sort Summary Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${filteredSchemes.size} " + (if (currentLanguage == AppLanguage.HINDI) "योजनाएं उपलब्ध" else "Schemes Displayed"),
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = GovNavyPrimary,
                            fontSize = 13.sp
                        )
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (searchQuery.isNotEmpty() || selectedCategoryFilter != "All Categories" || selectedTargetGroup != "All") {
                            TextButton(onClick = {
                                searchQuery = ""
                                selectedCategoryFilter = "All Categories"
                                selectedTargetGroup = "All"
                                onlyDbtFilter = false
                            }) {
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) "फ़िल्टर हटाएं" else "Reset",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = GovSaffron,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }

                        var sortMenuExpanded by remember { mutableStateOf(false) }

                        Box {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color.White,
                                border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                                modifier = Modifier.clickable { sortMenuExpanded = true }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SwapVert,
                                        contentDescription = null,
                                        tint = GovNavyPrimary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = if (currentLanguage == AppLanguage.HINDI) selectedSortOption.displayNameHi else selectedSortOption.displayNameEn,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = GovNavyPrimary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = sortMenuExpanded,
                                onDismissRequest = { sortMenuExpanded = false }
                            ) {
                                SchemeSortOption.entries.forEach { option ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = if (currentLanguage == AppLanguage.HINDI) option.displayNameHi else option.displayNameEn,
                                                fontWeight = if (selectedSortOption == option) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        onClick = {
                                            selectedSortOption = option
                                            sortMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 6. Empty State
            if (filteredSchemes.isEmpty()) {
                item {
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
                                modifier = Modifier.size(44.dp)
                            )
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) "कोई योजना नहीं मिली" else "No matching schemes found",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF475569)
                                )
                            )
                            Button(
                                onClick = {
                                    searchQuery = ""
                                    selectedTab = GovLevelTab.ALL
                                    selectedCategoryFilter = "All Categories"
                                    selectedTargetGroup = "All"
                                    onlyDbtFilter = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = GovNavyPrimary),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(if (currentLanguage == AppLanguage.HINDI) "सभी फ़िल्टर साफ़ करें" else "Clear All Filters")
                            }
                        }
                    }
                }
            } else {
                // 7. Master Scheme Cards
                items(filteredSchemes, key = { it.id }) { scheme ->
                    val isBookmarked = bookmarkedSchemeIds.contains(scheme.id)

                    MasterSchemeCardItem(
                        scheme = scheme,
                        currentLanguage = currentLanguage,
                        fontSizeMultiplier = fontSizeMultiplier,
                        isBookmarked = isBookmarked,
                        onToggleBookmark = {
                            bookmarkedSchemeIds = if (isBookmarked) {
                                bookmarkedSchemeIds - scheme.id
                            } else {
                                bookmarkedSchemeIds + scheme.id
                            }
                        },
                        onOpenDetails = { selectedSchemeForDetails = scheme }
                    )
                }
            }
        }
    }

    // -------------------------------------------------------------
    // SCHEME DETAIL MODAL SHEET
    // -------------------------------------------------------------
    selectedSchemeForDetails?.let { scheme ->
        Dialog(onDismissRequest = { selectedSchemeForDetails = null }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.90f)
                    .padding(vertical = 10.dp),
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
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                color = scheme.categoryColor.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = scheme.categoryIcon,
                                        contentDescription = null,
                                        tint = scheme.categoryColor,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            Column {
                                Text(
                                    text = scheme.getName(currentLanguage),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GovNavyPrimary,
                                        fontSize = 15.sp
                                    )
                                )
                                Text(
                                    text = "${scheme.stateOrUtName} • ${scheme.getMinistry(currentLanguage)}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF64748B),
                                        fontSize = 11.sp
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
                                contentDescription = "Close",
                                tint = Color(0xFF64748B)
                            )
                        }
                    }

                    HorizontalDivider(color = Color(0xFFE2E8F0), modifier = Modifier.padding(vertical = 10.dp))

                    // Scrollable Info
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Benefit Box
                        Surface(
                            color = Color(0xFFF0FDF4),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) "मुख्य योजना लाभ:" else "Scheme Benefits:",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFF15803D),
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = scheme.getBenefit(currentLanguage),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color(0xFF14532D),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                )
                            }
                        }

                        // Overview
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI) "योजना का उद्देश्य (Overview):" else "Scheme Overview:",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = GovNavyPrimary
                            )
                        )
                        Text(
                            text = scheme.getOverview(currentLanguage),
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF334155), lineHeight = 17.sp)
                        )

                        // Eligibility
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI) "पात्रता मानदंड (Eligibility Criteria):" else "Eligibility Criteria:",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = GovNavyPrimary
                            )
                        )
                        Surface(
                            color = scheme.eligibilityStatus.bgColor,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) scheme.eligibilityStatus.labelHi else scheme.eligibilityStatus.labelEn,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = scheme.eligibilityStatus.color,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = scheme.getEligibility(currentLanguage),
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF1E293B))
                                )
                            }
                        }

                        // Documents
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI) "आवश्यक दस्तावेज़:" else "Required Documents:",
                            style = MaterialTheme.typography.titleSmall.copy(
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
                                    tint = GovNavyPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(text = doc, style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF334155)))
                            }
                        }
                    }

                    HorizontalDivider(color = Color(0xFFE2E8F0), modifier = Modifier.padding(vertical = 10.dp))

                    // Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { selectedSchemeForDetails = null },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(strings.closeButton)
                        }

                        Button(
                            onClick = { selectedSchemeForDetails = null },
                            colors = ButtonDefaults.buttonColors(containerColor = GovNavyPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1.5f)
                        ) {
                            Text(if (currentLanguage == AppLanguage.HINDI) "आवेदन पोर्टल (Portal) ↗" else "Official Portal ↗")
                        }
                    }
                }
            }
        }
    }

    // -------------------------------------------------------------
    // EXHAUSTIVE FILTER DIALOG
    // -------------------------------------------------------------
    if (showFilterSheet) {
        Dialog(onDismissRequest = { showFilterSheet = false }) {
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
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI) "फ़िल्टर विकल्प" else "Directory Filters",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = GovNavyPrimary
                            )
                        )
                        IconButton(onClick = { showFilterSheet = false }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    HorizontalDivider(color = Color(0xFFE2E8F0))

                    // DBT Only Toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onlyDbtFilter = !onlyDbtFilter }
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) "केवल डीबीटी सक्रिय योजनाएं" else "Direct Benefit Transfer (DBT) Only",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                            )
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) "सीधे बैंक खाते में नकद लाभ" else "Direct cash transfer into citizen bank account",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF64748B), fontSize = 11.sp)
                            )
                        }
                        Switch(checked = onlyDbtFilter, onCheckedChange = { onlyDbtFilter = it })
                    }

                    // Bottom Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                searchQuery = ""
                                selectedCategoryFilter = "All Categories"
                                selectedTargetGroup = "All"
                                onlyDbtFilter = false
                                showFilterSheet = false
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(if (currentLanguage == AppLanguage.HINDI) "रीसेट" else "Reset All")
                        }

                        Button(
                            onClick = { showFilterSheet = false },
                            colors = ButtonDefaults.buttonColors(containerColor = GovNavyPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1.4f)
                        ) {
                            Text(if (currentLanguage == AppLanguage.HINDI) "लागू करें" else "Apply")
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// MASTER SCHEME CARD COMPONENT (FULFILLING ALL SPECIFICATIONS)
// -------------------------------------------------------------

/**
 * Each scheme card shows:
 * - Scheme name
 * - Government level (Central / State / UT)
 * - State/UT
 * - Ministry/Department
 * - Category
 * - Short benefit
 * - Eligibility indicator
 * - Save icon
 */
@Composable
fun MasterSchemeCardItem(
    scheme: MasterGovernmentScheme,
    currentLanguage: AppLanguage,
    fontSizeMultiplier: Float,
    isBookmarked: Boolean,
    onToggleBookmark: () -> Unit,
    onOpenDetails: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = org.jansaarthi.app.ui.localization.getJanSaarthiStrings(currentLanguage)

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onOpenDetails() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 1. TOP ROW: Category Icon & Name, Government Level Badge, State/UT, and Save Icon
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
                    // Category Icon
                    Surface(
                        color = scheme.categoryColor.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.size(34.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = scheme.categoryIcon,
                                contentDescription = null,
                                tint = scheme.categoryColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Column {
                        // Level Badge + State/UT
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // Government Level Badge
                            val levelBadgeBg = when (scheme.level) {
                                GovLevelTab.CENTRAL -> Color(0xFFEFF6FF)
                                GovLevelTab.STATE -> Color(0xFFFEF3C7)
                                GovLevelTab.UT -> Color(0xFFF3E8FF)
                                else -> Color(0xFFF1F5F9)
                            }
                            val levelBadgeColor = when (scheme.level) {
                                GovLevelTab.CENTRAL -> GovNavyPrimary
                                GovLevelTab.STATE -> Color(0xFF92400E)
                                GovLevelTab.UT -> Color(0xFF6B21A8)
                                else -> Color(0xFF475569)
                            }
                            val levelBadgeText = when (scheme.level) {
                                GovLevelTab.CENTRAL -> "🇮🇳 ${strings.centralGovernmentLabel}"
                                GovLevelTab.STATE -> "🏛️ ${strings.stateGovernmentLabel}"
                                GovLevelTab.UT -> "🏙️ ${strings.unionTerritoryLabel}"
                                else -> ""
                            }

                            Surface(
                                color = levelBadgeBg,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = levelBadgeText,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = levelBadgeColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp
                                    )
                                )
                            }

                            // State / UT Name
                            Surface(
                                color = Color(0xFFF8FAFC),
                                shape = RoundedCornerShape(4.dp),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                            ) {
                                Text(
                                    text = scheme.stateOrUtName,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFF475569),
                                        fontSize = 9.sp
                                    )
                                )
                            }
                        }

                        // Category Name
                        Text(
                            text = scheme.categoryName,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF64748B),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }

                // Save Icon Button (Bookmark)
                IconButton(onClick = onToggleBookmark, modifier = Modifier.size(34.dp)) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = strings.saveScheme,
                        tint = if (isBookmarked) GovSaffron else Color(0xFF64748B),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // 2. SCHEME NAME
            Text(
                text = scheme.getName(currentLanguage),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = GovNavyPrimary,
                    fontSize = (15 * fontSizeMultiplier).sp,
                    lineHeight = 19.sp
                )
            )

            // 3. MINISTRY / DEPARTMENT
            Text(
                text = scheme.getMinistry(currentLanguage),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF64748B),
                    fontSize = (11 * fontSizeMultiplier).sp
                )
            )

            // 4. SHORT BENEFIT HIGHLIGHT
            Surface(
                color = Color(0xFFF0FDF4),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
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
                        text = scheme.getBenefit(currentLanguage),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF14532D),
                            fontWeight = FontWeight.Bold,
                            fontSize = (11.5f * fontSizeMultiplier).sp,
                            lineHeight = 15.sp
                        )
                    )
                }
            }

            // 5. ELIGIBILITY INDICATOR & TAGS
            Surface(
                color = scheme.eligibilityStatus.bgColor,
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = scheme.eligibilityStatus.getLabel(strings),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = scheme.eligibilityStatus.color,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )

                    Text(
                        text = "• ${scheme.getEligibility(currentLanguage)}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF334155),
                            fontSize = 10.5.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // 6. BOTTOM ACTION ROW
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (scheme.isDbtEnabled) {
                    Surface(
                        color = Color(0xFFDCFCE7),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "⚡ ${strings.dbtActiveLabel}",
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF15803D),
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            )
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(4.dp))
                }

                TextButton(
                    onClick = onOpenDetails,
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${strings.viewDetails} →",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = GovNavyPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}
