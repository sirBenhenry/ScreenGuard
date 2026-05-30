package com.sirbenhenry.screenguard.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = Color(0xFF4A9EFF),
    onPrimary = Color(0xFF001F3F),
    primaryContainer = Color(0xFF0A2A4A),
    onPrimaryContainer = Color(0xFF88CCFF),
    secondary = Color(0xFF44BB88),
    onSecondary = Color(0xFF003320),
    secondaryContainer = Color(0xFF0A2A1A),
    onSecondaryContainer = Color(0xFF88DDBB),
    background = Color(0xFF0A0E1A),
    onBackground = Color(0xFFE0E8F0),
    surface = Color(0xFF111826),
    onSurface = Color(0xFFDDE4EE),
    surfaceVariant = Color(0xFF1A2233),
    onSurfaceVariant = Color(0xFF8899AA),
    error = Color(0xFFFF6B6B),
    onError = Color(0xFF3A0000),
    outline = Color(0xFF334455)
)

@Composable
fun ScreenGuardTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColors,
        typography = Typography(),
        content = content
    )
}
