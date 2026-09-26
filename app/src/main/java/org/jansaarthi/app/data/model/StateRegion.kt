package org.jansaarthi.app.data.model

/**
 * Representation of an Indian State or Union Territory
 */
data class IndianState(
    val code: String,
    val nameEn: String,
    val nameHi: String,
    val capitalEn: String,
    val capitalHi: String,
    val zoneEn: String,
    val zoneHi: String,
    val isUnionTerritory: Boolean = false,
    val isPopular: Boolean = false
)

/**
 * Complete, Official Catalog of all 28 Indian States and 8 Union Territories for JanSaarthi
 */
val ALL_INDIAN_STATES_AND_UTS: List<IndianState> = listOf(
    // 28 States of India
    IndianState("AP", "Andhra Pradesh", "आंध्र प्रदेश", "Amaravati", "अमरावती", "Southern", "दक्षिणी", isPopular = true),
    IndianState("AR", "Arunachal Pradesh", "अरुणाचल प्रदेश", "Itanagar", "ईटानगर", "North-Eastern", "पूर्वोत्तर"),
    IndianState("AS", "Assam", "असम", "Dispur", "दिसपुर", "North-Eastern", "पूर्वोत्तर", isPopular = true),
    IndianState("BR", "Bihar", "बिहार", "Patna", "पटना", "Eastern", "पूर्वी", isPopular = true),
    IndianState("CT", "Chhattisgarh", "छत्तीसगढ़", "Raipur", "रायपुर", "Central", "मध्य"),
    IndianState("GA", "Goa", "गोवा", "Panaji", "पणजी", "Western", "पश्चिमी"),
    IndianState("GJ", "Gujarat", "गुजरात", "Gandhinagar", "गांधीनगर", "Western", "पश्चिमी", isPopular = true),
    IndianState("HR", "Haryana", "हरियाणा", "Chandigarh", "चंडीगढ़", "Northern", "उत्तरी"),
    IndianState("HP", "Himachal Pradesh", "हिमाचल प्रदेश", "Shimla", "शिमला", "Northern", "उत्तरी"),
    IndianState("JH", "Jharkhand", "झारखंड", "Ranchi", "राँची", "Eastern", "पूर्वी"),
    IndianState("KA", "Karnataka", "कर्नाटक", "Bengaluru", "बेंगलुरु", "Southern", "दक्षिणी", isPopular = true),
    IndianState("KL", "Kerala", "केरल", "Thiruvananthapuram", "तिरुवनंतपुरम", "Southern", "दक्षिणी", isPopular = true),
    IndianState("MP", "Madhya Pradesh", "मध्य प्रदेश", "Bhopal", "भोपाल", "Central", "मध्य", isPopular = true),
    IndianState("MH", "Maharashtra", "महाराष्ट्र", "Mumbai", "मुंबई", "Western", "पश्चिमी", isPopular = true),
    IndianState("MN", "Manipur", "मणिपुर", "Imphal", "इम्फाल", "North-Eastern", "पूर्वोत्तर"),
    IndianState("ML", "Meghalaya", "मेघालय", "Shillong", "शिलांग", "North-Eastern", "पूर्वोत्तर"),
    IndianState("MZ", "Mizoram", "मिज़ोरम", "Aizawl", "आइज़ोल", "North-Eastern", "पूर्वोत्तर"),
    IndianState("NL", "Nagaland", "नागालैंड", "Kohima", "कोहिमा", "North-Eastern", "पूर्वोत्तर"),
    IndianState("OR", "Odisha", "ओडिशा", "Bhubaneswar", "भुवनेश्वर", "Eastern", "पूर्वी", isPopular = true),
    IndianState("PB", "Punjab", "पंजाब", "Chandigarh", "चंडीगढ़", "Northern", "उत्तरी", isPopular = true),
    IndianState("RJ", "Rajasthan", "राजस्थान", "Jaipur", "जयपुर", "Northern", "उत्तरी", isPopular = true),
    IndianState("SK", "Sikkim", "सिक्किम", "Gangtok", "गंगटोक", "North-Eastern", "पूर्वोत्तर"),
    IndianState("TN", "Tamil Nadu", "तमिलनाडु", "Chennai", "चेन्नई", "Southern", "दक्षिणी", isPopular = true),
    IndianState("TG", "Telangana", "तेलंगाना", "Hyderabad", "हैदराबाद", "Southern", "दक्षिणी", isPopular = true),
    IndianState("TR", "Tripura", "त्रिपुरा", "Agartala", "अगरतला", "North-Eastern", "पूर्वोत्तर"),
    IndianState("UT", "Uttarakhand", "उत्तराखंड", "Dehradun", "देहरादून", "Northern", "उत्तरी"),
    IndianState("UP", "Uttar Pradesh", "उत्तर प्रदेश", "Lucknow", "लखनऊ", "Northern", "उत्तरी", isPopular = true),
    IndianState("WB", "West Bengal", "पश्चिम बंगाल", "Kolkata", "कोलकाता", "Eastern", "पूर्वी", isPopular = true),

    // 8 Union Territories of India
    IndianState("AN", "Andaman and Nicobar Islands", "अंडमान और निकोबार द्वीप समूह", "Port Blair", "पोर्ट ब्लेयर", "UT / Island", "केंद्र शासित प्रदेश", isUnionTerritory = true),
    IndianState("CH", "Chandigarh", "चंडीगढ़", "Chandigarh", "चंडीगढ़", "UT / Northern", "केंद्र शासित प्रदेश", isUnionTerritory = true),
    IndianState("DN", "Dadra & Nagar Haveli and Daman & Diu", "दादरा एवं नगर हवेली और दमन एवं दीव", "Daman", "दमन", "UT / Western", "केंद्र शासित प्रदेश", isUnionTerritory = true),
    IndianState("DL", "Delhi (NCT)", "दिल्ली (राष्ट्रीय राजधानी क्षेत्र)", "New Delhi", "नई दिल्ली", "UT / Northern", "केंद्र शासित प्रदेश", isUnionTerritory = true, isPopular = true),
    IndianState("JK", "Jammu and Kashmir", "जम्मू और कश्मीर", "Srinagar / Jammu", "श्रीनगर / जम्मू", "UT / Northern", "केंद्र शासित प्रदेश", isUnionTerritory = true, isPopular = true),
    IndianState("LA", "Ladakh", "लद्दाख", "Leh", "लेह", "UT / Northern", "केंद्र शासित प्रदेश", isUnionTerritory = true),
    IndianState("LD", "Lakshadweep", "लक्षद्वीप", "Kavaratti", "कवरत्ती", "UT / Island", "केंद्र शासित प्रदेश", isUnionTerritory = true),
    IndianState("PY", "Puducherry", "पुदुचेरी", "Puducherry", "पुदुचेरी", "UT / Southern", "केंद्र शासित प्रदेश", isUnionTerritory = true)
)

val INDIAN_STATES: List<IndianState> = ALL_INDIAN_STATES_AND_UTS
