package com.example.finalproject.ui.theme

import androidx.compose.ui.graphics.Color

// =======================================================================
// UI/UX DESIGN SYSTEM: DARK NAVY, CREAM, RED & GLASSMORPHISM
// Based on android_compose_m3_ui_ux_skill.md
// =======================================================================

// 1. Dark Navy (Kanvas & Latar Belakang Utama)
val DarkNavyBackground = Color(0xFF0B132B)       // Deep Midnight Navy (#0B132B)
val DarkNavyContainerLow = Color(0xFF121C3A)     // Navy menengah untuk kontainer dasar (#121C3A)
val DarkNavyContainerHigh = Color(0xFF1C2541)    // Elevated Navy (#1C2541)

// 2. Cream (Tipografi, Ikon & Sorotan Teks)
val WarmCream = Color(0xFFF5EFE6)                // Warm Cream (#F5EFE6)
val SoftCream = Color(0xFFD0C7BA)                // Soft Cream untuk label/deskripsi (#D0C7BA)
val MutedCream = Color(0xFF9E9589)               // Muted Cream untuk placeholder/caption

// 3. Red (Aksen Utama, CTA & Status Aktif)
val CrimsonRed = Color(0xFFE63946)               // Vibrant Crimson Red (#E63946)
val DeepRedContainer = Color(0xFF5C161D)         // Deep Red (#5C161D)
val ErrorBrightRed = Color(0xFFFF4D5A)           // Merah terang validasi (#FF4D5A)

// 4. Glassmorphism Specular Gradients & Ambient Glow
val GlowRed = Color(0xFFE63946)                  // Alpha 0.18f in ambient box
val GlowCream = Color(0xFFF5EFE6)                // Alpha 0.10f in ambient box
val BorderGlassSpecular = Color(0xFFF5EFE6)      // Specular border gradient top-left (alpha 0.35f) to bottom-right (alpha 0.05f)

// 5. Semantic Status Colors
val SuccessEmerald = Color(0xFF10B981)
val WarningAmber = Color(0xFFF59E0B)
