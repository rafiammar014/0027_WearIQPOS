package com.example.finalproject.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.example.finalproject.ui.theme.DarkNavyContainerHigh
import com.example.finalproject.ui.theme.WarmCream

/**
 * Komponen Standar Glassmorphism sesuai panduan android_compose_m3_ui_ux_skill.md:
 * 1. Translucent Surface: Layer #1C2541 (alpha 0.75f) + aksen tipis Warm Cream (alpha 0.05f)
 * 2. Specular Gradient Border: 1dp Brush.linearGradient dari kiri-atas (Cream alpha 0.35f) ke kanan-bawah (Cream alpha 0.05f)
 * 3. Rounded Corners: Default RoundedCornerShape(20.dp)
 *
 * Catatan penting: Modifier.blur() tidak diterapkan pada kontainer yang membungkus teks/input
 * karena akan memburamkan seluruh teks dan isi formulir. Efek blur diterapkan pada latar belakang (AmbientBackground).
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    content: @Composable BoxScope.() -> Unit
) {
    val specularBorder = Brush.linearGradient(
        colors = listOf(
            WarmCream.copy(alpha = 0.35f),
            WarmCream.copy(alpha = 0.06f)
        )
    )

    Box(
        modifier = modifier
            .clip(shape)
            .border(width = 1.dp, brush = specularBorder, shape = shape)
            .background(DarkNavyContainerHigh.copy(alpha = 0.78f))
            .background(WarmCream.copy(alpha = 0.03f))
    ) {
        content()
    }
}
