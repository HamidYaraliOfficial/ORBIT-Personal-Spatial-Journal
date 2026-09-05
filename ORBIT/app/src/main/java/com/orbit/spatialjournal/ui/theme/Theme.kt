package com.orbit.spatialjournal.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.orbit.spatialjournal.core.model.AccentStyle
import com.orbit.spatialjournal.core.model.ThemeMode

/**
 * Two independent axes, as requested: brightness Mode (Light / Dark / AMOLED / System) and
 * Accent Style (Windows 11 default / Red / Blue). Every combination is a real, distinct
 * Material3 ColorScheme — nine usable themes in total.
 */
private fun colorSchemeFor(mode: ThemeMode, accent: AccentStyle, systemDark: Boolean): ColorScheme {
    val isDark = when (mode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK, ThemeMode.AMOLED -> true
        ThemeMode.SYSTEM -> systemDark
    }

    val base = when (accent) {
        AccentStyle.WINDOWS11 -> if (isDark) {
            darkColorScheme(
                primary = Win11DarkPrimary, onPrimary = Win11DarkOnPrimary,
                background = Win11DarkBackground, surface = Win11DarkSurface, surfaceVariant = Win11DarkSurfaceVariant
            )
        } else {
            lightColorScheme(
                primary = Win11LightPrimary, onPrimary = Win11LightOnPrimary,
                background = Win11LightBackground, surface = Win11LightSurface, surfaceVariant = Win11LightSurfaceVariant
            )
        }
        AccentStyle.RED -> if (isDark) {
            darkColorScheme(primary = RedDarkPrimary, onPrimary = RedDarkOnPrimary)
        } else {
            lightColorScheme(primary = RedLightPrimary, onPrimary = RedLightOnPrimary)
        }
        AccentStyle.BLUE -> if (isDark) {
            darkColorScheme(primary = BlueDarkPrimary, onPrimary = BlueDarkOnPrimary)
        } else {
            lightColorScheme(primary = BlueLightPrimary, onPrimary = BlueLightOnPrimary)
        }
    }

    return if (mode == ThemeMode.AMOLED) {
        base.copy(background = AmoledBackground, surface = AmoledSurface)
    } else base
}

@Composable
fun OrbitTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    accentStyle: AccentStyle = AccentStyle.WINDOWS11,
    content: @Composable () -> Unit
) {
    val systemDark = isSystemInDarkTheme()
    val colorScheme = colorSchemeFor(themeMode, accentStyle, systemDark)
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars =
                colorScheme == colorScheme && !(themeMode == ThemeMode.DARK || themeMode == ThemeMode.AMOLED || (themeMode == ThemeMode.SYSTEM && systemDark))
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = OrbitTypography,
        shapes = OrbitShapes,
        content = content
    )
}
