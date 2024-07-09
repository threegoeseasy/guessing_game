package com.tge.guessinggame.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
    primary = ColorBackgroundDark,
    onPrimary = ColorForegroundDark,
    surface = ColorBackgroundDark,
    onSurface = ColorForegroundDark,
    secondary = GreenDarkTheme
)

private val LightColorPalette = lightColors(
    primary = Purple500,
    onPrimary = Black,
    surface = White,
    onSurface = Black,
    secondary = GreenLightTheme
)

@Composable
fun GuessingGameTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        content = content
    )
}