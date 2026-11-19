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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ExitToApp
import androidx.compose.material.icons.automirrored.rounded.ReceiptLong
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Checkroom
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material.icons.rounded.PointOfSale
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finalproject.ui.components.AmbientBackground
import com.example.finalproject.ui.theme.CrimsonRed
import com.example.finalproject.ui.theme.DarkNavyBackground
import com.example.finalproject.ui.theme.DarkNavyContainerHigh
import com.example.finalproject.ui.theme.DeepRedContainer
import com.example.finalproject.ui.theme.ErrorBrightRed
import com.example.finalproject.ui.theme.SuccessEmerald
import com.example.finalproject.ui.theme.WarmCream

/**
 * DashboardScreen WearIQ POS:
 * 1. Root berlatar solid Dark Navy (#0B132B) untuk mencegah ghosting transisi.
 * 2. Top Bar dengan greeting + tombol Logout profil (44.dp).
 * 3. Hero Card Cream Solid (#F5EFE6) kontras tinggi dengan nominal penjualan & CTA Kasir.
 * 4. Bento Grid 2 Kolom (148.dp, 22.dp) Glassmorphism (Katalog vs Restock).
 * 5. Banner AI Insights full-width dengan radial glow merah.
 * 6. Seksi ringkas "Transaksi Terakhir" mengisi ruang bawah dengan 2 item transaksi dalam 1 glass card ramping.
 * 7. Padding bawah 96.dp agar konten tidak tertimpa Floating Bottom Bar.
 */
