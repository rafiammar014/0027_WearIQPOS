package com.example.finalproject.ui.screens.restock

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finalproject.ui.components.AmbientBackground
import com.example.finalproject.ui.theme.CrimsonRed
import com.example.finalproject.ui.theme.DarkNavyBackground
import com.example.finalproject.ui.theme.DarkNavyContainerHigh
import com.example.finalproject.ui.theme.WarmCream

@Composable
fun RestockScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkNavyBackground)
    ) {
        AmbientBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(DarkNavyContainerHigh.copy(alpha = 0.8f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Inventory2,
                        contentDescription = null,
                        tint = CrimsonRed,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Manajemen Restock",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = WarmCream
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Pantau dan perbarui stok pakaian yang menipis.",
                    fontSize = 13.sp,
                    color = WarmCream.copy(alpha = 0.65f)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CrimsonRed,
                        contentColor = WarmCream
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Kembali ke Dashboard", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(96.dp))
            }
        }
    }
}
