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

@Composable
fun FeedScreen(
    viewModel: FeedViewModel = hiltViewModel(),
    onTranscriptClick: (String) -> Unit
) {
    val transcriptList by viewModel.transcripts.collectAsState()
    val scrollState = rememberScrollState()

    // Fetch data once on screen load
    LaunchedEffect(Unit) {
        viewModel.fetchTranscripts()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Text("Feed", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(modifier = Modifier.height(12.dp))

        transcriptList.forEach { transcript ->
            TranscriptCard(
                transcript = transcript,
                onClick = {onTranscriptClick(transcript.id)})
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (transcriptList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}
