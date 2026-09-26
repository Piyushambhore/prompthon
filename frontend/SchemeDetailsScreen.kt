package org.jansaarthi.app.ui.screens.schemes

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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.Launch
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
 * JanSaarthi Complete Government Scheme Details Screen (Frontend UI Only)
 * ----------------------------------------------------------------------------
 * Exhaustive single-window citizen welfare overview containing all 17 mandated sections:
 * 1. Scheme Name (Bilingual title)
 * 2. Central / State / UT (Jurisdiction badge)
 * 3. State (Applicable residency / domicile scope)
 * 4. Ministry / Department (Official issuing government authorities)
 * 5. Category (Themed domain pill with vector icon)
 * 6. About (In-depth policy background & objectives)
 * 7. Benefits (Financial & in-kind assistance breakdown)
 * 8. Eligibility (Bullet points of qualifications and exclusions)
 * 9. Required Documents (Visual checklist with document icons)
 * 10. How to Apply (Step-by-step numbered guide)
 * 11. Application Mode (Online / CSC / Mobile / Offline Bank)
 * 12. Important Dates (Launch year, DBT schedule, renewal dates)
 * 13. FAQs (Expandable accordion cards with common questions)
 * 14. Helpline (Toll-free numbers, email, grievance redressal)
 * 15. Official Source (Verified portal URLs with external badges)
 * 16. Save Scheme (Interactive bookmark action with local state)
 * 17. Apply Now (High-contrast sticky bottom CTA bar)
 *
 * Frontend UI Only: Uses realistic local mock data with expandable sections.
 * No backend, database, Firebase, or external API code.
 */

// -------------------------------------------------------------
// DATA MODELS FOR DETAILED WELFARE SCHEME
// -------------------------------------------------------------

enum class DetailGovLevel(val labelEn: String, val labelHi: String, val icon: ImageVector) {
    CENTRAL("Central Government", "केंद्र सरकार", Icons.Default.AccountBalance),
    STATE("State Government", "राज्य सरकार", Icons.Default.Apartment),
    UT("Union Territory", "केंद्र शासित प्रदेश", Icons.Default.LocationCity)
}

data class SchemeFaqItem(
    val questionEn: String,
    val questionHi: String,
    val answerEn: String,
    val answerHi: String
)

data class DetailedWelfareScheme(
    val id: String,
    val nameEn: String,
    val nameHi: String,
    val acronym: String,
    val govLevel: DetailGovLevel,
    val applicableState: String, // e.g. "National (All 36 States & UTs)", "Maharashtra", etc.
    val ministryEn: String,
    val ministryHi: String,
    val departmentEn: String,
    val departmentHi: String,
    val categoryName: String,
    val categoryIcon: ImageVector,
    val categoryColor: Color,
    val aboutEn: String,
    val aboutHi: String,
    val benefitsSummaryEn: String,
    val benefitsSummaryHi: String,
    val benefitsList: List<String>,
    val eligibilityCriteria: List<String>,
    val exclusions: List<String>,
    val requiredDocuments: List<String>,
    val applicationSteps: List<String>,
    val applicationModes: List<String>, // "Online Web Portal", "Common Service Centre (CSC)", "Mobile App"
    val importantDates: List<Pair<String, String>>, // Label to Date string
    val faqs: List<SchemeFaqItem>,
    val tollFreeHelpline: String,
    val supportEmail: String,
    val grievancePortal: String,
    val officialWebsiteUrl: String,
    val isDbtEnabled: Boolean = true
)

// -------------------------------------------------------------
// REALISTIC MOCK DATASET FOR FLAGSHIP SCHEMES
// -------------------------------------------------------------

