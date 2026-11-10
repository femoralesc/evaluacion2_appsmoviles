package com.example.juntavecinal_grupox.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = SoftGreen,
    onPrimary = OnSoftGreen,
    primaryContainer = ForestGreen,
    onPrimaryContainer = Color.White,
    secondary = SoftClay,
    onSecondary = OnSoftClay,
    secondaryContainer = Clay,
    onSecondaryContainer = Color.White,
    background = Color(0xFF111411),
    onBackground = Color(0xFFE2E3DD),
    surface = Color(0xFF111411),
    onSurface = Color(0xFFE2E3DD),
    surfaceVariant = Color(0xFF414941),
    onSurfaceVariant = Color(0xFFC1C9BF),
    error = ErrorRed,
    errorContainer = ErrorContainer
)

private val LightColorScheme = lightColorScheme(
    primary = ForestGreen,
    onPrimary = OnForestGreen,
    primaryContainer = SoftGreen,
    onPrimaryContainer = OnSoftGreen,
    secondary = Clay,
    onSecondary = Color.White,
    secondaryContainer = SoftClay,
    onSecondaryContainer = OnSoftClay,
    background = WarmBackground,
    onBackground = Ink,
    surface = WarmSurface,
    onSurface = Ink,
    surfaceVariant = Color(0xFFE7EBE4),
    onSurfaceVariant = MutedInk,
    outline = Border,
    error = ErrorRed,
    errorContainer = ErrorContainer
)

@Composable
fun JuntaVecinal_GrupoXTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
