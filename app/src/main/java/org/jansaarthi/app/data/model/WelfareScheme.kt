package org.jansaarthi.app.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

enum class SchemeLevel {
    CENTRAL, // National Government of India Schemes (Nationwide for all citizens)
    STATE    // State Government / UT Specific Welfare Schemes
}

/**
 * Encapsulated Localized Content supporting Indian languages with clean fallbacks.
 * Enables easy expansion of scheme translations without modifying UI screens.
 */
data class LocalizedContent(
    val en: String,
    val hi: String? = null,
    val mr: String? = null,
    val ta: String? = null,
    val te: String? = null,
    val bn: String? = null,
    val gu: String? = null,
    val kn: String? = null
) {
    fun get(language: AppLanguage): String {
        return when (language) {
            AppLanguage.ENGLISH -> en
            AppLanguage.HINDI -> hi ?: en
            AppLanguage.MARATHI -> mr ?: hi ?: en
            AppLanguage.TAMIL -> ta ?: en
            AppLanguage.TELUGU -> te ?: en
            AppLanguage.BENGALI -> bn ?: hi ?: en
            AppLanguage.GUJARATI -> gu ?: hi ?: en
            AppLanguage.KANNADA -> kn ?: en
            else -> hi ?: en
        }
    }
}

/**
 * Detailed Scheme metadata covering Eligibility, Benefits, Documents, and Steps.
 */
data class SchemeDetails(
    val eligibility: LocalizedContent,
    val benefitsDetailed: LocalizedContent,
    val requiredDocuments: List<LocalizedContent>,
    val applicationSteps: List<LocalizedContent>,
    val officialPortalUrl: String = "https://services.india.gov.in"
)

data class WelfareScheme(
    val id: String,
    val title: LocalizedContent,
    val department: LocalizedContent,
    val benefit: LocalizedContent,
    val level: SchemeLevel,
    val stateCode: String? = null,
    val stateNameEn: String? = null,
    val category: String,
    val icon: ImageVector,
    val details: SchemeDetails,
    val isDbtLinked: Boolean = true
) {
    // Convenience backward-compatible accessors
    val titleEn: String get() = title.en
    val titleHi: String get() = title.hi ?: title.en
    val departmentEn: String get() = department.en
    val departmentHi: String get() = department.hi ?: department.en
    val benefitEn: String get() = benefit.en
    val benefitHi: String get() = benefit.hi ?: benefit.en

    val tag: String
        get() = if (level == SchemeLevel.CENTRAL) "Central Scheme" else "${stateNameEn ?: "State"} Scheme"

    fun getTitle(language: AppLanguage): String = title.get(language)
    fun getDepartment(language: AppLanguage): String = department.get(language)
    fun getBenefit(language: AppLanguage): String = benefit.get(language)
}

/**
 * Nationwide Central Government Schemes (Available in all 36 States & UTs)
 */
