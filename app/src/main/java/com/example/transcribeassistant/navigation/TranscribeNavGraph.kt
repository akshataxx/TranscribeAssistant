package com.example.transcribeassistant.navigation

import android.app.Application
import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.transcribeassistant.di.JwtManagerEntryPoint
import com.example.transcribeassistant.ui.screen.components.BottomNavBar
import com.example.transcribeassistant.ui.screen.TranscribeDetailsScreen
import com.example.transcribeassistant.ui.screen.dashboard.DashboardScreen
import com.example.transcribeassistant.ui.screen.feed.FeedScreen
import com.example.transcribeassistant.ui.screen.login.LoginScreen
import com.example.transcribeassistant.ui.screen.profile.ProfileScreen
import com.example.transcribeassistant.ui.screen.profile.SettingsScreen
import com.example.transcribeassistant.ui.viewmodel.LoginViewModel
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

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
        val context = LocalContext.current
        val jwtManager = EntryPointAccessors.fromApplication(
            context.applicationContext as Application,
            JwtManagerEntryPoint::class.java
        ).jwtManager()

        val scope = rememberCoroutineScope()

        // decide start destination based on logged in user or not
        val accessToken = runBlocking { jwtManager.getAccessToken() }
        val startDest = if (accessToken.isNullOrBlank()) "login" else Screen.Dashboard.route

        NavHost(
            navController = navController,
            startDestination = startDest,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("login") {
                val loginViewModel: LoginViewModel = hiltViewModel()
                LoginScreen(
                    navController = navController,
                    viewModel = loginViewModel,
                    webClientId = "63948187194-8rir4fa743qu7ri2dhsou5b9ec489p5n.apps.googleusercontent.com"
                )
            }

            composable(Screen.Feed.route) {
                FeedScreen(
                    viewModel = hiltViewModel(),
                    onTranscriptClick = { transcriptId ->
                        navController.navigate(Screen.TranscribeDetails.createRoute(transcriptId))
                    }
                )
            }
            composable(Screen.Dashboard.route) {
                DashboardScreen(navController = navController, viewModel = hiltViewModel())
            }
            composable(Screen.Notifications.route) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Notifications Screen")
                }
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onSettingsClick = {
                        navController.navigate(Screen.Settings.route) {
                            launchSingleTop = true
                        }
                    },
                    onLogoutClick = {
                        scope.launch {
                            // Clear tokens, then go to login and wipe history
                            jwtManager.clearTokens()
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen()
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
                TranscribeDetailsScreen(
                    transcriptId = transcriptId,
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(
                route = "transcripts/{categoryId}",
                arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
            ) { backStackEntry ->
                val categoryId = backStackEntry.arguments?.getString("categoryId")
                FeedScreen(
                    viewModel = hiltViewModel(),
                    onTranscriptClick = { transcriptId ->
                        navController.navigate(Screen.TranscribeDetails.createRoute(transcriptId))
                    },
                    categoryId = categoryId
                )
            }
        }
    }
}