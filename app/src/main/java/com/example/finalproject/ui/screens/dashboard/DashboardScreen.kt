package com.example.finalproject.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finalproject.ui.theme.BorderSubtle
import com.example.finalproject.ui.theme.ElectricCyan
import com.example.finalproject.ui.theme.ErrorRose
import com.example.finalproject.ui.theme.MidnightNavy
import com.example.finalproject.ui.theme.MidnightNavyElevated
import com.example.finalproject.ui.theme.ObsidianBlack
import com.example.finalproject.ui.theme.TextPrimary
import com.example.finalproject.ui.theme.TextSecondary

@Composable
fun DashboardScreen(
    onNavigateToCatalog: () -> Unit,
    onNavigateToPos: () -> Unit,
    onNavigateToRestock: () -> Unit,
    onNavigateToAiInsight: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // TOP BAR
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "WearIQ POS",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Panel Kontrol Utama",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }

                // Tombol Keluar (Logout)
                IconButton(
                    onClick = onLogout,
                    modifier = Modifier
                        .size(44.dp)
                        .background(MidnightNavyElevated, CircleShape)
                        .border(1.dp, BorderSubtle, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Keluar Akun",
                        tint = ErrorRose
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // DAFTAR MENU FITUR POS
            DashboardMenuCard(
                title = "Kasir (POS)",
                subtitle = "Proses transaksi penjualan pakaian & cetak struk belanja",
                icon = Icons.Default.PointOfSale,
                onClick = onNavigateToPos
            )

            DashboardMenuCard(
                title = "Katalog Pakaian",
                subtitle = "Kelola varian baju, SKU, ukuran, warna, & harga jual",
                icon = Icons.Default.ShoppingBag,
                onClick = onNavigateToCatalog
            )

            DashboardMenuCard(
                title = "Manajemen Restock",
                subtitle = "Pantau stok menipis & perbarui persediaan pakaian",
                icon = Icons.Default.Inventory,
                onClick = onNavigateToRestock
            )

            DashboardMenuCard(
                title = "WearIQ AI Insights",
                subtitle = "Prediksi restock & analisis tren busana via Gemini 2.5 Flash",
                icon = Icons.Default.AutoAwesome,
                onClick = onNavigateToAiInsight,
                isAiHighlight = true
            )
        }
    }
}

@Composable
private fun DashboardMenuCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    isAiHighlight: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .border(
                1.dp,
                if (isAiHighlight) ElectricCyan else BorderSubtle,
                RoundedCornerShape(14.dp)
            ),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isAiHighlight) MidnightNavyElevated else MidnightNavy
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        if (isAiHighlight) ElectricCyan.copy(alpha = 0.2f) else MidnightNavyElevated,
                        RoundedCornerShape(12.dp)
                    )
                    .border(
                        1.dp,
                        if (isAiHighlight) ElectricCyan else BorderSubtle,
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = ElectricCyan,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
