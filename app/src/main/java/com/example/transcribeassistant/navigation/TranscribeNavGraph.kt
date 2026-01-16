package com.example.transcribeassistant.navigation

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
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
import com.example.transcribeassistant.data.auth.AuthStateManager
import com.example.transcribeassistant.di.JwtManagerEntryPoint
import com.example.transcribeassistant.ui.screen.components.BottomNavBar
import com.example.transcribeassistant.ui.screen.transcription.TranscribeDetailsScreen
import com.example.transcribeassistant.ui.screen.dashboard.DashboardScreen
import com.example.transcribeassistant.ui.screen.feed.FeedScreen
import com.example.transcribeassistant.ui.screen.login.LoginScreen
import com.example.transcribeassistant.ui.screen.subscription.SubscriptionScreen
import com.example.transcribeassistant.ui.viewmodel.LoginViewModel
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.runBlocking

// Colors matching the new Scoop light theme
private val LightBackground = Color(0xFFF5F7FA)
private val LightBackgroundEnd = Color(0xFFE8ECF1)
private val PrimaryText = Color(0xFF1F2937)

@Composable
fun TranscribeNavGraph(
    navController: NavHostController = rememberNavController(),
    authViewModel: com.example.transcribeassistant.ui.viewmodel.AuthViewModel
) {
    // Deep link URI for transcribe details used later
    val uri = "transcribeassistant://transcript/"

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            // Only show bottom bar when not on login screen
            if (currentRoute != "login") {
                BottomNavBar(currentRoute = currentRoute ?: "") {
                    if (it != currentRoute) {
                        navController.navigate(it) {
                            popUpTo(Screen.Feed.route) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                }
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        LightBackground,
                        LightBackgroundEnd
                    )
                )
            )
    ){ paddingValues ->
        val context = LocalContext.current
        val jwtManager = EntryPointAccessors.fromApplication(
            context.applicationContext as Application,
            JwtManagerEntryPoint::class.java
        ).jwtManager()

        val scope = rememberCoroutineScope()

        // Listen for authentication expiration
        LaunchedEffect(authViewModel) {
            authViewModel.authenticationExpired.collect {
                navController.navigate("login") {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }

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
                    Text("Notifications Screen", color = PrimaryText)
                }
            }
            composable(Screen.Subscription.route) {
                SubscriptionScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable("add") {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Add Screen", color = PrimaryText)
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