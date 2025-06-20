package com.example.transcribeassistant.ui.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.transcribeassistant.ui.screen.feed.FeedScreen

@Composable
fun TranscribeAssistantApp() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        // For now, we directly show the detail screen with a test ID
//        TranscribeDetailsScreen(transcriptId = "test-id")
        FeedScreen()
    }
}