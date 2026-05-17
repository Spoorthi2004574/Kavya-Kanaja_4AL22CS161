package com.kavyakanaja.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = AccentMaroon,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFD9DE),
    onPrimaryContainer = Color(0xFF410013),
    secondary = InkBrown,
    onSecondary = Color.White,
    tertiary = MutedInk,
    background = PaperCream,
    onBackground = InkBrown,
    surface = PaperSurface,
    onSurface = InkBrown,
    surfaceVariant = Color(0xFFE8E0D6),
    onSurfaceVariant = MutedInk,
    outline = Color(0xFFB0A69A),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFFFB1C4),
    onPrimary = Color(0xFF670021),
    secondary = Color(0xFFD9C4B0),
    onSecondary = Color(0xFF3B2D22),
    background = Color(0xFF1B1714),
    onBackground = Color(0xFFEAE3DA),
    surface = Color(0xFF231E1A),
    onSurface = Color(0xFFEAE3DA),
)

@Composable
fun KavyaKanajaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val scheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = scheme,
        typography = KavyaTypography,
        content = content,
    )
}
