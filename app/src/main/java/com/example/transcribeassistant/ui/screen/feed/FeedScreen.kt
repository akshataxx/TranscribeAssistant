package com.example.transcribeassistant.ui.screen.feed

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.transcribeassistant.ui.screen.components.AnimatedBlobsBackground
import com.example.transcribeassistant.ui.screen.components.ScoopBlue
import com.example.transcribeassistant.ui.screen.components.ScoopCyan
import com.example.transcribeassistant.ui.screen.components.ScoopPurple
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

    AnimatedBlobsBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            Text(
                text = "Your Feed",
                style = MaterialTheme.typography.headlineMedium.copy(
                    brush = Brush.linearGradient(
                        colors = listOf(ScoopPurple, ScoopBlue, ScoopCyan)
                    )
                ),
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            transcriptList.forEach { transcript ->
                TranscriptCard(
                    transcript = transcript,
                    onClick = { onTranscriptClick(transcript.id) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (transcriptList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = ScoopPurple)
                }
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

    AnimatedBlobsBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            transcriptList.forEach { transcript ->
                TranscriptCard(
                    transcript = transcript,
                    onClick = { onTranscriptClick(transcript.id) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (transcriptList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = ScoopPurple)
                }
            }
        }
    }
}
