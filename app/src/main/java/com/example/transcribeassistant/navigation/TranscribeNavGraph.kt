package com.example.transcribeassistant.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.transcribeassistant.ui.screen.TranscribeDetailsScreen
import com.example.transcribeassistant.ui.screen.feed.FeedScreen

@Composable
fun TranscribeNavGraph(
    navController: NavHostController = rememberNavController()
) {
    // Deep link URI for transcribe details used later
    val uri = "transcribeassistant://transcript/"

    NavHost(
        navController = navController,
        startDestination = Screen.Feed.route
    ) {
        composable(
            route = Screen.Feed.route) {
            FeedScreen(
                viewModel = hiltViewModel(),
                onTranscriptClick = { transcriptId ->
                    navController.navigate(Screen.TranscribeDetails.createRoute(transcriptId))
                }
            )
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