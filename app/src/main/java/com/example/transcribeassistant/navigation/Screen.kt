package com.example.transcribeassistant.navigation

sealed class Screen(val route: String) {
    object Feed : Screen("feed")
    object Dashboard : Screen("dashboard")
    object Notifications: Screen("notifications")
    object Profile: Screen("profile")
    object TranscribeDetails : Screen("transcript/{transcriptId}") {
        fun createRoute(transcriptId: String) = "transcript/$transcriptId"
    }
    object Transcripts : Screen("transcripts/{categoryId}") {
        fun createRoute(categoryId: String) = "transcripts/$categoryId"
    }
}