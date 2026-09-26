package org.jansaarthi.app.ui.screens.schemes

import androidx.compose.animation.*
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.FactCheck
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
 * JanSaarthi "Find Schemes For Me" Screen (Frontend UI Only)
 * ----------------------------------------------------------------------------
 * Intelligent citizen matching portal displaying user's profile traits as
 * selectable/filterable chips to dynamically calculate tailored entitlements.
 *
 * 4 Mandated Core Sections:
 * 1. Your Profile (Interactive trait chips: Age, Gender, State, Landholding, Income, Category)
 * 2. Matching Criteria (Algorithmic logic summary, match percentage, preset shortcuts)
 * 3. Schemes You May Be Eligible For (Result cards with "Why it may match", Benefit, Gov Level, Status)
 * 4. View All (Directory expansion trigger to inspect lower matches & master catalog)
 *
 * Frontend UI Only: Uses realistic local mock matching logic.
 * No backend, database, Firebase, or external API code.
 */

// -------------------------------------------------------------
// DATA MODELS FOR PROFILE TRAITS & MATCHED SCHEMES
// -------------------------------------------------------------

data class ProfileTraitChip(
    val id: String,
    val categoryLabelEn: String,
    val categoryLabelHi: String,
    val valueLabelEn: String,
    val valueLabelHi: String,
    val icon: ImageVector,
    val isEnabled: Boolean = true
)

enum class MatchGovLevel(val labelEn: String, val labelHi: String, val icon: ImageVector) {
    CENTRAL("Central Government", "केंद्र सरकार", Icons.Default.AccountBalance),
    STATE("State Government", "राज्य सरकार", Icons.Default.Apartment),
    UT("Union Territory", "केंद्र शासित प्रदेश", Icons.Default.LocationCity)
}

enum class MatchConfidence(
    val labelEn: String,
    val labelHi: String,
    val scoreText: String,
    val badgeColor: Color,
    val bgColor: Color
) {
    HIGH_MATCH("✔ Highly Eligible", "✔ उच्च पात्रता", "100% Match", Color(0xFF15803D), Color(0xFFDCFCE7)),
    FAMILY_MATCH("👨‍👩‍👧 Family Benefit", "👨‍👩‍👧 परिवार लाभ", "95% Match", Color(0xFF0369A1), Color(0xFFE0F2FE)),
    CONDITIONAL_MATCH("⚠ Check Criteria", "⚠ शर्तें लागू", "85% Match", Color(0xFFB45309), Color(0xFFFEF3C7)),
    UNIVERSAL_MATCH("🌐 Universal Citizen", "🌐 सभी नागरिक", "90% Match", Color(0xFF4338CA), Color(0xFFEEF2FF))
}

data class MatchedSchemeItem(
    val id: String,
    val nameEn: String,
    val nameHi: String,
    val govLevel: MatchGovLevel,
    val stateOrUtName: String,
    val ministryOrDeptEn: String,
    val ministryOrDeptHi: String,
    val benefitHighlightEn: String,
    val benefitHighlightHi: String,
    val whyItMatchesEn: String,
    val whyItMatchesHi: String,
    val status: MatchConfidence,
    val requiredTraits: Set<String>, // trait IDs required for active match
    val isDbtEnabled: Boolean = true,
    val overviewEn: String,
    val overviewHi: String,
    val keyGuarantees: List<String>,
    val requiredDocuments: List<String>,
    val applicationSteps: List<String>,
    val officialPortalUrl: String
) {
    fun getName(lang: AppLanguage): String = if (lang == AppLanguage.HINDI) nameHi else nameEn
    fun getBenefit(lang: AppLanguage): String = if (lang == AppLanguage.HINDI) benefitHighlightHi else benefitHighlightEn
    fun getWhyItMatches(lang: AppLanguage): String = if (lang == AppLanguage.HINDI) whyItMatchesHi else whyItMatchesEn
    fun getMinistry(lang: AppLanguage): String = if (lang == AppLanguage.HINDI) ministryOrDeptHi else ministryOrDeptEn
    fun getOverview(lang: AppLanguage): String = if (lang == AppLanguage.HINDI) overviewHi else overviewEn
}

// -------------------------------------------------------------
// REALISTIC LOCAL MOCK MATCHED DATASET
// -------------------------------------------------------------

