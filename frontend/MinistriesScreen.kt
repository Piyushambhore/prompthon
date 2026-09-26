package org.jansaarthi.app.ui.screens.ministries

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
import org.jansaarthi.app.ui.screens.schemes.SchemeListScreen
import org.jansaarthi.app.ui.theme.*

/**
 * ----------------------------------------------------------------------------
 * JanSaarthi "Ministries & Departments" Browsing Screen (Frontend UI Only)
 * ----------------------------------------------------------------------------
 * Dedicated directory allowing citizens to explore government welfare programs
 * by sponsoring Union Ministry and constituent administrative Departments.
 *
 * Core Mandated Features:
 * - Search ministry bar (live query across English/Hindi titles, departments, acronyms)
 * - Ministry cards with official branding, icons, and soft themed borders
 * - Explicit list of constituent Government Departments under each ministry
 * - Scheme count placeholder badge (e.g. "28 Schemes", "22 Schemes")
 * - Interactive tap action: Opens its Scheme List screen using the reusable SchemeListScreen UI
 *
 * Frontend UI Only: Uses realistic local mock data with explicit prototype evaluation disclaimer.
 * No backend, database, Firebase, or external API code.
 */

// -------------------------------------------------------------
// DATA MODELS FOR MINISTRIES & DEPARTMENTS
// -------------------------------------------------------------

data class GovernmentDepartment(
    val nameEn: String,
    val nameHi: String,
    val shortCode: String,
    val roleEn: String
) {
    fun getName(lang: AppLanguage): String = if (lang == AppLanguage.HINDI) nameHi else nameEn
}

data class MinistryItem(
    val id: String,
    val nameEn: String,
    val nameHi: String,
    val acronym: String,
    val departments: List<GovernmentDepartment>,
    val schemeCount: Int,
    val icon: ImageVector,
    val accentColor: Color,
    val tintColor: Color,
    val borderColor: Color,
    val descriptionEn: String,
    val descriptionHi: String,
    val flagshipSchemes: List<String>,
    val websiteUrl: String
) {
    fun getName(lang: AppLanguage): String = if (lang == AppLanguage.HINDI) nameHi else nameEn
    fun getDescription(lang: AppLanguage): String = if (lang == AppLanguage.HINDI) descriptionHi else descriptionEn
}

// -------------------------------------------------------------
// REALISTIC MOCK DATASET FOR 15 UNION MINISTRIES & DEPARTMENTS
// -------------------------------------------------------------

