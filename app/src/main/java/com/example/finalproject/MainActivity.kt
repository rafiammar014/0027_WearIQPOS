package com.example.finalproject

import android.graphics.Color as AndroidColor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.finalproject.ui.navigation.WearIqNavGraph
import com.example.finalproject.ui.theme.DarkNavyBackground
import com.example.finalproject.ui.theme.FinalProjectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT)
        )
        setContent {
            FinalProjectTheme {
                val navController = rememberNavController()
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(DarkNavyBackground)
                ) {
                    WearIqNavGraph(
                        navController = navController,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}
