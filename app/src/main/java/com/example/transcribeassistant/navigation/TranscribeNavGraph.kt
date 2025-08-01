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
import dagger.hilt.android.EntryPointAccessors
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
        val jwtManager = EntryPointAccessors.fromContext(
            context,
            JwtManagerEntryPoint::class.java
        ).jwtManager()

// decide start destination
        val accessToken = runBlocking { jwtManager.getAccessToken() }
        val startDest = if (accessToken.isNullOrBlank()) "login" else Screen.Dashboard.route

        NavHost(
            navController = navController,
            startDestination = startDest,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("login") {
                // You need to supply invokeGoogleSignIn appropriately; stub or real implementation
                val loginViewModel: com.example.transcribeassistant.ui.viewmodel.LoginViewModel = hiltViewModel()
                LoginScreen(
                    navController = navController,
                    viewModel = loginViewModel,
                    invokeGoogleSignIn = {
                        // TODO: replace with real Google sign-in logic that returns the credential/token
                        "eyJhbGciOiJSUzI1NiIsImtpZCI6ImRkNTMwMTIwNGZjMWQ2YTBkNjhjNzgzYTM1Y2M5YzEwYjI1ZTFmNGEiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhenAiOiI2Mzk0ODE4NzE5NC04cmlyNGZhNzQzcXU3cmkyZGhzb3U1YjllYzQ4OXA1bi5hcHBzLmdvb2dsZXVzZXJjb250ZW50LmNvbSIsImF1ZCI6IjYzOTQ4MTg3MTk0LThyaXI0ZmE3NDNxdTdyaTJkaHNvdTViOWVjNDg5cDVuLmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwic3ViIjoiMTAxODMxMDM3MDM4MjA1NzQzMDQ1IiwiZW1haWwiOiJidXR0ZXIuY2hpY2tlbjMwMTdAZ21haWwuY29tIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsImF0X2hhc2giOiJIWTRjd3JhaV9leUpuNlZ4ZTBwZ1BnIiwiaWF0IjoxNzUzOTQ4OTY4LCJleHAiOjE3NTM5NTI1Njh9.aLQsKQGonTc956TqYVuKiq7XM5F6gE07H6Q3DNXWweWNJSUBbYVydkNW3PZwY33fh7eHoKFYr3rR4t4tQnhaZ_cmknqeJHWHT_RN97uZ-NuTPXuMYEU0J9L0YaQBYJ3At1V7m0oDsg24LlT7N5RkNhQO9uo5PfHU6dlt7diS6mcQ2n3ffu12w8A9UU0Avvw0UBJ9KPPNs2_Q7C813CDEpjiB4RwWE4NfneAzRumKTq3e8H6tP0hFjzd8B0J7DPZOkhcr14gBLVNYdNQttwMc7gcGfDJa5hOm5ND9cZf7PvRajvvIApueVGHUCjjnd-UtppteW1eEcPzWwuexCzSBuw"
                    }
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