val REALISTIC_MATCHED_SCHEMES_MOCK: List<MatchedSchemeItem> = listOf(
    MatchedSchemeItem(
        id = "MATCH-01",
        nameEn = "PM Kisan Samman Nidhi (PM-KISAN)",
        nameHi = "प्रधानमंत्री किसान सम्मान निधि",
        govLevel = MatchGovLevel.CENTRAL,
        stateOrUtName = "National (All India)",
        ministryOrDeptEn = "Ministry of Agriculture & Farmers Welfare",
        ministryOrDeptHi = "कृषि एवं किसान कल्याण मंत्रालय",
        benefitHighlightEn = "₹6,000/year via direct DBT in 3 equal installments of ₹2,000",
        benefitHighlightHi = "₹6,000 प्रति वर्ष 3 समान किस्तों में सीधे बैंक खाते में (डीबीटी)",
        whyItMatchesEn = "Matches your Small & Marginal Farmer status + Rural residence + Aadhaar e-KYC linked.",
        whyItMatchesHi = "आपके छोटे व सीमांत किसान होने, ग्रामीण निवास और आधार लिंक बैंक खाते से 100% मेल खाती है।",
        status = MatchConfidence.HIGH_MATCH,
        requiredTraits = setOf("TRAIT_FARMER", "TRAIT_RURAL"),
        isDbtEnabled = true,
        overviewEn = "Direct income support of ₹6,000 per year paid in three equal installments directly into Aadhaar-seeded bank accounts of all landholder farmer families.",
        overviewHi = "देशभर के सभी भूमिधारक किसान परिवारों को ₹6,000 प्रति वर्ष की प्रत्यक्ष आय सहायता।",
        keyGuarantees = listOf("100% Central Government funding without middlemen", "Transfer via Public Financial Management System (PFMS)", "Over 11 crore farmers currently enrolled"),
        requiredDocuments = listOf("Aadhaar Card", "Land record 7/12 extract or Khasra", "Active Bank Passbook"),
        applicationSteps = listOf("Visit pmkisan.gov.in or Common Service Centre (CSC)", "Complete biometric e-KYC authentication", "Verify land record linkage with village patwari"),
        officialPortalUrl = "https://pmkisan.gov.in"
    ),
    MatchedSchemeItem(
        id = "MATCH-02",
        nameEn = "Mukhyamantri Majhi Ladki Bahin Yojana",
        nameHi = "मुख्यमंत्री माझी लाड़की बहिन योजना",
        govLevel = MatchGovLevel.STATE,
        stateOrUtName = "Maharashtra Resident",
        ministryOrDeptEn = "Dept of Women & Child Development (Govt of Maharashtra)",
        ministryOrDeptHi = "महिला एवं बाल विकास विभाग (महाराष्ट्र शासन)",
        benefitHighlightEn = "₹1,500/month (₹18,000/year) direct cash transfer to Aadhaar bank account",
        benefitHighlightHi = "प्रति माह ₹1,500 (वार्षिक ₹18,000) आधार से जुड़े बैंक खाते में सीधा अंतरण",
        whyItMatchesEn = "Matches your Female gender + Maharashtra domicile + Annual family income under ₹2.5 Lakhs.",
        whyItMatchesHi = "आपकी महिला श्रेणी, महाराष्ट्र राज्य अधिवास और ₹2.5 लाख से कम वार्षिक पारिवारिक आय से पूरी तरह मेल खाती है।",
        status = MatchConfidence.HIGH_MATCH,
        requiredTraits = setOf("TRAIT_FEMALE", "TRAIT_STATE", "TRAIT_INCOME_LOW"),
        isDbtEnabled = true,
        overviewEn = "Flagship socio-economic financial empowerment scheme by Government of Maharashtra providing ₹1,500 per month directly to eligible women heads of families.",
        overviewHi = "महाराष्ट्र शासन की महत्वाकांक्षी योजना, पात्र महिलाओं को आर्थिक स्वावलंबन हेतु ₹1,500 मासिक सम्मान राशि।",
        keyGuarantees = listOf("Monthly ₹1,500 credit on the 1st week of each month", "Direct Aadhaar DBT without intermediaries", "Universal cover for eligible women aged 21 to 65"),
        requiredDocuments = listOf("Aadhaar Card", "Ration Card (Yellow/Orange)", "Income Certificate (< ₹2.5L)", "Maharashtra Domicile Certificate"),
        applicationSteps = listOf("Download Nari Shakti Doot App or visit Setu Suvidha Kendra", "Upload Aadhaar and Ration Card", "District validation and immediate monthly disbursal"),
        officialPortalUrl = "https://ladkibahin.maharashtra.gov.in"
    ),
    MatchedSchemeItem(
        id = "MATCH-03",
        nameEn = "Namo Shetkari Mahasanman Nidhi Yojana",
        nameHi = "नमो शेतकरी महासंमान निधि योजना",
        govLevel = MatchGovLevel.STATE,
        stateOrUtName = "Maharashtra Resident",
        ministryOrDeptEn = "Department of Agriculture (Govt of Maharashtra)",
        ministryOrDeptHi = "कृषि विभाग (महाराष्ट्र शासन)",
        benefitHighlightEn = "₹6,000/year additional state top-up (Total ₹12,000/year along with PM-KISAN)",
        benefitHighlightHi = "₹6,000 प्रति वर्ष अतिरिक्त राज्य सहायता (पीएम-किसान के साथ कुल ₹12,000/वर्ष)",
        whyItMatchesEn = "Matches active PM-KISAN beneficiary status + Maharashtra farmer agricultural landholding.",
        whyItMatchesHi = "सक्रिय पीएम-किसान लाभार्थी स्थिति और महाराष्ट्र में कृषि भूमि धारक होने से सीधे मेल खाती है।",
        status = MatchConfidence.HIGH_MATCH,
        requiredTraits = setOf("TRAIT_FARMER", "TRAIT_STATE"),
        isDbtEnabled = true,
        overviewEn = "Special state government welfare top-up matching Central PM-KISAN, granting an additional ₹6,000 annually to every verified farmer in Maharashtra.",
        overviewHi = "महाराष्ट्र के किसानों को केंद्र के ₹6,000 के अतिरिक्त ₹6,000 प्रति वर्ष की राज्य सहायता।",
        keyGuarantees = listOf("Double income support: ₹6,000 Central + ₹6,000 State", "Automatic qualification for verified PM-KISAN farmers", "Paid synchronously with central tranches"),
        requiredDocuments = listOf("PM-KISAN Registration ID", "Aadhaar Card", "7/12 Land Record Extract"),
        applicationSteps = listOf("No separate registration needed for active PM-KISAN beneficiaries", "Check DBT status on mahadbt.maharashtra.gov.in"),
        officialPortalUrl = "https://mahadbt.maharashtra.gov.in"
    ),
    MatchedSchemeItem(
        id = "MATCH-04",
        nameEn = "Ayushman Bharat PM-JAY (Golden Card)",
        nameHi = "आयुष्मान भारत प्रधानमंत्री जन आरोग्य योजना",
        govLevel = MatchGovLevel.CENTRAL,
        stateOrUtName = "National (All India)",
        ministryOrDeptEn = "Ministry of Health & Family Welfare",
        ministryOrDeptHi = "स्वास्थ्य एवं परिवार कल्याण मंत्रालय",
        benefitHighlightEn = "₹5,00,000 cashless family hospitalisation cover per year across India",
        benefitHighlightHi = "प्रति परिवार प्रति वर्ष ₹5,00,000 तक का मुफ़्त कैशलेस अस्पताल उपचार",
        whyItMatchesEn = "Matches your Low Income bracket (< ₹2.5 Lakhs) + SECC rural deprivation indicators.",
        whyItMatchesHi = "आपके परिवार की ₹2.5 लाख से कम आय और एसईसीसी ग्रामीण वंचना मानकों से मेल खाती है।",
        status = MatchConfidence.FAMILY_MATCH,
        requiredTraits = setOf("TRAIT_INCOME_LOW"),
        isDbtEnabled = false,
        overviewEn = "World's largest government-financed healthcare assurance scheme providing ₹5 Lakh cashless coverage for secondary and tertiary inpatient treatments.",
        overviewHi = "विश्व की सबसे बड़ी सरकारी स्वास्थ्य योजना, 27,000+ अस्पतालों में कैशलेस इलाज।",
        keyGuarantees = listOf("Covers 1,949 treatments, surgeries, and diagnostics", "Cashless paperless treatment at all empanelled hospitals", "Includes pre-existing diseases from day one"),
        requiredDocuments = listOf("Aadhaar Card", "Ration Card (NFSA)"),
        applicationSteps = listOf("Search beneficiary status at beneficiary.nha.gov.in", "Complete e-KYC using mobile OTP or fingerprint", "Download instant PVC Ayushman Golden Card"),
        officialPortalUrl = "https://pmjay.gov.in"
    ),
    MatchedSchemeItem(
        id = "MATCH-05",
        nameEn = "PM Vishwakarma Kaushal Samman",
        nameHi = "प्रधानमंत्री विश्वकर्मा कौशल सम्मान",
        govLevel = MatchGovLevel.CENTRAL,
        stateOrUtName = "National (All India)",
        ministryOrDeptEn = "Ministry of MSME",
        ministryOrDeptHi = "सूक्ष्म, लघु एवं मध्यम उद्यम मंत्रालय",
        benefitHighlightEn = "₹15,000 free toolkit grant + collateral-free business loans up to ₹3 Lakhs at 5%",
        benefitHighlightHi = "₹15,000 का मुफ़्त आधुनिक टूलकिट अनुदान + 5% रियायती ब्याज पर ₹3 लाख तक ऋण",
        whyItMatchesEn = "Matches your Self-Employed artisan status + OBC Social Category in rural area.",
        whyItMatchesHi = "आपके स्व-रोजगार / कारीगर व्यवसाय, ओबीसी सामाजिक श्रेणी और ग्रामीण निवास से मेल खाती है।",
        status = MatchConfidence.HIGH_MATCH,
        requiredTraits = setOf("TRAIT_SELF_EMPLOYED", "TRAIT_OBC"),
        isDbtEnabled = true,
        overviewEn = "End-to-end holistic support for traditional artisans and craftspeople working with their hands and tools across 18 family-based trades.",
        overviewHi = "पारंपरिक शिल्पकारों व कारीगरों को कौशल प्रशिक्षण, मुफ़्त टूलकिट व रियायती ऋण सहायता।",
        keyGuarantees = listOf("Basic & advanced skill training with ₹500/day stipend", "₹15,000 digital e-voucher for modern toolkits", "Collateral-free credit: ₹1 Lakh first tranche + ₹2 Lakhs second tranche"),
        requiredDocuments = listOf("Aadhaar Card", "Bank Account Details", "Trade Declaration"),
        applicationSteps = listOf("Register at nearest Common Service Centre (CSC)", "Three-tier verification (Gram Panchayat / Urban Local Body)", "Skill training and loan disbursal via MSME portal"),
        officialPortalUrl = "https://pmvishwakarma.gov.in"
    ),
    MatchedSchemeItem(
        id = "MATCH-06",
        nameEn = "Pradhan Mantri Awas Yojana - Gramin (PMAY-G)",
        nameHi = "प्रधानमंत्री आवास योजना - ग्रामीण",
        govLevel = MatchGovLevel.CENTRAL,
        stateOrUtName = "National (All India)",
        ministryOrDeptEn = "Ministry of Rural Development",
        ministryOrDeptHi = "ग्रामीण विकास मंत्रालय",
        benefitHighlightEn = "₹1,20,000 direct housing grant + 90 days MGNREGA wages + ₹12,000 toilet aid",
        benefitHighlightHi = "पक्का मकान निर्माण हेतु ₹1,20,000 सीधा अनुदान + 90 दिन मजदूरी + ₹12,000 शौचालय सहायता",
        whyItMatchesEn = "Matches your Rural area residence + low annual income category (< ₹2.5 Lakhs).",
        whyItMatchesHi = "आपके ग्रामीण निवास और कम वार्षिक पारिवारिक आय (< ₹2.5 लाख) से मेल खाती है।",
        status = MatchConfidence.CONDITIONAL_MATCH,
        requiredTraits = setOf("TRAIT_RURAL", "TRAIT_INCOME_LOW"),
        isDbtEnabled = true,
        overviewEn = "Financial assistance to houseless and kutcha house living families in rural areas for the construction of a permanent pucca house with basic amenities.",
        overviewHi = "ग्रामीण क्षेत्रों में कच्चे मकानों में रहने वाले परिवारों को पक्का मकान बनाने हेतु वित्तीय अनुदान।",
        keyGuarantees = listOf("Direct DBT transfer in 3 geo-tagged construction stages", "Integrated with Swachh Bharat toilet assistance and Ujjwala gas", "Minimum 25 sq. m. dwelling area with hygienic cooking space"),
        requiredDocuments = listOf("Aadhaar Card", "Job Card (MGNREGA)", "Bank Account Details", "Kutcha house photo"),
        applicationSteps = listOf("Name verification in Gram Sabha priority list (Awaas+)", "Geo-tagging of construction plot by village officer", "DBT tranches released into bank account"),
        officialPortalUrl = "https://pmayg.nic.in"
    ),
    MatchedSchemeItem(
        id = "MATCH-07",
        nameEn = "Pradhan Mantri Mudra Yojana (PMMY)",
        nameHi = "प्रधानमंत्री मुद्रा योजना",
        govLevel = MatchGovLevel.CENTRAL,
        stateOrUtName = "National (All India)",
        ministryOrDeptEn = "Ministry of Finance",
        ministryOrDeptHi = "वित्त मंत्रालय",
        benefitHighlightEn = "Collateral-free enterprise loans up to ₹10 Lakhs (Shishu, Kishore, Tarun)",
        benefitHighlightHi = "बिना किसी गारंटी के ₹10 लाख तक का व्यवसाय ऋण (शिशु, किशोर, तरुण)",
        whyItMatchesEn = "Matches your Self-Employed status + age bracket for micro-enterprise funding.",
        whyItMatchesHi = "आपके स्व-रोजगार और सूक्ष्म व्यवसाय विस्तार की जरूरतों से मेल खाती है।",
        status = MatchConfidence.CONDITIONAL_MATCH,
        requiredTraits = setOf("TRAIT_SELF_EMPLOYED", "TRAIT_AGE"),
        isDbtEnabled = false,
        overviewEn = "Enables non-corporate, non-farm small and micro-enterprises to access collateral-free institutional credit up to ₹10 Lakhs.",
        overviewHi = "लघु एवं सूक्ष्म उद्यमियों को व्यापार शुरू करने या विस्तार करने हेतु बैंक ऋण।",
        keyGuarantees = listOf("Shishu: Loans up to ₹50,000", "Kishore: ₹50,000 to ₹5 Lakhs", "Tarun: ₹5 Lakhs to ₹10 Lakhs"),
        requiredDocuments = listOf("Aadhaar & PAN Card", "Udyam Registration", "Business Project Report", "6 Months Bank Statement"),
        applicationSteps = listOf("Apply online at udyamimitra.in or visit any public/private bank branch", "Submit quotation and business proof", "Loan sanction and Mudra RuPay card issuance"),
        officialPortalUrl = "https://www.mudra.org.in"
    ),
    MatchedSchemeItem(
        id = "MATCH-08",
        nameEn = "Sukanya Samriddhi Yojana (SSY)",
        nameHi = "सुकन्या समृद्धि योजना",
        govLevel = MatchGovLevel.CENTRAL,
        stateOrUtName = "National (All India)",
        ministryOrDeptEn = "Ministry of Finance",
        ministryOrDeptHi = "वित्त मंत्रालय",
        benefitHighlightEn = "Highest sovereign 8.2% tax-free compound interest + Section 80C tax deduction",
        benefitHighlightHi = "सर्वोच्च 8.2% कर-मुक्त चक्रवृद्धि ब्याज + परिपक्वता पर पूर्ण कर छूट (80C)",
        whyItMatchesEn = "Matches female welfare criteria for families investing in daughters' education and marriage.",
        whyItMatchesHi = "बेटियों की उच्च शिक्षा व भविष्य की आर्थिक सुरक्षा हेतु विशेष बचत योजना से मेल खाती है।",
        status = MatchConfidence.FAMILY_MATCH,
        requiredTraits = setOf("TRAIT_FEMALE"),
        isDbtEnabled = false,
        overviewEn = "Government of India backed small deposit savings scheme launched under Beti Bachao Beti Padhao campaign offering guaranteed highest returns.",
        overviewHi = "बेटी बचाओ बेटी पढ़ाओ अभियान के अंतर्गत बालिकाओं के लिए विशेष बचत खाता।",
        keyGuarantees = listOf("Triple tax exemption (EEE) on deposit, interest, and maturity", "Partial 50% withdrawal allowed after 18 years for higher studies", "Account transferable across any post office or bank branch in India"),
        requiredDocuments = listOf("Birth certificate of girl child", "Parent/Guardian Aadhaar and PAN", "Passport size photograph"),
        applicationSteps = listOf("Visit nearest Post Office or authorized commercial bank branch", "Fill Form-1 with initial deposit (min ₹250)", "Receive passbook; deposit annually for 15 years"),
        officialPortalUrl = "https://www.indiapost.gov.in"
    ),
    MatchedSchemeItem(
        id = "MATCH-09",
        nameEn = "Mahatma Jyotirao Phule Jan Arogya Yojana (MJPJAY)",
        nameHi = "महात्मा ज्योतिराव फुले जन आरोग्य योजना",
        govLevel = MatchGovLevel.STATE,
        stateOrUtName = "Maharashtra Resident",
        ministryOrDeptEn = "Public Health Department (Govt of Maharashtra)",
        ministryOrDeptHi = "सार्वजनिक स्वास्थ्य विभाग (महाराष्ट्र शासन)",
        benefitHighlightEn = "₹5,00,000 cashless secondary and tertiary hospitalisation cover across Maharashtra",
        benefitHighlightHi = "महाराष्ट्र के 1,000+ अस्पतालों में ₹5,00,000 तक मुफ़्त शल्यक्रिया व अस्पताल उपचार",
        whyItMatchesEn = "Matches your Maharashtra state residency + Ration Card holder status.",
        whyItMatchesHi = "आपके महाराष्ट्र राज्य निवासी होने और राशन कार्ड धारक होने के आधार पर 100% लागू।",
        status = MatchConfidence.HIGH_MATCH,
        requiredTraits = setOf("TRAIT_STATE", "TRAIT_INCOME_LOW"),
        isDbtEnabled = false,
        overviewEn = "Flagship health assurance scheme of the Maharashtra Government providing cashless secondary and tertiary medical treatment up to ₹5 Lakhs per family per year.",
        overviewHi = "महाराष्ट्र शासन की राज्य स्वास्थ्य योजना, प्रति परिवार ₹5 लाख तक का कैशलेस इलाज।",
        keyGuarantees = listOf("Covers 1,356 medical and surgical therapies", "Dedicated Arogyamitra assistance at hospital desks", "Integrated seamlessly with Central Ayushman Bharat"),
        requiredDocuments = listOf("Ration Card (Yellow/Orange/White)", "Aadhaar Card or Voter ID"),
        applicationSteps = listOf("Visit any empanelled hospital network desk", "Meet Arogyamitra with Ration Card and Aadhaar", "Direct cashless treatment admission"),
        officialPortalUrl = "https://www.jeevandayee.gov.in"
    ),
    MatchedSchemeItem(
        id = "MATCH-10",
        nameEn = "Atal Pension Yojana (APY)",
        nameHi = "अटल पेंशन योजना",
        govLevel = MatchGovLevel.CENTRAL,
        stateOrUtName = "National (All India)",
        ministryOrDeptEn = "Ministry of Finance",
        ministryOrDeptHi = "वित्त मंत्रालय",
        benefitHighlightEn = "Guaranteed lifelong monthly pension of ₹1,000 to ₹5,000 after age 60",
        benefitHighlightHi = "60 वर्ष की आयु के बाद ₹1,000 से ₹5,000 तक आजीवन सुनिश्चित मासिक पेंशन",
        whyItMatchesEn = "Matches your age (28 years, within 18-40 entry window) + self-employed unorganized sector.",
        whyItMatchesHi = "आपकी आयु (28 वर्ष, 18-40 पात्रता सीमा) और असंगठित क्षेत्र से पूरी तरह मेल खाती है।",
        status = MatchConfidence.UNIVERSAL_MATCH,
        requiredTraits = setOf("TRAIT_AGE"),
        isDbtEnabled = true,
        overviewEn = "Government-guaranteed pension scheme administered by PFRDA focused on all citizens in the unorganised sector to provide lifelong old age financial security.",
        overviewHi = "असंगठित क्षेत्र के नागरिकों को बुढ़ापे में नियमित आय सुरक्षा हेतु सरकारी पेंशन योजना।",
        keyGuarantees = listOf("Fixed monthly pension of ₹1,000 to ₹5,000 chosen by subscriber", "Same pension to spouse upon subscriber's demise", "Accumulated pension corpus returned to nominee"),
        requiredDocuments = listOf("Savings Bank Account", "Aadhaar Card", "Active Mobile Number"),
        applicationSteps = listOf("Log in to internet banking or visit your bank branch", "Fill auto-debit consent form with chosen pension amount", "PRAN card issued immediately"),
        officialPortalUrl = "https://www.npscra.nsdl.co.in"
    )
)

