package org.jansaarthi.app.ui.screens.home

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import org.jansaarthi.app.data.model.AppLanguage
import org.jansaarthi.app.ui.localization.getJanSaarthiStrings
import org.jansaarthi.app.ui.screens.auth.AuthViewModel

/**
 * CitizenHomeScreen bridge: Passes active session context into JanSaarthiHomeScreen.
 */
@Composable
fun CitizenHomeScreen(
    viewModel: AuthViewModel,
    onChangeState: () -> Unit,
    currentLanguage: AppLanguage,
    onSelectLanguage: (AppLanguage) -> Unit,
    isLargeFont: Boolean,
    onFontScaleToggle: () -> Unit,
    onOpenEligibilityProfile: () -> Unit = {},
    onOpenFindSchemesForMe: () -> Unit = {},
    onOpenCategories: () -> Unit = {},
    onOpenMinistries: () -> Unit = {},
    onOpenAllSchemes: () -> Unit = {},
    onOpenDocuments: () -> Unit = {},
    onOpenApplications: () -> Unit = {},
    onOpenSearch: (String) -> Unit = {},
    onOpenSavedSchemes: () -> Unit = {},
    onOpenProfileSettings: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val currentSession by viewModel.currentSession.collectAsState()
    val strings = getJanSaarthiStrings(currentLanguage)

    val profile = currentSession?.profile
    val isGuest = currentSession?.isGuest ?: false
    val citizenName = profile?.fullName?.ifBlank { null } ?: strings.welcomeCitizen
    val currentState = profile?.selectedState ?: "Maharashtra"

    JanSaarthiHomeScreen(
        citizenName = citizenName,
        currentState = currentState,
        isGuest = isGuest,
        onChangeState = onChangeState,
        currentLanguage = currentLanguage,
        onSelectLanguage = onSelectLanguage,
        isLargeFont = isLargeFont,
        onFontScaleToggle = onFontScaleToggle,
        onOpenEligibilityProfile = onOpenEligibilityProfile,
        onOpenFindSchemesForMe = onOpenFindSchemesForMe,
        onOpenCategories = onOpenCategories,
        onOpenMinistries = onOpenMinistries,
        onOpenAllSchemes = onOpenAllSchemes,
        onOpenDocuments = onOpenDocuments,
        onOpenApplications = onOpenApplications,
        onOpenSearch = onOpenSearch,
        onOpenSavedSchemes = onOpenSavedSchemes,
        onOpenProfileSettings = onOpenProfileSettings,
        onLogout = { viewModel.logout() },
        modifier = modifier
    )
}
