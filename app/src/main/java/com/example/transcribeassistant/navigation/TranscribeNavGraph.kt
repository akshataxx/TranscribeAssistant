package com.example.transcribeassistant.navigation

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.example.transcribeassistant.common.AppEventBus
import com.example.transcribeassistant.common.PendingDeepLinkManager
import com.example.transcribeassistant.di.JwtManagerEntryPoint
import com.example.transcribeassistant.ui.screen.activity.ActivityScreen
import com.example.transcribeassistant.ui.screen.components.AnimatedBlobsBackground
import com.example.transcribeassistant.ui.screen.components.BottomNavBar
import com.example.transcribeassistant.ui.screen.add.AddLinkScreen
import com.example.transcribeassistant.ui.screen.transcription.TranscribeDetailsScreen
import com.example.transcribeassistant.ui.screen.dashboard.DashboardScreen
import com.example.transcribeassistant.ui.screen.feed.FeedScreen
import com.example.transcribeassistant.ui.screen.login.LoginScreen
import com.example.transcribeassistant.ui.screen.profile.ProfileScreen
import com.example.transcribeassistant.ui.screen.subscription.SubscriptionScreen
import com.example.transcribeassistant.ui.viewmodel.LoginViewModel
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.runBlocking
import androidx.compose.ui.unit.dp

// Colors matching the new Scoop light theme
private val LightBackground = Color(0xFFF5F7FA)
private val LightBackgroundEnd = Color(0xFFE8ECF1)
private val PrimaryText = Color(0xFF1F2937)

@Composable
fun TranscribeNavGraph(
    navController: NavHostController = rememberNavController(),
    authViewModel: com.example.transcribeassistant.ui.viewmodel.AuthViewModel
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Observe badge count from AppEventBus
    val activityBadgeCount by AppEventBus.newCompletionCount.collectAsState()

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            if (currentRoute != "login") {
                BottomNavBar(
                    currentRoute = currentRoute ?: "",
                    activityBadgeCount = activityBadgeCount
                ) {
                    if (it != currentRoute) {
                        navController.navigate(it) {
                            popUpTo(Screen.Dashboard.route) { inclusive = false }
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
                    colors = listOf(LightBackground, LightBackgroundEnd)
                )
            )
    ) { paddingValues ->
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

        // Handle deep links from notification taps
        LaunchedEffect(Unit) {
            PendingDeepLinkManager.pendingDeepLink.collect { deepLink ->
                deepLink ?: return@collect
                // Only navigate if the user is past the login screen
                if (currentRoute == "login") return@collect

                when (deepLink) {
                    is PendingDeepLinkManager.DeepLink.Transcript -> {
                        navController.navigate(Screen.Feed.route) {
                            popUpTo(Screen.Dashboard.route) { inclusive = false }
                            launchSingleTop = true
                        }
                        navController.navigate(Screen.TranscribeDetails.createRoute(deepLink.transcriptId))
                    }
                    is PendingDeepLinkManager.DeepLink.ActivityTab -> {
                        navController.navigate(Screen.Activity.route) {
                            popUpTo(Screen.Dashboard.route) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                }
                PendingDeepLinkManager.clear()
            }
        }

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
                    onTranscriptClick = { transcriptId ->
                        navController.navigate(Screen.TranscribeDetails.createRoute(transcriptId))
                    }
                )
            }

            composable(Screen.Dashboard.route) {
                DashboardScreen(navController = navController, viewModel = hiltViewModel())
            }

            composable(Screen.Activity.route) {
                ActivityScreen(
                    onTranscriptClick = { transcriptId ->
                        navController.navigate(Screen.TranscribeDetails.createRoute(transcriptId))
                    }
                )
            }

            composable(Screen.Subscription.route) {
                SubscriptionScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.AddLink.route) {
                AddLinkScreen(
                    onViewActivity = {
                        navController.navigate(Screen.Activity.route) {
                            popUpTo(Screen.Dashboard.route) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    onSettingsClick = {
                        navController.navigate(Screen.Settings.route)
                    },
                    onSubscriptionClick = {
                        navController.navigate(Screen.Subscription.route)
                    },
                    onLogoutClick = {
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(Screen.Settings.route) {
                UnderConstructionScreen(
                    title = "Settings",
                    description = "Settings will be available here soon."
                )
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
                    onTranscriptClick = { transcriptId ->
                        navController.navigate(Screen.TranscribeDetails.createRoute(transcriptId))
                    },
                    categoryId = categoryId
                )
            }
        }
    }
}

@Composable
private fun UnderConstructionScreen(
    title: String,
    description: String
) {
    AnimatedBlobsBackground {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Build,
                    contentDescription = null,
                    tint = PrimaryText
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "$title is under construction",
                    style = MaterialTheme.typography.titleLarge.copy(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF7C3AED),
                                Color(0xFF2563EB),
                                Color(0xFF06B6D4)
                            )
                        )
                    ),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = description,
                    color = PrimaryText.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
