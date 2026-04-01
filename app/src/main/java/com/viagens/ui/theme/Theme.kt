package com.viagens.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = PrimaryPurple,
    secondary = SecondaryPurple,
    tertiary = LightPurple,

    background = LightBackground,
    surface = LightSurface,

    onPrimary = ButtonText,
    onSecondary = ButtonText,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

@Composable
fun ViagensTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography(),
        content = content
    )
}