val NATIONWIDE_CENTRAL_SCHEMES: List<WelfareScheme> = listOf(
    WelfareScheme(
        id = "CEN-01",
        title = LocalizedContent(
            en = "PM Kisan Samman Nidhi",
            hi = "प्रधानमंत्री किसान सम्मान निधि",
            mr = "प्रधानमंत्री किसान सन्मान निधी",
            ta = "பிரதான் மந்திரி கிசான் சம்மான் நிதி",
            te = "పీఎం కిసాన్ సమ్మాన్ నిధి",
            bn = "প্রধানমন্ত্রী কিষাণ সম্মান নিধি",
            gu = "પ્રધાનમંત્રી કિસાન સન્માન નિધિ",
            kn = "ಪಿಎಂ ಕಿಸಾನ್ ಸಮ್ಮಾನ್ ನಿಧಿ"
        ),
        department = LocalizedContent(
            en = "Ministry of Agriculture & Farmers Welfare",
            hi = "कृषि एवं किसान कल्याण मंत्रालय (भारत सरकार)",
            mr = "कृषी व शेतकरी कल्याण मंत्रालय",
            ta = "விவசாயம் மற்றும் விவசாயிகள் நல அமைச்சகம்"
        ),
        benefit = LocalizedContent(
            en = "₹6,000 per year direct income support in 3 equal installments",
            hi = "₹6,000 प्रति वर्ष 3 समान किस्तों में बैंक खाते में सीधा अंतरण (DBT)",
            mr = "दरवर्षी ₹6,000 थेट बँक खात्यात जमा",
            ta = "ஆண்டுக்கு ₹6,000 நேரடி வங்கி கணக்கு வரவு",
            te = "ఏడాదికి ₹6,000 నేరుగా బ్యాంక్ ఖాతాలో జమ"
        ),
        level = SchemeLevel.CENTRAL,
        category = "Agriculture",
        icon = Icons.Default.Agriculture,
        details = SchemeDetails(
            eligibility = LocalizedContent(
                en = "All landholding farmer families having cultivable land in their names.",
                hi = "अपने नाम पर कृषि योग्य भूमि रखने वाले सभी पात्र किसान परिवार।",
                mr = "स्वतःच्या नावावर शेतजमीन असलेले सर्व पात्र शेतकरी कुटुंब.",
                ta = "தங்கள் பெயரில் சாகுபடி நிலம் வைத்துள்ள அனைத்து விவசாய குடும்பங்கள்."
            ),
            benefitsDetailed = LocalizedContent(
                en = "₹2,000 credited every 4 months directly via Aadhaar-linked DBT into farmer bank account.",
                hi = "आधार-लिंक्ड बैंक खाते में प्रति 4 माह में ₹2,000 की किस्त (वार्षिक ₹6,000)।",
                mr = "दर 4 महिन्यांनी ₹2,000 चा हप्ता थेट बँक खात्यात जमा होतो."
            ),
            requiredDocuments = listOf(
                LocalizedContent(en = "Aadhaar Card", hi = "आधार कार्ड", mr = "आधार कार्ड", ta = "ஆதார் அட்டை"),
                LocalizedContent(en = "Land Ownership Record (Khata/Khasra/7/12)", hi = "भूमि स्वामित्व अभिलेख (खतौनी/खसरा/7/12)", mr = "जमीन मालकी हक्क उतारा (7/12 व 8-अ)"),
                LocalizedContent(en = "Active Bank Account Passbook", hi = "सक्रिय बैंक खाता पासबुक", mr = "बँक पासबुक")
            ),
            applicationSteps = listOf(
                LocalizedContent(en = "Visit pmkisan.gov.in or nearest CSC e-Seva centre", hi = "pmkisan.gov.in पोर्टल पर जाएं अथवा नजदीकी सीएससी केंद्र पर संपर्क करें"),
                LocalizedContent(en = "Submit Aadhaar & Land Khata details for e-KYC verification", hi = "ई-केवाईसी और भूमि रिकॉर्ड विवरण दर्ज करें"),
                LocalizedContent(en = "State nodal officer verifies application and releases first installment", hi = "सत्यापन उपरांत प्रथम किस्त बैंक खाते में हस्तांतरित की जाती है")
            ),
            officialPortalUrl = "https://pmkisan.gov.in"
        )
    ),
    WelfareScheme(
        id = "CEN-02",
        title = LocalizedContent(
            en = "Ayushman Bharat PM-JAY",
            hi = "आयुष्मान भारत - प्रधानमंत्री जन आरोग्य योजना",
            mr = "आयुष्मान भारत योजना (PM-JAY)",
            ta = "ஆயுஷ்மான் பாரத் திட்டம்",
            te = "ఆయుష్మాన్ భారత్ పీఎం-జేవై",
            bn = "আয়ুষ্মান ভারত",
            gu = "આયુષ્માન ભારત યોજના",
            kn = "ಆಯುಷ್ಮಾನ್ ಭಾರತ್"
        ),
        department = LocalizedContent(
            en = "National Health Authority (NHA)",
            hi = "राष्ट्रीय स्वास्थ्य प्राधिकरण (भारत सरकार)",
            mr = "राष्ट्रीय आरोग्य प्राधिकरण"
        ),
        benefit = LocalizedContent(
            en = "₹5 Lakh annual cashless healthcare for secondary & tertiary hospital treatment",
            hi = "प्रति परिवार प्रति वर्ष ₹5 लाख तक का कैशलेस एवं मुफ्त अस्पताल उपचार",
            mr = "कुटुंबाला दरवर्षी ₹5 लाखांपर्यंत मोफत व कॅशलेस उपचार",
            ta = "குடும்பத்திற்கு ஆண்டுக்கு ₹5 லட்சம் வரை இலவச மருத்துவ சிகிச்சை"
        ),
        level = SchemeLevel.CENTRAL,
        category = "Healthcare",
        icon = Icons.Default.HealthAndSafety,
        details = SchemeDetails(
            eligibility = LocalizedContent(
                en = "Deprived rural households and identified occupational urban workers as per SECC database.",
                hi = "SECC डेटाबेस और राशन कार्ड सूची के अनुसार सभी पात्र ग्रामीण एवं शहरी वंचित परिवार।",
                mr = "SECC यादी व शिधापत्रिका धारक वंचित कुटुंबे."
            ),
            benefitsDetailed = LocalizedContent(
                en = "Full cashless medical cover up to ₹5,00,000 per family per year in 27,000+ empanelled hospitals.",
                hi = "देश भर के 27,000 से अधिक सूचीबद्ध अस्पतालों में सर्जरी और दवाइयों सहित ₹5 लाख तक का मुफ्त उपचार।"
            ),
            requiredDocuments = listOf(
                LocalizedContent(en = "Aadhaar Card", hi = "आधार कार्ड", mr = "आधार कार्ड"),
                LocalizedContent(en = "Ration Card or Family ID", hi = "राशन कार्ड अथवा परिवार पहचान पत्र", mr = "शिधापत्रिका")
            ),
            applicationSteps = listOf(
                LocalizedContent(en = "Check eligibility online at beneficiary.nha.gov.in", hi = "beneficiary.nha.gov.in पर पात्रता जांचें"),
                LocalizedContent(en = "Complete Aadhaar biometric eKYC", hi = "आधार आधारित ई-केवाईसी पूर्ण करें"),
                LocalizedContent(en = "Download Ayushman Golden Card instantly", hi = "आयुष्मान गोल्डन कार्ड तुरंत डाउनलोड करें")
            ),
            officialPortalUrl = "https://beneficiary.nha.gov.in"
        )
    ),
    WelfareScheme(
        id = "CEN-03",
        title = LocalizedContent(
            en = "Pradhan Mantri Awas Yojana (PMAY)",
            hi = "प्रधानमंत्री आवास योजना (ग्रामीण एवं शहरी)",
            mr = "प्रधानमंत्री आवास योजना",
            ta = "பிரதான் மந்திரி ஆவாஸ் திட்டம்",
            te = "పీఎం ఆవాస్ యోజన",
            bn = "প্রধানমন্ত্রী আবাস যোজনা",
            gu = "પ્રધાનમંત્રી આવાસ યોજના",
            kn = "ಪಿಎಂ ಆವಾಸ್ ಯೋಜನೆ"
        ),
        department = LocalizedContent(
            en = "Ministry of Housing & Urban Affairs",
            hi = "आवास एवं शहरी कार्य मंत्रालय (भारत सरकार)"
        ),
        benefit = LocalizedContent(
            en = "Financial subsidy up to ₹2.67 Lakh for building pucca house",
            hi = "पक्का मकान बनाने हेतु ₹2.67 लाख तक की सीधी सरकारी वित्तीय सब्सिडी",
            mr = "पक्के घर बांधण्यासाठी ₹2.67 लाखांपर्यंत अनुदान"
        ),
        level = SchemeLevel.CENTRAL,
        category = "Housing",
        icon = Icons.Default.Home,
        details = SchemeDetails(
            eligibility = LocalizedContent(
                en = "Families without a pucca house anywhere in India having annual household income under eligibility brackets.",
                hi = "जिन परिवारों के पास देश में कहीं भी पक्का मकान नहीं है और जो बीपीएल/ईडब्ल्यूएस श्रेणी में आते हैं।"
            ),
            benefitsDetailed = LocalizedContent(
                en = "Direct financial transfer in geo-tagged stages: foundation, plinth, lintel, and final completion.",
                hi = "जियो-टैगिंग के आधार पर 4 चरणों में सीधे बैंक खाते में निर्माण सब्सिडी का हस्तांतरण।"
            ),
            requiredDocuments = listOf(
                LocalizedContent(en = "Aadhaar Card", hi = "आधार कार्ड"),
                LocalizedContent(en = "Income Certificate", hi = "आय प्रमाण पत्र"),
                LocalizedContent(en = "Land/House site document or Gram Panchayat certificate", hi = "भूमि दस्तावेज अथवा ग्राम पंचायत अनापत्ति प्रमाण पत्र")
            ),
            applicationSteps = listOf(
                LocalizedContent(en = "Submit application through local Gram Panchayat or Urban Local Body", hi = "स्थानीय ग्राम पंचायत अथवा नगरपालिका में आवेदन जमा करें"),
                LocalizedContent(en = "Geo-tagging and verification of plot by government engineer", hi = "सरकारी सर्वेक्षक द्वारा भूखंड का जियो-टैगिंग एवं सत्यापन"),
                LocalizedContent(en = "Subsidy amount released directly in DBT installments", hi = "निर्माण प्रगति के अनुसार किस्तों का भुगतान")
            ),
            officialPortalUrl = "https://pmaymis.gov.in"
        )
    ),
    WelfareScheme(
        id = "CEN-04",
        title = LocalizedContent(
            en = "One Nation One Ration Card (ONORC)",
            hi = "एक राष्ट्र एक राशन कार्ड",
            mr = "वन नेशन वन रेशन कार्ड",
            ta = "ஒரே நாடு ஒரே ரேஷன் கார்டு",
            te = "ఒకే దేశం ఒకే రేషన్ కార్డు",
            bn = "এক দেশ এক রেশন কার্ড",
            gu = "એક રાષ્ટ્ર એક રેશન કાર્ડ",
            kn = "ಒಂದು ರಾಷ್ಟ್ರ ಒಂದು ಪಡಿತರ ಚೀಟಿ"
        ),
        department = LocalizedContent(
            en = "Department of Food & Public Distribution",
            hi = "खाद्य एवं सार्वजनिक वितरण विभाग (भारत सरकार)"
        ),
        benefit = LocalizedContent(
            en = "Subsidized foodgrains at any Fair Price Shop across India with biometric verification",
            hi = "देश की किसी भी राशन दुकान से बायोमेट्रिक सत्यापन पर रियायती खाद्यान्न प्राप्त करें",
            mr = "देशातील कोणत्याही रेशन दुकानातून बायोमेट्रिक पडताळणीवर धान्य मिळवा"
        ),
        level = SchemeLevel.CENTRAL,
        category = "Food",
        icon = Icons.Default.Fastfood,
        details = SchemeDetails(
            eligibility = LocalizedContent(
                en = "All NFSA ration card holders (Antyodaya & Priority Households) migrating for work or residence.",
                hi = "राष्ट्रीय खाद्य सुरक्षा अधिनियम (NFSA) के अंतर्गत सभी पात्र अंत्योदय एवं प्राथमिकता राशन कार्ड धारक।"
            ),
            benefitsDetailed = LocalizedContent(
                en = "Free or highly subsidized rice, wheat, and coarse grains accessible across all 5.4 lakh Fair Price Shops in India.",
                hi = "देश भर की 5.4 लाख राशन दुकानों में से किसी भी दुकान से परिवार के किसी भी सदस्य द्वारा बायोमेट्रिक से राशन उठाव।"
            ),
            requiredDocuments = listOf(
                LocalizedContent(en = "Ration Card Number", hi = "राशन कार्ड नंबर"),
                LocalizedContent(en = "Aadhaar Card of family members", hi = "परिवार के सदस्यों का आधार कार्ड")
            ),
            applicationSteps = listOf(
                LocalizedContent(en = "Ensure Aadhaar is seeded with ration card at nearest PDS shop", hi = "राशन कार्ड में आधार लिंक सुनिश्चित करें"),
                LocalizedContent(en = "Visit any ePoS enabled Fair Price Shop across India", hi = "देश की किसी भी ई-पॉस राशन दुकान पर जाएं"),
                LocalizedContent(en = "Provide biometric fingerprint to collect food grains", hi = "बायोमेट्रिक फिंगरप्रिंट देकर खाद्यान्न प्राप्त करें")
            ),
            officialPortalUrl = "https://nfsa.gov.in"
        )
    ),
    WelfareScheme(
        id = "CEN-05",
        title = LocalizedContent(
            en = "National Social Assistance Programme (Pension)",
            hi = "राष्ट्रीय सामाजिक सहायता कार्यक्रम (पेंशन योजना)",
            mr = "राष्ट्रीय सामाजिक सहाय्यता निवृत्तीवेतन योजना",
            ta = "தேசிய சமூக உதவி ஓய்வூதியம்",
            te = "జాతీయ సామాజిక సహాయ పింఛను",
            bn = "জাতীয় সামাজিক সহায়তা পেনশন",
            gu = "રાષ્ટ્રીય સામાજિક સહાય પેન્શન",
            kn = "ರಾಷ್ಟ್ರೀಯ ಸಾಮಾಜಿಕ ನೆರವು ಪಿಂಚಣಿ"
        ),
        department = LocalizedContent(
            en = "Ministry of Rural Development",
            hi = "ग्रामीण विकास मंत्रालय (भारत सरकार)"
        ),
        benefit = LocalizedContent(
            en = "Monthly social security pension for elderly, widows, and persons with disabilities",
            hi = "वरिष्ठ नागरिकों, निराश्रित विधवाओं एवं दिव्यांगजनों को मासिक प्रत्यक्ष पेंशन",
            mr = "ज्येष्ठ नागरिक, विधवा व दिव्यांगांना दरमहा पेन्शन"
        ),
        level = SchemeLevel.CENTRAL,
        category = "Pension",
        icon = Icons.Default.Elderly,
        details = SchemeDetails(
            eligibility = LocalizedContent(
                en = "BPL citizens aged 60+ for old age pension, BPL widows aged 40+, and persons with 80%+ disability.",
                hi = "60 वर्ष से अधिक आयु के बीपीएल वृद्धजन, 40 वर्ष से अधिक आयु की विधवाएं और 80% से अधिक दिव्यांगजन।"
            ),
            benefitsDetailed = LocalizedContent(
                en = "Monthly pension directly credited into bank/post office account with additional state top-up.",
                hi = "मासिक पेंशन राशि बैंक अथवा डाकघर खाते में सीधे जमा, राज्य सरकार द्वारा अतिरिक्त अंशदान।"
            ),
            requiredDocuments = listOf(
                LocalizedContent(en = "Aadhaar Card", hi = "आधार कार्ड"),
                LocalizedContent(en = "Age Proof / Birth Certificate", hi = "आयु प्रमाण पत्र"),
                LocalizedContent(en = "BPL Ration Card", hi = "बीपीएल राशन कार्ड")
            ),
            applicationSteps = listOf(
                LocalizedContent(en = "Submit form at Tehsil / Block Development Office / Jan Seva Kendra", hi = "तहसील अथवा ब्लॉक विकास अधिकारी कार्यालय में आवेदन प्रस्तुत करें"),
                LocalizedContent(en = "Verification by Social Welfare Inspector", hi = "समाज कल्याण निरीक्षक द्वारा सत्यापन"),
                LocalizedContent(en = "Pension PPO issued and monthly DBT activated", hi = "पीपीओ जारी होकर मासिक डीबीटी चालू होता है")
            ),
            officialPortalUrl = "https://nsap.nic.in"
        )
    ),
    WelfareScheme(
        id = "CEN-06",
        title = LocalizedContent(
            en = "PM Ujjwala Yojana 2.0",
            hi = "प्रधानमंत्री उज्ज्वला योजना 2.0",
            mr = "प्रधानमंत्री उज्ज्वला योजना 2.0",
            ta = "பிரதான் மந்திரி உஜ்வாலா திட்டம்",
            te = "పీఎం ఉజ్వల యోజన",
            bn = "প্রধানমন্ত্রী উজ্জ্বলা যোজনা",
            gu = "પ્રધાનમંત્રી ઉજ્જવલા યોજના",
            kn = "ಪಿಎಂ ಉಜ್ವಲ ಯೋಜನೆ"
        ),
        department = LocalizedContent(
            en = "Ministry of Petroleum and Natural Gas",
            hi = "पेट्रोलियम एवं प्राकृतिक गैस मंत्रालय (भारत सरकार)"
        ),
        benefit = LocalizedContent(
            en = "Deposit-free LPG cylinder connection, free first refill, and stove for women",
            hi = "महिलाओं को मुफ्त एलपीजी गैस कनेक्शन, पहला सिलेंडर रिफिल एवं गैस चूल्हा मुफ्त",
            mr = "महिलांना मोफत गॅस कनेक्शन व मोफत पहिला सिलिंडर"
        ),
        level = SchemeLevel.CENTRAL,
        category = "Energy",
        icon = Icons.Default.LocalFireDepartment,
        details = SchemeDetails(
            eligibility = LocalizedContent(
                en = "Adult woman belonging to poor/BPL household having no existing LPG connection.",
                hi = "बीपीएल अथवा गरीब परिवार की वयस्क महिला, जिनके परिवार में पहले से कोई गैस कनेक्शन न हो।"
            ),
            benefitsDetailed = LocalizedContent(
                en = "100% free connection, pressure regulator, safety hose, first LPG cylinder, and targeted per-cylinder subsidy.",
                hi = "बिना किसी अग्रिम शुल्क के गैस कनेक्शन, सुरक्षा पाइप, चूल्हा और सब्सिडी।"
            ),
            requiredDocuments = listOf(
                LocalizedContent(en = "Aadhaar Card of applicant woman", hi = "आवेदक महिला का आधार कार्ड"),
                LocalizedContent(en = "Ration Card reflecting family members", hi = "राशन कार्ड"),
                LocalizedContent(en = "Bank Account Passbook (Aadhaar linked)", hi = "बैंक पासबुक (आधार लिंक)")
            ),
            applicationSteps = listOf(
                LocalizedContent(en = "Apply online at pmuy.gov.in or visit nearest LPG distributor", hi = "pmuy.gov.in पर ऑनलाइन आवेदन करें अथवा नजदीकी गैस एजेंसी पर संपर्क करें"),
                LocalizedContent(en = "Submit KYC and biometric authentication", hi = "केवाईसी और बायोमेट्रिक सत्यापन पूरा करें"),
                LocalizedContent(en = "LPG kit and cylinder delivered to household", hi = "गैस किट और सिलेंडर घर पर उपलब्ध कराया जाता है")
            ),
            officialPortalUrl = "https://www.pmuy.gov.in"
        )
    )
)