val OFFICIAL_GOVERNMENT_MINISTRIES: List<MinistryItem> = listOf(
    MinistryItem(
        id = "MIN-01",
        nameEn = "Ministry of Agriculture & Farmers Welfare",
        nameHi = "कृषि एवं किसान कल्याण मंत्रालय",
        acronym = "MoA&FW",
        departments = listOf(
            GovernmentDepartment(
                nameEn = "Department of Agriculture and Farmers Welfare (DA&FW)",
                nameHi = "कृषि एवं किसान कल्याण विभाग",
                shortCode = "DA&FW",
                roleEn = "Crop production, credit, agricultural inputs and farmer income support"
            ),
            GovernmentDepartment(
                nameEn = "Department of Agricultural Research and Education (DARE)",
                nameHi = "कृषि अनुसंधान एवं शिक्षा विभाग",
                shortCode = "DARE",
                roleEn = "ICAR institutes, agricultural universities, and soil sciences"
            )
        ),
        schemeCount = 28,
        icon = Icons.Default.Agriculture,
        accentColor = Color(0xFF15803D),
        tintColor = Color(0xFFF0FDF4),
        borderColor = Color(0xFFBBF7D0),
        descriptionEn = "Formulating national policies on agricultural growth, credit, farm income assurance, and crop loss risk management.",
        descriptionHi = "कृषि विकास, संस्थागत ऋण, किसान आय सुरक्षा एवं फसल जोखिम प्रबंधन पर राष्ट्रीय नीतियां।",
        flagshipSchemes = listOf("PM Kisan Samman Nidhi (PM-KISAN)", "PM Fasal Bima Yojana", "Kisan Credit Card (KCC)", "PM Krishi Sinchayee Yojana"),
        websiteUrl = "https://agricoop.nic.in"
    ),
    MinistryItem(
        id = "MIN-02",
        nameEn = "Ministry of Finance",
        nameHi = "वित्त मंत्रालय",
        acronym = "MoF",
        departments = listOf(
            GovernmentDepartment(
                nameEn = "Department of Financial Services (DFS)",
                nameHi = "वित्तीय सेवाएं विभाग",
                shortCode = "DFS",
                roleEn = "Banking operations, micro-credit, social security pensions and insurance"
            ),
            GovernmentDepartment(
                nameEn = "Department of Economic Affairs (DEA)",
                nameHi = "आर्थिक कार्य विभाग",
                shortCode = "DEA",
                roleEn = "Macro-economic monitoring, capital markets and fiscal management"
            ),
            GovernmentDepartment(
                nameEn = "Department of Revenue (DoR)",
                nameHi = "राजस्व विभाग",
                shortCode = "DoR",
                roleEn = "Direct & indirect taxation, GST administration and tax incentives"
            )
        ),
        schemeCount = 22,
        icon = Icons.Default.AccountBalance,
        accentColor = Color(0xFF3730A3),
        tintColor = Color(0xFFEEF2FF),
        borderColor = Color(0xFFC7D2FE),
        descriptionEn = "Administers sovereign revenue, national banking systems, affordable loans, pensions, and financial inclusion welfare.",
        descriptionHi = "राष्ट्रीय बैंकिंग प्रणाली, रियायती ऋण, सामाजिक पेंशन, बीमा और वित्तीय समावेशन का संचालन।",
        flagshipSchemes = listOf("Pradhan Mantri Mudra Yojana (PMMY)", "Atal Pension Yojana (APY)", "PM Jan Dhan Yojana (PMJDY)", "PM Suraksha Bima Yojana"),
        websiteUrl = "https://financialservices.gov.in"
    ),
    MinistryItem(
        id = "MIN-03",
        nameEn = "Ministry of Health & Family Welfare",
        nameHi = "स्वास्थ्य एवं परिवार कल्याण मंत्रालय",
        acronym = "MoHFW",
        departments = listOf(
            GovernmentDepartment(
                nameEn = "Department of Health and Family Welfare (DoHFW)",
                nameHi = "स्वास्थ्य एवं परिवार कल्याण विभाग",
                shortCode = "DoHFW",
                roleEn = "Public hospitals, immunization, cashless insurance and disease control"
            ),
            GovernmentDepartment(
                nameEn = "Department of Health Research (DHR)",
                nameHi = "स्वास्थ्य अनुसंधान विभाग",
                shortCode = "DHR",
                roleEn = "ICMR, clinical trials, epidemiology and modern healthcare innovation"
            )
        ),
        schemeCount = 26,
        icon = Icons.Default.HealthAndSafety,
        accentColor = Color(0xFF0F766E),
        tintColor = Color(0xFFF0FDFA),
        borderColor = Color(0xFF99F6E4),
        descriptionEn = "Delivering universal health coverage, cashless secondary/tertiary hospitalisation, and primary preventive care across India.",
        descriptionHi = "सार्वभौमिक स्वास्थ्य सुरक्षा, कैशलेस अस्पताल उपचार और प्राथमिक स्वास्थ्य सेवाओं की राष्ट्रीय व्यवस्था।",
        flagshipSchemes = listOf("Ayushman Bharat PM-JAY", "National Health Mission (NHM)", "PM Ayushman Bharat Health Infrastructure (PM-ABHIM)", "Pradhan Mantri Jan Aushadhi"),
        websiteUrl = "https://mohfw.gov.in"
    ),
    MinistryItem(
        id = "MIN-04",
        nameEn = "Ministry of Rural Development",
        nameHi = "ग्रामीण विकास मंत्रालय",
        acronym = "MoRD",
        departments = listOf(
            GovernmentDepartment(
                nameEn = "Department of Rural Development (DoRD)",
                nameHi = "ग्रामीण विकास विभाग",
                shortCode = "DoRD",
                roleEn = "Rural pucca housing, wage employment, women self-help groups and roads"
            ),
            GovernmentDepartment(
                nameEn = "Department of Land Resources (DoLR)",
                nameHi = "भूमि संसाधन विभाग",
                shortCode = "DoLR",
                roleEn = "Digital India Land Records Modernization Programme (DILRMP) and watershed"
            )
        ),
        schemeCount = 19,
        icon = Icons.Default.Terrain,
        accentColor = Color(0xFFB45309),
        tintColor = Color(0xFFFFFBEB),
        borderColor = Color(0xFFFDE68A),
        descriptionEn = "Eradicating rural poverty through guaranteed pucca housing, wage employment, all-weather connectivity, and self-help livelihoods.",
        descriptionHi = "पक्का आवास, 100 दिन का रोजगार, बारहमासी सड़कें और महिला स्वयं सहायता समूहों द्वारा ग्रामीण उत्थान।",
        flagshipSchemes = listOf("Pradhan Mantri Awas Yojana - Gramin", "MGNREGA 100-Day Wage Guarantee", "DAY-NRLM (Aajeevika)", "PM Gram Sadak Yojana (PMGSY)"),
        websiteUrl = "https://rural.gov.in"
    ),
    MinistryItem(
        id = "MIN-05",
        nameEn = "Ministry of Education",
        nameHi = "शिक्षा मंत्रालय",
        acronym = "MoE",
        departments = listOf(
            GovernmentDepartment(
                nameEn = "Department of School Education and Literacy (DoSEL)",
                nameHi = "स्कूल शिक्षा और साक्षरता विभाग",
                shortCode = "DoSEL",
                roleEn = "Universal primary/secondary schooling, nutrition meals, and NEP 2020"
            ),
            GovernmentDepartment(
                nameEn = "Department of Higher Education (DHE)",
                nameHi = "उच्चतर शिक्षा विभाग",
                shortCode = "DHE",
                roleEn = "Central universities, IITs/IIMs, National Scholarship Portal and research"
            )
        ),
        schemeCount = 31,
        icon = Icons.Default.School,
        accentColor = Color(0xFF1D4ED8),
        tintColor = Color(0xFFEFF6FF),
        borderColor = Color(0xFFBFDBFE),
        descriptionEn = "Ensuring quality education, school nutritional meals, merit scholarships, and inclusive university access under NEP 2020.",
        descriptionHi = "गुणवत्तापूर्ण शिक्षा, स्कूलों में पौष्टिक भोजन, योग्यता छात्रवृत्ति और राष्ट्रीय शिक्षा नीति 2020 का कार्यान्वयन।",
        flagshipSchemes = listOf("PM POSHAN (Mid-Day Meal Scheme)", "Samagra Shiksha Abhiyan", "National Means-cum-Merit Scholarship", "PM Research Fellowship (PMRF)"),
        websiteUrl = "https://education.gov.in"
    ),
    MinistryItem(
        id = "MIN-06",
        nameEn = "Ministry of Women and Child Development",
        nameHi = "महिला एवं बाल विकास मंत्रालय",
        acronym = "MWCD",
        departments = listOf(
            GovernmentDepartment(
                nameEn = "Department of Women and Child Development",
                nameHi = "महिला एवं बाल विकास विभाग",
                shortCode = "MWCD",
                roleEn = "Maternal nutrition, Anganwadi network, girl child education and protection"
            )
        ),
        schemeCount = 24,
        icon = Icons.Default.FamilyRestroom,
        accentColor = Color(0xFFBE185D),
        tintColor = Color(0xFFFDF2F8),
        borderColor = Color(0xFFFBCFE8),
        descriptionEn = "Promoting holistic physical and socio-economic advancement of women, adolescent girls, and children across all states.",
        descriptionHi = "महिलाओं, किशोरियों और बच्चों के सर्वांगीण विकास, पोषण एवं सुरक्षा हेतु समर्पित राष्ट्रीय नीतियां।",
        flagshipSchemes = listOf("Beti Bachao Beti Padhao", "Mission Poshan 2.0 (Anganwadi)", "Pradhan Mantri Matru Vandana Yojana", "Mission Shakti"),
        websiteUrl = "https://wcd.nic.in"
    ),
    MinistryItem(
        id = "MIN-07",
        nameEn = "Ministry of Social Justice & Empowerment",
        nameHi = "सामाजिक न्याय एवं अधिकारिता मंत्रालय",
        acronym = "MoSJE",
        departments = listOf(
            GovernmentDepartment(
                nameEn = "Department of Social Justice and Empowerment (DoSJE)",
                nameHi = "सामाजिक न्याय और अधिकारिता विभाग",
                shortCode = "DoSJE",
                roleEn = "Welfare of SC, OBC, EWS, senior citizens, and de-addiction programs"
            ),
            GovernmentDepartment(
                nameEn = "Department of Empowerment of Persons with Disabilities (DEPwD)",
                nameHi = "दिव्यांगजन सशक्तिकरण विभाग",
                shortCode = "DEPwD",
                roleEn = "Divyangjan welfare, UDID disability card, assistive aids and accessibility"
            )
        ),
        schemeCount = 35,
        icon = Icons.Default.Elderly,
        accentColor = Color(0xFF6D28D9),
        tintColor = Color(0xFFFAF5FF),
        borderColor = Color(0xFFE9D5FF),
        descriptionEn = "Affirmative action, pension coverage, and assistive aids for SC, OBC, transgender persons, senior citizens, and persons with disabilities.",
        descriptionHi = "अनुसूचित जाति, अन्य पिछड़ा वर्ग, वरिष्ठ नागरिकों और दिव्यांगजनों के सशक्तिकरण व सामाजिक सुरक्षा योजनाएं।",
        flagshipSchemes = listOf("National Social Assistance Programme (NSAP)", "ADIP Scheme (Divyang Assistive Aids)", "PM-DAKSH Skill Development", "Rashtriya Vayoshri Yojana"),
        websiteUrl = "https://socialjustice.gov.in"
    ),
    MinistryItem(
        id = "MIN-08",
        nameEn = "Ministry of Micro, Small and Medium Enterprises",
        nameHi = "सूक्ष्म, लघु एवं मध्यम उद्यम मंत्रालय",
        acronym = "MSME",
        departments = listOf(
            GovernmentDepartment(
                nameEn = "Office of Development Commissioner (DC-MSME)",
                nameHi = "विकास आयुक्त कार्यालय (सूक्ष्म, लघु एवं मध्यम उद्यम)",
                shortCode = "DC-MSME",
                roleEn = "Enterprise subsidies, cluster development, technology centers and Udyam"
            ),
            GovernmentDepartment(
                nameEn = "National Small Industries Corporation (NSIC)",
                nameHi = "राष्ट्रीय लघु उद्योग निगम",
                shortCode = "NSIC",
                roleEn = "Raw material assistance, credit syndication, and marketing support"
            )
        ),
        schemeCount = 18,
        icon = Icons.Default.BusinessCenter,
        accentColor = Color(0xFFC2410C),
        tintColor = Color(0xFFFFF7ED),
        borderColor = Color(0xFFFED7AA),
        descriptionEn = "Supporting traditional artisans, micro-entrepreneurs, and small industrial units with subsidized loans, tools, and market linkages.",
        descriptionHi = "कारीगरों, शिल्पकारों व लघु उद्यमियों को रियायती पूंजी, आधुनिक औजार व तकनीकी सहायता प्रदान करना।",
        flagshipSchemes = listOf("PM Vishwakarma Scheme", "Prime Minister Employment Generation (PMEGP)", "Credit Guarantee Trust (CGTMSE)", "MSME Champions ZED"),
        websiteUrl = "https://msme.gov.in"
    ),
    MinistryItem(
        id = "MIN-09",
        nameEn = "Ministry of Electronics & Information Technology",
        nameHi = "इलेक्ट्रॉनिक्स एवं सूचना प्रौद्योगिकी मंत्रालय",
        acronym = "MeitY",
        departments = listOf(
            GovernmentDepartment(
                nameEn = "National Informatics Centre (NIC)",
                nameHi = "राष्ट्रीय सूचना विज्ञान केंद्र",
                shortCode = "NIC",
                roleEn = "Government ICT backbone, e-governance infrastructure and digital services"
            ),
            GovernmentDepartment(
                nameEn = "Digital India Corporation (DIC)",
                nameHi = "डिजिटल इंडिया कॉर्पोरेशन",
                shortCode = "DIC",
                roleEn = "Grassroots digital delivery, DigiLocker, UMANG and AI governance"
            )
        ),
        schemeCount = 15,
        icon = Icons.Default.Devices,
        accentColor = Color(0xFF0284C7),
        tintColor = Color(0xFFF0F9FF),
        borderColor = Color(0xFFBAE6FD),
        descriptionEn = "Driving Digital India initiatives, public digital public infrastructure, digital literacy, and electronic governance for citizens.",
        descriptionHi = "डिजिटल इंडिया कार्यक्रम, लोक डिजिटल अवसंरचना, डिजिटल साक्षरता और इलेक्ट्रॉनिक नागरिक सेवाओं का विस्तार।",
        flagshipSchemes = listOf("Pradhan Mantri Gramin Digital Saksharta Abhiyan (PMGDISHA)", "DigiLocker Ecosystem", "UMANG Citizen Services App", "Bhashini Indian Language AI"),
        websiteUrl = "https://meity.gov.in"
    ),
    MinistryItem(
        id = "MIN-10",
        nameEn = "Ministry of Jal Shakti",
        nameHi = "जल शक्ति मंत्रालय",
        acronym = "MoJS",
        departments = listOf(
            GovernmentDepartment(
                nameEn = "Department of Drinking Water and Sanitation (DDWS)",
                nameHi = "पेयजल एवं स्वच्छता विभाग",
                shortCode = "DDWS",
                roleEn = "Har Ghar Nal Se Jal tap water supply and rural Swachh Bharat cleanliness"
            ),
            GovernmentDepartment(
                nameEn = "Department of Water Resources, River Development & Ganga Rejuvenation",
                nameHi = "जल संसाधन, नदी विकास और गंगा संरक्षण विभाग",
                shortCode = "DoWR",
                roleEn = "Groundwater recharge, irrigation projects and river conservation"
            )
        ),
        schemeCount = 16,
        icon = Icons.Default.WaterDrop,
        accentColor = Color(0xFF0369A1),
        tintColor = Color(0xFFF0F9FF),
        borderColor = Color(0xFFBAE6FD),
        descriptionEn = "Providing functional piped tap water to every rural household and sustaining national open-defecation-free sanitation status.",
        descriptionHi = "प्रत्येक ग्रामीण घर तक नल से जल पहुंचाना और स्थायी स्वच्छता व जल संरक्षण सुनिश्चित करना।",
        flagshipSchemes = listOf("Jal Jeevan Mission (Har Ghar Jal)", "Swachh Bharat Mission - Grameen", "Namami Gange Mission", "Atal Bhujal Yojana"),
        websiteUrl = "https://jalshakti-ddws.gov.in"
    ),
    MinistryItem(
        id = "MIN-11",
        nameEn = "Ministry of Housing and Urban Affairs",
        nameHi = "आवासन और शहरी कार्य मंत्रालय",
        acronym = "MoHUA",
        departments = listOf(
            GovernmentDepartment(
                nameEn = "Central Public Works Department (CPWD)",
                nameHi = "केंद्रीय लोक निर्माण विभाग",
                shortCode = "CPWD",
                roleEn = "Urban public infrastructure, green housing, and civil engineering works"
            ),
            GovernmentDepartment(
                nameEn = "Urban Poverty Alleviation Division",
                nameHi = "शहरी गरीबी उपशमन प्रभाग",
                shortCode = "UPA",
                roleEn = "Urban poor shelter, micro-credit for street vendors, and DAY-NULM"
            )
        ),
        schemeCount = 14,
        icon = Icons.Default.Apartment,
        accentColor = Color(0xFF334155),
        tintColor = Color(0xFFF8FAFC),
        borderColor = Color(0xFFCBD5E1),
        descriptionEn = "Affordable pucca housing for the urban poor, street vendor working capital loans, metro transit, and smart urban infrastructure.",
        descriptionHi = "शहरी गरीबों हेतु पक्का आवास, रेहड़ी-पटरी वालों के लिए कार्यशील पूंजी ऋण और आधुनिक शहरी सुविधाएं।",
        flagshipSchemes = listOf("Pradhan Mantri Awas Yojana - Urban (PMAY-U)", "PM Street Vendor's AtmaNirbhar Nidhi (PM SVANidhi)", "Smart Cities Mission", "AMRUT 2.0"),
        websiteUrl = "https://mohua.gov.in"
    ),
    MinistryItem(
        id = "MIN-12",
        nameEn = "Ministry of Skill Development and Entrepreneurship",
        nameHi = "कौशल विकास एवं उद्यमिता मंत्रालय",
        acronym = "MSDE",
        departments = listOf(
            GovernmentDepartment(
                nameEn = "Directorate General of Training (DGT)",
                nameHi = "प्रशिक्षण महानिदेशालय",
                shortCode = "DGT",
                roleEn = "Industrial Training Institutes (ITIs), crafts training, and apprenticeships"
            ),
            GovernmentDepartment(
                nameEn = "National Skill Development Corporation (NSDC)",
                nameHi = "राष्ट्रीय कौशल विकास निगम",
                shortCode = "NSDC",
                roleEn = "Industry-aligned vocational certification, skill hubs, and global placement"
            )
        ),
        schemeCount = 20,
        icon = Icons.Default.Handyman,
        accentColor = Color(0xFFD97706),
        tintColor = Color(0xFFFFFBEB),
        borderColor = Color(0xFFFDE68A),
        descriptionEn = "Equipping Indian youth with industry-relevant vocational skills, standardized certification, and paid apprenticeships.",
        descriptionHi = "भारतीय युवाओं को उद्योग-उन्मुख कौशल प्रशिक्षण, मान्यता प्राप्त प्रमाण पत्र और शिक्षुता (अप्रेंटिसशिप) प्रदान करना।",
        flagshipSchemes = listOf("Pradhan Mantri Kaushal Vikas Yojana (PMKVY 4.0)", "National Apprenticeship Promotion Scheme (NAPS)", "Jan Shikshan Sansthan (JSS)", "Craftsmen Training Scheme (CTS)"),
        websiteUrl = "https://msde.gov.in"
    ),
    MinistryItem(
        id = "MIN-13",
        nameEn = "Ministry of Labour and Employment",
        nameHi = "श्रम एवं रोजगार मंत्रालय",
        acronym = "MoLE",
        departments = listOf(
            GovernmentDepartment(
                nameEn = "Employees' Provident Fund Organisation (EPFO)",
                nameHi = "कर्मचारी भविष्य निधि संगठन",
                shortCode = "EPFO",
                roleEn = "Universal retirement provident funds, pension schemes and insurance"
            ),
            GovernmentDepartment(
                nameEn = "Employees' State Insurance Corporation (ESIC)",
                nameHi = "कर्मचारी राज्य बीमा निगम",
                shortCode = "ESIC",
                roleEn = "Social security healthcare, sickness benefits and maternity coverage"
            )
        ),
        schemeCount = 17,
        icon = Icons.Default.Engineering,
        accentColor = Color(0xFF1E293B),
        tintColor = Color(0xFFF1F5F9),
        borderColor = Color(0xFFCBD5E1),
        descriptionEn = "Safeguarding the welfare of organized and unorganized workers, pension safety nets, and formal employment generation.",
        descriptionHi = "संगठित एवं असंगठित क्षेत्र के श्रमिकों का कल्याण, पेंशन सुरक्षा और औपचारिक रोजगार सृजन।",
        flagshipSchemes = listOf("e-Shram National Worker Database", "Pradhan Mantri Shram Yogi Maandhan (PM-SYM)", "Aatmanirbhar Bharat Rojgar Yojana", "Pradhan Mantri Rojgar Protsahan"),
        websiteUrl = "https://labour.gov.in"
    ),
    MinistryItem(
        id = "MIN-14",
        nameEn = "Ministry of Law and Justice",
        nameHi = "विधि एवं न्याय मंत्रालय",
        acronym = "MoLJ",
        departments = listOf(
            GovernmentDepartment(
                nameEn = "Department of Justice (DoJ)",
                nameHi = "न्याय विभाग",
                shortCode = "DoJ",
                roleEn = "Tele-Law free legal aid, legal literacy, and judicial infrastructure"
            ),
            GovernmentDepartment(
                nameEn = "Legislative Department",
                nameHi = "विधायी विभाग",
                shortCode = "Legis",
                roleEn = "Drafting parliamentary legislation and statutory reforms"
            )
        ),
        schemeCount = 11,
        icon = Icons.Default.Gavel,
        accentColor = Color(0xFF991B1B),
        tintColor = Color(0xFFFEF2F2),
        borderColor = Color(0xFFFECACA),
        descriptionEn = "Ensuring equal justice for all citizens through free panel lawyer consultations, grassroots legal awareness, and mobile legal aid.",
        descriptionHi = "मुफ़्त कानूनी परामर्श, विधिक जागरूकता और टेली-लॉ सेवाओं द्वारा प्रत्येक नागरिक को सुलभ न्याय।",
        flagshipSchemes = listOf("Tele-Law Citizen Advice via CSC", "Nyaya Bandhu (Pro Bono Legal Services)", "Fast Track Special Courts (FTSCs)", "Gram Nyayalayas Scheme"),
        websiteUrl = "https://lawmin.gov.in"
    ),
    MinistryItem(
        id = "MIN-15",
        nameEn = "Ministry of Youth Affairs and Sports",
        nameHi = "युवा कार्यक्रम एवं खेल मंत्रालय",
        acronym = "MYAS",
        departments = listOf(
            GovernmentDepartment(
                nameEn = "Department of Sports",
                nameHi = "खेल विभाग",
                shortCode = "Sports",
                roleEn = "Olympic training, talent identification, SAI academies, and stadium infrastructure"
            ),
            GovernmentDepartment(
                nameEn = "Department of Youth Affairs",
                nameHi = "युवा कार्यक्रम विभाग",
                shortCode = "Youth",
                roleEn = "NSS volunteerism, Nehru Yuva Kendra Sangathan, and youth leadership"
            )
        ),
        schemeCount = 12,
        icon = Icons.Default.EmojiEvents,
        accentColor = Color(0xFFEA580C),
        tintColor = Color(0xFFFFF7ED),
        borderColor = Color(0xFFFED7AA),
        descriptionEn = "Fostering grassroots sporting talent, national fitness, Olympic podium excellence, and youth nation-building leadership.",
        descriptionHi = "जमीनी खेल प्रतिभाओं की पहचान, ओलंपिक तैयारी, राष्ट्रीय फिटनेस और युवा नेतृत्व विकास।",
        flagshipSchemes = listOf("Khelo India National Programme", "Target Olympic Podium Scheme (TOPS)", "National Youth Corps (NYC)", "Rashtriya Yuva Sashaktikaran"),
        websiteUrl = "https://yas.nic.in"
    )
)

