package com.example.transcribeassistant.ui.screen.feed

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.transcribeassistant.ui.viewmodel.DashboardViewModel
import com.example.transcribeassistant.ui.viewmodel.RefreshManagerViewModel

@Composable
fun FeedScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onTranscriptClick: (String) -> Unit,
    categoryId: String? = null,
    refreshManagerViewModel: RefreshManagerViewModel = hiltViewModel()
) {
    val transcriptList by if (categoryId != null) {
        viewModel.transcriptsByCategory.collectAsState()
    } else {
        viewModel.transcripts.collectAsState()
    }
    val scrollState = rememberScrollState()

    // Initial data fetch
    LaunchedEffect(categoryId) {
        if (categoryId != null) {
            viewModel.fetchTranscriptsByCategory(categoryId)
        } else {
            viewModel.fetchTranscripts()
        }
    }

    // Listen for app foreground refresh events
    LaunchedEffect(categoryId) {
        refreshManagerViewModel.appRefreshManager.refreshTrigger.collect { event ->
            Log.d("FeedScreen", "Received refresh event: $event")
            if (categoryId != null) {
                viewModel.fetchTranscriptsByCategory(categoryId)
            } else {
                viewModel.fetchTranscripts()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Text(if (categoryId != null) "Transcripts" else "Feed", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(modifier = Modifier.height(12.dp))

        transcriptList.forEach { transcript ->
            TranscriptCard(
                transcript = transcript,
                onClick = { onTranscriptClick(transcript.id) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (transcriptList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun TranscriptsScreen(
    categoryId: String,
    viewModel: DashboardViewModel = hiltViewModel(),
    onTranscriptClick: (String) -> Unit
) {
    val transcriptList by viewModel.transcriptsByCategory.collectAsState()
    val scrollState = rememberScrollState()

    // Fetch data once on screen load
    LaunchedEffect(categoryId) {
        viewModel.fetchTranscriptsByCategory(categoryId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Text("Transcripts", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(modifier = Modifier.height(12.dp))

        transcriptList.forEach { transcript ->
            TranscriptCard(
                transcript = transcript,
                onClick = { onTranscriptClick(transcript.id) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (transcriptList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}
