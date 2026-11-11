package com.example.finalproject.ui.navigation

/**
 * Definisi route layar untuk Navigation Compose pada WearIQ POS.
 */
sealed class Screen(val route: String) {
    data object Auth : Screen("auth")
    data object Dashboard : Screen("dashboard")
    data object Catalog : Screen("catalog")
    data object Pos : Screen("pos")
    data object Restock : Screen("restock")
    data object AiInsight : Screen("ai_insight")
}
