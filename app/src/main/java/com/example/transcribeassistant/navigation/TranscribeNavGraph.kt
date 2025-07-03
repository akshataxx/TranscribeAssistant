package com.example.transcribeassistant.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.compose.ui.Modifier
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.transcribeassistant.ui.screen.components.BottomNavBar
import com.example.transcribeassistant.ui.screen.TranscribeDetailsScreen
import com.example.transcribeassistant.ui.screen.dashboard.DashboardScreen
import com.example.transcribeassistant.ui.screen.feed.FeedScreen

@Composable
fun TranscribeNavGraph(
    navController: NavHostController = rememberNavController()
) {
    // Deep link URI for transcribe details used later
    val uri = "transcribeassistant://transcript/"

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        containerColor = Color(0xFF2C2B3E),
        bottomBar = {
            BottomNavBar(currentRoute = currentRoute ?: "") {
                if (it != currentRoute) {
                    navController.navigate(it) {
                        popUpTo(Screen.Feed.route) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            }
        }
    ){ paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Feed.route) {
                FeedScreen(
                    viewModel = hiltViewModel(),
                    onTranscriptClick = { transcriptId ->
                        navController.navigate(Screen.TranscribeDetails.createRoute(transcriptId))
                    }
                )
            }
            composable(Screen.Dashboard.route) {
                DashboardScreen(viewModel = hiltViewModel())
            }
            composable(Screen.Notifications.route) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Notifications Screen")
                }
            }
            composable(Screen.Profile.route) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Profile Screen")
                }
            }
            composable("add") {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Add Screen")
                }
            }
            composable(
                route = Screen.TranscribeDetails.route,
                arguments = listOf(navArgument("transcriptId") { type = NavType.StringType })
            ) { backStackEntry ->
                val transcriptId =
                    backStackEntry.arguments?.getString("transcriptId") ?: return@composable
                TranscribeDetailsScreen(transcriptId = transcriptId)
            }
        }
    }
}