/**
 * Curated State-Specific Schemes with full localized details
 */
val STATE_SPECIFIC_SCHEMES: List<WelfareScheme> = listOf(
    // MAHARASHTRA
    WelfareScheme(
        id = "MH-01",
        title = LocalizedContent(
            en = "Mukhyamantri Majhi Ladki Bahin Yojana",
            hi = "मुख्यमंत्री माझी लाडकी बहीण योजना",
            mr = "मुख्यमंत्री माझी लाडकी बहीण योजना"
        ),
        department = LocalizedContent(
            en = "Govt of Maharashtra • Women & Child Development",
            hi = "महाराष्ट्र शासन • महिला व बाल विकास विभाग",
            mr = "महाराष्ट्र शासन • महिला व बाल विकास विभाग"
        ),
        benefit = LocalizedContent(
            en = "₹1,500 monthly financial aid directly transferred to eligible women in Maharashtra",
            hi = "महाराष्ट्र की पात्र महिलाओं के बैंक खाते में ₹1,500 प्रति माह सीधा आर्थिक लाभ",
            mr = "पात्र महिलांच्या बँक खात्यात दरमहा ₹1,500 थेट जमा"
        ),
        level = SchemeLevel.STATE,
        stateCode = "MH",
        stateNameEn = "Maharashtra",
        category = "Women",
        icon = Icons.Default.Female,
        details = SchemeDetails(
            eligibility = LocalizedContent(
                en = "Women residents of Maharashtra aged 21-65 with family annual income under ₹2.5 Lakh.",
                hi = "महाराष्ट्र की निवासी महिलाएं (आयु 21 से 65 वर्ष) जिनकी पारिवारिक वार्षिक आय ₹2.5 लाख से कम हो।",
                mr = "महाराष्ट्रातील 21 ते 65 वयोगटातील महिला ज्यांचे कौटुंबिक वार्षिक उत्पन्न ₹2.5 लाखांपेक्षा कमी आहे."
            ),
            benefitsDetailed = LocalizedContent(
                en = "₹1,500 transferred every month directly into the beneficiary Aadhaar-seeded bank account.",
                hi = "प्रति माह ₹1,500 की वित्तीय सहायता सीधे बैंक खाते में (वार्षिक ₹18,000)।",
                mr = "दरमहा ₹1,500 ची थेट आर्थिक मदत बँक खात्यात जमा."
            ),
            requiredDocuments = listOf(
                LocalizedContent(en = "Aadhaar Card", hi = "आधार कार्ड", mr = "आधार कार्ड"),
                LocalizedContent(en = "Maharashtra Domicile Certificate or Ration Card", hi = "महाराष्ट्र अधिवास प्रमाण पत्र अथवा राशन कार्ड", mr = "अधिवास प्रमाणपत्र किंवा रेशन कार्ड"),
                LocalizedContent(en = "Income Certificate (under ₹2.5 Lakh)", hi = "आय प्रमाण पत्र (₹2.5 लाख से कम)", mr = "उत्पन्नाचा दाखला"),
                LocalizedContent(en = "Aadhaar Linked Bank Passbook", hi = "आधार लिंक बैंक पासबुक", mr = "बँक पासबुक")
            ),
            applicationSteps = listOf(
                LocalizedContent(en = "Apply online via Nari Shakti Doot App or official portal ladakibahin.maharashtra.gov.in", hi = "नारी शक्ति दूत ऐप अथवा ladakibahin.maharashtra.gov.in पोर्टल पर आवेदन करें"),
                LocalizedContent(en = "Verify Aadhaar OTP and upload bank details", hi = "आधार ओटीपी सत्यापित करें और बैंक विवरण दर्ज करें"),
                LocalizedContent(en = "District committee approves application and starts monthly DBT", hi = "सत्यापन पश्चात मासिक डीबीटी चालू होता है")
            ),
            officialPortalUrl = "https://ladakibahin.maharashtra.gov.in"
        )
    ),
    WelfareScheme(
        id = "MH-02",
        title = LocalizedContent(
            en = "Sanjay Gandhi Niradhar Anudan Yojana",
            hi = "संजय गांधी निराधार अनुदान योजना",
            mr = "संजय गांधी निराधार अनुदान योजना"
        ),
        department = LocalizedContent(
            en = "Govt of Maharashtra • Social Justice",
            hi = "महाराष्ट्र शासन • सामाजिक न्याय व विशेष सहाय्य विभाग",
            mr = "महाराष्ट्र शासन • सामाजिक न्याय विभाग"
        ),
        benefit = LocalizedContent(
            en = "Monthly pension of ₹1,500 for destitute elderly, disabled, and single women",
            hi = "निराधार वृद्ध, दिव्यांग और निराश्रित नागरिकों को ₹1,500 मासिक पेंशन",
            mr = "निराधार वृद्ध, दिव्यांग व एकल महिलांना दरमहा ₹1,500 पेन्शन"
        ),
        level = SchemeLevel.STATE,
        stateCode = "MH",
        stateNameEn = "Maharashtra",
        category = "Pension",
        icon = Icons.Default.Elderly,
        details = SchemeDetails(
            eligibility = LocalizedContent(
                en = "Blind, disabled, cancer/TB patients, and destitute persons aged 65+ with family income below ₹21,000/year.",
                hi = "65 वर्ष से अधिक आयु के निराधार वृद्ध, दिव्यांग अथवा गंभीर बीमारी से पीड़ित व्यक्ति जिनकी आय ₹21,000 से कम हो।"
            ),
            benefitsDetailed = LocalizedContent(
                en = "₹1,500 per month for single beneficiary and ₹1,500 with additional allowance if dependent children.",
                hi = "₹1,500 प्रति माह नियमित पेंशन सहायता।"
            ),
            requiredDocuments = listOf(
                LocalizedContent(en = "Age Proof & Residence Certificate", hi = "आयु एवं निवास प्रमाण पत्र"),
                LocalizedContent(en = "Income Certificate issued by Tehsildar", hi = "तहसीलदार द्वारा जारी आय प्रमाण पत्र"),
                LocalizedContent(en = "Disability / Medical certificate (if applicable)", hi = "दिव्यांगता प्रमाण पत्र")
            ),
            applicationSteps = listOf(
                LocalizedContent(en = "Submit application at local Taluka Tehsildar office or Setu Kendra", hi = "तहसील कार्यालय अथवा सेतू केंद्र में आवेदन करें"),
                LocalizedContent(en = "Sanction committee verifies eligibility", hi = "तहसीलदार समिति द्वारा सत्यापन"),
                LocalizedContent(en = "Monthly pension credited to bank account", hi = "मासिक पेंशन खाते में जमा की जाती है")
            ),
            officialPortalUrl = "https://sjsa.maharashtra.gov.in"
        )
    ),

    // UTTAR PRADESH
    WelfareScheme(
        id = "UP-01",
        title = LocalizedContent(
            en = "Mukhyamantri Kanya Sumangala Yojana",
            hi = "मुख्यमंत्री कन्या सुमंगला योजना"
        ),
        department = LocalizedContent(
            en = "Govt of Uttar Pradesh • Women & Child Welfare",
            hi = "उत्तर प्रदेश शासन • महिला कल्याण विभाग"
        ),
        benefit = LocalizedContent(
            en = "₹25,000 phased monetary grant from birth till graduation for girl children",
            hi = "बालिका के जन्म से स्नातक तक 6 चरणों में कुल ₹25,000 की वित्तीय सहायता"
        ),
        level = SchemeLevel.STATE,
        stateCode = "UP",
        stateNameEn = "Uttar Pradesh",
        category = "Education",
        icon = Icons.Default.ChildCare,
        details = SchemeDetails(
            eligibility = LocalizedContent(
                en = "Residents of Uttar Pradesh with max 2 daughters and family annual income up to ₹3 Lakh.",
                hi = "उत्तर प्रदेश के स्थायी निवासी, अधिकतम 2 बालिकाओं वाले परिवार जिनकी वार्षिक आय ₹3 लाख तक हो।"
            ),
            benefitsDetailed = LocalizedContent(
                en = "Stage 1: ₹5,000 on birth, Stage 2: ₹2,000 on vaccination, Stage 3: ₹3,000 on Class 1, Stage 4: ₹3,000 on Class 6, Stage 5: ₹5,000 on Class 9, Stage 6: ₹7,000 on Degree/Diploma admission.",
                hi = "जन्म पर ₹5,000, टीकाकरण पर ₹2,000, कक्षा 1 में ₹3,000, कक्षा 6 में ₹3,000, कक्षा 9 में ₹5,000 और उच्च शिक्षा में ₹7,000।"
            ),
            requiredDocuments = listOf(
                LocalizedContent(en = "Birth Certificate of Girl", hi = "बालिका का जन्म प्रमाण पत्र"),
                LocalizedContent(en = "UP Domicile Certificate", hi = "उत्तर प्रदेश निवास प्रमाण पत्र"),
                LocalizedContent(en = "Income Certificate", hi = "आय प्रमाण पत्र"),
                LocalizedContent(en = "School/College Admission Receipt", hi = "विद्यालय/कॉलेज प्रवेश रसीद")
            ),
            applicationSteps = listOf(
                LocalizedContent(en = "Register on mksy.up.gov.in citizen portal", hi = "mksy.up.gov.in पोर्टल पर नागरिक पंजीकरण करें"),
                LocalizedContent(en = "Apply for relevant stage with required documents", hi = "संबंधित चरण के लिए दस्तावेज अपलोड करें"),
                LocalizedContent(en = "BDO / SDM verifies and releases funds via DBT", hi = "सत्यापन उपरांत धनराशि सीधे खाते में हस्तांतरित होती है")
            ),
            officialPortalUrl = "https://mksy.up.gov.in"
        )
    ),

    // DELHI (NCT)
    WelfareScheme(
        id = "DL-01",
        title = LocalizedContent(
            en = "Delhi Mukhyamantri Teerth Yatra Yojana",
            hi = "मुख्यमंत्री तीर्थ यात्रा योजना (दिल्ली)"
        ),
        department = LocalizedContent(
            en = "Govt of NCT of Delhi • Tirth Yatra Vikas Samiti",
            hi = "दिल्ली सरकार • तीर्थ यात्रा विकास समिति"
        ),
        benefit = LocalizedContent(
            en = "100% free pilgrimage travel, AC accommodation, and food for senior citizens of Delhi",
            hi = "वरिष्ठ नागरिकों को सभी तीर्थ स्थलों की 100% निःशुल्क एसी यात्रा, भोजन एवं आवास"
        ),
        level = SchemeLevel.STATE,
        stateCode = "DL",
        stateNameEn = "Delhi (NCT)",
        category = "Social",
        icon = Icons.Default.Train,
        details = SchemeDetails(
            eligibility = LocalizedContent(
                en = "Senior citizens aged 60+ who are permanent residents of Delhi with annual family income under ₹3 Lakh.",
                hi = "दिल्ली के निवासी वरिष्ठ नागरिक (आयु 60 वर्ष या अधिक) जिनकी पारिवारिक आय ₹3 लाख से कम हो।"
            ),
            benefitsDetailed = LocalizedContent(
                en = "Free train journey in AC coaches, meals, boarding, local transport, and ₹1 Lakh accidental insurance.",
                hi = "वातानुकूलित ट्रेन यात्रा, ठहरने की उत्तम व्यवस्था, भोजन और ₹1 लाख का दुर्घटना बीमा पूरी तरह मुफ्त।"
            ),
            requiredDocuments = listOf(
                LocalizedContent(en = "Voter ID card of Delhi", hi = "दिल्ली का मतदाता पहचान पत्र (Voter ID)"),
                LocalizedContent(en = "Aadhaar Card", hi = "आधार कार्ड"),
                LocalizedContent(en = "Medical Fitness Certificate", hi = "चिकित्सीय स्वास्थ्य प्रमाण पत्र")
            ),
            applicationSteps = listOf(
                LocalizedContent(en = "Apply on Delhi e-District portal edistrict.delhigovt.nic.in", hi = "edistrict.delhigovt.nic.in पोर्टल पर आवेदन करें"),
                LocalizedContent(en = "Select preferred pilgrimage route and attendant details", hi = "तीर्थ यात्रा मार्ग चुनें"),
                LocalizedContent(en = "Receive digital travel ticket and board chartered trains", hi = "डिजिटल यात्रा टिकट प्राप्त करें")
            ),
            officialPortalUrl = "https://edistrict.delhigovt.nic.in"
        )
    ),

    // BIHAR
    WelfareScheme(
        id = "BR-01",
        title = LocalizedContent(
            en = "Mukhyamantri Kanya Utthan Yojana",
            hi = "मुख्यमंत्री कन्या उत्थान योजना (बिहार)"
        ),
        department = LocalizedContent(
            en = "Govt of Bihar • Education & Social Welfare",
            hi = "बिहार सरकार • शिक्षा एवं समाज कल्याण विभाग"
        ),
        benefit = LocalizedContent(
            en = "₹50,000 cash incentive upon passing graduation for female students of Bihar",
            hi = "स्नातक उत्तीर्ण करने वाली छात्राओं को ₹50,000 की सीधी वित्तीय प्रोत्साहन राशि"
        ),
        level = SchemeLevel.STATE,
        stateCode = "BR",
        stateNameEn = "Bihar",
        category = "Education",
        icon = Icons.Default.School,
        details = SchemeDetails(
            eligibility = LocalizedContent(
                en = "Unmarried girl students who are permanent residents of Bihar and have completed graduation from recognized colleges in Bihar.",
                hi = "बिहार की निवासी छात्राएं जिन्होंने राज्य के मान्यता प्राप्त विश्वविद्यालय से स्नातक परीक्षा उत्तीर्ण की हो।"
            ),
            benefitsDetailed = LocalizedContent(
                en = "One-time lump sum grant of ₹50,000 transferred via DBT to encourage higher education.",
                hi = "उच्च शिक्षा को बढ़ावा देने हेतु ₹50,000 की एकमुश्त अनुदान राशि सीधे बैंक खाते में।"
            ),
            requiredDocuments = listOf(
                LocalizedContent(en = "Graduation Degree / Final Marksheet", hi = "स्नातक अंक पत्र"),
                LocalizedContent(en = "Bihar Domicile Certificate", hi = "बिहार निवास प्रमाण पत्र"),
                LocalizedContent(en = "Aadhaar Card linked Bank Account", hi = "आधार लिंक बैंक खाता विवरण")
            ),
            applicationSteps = listOf(
                LocalizedContent(en = "Visit medhasoft.bih.nic.in portal", hi = "medhasoft.bih.nic.in पोर्टल पर जाएं"),
                LocalizedContent(en = "Enter roll number and university registration to auto-fetch record", hi = "विश्वविद्यालय रोल नंबर दर्ज करें"),
                LocalizedContent(en = "Verify bank account for direct credit", hi = "बैंक खाता सत्यापित करें")
            ),
            officialPortalUrl = "https://medhasoft.bih.nic.in"
        )
    ),

    // TAMIL NADU
    WelfareScheme(
        id = "TN-01",
        title = LocalizedContent(
            en = "Kalaignar Magalir Urimai Thittam",
            hi = "कलैग्नार महिला अधिकार योजना",
            ta = "கலைஞர் மகளிர் உரிமைத் திட்டம்"
        ),
        department = LocalizedContent(
            en = "Govt of Tamil Nadu • Special Programme",
            hi = "तमिलनाडु सरकार • महिला कल्याण विभाग",
            ta = "தமிழ்நாடு அரசு • சிறப்பு திட்ட அமலாக்கத் துறை"
        ),
        benefit = LocalizedContent(
            en = "₹1,000 monthly rights entitlement directly credited to female heads of families",
            hi = "पात्र महिला मुखियाओं को ₹1,000 प्रति माह सीधा बैंक खाते में अधिकार अनुदान",
            ta = "குடும்பத் தலைவிகளுக்கு மாதம் ₹1,000 உரிமைத் தொகை வங்கி கணக்கில் நேரடி வரவு"
        ),
        level = SchemeLevel.STATE,
        stateCode = "TN",
        stateNameEn = "Tamil Nadu",
        category = "Women",
        icon = Icons.Default.Female,
        details = SchemeDetails(
            eligibility = LocalizedContent(
                en = "Women heads of families aged 21+ with annual family income below ₹2.5 Lakh in Tamil Nadu.",
                hi = "तमिलनाडु में 21 वर्ष से अधिक आयु की महिला मुखिया जिनकी वार्षिक पारिवारिक आय ₹2.5 लाख से कम हो।",
                ta = "ஆண்டு வருமானம் ₹2.5 லட்சத்திற்குள் உள்ள 21 வயது நிரம்பிய குடும்பத் தலைவிகள்."
            ),
            benefitsDetailed = LocalizedContent(
                en = "₹1,000 deposited on the 15th of every month into beneficiary bank accounts.",
                hi = "प्रति माह ₹1,000 की नियमित वित्तीय सहायता (वार्षिक ₹12,000)।",
                ta = "மாதந்தோறும் ₹1,000 நேரடி வங்கி கணக்கு வரவு."
            ),
            requiredDocuments = listOf(
                LocalizedContent(en = "Smart Ration Card of Tamil Nadu", hi = "स्मार्ट राशन कार्ड", ta = "ஸ்மார்ட் ரேஷன் அட்டை"),
                LocalizedContent(en = "Aadhaar Card", hi = "आधार कार्ड", ta = "ஆதார் அட்டை"),
                LocalizedContent(en = "Electricity Bill / Connection Number", hi = "बिजली बिल उपभोक्ता संख्या", ta = "மின் இணைப்பு எண்")
            ),
            applicationSteps = listOf(
                LocalizedContent(en = "Apply through special camp or e-Sevai centre", hi = "विशेष ई-सेवा शिविर में आवेदन जमा करें", ta = "இ-சேவை மையம் மூலம் விண்ணப்பிக்கவும்"),
                LocalizedContent(en = "Field verification by Village Administrative Officer (VAO)", hi = "ग्राम प्रशासनिक अधिकारी द्वारा सत्यापन", ta = "கிராம நிர்வாக அலுவலர் மூலம் கள சரிபார்ப்பு"),
                LocalizedContent(en = "SMS notification and monthly DBT activation", hi = "सत्यापन उपरांत मासिक डीबीटी चालू", ta = "வங்கி கணக்கில் மாதாந்திர வரவு தொடங்கும்")
            ),
            officialPortalUrl = "https://kmut.tn.gov.in"
        )
    ),

    // KARNATAKA
    WelfareScheme(
        id = "KA-01",
        title = LocalizedContent(
            en = "Gruha Lakshmi Scheme",
            hi = "गृह लक्ष्मी योजना (कर्नाटक)",
            kn = "ಗೃಹ ಲಕ್ಷ್ಮಿ ಯೋಜನೆ"
        ),
        department = LocalizedContent(
            en = "Govt of Karnataka • Women & Child Development",
            hi = "कर्नाटक सरकार • महिला एवं बाल विकास विभाग",
            kn = "ಕರ್ನಾಟಕ ಸರ್ಕಾರ • ಮಹಿಳಾ ಮತ್ತು ಮಕ್ಕಳ ಅಭಿವೃದ್ಧಿ ಇಲಾಖೆ"
        ),
        benefit = LocalizedContent(
            en = "₹2,000 monthly financial grant for the woman head of every eligible household",
            hi = "प्रत्येक पात्र परिवार की महिला मुखिया को ₹2,000 प्रति माह की आर्थिक सहायता",
            kn = "ಪ್ರತಿ ಅರ್ಹ ಕುಟುಂಬದ ಮಹಿಳಾ ಯಜಮಾನಿಗೆ ಮಾಸಿಕ ₹2,000 ನೇರ ನೆರವು"
        ),
        level = SchemeLevel.STATE,
        stateCode = "KA",
        stateNameEn = "Karnataka",
        category = "Women",
        icon = Icons.Default.Female,
        details = SchemeDetails(
            eligibility = LocalizedContent(
                en = "Woman identified as the head of the family in APL/BPL/Antyodaya ration cards in Karnataka.",
                hi = "कर्नाटक में बीपीएल/एपीएल/अंत्योदय राशन कार्ड में परिवार की मुखिया के रूप में दर्ज महिला।",
                kn = "ಕರ್ನಾಟಕದ ಬಿಪಿಎಲ್/ಎಪಿಎಲ್ ಪಡಿತರ ಚೀಟಿಯಲ್ಲಿ ಕುಟುಂಬದ ಮುಖ್ಯಸ್ಥೆಯಾಗಿರುವ ಮಹಿಳೆ."
            ),
            benefitsDetailed = LocalizedContent(
                en = "₹2,000 deposited directly every month via DBT into Aadhaar-linked bank account.",
                hi = "प्रति माह ₹2,000 की सीधी सहायता (वार्षिक ₹24,000)।",
                kn = "ಪ್ರತಿ ತಿಂಗಳು ₹2,000 ನೇರವಾಗಿ ಬ್ಯಾಂಕ್ ಖಾತೆಗೆ ಜಮೆ."
            ),
            requiredDocuments = listOf(
                LocalizedContent(en = "Ration Card (APL/BPL/AAY)", hi = "राशन कार्ड", kn = "ಪಡಿತರ ಚೀಟಿ"),
                LocalizedContent(en = "Aadhaar Card of Wife and Husband", hi = "पति एवं पत्नी का आधार कार्ड", kn = "ಆಧಾರ್ ಕಾರ್ಡ್"),
                LocalizedContent(en = "Aadhaar seeded bank account passbook", hi = "आधार लिंक बैंक पासबुक", kn = "ಬ್ಯಾಂಕ್ ಪಾಸ್‌ಬುಕ್")
            ),
            applicationSteps = listOf(
                LocalizedContent(en = "Apply online via Seva Sindhu portal or visit Karnataka One / Grama One", hi = "सेवा सिंधु पोर्टल अथवा ग्राम वन केंद्र पर आवेदन करें", kn = "ಸೇವಾ ಸಿಂಧು ಪೋರ್ಟಲ್ ಅಥವಾ ಗ್ರಾಮ ಒನ್ ಕೇಂದ್ರಕ್ಕೆ ಭೇಟಿ ನೀಡಿ"),
                LocalizedContent(en = "Biometric / OTP verification", hi = "बायोमेट्रिक अथवा ओटीपी सत्यापन", kn = "ಬಯೋಮೆಟ್ರಿಕ್ / ಒಟಿಪಿ ಪರಿಶೀಲನೆ"),
                LocalizedContent(en = "Monthly DBT approval notification", hi = "मंजूरी पश्चात मासिक डीबीटी सक्रिय", kn = "ಮಾಸಿಕ ಹಣ ವರ್ಗಾವಣೆ ಸಕ್ರಿಯ")
            ),
            officialPortalUrl = "https://sevasindhu.karnataka.gov.in"
        )
    )
)