// -------------------------------------------------------------
// MAIN MINISTRIES & DEPARTMENTS COMPOSABLE
// -------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MinistriesScreen(
    currentLanguage: AppLanguage = AppLanguage.HINDI,
    onSelectLanguage: (AppLanguage) -> Unit = {},
    isLargeFont: Boolean = false,
    onFontScaleToggle: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onSelectMinistry: (MinistryItem) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val fontSizeMultiplier = if (isLargeFont) 1.15f else 1.0f

    // -------------------------------------------------------------
    // LOCAL REACTIVE STATES
    // -------------------------------------------------------------
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilterCategory by remember { mutableStateOf("All") }
    var inspectedMinistryForModal by remember { mutableStateOf<MinistryItem?>(null) }

    // Standalone drilldown state: When tapped, opens reusable SchemeListScreen
    var activeDrilldownMinistry by remember { mutableStateOf<MinistryItem?>(null) }

    // If activeDrilldownMinistry is set, render the reusable SchemeListScreen directly
    if (activeDrilldownMinistry != null) {
        SchemeListScreen(
            initialMinistryName = activeDrilldownMinistry!!.nameEn,
            currentLanguage = currentLanguage,
            onSelectLanguage = onSelectLanguage,
            isLargeFont = isLargeFont,
            onFontScaleToggle = onFontScaleToggle,
            onNavigateBack = { activeDrilldownMinistry = null }
        )
        return
    }

    // Filter ministries based on search query
    val filteredMinistries = remember(searchQuery, selectedFilterCategory, currentLanguage) {
        val q = searchQuery.trim().lowercase()

        OFFICIAL_GOVERNMENT_MINISTRIES.filter { min ->
            val matchesQuery = q.isEmpty() ||
                    min.nameEn.lowercase().contains(q) ||
                    min.nameHi.lowercase().contains(q) ||
                    min.acronym.lowercase().contains(q) ||
                    min.descriptionEn.lowercase().contains(q) ||
                    min.descriptionHi.lowercase().contains(q) ||
                    min.departments.any { dept ->
                        dept.nameEn.lowercase().contains(q) ||
                                dept.nameHi.lowercase().contains(q) ||
                                dept.shortCode.lowercase().contains(q) ||
                                dept.roleEn.lowercase().contains(q)
                    } ||
                    min.flagshipSchemes.any { it.lowercase().contains(q) }

            val matchesFilter = when (selectedFilterCategory) {
                "All" -> true
                "Rural & Agriculture" -> min.id == "MIN-01" || min.id == "MIN-04" || min.id == "MIN-10"
                "Social & Health" -> min.id == "MIN-03" || min.id == "MIN-06" || min.id == "MIN-07"
                "Economy & MSME" -> min.id == "MIN-02" || min.id == "MIN-08" || min.id == "MIN-13"
                "Education & Skills" -> min.id == "MIN-05" || min.id == "MIN-12" || min.id == "MIN-09"
                else -> true
            }

            matchesQuery && matchesFilter
        }
    }

    val totalSchemesCount = remember { OFFICIAL_GOVERNMENT_MINISTRIES.sumOf { it.schemeCount } }
    val totalDepartmentsCount = remember { OFFICIAL_GOVERNMENT_MINISTRIES.sumOf { it.departments.size } }

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
                                    text = if (currentLanguage == AppLanguage.HINDI) "मंत्रालय एवं विभाग" else "Ministries & Departments",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GovNavyPrimary,
                                        fontSize = (16 * fontSizeMultiplier).sp
                                    )
                                )
                                Text(
                                    text = if (currentLanguage == AppLanguage.HINDI)
                                        "भारत सरकार के 15 प्रमुख मंत्रालय • myScheme"
                                    else
                                        "Government of India Portfolios • myScheme",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF64748B),
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        // Badge Counter
                        Surface(
                            color = Color(0xFFEFF6FF),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFFBFDBFE))
                        ) {
                            Text(
                                text = "15 Ministries",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
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
                                "📌 यूआई प्रदर्शन: मंत्रालय व विभाग सूची स्थानीय मॉक डेटा दर्शाती है। किसी भी मंत्रालय पर टैप करके योजना सूची खोलें।"
                            else
                                "📌 UI Demonstration: Browse portfolios using local mock data. Tap any ministry to open its reusable Scheme List.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF92400E),
                                fontSize = 11.sp,
                                lineHeight = 14.sp
                            )
                        )
                    }
                }
            }

            // 3. Search Ministry & Department Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI)
                                "मंत्रालय, विभाग, संक्षिप्त नाम या योजना खोजें..."
                            else
                                "Search ministry, department, acronym or scheme...",
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

            // 4. Quick Domain Category Filter Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val filterOptions = listOf(
                        "All",
                        "Rural & Agriculture",
                        "Social & Health",
                        "Economy & MSME",
                        "Education & Skills"
                    )

                    items(filterOptions) { filterItem ->
                        val isSelected = selectedFilterCategory == filterItem
                        val label = when (filterItem) {
                            "All" -> if (currentLanguage == AppLanguage.HINDI) "सभी मंत्रालय (${OFFICIAL_GOVERNMENT_MINISTRIES.size})" else "All Ministries (${OFFICIAL_GOVERNMENT_MINISTRIES.size})"
                            "Rural & Agriculture" -> if (currentLanguage == AppLanguage.HINDI) "🌾 कृषि व ग्रामीण" else "🌾 Rural & Agri"
                            "Social & Health" -> if (currentLanguage == AppLanguage.HINDI) "🏥 स्वास्थ्य व कल्याण" else "🏥 Health & Social"
                            "Economy & MSME" -> if (currentLanguage == AppLanguage.HINDI) "💼 वित्त व उद्यम" else "💼 Finance & MSME"
                            "Education & Skills" -> if (currentLanguage == AppLanguage.HINDI) "🎓 शिक्षा व कौशल" else "🎓 Education & Skills"
                            else -> filterItem
                        }

                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedFilterCategory = filterItem },
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

            // 5. Aggregate Statistics Header
            item {
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI)
                                    "${filteredMinistries.size} मंत्रालय प्रदर्शित"
                                else
                                    "${filteredMinistries.size} Ministries Displayed",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GovNavyPrimary
                                )
                            )
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI)
                                    "कुल $totalDepartmentsCount विभाग • $totalSchemesCount+ कल्याणकारी योजनाएं"
                                else
                                    "$totalDepartmentsCount Administrative Departments • $totalSchemesCount+ Sponsoring Schemes",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF64748B),
                                    fontSize = 11.sp
                                )
                            )
                        }

                        if (searchQuery.isNotEmpty() || selectedFilterCategory != "All") {
                            TextButton(onClick = {
                                searchQuery = ""
                                selectedFilterCategory = "All"
                            }) {
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
            }

            // 6. Ministry Cards List
            if (filteredMinistries.isEmpty()) {
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
                                .padding(28.dp),
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
                                text = if (currentLanguage == AppLanguage.HINDI)
                                    "कोई मंत्रालय या विभाग नहीं मिला"
                                else
                                    "No ministries or departments found",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF475569)
                                )
                            )
                            Text(
                                text = if (currentLanguage == AppLanguage.HINDI)
                                    "कृपया दूसरा शब्द खोजें या फ़िल्टर रीसेट करें"
                                else
                                    "Try adjusting your search query or reset the filter",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF64748B))
                            )
                        }
                    }
                }
            } else {
                items(filteredMinistries, key = { it.id }) { ministry ->
                    MinistryCard(
                        ministry = ministry,
                        currentLanguage = currentLanguage,
                        fontSizeMultiplier = fontSizeMultiplier,
                        onClick = {
                            // First invoke callback if provided by parent navigation
                            onSelectMinistry(ministry)
                            // Also set local drilldown state so the reusable SchemeListScreen opens immediately
                            activeDrilldownMinistry = ministry
                        },
                        onInspectDetails = {
                            inspectedMinistryForModal = ministry
                        }
                    )
                }
            }
        }
    }

    // -------------------------------------------------------------
    // MINISTRY DETAILS & DEPARTMENTS INSPECTION DIALOG
    // -------------------------------------------------------------
    inspectedMinistryForModal?.let { ministry ->
        Dialog(onDismissRequest = { inspectedMinistryForModal = null }) {
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
                    // Header with Icon & Title
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                color = ministry.tintColor,
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, ministry.borderColor),
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = ministry.icon,
                                        contentDescription = null,
                                        tint = ministry.accentColor,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                            }

                            Column {
                                Text(
                                    text = ministry.getName(currentLanguage),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GovNavyPrimary
                                    )
                                )
                                Text(
                                    text = "${ministry.acronym} • Official Union Portfolio",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF64748B),
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        IconButton(onClick = { inspectedMinistryForModal = null }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color(0xFF64748B)
                            )
                        }
                    }

                    HorizontalDivider(color = Color(0xFFE2E8F0))

                    // Overview
                    Text(
                        text = ministry.getDescription(currentLanguage),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF334155),
                            lineHeight = 16.sp
                        )
                    )

                    // Department Name Section
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI) "संबंधित प्रशासनिक विभाग (${ministry.departments.size})" else "Administrative Departments (${ministry.departments.size})",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = GovNavyPrimary
                        )
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        ministry.departments.forEach { dept ->
                            Surface(
                                color = Color(0xFFF8FAFC),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = dept.getName(currentLanguage),
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF0F172A)
                                            ),
                                            modifier = Modifier.weight(1f)
                                        )
                                        Surface(
                                            color = Color(0xFFEFF6FF),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = dept.shortCode,
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = GovNavyPrimary,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                        }
                                    }
                                    Text(
                                        text = dept.roleEn,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Color(0xFF64748B),
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // Key Flagship Schemes
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI) "प्रमुख प्रायोजित योजनाएं" else "Flagship Sponsoring Schemes",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        ministry.flagshipSchemes.forEach { schemeTitle ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF15803D),
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = schemeTitle,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF1E293B),
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Open Reusable Scheme List CTA
                    Button(
                        onClick = {
                            val targetMinistry = ministry
                            inspectedMinistryForModal = null
                            onSelectMinistry(targetMinistry)
                            activeDrilldownMinistry = targetMinistry
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GovNavyPrimary)
                    ) {
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI)
                                "${ministry.schemeCount} योजनाएं देखें →"
                            else
                                "View All ${ministry.schemeCount} Schemes in this Ministry →",
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

