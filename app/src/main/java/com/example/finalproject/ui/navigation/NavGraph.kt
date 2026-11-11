package com.example.finalproject.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.finalproject.ui.screens.ai_insight.AiInsightScreen
import com.example.finalproject.ui.screens.auth.AuthScreen
import com.example.finalproject.ui.screens.catalog.CatalogScreen
import com.example.finalproject.ui.screens.dashboard.DashboardScreen
import com.example.finalproject.ui.screens.pos.PosScreen
import com.example.finalproject.ui.screens.restock.RestockScreen

@Composable
fun WearIqNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Auth.route,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
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
                onNavigateToCatalog = { navController.navigate(Screen.Catalog.route) },
                onNavigateToPos = { navController.navigate(Screen.Pos.route) },
                onNavigateToRestock = { navController.navigate(Screen.Restock.route) },
                onNavigateToAiInsight = { navController.navigate(Screen.AiInsight.route) }
            )
        }

        composable(Screen.Catalog.route) {
            CatalogScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Pos.route) {
            PosScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Restock.route) {
            RestockScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.AiInsight.route) {
            AiInsightScreen(onBack = { navController.popBackStack() })
        }
    }
}
