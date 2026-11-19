package com.example.finalproject.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.PointOfSale
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.finalproject.data.remote.ApiClient
import com.example.finalproject.ui.screens.ai_insight.AiInsightScreen
import com.example.finalproject.ui.screens.auth.AuthScreen
import com.example.finalproject.ui.screens.catalog.CatalogScreen
import com.example.finalproject.ui.screens.dashboard.DashboardScreen
import com.example.finalproject.ui.screens.pos.PosScreen
import com.example.finalproject.ui.screens.restock.RestockScreen
import com.example.finalproject.ui.theme.CrimsonRed
import com.example.finalproject.ui.theme.DarkNavyBackground
import com.example.finalproject.ui.theme.WarmCream
import kotlinx.coroutines.launch

/**
 * NavGraph WearIQ POS:
 * 1. Root berlatar solid Dark Navy (#0B132B) untuk mencegah ghosting/overlap transisi.
 * 2. Durasi animasi transisi navigasi 200ms dengan fadeIn / fadeOut bersih.
 * 3. Floating Glass Bottom Navigation Bar dibuat persisten untuk 4 rute utama:
 *    - Dashboard (Beranda)
 *    - Pos (Kasir)
 *    - Catalog (Katalog)
 *    - AiInsight (AI Insights)
 */
@Composable
fun WearIqNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Auth.route,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val tokenManager = ApiClient.getTokenManager(context)

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Tampilkan Floating Bottom Bar hanya pada 4 rute utama
    val isTopLevelRoute = currentRoute in listOf(
        Screen.Dashboard.route,
        Screen.Pos.route,
        Screen.Catalog.route,
        Screen.AiInsight.route
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkNavyBackground)
    ) {
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.fillMaxSize(),
            enterTransition = { fadeIn(animationSpec = tween(200)) },
            exitTransition = { fadeOut(animationSpec = tween(200)) },
            popEnterTransition = { fadeIn(animationSpec = tween(200)) },
            popExitTransition = { fadeOut(animationSpec = tween(200)) }
        ) {
            composable(Screen.Auth.route) {
                AuthScreen(
                    onLoginSuccess = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Auth.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    onNavigateToCatalog = {
                        navController.navigate(Screen.Catalog.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToPos = {
                        navController.navigate(Screen.Pos.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToRestock = { navController.navigate(Screen.Restock.route) },
                    onNavigateToAiInsight = {
                        navController.navigate(Screen.AiInsight.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onLogout = {
                        coroutineScope.launch {
                            tokenManager.clearSession()
                            navController.navigate(Screen.Auth.route) {
                                popUpTo(Screen.Dashboard.route) { inclusive = true }
                            }
                        }
                    }
                )
            }

            composable(Screen.Pos.route) {
                PosScreen()
            }

            composable(Screen.Catalog.route) {
                CatalogScreen()
            }

            composable(Screen.Restock.route) {
                RestockScreen(onBack = { navController.popBackStack() })
            }

            composable(Screen.AiInsight.route) {
                AiInsightScreen()
            }
        }

        // Floating Bottom Bar Persisten di level Root NavGraph
        if (isTopLevelRoute) {
            val selectedIndex = when (currentRoute) {
                Screen.Pos.route -> 1
                Screen.Catalog.route -> 2
                Screen.AiInsight.route -> 3
                else -> 0 // Dashboard
            }

            FloatingGlassBottomNavBar(
                selectedIndex = selectedIndex,
                onItemSelected = { index ->
                    val targetRoute = when (index) {
                        1 -> Screen.Pos.route
                        2 -> Screen.Catalog.route
                        3 -> Screen.AiInsight.route
                        else -> Screen.Dashboard.route
                    }

                    if (currentRoute != targetRoute) {
                        navController.navigate(targetRoute) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            )
        }
    }
}

/**
 * Floating Glass Bottom Navigation Bar berbentuk Pill (32.dp)
 */
@Composable
private fun FloatingGlassBottomNavBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val navItems = listOf(
        NavItem("Beranda", Icons.Rounded.Home),
        NavItem("Kasir", Icons.Rounded.PointOfSale),
        NavItem("Katalog", Icons.Rounded.ShoppingBag),
        NavItem("AI Insights", Icons.Rounded.AutoAwesome)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(Color(0xD9111B36)) // Glass Navy #D9111B36
            .border(1.dp, WarmCream.copy(alpha = 0.18f), RoundedCornerShape(32.dp))
            .padding(horizontal = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            navItems.forEachIndexed { index, item ->
                val isSelected = selectedIndex == index
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) CrimsonRed else Color.Transparent)
                        .clickable { onItemSelected(index) }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = if (isSelected) WarmCream else WarmCream.copy(alpha = 0.55f),
                            modifier = Modifier.size(20.dp)
                        )
                        if (isSelected) {
                            Text(
                                text = item.label,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = WarmCream
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class NavItem(
    val label: String,
    val icon: ImageVector
)
