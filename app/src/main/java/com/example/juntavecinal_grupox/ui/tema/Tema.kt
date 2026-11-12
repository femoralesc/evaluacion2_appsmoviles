package com.example.juntavecinal_grupox.ui.tema

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ColoresOscuros = darkColorScheme(
    primary = Color(0xFFB5CEF0),
    onPrimary = AzulMarino,
    primaryContainer = AzulMarino,
    onPrimaryContainer = Color(0xFFE5EEFB),
    secondary = Color(0xFF76D5C7),
    onSecondary = Color(0xFF003D35),
    secondaryContainer = Color(0xFF164F46),
    onSecondaryContainer = Color(0xFFB4F0E5),
    tertiary = Color(0xFFFFB3B8),
    onTertiary = Color(0xFF650015),
    tertiaryContainer = Color(0xFF8A1222),
    onTertiaryContainer = FondoAlerta,
    background = Color(0xFF111820),
    onBackground = Color(0xFFE7EDF3),
    surface = Color(0xFF111820),
    onSurface = Color(0xFFE7EDF3),
    surfaceVariant = Color(0xFF303C49),
    onSurfaceVariant = Color(0xFFC6D1DC),
    surfaceContainerLowest = Color(0xFF0C1219),
    surfaceContainerLow = Color(0xFF17212B),
    surfaceContainer = Color(0xFF1C2732),
    surfaceContainerHigh = Color(0xFF24303D),
    surfaceContainerHighest = Color(0xFF303C49),
    surfaceTint = Color(0xFFB5CEF0),
    outline = Color(0xFF8B99A8),
    outlineVariant = Color(0xFF465363),
    error = Color(0xFFFFB3B8),
    onError = Color(0xFF650015),
    errorContainer = Color(0xFF8A1222),
    onErrorContainer = FondoAlerta
)

private val ColoresClaros = lightColorScheme(
    primary = AzulMarino,
    onPrimary = Color.White,
    primaryContainer = AzulSuave,
    onPrimaryContainer = AzulMarino,
    secondary = VerdeAgua,
    onSecondary = GrisCarbon,
    secondaryContainer = VerdeAguaSuave,
    onSecondaryContainer = VerdeAguaOscuro,
    tertiary = RojoCoral,
    onTertiary = Color.Black,
    tertiaryContainer = FondoAlerta,
    onTertiaryContainer = RojoAlertaOscuro,
    background = BlancoHumo,
    onBackground = GrisCarbon,
    surface = Color.White,
    onSurface = GrisCarbon,
    surfaceVariant = GrisSuave,
    onSurfaceVariant = TextoSecundario,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = BlancoHumo,
    surfaceContainer = Color(0xFFF1F3F5),
    surfaceContainerHigh = Color(0xFFEDF0F3),
    surfaceContainerHighest = GrisSuave,
    surfaceTint = AzulMarino,
    outline = Borde,
    outlineVariant = Color(0xFFCCD2D8),
    error = RojoAlertaOscuro,
    onError = Color.White,
    errorContainer = FondoAlerta,
    onErrorContainer = RojoAlertaOscuro
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
