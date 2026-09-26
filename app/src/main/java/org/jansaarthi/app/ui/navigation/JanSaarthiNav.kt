package org.jansaarthi.app.ui.navigation

sealed class JanSaarthiDestination {
    data object Login : JanSaarthiDestination()
    data object SignUp : JanSaarthiDestination()
    data object ForgotPassword : JanSaarthiDestination()
    data object StateSelection : JanSaarthiDestination()
    data object EligibilityProfile : JanSaarthiDestination()
    data object Categories : JanSaarthiDestination()
    data object Ministries : JanSaarthiDestination()
    data class SchemeList(
        val categoryId: String? = null,
        val categoryName: String? = null,
        val ministryName: String? = null
    ) : JanSaarthiDestination()
    data object AllSchemes : JanSaarthiDestination()
    data object FindSchemesForMe : JanSaarthiDestination()
    data class SchemeDetails(val schemeId: String? = null) : JanSaarthiDestination()
    data object Documents : JanSaarthiDestination()
    data object MyApplications : JanSaarthiDestination()
    data class Search(val initialQuery: String = "") : JanSaarthiDestination()
    data object SavedSchemes : JanSaarthiDestination()
    data object ProfileSettings : JanSaarthiDestination()
    data object Home : JanSaarthiDestination()
}

