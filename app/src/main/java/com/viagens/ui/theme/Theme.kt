package com.viagens.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = TopGradientMedium,
    secondary = ButtonGradientStart,
    tertiary = TopGradientStart,
    background = White,
    surface = White,
    onPrimary = White,
    onSecondary = White,
    onBackground = TextDark,
    onSurface = TextDark
)

@Composable
fun ViagensTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
