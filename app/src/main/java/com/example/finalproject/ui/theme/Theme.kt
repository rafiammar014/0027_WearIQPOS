package com.example.finalproject.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Strict Material 3 Color System: Dark Navy, Cream, Red & Glassmorphism
 * Reference: android_compose_m3_ui_ux_skill.md
 */
private val WearIqDarkColorScheme = darkColorScheme(
    primary = CrimsonRed,                      // #E63946 Vibrant Crimson Red (Tombol aksi utama, chip aktif)
    onPrimary = WarmCream,                     // #F5EFE6 Teks Cream di atas tombol merah
    primaryContainer = DeepRedContainer,       // #5C161D Deep Red untuk badge / highlight status
    onPrimaryContainer = WarmCream,
    secondary = CrimsonRed,
    onSecondary = WarmCream,
    background = DarkNavyBackground,           // #0B132B Deep Midnight Navy (Kanvas utama)
    onBackground = WarmCream,                  // #F5EFE6 Warm Cream (Judul & teks utama)
    surface = DarkNavyContainerLow,            // #121C3A Navy menengah (Kontainer dasar)
    onSurface = WarmCream,                     // #F5EFE6 Warm Cream (Teks permukaan)
    surfaceVariant = DarkNavyContainerHigh,    // #1C2541 Elevated Navy
    onSurfaceVariant = SoftCream,              // #D0C7BA Soft Cream untuk subjudul dan label
    surfaceContainerLow = DarkNavyContainerLow,
    surfaceContainerHigh = DarkNavyContainerHigh,
    outline = Color(0xFFF5EFE6).copy(alpha = 0.20f),
    error = ErrorBrightRed,                    // #FF4D5A Merah terang validasi error
    onError = WarmCream
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
