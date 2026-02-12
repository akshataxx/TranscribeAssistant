package com.example.transcribeassistant.navigation

sealed class Screen(val route: String) {
    object Feed : Screen("feed")
    object Dashboard : Screen("dashboard")
    object Notifications: Screen("notifications")
    object Subscription : Screen("subscription")
    object AddLink : Screen("add")
    object Profile : Screen("profile")
    object Settings : Screen("settings")
    object TranscribeDetails : Screen("transcript/{transcriptId}") {
        fun createRoute(transcriptId: String) = "transcript/$transcriptId"
    }
    object Transcripts : Screen("transcripts/{categoryId}") {
        fun createRoute(categoryId: String) = "transcripts/$categoryId"
    }
}