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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import org.jansaarthi.app.ui.localization.getJanSaarthiStrings
import org.jansaarthi.app.ui.theme.*

/**
 * ----------------------------------------------------------------------------
 * JanSaarthi Reusable Government Scheme List Screen (Frontend UI Only)
 * ----------------------------------------------------------------------------
 * Opened when a category is tapped, or navigated to directly.
 *
 * Mandated Features:
 * - Category Title (with dynamic category switching / reset)
 * - Accessible Live Search Bar
 * - Central / State / UT Filter
 * - State Filter (Nationwide, Maharashtra, UP, TN, Bihar, Karnataka, Delhi, etc.)
 * - Ministry Filter (All Ministries + 10 Major Central & State Ministries)
 * - Gender Filter (All, Female Only, Male Only, Transgender)
 * - Age Bracket Filter (All, Children 0-17, Youth 18-35, Adults 36-59, Senior Citizens 60+)
 * - Social Category Filter (All, General, OBC, SC, ST, EWS)
 * - Category Domain Filter (All 15 official myScheme broad domains)
 * - Benefit Type Filter (All, DBT Cash, Subsidy, Loan, Health/Insurance, Scholarship, In-Kind)
 * - Eligibility Filter (All, DBT Linked, Low Income < ₹2.5L, Students, Farmers)
 * - Sort System (Most Popular, Highest Benefit, Newly Launched, Alphabetical A-Z)
 * - Rich Scheme Cards with benefit badges, eligibility summaries, save/share actions
 * - Interactive Scheme Detail Modal with application guidelines & document requirements
 * - Prominent UI Demonstration Banner (explicit disclaimer regarding realistic mock data)
 *
 * Strictly Frontend UI: No API, Firebase, or database.
 */

// -------------------------------------------------------------
// DATA MODELS (FRONTEND UI DEMONSTRATION)
// -------------------------------------------------------------

enum class SchemeJurisdiction {
    ALL,
    CENTRAL,
    STATE,
    UT
}

enum class BenefitType(val displayNameEn: String, val displayNameHi: String) {
    ALL("All Benefits", "सभी लाभ"),
    DBT_CASH("DBT Cash Transfer", "डीबीटी नकद हस्तांतरण"),
    SUBSIDY("Subsidy & Grant", "सब्सिडी एवं अनुदान"),
    LOAN_CREDIT("Loan & Credit Guarantee", "ऋण एवं क्रेडिट गारंटी"),
    HEALTH_INSURANCE("Health & Insurance", "स्वास्थ्य एवं बीमा"),
    SCHOLARSHIP_TRAINING("Scholarship & Training", "छात्रवृत्ति एवं कौशल"),
    IN_KIND("In-Kind & Utilities", "वस्तुगत सहायता एवं सुविधाएं")
}

enum class TargetGender(val displayNameEn: String, val displayNameHi: String) {
    ALL("All Genders", "सभी नागरिक"),
    FEMALE("Female Only", "केवल महिलाएं"),
    MALE("Male Only", "केवल पुरुष"),
    TRANSGENDER("Transgender", "ट्रांसजेंडर")
}

enum class AgeBracket(val displayNameEn: String, val displayNameHi: String) {
    ALL("All Ages", "सभी आयु वर्ग"),
    CHILDREN("Children (0-17 yrs)", "बच्चे (0-17 वर्ष)"),
    YOUTH("Youth (18-35 yrs)", "युवा (18-35 वर्ष)"),
    ADULTS("Working Adults (36-59 yrs)", "वयस्क (36-59 वर्ष)"),
    SENIORS("Senior Citizens (60+ yrs)", "वरिष्ठ नागरिक (60+ वर्ष)")
}

enum class SchemeSortOption(val displayNameEn: String, val displayNameHi: String) {
    POPULARITY("Most Popular", "सर्वाधिक लोकप्रिय"),
    HIGHEST_BENEFIT("Highest Financial Benefit", "अधिकतम वित्तीय लाभ"),
    NEWLY_LAUNCHED("Newly Launched", "हाल ही में शुरू"),
    ALPHABETICAL("Alphabetical (A to Z)", "वर्णमाला अनुसार (A-Z)")
}

data class SchemeItem(
    val id: String,
    val titleEn: String,
    val titleHi: String,
    val categoryId: String,
    val categoryNameEn: String,
    val categoryNameHi: String,
    val jurisdiction: SchemeJurisdiction,
    val stateCode: String? = null,
    val stateNameEn: String? = null,
    val ministryEn: String,
    val ministryHi: String,
    val benefitType: BenefitType,
    val financialBenefitEn: String,
    val financialBenefitHi: String,
    val benefitAmountValue: Long, // For sorting by benefit value
    val targetGender: TargetGender,
    val targetAgeBracket: AgeBracket,
    val socialCategory: String = "All",
    val isDbtLinked: Boolean = true,
    val requiresLowIncome: Boolean = false,
    val eligibilitySummaryEn: String,
    val eligibilitySummaryHi: String,
    val overviewEn: String,
    val overviewHi: String,
    val detailedBenefits: List<String>,
    val requiredDocuments: List<String>,
    val applicationSteps: List<String>,
    val officialPortalUrl: String,
    val popularityScore: Int,
    val launchYear: Int,
    val icon: ImageVector
) {
    fun getTitle(lang: AppLanguage): String = if (lang == AppLanguage.HINDI) titleHi else titleEn
    fun getCategoryName(lang: AppLanguage): String = if (lang == AppLanguage.HINDI) categoryNameHi else categoryNameEn
    fun getMinistry(lang: AppLanguage): String = if (lang == AppLanguage.HINDI) ministryHi else ministryEn
    fun getFinancialBenefit(lang: AppLanguage): String = if (lang == AppLanguage.HINDI) financialBenefitHi else financialBenefitEn
    fun getEligibilitySummary(lang: AppLanguage): String = if (lang == AppLanguage.HINDI) eligibilitySummaryHi else eligibilitySummaryEn
    fun getOverview(lang: AppLanguage): String = if (lang == AppLanguage.HINDI) overviewHi else overviewEn
}

// -------------------------------------------------------------
// COMPREHENSIVE REALISTIC MOCK DATASET (FOR UI DEMO ONLY)
// -------------------------------------------------------------

