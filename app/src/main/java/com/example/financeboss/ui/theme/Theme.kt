package com.example.financeboss.ui.theme


import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme

import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable



private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = PrimaryLight.copy(alpha = 0.15f),
    secondary = Secondary,
    background = BackgroundLight,
    surface = SurfaceLight,
    onBackground = OnSurfaceLight,
    onSurface = OnSurfaceLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    surfaceVariant = BackgroundLight,
    error = ExpenseRed
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryLight,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = Primary.copy(alpha = 0.3f),
    secondary = Secondary,
    background = BackgroundDark,
    surface = SurfaceDark,
    onBackground = OnSurfaceDark,
    onSurface = OnSurfaceDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    surfaceVariant = SurfaceDark,
    error = ExpenseRed
)



@Composable
fun FinanceBossTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography(),
        content = content
    )
}