val DETAILED_FLAGSHIP_SCHEMES_MOCK: List<DetailedWelfareScheme> = listOf(
    DetailedWelfareScheme(
        id = "SCH-DET-01",
        nameEn = "Pradhan Mantri Kisan Samman Nidhi (PM-KISAN)",
        nameHi = "प्रधानमंत्री किसान सम्मान निधि",
        acronym = "PM-KISAN",
        govLevel = DetailGovLevel.CENTRAL,
        applicableState = "National (All 36 States & UTs)",
        ministryEn = "Ministry of Agriculture & Farmers Welfare",
        ministryHi = "कृषि एवं किसान कल्याण मंत्रालय",
        departmentEn = "Department of Agriculture and Farmers Welfare (DA&FW)",
        departmentHi = "कृषि एवं किसान कल्याण विभाग",
        categoryName = "Agriculture, Rural & Environment",
        categoryIcon = Icons.Default.Agriculture,
        categoryColor = Color(0xFF15803D),
        aboutEn = "PM-KISAN is a flagship Central Sector Scheme with 100% funding from the Government of India. It became operational on 1st December 2018. Under the scheme, income support of ₹6,000 per annum is provided to all landholder farmer families across the country to supplement their financial needs in procuring agricultural inputs and domestic obligations.",
        aboutHi = "प्रधानमंत्री किसान सम्मान निधि (पीएम-किसान) भारत सरकार द्वारा 100% वित्तपोषित एक केंद्रीय क्षेत्र योजना है। यह योजना 1 दिसंबर 2018 से प्रभावी है। इसके तहत देश के सभी भूमिधारक किसान परिवारों को कृषि सामग्री खरीदने और घरेलू जरूरतों की पूर्ति हेतु ₹6,000 प्रति वर्ष की प्रत्यक्ष आय सहायता प्रदान की जाती है।",
        benefitsSummaryEn = "₹6,000 per year transferred directly into beneficiary bank accounts in three equal 4-monthly installments of ₹2,000 each.",
        benefitsSummaryHi = "प्रति वर्ष ₹6,000 की राशि ₹2,000-₹2,000 की तीन समान किस्तों में हर चार महीने पर सीधे बैंक खाते में (डीबीटी) भेजी जाती है।",
        benefitsList = listOf(
            "100% direct bank credit via PFMS without commission or middlemen",
            "Automatic synchronization with State top-ups (e.g. Maharashtra Namo Shetkari additional ₹6,000/yr)",
            "Enables purchase of seeds, organic fertilizers, crop protection and electricity",
            "Emergency liquidity safeguard against unseasonal crop damages"
        ),
        eligibilityCriteria = listOf(
            "Small and marginal farmer families possessing cultivable agricultural land up to 2 hectares in their name",
            "Land ownership records verified in State land revenue data (e.g. 7/12 extract / Khasra-Khatauni)",
            "Valid Aadhaar card linked with active bank account and enabled for DBT (Direct Benefit Transfer)",
            "Mandatory completion of biometric or OTP-based e-KYC on PM-KISAN portal"
        ),
        exclusions = listOf(
            "Institutional landholders and non-agricultural entities",
            "Former and present holders of constitutional posts, ministers, MPs, MLAs, and Mayors",
            "Serving or retired government employees (except Group D / multi-tasking staff)",
            "All persons who paid Income Tax in last assessment year",
            "Professionals like doctors, engineers, lawyers, and chartered accountants"
        ),
        requiredDocuments = listOf(
            "Aadhaar Card (Linked with active mobile number for OTP)",
            "Land Revenue Record / 7/12 Extract / Khasra-Khatauni proof of landholding",
            "Bank Passbook or Account Details (Aadhaar Seeded)",
            "Active Mobile Number registered with Aadhaar"
        ),
        applicationSteps = listOf(
            "Visit the official PM-KISAN portal (pmkisan.gov.in) or visit nearest village Common Service Centre (CSC).",
            "Click on 'New Farmer Registration' under Farmers Corner and enter Aadhaar Number & Mobile Number.",
            "Select State, District, Sub-District, Block, and Village; enter land parcel ownership survey numbers.",
            "Upload land document copy and submit; note down the temporary Registration Number.",
            "Village Patwari / District Nodal Officer verifies land record; state approves and triggers DBT disbursement."
        ),
        applicationModes = listOf(
            "Online Citizen Self-Registration (pmkisan.gov.in)",
            "Village Common Service Centre (CSC) Assisted Counter",
            "PM-KISAN Mobile Application (Face Authentication)",
            "State Agriculture Department Village Extension Officer"
        ),
        importantDates = listOf(
            "Scheme Launch Date" to "24 February 2019 (Effective 01 Dec 2018)",
            "Tranche 1 (April - July)" to "Disbursed annually in April/May",
            "Tranche 2 (August - November)" to "Disbursed annually in August/September",
            "Tranche 3 (December - March)" to "Disbursed annually in December/January",
            "Next 18th Installment" to "Scheduled release: 15 October 2024"
        ),
        faqs = listOf(
            SchemeFaqItem(
                questionEn = "What is the mandatory e-KYC requirement?",
                questionHi = "ई-केवाईसी (e-KYC) क्यों अनिवार्य है?",
                answerEn = "e-KYC is mandatory for all PM-KISAN beneficiaries to ensure zero leakages. It can be done online through OTP on pmkisan.gov.in or via biometric scan at any CSC.",
                answerHi = "फर्जी लाभार्थियों को रोकने और सही किसान तक राशि पहुंचाने के लिए ई-केवाईसी अनिवार्य है। इसे पोर्टल पर ओटीपी द्वारा या सीएससी केंद्र पर बायोमेट्रिक द्वारा किया जा सकता है।"
            ),
            SchemeFaqItem(
                questionEn = "Can family members with separate land titles apply separately?",
                questionHi = "क्या परिवार के सदस्य अलग-अलग आवेदन कर सकते हैं?",
                answerEn = "Under PM-KISAN guidelines, a 'Farmer Family' is defined as husband, wife, and minor children. Only one adult head possessing registered land in the family unit is eligible for the benefit.",
                answerHi = "पीएम-किसान के अनुसार 'परिवार' का अर्थ पति, पत्नी और नाबालिग बच्चे हैं। परिवार इकाई में से केवल एक व्यक्ति जिसके नाम भूमि है, वह पात्र होगा।"
            ),
            SchemeFaqItem(
                questionEn = "How can a farmer check their beneficiary payment status?",
                questionHi = "किसान अपनी किस्त की स्थिति कैसे जांच सकते हैं?",
                answerEn = "Visit pmkisan.gov.in, tap 'Know Your Status' under Farmers Corner, enter Registration Number or Aadhaar, and view detailed payment history with FTO status.",
                answerHi = "pmkisan.gov.in पर जाएं, 'Know Your Status' पर क्लिक करें, अपना रजिस्ट्रेशन नंबर दर्ज करें और सभी किस्तों की स्थिति व बैंक विवरण देखें।"
            )
        ),
        tollFreeHelpline = "155261 / 1800-115-526 (National PM-KISAN Toll-Free)",
        supportEmail = "pmkisan-ict@gov.in",
        grievancePortal = "https://pgportal.gov.in (CPGRAMS Central Citizen Grievance)",
        officialWebsiteUrl = "https://pmkisan.gov.in",
        isDbtEnabled = true
    ),
    DetailedWelfareScheme(
        id = "SCH-DET-02",
        nameEn = "Mukhyamantri Majhi Ladki Bahin Yojana",
        nameHi = "मुख्यमंत्री माझी लाड़की बहिन योजना",
        acronym = "MLBY",
        govLevel = DetailGovLevel.STATE,
        applicableState = "Maharashtra Resident",
        ministryEn = "Department of Women & Child Development (Govt of Maharashtra)",
        ministryHi = "महिला एवं बाल विकास विभाग (महाराष्ट्र शासन)",
        departmentEn = "Directorate of Women and Child Development",
        departmentHi = "महिला एवं बाल विकास संचालनालय",
        categoryName = "Women & Child",
        categoryIcon = Icons.Default.FamilyRestroom,
        categoryColor = Color(0xFFBE185D),
        aboutEn = "Mukhyamantri Majhi Ladki Bahin Yojana is an ambitious social welfare initiative launched by the Government of Maharashtra in July 2024. The scheme seeks to ensure gender equity, financial self-reliance, and basic nutrition security for women across Maharashtra by transferring ₹1,500 every month directly to their bank accounts.",
        aboutHi = "मुख्यमंत्री माझी लाड़की बहिन योजना महाराष्ट्र शासन द्वारा जुलाई 2024 में शुरू की गई एक ऐतिहासिक कल्याणकारी योजना है। इसका मुख्य उद्देश्य महाराष्ट्र की महिलाओं को आर्थिक स्वावलंबन प्रदान करना, पोषण स्तर में सुधार करना और सम्मानजनक जीवन सुनिश्चित करना है। इसके तहत पात्र महिलाओं को ₹1,500 प्रति माह सीधे बैंक खाते में दिए जाते हैं।",
        benefitsSummaryEn = "Guaranteed ₹1,500 per month (total ₹18,000 annually) transferred directly to Aadhaar-linked bank accounts on the 1st week of each month.",
        benefitsSummaryHi = "प्रति माह ₹1,500 (वार्षिक ₹18,000) की सम्मान राशि महिला के आधार लिंक बैंक खाते में सीधे ट्रांसफर की जाती है।",
        benefitsList = listOf(
            "Monthly ₹1,500 direct basic income credited every month",
            "Promotes female entrepreneurship, household health, and child education",
            "Universal coverage for eligible women aged 21 to 65 years in Maharashtra",
            "No fees or registration costs at government counters"
        ),
        eligibilityCriteria = listOf(
            "Female resident and domicile of Maharashtra State",
            "Age between 21 years and 65 years as on 1st July 2024",
            "Combined annual family income from all sources must not exceed ₹2.5 Lakhs",
            "Possession of Yellow or Orange NFSA Ration Card or Income Certificate (< ₹2.5L)",
            "Active personal bank account linked with Aadhaar card"
        ),
        exclusions = listOf(
            "Families with combined annual income exceeding ₹2.5 Lakhs",
            "Families where any member is a regular/permanent government employee or pensioner",
            "Families paying Income Tax or owning a four-wheeler vehicle (except tractors)",
            "Women already receiving more than ₹1,500/month under another state cash transfer scheme"
        ),
        requiredDocuments = listOf(
            "Aadhaar Card of the applicant woman",
            "Maharashtra Domicile Certificate or 15-year Ration Card / School Leaving Certificate",
            "Income Certificate issued by Tehsildar (< ₹2.5L) or Yellow/Orange Ration Card",
            "Applicant's own Bank Passbook copy (Aadhaar linked)",
            "Hamipatra (Self-declaration form undertaking eligibility)"
        ),
        applicationSteps = listOf(
            "Download 'Nari Shakti Doot' mobile application on Android smartphone or visit Setu Kendra / Anganwadi centre.",
            "Log in using mobile number OTP and select 'Mukhyamantri Majhi Ladki Bahin Yojana'.",
            "Fill in Aadhaar details, personal information, bank account IFSC, and family members.",
            "Upload photo of Aadhaar Card, Ration Card/Income Certificate, and signed Hamipatra.",
            "Submit application; Ward/Panchayat committee approves list and first DBT is credited."
        ),
        applicationModes = listOf(
            "Nari Shakti Doot Mobile Application (Self Citizen)",
            "Village Anganwadi Sevika / Gram Panchayat Counter",
            "Setu Suvidha Kendra / MahaOnline Centers",
            "Municipal Ward Citizen Facilitation Desks"
        ),
        importantDates = listOf(
            "Scheme Announcement" to "28 June 2024 (Maharashtra Budget)",
            "Application Portal Open" to "01 July 2024",
            "First & Second Tranche" to "₹3,000 disbursed on Raksha Bandhan (August 2024)",
            "Monthly Release Schedule" to "Every month between 1st and 10th"
        ),
        faqs = listOf(
            SchemeFaqItem(
                questionEn = "Can unmarried women aged 21+ apply for the scheme?",
                questionHi = "क्या 21 वर्ष से अधिक आयु की अविवाहित महिलाएं आवेदन कर सकती हैं?",
                answerEn = "Yes! Married, unmarried, widowed, divorced, and destitute women aged between 21 and 65 years who meet the income criteria are fully eligible.",
                answerHi = "हां! 21 से 65 वर्ष की विवाहित, अविवाहित, विधवा, तलाकशुदा और परित्यक्ता महिलाएं जो आय मानदंडों को पूरा करती हैं, पूरी तरह पात्र हैं।"
            ),
            SchemeFaqItem(
                questionEn = "What if a family doesn't possess a Domicile Certificate?",
                questionHi = "यदि अधिवास प्रमाणपत्र (Domicile) न हो तो क्या करें?",
                answerEn = "The Maharashtra Government allows a Ration Card older than 15 years, Voter ID card, or School Leaving Certificate of Maharashtra as valid proof of domicile.",
                answerHi = "शासन के निर्णय अनुसार 15 वर्ष पुराना राशन कार्ड, वोटर आईडी या स्कूल छोड़ने का प्रमाणपत्र (टीसी) भी अधिवास प्रमाण के रूप में मान्य है।"
            )
        ),
        tollFreeHelpline = "181 (Women Helpline) / 1905 (Maharashtra State Citizen Call Centre)",
        supportEmail = "ladkibahin-support@maharashtra.gov.in",
        grievancePortal = "https://grievances.maharashtra.gov.in (Aaple Sarkar Grievance Portal)",
        officialWebsiteUrl = "https://ladkibahin.maharashtra.gov.in",
        isDbtEnabled = true
    ),
    DetailedWelfareScheme(
        id = "SCH-DET-03",
        nameEn = "Ayushman Bharat PM-JAY (Golden Card)",
        nameHi = "आयुष्मान भारत प्रधानमंत्री जन आरोग्य योजना",
        acronym = "PM-JAY",
        govLevel = DetailGovLevel.CENTRAL,
        applicableState = "National (All 36 States & UTs)",
        ministryEn = "Ministry of Health & Family Welfare",
        ministryHi = "स्वास्थ्य एवं परिवार कल्याण मंत्रालय",
        departmentEn = "National Health Authority (NHA)",
        departmentHi = "राष्ट्रीय स्वास्थ्य प्राधिकरण",
        categoryName = "Health & Wellness",
        categoryIcon = Icons.Default.HealthAndSafety,
        categoryColor = Color(0xFF0F766E),
        aboutEn = "Ayushman Bharat Pradhan Mantri Jan Arogya Yojana (PM-JAY) is the world's largest health assurance scheme launched by the Prime Minister in September 2018. It aims to provide cashless hospitalisation cover of ₹5 Lakhs per family per year to over 12 crore poor and vulnerable families (covering bottom 40% of population) and all senior citizens aged 70+ years.",
        aboutHi = "आयुष्मान भारत प्रधानमंत्री जन आरोग्य योजना (पीएम-जय) विश्व की सबसे बड़ी सरकारी स्वास्थ्य बीमा योजना है। यह देश के 12 करोड़ से अधिक गरीब परिवारों और 70 वर्ष से अधिक आयु के सभी वरिष्ठ नागरिकों को प्रति परिवार प्रति वर्ष ₹5,00,000 का मुफ़्त कैशलेस अस्पताल उपचार प्रदान करती है।",
        benefitsSummaryEn = "₹5,00,000 cashless secondary and tertiary inpatient hospitalisation coverage per eligible family per year across 27,000+ empanelled public and private hospitals.",
        benefitsSummaryHi = "प्रति परिवार प्रति वर्ष ₹5 लाख तक का मुफ़्त कैशलेस अस्पताल इलाज देशभर के 27,000+ सरकारी एवं निजी सूचीबद्ध अस्पतालों में।",
        benefitsList = listOf(
            "Covers 1,949 medical, surgical, and diagnostic inpatient procedures",
            "Includes 3 days pre-hospitalisation and 15 days post-hospitalisation medicines",
            "Pre-existing diseases are covered from the very first day of enrollment",
            "Cashless & paperless access at all hospital Arogyamitra desks across India",
            "Expanded universal coverage for all senior citizens aged 70+ regardless of income"
        ),
        eligibilityCriteria = listOf(
            "Deprived rural and identified urban occupational families under SECC 2011 database",
            "Active NFSA Antyodaya Anna Yojana (AAY) and Priority Household (PHH) Ration Card holders",
            "All senior citizens residing in India aged 70 years and above (Ayushman Vay Vandana Card)"
        ),
        exclusions = listOf(
            "Families covered under central government health schemes (CGHS/ECHS/ESIC) for double benefit",
            "Families owning motorized fishing boats, four-wheelers, or mechanized agricultural equipment in rural survey"
        ),
        requiredDocuments = listOf(
            "Aadhaar Card (Mandatory for biometric/OTP e-KYC)",
            "Ration Card or Family Identification Document",
            "Mobile number linked with Aadhaar"
        ),
        applicationSteps = listOf(
            "Open beneficiary.nha.gov.in or Ayushman App on mobile.",
            "Select Login as 'Beneficiary', enter mobile number and verify OTP.",
            "Select State, Scheme (PM-JAY), and search by Aadhaar or Ration Card number.",
            "Perform Face / Iris / OTP e-KYC authentication.",
            "Download instant digital Ayushman Card PDF with QR code or print PVC card."
        ),
        applicationModes = listOf(
            "National Health Authority Portal (beneficiary.nha.gov.in)",
            "Ayushman Beneficiary Mobile App (Face Authentication)",
            "Hospital Arogyamitra Help Desk at Empanelled Hospitals",
            "Common Service Centres (CSC) / UTIITSL Centres"
        ),
        importantDates = listOf(
            "Scheme Launch Date" to "23 September 2018 (Ranchi, Jharkhand)",
            "Card Validity" to "Lifelong with dynamic online e-KYC",
            "Senior Citizens 70+ Expansion" to "Approved October 2024"
        ),
        faqs = listOf(
            SchemeFaqItem(
                questionEn = "Is there any limit on family size or age under PM-JAY?",
                questionHi = "क्या परिवार के सदस्यों की संख्या या आयु की कोई सीमा है?",
                answerEn = "No! There is no cap on family size, gender, or age. All members listed in the ration card or family ID are covered under the single family ₹5 Lakh pool.",
                answerHi = "नहीं! परिवार के सदस्यों की संख्या, आयु या लिंग की कोई सीमा नहीं है। परिवार के सभी सदस्य ₹5 लाख के संयुक्त कवर के अंतर्गत आते हैं।"
            ),
            SchemeFaqItem(
                questionEn = "Can the Ayushman Card be used in other states during travel?",
                questionHi = "क्या दूसरे राज्य में इलाज के दौरान कार्ड का उपयोग किया जा सकता है?",
                answerEn = "Yes! PM-JAY is 100% portable nationwide. A beneficiary from Maharashtra or UP can receive cashless treatment at any empanelled hospital in Delhi, Tamil Nadu, or anywhere in India.",
                answerHi = "हां! आयुष्मान भारत 100% राष्ट्रीय पोर्टेबल है। आप भारत के किसी भी राज्य के सूचीबद्ध अस्पताल में जाकर मुफ़्त इलाज प्राप्त कर सकते हैं।"
            )
        ),
        tollFreeHelpline = "14555 (National Ayushman Bharat Call Centre)",
        supportEmail = "ayushmanbharat.nha@gov.in",
        grievancePortal = "https://cgrms.pmjay.gov.in (National Consumer Grievance Portal)",
        officialWebsiteUrl = "https://pmjay.gov.in",
        isDbtEnabled = false
    ),
    DetailedWelfareScheme(
        id = "SCH-DET-04",
        nameEn = "Ladli Beti Financial Security Scheme",
        nameHi = "लाडली बेटी वित्तीय सुरक्षा योजना",
        acronym = "Ladli Beti (UT)",
        govLevel = DetailGovLevel.UT,
        applicableState = "Jammu & Kashmir (UT Resident)",
        ministryEn = "Social Welfare Department (UT of Jammu & Kashmir)",
        ministryHi = "समाज कल्याण विभाग (केंद्र शासित प्रदेश जम्मू-कश्मीर)",
        departmentEn = "Directorate of Social Welfare, Jammu & Kashmir",
        departmentHi = "समाज कल्याण संचालनालय, जम्मू-कश्मीर",
        categoryName = "Women & Child",
        categoryIcon = Icons.Default.FamilyRestroom,
        categoryColor = Color(0xFFBE185D),
        aboutEn = "Ladli Beti is an impactful social security scheme launched by the Union Territory Administration of Jammu & Kashmir to arrest female foeticide, balance child sex ratio, and ensure girl children born in economically weaker sections receive a robust financial foundation for higher education and self-reliance.",
        aboutHi = "लाडली बेटी केंद्र शासित प्रदेश जम्मू-कश्मीर प्रशासन द्वारा आर्थिक रूप से कमजोर परिवारों में जन्म लेने वाली बालिकाओं के लिए शुरू की गई एक प्रमुख सामाजिक सुरक्षा योजना है। इसका मुख्य उद्देश्य बालिकाओं के भविष्य को सुरक्षित करना, उच्च शिक्षा को बढ़ावा देना और कन्या भ्रूण हत्या को रोकना है।",
        benefitsSummaryEn = "₹1,000/month government contribution deposited directly into the child's recurring bank account for 14 years, accumulating to ₹6.5 Lakhs at age 21.",
        benefitsSummaryHi = "14 वर्षों तक बालिका के आवर्ती बैंक खाते में सरकार द्वारा ₹1,000/माह का योगदान, 21 वर्ष की आयु पर ₹6.5 लाख की सुनिश्चित परिपक्वता राशि।",
        benefitsList = listOf(
            "Direct contribution of ₹1,000 per month by J&K UT Administration into recurring bank account",
            "Maturity payout of ₹6.5 Lakhs directly upon attaining 21 years of age",
            "Complete financial coverage for university education or career establishment",
            "Zero deposit burden on low-income parents"
        ),
        eligibilityCriteria = listOf(
            "Girl child born on or after 1st April 2015",
            "Permanent resident / Domicile of Union Territory of Jammu & Kashmir",
            "Combined annual family income from all sources must not exceed ₹75,000",
            "Application submitted within 6 months of child's birth",
            "Applicable for up to two girl children per family"
        ),
        exclusions = listOf(
            "Families with combined annual income exceeding ₹75,000",
            "Children of regular government employees or income tax payees",
            "Girl children born prior to 1st April 2015"
        ),
        requiredDocuments = listOf(
            "Birth Certificate of girl child issued by Municipal Corporation / Registrar",
            "Domicile Certificate / PRC of Parents in J&K UT",
            "Income Certificate issued by Tehsildar (< ₹75,000/year)",
            "Aadhaar Card of Parents and Girl Child",
            "Bank Account Passbook opened in girl child's name"
        ),
        applicationSteps = listOf(
            "Obtain Ladli Beti application form from nearest Child Development Project Officer (CDPO) or download from jksocialwelfare.nic.in.",
            "Fill details, attach birth certificate, parents' domicile certificate, and Tehsildar income certificate.",
            "Submit completed dossier to local Anganwadi Centre or CDPO office within 6 months of birth.",
            "CDPO verifies records and forwards approval recommendation to District Social Welfare Officer (DSWO).",
            "Sanction order issued and J&K Bank opens recurring deposit account with monthly UT deposit."
        ),
        applicationModes = listOf(
            "Child Development Project Officer (CDPO) Office / Anganwadi Centres",
            "District Social Welfare Officer (DSWO) Counter",
            "J&K JanSugam Online Citizen Services Portal (jksugam.jk.gov.in)"
        ),
        importantDates = listOf(
            "Scheme Launch Date" to "01 April 2015",
            "Application Window" to "Within 6 months of child's birth",
            "Monthly Contribution Cycle" to "1st week of each month into J&K Bank account",
            "Account Maturity" to "Upon girl child completing 21 years of age"
        ),
        faqs = listOf(
            SchemeFaqItem(
                questionEn = "Can funds be withdrawn before 21 years?",
                questionHi = "क्या 21 वर्ष से पहले पैसे निकाले जा सकते हैं?",
                answerEn = "No. The accumulated sum can only be withdrawn after the girl child completes 21 years of age and submits maturity proof, ensuring full educational or marriage security.",
                answerHi = "नहीं। परिपक्वता राशि केवल 21 वर्ष की आयु पूरी होने पर ही निकाली जा सकती है ताकि बालिका की उच्च शिक्षा या भविष्य सुरक्षित रहे।"
            ),
            SchemeFaqItem(
                questionEn = "Is the benefit applicable to all daughters in the family?",
                questionHi = "क्या परिवार की सभी बेटियों को यह लाभ मिल सकता है?",
                answerEn = "The benefit is available for up to two girl children per eligible family.",
                answerHi = "यह लाभ प्रति पात्र परिवार में अधिकतम दो बेटियों के लिए उपलब्ध है।"
            )
        ),
        tollFreeHelpline = "0194-2473719 (Srinagar HQ) / 0191-2478400 (Jammu HQ)",
        supportEmail = "socialwelfare-jk@nic.in",
        grievancePortal = "https://jkgrievance.jk.gov.in (J&K Citizen Grievance Portal)",
        officialWebsiteUrl = "https://jksocialwelfare.nic.in",
        isDbtEnabled = true
    )
)

