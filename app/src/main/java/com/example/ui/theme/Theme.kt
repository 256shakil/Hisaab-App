package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = BluePrimary,
    secondary = GreenSecondary,
    tertiary = AmberAccent,
    background = DarkBackground,
    surface = SurfaceDark,
    onBackground = OnSurfaceDark,
    onSurface = OnSurfaceDark,
    error = RedExpense,
    surfaceContainer = SurfaceContainerDark,
    surfaceContainerLow = SurfaceContainerLowDark
)

private val LightColorScheme = lightColorScheme(
    primary = BluePrimary,
    secondary = GreenSecondary,
    tertiary = AmberAccent,
    background = LightBackground,
    surface = SurfaceLight,
    onBackground = OnSurfaceLight,
    onSurface = OnSurfaceLight,
    error = RedExpense,
    surfaceContainer = SurfaceContainerLight,
    surfaceContainerLow = SurfaceContainerLowLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
