package com.example.transcribeassistant.ui.screen.feed

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.transcribeassistant.ui.screen.components.AnimatedBlobsBackground
import com.example.transcribeassistant.ui.screen.components.SecondaryText
import com.example.transcribeassistant.ui.screen.components.ScoopBlue
import com.example.transcribeassistant.ui.screen.components.ScoopCyan
import com.example.transcribeassistant.ui.screen.components.ScoopPurple
import com.example.transcribeassistant.ui.viewmodel.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
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
    val isInitialLoading by viewModel.isInitialLoading.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val scrollState = rememberScrollState()

    // Fetch data on screen load
    LaunchedEffect(categoryId) {
        if (categoryId != null) {
            viewModel.fetchTranscriptsByCategory(categoryId)
        } else {
            viewModel.fetchTranscripts()
        }
    }

    // Lifecycle-aware polling: start on resume, stop on pause
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    viewModel.startPolling()
                    if (categoryId != null) {
                        viewModel.fetchTranscriptsByCategory(categoryId)
                    } else {
                        viewModel.fetchTranscripts()
                    }
                }
                Lifecycle.Event.ON_PAUSE -> viewModel.stopPolling()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            viewModel.stopPolling()
        }
    }

    AnimatedBlobsBackground {
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier.fillMaxSize()
        ) {
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

                when {
                    isInitialLoading && transcriptList.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = ScoopPurple)
                        }
                    }
                    transcriptList.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "No transcripts yet",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = SecondaryText
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Add a link to get started",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = SecondaryText.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                    else -> {
                        transcriptList.forEach { transcript ->
                            TranscriptCard(
                                transcript = transcript,
                                onClick = { onTranscriptClick(transcript.id) }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranscriptsScreen(
    categoryId: String,
    viewModel: DashboardViewModel = hiltViewModel(),
    onTranscriptClick: (String) -> Unit
) {
    val transcriptList by viewModel.transcriptsByCategory.collectAsState()
    val isInitialLoading by viewModel.isInitialLoading.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val scrollState = rememberScrollState()

    // Fetch data on screen load
    LaunchedEffect(categoryId) {
        viewModel.fetchTranscriptsByCategory(categoryId)
    }

    // Lifecycle-aware polling
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    viewModel.startPolling()
                    viewModel.fetchTranscriptsByCategory(categoryId)
                }
                Lifecycle.Event.ON_PAUSE -> viewModel.stopPolling()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            viewModel.stopPolling()
        }
    }

    AnimatedBlobsBackground {
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(scrollState)
            ) {
                when {
                    isInitialLoading && transcriptList.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = ScoopPurple)
                        }
                    }
                    transcriptList.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No transcripts in this category",
                                style = MaterialTheme.typography.titleMedium,
                                color = SecondaryText
                            )
                        }
                    }
                    else -> {
                        transcriptList.forEach { transcript ->
                            TranscriptCard(
                                transcript = transcript,
                                onClick = { onTranscriptClick(transcript.id) }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    }
}
