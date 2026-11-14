package com.example.finalproject.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val WearIqDarkColorScheme = darkColorScheme(
    primary = ElectricCyan,
    onPrimary = Color(0xFF06131D),
    primaryContainer = MidnightNavyElevated,
    onPrimaryContainer = ElectricCyanGlow,
    secondary = ElectricCyanDark,
    onSecondary = Color.White,
    background = ObsidianBlack,
    onBackground = TextPrimary,
    surface = MidnightNavy,
    onSurface = TextPrimary,
    surfaceVariant = MidnightNavyElevated,
    onSurfaceVariant = TextSecondary,
    outline = BorderSubtle,
    error = ErrorRose,
    onError = Color.White
)

@Composable
fun FinalProjectTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = WearIqDarkColorScheme,
        typography = Typography,
        content = content
    )
}