// -------------------------------------------------------------
// MAIN "FIND SCHEMES FOR ME" COMPOSABLE
// -------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FindSchemesForMeScreen(
    currentLanguage: AppLanguage = AppLanguage.HINDI,
    onSelectLanguage: (AppLanguage) -> Unit = {},
    isLargeFont: Boolean = false,
    onFontScaleToggle: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onEditProfile: () -> Unit = {},
    onViewAllSchemes: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val fontSizeMultiplier = if (isLargeFont) 1.15f else 1.0f

    // -------------------------------------------------------------
    // PROFILE TRAITS AS SELECTABLE / FILTERABLE CHIPS
    // -------------------------------------------------------------
    val initialTraits = remember {
        listOf(
            ProfileTraitChip("TRAIT_AGE", "Age", "आयु", "28 Years", "28 वर्ष", Icons.Default.Cake, true),
            ProfileTraitChip("TRAIT_FEMALE", "Gender", "लिंग", "Female", "महिला", Icons.Default.Person, true),
            ProfileTraitChip("TRAIT_STATE", "State", "राज्य", "Maharashtra", "महाराष्ट्र", Icons.Default.LocationOn, true),
            ProfileTraitChip("TRAIT_RURAL", "Residence", "क्षेत्र", "Rural", "ग्रामीण", Icons.Default.Agriculture, true),
            ProfileTraitChip("TRAIT_FARMER", "Occupation", "व्यवसाय", "Farmer (< 2 Ha)", "किसान (< 2 हे.)", Icons.Default.Grass, true),
            ProfileTraitChip("TRAIT_INCOME_LOW", "Income", "पारिवारिक आय", "< ₹2.5 Lakhs", "< ₹2.5 लाख", Icons.Default.CurrencyRupee, true),
            ProfileTraitChip("TRAIT_OBC", "Category", "सामाजिक श्रेणी", "OBC", "ओबीसी", Icons.Default.Group, true),
            ProfileTraitChip("TRAIT_SELF_EMPLOYED", "Employment", "रोजगार", "Self-Employed", "स्व-रोजगार", Icons.Default.BusinessCenter, true)
        )
    }

    // Set of active trait IDs toggled by citizen
    var activeTraitIds by remember {
        mutableStateOf(initialTraits.map { it.id }.toSet())
    }

    // Modal Sheet Controllers
    var selectedSchemeForDetails by remember { mutableStateOf<MatchedSchemeItem?>(null) }
    var bookmarkedSchemeIds by remember { mutableStateOf(setOf("MATCH-01", "MATCH-02")) }
    var showAllMatchesExpanded by remember { mutableStateOf(false) }

    // -------------------------------------------------------------
    // MATCHING CALCULATION ENGINE
    // -------------------------------------------------------------
    val matchedSchemes = remember(activeTraitIds, showAllMatchesExpanded) {
        REALISTIC_MATCHED_SCHEMES_MOCK.filter { scheme ->
            if (showAllMatchesExpanded) {
                true // Show all in view all mode
            } else {
                // Scheme matches if at least one of its required traits is currently active
                // and none of its strictly contradictory traits are absent
                val matchesTraits = scheme.requiredTraits.isEmpty() ||
                        scheme.requiredTraits.any { activeTraitIds.contains(it) }
                matchesTraits
            }
        }.sortedWith(
            compareByDescending<MatchedSchemeItem> { scheme ->
                // Sort by count of matching active traits
                scheme.requiredTraits.count { activeTraitIds.contains(it) }
            }.thenBy { it.status.ordinal }
        )
    }

    // Calculate dynamic profile match score
    val matchPercentage = remember(activeTraitIds) {
        val activeCount = activeTraitIds.size
        val totalCount = initialTraits.size
        if (totalCount == 0) 100 else ((activeCount.toFloat() / totalCount.toFloat()) * 100).toInt()
    }

    Scaffold(
        topBar = {
            Column {
                // National Banner
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
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) "मेरे लिए सरकारी योजनाएं" else "Find Schemes For Me",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GovNavyPrimary,
                                        fontSize = (16 * fontSizeMultiplier).sp
                                    )
                                )
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI)
                                        "व्यक्तिगत प्रोफ़ाइल मिलान इंजन • myScheme"
                                    else
                                        "Personalized Citizen Eligibility Engine • myScheme",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF64748B),
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        // Match Score Badge
                        Surface(
                            color = Color(0xFFDCFCE7),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFF86EFAC))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color(0xFF15803D),
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = "$matchPercentage% Match",
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
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Accessibility Controls
            item {
                AccessibilityBar(
                    currentLanguage = currentLanguage,
                    onSelectLanguage = onSelectLanguage,
                    isLargeFont = isLargeFont,
                    onFontScaleToggle = onFontScaleToggle
                )
            }

            // Prototype Evaluation Disclaimer Banner
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
                                "📌 यूआई प्रदर्शन: प्रोफ़ाइल चिप्स पर टैप करके योजना मिलान की जांच करें। यह स्थानीय मॉक मिलान इंजन है।"
                            else
                                "📌 UI Demonstration: Tap profile chips to toggle criteria & observe real-time scheme matching. Local mock engine.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF92400E),
                                fontSize = 11.sp,
                                lineHeight = 14.sp
                            )
                        )
                    }
                }
            }

            // -------------------------------------------------------------
            // SECTION 1: YOUR PROFILE (SELECTABLE / FILTERABLE CHIPS)
            // -------------------------------------------------------------
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Section Header with Edit CTA
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
                                    Icon(
                                        imageVector = Icons.Default.Badge,
                                        contentDescription = null,
                                        tint = GovNavyPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = if (currentLanguage == AppLanguage.HINDI) "1. आपकी प्रोफ़ाइल (Your Profile)" else "1. Your Profile",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = GovNavyPrimary,
                                            fontSize = (15 * fontSizeMultiplier).sp
                                        )
                                    )
                                }
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI)
                                        "चिप्स पर टैप करके पात्रता शर्तों को चालू/बंद करें"
                                    else
                                        "Tap traits below to toggle active matching filters",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF64748B),
                                        fontSize = 11.sp
                                    )
                                )
                            }

                            TextButton(
                                onClick = onEditProfile,
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) "संपादित करें ✎" else "Edit Profile ✎",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = GovNavyPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }

                        // Selectable Profile Traits Chips Flow / Row
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI) "सक्रिय व्यक्तिगत विशेषताएं (${activeTraitIds.size}/${initialTraits.size}):" else "Active Personal Traits (${activeTraitIds.size}/${initialTraits.size}):",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF475569),
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.5.sp
                            )
                        )

                        // 2-row horizontal or flex chips
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            // Row 1: Primary traits
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(initialTraits.take(4)) { trait ->
                                    val isSelected = activeTraitIds.contains(trait.id)
                                    val label = if (currentLanguage == AppLanguage.HINDI)
                                        "${trait.categoryLabelHi}: ${trait.valueLabelHi}"
                                    else
                                        "${trait.categoryLabelEn}: ${trait.valueLabelEn}"

                                    FilterChip(
                                        selected = isSelected,
                                        onClick = {
                                            activeTraitIds = if (isSelected) {
                                                activeTraitIds - trait.id
                                            } else {
                                                activeTraitIds + trait.id
                                            }
                                        },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = if (isSelected) Icons.Default.Check else trait.icon,
                                                contentDescription = null,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        },
                                        label = {
                                            Text(
                                                text = label,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                    fontSize = 11.sp
                                                )
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = GovNavyPrimary,
                                            selectedLabelColor = Color.White,
                                            selectedLeadingIconColor = Color.White,
                                            containerColor = Color(0xFFF1F5F9),
                                            labelColor = Color(0xFF334155),
                                            iconColor = Color(0xFF64748B)
                                        ),
                                        border = FilterChipDefaults.filterChipBorder(
                                            enabled = true,
                                            selected = isSelected,
                                            borderColor = if (isSelected) GovNavyPrimary else Color(0xFFCBD5E1)
                                        )
                                    )
                                }
                            }

                            // Row 2: Secondary traits
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(initialTraits.drop(4)) { trait ->
                                    val isSelected = activeTraitIds.contains(trait.id)
                                    val label = if (currentLanguage == AppLanguage.HINDI)
                                        "${trait.categoryLabelHi}: ${trait.valueLabelHi}"
                                    else
                                        "${trait.categoryLabelEn}: ${trait.valueLabelEn}"

                                    FilterChip(
                                        selected = isSelected,
                                        onClick = {
                                            activeTraitIds = if (isSelected) {
                                                activeTraitIds - trait.id
                                            } else {
                                                activeTraitIds + trait.id
                                            }
                                        },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = if (isSelected) Icons.Default.Check else trait.icon,
                                                contentDescription = null,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        },
                                        label = {
                                            Text(
                                                text = label,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                    fontSize = 11.sp
                                                )
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = GovNavyPrimary,
                                            selectedLabelColor = Color.White,
                                            selectedLeadingIconColor = Color.White,
                                            containerColor = Color(0xFFF1F5F9),
                                            labelColor = Color(0xFF334155),
                                            iconColor = Color(0xFF64748B)
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
                }
            }

            // -------------------------------------------------------------
            // SECTION 2: MATCHING CRITERIA
            // -------------------------------------------------------------
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
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
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.FactCheck,
                                    contentDescription = null,
                                    tint = Color(0xFF0F766E),
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) "2. मिलान मानदंड (Matching Criteria)" else "2. Matching Criteria",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GovNavyPrimary,
                                        fontSize = (15 * fontSizeMultiplier).sp
                                    )
                                )
                            }

                            // Match Score Pill
                            Surface(
                                color = Color(0xFFEFF6FF),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "${activeTraitIds.size} Active Rules",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = GovNavyPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }

                        // Algorithmic summary explanation
                        Surface(
                            color = Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                val summaryRules = mutableListOf<String>()
                                if (activeTraitIds.contains("TRAIT_STATE")) summaryRules.add("Includes Central + Maharashtra State schemes")
                                if (activeTraitIds.contains("TRAIT_FEMALE")) summaryRules.add("Evaluates Women & Motherhood entitlements")
                                if (activeTraitIds.contains("TRAIT_FARMER")) summaryRules.add("Searches direct agricultural DBT & irrigation grants")
                                if (activeTraitIds.contains("TRAIT_INCOME_LOW")) summaryRules.add("Prioritizes BPL/low-income assistance (< ₹2.5L)")

                                if (summaryRules.isEmpty()) {
                                    Text(
                                        text = if (currentLanguage == AppLanguage.HINDI)
                                            "कोई व्यक्तिगत शर्त सक्रिय नहीं है। सभी योजनाएं दर्शायी जा रही हैं।"
                                        else
                                            "No specific filters selected. Showing baseline citizen schemes.",
                                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF64748B))
                                    )
                                } else {
                                    summaryRules.forEach { rule ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = Color(0xFF15803D),
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Text(
                                                text = rule,
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = Color(0xFF334155),
                                                    fontSize = 11.sp
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Preset Criteria Shortcuts
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = { activeTraitIds = initialTraits.map { it.id }.toSet() },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) "सभी चुनें" else "Select All",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp)
                                )
                            }

                            OutlinedButton(
                                onClick = {
                                    activeTraitIds = setOf("TRAIT_FARMER", "TRAIT_RURAL", "TRAIT_STATE")
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) "किसान प्राथमिकता" else "Farmer Focus",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp)
                                )
                            }

                            OutlinedButton(
                                onClick = {
                                    activeTraitIds = setOf("TRAIT_FEMALE", "TRAIT_INCOME_LOW", "TRAIT_STATE")
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) "महिला कल्याण" else "Women Focus",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp)
                                )
                            }
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // SECTION 3: SCHEMES YOU MAY BE ELIGIBLE FOR
            // -------------------------------------------------------------
            item {
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
                            Icon(
                                imageVector = Icons.Default.Stars,
                                contentDescription = null,
                                tint = GovSaffron,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI)
                                    "3. योजनाएं जिनके आप पात्र हो सकते हैं"
                                else
                                    "3. Schemes You May Be Eligible For",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GovNavyPrimary,
                                    fontSize = (15 * fontSizeMultiplier).sp
                                )
                            )
                        }
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI)
                                "${matchedSchemes.size} योजनाएं आपकी प्रोफ़ाइल से मेल खाती हैं"
                            else
                                "${matchedSchemes.size} Matched Schemes based on active criteria",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF64748B),
                                fontSize = 11.sp
                            )
                        )
                    }

                    // Reset / Clear Button if customized
                    if (activeTraitIds.size != initialTraits.size) {
                        TextButton(onClick = { activeTraitIds = initialTraits.map { it.id }.toSet() }) {
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) "रीसेट" else "Reset",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = GovSaffron,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }

            // Render Result Cards
            if (matchedSchemes.isEmpty()) {
                item {
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterAltOff,
                                contentDescription = null,
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(40.dp)
                            )
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI)
                                    "कोई योजना मेल नहीं खाती"
                                else
                                    "No matching schemes for selected traits",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF475569)
                                )
                            )
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI)
                                    "कृपया अधिक प्रोफ़ाइल चिप्स चालू करें या 'सभी चुनें' पर क्लिक करें"
                                else
                                    "Enable more profile chips above to broaden your matched results",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF64748B)),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(matchedSchemes, key = { it.id }) { scheme ->
                    val isBookmarked = bookmarkedSchemeIds.contains(scheme.id)
                    EligibleSchemeResultCard(
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

            // -------------------------------------------------------------
            // SECTION 4: VIEW ALL
            // -------------------------------------------------------------
            item {
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AllInclusive,
                                contentDescription = null,
                                tint = GovNavyPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) "4. समस्त सरकारी योजनाएं (View All)" else "4. View All Government Schemes",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GovNavyPrimary,
                                    fontSize = (15 * fontSizeMultiplier).sp
                                )
                            )
                        }

                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI)
                                "अपनी प्रोफ़ाइल से परे केंद्र, राज्य व केंद्र शासित प्रदेशों की संपूर्ण 500+ कल्याणकारी योजनाओं का कैटलॉग देखें।"
                            else
                                "Explore the full master directory of Central, State & Union Territory welfare programs across India.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF64748B),
                                fontSize = 11.5.sp,
                                textAlign = TextAlign.Center
                            )
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showAllMatchesExpanded = !showAllMatchesExpanded },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, Color(0xFFCBD5E1))
                            ) {
                                Text(
                                    text = if (showAllMatchesExpanded) {
                                        if (currentLanguage == AppLanguage.HINDI) "केवल सर्वश्रेष्ठ मिलान" else "Show Top Matches"
                                    } else {
                                        if (currentLanguage == AppLanguage.HINDI) "सभी मिलान खोलें" else "Expand All Results"
                                    },
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }

                            Button(
                                onClick = onViewAllSchemes,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = GovNavyPrimary)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = if (currentLanguage == AppLanguage.HINDI) "मास्टर डायरेक्टरी →" else "Master Catalog →",
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
            }
        }
    }

    // -------------------------------------------------------------
    // SCHEME DETAILS & APPLICATION MODAL
    // -------------------------------------------------------------
    selectedSchemeForDetails?.let { scheme ->
        Dialog(onDismissRequest = { selectedSchemeForDetails = null }) {
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
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Surface(
                                color = scheme.status.bgColor,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) scheme.status.labelHi else scheme.status.labelEn,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = scheme.status.badgeColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = scheme.getName(currentLanguage),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GovNavyPrimary
                                )
                            )
                            Text(
                                text = scheme.getMinistry(currentLanguage),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF64748B),
                                    fontSize = 11.sp
                                )
                            )
                        }

                        IconButton(onClick = { selectedSchemeForDetails = null }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color(0xFF64748B)
                            )
                        }
                    }

                    HorizontalDivider(color = Color(0xFFE2E8F0))

                    // Why It Matches Banner
                    Surface(
                        color = Color(0xFFEFF6FF),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = GovNavyPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = scheme.getWhyItMatches(currentLanguage),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF1E3A8A),
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 11.5.sp
                                )
                            )
                        }
                    }

                    // Benefit Highlight
                    Surface(
                        color = Color(0xFFF0FDF4),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF15803D),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = scheme.getBenefit(currentLanguage),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF14532D),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }

                    // Overview
                    Text(
                        text = scheme.getOverview(currentLanguage),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF334155),
                            lineHeight = 16.sp
                        )
                    )

                    // Required Documents
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI) "आवश्यक दस्तावेज़:" else "Required Documents:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = GovNavyPrimary
                        )
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        scheme.requiredDocuments.take(3).forEach { doc ->
                            Surface(
                                color = Color(0xFFF1F5F9),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "📄 $doc",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFF475569),
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Simulated Portal Button
                    Button(
                        onClick = { selectedSchemeForDetails = null },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GovNavyPrimary)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) "आधिकारिक पोर्टल पर आवेदन करें ↗" else "Apply on Official Portal ↗",
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
    }
}