val REALISTIC_MOCK_SCHEMES: List<SchemeItem> = listOf(
    SchemeItem(
        id = "SCH-AGR-01",
        titleEn = "PM Kisan Samman Nidhi (PM-KISAN)",
        titleHi = "प्रधानमंत्री किसान सम्मान निधि (पीएम-किसान)",
        categoryId = "CAT-01",
        categoryNameEn = "Agriculture, Rural & Environment",
        categoryNameHi = "कृषि, ग्रामीण एवं पर्यावरण",
        jurisdiction = SchemeJurisdiction.CENTRAL,
        stateCode = null,
        stateNameEn = "All India (Nationwide)",
        ministryEn = "Ministry of Agriculture & Farmers Welfare",
        ministryHi = "कृषि एवं किसान कल्याण मंत्रालय",
        benefitType = BenefitType.DBT_CASH,
        financialBenefitEn = "₹6,000 per year via direct DBT in 3 equal installments of ₹2,000",
        financialBenefitHi = "₹6,000 प्रति वर्ष 3 समान किस्तों (₹2,000) में सीधे बैंक खाते में",
        benefitAmountValue = 6000,
        targetGender = TargetGender.ALL,
        targetAgeBracket = AgeBracket.ADULTS,
        socialCategory = "All",
        isDbtLinked = true,
        requiresLowIncome = false,
        eligibilitySummaryEn = "All landholder farmer families with cultivable land in their names. Institutional landholders excluded.",
        eligibilitySummaryHi = "खेती योग्य भूमि वाले सभी किसान परिवार। संस्थागत भूमि धारक पात्र नहीं हैं।",
        overviewEn = "PM-KISAN is a Central Sector scheme with 100% funding from Government of India to provide income support to all landholding farmer families across the country.",
        overviewHi = "पीएम-किसान भारत सरकार की 100% वित्तपोषित योजना है जो देश के सभी भूमिधारक किसान परिवारों को आय सहायता प्रदान करती है।",
        detailedBenefits = listOf(
            "₹6,000 annual direct cash transfer via Aadhaar-enabled DBT.",
            "Released in 3 four-monthly cycles (April-July, August-November, December-March).",
            "Zero middleman intervention with direct PFMS bank disbursal."
        ),
        requiredDocuments = listOf(
            "Aadhaar Card with mobile number linked",
            "Proof of agricultural landownership (Khatoni/7-12 extract)",
            "Active Bank Account Passbook seeded with NPCI/DBT",
            "Citizenship Identity Proof"
        ),
        applicationSteps = listOf(
            "Visit the official PM-KISAN portal (pmkisan.gov.in) or nearby CSC.",
            "Click on 'New Farmer Registration' and enter your Aadhaar and State.",
            "Fill in land survey details and bank account numbers.",
            "Submit e-KYC using OTP or biometric verification at CSC."
        ),
        officialPortalUrl = "https://pmkisan.gov.in",
        popularityScore = 98,
        launchYear = 2019,
        icon = Icons.Default.Agriculture
    ),
    SchemeItem(
        id = "SCH-AGR-02",
        titleEn = "PM Fasal Bima Yojana (PMFBY)",
        titleHi = "प्रधानमंत्री फसल बीमा योजना (पीएमएफबीवाई)",
        categoryId = "CAT-01",
        categoryNameEn = "Agriculture, Rural & Environment",
        categoryNameHi = "कृषि, ग्रामीण एवं पर्यावरण",
        jurisdiction = SchemeJurisdiction.CENTRAL,
        stateCode = null,
        stateNameEn = "All India (Nationwide)",
        ministryEn = "Ministry of Agriculture & Farmers Welfare",
        ministryHi = "कृषि एवं किसान कल्याण मंत्रालय",
        benefitType = BenefitType.SUBSIDY,
        financialBenefitEn = "Comprehensive crop loss insurance with subsidized farmer premium (1.5% to 2%)",
        financialBenefitHi = "व्यापक फसल बीमा सुरक्षा, केवल 1.5% से 2% रियायती किसान प्रीमियम पर",
        benefitAmountValue = 25000,
        targetGender = TargetGender.ALL,
        targetAgeBracket = AgeBracket.ADULTS,
        socialCategory = "All",
        isDbtLinked = true,
        requiresLowIncome = false,
        eligibilitySummaryEn = "All farmers growing notified crops in notified areas including sharecroppers and tenant farmers.",
        eligibilitySummaryHi = "अधिसूचित क्षेत्रों में अधिसूचित फसलें उगाने वाले सभी किसान एवं बटाईदार।",
        overviewEn = "Provides financial support to farmers suffering crop loss or damage arising out of unforeseen non-preventable natural events like floods, drought, pest attacks.",
        overviewHi = "सूखा, बाढ़, ओलावृष्टि एवं कीट हमलों जैसी अप्रत्याशित प्राकृतिक आपदाओं से फसल नुकसान पर किसानों को आर्थिक सुरक्षा।",
        detailedBenefits = listOf(
            "Low premium: 2% for Kharif, 1.5% for Rabi, 5% for commercial/horticultural crops.",
            "Full insured sum claim settlement directly into bank account.",
            "Covers prevented sowing, mid-season adversity, and localized post-harvest losses."
        ),
        requiredDocuments = listOf(
            "Land ownership record or tenancy agreement",
            "Crop Sowing Certificate issued by Patwari/Village Officer",
            "Aadhaar Card & Bank Passbook"
        ),
        applicationSteps = listOf(
            "Apply via National Crop Insurance Portal (pmfby.gov.in) before cutoff date.",
            "Select State, Season, Year, and Crop.",
            "Pay subsidized premium online or through Primary Agricultural Credit Society (PACS)."
        ),
        officialPortalUrl = "https://pmfby.gov.in",
        popularityScore = 90,
        launchYear = 2016,
        icon = Icons.Default.Grass
    ),
    SchemeItem(
        id = "SCH-AGR-03",
        titleEn = "Namo Shetkari Mahasanman Nidhi Yojana",
        titleHi = "नमो शेतकरी महासन्मान निधी योजना",
        categoryId = "CAT-01",
        categoryNameEn = "Agriculture, Rural & Environment",
        categoryNameHi = "कृषि, ग्रामीण एवं पर्यावरण",
        jurisdiction = SchemeJurisdiction.STATE,
        stateCode = "MH",
        stateNameEn = "Maharashtra",
        ministryEn = "Department of Agriculture (Govt of Maharashtra)",
        ministryHi = "कृषि विभाग (महाराष्ट्र शासन)",
        benefitType = BenefitType.DBT_CASH,
        financialBenefitEn = "₹6,000 per year supplementary state grant (Total ₹12,000/yr with PM-KISAN)",
        financialBenefitHi = "₹6,000 प्रति वर्ष अतिरिक्त राज्य अनुदान (पीएम-किसान संग कुल ₹12,000/वर्ष)",
        benefitAmountValue = 6000,
        targetGender = TargetGender.ALL,
        targetAgeBracket = AgeBracket.ADULTS,
        socialCategory = "All",
        isDbtLinked = true,
        requiresLowIncome = false,
        eligibilitySummaryEn = "Registered beneficiaries of PM-KISAN residing in the state of Maharashtra.",
        eligibilitySummaryHi = "महाराष्ट्र राज्य के निवासी एवं पीएम-किसान योजना के पंजीकृत पात्र लाभार्थी।",
        overviewEn = "Maharashtra State Government top-up assistance matching PM-KISAN, granting an additional ₹6,000 annually to boost rural agricultural income.",
        overviewHi = "महाराष्ट्र सरकार द्वारा पीएम-किसान के बराबर अतिरिक्त ₹6,000 वार्षिक सहायता, कुल मिलाकर ₹12,000 की सुनिश्चित आर्थिक सुरक्षा।",
        detailedBenefits = listOf(
            "₹2,000 credited three times a year directly to Aadhaar-linked accounts.",
            "Seamless auto-enrollment for existing verified PM-KISAN beneficiaries in Maharashtra."
        ),
        requiredDocuments = listOf(
            "Maharashtra Resident Domicile Proof",
            "PM-KISAN Beneficiary ID & Aadhaar",
            "7/12 Land Record of Maharashtra"
        ),
        applicationSteps = listOf(
            "Existing PM-KISAN beneficiaries in Maharashtra are automatically verified via MahaDBT.",
            "Check status on MahaDBT portal using Aadhaar number."
        ),
        officialPortalUrl = "https://mahadbt.maharashtra.gov.in",
        popularityScore = 88,
        launchYear = 2023,
        icon = Icons.Default.Agriculture
    ),
    SchemeItem(
        id = "SCH-BNK-01",
        titleEn = "Pradhan Mantri Jan Dhan Yojana (PMJDY)",
        titleHi = "प्रधानमंत्री जन धन योजना (पीएमजेडीवाई)",
        categoryId = "CAT-02",
        categoryNameEn = "Banking, Financial Services & Insurance",
        categoryNameHi = "बैंकिंग, वित्तीय सेवाएं एवं बीमा",
        jurisdiction = SchemeJurisdiction.CENTRAL,
        stateCode = null,
        stateNameEn = "All India (Nationwide)",
        ministryEn = "Ministry of Finance (Department of Financial Services)",
        ministryHi = "वित्त मंत्रालय (वित्तीय सेवाएं विभाग)",
        benefitType = BenefitType.DBT_CASH,
        financialBenefitEn = "Zero-balance savings account + ₹2 Lakh RuPay accidental cover + ₹10,000 overdraft",
        financialBenefitHi = "जीरो-बैलेंस बैंक खाता + ₹2 लाख दुर्घटना बीमा + ₹10,000 ओवरड्राफ्ट सुविधा",
        benefitAmountValue = 200000,
        targetGender = TargetGender.ALL,
        targetAgeBracket = AgeBracket.ALL,
        socialCategory = "All",
        isDbtLinked = true,
        requiresLowIncome = false,
        eligibilitySummaryEn = "Any Indian citizen aged 10 years and above not holding any other formal savings bank account.",
        eligibilitySummaryHi = "10 वर्ष या अधिक आयु का कोई भी भारतीय नागरिक जिसका पहले कोई बचत बैंक खाता न हो।",
        overviewEn = "National Mission for Financial Inclusion to ensure access to financial services, namely, basic savings & deposit accounts, remittance, credit, insurance, pension in an affordable manner.",
        overviewHi = "सुलभ एवं किफायती रूप से बैंकिंग, बचत, प्रेषण, ऋण, बीमा एवं पेंशन सेवाएं उपलब्ध कराने का राष्ट्रीय वित्तीय समावेशन मिशन।",
        detailedBenefits = listOf(
            "No minimum balance requirement in the account.",
            "Free RuPay Debit Card with inbuilt ₹2,00,000 accidental insurance cover.",
            "Eligible for Direct Benefit Transfer (DBT) under all Central and State schemes."
        ),
        requiredDocuments = listOf(
            "Aadhaar Card or Voter ID / Passport / NREGA Card",
            "Two recent passport size photographs"
        ),
        applicationSteps = listOf(
            "Visit any commercial bank branch or Bank Mitra / CSP center.",
            "Fill the simple one-page Jan Dhan account opening form.",
            "Submit KYC document; account is opened instantly with zero initial deposit."
        ),
        officialPortalUrl = "https://pmjdy.gov.in",
        popularityScore = 96,
        launchYear = 2014,
        icon = Icons.Default.AccountBalance
    ),
    SchemeItem(
        id = "SCH-BNK-02",
        titleEn = "Atal Pension Yojana (APY)",
        titleHi = "अटल पेंशन योजना (एपीवाई)",
        categoryId = "CAT-02",
        categoryNameEn = "Banking, Financial Services & Insurance",
        categoryNameHi = "बैंकिंग, वित्तीय सेवाएं एवं बीमा",
        jurisdiction = SchemeJurisdiction.CENTRAL,
        stateCode = null,
        stateNameEn = "All India (Nationwide)",
        ministryEn = "Ministry of Finance (PFRDA)",
        ministryHi = "वित्त मंत्रालय (पीएफआरडीए)",
        benefitType = BenefitType.DBT_CASH,
        financialBenefitEn = "Guaranteed monthly pension of ₹1,000 to ₹5,000 for life from age 60",
        financialBenefitHi = "60 वर्ष की आयु से जीवनभर ₹1,000 से ₹5,000 की गारंटीड मासिक पेंशन",
        benefitAmountValue = 60000,
        targetGender = TargetGender.ALL,
        targetAgeBracket = AgeBracket.YOUTH,
        socialCategory = "All",
        isDbtLinked = true,
        requiresLowIncome = false,
        eligibilitySummaryEn = "Indian citizens aged 18 to 40 years having a savings bank account. Must not be an income taxpayer.",
        eligibilitySummaryHi = "18 से 40 वर्ष की आयु के भारतीय नागरिक जिनके पास बैंक खाता हो। आयकर दाता पात्र नहीं हैं।",
        overviewEn = "A periodic contribution pension scheme focused on the unorganized sector workers, guaranteeing a minimum monthly pension of ₹1,000 to ₹5,000 after 60 years.",
        overviewHi = "असंगठित क्षेत्र के श्रमिकों के लिए वृद्धावस्था सामाजिक सुरक्षा, 60 वर्ष पूर्ण होने पर न्यूनतम ₹1,000 से ₹5,000 तक की गारंटीड मासिक पेंशन।",
        detailedBenefits = listOf(
            "Government-guaranteed minimum monthly pension for the subscriber.",
            "Same pension payable to spouse upon subscriber's demise.",
            "Full accumulated pension wealth returned to nominee after both."
        ),
        requiredDocuments = listOf(
            "Savings Bank / Post Office Account details",
            "Aadhaar Number & Registered Mobile Number"
        ),
        applicationSteps = listOf(
            "Approach your bank branch or use Internet Banking / UPI APY portal.",
            "Choose monthly pension slab (₹1,000, ₹2,000, ₹3,000, ₹4,000, or ₹5,000).",
            "Set auto-debit authorization for monthly contributions."
        ),
        officialPortalUrl = "https://www.npscra.nsdl.co.in",
        popularityScore = 87,
        launchYear = 2015,
        icon = Icons.Default.Savings
    ),
    SchemeItem(
        id = "SCH-BUS-01",
        titleEn = "Pradhan Mantri Mudra Yojana (PMMY)",
        titleHi = "प्रधानमंत्री मुद्रा योजना (पीएमएमवाई)",
        categoryId = "CAT-03",
        categoryNameEn = "Business & Entrepreneurship",
        categoryNameHi = "व्यवसाय एवं उद्यमिता",
        jurisdiction = SchemeJurisdiction.CENTRAL,
        stateCode = null,
        stateNameEn = "All India (Nationwide)",
        ministryEn = "Ministry of Finance",
        ministryHi = "वित्त मंत्रालय",
        benefitType = BenefitType.LOAN_CREDIT,
        financialBenefitEn = "Collateral-free business loans up to ₹10 Lakhs (Shishu, Kishore, Tarun)",
        financialBenefitHi = "बिना किसी गारंटी के ₹10 लाख तक का व्यवसाय ऋण (शिशु, किशोर, तरुण)",
        benefitAmountValue = 1000000,
        targetGender = TargetGender.ALL,
        targetAgeBracket = AgeBracket.ADULTS,
        socialCategory = "All",
        isDbtLinked = false,
        requiresLowIncome = false,
        eligibilitySummaryEn = "Non-Corporate, Non-Farm Small/Micro enterprises in manufacturing, trading, and service sectors.",
        eligibilitySummaryHi = "विनिर्माण, व्यापार एवं सेवा क्षेत्र में कार्यरत गैर-कॉर्पोरेट, गैर-कृषि लघु एवं सूक्ष्म उद्यमी।",
        overviewEn = "Provides collateral-free loans to micro and small enterprises under three categories: Shishu (up to ₹50,000), Kishore (₹50,001 to ₹5 Lakhs), and Tarun (₹5,00,001 to ₹10 Lakhs).",
        overviewHi = "सूक्ष्म एवं लघु उद्यमियों को व्यवसाय शुरू करने या बढ़ाने के लिए बिना संपत्ति गिरवी रखे तीन श्रेणियों में ऋण प्रदान किया जाता है।",
        detailedBenefits = listOf(
            "Zero collateral or third-party guarantee required.",
            "Reasonable interest rates as per RBI guidelines.",
            "Mudra Card provided for working capital withdrawals at ATMs."
        ),
        requiredDocuments = listOf(
            "Proof of Business Identity / Address & Udyam Registration",
            "Aadhaar Card & PAN Card",
            "Project report / quotation of machinery or inventory to be purchased",
            "Bank statement for the last 6 months"
        ),
        applicationSteps = listOf(
            "Apply online via UdyamiMitra portal (udyamimitra.in) or visit any scheduled commercial bank / NBFC.",
            "Choose loan category (Shishu, Kishore, or Tarun).",
            "Upload project details; bank sanctions loan without collateral."
        ),
        officialPortalUrl = "https://www.mudra.org.in",
        popularityScore = 93,
        launchYear = 2015,
        icon = Icons.Default.BusinessCenter
    ),
    SchemeItem(
        id = "SCH-BUS-02",
        titleEn = "Stand-Up India Scheme for Women and SC/ST",
        titleHi = "स्टैंड-अप इंडिया योजना (महिला एवं अ.जा./अ.ज.जा.)",
        categoryId = "CAT-03",
        categoryNameEn = "Business & Entrepreneurship",
        categoryNameHi = "व्यवसाय एवं उद्यमिता",
        jurisdiction = SchemeJurisdiction.CENTRAL,
        stateCode = null,
        stateNameEn = "All India (Nationwide)",
        ministryEn = "Ministry of Finance",
        ministryHi = "वित्त मंत्रालय",
        benefitType = BenefitType.LOAN_CREDIT,
        financialBenefitEn = "Bank composite loans from ₹10 Lakhs to ₹1 Crore for greenfield enterprises",
        financialBenefitHi = "नए ग्रीनफील्ड उद्यम हेतु ₹10 लाख से ₹1 करोड़ तक का बैंक कम्पोजिट ऋण",
        benefitAmountValue = 10000000,
        targetGender = TargetGender.FEMALE,
        targetAgeBracket = AgeBracket.ADULTS,
        socialCategory = "SC, ST, Women",
        isDbtLinked = false,
        requiresLowIncome = false,
        eligibilitySummaryEn = "SC/ST and/or Woman entrepreneurs above 18 years for setting up greenfield ventures in manufacturing, services, or trading.",
        eligibilitySummaryHi = "18 वर्ष से अधिक आयु की महिला एवं अ.जा./अ.ज.जा. उद्यमी, जो नया ग्रीनफील्ड उद्यम स्थापित कर रहे हों।",
        overviewEn = "Facilitates bank loans between ₹10 Lakhs and ₹1 Crore to at least one SC or ST borrower and at least one woman borrower per bank branch for setting up greenfield enterprises.",
        overviewHi = "प्रत्येक बैंक शाखा द्वारा कम से कम एक महिला और एक अ.जा./अ.ज.जा. उद्यमी को ₹10 लाख से ₹1 करोड़ तक का ऋण सुनिश्चित करना।",
        detailedBenefits = listOf(
            "Covers 85% of project cost (composite loan including term loan and working capital).",
            "Repayment period of up to 7 years with a moratorium of up to 18 months."
        ),
        requiredDocuments = listOf(
            "Proof of SC/ST category (if applicable) and Identity/Address proof",
            "Greenfield Project Proposal & DPR",
            "Udyam registration and Pollution Control NOC (where required)"
        ),
        applicationSteps = listOf(
            "Register on standupmitra.in portal.",
            "Select nearby bank branch and connect with handholding agency.",
            "Submit detailed project report for loan sanction."
        ),
        officialPortalUrl = "https://www.standupmitra.in",
        popularityScore = 84,
        launchYear = 2016,
        icon = Icons.Default.Storefront
    ),
    SchemeItem(
        id = "SCH-EDU-01",
        titleEn = "National Scholarship Portal - Post-Matric Scholarship",
        titleHi = "राष्ट्रीय छात्रवृत्ति पोर्टल - पोस्ट-मैट्रिक छात्रवृत्ति",
        categoryId = "CAT-04",
        categoryNameEn = "Education & Learning",
        categoryNameHi = "शिक्षा एवं शिक्षण",
        jurisdiction = SchemeJurisdiction.CENTRAL,
        stateCode = null,
        stateNameEn = "All India (Nationwide)",
        ministryEn = "Ministry of Social Justice & Empowerment",
        ministryHi = "सामाजिक न्याय एवं अधिकारिता मंत्रालय",
        benefitType = BenefitType.SCHOLARSHIP_TRAINING,
        financialBenefitEn = "100% compulsory non-refundable fees reimbursed + up to ₹13,500/year maintenance allowance",
        financialBenefitHi = "100% अनिवार्य गैर-वापसी योग्य शिक्षण शुल्क प्रतिपूर्ति + ₹13,500/वर्ष तक निर्वाह भत्ता",
        benefitAmountValue = 50000,
        targetGender = TargetGender.ALL,
        targetAgeBracket = AgeBracket.YOUTH,
        socialCategory = "SC, ST, OBC, EWS",
        isDbtLinked = true,
        requiresLowIncome = true,
        eligibilitySummaryEn = "Students studying in Class 11 and above whose annual family income is below ₹2.5 Lakhs.",
        eligibilitySummaryHi = "कक्षा 11 एवं उससे आगे पढ़ने वाले छात्र जिनके परिवार की वार्षिक आय ₹2.5 लाख से कम हो।",
        overviewEn = "Provides financial assistance to eligible students at post-matriculation or post-secondary stage to enable them to complete their higher education without financial hardship.",
        overviewHi = "आर्थिक रूप से कमजोर वर्गों के छात्रों को उच्च शिक्षा पूर्ण करने हेतु शिक्षण शुल्क प्रतिपूर्ति एवं मासिक छात्रवृत्ति सहायता।",
        detailedBenefits = listOf(
            "Full tuition fee, examination fee, and library fees reimbursed directly to institution/student.",
            "Monthly maintenance allowance credited directly via DBT to student's bank account."
        ),
        requiredDocuments = listOf(
            "Caste Certificate & Valid Income Certificate (< ₹2.5 Lakhs)",
            "Previous Year Marksheet (Class 10/12/Diploma)",
            "Fee Receipt of current college / course admission",
            "Aadhaar-seeded Bank Account Passbook"
        ),
        applicationSteps = listOf(
            "Register on scholarships.gov.in with OTR (One Time Registration).",
            "Fill academic and personal details; upload required certificates.",
            "Application verified by School/College Nodal Officer, District Officer, and state."
        ),
        officialPortalUrl = "https://scholarships.gov.in",
        popularityScore = 95,
        launchYear = 2015,
        icon = Icons.Default.School
    ),
    SchemeItem(
        id = "SCH-EDU-02",
        titleEn = "Kanya Utthan Yojana (Higher Education)",
        titleHi = "मुख्यमंत्री कन्या उत्थान योजना (उच्च शिक्षा)",
        categoryId = "CAT-04",
        categoryNameEn = "Education & Learning",
        categoryNameHi = "शिक्षा एवं शिक्षण",
        jurisdiction = SchemeJurisdiction.STATE,
        stateCode = "BR",
        stateNameEn = "Bihar",
        ministryEn = "Education Department (Govt of Bihar)",
        ministryHi = "शिक्षा विभाग (बिहार सरकार)",
        benefitType = BenefitType.SCHOLARSHIP_TRAINING,
        financialBenefitEn = "₹50,000 direct cash grant on completing graduation (Degree) + ₹25,000 for Class 12",
        financialBenefitHi = "स्नातक (ग्रेजुएशन) उत्तीर्ण करने पर ₹50,000 एवं 12वीं उत्तीर्ण पर ₹25,000 नकद प्रोत्साहन",
        benefitAmountValue = 50000,
        targetGender = TargetGender.FEMALE,
        targetAgeBracket = AgeBracket.YOUTH,
        socialCategory = "All",
        isDbtLinked = true,
        requiresLowIncome = false,
        eligibilitySummaryEn = "Unmarried female students who are permanent residents of Bihar passing graduation from recognized universities in Bihar.",
        eligibilitySummaryHi = "बिहार की स्थायी निवासी अविवाहित छात्राएं जिन्होंने बिहार के मान्यता प्राप्त विश्वविद्यालयों से स्नातक उत्तीर्ण किया हो।",
        overviewEn = "Flagship incentive program by Bihar Government to prevent child marriage, reduce female dropout rates, and empower girls to achieve university degrees.",
        overviewHi = "बाल विवाह रोकने, बालिकाओं की शिक्षा को बढ़ावा देने एवं उच्च शिक्षा में बेटियों की भागीदारी बढ़ाने हेतु बिहार सरकार की प्रमुख योजना।",
        detailedBenefits = listOf(
            "One-time ₹50,000 DBT cash reward directly deposited in girl's personal bank account upon graduation.",
            "₹25,000 for intermediate (12th pass) girls."
        ),
        requiredDocuments = listOf(
            "Bihar Permanent Resident Certificate (Domicile)",
            "Graduation Degree Certificate / Final Marksheet",
            "Aadhaar Card & Student's Personal Bank Passbook"
        ),
        applicationSteps = listOf(
            "Apply online via MedhaSoft portal (medhasoft.bih.nic.in).",
            "Enter university registration number, Aadhaar, and bank account.",
            "College verifies academic records; funds disbursed directly by state treasury."
        ),
        officialPortalUrl = "https://medhasoft.bih.nic.in",
        popularityScore = 89,
        launchYear = 2018,
        icon = Icons.Default.AutoStories
    ),
    SchemeItem(
        id = "SCH-HLT-01",
        titleEn = "Ayushman Bharat PM-JAY (Golden Card)",
        titleHi = "आयुष्मान भारत प्रधानमंत्री जन आरोग्य योजना (गोल्डन कार्ड)",
        categoryId = "CAT-05",
        categoryNameEn = "Health & Wellness",
        categoryNameHi = "स्वास्थ्य एवं कल्याण",
        jurisdiction = SchemeJurisdiction.CENTRAL,
        stateCode = null,
        stateNameEn = "All India (Nationwide)",
        ministryEn = "Ministry of Health & Family Welfare (National Health Authority)",
        ministryHi = "स्वास्थ्य एवं परिवार कल्याण मंत्रालय (एनएचए)",
        benefitType = BenefitType.HEALTH_INSURANCE,
        financialBenefitEn = "₹5,00,000 cashless hospital treatment per family per year across India",
        financialBenefitHi = "प्रति परिवार प्रति वर्ष ₹5,00,000 तक का मुफ़्त कैशलेस अस्पताल इलाज",
        benefitAmountValue = 500000,
        targetGender = TargetGender.ALL,
        targetAgeBracket = AgeBracket.ALL,
        socialCategory = "BPL / SECC Identified",
        isDbtLinked = false,
        requiresLowIncome = true,
        eligibilitySummaryEn = "Deprived rural and identified urban occupational families based on SECC 2011 + All senior citizens aged 70+.",
        eligibilitySummaryHi = "एसईसीसी 2011 अनुसार चिन्हित ग्रामीण व शहरी गरीब परिवार + 70 वर्ष से अधिक आयु के सभी वरिष्ठ नागरिक।",
        overviewEn = "The world's largest government-funded health assurance scheme covering over 12 crore poor families (55 crore citizens) providing secondary and tertiary hospitalisation care.",
        overviewHi = "विश्व की सबसे बड़ी सरकारी स्वास्थ्य आश्वासन योजना, 55 करोड़ नागरिकों को 27,000+ अस्पतालों में ₹5 लाख तक का कैशलेस इलाज।",
        detailedBenefits = listOf(
            "Cashless & paperless access to healthcare services at all empanelled public and private hospitals.",
            "Covers 3 days pre-hospitalisation and 15 days post-hospitalisation expenses including medicines and diagnostic tests.",
            "No restriction on family size, age, or gender; pre-existing conditions covered from Day 1."
        ),
        requiredDocuments = listOf(
            "Aadhaar Card or Ration Card (NFSA)",
            "Ayushman Card (generated with instant e-KYC)"
        ),
        applicationSteps = listOf(
            "Check eligibility on beneficiary.nha.gov.in using mobile or Ration card.",
            "Complete face/OTP e-KYC to download instant Ayushman Card.",
            "Show Ayushman Card at hospital Ayushman Mitra desk for cashless admission."
        ),
        officialPortalUrl = "https://pmjay.gov.in",
        popularityScore = 99,
        launchYear = 2018,
        icon = Icons.Default.HealthAndSafety
    ),
    SchemeItem(
        id = "SCH-HLT-02",
        titleEn = "Mahatma Jyotirao Phule Jan Arogya Yojana (MJPJAY)",
        titleHi = "महात्मा ज्योतिराव फुले जन आरोग्य योजना (एमजेपीजेएवाय)",
        categoryId = "CAT-05",
        categoryNameEn = "Health & Wellness",
        categoryNameHi = "स्वास्थ्य एवं कल्याण",
        jurisdiction = SchemeJurisdiction.STATE,
        stateCode = "MH",
        stateNameEn = "Maharashtra",
        ministryEn = "Public Health Department (Govt of Maharashtra)",
        ministryHi = "सार्वजनिक स्वास्थ्य विभाग (महाराष्ट्र शासन)",
        benefitType = BenefitType.HEALTH_INSURANCE,
        financialBenefitEn = "₹5,00,000 universal family medical cover across 1,356 treatments in Maharashtra",
        financialBenefitHi = "महाराष्ट्र में 1,356 उपचारों हेतु प्रति परिवार ₹5,00,000 तक सार्वभौमिक कैशलेस स्वास्थ्य सुरक्षा",
        benefitAmountValue = 500000,
        targetGender = TargetGender.ALL,
        targetAgeBracket = AgeBracket.ALL,
        socialCategory = "All Maharashtra Residents",
        isDbtLinked = false,
        requiresLowIncome = false,
        eligibilitySummaryEn = "All families holding valid ration cards (Yellow, Orange, or White) residing in Maharashtra.",
        eligibilitySummaryHi = "महाराष्ट्र में रहने वाले सभी वैध राशन कार्ड (पीला, केसरी, सफेद) धारक परिवार।",
        overviewEn = "Universal health coverage initiative by Government of Maharashtra offering cashless hospital care for complex surgeries, oncology, cardiology, and emergency care across 1,000+ empanelled hospitals.",
        overviewHi = "महाराष्ट्र सरकार की सार्वभौमिक स्वास्थ्य योजना, गंभीर बीमारियों, सर्जरी एवं कैंसर के इलाज के लिए ₹5 लाख का मुफ़्त इलाज।",
        detailedBenefits = listOf(
            "Covers 1,356 surgical and medical procedures across 34 multi-specialty categories.",
            "Includes diagnostic tests, medications, ICU charges, and post-discharge follow-up."
        ),
        requiredDocuments = listOf(
            "Ration Card issued by Maharashtra Food & Civil Supplies Department",
            "Aadhaar Card of all family members"
        ),
        applicationSteps = listOf(
            "Visit any network hospital in Maharashtra and meet the 'Arogyamitra'.",
            "Present Ration Card and Aadhaar for online pre-authorization.",
            "Receive cashless inpatient treatment without paying any advance deposit."
        ),
        officialPortalUrl = "https://www.jeevandayee.gov.in",
        popularityScore = 91,
        launchYear = 2012,
        icon = Icons.Default.LocalHospital
    ),
    SchemeItem(
        id = "SCH-HOU-01",
        titleEn = "Pradhan Mantri Awas Yojana - Gramin (PMAY-G)",
        titleHi = "प्रधानमंत्री आवास योजना - ग्रामीण (पीएमएवाई-जी)",
        categoryId = "CAT-06",
        categoryNameEn = "Housing & Shelter",
        categoryNameHi = "आवास एवं आश्रय",
        jurisdiction = SchemeJurisdiction.CENTRAL,
        stateCode = null,
        stateNameEn = "All India (Nationwide)",
        ministryEn = "Ministry of Rural Development",
        ministryHi = "ग्रामीण विकास मंत्रालय",
        benefitType = BenefitType.SUBSIDY,
        financialBenefitEn = "₹1,20,000 grant (plains) / ₹1,30,000 (hills) + ₹12,000 for toilet + 90 days MGNREGA wages",
        financialBenefitHi = "मैदानी इलाकों में ₹1,20,000 / पहाड़ी में ₹1,30,000 अनुदान + ₹12,000 शौचालय + 90 दिन मनरेगा मजदूरी",
        benefitAmountValue = 150000,
        targetGender = TargetGender.ALL,
        targetAgeBracket = AgeBracket.ADULTS,
        socialCategory = "Houseless / Kutcha House",
        isDbtLinked = true,
        requiresLowIncome = true,
        eligibilitySummaryEn = "Houseless families or those living in zero, one, or two room kutcha houses with kutcha roof identified via Awas+ list.",
        eligibilitySummaryHi = "बेघर परिवार या कच्चे मकानों में रहने वाले परिवार जो आवास+ प्रतीक्षा सूची में दर्ज हैं।",
        overviewEn = "Assists rural houseless households in building disaster-resilient pucca houses equipped with basic civic amenities like clean cooking gas (Ujjwala), toilet (SBM), and electricity (Saubhagya).",
        overviewHi = "ग्रामीण भारत में बेघर और कच्चे घरों में रहने वाले परिवारों को बुनियादी सुविधाओं से युक्त पक्का मकान बनाने हेतु वित्तीय सहायता।",
        detailedBenefits = listOf(
            "Direct cash installments into bank account linked to geotagged construction milestones (plinth, lintel, roof, completion).",
            "Additional ₹12,000 for individual household latrine under Swachh Bharat Mission.",
            "90 to 95 days of unskilled labour wages under MGNREGA (approx ₹20,000 additional)."
        ),
        requiredDocuments = listOf(
            "Aadhaar Card and Bank Passbook with DBT enable",
            "MGNREGA Job Card Number",
            "Land title or Gram Panchayat allotment certificate",
            "Sworn undertaking of not owning another pucca house"
        ),
        applicationSteps = listOf(
            "Beneficiary selection is based on SECC 2011 and verified by Gram Sabha.",
            "Geo-tagging of existing house and vacant plot by Block Development Officer.",
            "Installments released via FTO (Fund Transfer Order) after each building stage."
        ),
        officialPortalUrl = "https://pmayg.nic.in",
        popularityScore = 97,
        launchYear = 2016,
        icon = Icons.Default.Home
    ),
    SchemeItem(
        id = "SCH-LAW-01",
        titleEn = "Tele-Law Citizen Portal: Free Legal Aid for Citizens",
        titleHi = "टेली-लॉ नागरिक सेवा: मुफ़्त कानूनी सलाह एवं सहायता",
        categoryId = "CAT-07",
        categoryNameEn = "Public Safety, Law & Justice",
        categoryNameHi = "सार्वजनिक सुरक्षा, कानून एवं न्याय",
        jurisdiction = SchemeJurisdiction.CENTRAL,
        stateCode = null,
        stateNameEn = "All India (Nationwide)",
        ministryEn = "Ministry of Law and Justice (Department of Justice)",
        ministryHi = "विधि एवं न्याय मंत्रालय (न्याय विभाग)",
        benefitType = BenefitType.IN_KIND,
        financialBenefitEn = "100% free expert legal advice and advocate consultation via video-call / phone",
        financialBenefitHi = "पैनल अधिवक्ताओं द्वारा वीडियो कॉल / फोन पर 100% मुफ़्त कानूनी सलाह व मार्गदर्शन",
        benefitAmountValue = 15000,
        targetGender = TargetGender.ALL,
        targetAgeBracket = AgeBracket.ADULTS,
        socialCategory = "Women, SC, ST, Low Income",
        isDbtLinked = false,
        requiresLowIncome = false,
        eligibilitySummaryEn = "Free for women, children, SC/ST, victims of disasters/violence, persons with disability, and persons with annual income under ₹3 Lakhs.",
        eligibilitySummaryHi = "महिलाओं, बच्चों, अ.जा./अ.ज.जा., दिव्यांगजनों एवं ₹3 लाख से कम आय वाले नागरिकों के लिए पूर्णतः मुफ़्त।",
        overviewEn = "Connects disadvantaged and needy citizens with panel lawyers through video conferencing and telephone facilities available at 2.5 Lakh Common Service Centres (CSCs).",
        overviewHi = "देश के ग्रामीण नागरिकों को सामान्य सेवा केंद्रों (सीएससी) और मोबाइल ऐप के जरिए वरिष्ठ वकीलों से सीधे जोड़कर मुफ़्त कानूनी परामर्श।",
        detailedBenefits = listOf(
            "Pre-litigation advice on property disputes, domestic violence, matrimonial matters, wages, and family inheritance.",
            "Assistance in drafting petitions and referral to District Legal Services Authority (DLSA) for free courtroom representation."
        ),
        requiredDocuments = listOf(
            "Aadhaar or identity proof",
            "Case-related documents or FIR copy (if available)"
        ),
        applicationSteps = listOf(
            "Download Tele-Law Citizen Mobile App or visit nearby CSC center.",
            "Select language and nature of legal dispute.",
            "Book appointment slot; talk directly with high court/district panel lawyer."
        ),
        officialPortalUrl = "https://www.tele-law.in",
        popularityScore = 80,
        launchYear = 2017,
        icon = Icons.Default.Gavel
    ),
    SchemeItem(
        id = "SCH-SCI-01",
        titleEn = "Pradhan Mantri Gramin Digital Saksharta Abhiyan (PMGDISHA)",
        titleHi = "प्रधानमंत्री ग्रामीण डिजिटल साक्षरता अभियान (पीएमदिशा)",
        categoryId = "CAT-08",
        categoryNameEn = "Science, IT & Communications",
        categoryNameHi = "विज्ञान, आईटी एवं संचार",
        jurisdiction = SchemeJurisdiction.CENTRAL,
        stateCode = null,
        stateNameEn = "All India (Nationwide)",
        ministryEn = "Ministry of Electronics & Information Technology (MeitY)",
        ministryHi = "इलेक्ट्रॉनिक्स एवं सूचना प्रौद्योगिकी मंत्रालय",
        benefitType = BenefitType.SCHOLARSHIP_TRAINING,
        financialBenefitEn = "20-hour free certified digital literacy training + official NIELIT certification",
        financialBenefitHi = "20 घंटे का मुफ़्त प्रमाणित डिजिटल प्रशिक्षण + आधिकारिक नाइलेट सरकारी प्रमाणपत्र",
        benefitAmountValue = 5000,
        targetGender = TargetGender.ALL,
        targetAgeBracket = AgeBracket.ALL,
        socialCategory = "Rural Households",
        isDbtLinked = false,
        requiresLowIncome = false,
        eligibilitySummaryEn = "One digitally illiterate person aged 14 to 60 years per eligible rural household.",
        eligibilitySummaryHi = "प्रत्येक पात्र ग्रामीण परिवार से 14 से 60 वर्ष की आयु का एक गैर-डिजिटल साक्षर नागरिक।",
        overviewEn = "Flagship digital empowerment program to bridge the digital divide in rural India by training 6 crore rural citizens to operate smartphones, computers, UPI, and e-government portals.",
        overviewHi = "ग्रामीण भारत को डिजिटल रूप से सक्षम बनाने हेतु स्मार्टफोन, कंप्यूटर, यूपीआई और ई-गवर्नेंस सेवाओं के संचालन का मुफ़्त प्रशिक्षण।",
        detailedBenefits = listOf(
            "Learn smartphone browsing, email, DigiLocker, BHIM-UPI, and online train/bus booking.",
            "Government certified assessment conducted by NIELIT with recognized digital diploma."
        ),
        requiredDocuments = listOf(
            "Aadhaar Card",
            "Self-declaration of digital illiteracy"
        ),
        applicationSteps = listOf(
            "Locate nearest PMGDISHA training centre at village CSC.",
            "Enroll with Aadhaar fingerprint authentication.",
            "Complete 20 hours hands-on computer module and pass online assessment."
        ),
        officialPortalUrl = "https://www.pmgdisha.in",
        popularityScore = 86,
        launchYear = 2017,
        icon = Icons.Default.Devices
    ),
    SchemeItem(
        id = "SCH-SKL-01",
        titleEn = "PM Vishwakarma Scheme (Artisans & Craftsmen)",
        titleHi = "पीएम विश्वकर्मा योजना (पारंपरिक कारीगर एवं शिल्पकार)",
        categoryId = "CAT-09",
        categoryNameEn = "Skills & Employment",
        categoryNameHi = "कौशल विकास एवं रोजगार",
        jurisdiction = SchemeJurisdiction.CENTRAL,
        stateCode = null,
        stateNameEn = "All India (Nationwide)",
        ministryEn = "Ministry of Micro, Small and Medium Enterprises (MSME)",
        ministryHi = "सूक्ष्म, लघु एवं मध्यम उद्यम मंत्रालय",
        benefitType = BenefitType.SUBSIDY,
        financialBenefitEn = "₹15,000 modern toolkit grant + ₹3 Lakhs collateral-free loan at 5% interest",
        financialBenefitHi = "₹15,000 आधुनिक टूलकिट अनुदान + ₹3 लाख तक का 5% रियायती ब्याज पर बिना गारंटी ऋण",
        benefitAmountValue = 315000,
        targetGender = TargetGender.ALL,
        targetAgeBracket = AgeBracket.ADULTS,
        socialCategory = "Traditional Artisans",
        isDbtLinked = true,
        requiresLowIncome = false,
        eligibilitySummaryEn = "Artisans engaged in 18 traditional family-based trades (Carpenters, Blacksmiths, Masons, Tailors, Potters, Cobblers).",
        eligibilitySummaryHi = "18 पारंपरिक व्यवसायों (बढ़ई, लोहार, राजमिस्त्री, दर्जी, कुम्हार, मोची आदि) में कार्यरत कारीगर।",
        overviewEn = "Provides end-to-end support to traditional artisans and craftspeople: recognition via PM Vishwakarma ID, skill upgrading, toolkit e-vouchers, collateral-free credit, and marketing linkages.",
        overviewHi = "हाथ और औजारों से काम करने वाले पारंपरिक कारीगरों को पहचान, उन्नत प्रशिक्षण, टूलकिट सहायता और रियायती ऋण उपलब्ध कराने की राष्ट्रीय योजना।",
        detailedBenefits = listOf(
            "PM Vishwakarma Certificate and ID Card.",
            "Basic training of 5-7 days and advanced training of 15+ days with ₹500/day stipend.",
            "₹15,000 e-voucher grant for purchasing modern toolkits.",
            "Collateral-free credit support: 1st tranche up to ₹1 Lakh (18 months), 2nd tranche up to ₹2 Lakhs (30 months) at 5% interest."
        ),
        requiredDocuments = listOf(
            "Aadhaar Card and Active Mobile Number",
            "Bank Passbook details",
            "Ration Card (for family verification)"
        ),
        applicationSteps = listOf(
            "Apply free of charge at any CSC center with biometric e-KYC.",
            "Gram Panchayat / Urban Local Body verifies artisanal trade.",
            "Receive Vishwakarma digital certificate and toolkit voucher."
        ),
        officialPortalUrl = "https://pmvishwakarma.gov.in",
        popularityScore = 96,
        launchYear = 2023,
        icon = Icons.Default.Handyman
    ),
    SchemeItem(
        id = "SCH-SOC-01",
        titleEn = "Indira Gandhi National Old Age Pension Scheme (IGNOAPS)",
        titleHi = "इंदिरा गांधी राष्ट्रीय वृद्धावस्था पेंशन योजना",
        categoryId = "CAT-10",
        categoryNameEn = "Social Welfare & Empowerment",
        categoryNameHi = "सामाजिक कल्याण एवं सशक्तीकरण",
        jurisdiction = SchemeJurisdiction.CENTRAL,
        stateCode = null,
        stateNameEn = "All India (Nationwide)",
        ministryEn = "Ministry of Rural Development (NSAP)",
        ministryHi = "ग्रामीण विकास मंत्रालय (एनएसएपी)",
        benefitType = BenefitType.DBT_CASH,
        financialBenefitEn = "₹1,000 to ₹2,500 monthly pension credited directly via DBT to senior citizens",
        financialBenefitHi = "वरिष्ठ नागरिकों को ₹1,000 से ₹2,500 प्रति माह सीधे बैंक खाते में पेंशन",
        benefitAmountValue = 24000,
        targetGender = TargetGender.ALL,
        targetAgeBracket = AgeBracket.SENIORS,
        socialCategory = "BPL Senior Citizens",
        isDbtLinked = true,
        requiresLowIncome = true,
        eligibilitySummaryEn = "Persons aged 60 years and above belonging to a household living Below the Poverty Line (BPL).",
        eligibilitySummaryHi = "गरीबी रेखा से नीचे (बीपीएल) जीवन यापन करने वाले 60 वर्ष या उससे अधिक आयु के वृद्ध नागरिक।",
        overviewEn = "Constitutional social assistance providing monthly pension to destitute elderly citizens across India to ensure dignified living without financial dependency.",
        overviewHi = "वृद्धावस्था में आर्थिक सुरक्षा सुनिश्चित करने हेतु केंद्र व राज्य सरकारों के संयुक्त सहयोग से दी जाने वाली मासिक पेंशन।",
        detailedBenefits = listOf(
            "Monthly financial security credited directly through Direct Benefit Transfer.",
            "Enhanced monthly rate for super-senior citizens aged 80 and above."
        ),
        requiredDocuments = listOf(
            "Age Proof / Aadhaar Card (confirming 60+ age)",
            "Valid BPL Card / Ration Card",
            "Bank Account Passbook seeded with Aadhaar"
        ),
        applicationSteps = listOf(
            "Apply via National Social Assistance Programme (nsap.nic.in) or Block Development Office / Tehsildar.",
            "Verification of BPL status and age by local authority.",
            "Sanction order issued and monthly pension disbursed through PFMS."
        ),
        officialPortalUrl = "https://nsap.nic.in",
        popularityScore = 94,
        launchYear = 2007,
        icon = Icons.Default.Elderly
    ),
    SchemeItem(
        id = "SCH-SPT-01",
        titleEn = "Khelo India Talent Development Scholarship",
        titleHi = "खेलो इंडिया प्रतिभा विकास छात्रवृत्ति",
        categoryId = "CAT-11",
        categoryNameEn = "Sports & Culture",
        categoryNameHi = "खेल एवं संस्कृति",
        jurisdiction = SchemeJurisdiction.CENTRAL,
        stateCode = null,
        stateNameEn = "All India (Nationwide)",
        ministryEn = "Ministry of Youth Affairs and Sports",
        ministryHi = "युवा कार्यक्रम एवं खेल मंत्रालय",
        benefitType = BenefitType.SCHOLARSHIP_TRAINING,
        financialBenefitEn = "₹5,00,000 per annum for 8 consecutive years for identified sporting talent",
        financialBenefitHi = "चिन्हित प्रतिभाशाली एथलीटों को लगातार 8 वर्षों तक ₹5,00,000 वार्षिक वित्तीय सहायता",
        benefitAmountValue = 500000,
        targetGender = TargetGender.ALL,
        targetAgeBracket = AgeBracket.YOUTH,
        socialCategory = "Athletes & Sports Talents",
        isDbtLinked = true,
        requiresLowIncome = false,
        eligibilitySummaryEn = "Young athletes identified through Khelo India Youth Games, University Games, and national trials.",
        eligibilitySummaryHi = "खेलो इंडिया युवा खेलों एवं राष्ट्रीय चयन स्पर्धाओं में उत्कृष्ट प्रदर्शन करने वाले युवा एथलीट।",
        overviewEn = "Identifies and nurtures young sporting talent from grassroots across various Olympic disciplines, providing world-class coaching, diet, equipment, and competition exposure.",
        overviewHi = "ओलंपिक खेलों में पदक जीतने हेतु युवा खेल प्रतिभाओं को कोचिंग, वैज्ञानिक प्रशिक्षण, पोषण और अंतर्राष्ट्रीय स्तर की सुविधाएं।",
        detailedBenefits = listOf(
            "₹5 Lakh annual financial support covering equipment, coaching fees, and out-of-pocket allowances.",
            "Admission into high-performance National Centres of Excellence (NCOE)."
        ),
        requiredDocuments = listOf(
            "National / State Sports Competition Certificate",
            "Age Certificate / School Marksheet",
            "Aadhaar Card and Bank Account details"
        ),
        applicationSteps = listOf(
            "Register on Khelo India Athlete Portal (kheloindia.gov.in).",
            "Participate in recognized Khelo India Youth/University events.",
            "Talent Identification Committee assesses and inducts shortlisted athletes."
        ),
        officialPortalUrl = "https://kheloindia.gov.in",
        popularityScore = 82,
        launchYear = 2018,
        icon = Icons.Default.EmojiEvents
    ),
    SchemeItem(
        id = "SCH-TRA-01",
        titleEn = "Pradhan Mantri Gram Sadak Yojana (PMGSY)",
        titleHi = "प्रधानमंत्री ग्राम सड़क योजना (पीएमजीएसवाई)",
        categoryId = "CAT-12",
        categoryNameEn = "Transport & Infrastructure",
        categoryNameHi = "परिवहन एवं बुनियादी ढांचा",
        jurisdiction = SchemeJurisdiction.CENTRAL,
        stateCode = null,
        stateNameEn = "All India (Nationwide)",
        ministryEn = "Ministry of Rural Development (NRRDA)",
        ministryHi = "ग्रामीण विकास मंत्रालय",
        benefitType = BenefitType.IN_KIND,
        financialBenefitEn = "100% all-weather paved road connectivity connecting rural villages to state highways",
        financialBenefitHi = "ग्रामीण बस्तियों को मुख्य मार्गों एवं बाजारों से जोड़ने वाली पक्की बारहमासी सड़कें",
        benefitAmountValue = 2000000,
        targetGender = TargetGender.ALL,
        targetAgeBracket = AgeBracket.ALL,
        socialCategory = "Rural Habitations",
        isDbtLinked = false,
        requiresLowIncome = false,
        eligibilitySummaryEn = "Eligible unconnected habitations with population 500+ (plain areas) and 250+ (hilly/tribal/desert areas).",
        eligibilitySummaryHi = "500+ आबादी (मैदानी) एवं 250+ आबादी (पहाड़ी/जनजातीय) वाले असंबद्ध ग्रामीण क्षेत्र।",
        overviewEn = "Centrally sponsored flagship program to provide all-weather road connectivity to unconnected rural habitations, enhancing access to education, healthcare, and economic markets.",
        overviewHi = "ग्रामीण क्षेत्रों के सामाजिक व आर्थिक विकास हेतु सभी योग्य गांवों को पक्की बारहमासी सड़कों से जोड़ने का राष्ट्रीय अभियान।",
        detailedBenefits = listOf(
            "All-weather bitumen asphalt roads built to Indian Roads Congress standards.",
            "5-year built-in maintenance guarantee by contracting agency."
        ),
        requiredDocuments = listOf(
            "Gram Sabha Resolution for Village Connectivity",
            "Habitation Population Census Data"
        ),
        applicationSteps = listOf(
            "Proposals formulated by District Panchayat and State Rural Roads Development Agency.",
            "Citizen feedback and road quality reporting via 'Meri Sadak' mobile app."
        ),
        officialPortalUrl = "https://omms.nic.in",
        popularityScore = 79,
        launchYear = 2000,
        icon = Icons.Default.DirectionsBus
    ),
    SchemeItem(
        id = "SCH-UTL-01",
        titleEn = "Jal Jeevan Mission: Har Ghar Jal",
        titleHi = "जल जीवन मिशन: हर घर जल",
        categoryId = "CAT-14",
        categoryNameEn = "Utility & Sanitation",
        categoryNameHi = "उपयोगिता एवं स्वच्छता",
        jurisdiction = SchemeJurisdiction.CENTRAL,
        stateCode = null,
        stateNameEn = "All India (Nationwide)",
        ministryEn = "Ministry of Jal Shakti (Department of Drinking Water and Sanitation)",
        ministryHi = "जल शक्ति मंत्रालय",
        benefitType = BenefitType.IN_KIND,
        financialBenefitEn = "Free piped tap water connection delivering 55 litres/person/day in every rural home",
        financialBenefitHi = "हर ग्रामीण घर में प्रति व्यक्ति 55 लीटर शुद्ध नल जल की मुफ़्त पाइपलाइन आपूर्ति",
        benefitAmountValue = 18000,
        targetGender = TargetGender.ALL,
        targetAgeBracket = AgeBracket.ALL,
        socialCategory = "All Rural Households",
        isDbtLinked = false,
        requiresLowIncome = false,
        eligibilitySummaryEn = "All households situated in rural villages across all States and Union Territories of India.",
        eligibilitySummaryHi = "भारत के सभी राज्यों एवं केंद्र शासित प्रदेशों के गांवों में स्थित सभी ग्रामीण परिवार।",
        overviewEn = "A transformative initiative ensuring every rural household gets safe and adequate drinking water through individual household tap connections (FHTC) on a long-term basis.",
        overviewHi = "देश के प्रत्येक ग्रामीण परिवार को घर पर ही नियमित और शुद्ध पेयजल उपलब्ध कराने का महत्वाकांक्षी राष्ट्रीय मिशन।",
        detailedBenefits = listOf(
            "Zero installation charge for household water meter and pipe fitting.",
            "Potable water certified to BIS IS 10500 drinking standards.",
            "Community water quality testing through Field Test Kits (FTKs)."
        ),
        requiredDocuments = listOf(
            "Household identity proof (Aadhaar / Voter ID)",
            "Gram Panchayat village survey verification"
        ),
        applicationSteps = listOf(
            "Village Water & Sanitation Committee (VWSC) prepares Village Action Plan.",
            "Pipeline works executed; tap connection installed at household doorstep.",
            "Track village progress live on JJM Dashboard (ejalshakti.gov.in)."
        ),
        officialPortalUrl = "https://jaljeevanmission.gov.in",
        popularityScore = 93,
        launchYear = 2019,
        icon = Icons.Default.WaterDrop
    ),
    SchemeItem(
        id = "SCH-WOM-01",
        titleEn = "Mukhyamantri Majhi Ladki Bahin Yojana",
        titleHi = "मुख्यमंत्री माझी लाडकी बहीण योजना",
        categoryId = "CAT-15",
        categoryNameEn = "Women & Child",
        categoryNameHi = "महिला एवं बाल कल्याण",
        jurisdiction = SchemeJurisdiction.STATE,
        stateCode = "MH",
        stateNameEn = "Maharashtra",
        ministryEn = "Department of Women & Child Development (Govt of Maharashtra)",
        ministryHi = "महिला व बाल विकास विभाग (महाराष्ट्र शासन)",
        benefitType = BenefitType.DBT_CASH,
        financialBenefitEn = "₹1,500 monthly direct cash transfer (₹18,000/year) into woman's bank account",
        financialBenefitHi = "प्रति माह ₹1,500 नकद (वार्षिक ₹18,000) सीधे महिला के बैंक खाते में",
        benefitAmountValue = 18000,
        targetGender = TargetGender.FEMALE,
        targetAgeBracket = AgeBracket.ADULTS,
        socialCategory = "All Eligible Women",
        isDbtLinked = true,
        requiresLowIncome = true,
        eligibilitySummaryEn = "Married, widowed, divorced, deserted, or destitute women aged 21-65 years residing in Maharashtra with annual family income up to ₹2.5 Lakhs.",
        eligibilitySummaryHi = "महाराष्ट्र की 21 से 65 वर्ष आयु की महिलाएं जिनके परिवार की वार्षिक आय ₹2.5 लाख से कम हो।",
        overviewEn = "Flagship social security and economic independence scheme by Government of Maharashtra providing unconditional monthly financial assistance of ₹1,500 to women.",
        overviewHi = "महिलाओं के आर्थिक स्वावलंबन, स्वास्थ्य एवं पोषण सुधार हेतु महाराष्ट्र सरकार द्वारा ₹1,500 प्रति माह की प्रत्यक्ष आर्थिक सहायता।",
        detailedBenefits = listOf(
            "Direct Benefit Transfer of ₹1,500 every month on the 15th to woman's Aadhaar-seeded bank account.",
            "Income certificate exemption for families holding Yellow or Orange Ration Cards."
        ),
        requiredDocuments = listOf(
            "Aadhaar Card of the woman applicant",
            "Maharashtra Domicile Certificate or 15-year old Ration Card/Voter ID",
            "Yellow/Orange Ration Card or Income Certificate (< ₹2.5 Lakhs)",
            "Bank Passbook seeded with Aadhaar and active DBT",
            "Undertaking of not having government employee in family"
        ),
        applicationSteps = listOf(
            "Apply via Nari Shakti Doot mobile app or at local Anganwadi/Setu Seva Kendra.",
            "Upload Aadhaar, bank passbook, and ration card.",
            "Ward/Village committee approves application; money transferred monthly."
        ),
        officialPortalUrl = "https://ladakibahin.maharashtra.gov.in",
        popularityScore = 99,
        launchYear = 2024,
        icon = Icons.Default.FamilyRestroom
    ),
    SchemeItem(
        id = "SCH-WOM-02",
        titleEn = "Sukanya Samriddhi Yojana (SSY)",
        titleHi = "सुकन्या समृद्धि योजना (एसएसवाई)",
        categoryId = "CAT-15",
        categoryNameEn = "Women & Child",
        categoryNameHi = "महिला एवं बाल कल्याण",
        jurisdiction = SchemeJurisdiction.CENTRAL,
        stateCode = null,
        stateNameEn = "All India (Nationwide)",
        ministryEn = "Ministry of Finance",
        ministryHi = "वित्त मंत्रालय",
        benefitType = BenefitType.SUBSIDY,
        financialBenefitEn = "Highest sovereign 8.2% tax-free compound interest + 80C tax exemption on maturity",
        financialBenefitHi = "सर्वोच्च 8.2% कर-मुक्त चक्रवृद्धि ब्याज + परिपक्वता पर पूर्ण कर छूट (80C)",
        benefitAmountValue = 1500000,
        targetGender = TargetGender.FEMALE,
        targetAgeBracket = AgeBracket.CHILDREN,
        socialCategory = "All Girl Children",
        isDbtLinked = false,
        requiresLowIncome = false,
        eligibilitySummaryEn = "Girl child who is an Indian resident from birth until the age of 10 years (maximum 2 accounts per family).",
        eligibilitySummaryHi = "जन्म से 10 वर्ष तक की आयु की भारतीय बालिका (प्रति परिवार अधिकतम 2 खाते)।",
        overviewEn = "A small deposit savings scheme promoted under Beti Bachao Beti Padhao campaign to meet the financial expenses of higher education and marriage of the girl child.",
        overviewHi = "बेटी बचाओ बेटी पढ़ाओ अभियान के तहत बालिकाओं की उच्च शिक्षा एवं भविष्य की आर्थिक सुरक्षा हेतु विशेष बचत योजना।",
        detailedBenefits = listOf(
            "Highest guaranteed interest rate (currently 8.2% per annum) compounded annually.",
            "Triple tax exemption: contribution, interest earned, and final maturity are all 100% tax-free under EEE status.",
            "Partial withdrawal up to 50% allowed after girl turns 18 for university education."
        ),
        requiredDocuments = listOf(
            "Birth Certificate of the girl child issued by municipal/panchayat authority",
            "Identity Proof & Address Proof of the parent/guardian (Aadhaar/PAN)",
            "Two photographs of child and guardian"
        ),
        applicationSteps = listOf(
            "Visit any India Post Post Office branch or authorized commercial bank.",
            "Fill Form-1 and deposit initial amount (minimum ₹250).",
            "Passbook issued in girl's name; deposit annually for 15 years."
        ),
        officialPortalUrl = "https://www.indiapost.gov.in",
        popularityScore = 97,
        launchYear = 2015,
        icon = Icons.Default.Savings
    ),
    SchemeItem(
        id = "SCH-WOM-03",
        titleEn = "Kalaignar Magalir Urimai Thittam",
        titleHi = "कलैग्नार महिला अधिकार योजना",
        categoryId = "CAT-15",
        categoryNameEn = "Women & Child",
        categoryNameHi = "महिला एवं बाल कल्याण",
        jurisdiction = SchemeJurisdiction.STATE,
        stateCode = "TN",
        stateNameEn = "Tamil Nadu",
        ministryEn = "Special Programme Implementation Dept (Govt of Tamil Nadu)",
        ministryHi = "विशेष कार्यक्रम क्रियान्वयन विभाग (तमिलनाडु)",
        benefitType = BenefitType.DBT_CASH,
        financialBenefitEn = "₹1,000 monthly basic income transfer (₹12,000/year) directly to female head of household",
        financialBenefitHi = "प्रति माह ₹1,000 (वार्षिक ₹12,000) महिला मुखिया के बैंक खाते में सीधा अंतरण",
        benefitAmountValue = 12000,
        targetGender = TargetGender.FEMALE,
        targetAgeBracket = AgeBracket.ADULTS,
        socialCategory = "Eligible Female Heads",
        isDbtLinked = true,
        requiresLowIncome = true,
        eligibilitySummaryEn = "Women heads of families aged 21 years and above residing in Tamil Nadu with annual family income under ₹2.5 Lakhs.",
        eligibilitySummaryHi = "तमिलनाडु की 21 वर्ष से अधिक आयु की महिला परिवार प्रमुख जिनके परिवार की वार्षिक आय ₹2.5 लाख से कम हो।",
        overviewEn = "Pioneering basic income entitlement by Government of Tamil Nadu recognizing the unpaid labor of women homemakers and promoting female financial independence.",
        overviewHi = "महिलाओं के गृहकार्य के सम्मान एवं आर्थिक स्वावलंबन हेतु तमिलनाडु सरकार की मासिक नकद अंतरण योजना।",
        detailedBenefits = listOf(
            "Monthly ₹1,000 direct credit on the 15th of every month.",
            "Reaches over 1.15 crore eligible women in Tamil Nadu."
        ),
        requiredDocuments = listOf(
            "Tamil Nadu Smart Family Card (Ration Card)",
            "Aadhaar Card and Bank Passbook",
            "Electricity Consumer Number"
        ),
        applicationSteps = listOf(
            "Fill application form at specialized village/ward camps organized by revenue department.",
            "Fingerprint e-KYC verified against Aadhaar and Smart Card.",
            "Status verified online via kmut.tn.gov.in portal."
        ),
        officialPortalUrl = "https://kmut.tn.gov.in",
        popularityScore = 92,
        launchYear = 2023,
        icon = Icons.Default.VolunteerActivism
    )
)

