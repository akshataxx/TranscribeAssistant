package com.example.transcribeassistant.ui.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.transcribeassistant.ui.screen.dashboard.DashboardScreen
import androidx.navigation.compose.rememberNavController
import com.example.transcribeassistant.navigation.TranscribeNavGraph
import com.example.transcribeassistant.ui.screen.feed.FeedScreen

@Composable
fun TranscribeAssistantApp() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        DashboardScreen()
        val navController = rememberNavController()
        TranscribeNavGraph(navController = navController)

    }
}
