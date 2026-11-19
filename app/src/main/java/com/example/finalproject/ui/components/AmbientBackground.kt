package com.example.finalproject.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.example.finalproject.ui.theme.CrimsonRed
import com.example.finalproject.ui.theme.DarkNavyBackground
import com.example.finalproject.ui.theme.WarmCream

/**
 * Ambient Glow Background sesuai android_compose_m3_ui_ux_skill.md:
 * Menampilkan kanvas Dark Navy (#0B132B) dengan 2 lingkaran cahaya lembut (radial gradient blur)
 * menggunakan warna Red (#E63946 alpha 0.18f) dan Cream (#F5EFE6 alpha 0.10f)
 * agar efek glassmorphism terlihat berkilau dan hidup.
 */
@Composable
fun AmbientBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkNavyBackground)
    ) {
        // Red Ambient Glow (Atas-Kanan)
        Box(
            modifier = Modifier
                .size(320.dp)
                .align(Alignment.TopEnd)
                .offset(x = 60.dp, y = (-40).dp)
                .blur(80.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            CrimsonRed.copy(alpha = 0.22f),
                            CrimsonRed.copy(alpha = 0.05f),
                            DarkNavyBackground.copy(alpha = 0f)
                        )
                    ),
                    shape = CircleShape
                )
        )

        // Cream Ambient Glow (Bawah-Kiri)
        Box(
            modifier = Modifier
                .size(360.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-80).dp, y = 80.dp)
                .blur(90.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            WarmCream.copy(alpha = 0.12f),
                            WarmCream.copy(alpha = 0.03f),
                            DarkNavyBackground.copy(alpha = 0f)
                        )
                    ),
                    shape = CircleShape
                )
        )

        content()
    }
}