// -------------------------------------------------------------
// MINISTRY CARD COMPONENT
// -------------------------------------------------------------

@Composable
fun MinistryCard(
    ministry: MinistryItem,
    currentLanguage: AppLanguage,
    fontSizeMultiplier: Float,
    onClick: () -> Unit,
    onInspectDetails: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, ministry.borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 1. Header: Icon, Ministry Name, Scheme Count Placeholder
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
                        color = ministry.tintColor,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, ministry.borderColor),
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = ministry.icon,
                                contentDescription = null,
                                tint = ministry.accentColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Column {
                        Text(
                            text = ministry.getName(currentLanguage),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = GovNavyPrimary,
                                fontSize = (14.5f * fontSizeMultiplier).sp
                            ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                color = Color(0xFFF1F5F9),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = ministry.acronym,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFF475569),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp
                                    )
                                )
                            }
                            Text(
                                text = "• ${ministry.departments.size} " + (if (currentLanguage == AppLanguage.HINDI) "विभाग" else "Departments"),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF64748B),
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }

                // Scheme Count Placeholder Badge
                Surface(
                    color = ministry.tintColor,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, ministry.borderColor)
                ) {
                    Text(
                        text = "${ministry.schemeCount} " + (if (currentLanguage == AppLanguage.HINDI) "योजनाएं" else "Schemes"),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = ministry.accentColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.5.sp
                        )
                    )
                }
            }

            // 2. Department Name Section (Prominently showing constituent departments)
            Surface(
                color = Color(0xFFF8FAFC),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI) "🏛️ प्रशासनिक विभाग (Departments):" else "🏛️ Key Departments:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF475569),
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )

                    ministry.departments.forEach { dept ->
                        Text(
                            text = "• ${dept.getName(currentLanguage)}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF0F172A),
                                fontWeight = FontWeight.Medium,
                                fontSize = (11.5f * fontSizeMultiplier).sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // 3. Short description
            Text(
                text = ministry.getDescription(currentLanguage),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF475569),
                    fontSize = 11.5.sp,
                    lineHeight = 15.sp
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // 4. Sample Flagship Scheme Pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ministry.flagshipSchemes.take(2).forEach { scheme ->
                    Surface(
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "📌 $scheme",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF334155),
                                fontSize = 10.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            HorizontalDivider(color = Color(0xFFF1F5F9))

            // 5. Card Bottom Action Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onInspectDetails,
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (currentLanguage == AppLanguage.HINDI) "विभाग विवरण ℹ" else "Dept Info ℹ",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                Button(
                    onClick = onClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GovNavyPrimary),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = if (currentLanguage == AppLanguage.HINDI)
                                "योजनाएं खोलें →"
                            else
                                "Open Scheme List →",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 11.5.sp
                            )
                        )
                    }
                }
            }
        }
    }
}
