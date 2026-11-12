package com.example.juntavecinal_grupox.ui.tema

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ColoresOscuros = darkColorScheme(
    primary = VerdeSuave,
    onPrimary = SobreVerdeSuave,
    primaryContainer = VerdeBosque,
    onPrimaryContainer = Color.White,
    secondary = ArcillaSuave,
    onSecondary = SobreArcillaSuave,
    secondaryContainer = Arcilla,
    onSecondaryContainer = Color.White,
    background = Color(0xFF111411),
    onBackground = Color(0xFFE2E3DD),
    surface = Color(0xFF111411),
    onSurface = Color(0xFFE2E3DD),
    surfaceVariant = Color(0xFF414941),
    onSurfaceVariant = Color(0xFFC1C9BF),
    error = RojoError,
    errorContainer = FondoError
)

private val ColoresClaros = lightColorScheme(
    primary = VerdeBosque,
    onPrimary = SobreVerdeBosque,
    primaryContainer = VerdeSuave,
    onPrimaryContainer = SobreVerdeSuave,
    secondary = Arcilla,
    onSecondary = Color.White,
    secondaryContainer = ArcillaSuave,
    onSecondaryContainer = SobreArcillaSuave,
    background = FondoCalido,
    onBackground = Tinta,
    surface = SuperficieCalida,
    onSurface = Tinta,
    surfaceVariant = Color(0xFFE7EBE4),
    onSurfaceVariant = TintaSecundaria,
    outline = Borde,
    error = RojoError,
    errorContainer = FondoError
)

@Composable
fun TemaJuntaVecinal(
    temaOscuro: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (temaOscuro) ColoresOscuros else ColoresClaros,
        typography = Tipografia,
        content = content
    )
}
