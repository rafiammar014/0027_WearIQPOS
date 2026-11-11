package com.example.finalproject.ui.screens.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DashboardScreen(
    onNavigateToCatalog: () -> Unit,
    onNavigateToPos: () -> Unit,
    onNavigateToRestock: () -> Unit,
    onNavigateToAiInsight: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "WearIQ POS Dashboard",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onNavigateToPos, modifier = Modifier.fillMaxWidth()) {
            Text("Kasir (POS)")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onNavigateToCatalog, modifier = Modifier.fillMaxWidth()) {
            Text("Katalog Pakaian")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onNavigateToRestock, modifier = Modifier.fillMaxWidth()) {
            Text("Manajemen Restock")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onNavigateToAiInsight, modifier = Modifier.fillMaxWidth()) {
            Text("AI Insight")
        }
    }
}
