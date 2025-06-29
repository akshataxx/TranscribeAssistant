package com.example.transcribeassistant.navigation

sealed class Screen(val route: String) {
    object Feed : Screen("feed")
    object Dashboard : Screen("dashboard")
    object TranscribeDetails : Screen("transcript/{transcriptId}") {
        fun createRoute(transcriptId: String) = "transcript/$transcriptId"
    }
}