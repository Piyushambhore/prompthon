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

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF90CAF9),
    onPrimary = Color(0xFF003258),
    primaryContainer = Color(0xFF0D3B66),
    onPrimaryContainer = Color(0xFFD1E4FF),
    secondary = Color(0xFFFFB74D),
    onSecondary = Color(0xFF4E2600),
    tertiary = Color(0xFF81C784),
    onTertiary = Color(0xFF00390F),
    background = Color(0xFF101418),
    onBackground = Color(0xFFE2E2E6),
    surface = Color(0xFF1A1F26),
    onSurface = Color(0xFFE2E2E6),
    surfaceVariant = Color(0xFF262C36),
    onSurfaceVariant = Color(0xFFC4C7C5),
    outline = Color(0xFF475569)
)

@Composable
fun JanSaarthiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.primary.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = JanSaarthiTypography,
        content = content
    )
}