// -------------------------------------------------------------
// FILTER OPTIONS LISTS
// -------------------------------------------------------------

val ALL_FILTER_STATES = listOf(
    "All India (Nationwide)",
    "Maharashtra",
    "Uttar Pradesh",
    "Tamil Nadu",
    "Bihar",
    "Karnataka",
    "Rajasthan",
    "Madhya Pradesh",
    "West Bengal",
    "Gujarat",
    "NCT of Delhi",
    "Kerala",
    "Punjab",
    "Odisha",
    "Assam",
    "Telangana",
    "Andhra Pradesh"
)

val ALL_FILTER_MINISTRIES = listOf(
    "All Ministries",
    "Ministry of Agriculture & Farmers Welfare",
    "Ministry of Finance",
    "Ministry of Health & Family Welfare",
    "Ministry of Rural Development",
    "Ministry of Education",
    "Ministry of Women and Child Development",
    "Ministry of Social Justice & Empowerment",
    "Ministry of Micro, Small and Medium Enterprises",
    "Ministry of Electronics & Information Technology",
    "Ministry of Law and Justice",
    "Ministry of Jal Shakti",
    "Ministry of Youth Affairs and Sports"
)

val ALL_15_CATEGORY_TITLES = listOf(
    "All Categories",
    "Agriculture, Rural & Environment",
    "Banking, Financial Services & Insurance",
    "Business & Entrepreneurship",
    "Education & Learning",
    "Health & Wellness",
    "Housing & Shelter",
    "Public Safety, Law & Justice",
    "Science, IT & Communications",
    "Skills & Employment",
    "Social Welfare & Empowerment",
    "Sports & Culture",
    "Transport & Infrastructure",
    "Travel & Tourism",
    "Utility & Sanitation",
    "Women & Child"
)

