package org.jansaarthi.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = GovNavyPrimary,
    onPrimary = Color.White,
    primaryContainer = GovNavyContainer,
    onPrimaryContainer = GovOnNavyContainer,
    secondary = GovSaffron,
    onSecondary = Color.White,
    secondaryContainer = GovSaffronLight,
    onSecondaryContainer = Color(0xFF6B2600),
    tertiary = GovGreen,
    onTertiary = Color.White,
    tertiaryContainer = GovGreenLight,
    onTertiaryContainer = Color(0xFF0A3810),
    background = GovBackground,
    onBackground = GovTextPrimary,
    surface = GovSurface,
    onSurface = GovTextPrimary,
    surfaceVariant = GovSurfaceVariant,
    onSurfaceVariant = GovTextSecondary,
    outline = GovBorder,
    error = GovError,
    onError = GovOnError,
    errorContainer = GovErrorContainer
)

// Consistent high-contrast portal theme
private val DarkColorScheme = LightColorScheme

@Composable
fun JanSaarthiTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                // Status bar matches GovNavyPrimary header seamlessly with white status icons
                window.statusBarColor = GovNavyPrimary.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false

                // Navigation bar matches bottom bar white surface with dark navigation icons
                window.navigationBarColor = Color.White.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = true
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = JanSaarthiTypography,
        content = content
    )
}
