package com.example.juntavecinal_grupox.ui.tema

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.juntavecinal_grupox.R

private val familiaLato = FontFamily(
    Font(R.font.lato_normal, FontWeight.Normal),
    Font(R.font.lato_medio, FontWeight.Medium),
    Font(R.font.lato_negrita, FontWeight.Bold)
)

private val tipografiaBase = Typography()

val Tipografia = tipografiaBase.copy(
    displayLarge = tipografiaBase.displayLarge.copy(fontFamily = familiaLato),
    displayMedium = tipografiaBase.displayMedium.copy(fontFamily = familiaLato),
    displaySmall = tipografiaBase.displaySmall.copy(fontFamily = familiaLato),
    headlineLarge = tipografiaBase.headlineLarge.copy(fontFamily = familiaLato),
    headlineMedium = tipografiaBase.headlineMedium.copy(fontFamily = familiaLato),
    headlineSmall = tipografiaBase.headlineSmall.copy(fontFamily = familiaLato),
    titleLarge = tipografiaBase.titleLarge.copy(fontFamily = familiaLato),
    titleMedium = tipografiaBase.titleMedium.copy(fontFamily = familiaLato),
    titleSmall = tipografiaBase.titleSmall.copy(fontFamily = familiaLato),
    bodyLarge = tipografiaBase.bodyLarge.copy(fontFamily = familiaLato),
    bodyMedium = tipografiaBase.bodyMedium.copy(fontFamily = familiaLato),
    bodySmall = tipografiaBase.bodySmall.copy(fontFamily = familiaLato),
    labelLarge = tipografiaBase.labelLarge.copy(fontFamily = familiaLato),
    labelMedium = tipografiaBase.labelMedium.copy(fontFamily = familiaLato),
    labelSmall = tipografiaBase.labelSmall.copy(fontFamily = familiaLato)
)
