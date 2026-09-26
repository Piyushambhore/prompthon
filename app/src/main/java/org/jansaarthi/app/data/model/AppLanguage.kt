package org.jansaarthi.app.data.model

/**
 * Supported Official Indian Languages in JanSaarthi
 */
enum class AppLanguage(
    val code: String,
    val nativeName: String,
    val englishName: String,
    val scriptSample: String
) {
    ENGLISH("en", "English", "English", "Welcome"),
    HINDI("hi", "हिन्दी", "Hindi", "नमस्ते"),
    MARATHI("mr", "मराठी", "Marathi", "नमस्कार"),
    TAMIL("ta", "தமிழ்", "Tamil", "வணக்கம்"),
    TELUGU("te", "తెలుగు", "Telugu", "నమస్కారం"),
    BENGALI("bn", "বাংলা", "Bengali", "নমস্কার"),
    GUJARATI("gu", "ગુજરાતી", "Gujarati", "નમસ્તે"),
    KANNADA("kn", "ಕನ್ನಡ", "Kannada", "ನಮಸ್ಕಾರ"),
    PUNJABI("pa", "ਪੰਜਾਬੀ", "Punjabi", "ਸਤਿ ਸ੍ਰੀ ਅਕਾਲ"),
    ODIA("or", "ଓଡ଼ିଆ", "Odia", "ନମସ୍କାର"),
    MALAYALAM("ml", "മലയാളം", "Malayalam", "നമസ്കാരം");

    companion object {
        fun fromCode(code: String?): AppLanguage {
            return entries.find { it.code.equals(code, ignoreCase = true) } ?: ENGLISH
        }
    }
}
