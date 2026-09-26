package org.jansaarthi.app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.collectLatest
import org.jansaarthi.app.data.model.AppLanguage
import org.jansaarthi.app.data.repository.AuthRepositoryImpl
import org.jansaarthi.app.data.source.BackendAuthRemoteDataSource
import org.jansaarthi.app.data.source.SessionManager
import org.jansaarthi.app.ui.navigation.JanSaarthiDestination
import org.jansaarthi.app.ui.screens.auth.*
import org.jansaarthi.app.ui.screens.home.CitizenHomeScreen
import org.jansaarthi.app.ui.screens.onboarding.StateSelectionScreen
import org.jansaarthi.app.ui.theme.JanSaarthiTheme

class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels {
        val sessionManager = SessionManager(applicationContext)
        val remoteDataSource = BackendAuthRemoteDataSource()
        val repository = AuthRepositoryImpl(remoteDataSource, sessionManager)
        AuthViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Determine initial start destination based on session & profile completion
        val initialDestination = determineInitialDestination()

        setContent {
            // ---------------------------------------------------------------
            // NAVIGATION STATE
            // Using a simple back-stack rather than NavController to keep
            // the existing single-Activity, Composable-switch architecture.
            // ---------------------------------------------------------------
            var currentScreen by remember { mutableStateOf<JanSaarthiDestination>(initialDestination) }

            // Manual back stack: holds the screens we can pop back to.
            // Null entries are pruned automatically.
            val backStack = remember { mutableStateListOf<JanSaarthiDestination>() }

            val currentLanguage by authViewModel.currentLanguage.collectAsState()
            val currentSession by authViewModel.currentSession.collectAsState()
            var isLargeFont by remember { mutableStateOf(false) }

            // Helper: navigate forward (pushes current screen to back stack)
            fun navigateTo(destination: JanSaarthiDestination) {
                backStack.add(currentScreen)
                currentScreen = destination
            }

            // Helper: navigate backward (pops back stack)
            fun popBackStack() {
                if (backStack.isNotEmpty()) {
                    currentScreen = backStack.removeLast()
                } else {
                    // Fallback: if stack is empty go Home (or Login if not authenticated)
                    currentScreen = if (currentSession != null) {
                        JanSaarthiDestination.Home
                    } else {
                        JanSaarthiDestination.Login
                    }
                }
            }

            // Helper: navigate to a root destination (clears back stack)
            fun navigateToRoot(destination: JanSaarthiDestination) {
                backStack.clear()
                currentScreen = destination
            }

            // Listen to navigation events from ViewModel (auth flow)
            LaunchedEffect(Unit) {
                authViewModel.navEvents.collectLatest { event ->
                    when (event) {
                        is AuthNavEvent.NavigateToLogin -> {
                            navigateToRoot(JanSaarthiDestination.Login)
                        }
                        is AuthNavEvent.NavigateToSignUp -> {
                            navigateTo(JanSaarthiDestination.SignUp)
                        }
                        is AuthNavEvent.NavigateToForgotPassword -> {
                            navigateTo(JanSaarthiDestination.ForgotPassword)
                        }
                        is AuthNavEvent.NavigateToStateSelection -> {
                            navigateToRoot(JanSaarthiDestination.StateSelection)
                        }
                        is AuthNavEvent.NavigateToHome -> {
                            navigateToRoot(JanSaarthiDestination.Home)
                        }
                        is AuthNavEvent.ShowToast -> {
                            Toast.makeText(this@MainActivity, event.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            JanSaarthiTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // System back button: pop the back stack.
                    // Disabled when on Login or when the back stack is empty and we are on Home.
                    val isBackEnabled = currentScreen != JanSaarthiDestination.Login &&
                            !(currentScreen == JanSaarthiDestination.Home && backStack.isEmpty())

                    BackHandler(enabled = isBackEnabled) {
                        popBackStack()
                    }

                    AnimatedContent(
                        targetState = currentScreen,
                        transitionSpec = {
                            if (targetState == JanSaarthiDestination.Home ||
                                targetState == JanSaarthiDestination.StateSelection
                            ) {
                                slideInHorizontally { width -> width } + fadeIn() togetherWith
                                        slideOutHorizontally { width -> -width } + fadeOut()
                            } else {
                                slideInHorizontally { width -> -width } + fadeIn() togetherWith
                                        slideOutHorizontally { width -> width } + fadeOut()
                            }
                        },
                        label = "JanSaarthiScreenTransition"
                    ) { destination ->
                        when (destination) {
                            is JanSaarthiDestination.Login -> {
                                LoginScreen(
                                    viewModel = authViewModel,
                                    onNavigateToSignUp = {
                                        navigateTo(JanSaarthiDestination.SignUp)
                                    },
                                    onNavigateToForgotPassword = {
                                        navigateTo(JanSaarthiDestination.ForgotPassword)
                                    },
                                    currentLanguage = currentLanguage,
                                    onSelectLanguage = authViewModel::setLanguage,
                                    isLargeFont = isLargeFont,
                                    onFontScaleToggle = { isLargeFont = !isLargeFont }
                                )
                            }

                            is JanSaarthiDestination.SignUp -> {
                                SignUpScreen(
                                    viewModel = authViewModel,
                                    onNavigateToLogin = { popBackStack() },
                                    currentLanguage = currentLanguage,
                                    onSelectLanguage = authViewModel::setLanguage,
                                    isLargeFont = isLargeFont,
                                    onFontScaleToggle = { isLargeFont = !isLargeFont }
                                )
                            }

                            is JanSaarthiDestination.ForgotPassword -> {
                                ForgotPasswordScreen(
                                    viewModel = authViewModel,
                                    onNavigateBack = { popBackStack() },
                                    currentLanguage = currentLanguage,
                                    onSelectLanguage = authViewModel::setLanguage,
                                    isLargeFont = isLargeFont,
                                    onFontScaleToggle = { isLargeFont = !isLargeFont }
                                )
                            }

                            is JanSaarthiDestination.StateSelection -> {
                                // Allow back to Home only if profile is already complete
                                // (i.e., user navigated here from Settings, not first-time onboarding)
                                val canNavigateBackToHome =
                                    currentSession?.profile?.isProfileSetupComplete == true &&
                                            backStack.isNotEmpty()

                                StateSelectionScreen(
                                    viewModel = authViewModel,
                                    onNavigateBack = if (canNavigateBackToHome) {
                                        { popBackStack() }
                                    } else null,
                                    currentLanguage = currentLanguage,
                                    onSelectLanguage = authViewModel::setLanguage,
                                    isLargeFont = isLargeFont,
                                    onFontScaleToggle = { isLargeFont = !isLargeFont }
                                )
                            }

                            is JanSaarthiDestination.EligibilityProfile -> {
                                val currentStateName = currentSession?.profile?.selectedState

                                org.jansaarthi.app.ui.screens.eligibility.EligibilityProfileScreen(
                                    currentLanguage = currentLanguage,
                                    onSelectLanguage = authViewModel::setLanguage,
                                    isLargeFont = isLargeFont,
                                    onFontScaleToggle = { isLargeFont = !isLargeFont },
                                    initialStateName = currentStateName,
                                    onNavigateBack = { popBackStack() },
                                    onContinue = { _ ->
                                        // Profile updated: pop back to wherever we came from
                                        popBackStack()
                                        Toast.makeText(
                                            this@MainActivity,
                                            if (currentLanguage == AppLanguage.HINDI)
                                                "पात्रता प्रोफाइल अपडेट की गई!"
                                            else
                                                "Eligibility Profile updated!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                )
                            }

                            is JanSaarthiDestination.Categories -> {
                                org.jansaarthi.app.ui.screens.categories.CategoriesScreen(
                                    currentLanguage = currentLanguage,
                                    onSelectLanguage = authViewModel::setLanguage,
                                    isLargeFont = isLargeFont,
                                    onFontScaleToggle = { isLargeFont = !isLargeFont },
                                    onNavigateBack = { popBackStack() },
                                    onSelectCategory = { category ->
                                        navigateTo(
                                            JanSaarthiDestination.SchemeList(
                                                categoryId = category.id,
                                                categoryName = category.getName(currentLanguage)
                                            )
                                        )
                                    }
                                )
                            }

                            is JanSaarthiDestination.Ministries -> {
                                org.jansaarthi.app.ui.screens.ministries.MinistriesScreen(
                                    currentLanguage = currentLanguage,
                                    onSelectLanguage = authViewModel::setLanguage,
                                    isLargeFont = isLargeFont,
                                    onFontScaleToggle = { isLargeFont = !isLargeFont },
                                    onNavigateBack = { popBackStack() },
                                    onSelectMinistry = { ministry ->
                                        navigateTo(
                                            JanSaarthiDestination.SchemeList(
                                                ministryName = ministry.nameEn
                                            )
                                        )
                                    }
                                )
                            }

                            is JanSaarthiDestination.SchemeList -> {
                                // Capture destination at composition time to avoid stale reads
                                val dest = destination
                                org.jansaarthi.app.ui.screens.schemes.SchemeListScreen(
                                    initialCategoryId = dest.categoryId,
                                    initialCategoryName = dest.categoryName,
                                    initialMinistryName = dest.ministryName,
                                    currentLanguage = currentLanguage,
                                    onSelectLanguage = authViewModel::setLanguage,
                                    isLargeFont = isLargeFont,
                                    onFontScaleToggle = { isLargeFont = !isLargeFont },
                                    onNavigateBack = { popBackStack() },
                                    onNavigateSchemeDetails = { schemeId ->
                                        navigateTo(JanSaarthiDestination.SchemeDetails(schemeId))
                                    }
                                )
                            }

                            is JanSaarthiDestination.AllSchemes -> {
                                org.jansaarthi.app.ui.screens.schemes.AllSchemesScreen(
                                    currentLanguage = currentLanguage,
                                    onSelectLanguage = authViewModel::setLanguage,
                                    isLargeFont = isLargeFont,
                                    onFontScaleToggle = { isLargeFont = !isLargeFont },
                                    onNavigateBack = { popBackStack() },
                                    onNavigateHome = { navigateToRoot(JanSaarthiDestination.Home) },
                                    onNavigateApplications = { navigateToRoot(JanSaarthiDestination.MyApplications) },
                                    onNavigateDocuments = { navigateToRoot(JanSaarthiDestination.Documents) },
                                    onNavigateProfile = { navigateToRoot(JanSaarthiDestination.ProfileSettings) }
                                )
                            }

                            is JanSaarthiDestination.FindSchemesForMe -> {
                                org.jansaarthi.app.ui.screens.schemes.FindSchemesForMeScreen(
                                    currentLanguage = currentLanguage,
                                    onSelectLanguage = authViewModel::setLanguage,
                                    isLargeFont = isLargeFont,
                                    onFontScaleToggle = { isLargeFont = !isLargeFont },
                                    onNavigateBack = { popBackStack() },
                                    onEditProfile = {
                                        navigateTo(JanSaarthiDestination.EligibilityProfile)
                                    },
                                    onViewAllSchemes = {
                                        navigateTo(JanSaarthiDestination.AllSchemes)
                                    }
                                )
                            }

                            is JanSaarthiDestination.SchemeDetails -> {
                                val dest = destination
                                org.jansaarthi.app.ui.screens.schemes.SchemeDetailsScreen(
                                    schemeId = dest.schemeId,
                                    currentLanguage = currentLanguage,
                                    onSelectLanguage = authViewModel::setLanguage,
                                    isLargeFont = isLargeFont,
                                    onFontScaleToggle = { isLargeFont = !isLargeFont },
                                    // Back correctly pops to origin (Search, SavedSchemes, SchemeList, Home)
                                    onNavigateBack = { popBackStack() }
                                )
                            }

                            is JanSaarthiDestination.Documents -> {
                                org.jansaarthi.app.ui.screens.documents.DocumentsScreen(
                                    currentLanguage = currentLanguage,
                                    onSelectLanguage = authViewModel::setLanguage,
                                    isLargeFont = isLargeFont,
                                    onFontScaleToggle = { isLargeFont = !isLargeFont },
                                    onNavigateBack = { popBackStack() },
                                    onNavigateHome = { navigateToRoot(JanSaarthiDestination.Home) },
                                    onNavigateSchemes = { navigateToRoot(JanSaarthiDestination.AllSchemes) },
                                    onNavigateApplications = { navigateToRoot(JanSaarthiDestination.MyApplications) },
                                    onNavigateProfile = { navigateToRoot(JanSaarthiDestination.ProfileSettings) }
                                )
                            }

                            is JanSaarthiDestination.MyApplications -> {
                                org.jansaarthi.app.ui.screens.applications.MyApplicationsScreen(
                                    currentLanguage = currentLanguage,
                                    onSelectLanguage = authViewModel::setLanguage,
                                    isLargeFont = isLargeFont,
                                    onFontScaleToggle = { isLargeFont = !isLargeFont },
                                    onNavigateBack = { popBackStack() },
                                    onNavigateHome = { navigateToRoot(JanSaarthiDestination.Home) },
                                    onNavigateSchemes = { navigateToRoot(JanSaarthiDestination.AllSchemes) },
                                    onNavigateDocuments = { navigateToRoot(JanSaarthiDestination.Documents) },
                                    onNavigateProfile = { navigateToRoot(JanSaarthiDestination.ProfileSettings) }
                                )
                            }

                            is JanSaarthiDestination.Search -> {
                                val dest = destination
                                org.jansaarthi.app.ui.screens.search.SchemeSearchScreen(
                                    currentLanguage = currentLanguage,
                                    onSelectLanguage = authViewModel::setLanguage,
                                    isLargeFont = isLargeFont,
                                    onFontScaleToggle = { isLargeFont = !isLargeFont },
                                    initialQuery = dest.initialQuery,
                                    onNavigateBack = { popBackStack() },
                                    onOpenSchemeDetails = { schemeId ->
                                        navigateTo(JanSaarthiDestination.SchemeDetails(schemeId))
                                    }
                                )
                            }

                            is JanSaarthiDestination.SavedSchemes -> {
                                org.jansaarthi.app.ui.screens.saved.SavedSchemesScreen(
                                    currentLanguage = currentLanguage,
                                    onSelectLanguage = authViewModel::setLanguage,
                                    isLargeFont = isLargeFont,
                                    onFontScaleToggle = { isLargeFont = !isLargeFont },
                                    onNavigateBack = { popBackStack() },
                                    onExploreSchemes = { navigateTo(JanSaarthiDestination.AllSchemes) },
                                    onOpenSchemeDetails = { schemeId ->
                                        navigateTo(JanSaarthiDestination.SchemeDetails(schemeId))
                                    }
                                )
                            }

                            is JanSaarthiDestination.ProfileSettings -> {
                                val prof = currentSession?.profile
                                org.jansaarthi.app.ui.screens.profile.ProfileSettingsScreen(
                                    currentLanguage = currentLanguage,
                                    onSelectLanguage = authViewModel::setLanguage,
                                    isLargeFont = isLargeFont,
                                    onFontScaleToggle = { isLargeFont = !isLargeFont },
                                    citizenName = prof?.fullName?.ifBlank { null } ?: "Citizen",
                                    citizenMobile = prof?.mobileNumber ?: "+91 00000 00000",
                                    citizenState = prof?.selectedState ?: "Maharashtra",
                                    citizenDistrict = prof?.selectedDistrict ?: "",
                                    isGuest = currentSession?.isGuest ?: false,
                                    onNavigateBack = { popBackStack() },
                                    onOpenMyProfile = {
                                        navigateTo(JanSaarthiDestination.EligibilityProfile)
                                    },
                                    onOpenEligibilityProfile = {
                                        navigateTo(JanSaarthiDestination.EligibilityProfile)
                                    },
                                    onOpenStateSelection = {
                                        navigateTo(JanSaarthiDestination.StateSelection)
                                    },
                                    onOpenSavedSchemes = {
                                        navigateTo(JanSaarthiDestination.SavedSchemes)
                                    },
                                    onOpenMyApplications = {
                                        navigateTo(JanSaarthiDestination.MyApplications)
                                    },
                                    onOpenMyDocuments = {
                                        navigateTo(JanSaarthiDestination.Documents)
                                    },
                                    onOpenHelpSupport = { },
                                    onNavigateHome = { navigateToRoot(JanSaarthiDestination.Home) },
                                    onNavigateSchemes = { navigateToRoot(JanSaarthiDestination.AllSchemes) },
                                    onNavigateApplications = { navigateToRoot(JanSaarthiDestination.MyApplications) },
                                    onNavigateDocuments = { navigateToRoot(JanSaarthiDestination.Documents) },
                                    onLogout = { authViewModel.logout() }
                                )
                            }

                            is JanSaarthiDestination.Home -> {
                                CitizenHomeScreen(
                                    viewModel = authViewModel,
                                    onChangeState = {
                                        navigateTo(JanSaarthiDestination.StateSelection)
                                    },
                                    onOpenEligibilityProfile = {
                                        navigateTo(JanSaarthiDestination.EligibilityProfile)
                                    },
                                    onOpenFindSchemesForMe = {
                                        navigateTo(JanSaarthiDestination.FindSchemesForMe)
                                    },
                                    onOpenCategories = {
                                        navigateTo(JanSaarthiDestination.Categories)
                                    },
                                    onOpenMinistries = {
                                        navigateTo(JanSaarthiDestination.Ministries)
                                    },
                                    onOpenAllSchemes = {
                                        navigateTo(JanSaarthiDestination.AllSchemes)
                                    },
                                    onOpenDocuments = {
                                        navigateTo(JanSaarthiDestination.Documents)
                                    },
                                    onOpenApplications = {
                                        navigateTo(JanSaarthiDestination.MyApplications)
                                    },
                                    onOpenSearch = { query ->
                                        navigateTo(JanSaarthiDestination.Search(query))
                                    },
                                    onOpenSavedSchemes = {
                                        navigateTo(JanSaarthiDestination.SavedSchemes)
                                    },
                                    onOpenProfileSettings = {
                                        navigateTo(JanSaarthiDestination.ProfileSettings)
                                    },
                                    currentLanguage = currentLanguage,
                                    onSelectLanguage = authViewModel::setLanguage,
                                    isLargeFont = isLargeFont,
                                    onFontScaleToggle = { isLargeFont = !isLargeFont }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun determineInitialDestination(): JanSaarthiDestination {
        val session = SessionManager(applicationContext).getSession()
        return when {
            session == null -> JanSaarthiDestination.Login
            !session.profile.isProfileSetupComplete -> JanSaarthiDestination.StateSelection
            else -> JanSaarthiDestination.Home
        }
    }
}
