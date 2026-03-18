package com.example.transcribeassistant.navigation

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
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
import com.example.transcribeassistant.ui.screen.components.BottomNavBar
import com.example.transcribeassistant.ui.screen.add.AddLinkScreen
import com.example.transcribeassistant.ui.screen.transcription.TranscribeDetailsScreen
import com.example.transcribeassistant.ui.screen.dashboard.DashboardScreen
import com.example.transcribeassistant.ui.screen.feed.FeedScreen
import com.example.transcribeassistant.ui.screen.login.LoginScreen
import com.example.transcribeassistant.ui.viewmodel.ActivityViewModel
import com.example.transcribeassistant.ui.viewmodel.LoginViewModel
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.runBlocking

private val LightBackground = Color(0xFFF5F7FA)
private val LightBackgroundEnd = Color(0xFFE8ECF1)

@Composable
fun TranscribeNavGraph(
    navController: NavHostController = rememberNavController(),
    authViewModel: com.example.transcribeassistant.ui.viewmodel.AuthViewModel
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Scoped to the nav graph (Activity-level) so state persists across tab switches
    val activityViewModel: ActivityViewModel = hiltViewModel()

    // Observe badge count from AppEventBus
    val activityBadgeCount by AppEventBus.newCompletionCount.collectAsState()

    // Foreground refresh: if app was backgrounded for 5+ minutes, trigger a full refresh
    val lifecycleOwner = LocalLifecycleOwner.current
    var lastBackgroundedAt by remember { mutableStateOf<Long?>(null) }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> lastBackgroundedAt = System.currentTimeMillis()
                Lifecycle.Event.ON_START -> {
                    val lastBg = lastBackgroundedAt
                    if (lastBg != null && System.currentTimeMillis() - lastBg > 5 * 60 * 1000L) {
                        AppEventBus.emitRefresh()
                    }
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

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
                    },
                    viewModel = activityViewModel
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
                    categoryId = categoryId,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