@Composable
fun DashboardScreen(
    onNavigateToCatalog: () -> Unit,
    onNavigateToPos: () -> Unit,
    onNavigateToRestock: () -> Unit,
    onNavigateToAiInsight: () -> Unit,
    onLogout: () -> Unit,
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
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Spacer(modifier = Modifier.height(6.dp))

                // ================= 1. TOP BAR =================
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Selamat bekerja,",
                            fontSize = 13.sp,
                            color = WarmCream.copy(alpha = 0.60f),
                            fontWeight = FontWeight.Normal
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "WearIQ Boutique",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = WarmCream,
                            letterSpacing = (-0.3).sp
                        )
                    }

                    // Tombol Bundar Kaca Logout / Profil (44.dp)
                    IconButton(
                        onClick = onLogout,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(WarmCream.copy(alpha = 0.08f))
                            .border(
                                width = 1.dp,
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        WarmCream.copy(alpha = 0.30f),
                                        WarmCream.copy(alpha = 0.05f)
                                    )
                                ),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ExitToApp,
                            contentDescription = "Keluar Akun",
                            tint = WarmCream,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // ================= 2. HERO CARD — RINGKASAN HARI INI & AKSES KASIR =================
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(WarmCream)
                        .padding(20.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Penjualan Hari Ini",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DarkNavyBackground.copy(alpha = 0.65f)
                            )

                            // Trend badge hijau tipis
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(SuccessEmerald.copy(alpha = 0.12f))
                                    .border(1.dp, SuccessEmerald.copy(alpha = 0.35f), CircleShape)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Rounded.TrendingUp,
                                        contentDescription = null,
                                        tint = SuccessEmerald,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = "+14.8%",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SuccessEmerald
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Nominal Penjualan
                        Text(
                            text = "Rp 4.250.000",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkNavyBackground,
                            letterSpacing = (-0.5).sp
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "18 transaksi selesai",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = DarkNavyBackground.copy(alpha = 0.55f)
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // Tombol Lebar RED SOLID Akses Kasir
                        Button(
                            onClick = onNavigateToPos,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CrimsonRed,
                                contentColor = WarmCream
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.PointOfSale,
                                    contentDescription = null,
                                    tint = WarmCream,
                                    modifier = Modifier.size(19.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Buka Kasir (POS)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = WarmCream
                                )
                            }
                        }
                    }
                }

                // ================= 3. BENTO GRID 2 KOLOM (148.dp, 22.dp) =================
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // KARTU KIRI: KATALOG PAKAIAN
                    BentoGridCard(
                        modifier = Modifier
                            .weight(1f)
                            .height(148.dp)
                            .clickable(onClick = onNavigateToCatalog)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(WarmCream.copy(alpha = 0.10f))
                                    .border(1.dp, WarmCream.copy(alpha = 0.20f), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Checkroom,
                                    contentDescription = null,
                                    tint = WarmCream,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = "Katalog",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = WarmCream
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "142 Varian • SKU",
                                    fontSize = 12.sp,
                                    color = WarmCream.copy(alpha = 0.65f),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // KARTU KANAN: RESTOCK (DENGAN BADGE KRITIS)
                    BentoGridCard(
                        modifier = Modifier
                            .weight(1f)
                            .height(148.dp)
                            .clickable(onClick = onNavigateToRestock)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(WarmCream.copy(alpha = 0.10f))
                                    .border(1.dp, WarmCream.copy(alpha = 0.20f), RoundedCornerShape(12.dp))
                                    .align(Alignment.TopStart),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Inventory2,
                                    contentDescription = null,
                                    tint = CrimsonRed,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            // Badge Bulat Merah Kritis di Pojok Kanan Atas
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(CrimsonRed)
                                    .align(Alignment.TopEnd),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "5",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = WarmCream
                                )
                            }

                            Column(modifier = Modifier.align(Alignment.BottomStart)) {
                                Text(
                                    text = "Restock",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = WarmCream
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "5 Stok menipis",
                                    fontSize = 12.sp,
                                    color = ErrorBrightRed,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                // ================= 4. WEARIQ AI INSIGHTS BANNER =================
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    CrimsonRed.copy(alpha = 0.18f),
                                    DarkNavyContainerHigh.copy(alpha = 0.65f),
                                    DarkNavyContainerHigh.copy(alpha = 0.85f)
                                ),
                                radius = 450f
                            )
                        )
                        .border(
                            width = 1.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    CrimsonRed.copy(alpha = 0.40f),
                                    WarmCream.copy(alpha = 0.10f)
                                )
                            ),
                            shape = RoundedCornerShape(22.dp)
                        )
                        .clickable(onClick = onNavigateToAiInsight)
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(DeepRedContainer.copy(alpha = 0.65f))
                                .border(1.dp, CrimsonRed.copy(alpha = 0.45f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AutoAwesome,
                                contentDescription = null,
                                tint = CrimsonRed,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "WEARIQ AI INSIGHT",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = CrimsonRed,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Prediksi tren & rekomendasi restock siap ditinjau",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = WarmCream,
                                lineHeight = 18.sp
                            )
                        }

                        Icon(
                            imageVector = Icons.Rounded.ChevronRight,
                            contentDescription = null,
                            tint = WarmCream.copy(alpha = 0.60f),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // ================= 5. TRANSAKSI TERAKHIR (FILLING LOWER SPACE) =================
                Text(
                    text = "Transaksi Terakhir",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = WarmCream,
                    letterSpacing = 0.3.sp
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(WarmCream.copy(alpha = 0.06f))
                        .border(
                            1.dp,
                            Brush.linearGradient(
                                colors = listOf(WarmCream.copy(alpha = 0.20f), Color.Transparent)
                            ),
                            RoundedCornerShape(20.dp)
                        )
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Item Transaksi 1
                        RecentTransactionItem(
                            title = "Kemeja Oxford + Chino Khaki",
                            pcsCount = "2 pcs",
                            time = "16:42 WIB",
                            amount = "Rp 358.000"
                        )

                        HorizontalDivider(
                            color = WarmCream.copy(alpha = 0.08f),
                            thickness = 1.dp
                        )

                        // Item Transaksi 2
                        RecentTransactionItem(
                            title = "Kaos Combed 24s Hitam",
                            pcsCount = "1 pcs",
                            time = "15:20 WIB",
                            amount = "Rp 89.000"
                        )
                    }
                }

                // Ruang ekstra bawah agar konten terakhir tidak tertutup Floating Bottom Bar
                Spacer(modifier = Modifier.height(130.dp))
            }
        }
    }
}

@Composable
private fun RecentTransactionItem(
    title: String,
    pcsCount: String,
    time: String,
    amount: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(WarmCream.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ReceiptLong,
                    contentDescription = null,
                    tint = WarmCream.copy(alpha = 0.75f),
                    modifier = Modifier.size(18.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = WarmCream
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "$pcsCount • $time",
                    fontSize = 11.sp,
                    color = WarmCream.copy(alpha = 0.50f)
                )
            }
        }

        Text(
            text = amount,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = WarmCream
        )
    }
}

/**
 * Kartu Standar Bento Grid Glassmorphism
 */
@Composable
private fun BentoGridCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .background(WarmCream.copy(alpha = 0.07f))
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        WarmCream.copy(alpha = 0.25f),
                        Color.Transparent
                    )
                ),
                shape = RoundedCornerShape(22.dp)
            )
    ) {
        content()
    }
}
