package com.example.transcribeassistant.ui.screen.feed

import androidx.compose.foundation.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.transcribeassistant.ui.viewmodel.FeedViewModel
import com.example.transcribeassistant.ui.viewmodel.DashboardViewModel

@Composable
fun FeedScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onTranscriptClick: (String) -> Unit,
    categoryId: String? = null
) {
    val transcriptList by if (categoryId != null) {
        viewModel.transcriptsByCategory.collectAsState()
    } else {
        viewModel.transcripts.collectAsState()
    }
    val scrollState = rememberScrollState()

    // Fetch data once on screen load
    LaunchedEffect(categoryId) {
        if (categoryId != null) {
            viewModel.fetchTranscriptsByCategory(categoryId)
        } else {
            viewModel.fetchTranscripts()
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