// -------------------------------------------------------------
// MAIN SCHEME DETAILS SCREEN COMPOSABLE
// -------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchemeDetailsScreen(
    schemeId: String? = null,
    currentLanguage: AppLanguage = AppLanguage.HINDI,
    onSelectLanguage: (AppLanguage) -> Unit = {},
    isLargeFont: Boolean = false,
    onFontScaleToggle: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onApplyNow: (DetailedWelfareScheme) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val fontSizeMultiplier = if (isLargeFont) 1.15f else 1.0f

    // Resolve scheme by ID, allows interactive switching in frontend demo
    var currentSchemeId by remember(schemeId) {
        mutableStateOf(schemeId ?: DETAILED_FLAGSHIP_SCHEMES_MOCK.first().id)
    }

    val activeScheme = remember(currentSchemeId) {
        DETAILED_FLAGSHIP_SCHEMES_MOCK.firstOrNull { it.id.equals(currentSchemeId, ignoreCase = true) }
            ?: DETAILED_FLAGSHIP_SCHEMES_MOCK.first()
    }

    // Local Interactive States
    var isSaved by remember { mutableStateOf(false) }
    var showShareDialog by remember { mutableStateOf(false) }
    var showApplyModal by remember { mutableStateOf(false) }

    // Expandable Section Controllers
    var expandedAbout by remember { mutableStateOf(true) }
    var expandedBenefits by remember { mutableStateOf(true) }
    var expandedEligibility by remember { mutableStateOf(true) }
    var expandedDocuments by remember { mutableStateOf(true) }
    var expandedHowToApply by remember { mutableStateOf(true) }
    var expandedApplicationMode by remember { mutableStateOf(false) }
    var expandedImportantDates by remember { mutableStateOf(false) }
    var expandedFaqs by remember { mutableStateOf(false) }
    var expandedHelpline by remember { mutableStateOf(false) }
    var expandedOfficialSource by remember { mutableStateOf(false) }

    // Active expanded FAQ index
    var expandedFaqIndex by remember { mutableIntStateOf(-1) }

    Scaffold(
        topBar = {
            Column {
                // National Banner
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
                                    text = if (currentLanguage == AppLanguage.HINDI) "योजना का संपूर्ण विवरण" else "Scheme Details",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GovNavyPrimary,
                                        fontSize = (16 * fontSizeMultiplier).sp
                                    )
                                )
                                Text(
                                    text = activeScheme.acronym,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF64748B),
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        // Bookmark Save Icon & Share Action
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { isSaved = !isSaved }) {
                                Icon(
                                    imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                    contentDescription = "Save Scheme",
                                    tint = if (isSaved) GovSaffron else GovNavyPrimary
                                )
                            }

                            IconButton(onClick = { showShareDialog = true }) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share",
                                    tint = GovNavyPrimary
                                )
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            // Section 17: Sticky Bottom Action Bar with "Apply Now"
            Surface(
                color = Color.White,
                shadowElevation = 12.dp,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Save Scheme Button (Section 16)
                    OutlinedButton(
                        onClick = { isSaved = !isSaved },
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.5.dp, if (isSaved) GovSaffron else GovBorder),
                        modifier = Modifier.height(50.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = null,
                            tint = if (isSaved) GovSaffron else GovNavyPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isSaved) {
                                if (currentLanguage == AppLanguage.HINDI) "सहेजी गई" else "Saved"
                            } else {
                                if (currentLanguage == AppLanguage.HINDI) "सहेजें" else "Save"
                            },
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isSaved) GovSaffron else GovNavyPrimary
                            )
                        )
                    }

                    // Primary Apply Now CTA Button (Section 17)
                    Button(
                        onClick = {
                            onApplyNow(activeScheme)
                            showApplyModal = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GovNavyPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) "अभी आवेदन करें →" else "Apply Now →",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 15.sp
                                )
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
                bottom = innerPadding.calculateBottomPadding() + 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
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

            // Quick Scheme Switcher (Allows testing between Central, State and Healthcare programs)
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(DETAILED_FLAGSHIP_SCHEMES_MOCK) { scheme ->
                        val isSelected = scheme.id == activeScheme.id
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                if (!isSelected) {
                                    currentSchemeId = scheme.id
                                    expandedFaqIndex = -1
                                    onApplyNow(scheme)
                                }
                            },
                            label = {
                                Text(
                                    text = scheme.acronym,
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
                            )
                        )
                    }
                }
            }

            // -------------------------------------------------------------
            // SECTIONS 1 - 5: SCHEME HEADER & METADATA CARD
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
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Section 2: Central / State / UT Badge & DBT Status
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = when (activeScheme.govLevel) {
                                    DetailGovLevel.CENTRAL -> Color(0xFFEFF6FF)
                                    DetailGovLevel.STATE -> Color(0xFFFFF7ED)
                                    DetailGovLevel.UT -> Color(0xFFFAF5FF)
                                },
                                shape = RoundedCornerShape(6.dp),
                                border = BorderStroke(1.dp, when (activeScheme.govLevel) {
                                    DetailGovLevel.CENTRAL -> Color(0xFFBFDBFE)
                                    DetailGovLevel.STATE -> Color(0xFFFED7AA)
                                    DetailGovLevel.UT -> Color(0xFFE9D5FF)
                                })
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = activeScheme.govLevel.icon,
                                        contentDescription = null,
                                        tint = when (activeScheme.govLevel) {
                                            DetailGovLevel.CENTRAL -> GovNavyPrimary
                                            DetailGovLevel.STATE -> Color(0xFFC2410C)
                                            DetailGovLevel.UT -> Color(0xFF7E22CE)
                                        },
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = if (currentLanguage == AppLanguage.HINDI) activeScheme.govLevel.labelHi else activeScheme.govLevel.labelEn,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = when (activeScheme.govLevel) {
                                                DetailGovLevel.CENTRAL -> GovNavyPrimary
                                                DetailGovLevel.STATE -> Color(0xFFC2410C)
                                                DetailGovLevel.UT -> Color(0xFF7E22CE)
                                            },
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.5.sp
                                        )
                                    )
                                }
                            }

                            if (activeScheme.isDbtEnabled) {
                                Surface(
                                    color = Color(0xFFDCFCE7),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Bolt,
                                            contentDescription = null,
                                            tint = Color(0xFF15803D),
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Text(
                                            text = "DBT Active",
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

                        // Section 1: Scheme Name (Bilingual)
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = activeScheme.nameEn,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = GovNavyPrimary,
                                    fontSize = (18 * fontSizeMultiplier).sp,
                                    lineHeight = 24.sp
                                )
                            )
                            Text(
                                text = activeScheme.nameHi,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF475569),
                                    fontSize = (14 * fontSizeMultiplier).sp
                                )
                            )
                        }

                        // Section 3 & 4 & 5: State, Ministry/Department & Category Pills
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            // Section 3: State Scope
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = GovSaffron,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = activeScheme.applicableState,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF1E293B),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                )
                            }

                            // Section 4: Ministry & Department
                            Row(
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalance,
                                    contentDescription = null,
                                    tint = GovNavyPrimary,
                                    modifier = Modifier
                                        .size(16.dp)
                                        .padding(top = 2.dp)
                                )
                                Column {
                                    Text(
                                        text = if (currentLanguage == AppLanguage.HINDI) activeScheme.ministryHi else activeScheme.ministryEn,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = GovNavyPrimary,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 11.5.sp
                                        )
                                    )
                                    Text(
                                        text = if (currentLanguage == AppLanguage.HINDI) activeScheme.departmentHi else activeScheme.departmentEn,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Color(0xFF64748B),
                                            fontSize = 10.5.sp
                                        )
                                    )
                                }
                            }

                            // Section 5: Category
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Surface(
                                    color = activeScheme.categoryColor.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = activeScheme.categoryIcon,
                                            contentDescription = null,
                                            tint = activeScheme.categoryColor,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = activeScheme.categoryName,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = activeScheme.categoryColor,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.5.sp
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
            // SECTION 6: ABOUT
            // -------------------------------------------------------------
            item {
                ExpandableDetailsSection(
                    title = if (currentLanguage == AppLanguage.HINDI) "योजना के बारे में (About the Scheme)" else "About the Scheme",
                    icon = Icons.Default.Info,
                    iconColor = GovNavyPrimary,
                    isExpanded = expandedAbout,
                    onToggle = { expandedAbout = !expandedAbout }
                ) {
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI) activeScheme.aboutHi else activeScheme.aboutEn,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF334155),
                            fontSize = (13 * fontSizeMultiplier).sp,
                            lineHeight = 19.sp
                        )
                    )
                }
            }

            // -------------------------------------------------------------
            // SECTION 7: BENEFITS
            // -------------------------------------------------------------
            item {
                ExpandableDetailsSection(
                    title = if (currentLanguage == AppLanguage.HINDI) "योजना के लाभ (Benefits & Entitlements)" else "Benefits & Financial Entitlements",
                    icon = Icons.Default.CheckCircle,
                    iconColor = Color(0xFF15803D),
                    isExpanded = expandedBenefits,
                    onToggle = { expandedBenefits = !expandedBenefits }
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Highlight Pill Banner
                        Surface(
                            color = Color(0xFFF0FDF4),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) activeScheme.benefitsSummaryHi else activeScheme.benefitsSummaryEn,
                                modifier = Modifier.padding(10.dp),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF14532D),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            )
                        }

                        // Detailed Bullet Points
                        activeScheme.benefitsList.forEach { benefit ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color(0xFF15803D),
                                    modifier = Modifier
                                        .size(16.dp)
                                        .padding(top = 2.dp)
                                )
                                Text(
                                    text = benefit,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF334155),
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // SECTION 8: ELIGIBILITY & EXCLUSIONS
            // -------------------------------------------------------------
            item {
                ExpandableDetailsSection(
                    title = if (currentLanguage == AppLanguage.HINDI) "पात्रता एवं शर्तें (Eligibility Criteria)" else "Eligibility Criteria",
                    icon = Icons.AutoMirrored.Filled.FactCheck,
                    iconColor = Color(0xFF0F766E),
                    isExpanded = expandedEligibility,
                    onToggle = { expandedEligibility = !expandedEligibility }
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI) "कौन पात्र हैं:" else "Who is Eligible:",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                        )
                        activeScheme.eligibilityCriteria.forEach { crit ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Done,
                                    contentDescription = null,
                                    tint = Color(0xFF0F766E),
                                    modifier = Modifier
                                        .size(16.dp)
                                        .padding(top = 2.dp)
                                )
                                Text(
                                    text = crit,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF334155),
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp
                                    )
                                )
                            }
                        }

                        HorizontalDivider(color = Color(0xFFE2E8F0))

                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI) "कौन पात्र नहीं हैं (अपात्रता / Exclusions):" else "Who is Not Eligible (Exclusions):",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF991B1B)
                            )
                        )
                        activeScheme.exclusions.forEach { excl ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = null,
                                    tint = Color(0xFFDC2626),
                                    modifier = Modifier
                                        .size(16.dp)
                                        .padding(top = 2.dp)
                                )
                                Text(
                                    text = excl,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF64748B),
                                        fontSize = 11.5.sp,
                                        lineHeight = 15.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // SECTION 9: REQUIRED DOCUMENTS
            // -------------------------------------------------------------
            item {
                ExpandableDetailsSection(
                    title = if (currentLanguage == AppLanguage.HINDI) "आवश्यक दस्तावेज़ (Required Documents)" else "Required Documents Checklist",
                    icon = Icons.Default.Folder,
                    iconColor = Color(0xFFD97706),
                    isExpanded = expandedDocuments,
                    onToggle = { expandedDocuments = !expandedDocuments }
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        activeScheme.requiredDocuments.forEach { doc ->
                            Surface(
                                color = Color(0xFFF8FAFC),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Description,
                                        contentDescription = null,
                                        tint = Color(0xFF0284C7),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = doc,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Color(0xFF1E293B),
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 12.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // SECTION 10: HOW TO APPLY (STEP-BY-STEP)
            // -------------------------------------------------------------
            item {
                ExpandableDetailsSection(
                    title = if (currentLanguage == AppLanguage.HINDI) "आवेदन प्रक्रिया (How to Apply)" else "Step-by-Step Application Process",
                    icon = Icons.Default.FormatListNumbered,
                    iconColor = GovNavyPrimary,
                    isExpanded = expandedHowToApply,
                    onToggle = { expandedHowToApply = !expandedHowToApply }
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        activeScheme.applicationSteps.forEachIndexed { index, step ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Surface(
                                    color = GovNavyPrimary,
                                    shape = CircleShape,
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "${index + 1}",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            )
                                        )
                                    }
                                }

                                Text(
                                    text = step,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF334155),
                                        fontSize = 12.5.sp,
                                        lineHeight = 17.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // SECTION 11: APPLICATION MODE
            // -------------------------------------------------------------
            item {
                ExpandableDetailsSection(
                    title = if (currentLanguage == AppLanguage.HINDI) "आवेदन के माध्यम (Application Modes)" else "Application Channels & Modes",
                    icon = Icons.Default.Devices,
                    iconColor = Color(0xFF4338CA),
                    isExpanded = expandedApplicationMode,
                    onToggle = { expandedApplicationMode = !expandedApplicationMode }
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        activeScheme.applicationModes.forEach { mode ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Sensors,
                                    contentDescription = null,
                                    tint = Color(0xFF4338CA),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = mode,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF334155),
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // SECTION 12: IMPORTANT DATES
            // -------------------------------------------------------------
            item {
                ExpandableDetailsSection(
                    title = if (currentLanguage == AppLanguage.HINDI) "महत्वपूर्ण तिथियां (Important Dates)" else "Important Dates & Disbursement Schedules",
                    icon = Icons.Default.CalendarMonth,
                    iconColor = GovSaffron,
                    isExpanded = expandedImportantDates,
                    onToggle = { expandedImportantDates = !expandedImportantDates }
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        activeScheme.importantDates.forEach { (label, date) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF475569),
                                        fontSize = 11.5.sp
                                    )
                                )
                                Surface(
                                    color = Color(0xFFF1F5F9),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = date,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
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

            // -------------------------------------------------------------
            // SECTION 13: FAQS (ACCORDION)
            // -------------------------------------------------------------
            item {
                ExpandableDetailsSection(
                    title = if (currentLanguage == AppLanguage.HINDI) "अक्सर पूछे जाने वाले प्रश्न (FAQs)" else "Frequently Asked Questions (FAQs)",
                    icon = Icons.AutoMirrored.Filled.Help,
                    iconColor = Color(0xFF0E7490),
                    isExpanded = expandedFaqs,
                    onToggle = { expandedFaqs = !expandedFaqs }
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        activeScheme.faqs.forEachIndexed { index, faq ->
                            val isFaqOpen = expandedFaqIndex == index
                            Surface(
                                color = Color(0xFFF8FAFC),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        expandedFaqIndex = if (isFaqOpen) -1 else index
                                    }
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = if (currentLanguage == AppLanguage.HINDI) faq.questionHi else faq.questionEn,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF0F172A)
                                            ),
                                            modifier = Modifier.weight(1f)
                                        )
                                        Icon(
                                            imageVector = if (isFaqOpen) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                            contentDescription = null,
                                            tint = Color(0xFF64748B),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    if (isFaqOpen) {
                                        HorizontalDivider(color = Color(0xFFE2E8F0), modifier = Modifier.padding(vertical = 4.dp))
                                        Text(
                                            text = if (currentLanguage == AppLanguage.HINDI) faq.answerHi else faq.answerEn,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = Color(0xFF475569),
                                                fontSize = 11.5.sp,
                                                lineHeight = 16.sp
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
            // SECTION 14: HELPLINE & CITIZEN GRIEVANCE
            // -------------------------------------------------------------
            item {
                ExpandableDetailsSection(
                    title = if (currentLanguage == AppLanguage.HINDI) "हेल्पलाइन व सहायता (Helpline & Support)" else "Helpline, Redressal & Support",
                    icon = Icons.Default.Phone,
                    iconColor = Color(0xFF15803D),
                    isExpanded = expandedHelpline,
                    onToggle = { expandedHelpline = !expandedHelpline }
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SupportAgent,
                                contentDescription = null,
                                tint = Color(0xFF15803D),
                                modifier = Modifier.size(18.dp)
                            )
                            Column {
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) "टोल-फ्री हेल्पलाइन नंबर:" else "Toll-Free Helpline Number:",
                                    style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF64748B))
                                )
                                Text(
                                    text = activeScheme.tollFreeHelpline,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F172A)
                                    )
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                tint = GovNavyPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Column {
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) "आधिकारिक ईमेल सहायता:" else "Official Email Support:",
                                    style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF64748B))
                                )
                                Text(
                                    text = activeScheme.supportEmail,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GovNavyPrimary
                                    )
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Gavel,
                                contentDescription = null,
                                tint = GovSaffron,
                                modifier = Modifier.size(18.dp)
                            )
                            Column {
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI) "शिकायत निवारण पोर्टल (Grievance):" else "Citizen Grievance Portal:",
                                    style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF64748B))
                                )
                                Text(
                                    text = activeScheme.grievancePortal,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF2563EB),
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // -------------------------------------------------------------
            // SECTION 15: OFFICIAL SOURCE
            // -------------------------------------------------------------
            item {
                ExpandableDetailsSection(
                    title = if (currentLanguage == AppLanguage.HINDI) "आधिकारिक स्रोत (Official Source)" else "Official Government Source & Gazette",
                    icon = Icons.AutoMirrored.Filled.OpenInNew,
                    iconColor = GovNavyPrimary,
                    isExpanded = expandedOfficialSource,
                    onToggle = { expandedOfficialSource = !expandedOfficialSource }
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI)
                                "यह योजना आधिकारिक भारत सरकार/राज्य सरकार के राजपत्र एवं पोर्टल के तहत सत्यापित है।"
                            else
                                "Information verified from official Government Gazette and Sponsoring Ministry Portal.",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF475569))
                        )

                        Surface(
                            color = Color(0xFFEFF6FF),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = null,
                                        tint = GovNavyPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = activeScheme.officialWebsiteUrl,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = GovNavyPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.5.sp
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Surface(
                                    color = GovNavyPrimary,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Verified ↗",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = Color.White,
                                                fontSize = 9.5.sp,
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
    }

    // -------------------------------------------------------------
    // MODAL DIALOG: SIMULATED APPLICATION PORTAL
    // -------------------------------------------------------------
    if (showApplyModal) {
        Dialog(onDismissRequest = { showApplyModal = false }) {
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
                            Surface(
                                color = Color(0xFFDCFCE7),
                                shape = CircleShape,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Launch,
                                        contentDescription = null,
                                        tint = Color(0xFF15803D),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI) "आवेदन पोर्टल पर जाएं" else "Proceed to Official Portal",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GovNavyPrimary
                                )
                            )
                        }

                        IconButton(onClick = { showApplyModal = false }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color(0xFF64748B)
                            )
                        }
                    }

                    HorizontalDivider(color = Color(0xFFE2E8F0))

                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI)
                            "आप आधिकारिक सरकारी पोर्टल ${activeScheme.officialWebsiteUrl} पर रीडायरेक्ट होने जा रहे हैं। कृपया आधार से जुड़े मोबाइल फोन को अपने पास रखें।"
                        else
                            "You are proceeding to the official government portal ${activeScheme.officialWebsiteUrl}. Keep your Aadhaar-linked mobile phone nearby for OTP verification.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF334155),
                            lineHeight = 17.sp
                        )
                    )

                    Surface(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "🔒 100% Official & Encrypted Gateway",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF15803D),
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "National Informatics Centre (NIC) / Government of India",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF64748B),
                                    fontSize = 10.5.sp
                                )
                            )
                        }
                    }

                    Button(
                        onClick = { showApplyModal = false },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GovNavyPrimary)
                    ) {
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI) "पोर्टल खोलें ↗" else "Launch Official Portal ↗",
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

    // Share Confirmation Dialog
    if (showShareDialog) {
        Dialog(onDismissRequest = { showShareDialog = false }) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        tint = GovNavyPrimary,
                        modifier = Modifier.size(36.dp)
                    )
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI) "योजना साझा करें" else "Share Scheme Details",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = GovNavyPrimary
                        )
                    )
                    Text(
                        text = "${activeScheme.nameEn}\n${activeScheme.officialWebsiteUrl}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF64748B),
                            textAlign = TextAlign.Center
                        )
                    )
                    Button(
                        onClick = { showShareDialog = false },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GovNavyPrimary)
                    ) {
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI) "लिंक कॉपी करें ✓" else "Copy Link ✓",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// EXPANDABLE DETAILS SECTION COMPONENT
// -------------------------------------------------------------

@Composable
fun ExpandableDetailsSection(
    title: String,
    icon: ImageVector,
    iconColor: Color,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
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
            // Header Row (Clickable to expand/collapse)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = GovNavyPrimary,
                            fontSize = 14.5.sp
                        )
                    )
                }

                IconButton(
                    onClick = onToggle,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = Color(0xFF64748B)
                    )
                }
            }

            // Expandable Content
            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    content()
                }
            }
        }
    }
}