// -------------------------------------------------------------
// MAIN REUSABLE SCHEME LIST SCREEN
// -------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchemeListScreen(
    initialCategoryId: String? = null,
    initialCategoryName: String? = null,
    initialMinistryName: String? = null,
    currentLanguage: AppLanguage = AppLanguage.HINDI,
    onSelectLanguage: (AppLanguage) -> Unit = {},
    isLargeFont: Boolean = false,
    onFontScaleToggle: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
    // Optional: when provided, tapping a scheme card navigates to SchemeDetailsScreen
    // instead of showing the inline modal. This enables full back-stack navigation.
    onNavigateSchemeDetails: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val strings = getJanSaarthiStrings(currentLanguage)
    val fontSizeMultiplier = if (isLargeFont) 1.15f else 1.0f

    // -------------------------------------------------------------
    // LOCAL FILTER & SEARCH REACTIVE STATE
    // -------------------------------------------------------------
    var searchQuery by remember { mutableStateOf("") }
    var selectedJurisdiction by remember { mutableStateOf(SchemeJurisdiction.ALL) }
    var selectedState by remember { mutableStateOf("All India (Nationwide)") }
    var selectedMinistry by remember { mutableStateOf(initialMinistryName ?: "All Ministries") }
    var selectedGender by remember { mutableStateOf(TargetGender.ALL) }
    var selectedAgeBracket by remember { mutableStateOf(AgeBracket.ALL) }
    var selectedCategoryFilter by remember {
        val resolved = initialCategoryName ?: initialCategoryId?.let { id ->
            REALISTIC_MOCK_SCHEMES.firstOrNull { it.categoryId.equals(id, ignoreCase = true) }?.categoryNameEn
        } ?: "All Categories"
        mutableStateOf(resolved)
    }
    var selectedBenefitType by remember { mutableStateOf(BenefitType.ALL) }
    var onlyDbtFilter by remember { mutableStateOf(false) }
    var onlyLowIncomeFilter by remember { mutableStateOf(false) }
    var selectedSortOption by remember { mutableStateOf(SchemeSortOption.POPULARITY) }

    // Saved Bookmarks Set
    var bookmarkedSchemeIds by remember {
        mutableStateOf(setOf("SCH-AGR-01", "SCH-HLT-01", "SCH-WOM-01"))
    }

    // Modal Sheet Controllers
    var showFilterBottomSheet by remember { mutableStateOf(false) }
    var selectedSchemeForDetails by remember { mutableStateOf<SchemeItem?>(null) }
    var showShareConfirmationMessage by remember { mutableStateOf<String?>(null) }

    // -------------------------------------------------------------
    // FILTERING AND SORTING ENGINE
    // -------------------------------------------------------------
    val filteredAndSortedSchemes = remember(
        searchQuery,
        selectedJurisdiction,
        selectedState,
        selectedMinistry,
        selectedGender,
        selectedAgeBracket,
        selectedCategoryFilter,
        selectedBenefitType,
        onlyDbtFilter,
        onlyLowIncomeFilter,
        selectedSortOption,
        currentLanguage
    ) {
        val q = searchQuery.trim().lowercase()

        REALISTIC_MOCK_SCHEMES.filter { item ->
            // Search Query Filter
            val matchesQuery = q.isEmpty() ||
                    item.titleEn.lowercase().contains(q) ||
                    item.titleHi.lowercase().contains(q) ||
                    item.ministryEn.lowercase().contains(q) ||
                    item.ministryHi.lowercase().contains(q) ||
                    item.categoryNameEn.lowercase().contains(q) ||
                    item.categoryNameHi.lowercase().contains(q) ||
                    item.financialBenefitEn.lowercase().contains(q) ||
                    item.financialBenefitHi.lowercase().contains(q) ||
                    item.eligibilitySummaryEn.lowercase().contains(q) ||
                    item.eligibilitySummaryHi.lowercase().contains(q)

            // Jurisdiction Filter (Central / State / UT)
            val matchesJurisdiction = when (selectedJurisdiction) {
                SchemeJurisdiction.ALL -> true
                SchemeJurisdiction.CENTRAL -> item.jurisdiction == SchemeJurisdiction.CENTRAL
                SchemeJurisdiction.STATE -> item.jurisdiction == SchemeJurisdiction.STATE
                SchemeJurisdiction.UT -> item.jurisdiction == SchemeJurisdiction.UT
            }

            // State Filter
            val matchesState = if (selectedState == "All India (Nationwide)") {
                true
            } else {
                item.stateNameEn == selectedState || item.jurisdiction == SchemeJurisdiction.CENTRAL
            }

            // Ministry Filter
            val matchesMinistry = if (selectedMinistry == "All Ministries") {
                true
            } else {
                item.ministryEn.contains(selectedMinistry, ignoreCase = true) ||
                        selectedMinistry.contains(item.ministryEn, ignoreCase = true) ||
                        (item.ministryHi.isNotEmpty() && item.ministryHi.contains(selectedMinistry, ignoreCase = true))
            }

            // Gender Filter
            val matchesGender = when (selectedGender) {
                TargetGender.ALL -> true
                TargetGender.FEMALE -> item.targetGender == TargetGender.FEMALE || item.targetGender == TargetGender.ALL
                TargetGender.MALE -> item.targetGender == TargetGender.MALE || item.targetGender == TargetGender.ALL
                TargetGender.TRANSGENDER -> item.targetGender == TargetGender.TRANSGENDER || item.targetGender == TargetGender.ALL
            }

            // Age Bracket Filter
            val matchesAge = when (selectedAgeBracket) {
                AgeBracket.ALL -> true
                else -> item.targetAgeBracket == selectedAgeBracket || item.targetAgeBracket == AgeBracket.ALL
            }

            // Category Domain Filter
            val matchesCategory = if (selectedCategoryFilter == "All Categories") {
                true
            } else {
                item.categoryNameEn.equals(selectedCategoryFilter, ignoreCase = true)
            }

            // Benefit Type Filter
            val matchesBenefitType = when (selectedBenefitType) {
                BenefitType.ALL -> true
                else -> item.benefitType == selectedBenefitType
            }

            // DBT Only Filter
            val matchesDbt = !onlyDbtFilter || item.isDbtLinked

            // Low Income Filter
            val matchesLowIncome = !onlyLowIncomeFilter || item.requiresLowIncome

            matchesQuery && matchesJurisdiction && matchesState && matchesMinistry &&
                    matchesGender && matchesAge && matchesCategory && matchesBenefitType &&
                    matchesDbt && matchesLowIncome
        }.let { list ->
            // Sorting Logic
            when (selectedSortOption) {
                SchemeSortOption.POPULARITY -> list.sortedByDescending { it.popularityScore }
                SchemeSortOption.HIGHEST_BENEFIT -> list.sortedByDescending { it.benefitAmountValue }
                SchemeSortOption.NEWLY_LAUNCHED -> list.sortedByDescending { it.launchYear }
                SchemeSortOption.ALPHABETICAL -> list.sortedBy { it.getTitle(currentLanguage) }
            }
        }
    }

    // Active filters count calculation
    val activeFiltersCount = remember(
        selectedJurisdiction,
        selectedState,
        selectedMinistry,
        selectedGender,
        selectedAgeBracket,
        selectedCategoryFilter,
        selectedBenefitType,
        onlyDbtFilter,
        onlyLowIncomeFilter
    ) {
        var count = 0
        if (selectedJurisdiction != SchemeJurisdiction.ALL) count++
        if (selectedState != "All India (Nationwide)") count++
        if (selectedMinistry != "All Ministries") count++
        if (selectedGender != TargetGender.ALL) count++
        if (selectedAgeBracket != AgeBracket.ALL) count++
        if (selectedCategoryFilter != "All Categories") count++
        if (selectedBenefitType != BenefitType.ALL) count++
        if (onlyDbtFilter) count++
        if (onlyLowIncomeFilter) count++
        count
    }

    Scaffold(
        topBar = {
            Column {
                // National Banner
                GovernmentBanner(language = currentLanguage, fontSizeMultiplier = fontSizeMultiplier)

                // Top Bar with Title & Actions
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
                                    contentDescription = "Back",
                                    tint = GovNavyPrimary
                                )
                            }

                            Column {
                                val headerTitle = when {
                                    selectedMinistry != "All Ministries" -> selectedMinistry
                                    selectedCategoryFilter != "All Categories" -> selectedCategoryFilter
                                    else -> if (currentLanguage == AppLanguage.HINDI) "समस्त सरकारी योजनाएं" else "All Government Schemes"
                                }
                                Text(
                                    text = headerTitle,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GovNavyPrimary,
                                        fontSize = (15 * fontSizeMultiplier).sp
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI)
                                        "आधिकारिक राष्ट्रीय पोर्टल सूची • myScheme"
                                    else
                                        "Official National Directory • myScheme",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF64748B),
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        // Filter Button with Badge
                        IconButton(onClick = { showFilterBottomSheet = true }) {
                            BadgedBox(
                                badge = {
                                    if (activeFiltersCount > 0) {
                                        Badge(containerColor = GovSaffron, contentColor = Color.White) {
                                            Text("$activeFiltersCount", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FilterList,
                                    contentDescription = "Filter",
                                    tint = GovNavyPrimary
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
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // 1. Accessibility Bar
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
                                "📌 प्रोटोटाइप प्रदर्शन: यह स्क्रीन यूआई प्रदर्शन हेतु यथार्थवादी मॉक डेटा उपयोग करती है। यह लाइव सरकारी पोर्टल डेटा नहीं है।"
                            else
                                "📌 UI Demonstration: Uses realistic mock scheme data for prototype evaluation only. Not live official government data.",
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
                                "योजना, मंत्रालय, या लाभ खोजें..."
                            else
                                "Search schemes by title, ministry, benefit...",
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

            // 4. Quick Filter Chips: Level (Central / State / UT)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI) "योजना स्तर (Level):" else "Jurisdiction Level:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF475569)
                        )
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(SchemeJurisdiction.entries) { jur ->
                            val isSelected = selectedJurisdiction == jur
                            val label = when (jur) {
                                SchemeJurisdiction.ALL -> if (currentLanguage == AppLanguage.HINDI) "सभी (All)" else "All Levels"
                                SchemeJurisdiction.CENTRAL -> if (currentLanguage == AppLanguage.HINDI) "🇮🇳 केंद्र (Central)" else "🇮🇳 Central"
                                SchemeJurisdiction.STATE -> if (currentLanguage == AppLanguage.HINDI) "🏛️ राज्य (State)" else "🏛️ State"
                                SchemeJurisdiction.UT -> if (currentLanguage == AppLanguage.HINDI) "🏙️ केंद्र शासित (UT)" else "🏙️ UT"
                            }

                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedJurisdiction = jur },
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
            }

            // 5. Quick Benefit Type Chips
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI) "लाभ का प्रकार (Benefit Type):" else "Benefit Type:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF475569)
                        )
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(BenefitType.entries) { bType ->
                            val isSelected = selectedBenefitType == bType
                            val label = if (currentLanguage == AppLanguage.HINDI) bType.displayNameHi else bType.displayNameEn

                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedBenefitType = bType },
                                label = {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF0F766E),
                                    selectedLabelColor = Color.White,
                                    containerColor = Color.White,
                                    labelColor = Color(0xFF0F766E)
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = if (isSelected) Color(0xFF0F766E) else Color(0xFFCBD5E1)
                                )
                            )
                        }
                    }
                }
            }

            // 6. Active Filters Summary Bar & "More Filters" Trigger
            item {
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "${filteredAndSortedSchemes.size} " + (if (currentLanguage == AppLanguage.HINDI) "योजनाएं उपलब्ध" else "Schemes Found"),
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GovNavyPrimary,
                                    fontSize = 13.sp
                                )
                            )

                            if (activeFiltersCount > 0) {
                                Surface(
                                    color = Color(0xFFFEF3C7),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = "$activeFiltersCount active",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color(0xFF92400E),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            if (activeFiltersCount > 0 || searchQuery.isNotEmpty()) {
                                TextButton(onClick = {
                                    searchQuery = ""
                                    selectedJurisdiction = SchemeJurisdiction.ALL
                                    selectedState = "All India (Nationwide)"
                                    selectedMinistry = "All Ministries"
                                    selectedGender = TargetGender.ALL
                                    selectedAgeBracket = AgeBracket.ALL
                                    selectedCategoryFilter = "All Categories"
                                    selectedBenefitType = BenefitType.ALL
                                    onlyDbtFilter = false
                                    onlyLowIncomeFilter = false
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

                            FilledTonalButton(
                                onClick = { showFilterBottomSheet = true },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = GovNavyContainer,
                                    contentColor = GovNavyPrimary
                                )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Tune,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = if (currentLanguage == AppLanguage.HINDI) "सभी फ़िल्टर" else "All Filters",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 7. Sort Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI) "क्रमबद्ध करें (Sort):" else "Sort by:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.Medium
                        )
                    )

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
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) selectedSortOption.displayNameHi else selectedSortOption.displayNameEn,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = GovNavyPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    tint = GovNavyPrimary,
                                    modifier = Modifier.size(16.dp)
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

            // 8. Empty Results State
            if (filteredAndSortedSchemes.isEmpty()) {
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
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SearchOff,
                                contentDescription = null,
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(44.dp)
                            )
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI)
                                    "कोई योजना नहीं मिली"
                                else
                                    "No matching schemes found",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF475569)
                                )
                            )
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI)
                                    "कृपया खोज शब्द बदलें या कुछ फ़िल्टर हटाएं।"
                                else
                                    "Try relaxing your search terms or clearing some active filters.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF94A3B8),
                                    textAlign = TextAlign.Center
                                )
                            )
                            Button(
                                onClick = {
                                    searchQuery = ""
                                    selectedJurisdiction = SchemeJurisdiction.ALL
                                    selectedState = "All India (Nationwide)"
                                    selectedMinistry = "All Ministries"
                                    selectedGender = TargetGender.ALL
                                    selectedAgeBracket = AgeBracket.ALL
                                    selectedCategoryFilter = "All Categories"
                                    selectedBenefitType = BenefitType.ALL
                                    onlyDbtFilter = false
                                    onlyLowIncomeFilter = false
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
                // 9. Scheme Cards List
                items(filteredAndSortedSchemes, key = { it.id }) { scheme ->
                    val isBookmarked = bookmarkedSchemeIds.contains(scheme.id)

                    SchemeCard(
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
                        onOpenDetails = {
                            // If an external scheme-details navigator is provided, use it for
                            // proper back-stack navigation; otherwise fall back to inline modal.
                            if (onNavigateSchemeDetails != null) {
                                onNavigateSchemeDetails(scheme.id)
                            } else {
                                selectedSchemeForDetails = scheme
                            }
                        },
                        onShare = {
                            showShareConfirmationMessage = "${scheme.getTitle(currentLanguage)} details copied to clipboard!"
                        }
                    )
                }
            }
        }
    }

    // -------------------------------------------------------------
    // EXHAUSTIVE FILTER BOTTOM SHEET / DIALOG
    // -------------------------------------------------------------
    if (showFilterBottomSheet) {
        Dialog(onDismissRequest = { showFilterBottomSheet = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.88f)
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
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = GovNavyPrimary
                            )
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) "फ़िल्टर एवं चयन" else "Filters & Refinements",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GovNavyPrimary
                                )
                            )
                        }

                        IconButton(onClick = { showFilterBottomSheet = false }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color(0xFF64748B)
                            )
                        }
                    }

                    HorizontalDivider(color = Color(0xFFE2E8F0))

                    // Scrollable Filters Form
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // 1. Category Domain Filter (15 Categories)
                        FilterSectionTitle(
                            title = if (currentLanguage == AppLanguage.HINDI) "1. योजना श्रेणी (15 श्रेणियां)" else "1. Scheme Category (15 Domains)"
                        )
                        var categoryExpanded by remember { mutableStateOf(false) }
                        OutlinedCard(
                            onClick = { categoryExpanded = !categoryExpanded },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = selectedCategoryFilter,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                                )
                                Icon(
                                    imageVector = if (categoryExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = null
                                )
                            }
                        }
                        if (categoryExpanded) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
                                    .padding(6.dp)
                            ) {
                                ALL_15_CATEGORY_TITLES.forEach { cat ->
                                    val isSelected = selectedCategoryFilter == cat
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                selectedCategoryFilter = cat
                                                categoryExpanded = false
                                            }
                                            .padding(vertical = 8.dp, horizontal = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = cat,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = if (isSelected) GovNavyPrimary else Color(0xFF334155),
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            )
                                        )
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = GovNavyPrimary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // 2. State / UT Filter
                        FilterSectionTitle(
                            title = if (currentLanguage == AppLanguage.HINDI) "2. राज्य / केंद्र शासित प्रदेश" else "2. State / UT Filter"
                        )
                        var stateExpanded by remember { mutableStateOf(false) }
                        OutlinedCard(
                            onClick = { stateExpanded = !stateExpanded },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = selectedState,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                                )
                                Icon(
                                    imageVector = if (stateExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = null
                                )
                            }
                        }
                        if (stateExpanded) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
                                    .padding(6.dp)
                            ) {
                                ALL_FILTER_STATES.forEach { stateName ->
                                    val isSelected = selectedState == stateName
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                selectedState = stateName
                                                stateExpanded = false
                                            }
                                            .padding(vertical = 8.dp, horizontal = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = stateName,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = if (isSelected) GovNavyPrimary else Color(0xFF334155),
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            )
                                        )
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = GovNavyPrimary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // 3. Ministry Filter
                        FilterSectionTitle(
                            title = if (currentLanguage == AppLanguage.HINDI) "3. संबंधित मंत्रालय (Ministry)" else "3. Sponsoring Ministry"
                        )
                        var ministryExpanded by remember { mutableStateOf(false) }
                        OutlinedCard(
                            onClick = { ministryExpanded = !ministryExpanded },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = selectedMinistry,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Icon(
                                    imageVector = if (ministryExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = null
                                )
                            }
                        }
                        if (ministryExpanded) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
                                    .padding(6.dp)
                            ) {
                                ALL_FILTER_MINISTRIES.forEach { ministryName ->
                                    val isSelected = selectedMinistry == ministryName
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                selectedMinistry = ministryName
                                                ministryExpanded = false
                                            }
                                            .padding(vertical = 8.dp, horizontal = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = ministryName,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = if (isSelected) GovNavyPrimary else Color(0xFF334155),
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            )
                                        )
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = GovNavyPrimary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // 4. Gender Filter
                        FilterSectionTitle(
                            title = if (currentLanguage == AppLanguage.HINDI) "4. लिंग पात्रता (Gender)" else "4. Gender Eligibility"
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            TargetGender.entries.forEach { gen ->
                                val isSelected = selectedGender == gen
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedGender = gen },
                                    label = {
                                        Text(
                                            text = if (currentLanguage == AppLanguage.HINDI) gen.displayNameHi else gen.displayNameEn,
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                )
                            }
                        }

                        // 5. Age Bracket Filter
                        FilterSectionTitle(
                            title = if (currentLanguage == AppLanguage.HINDI) "5. आयु वर्ग (Age Group)" else "5. Age Group"
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            AgeBracket.entries.forEach { age ->
                                val isSelected = selectedAgeBracket == age
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) GovNavyContainer else Color(0xFFF8FAFC),
                                    border = BorderStroke(1.dp, if (isSelected) GovNavyPrimary else Color(0xFFE2E8F0)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedAgeBracket = age }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = if (currentLanguage == AppLanguage.HINDI) age.displayNameHi else age.displayNameEn,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) GovNavyPrimary else Color(0xFF334155)
                                            )
                                        )
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = GovNavyPrimary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // 6. Eligibility Specific Toggles
                        FilterSectionTitle(
                            title = if (currentLanguage == AppLanguage.HINDI) "6. विशेष पात्रता (Eligibility Criteria)" else "6. Targeted Eligibility Toggles"
                        )
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
                                    text = if (currentLanguage == AppLanguage.HINDI) "केवल डीबीटी सक्रिय योजनाएं (DBT Only)" else "Direct Benefit Transfer (DBT) Only",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                                )
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) "सीधे बैंक खाते में नकद लाभ" else "Direct cash credit to bank account",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF64748B), fontSize = 11.sp)
                                )
                            }
                            Switch(checked = onlyDbtFilter, onCheckedChange = { onlyDbtFilter = it })
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onlyLowIncomeFilter = !onlyLowIncomeFilter }
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) "कम आय वर्ग (वार्षिक < ₹2.5 लाख)" else "Low Income / BPL Priority (< ₹2.5L)",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                                )
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) "आर्थिक रूप से कमजोर परिवारों के लिए" else "Schemes prioritizing EWS & BPL households",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF64748B), fontSize = 11.sp)
                                )
                            }
                            Switch(checked = onlyLowIncomeFilter, onCheckedChange = { onlyLowIncomeFilter = it })
                        }
                    }

                    HorizontalDivider(color = Color(0xFFE2E8F0))

                    // Bottom Action Buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                selectedJurisdiction = SchemeJurisdiction.ALL
                                selectedState = "All India (Nationwide)"
                                selectedMinistry = "All Ministries"
                                selectedGender = TargetGender.ALL
                                selectedAgeBracket = AgeBracket.ALL
                                selectedCategoryFilter = "All Categories"
                                selectedBenefitType = BenefitType.ALL
                                onlyDbtFilter = false
                                onlyLowIncomeFilter = false
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(if (currentLanguage == AppLanguage.HINDI) "रीसेट" else "Reset All")
                        }

                        Button(
                            onClick = { showFilterBottomSheet = false },
                            colors = ButtonDefaults.buttonColors(containerColor = GovNavyPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1.5f)
                        ) {
                            Text(if (currentLanguage == AppLanguage.HINDI) "लागू करें (${filteredAndSortedSchemes.size} योजनाएं)" else "Apply (${filteredAndSortedSchemes.size} Schemes)")
                        }
                    }
                }
            }
        }
    }

    // -------------------------------------------------------------
    // SCHEME DETAIL MODAL DIALOG
    // -------------------------------------------------------------
    selectedSchemeForDetails?.let { scheme ->
        Dialog(onDismissRequest = { selectedSchemeForDetails = null }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.92f)
                    .padding(vertical = 8.dp),
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
                                color = GovNavyContainer,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = scheme.icon,
                                        contentDescription = null,
                                        tint = GovNavyPrimary,
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
                                        color = if (scheme.jurisdiction == SchemeJurisdiction.CENTRAL) Color(0xFFEFF6FF) else Color(0xFFFEF3C7),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = if (scheme.jurisdiction == SchemeJurisdiction.CENTRAL) "🇮🇳 Central Scheme" else "🏛️ ${scheme.stateNameEn ?: "State"} Scheme",
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = if (scheme.jurisdiction == SchemeJurisdiction.CENTRAL) GovNavyPrimary else Color(0xFF92400E),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 9.sp
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
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = Color(0xFF15803D),
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 9.sp
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
                                        fontSize = 15.sp
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

                    Text(
                        text = scheme.getMinistry(currentLanguage),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF64748B),
                            fontSize = 11.sp
                        )
                    )

                    HorizontalDivider(color = Color(0xFFE2E8F0), modifier = Modifier.padding(vertical = 8.dp))

                    // Scrollable Scheme Details
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Key Benefit Highlight Card
                        Surface(
                            color = Color(0xFFF0FDF4),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) "मुख्य वित्तीय लाभ (Financial Benefit):" else "Core Financial Benefit:",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFF166534),
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = scheme.getFinancialBenefit(currentLanguage),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color(0xFF14532D),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                )
                            }
                        }

                        // Section 1: Overview
                        DetailSectionBlock(
                            title = if (currentLanguage == AppLanguage.HINDI) "योजना का विवरण (Overview)" else "Scheme Overview",
                            content = scheme.getOverview(currentLanguage)
                        )

                        // Section 2: Detailed Benefits Breakdown
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) "विस्तृत लाभ (Key Benefits):" else "Detailed Benefits:",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GovNavyPrimary,
                                    fontSize = 13.sp
                                )
                            )
                            scheme.detailedBenefits.forEach { bft ->
                                Row(
                                    verticalAlignment = Alignment.Top,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF16A34A),
                                        modifier = Modifier
                                            .size(16.dp)
                                            .padding(top = 2.dp)
                                    )
                                    Text(
                                        text = bft,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Color(0xFF334155),
                                            lineHeight = 16.sp
                                        )
                                    )
                                }
                            }
                        }

                        // Section 3: Eligibility Criteria
                        DetailSectionBlock(
                            title = if (currentLanguage == AppLanguage.HINDI) "पात्रता मानदंड (Eligibility Criteria)" else "Eligibility Criteria",
                            content = scheme.getEligibilitySummary(currentLanguage)
                        )

                        // Section 4: Required Documents
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) "आवश्यक दस्तावेज़ (Required Documents):" else "Required Documents:",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GovNavyPrimary,
                                    fontSize = 13.sp
                                )
                            )
                            scheme.requiredDocuments.forEach { doc ->
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
                                            imageVector = Icons.Default.Description,
                                            contentDescription = null,
                                            tint = GovNavyPrimary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = doc,
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

                        // Section 5: Application Steps
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) "आवेदन कैसे करें (Application Process):" else "How to Apply:",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GovNavyPrimary,
                                    fontSize = 13.sp
                                )
                            )
                            scheme.applicationSteps.forEachIndexed { index, step ->
                                Row(
                                    verticalAlignment = Alignment.Top,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Surface(
                                        color = GovNavyContainer,
                                        shape = CircleShape,
                                        modifier = Modifier.size(20.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "${index + 1}",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = GovNavyPrimary,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 10.sp
                                                )
                                            )
                                        }
                                    }
                                    Text(
                                        text = step,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Color(0xFF334155),
                                            lineHeight = 16.sp
                                        )
                                    )
                                }
                            }
                        }

                        // Demonstration Disclaimer Inside Modal
                        Surface(
                            color = Color(0xFFFEF3C7),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "⚠ Prototype Mock Data: This scheme profile is simulated for UI demonstration purposes only. To apply officially, please visit the genuine government department portal.",
                                modifier = Modifier.padding(10.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF92400E),
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }

                    HorizontalDivider(color = Color(0xFFE2E8F0), modifier = Modifier.padding(vertical = 8.dp))

                    // Modal Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(if (currentLanguage == AppLanguage.HINDI) "आवेदन पोर्टल (Portal) ↗" else "Official Portal ↗")
                            }
                        }
                    }
                }
            }
        }
    }

    // Share Confirmation Snackbar / Notification
    showShareConfirmationMessage?.let { msg ->
        LaunchedEffect(msg) {
            kotlinx.coroutines.delay(2500)
            showShareConfirmationMessage = null
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Surface(
                color = Color(0xFF1E293B),
                shape = RoundedCornerShape(8.dp),
                shadowElevation = 6.dp
            ) {
                Text(
                    text = msg,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                )
            }
        }
    }
}

