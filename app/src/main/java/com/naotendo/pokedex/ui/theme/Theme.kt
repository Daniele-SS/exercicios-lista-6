package com.naotendo.pokedex.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val PokedexRed = Color(0xFFDC0A2D)
val PokedexBackground = Color(0xFFEFEFEF)
val PokedexGrayDark = Color(0xFF1D1D1D)
val PokedexGrayMedium = Color(0xFF666666)
val PokedexGrayLight = Color(0xFFE0E0E0)

private val PokedexColorScheme = lightColorScheme(
    primary = PokedexRed,
    onPrimary = Color.White,
    background = PokedexBackground,
    onBackground = PokedexGrayDark,
    surface = Color.White,
    onSurface = PokedexGrayDark,
    onSurfaceVariant = PokedexGrayMedium,
    outline = PokedexGrayLight,
)

@Composable
fun PokedexTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = PokedexColorScheme, content = content)
}