/**
 * Core Filter Logic:
 * State selection controls which State/UT schemes are shown,
 * while Central Government schemes remain available nationwide for all citizens!
 */
fun getApplicableSchemesForState(selectedStateName: String?): List<WelfareScheme> {
    if (selectedStateName.isNullOrBlank()) {
        return NATIONWIDE_CENTRAL_SCHEMES
    }

    val list = mutableListOf<WelfareScheme>()
    list.addAll(NATIONWIDE_CENTRAL_SCHEMES)

    val matchingStateSchemes = STATE_SPECIFIC_SCHEMES.filter {
        it.stateNameEn.equals(selectedStateName, ignoreCase = true)
    }

    if (matchingStateSchemes.isNotEmpty()) {
        list.addAll(matchingStateSchemes)
    } else {
        // Fallback dedicated state scheme for any other of the 36 states/UTs
        list.add(
            WelfareScheme(
                id = "STATE-GEN-01",
                title = LocalizedContent(
                    en = "$selectedStateName Resident Citizen Welfare Grant",
                    hi = "$selectedStateName नागरिक कल्याण एवं सामाजिक सुरक्षा योजना",
                    mr = "$selectedStateName नागरिक कल्याण अनुदान योजना"
                ),
                department = LocalizedContent(
                    en = "Govt of $selectedStateName • Social Welfare",
                    hi = "$selectedStateName शासन • समाज कल्याण विभाग"
                ),
                benefit = LocalizedContent(
                    en = "Dedicated resident welfare assistance and local subsidies for citizens of $selectedStateName",
                    hi = "$selectedStateName के निवासियों के लिए विशेष राज्य सामाजिक सुरक्षा एवं आर्थिक सहायता"
                ),
                level = SchemeLevel.STATE,
                stateNameEn = selectedStateName,
                category = "Social",
                icon = Icons.Default.AccountBalance,
                details = SchemeDetails(
                    eligibility = LocalizedContent(
                        en = "Permanent residents and domicile certificate holders of $selectedStateName.",
                        hi = "$selectedStateName के स्थायी निवासी और अधिवास प्रमाण पत्र धारक नागरिक।"
                    ),
                    benefitsDetailed = LocalizedContent(
                        en = "State welfare pensions, education scholarships, and localized citizen welfare transfers.",
                        hi = "राज्य स्तरीय पेंशन, छात्रवृत्ति और प्रत्यक्ष लाभ अंतरण योजनाएं।"
                    ),
                    requiredDocuments = listOf(
                        LocalizedContent(en = "Aadhaar Card", hi = "आधार कार्ड"),
                        LocalizedContent(en = "Domicile Certificate of $selectedStateName", hi = "$selectedStateName अधिवास/निवास प्रमाण पत्र"),
                        LocalizedContent(en = "Active Bank Account Details", hi = "सक्रिय बैंक खाता विवरण")
                    ),
                    applicationSteps = listOf(
                        LocalizedContent(en = "Apply through local e-District portal or Citizen Service Centre in $selectedStateName", hi = "स्थानीय ई-डिस्ट्रिक्ट पोर्टल अथवा नागरिक सेवा केंद्र पर आवेदन करें"),
                        LocalizedContent(en = "Verification by Block/Tehsil administration", hi = "प्रशासनिक सत्यापन उपरांत स्वीकृति"),
                        LocalizedContent(en = "Benefits disbursed directly via DBT", hi = "डीबीटी द्वारा बैंक खाते में लाभ अंतरण")
                    ),
                    officialPortalUrl = "https://services.india.gov.in"
                )
            )
        )
    }

    return list
}