// -------------------------------------------------------------
// REUSABLE SUB-COMPONENTS
// -------------------------------------------------------------

@Composable
fun FilterSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.Bold,
            color = GovNavyPrimary,
            fontSize = 12.sp
        )
    )
}

@Composable
fun DetailSectionBlock(title: String, content: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = GovNavyPrimary,
                fontSize = 13.sp
            )
        )
        Text(
            text = content,
            style = MaterialTheme.typography.bodySmall.copy(
                color = Color(0xFF334155),
                lineHeight = 17.sp
            )
        )
    }
}

/**
 * Clean Material 3 Card Representing a Government Welfare Scheme
 */
@Composable
fun SchemeCard(
    scheme: SchemeItem,
    currentLanguage: AppLanguage,
    fontSizeMultiplier: Float,
    isBookmarked: Boolean,
    onToggleBookmark: () -> Unit,
    onOpenDetails: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isCentral = scheme.jurisdiction == SchemeJurisdiction.CENTRAL

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
            // Top Row: Category Icon, Level Badge, Bookmark
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
                        color = if (isCentral) GovNavyContainer else Color(0xFFFFEDD5),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.size(34.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = scheme.icon,
                                contentDescription = null,
                                tint = if (isCentral) GovNavyPrimary else Color(0xFFC2410C),
                                modifier = Modifier.size(20.dp)
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
                                    text = if (isCentral) "🇮🇳 Central Scheme" else "🏛️ ${scheme.stateNameEn ?: "State"} Scheme",
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isCentral) GovNavyPrimary else Color(0xFF92400E),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp
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
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color(0xFF15803D),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        )
                                    )
                                }
                            }
                        }

                        Text(
                            text = scheme.categoryNameEn,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF64748B),
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                // Actions: Share + Bookmark
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    IconButton(onClick = onShare, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(onClick = onToggleBookmark, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (isBookmarked) GovSaffron else Color(0xFF64748B),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Scheme Title
            Text(
                text = scheme.getTitle(currentLanguage),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = GovNavyPrimary,
                    fontSize = (15 * fontSizeMultiplier).sp,
                    lineHeight = 19.sp
                )
            )

            // Ministry / Dept
            Text(
                text = scheme.getMinistry(currentLanguage),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF64748B),
                    fontSize = (11 * fontSizeMultiplier).sp
                )
            )

            // Financial Benefit Banner
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
                        text = scheme.getFinancialBenefit(currentLanguage),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF14532D),
                            fontWeight = FontWeight.Bold,
                            fontSize = (11.5f * fontSizeMultiplier).sp,
                            lineHeight = 15.sp
                        )
                    )
                }
            }

            // Tags Row: Gender, Age Bracket, Benefit Type
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    color = Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI) scheme.benefitType.displayNameHi else scheme.benefitType.displayNameEn,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF475569),
                            fontSize = 10.sp
                        )
                    )
                }

                Surface(
                    color = Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI) scheme.targetGender.displayNameHi else scheme.targetGender.displayNameEn,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF475569),
                            fontSize = 10.sp
                        )
                    )
                }

                Surface(
                    color = Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI) scheme.targetAgeBracket.displayNameHi else scheme.targetAgeBracket.displayNameEn,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF475569),
                            fontSize = 10.sp
                        )
                    )
                }
            }

            // Eligibility Snippet
            Text(
                text = "Eligibility: ${scheme.getEligibilitySummary(currentLanguage)}",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF475569),
                    fontSize = (11 * fontSizeMultiplier).sp,
                    lineHeight = 15.sp
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Bottom Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Launch Year: ${scheme.launchYear}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFF94A3B8),
                        fontSize = 10.sp
                    )
                )

                TextButton(
                    onClick = onOpenDetails,
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI) "विस्तार व आवेदन देखें →" else "View Details & Apply →",
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