// -------------------------------------------------------------
// SCHEME RESULT CARD COMPONENT
// -------------------------------------------------------------

@Composable
fun EligibleSchemeResultCard(
    scheme: MatchedSchemeItem,
    currentLanguage: AppLanguage,
    fontSizeMultiplier: Float,
    isBookmarked: Boolean,
    onToggleBookmark: () -> Unit,
    onOpenDetails: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onOpenDetails() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 1. Header: Status Badge, Government Level & Bookmark Save Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    // Eligibility Status Badge
                    Surface(
                        color = scheme.status.bgColor,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI) scheme.status.labelHi else scheme.status.labelEn,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = scheme.status.badgeColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        )
                    }

                    // Government Level Badge
                    Surface(
                        color = when (scheme.govLevel) {
                            MatchGovLevel.CENTRAL -> Color(0xFFEFF6FF)
                            MatchGovLevel.STATE -> Color(0xFFFFF7ED)
                            MatchGovLevel.UT -> Color(0xFFFAF5FF)
                        },
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI) scheme.govLevel.labelHi else scheme.govLevel.labelEn,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = when (scheme.govLevel) {
                                    MatchGovLevel.CENTRAL -> GovNavyPrimary
                                    MatchGovLevel.STATE -> Color(0xFFC2410C)
                                    MatchGovLevel.UT -> Color(0xFF7E22CE)
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                // Bookmark Save Icon
                IconButton(
                    onClick = onToggleBookmark,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Save Scheme",
                        tint = if (isBookmarked) GovSaffron else Color(0xFF94A3B8),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // 2. Scheme Name
            Column {
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
                    text = "${scheme.stateOrUtName} • ${scheme.getMinistry(currentLanguage)}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF64748B),
                        fontSize = 11.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // 3. Why It May Match (Prominent Callout Box)
            Surface(
                color = Color(0xFFF8FAFC),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = GovSaffron,
                        modifier = Modifier
                            .size(16.dp)
                            .padding(top = 1.dp)
                    )
                    Column {
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI) "यह योजना क्यों मेल खाती है:" else "Why it may match:",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF334155),
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        )
                        Text(
                            text = scheme.getWhyItMatches(currentLanguage),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF475569),
                                fontSize = (11 * fontSizeMultiplier).sp,
                                lineHeight = 15.sp
                            )
                        )
                    }
                }
            }

            // 4. Benefit Highlight Pill
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

            // 5. Bottom Action Row
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
                            text = "⚡ DBT Active",
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
                        text = if (currentLanguage == AppLanguage.HINDI) "विस्तार व आवेदन →" else "View Details & Apply →",
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
