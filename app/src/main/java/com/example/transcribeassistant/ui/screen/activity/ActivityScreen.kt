package com.example.transcribeassistant.ui.screen.activity

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.transcribeassistant.domain.model.ActivityStatus
import com.example.transcribeassistant.ui.screen.components.AnimatedBlobsBackground
import com.example.transcribeassistant.ui.screen.components.NewContentPill
import com.example.transcribeassistant.ui.screen.components.ScoopBlue
import com.example.transcribeassistant.ui.screen.components.ScoopCyan
import com.example.transcribeassistant.ui.screen.components.ScoopPurple
import com.example.transcribeassistant.ui.screen.components.SecondaryText
import com.example.transcribeassistant.ui.viewmodel.ActivityViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityScreen(
    onTranscriptClick: (String) -> Unit,
    viewModel: ActivityViewModel = hiltViewModel()
) {
    val items by viewModel.items.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val newJobIds by viewModel.newJobIds.collectAsState()

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.fetchJobs()
        viewModel.startPolling()
        viewModel.markAsViewed()
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.stopPolling() }
    }

    AnimatedBlobsBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header — gradient matching Feed / Add
            Text(
                text = "Activity",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineLarge.copy(
                    brush = Brush.linearGradient(
                        colors = listOf(ScoopPurple, ScoopBlue, ScoopCyan)
                    )
                ),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp, bottom = 16.dp)
            )

            when {
                isLoading && items.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = ScoopPurple, modifier = Modifier.size(36.dp))
                    }
                }

                error != null && items.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Oops!",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = error ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                color = SecondaryText
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.fetchJobs() },
                                colors = ButtonDefaults.buttonColors(containerColor = ScoopPurple)
                            ) {
                                Text("Try Again")
                            }
                        }
                    }
                }

                items.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        ActivityEmptyState()
                    }
                }

                else -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        PullToRefreshBox(
                            isRefreshing = isLoading,
                            onRefresh = { viewModel.fetchJobs() }
                        ) {
                            LazyColumn(
                                state = listState,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                // Spacer so the pill doesn't obscure the first row
                                if (newJobIds.isNotEmpty()) {
                                    item { Spacer(modifier = Modifier.height(44.dp)) }
                                }

                                items(items, key = { it.id }) { item ->
                                    val transcriptId = item.userTranscriptId
                                    val isClickable = item.status == ActivityStatus.COMPLETED &&
                                            transcriptId != null

                                    ActivityItemRow(
                                        item = item,
                                        isNew = viewModel.isNew(item),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .then(
                                                if (isClickable) {
                                                    Modifier.clickable { onTranscriptClick(transcriptId!!) }
                                                } else {
                                                    Modifier
                                                }
                                            )
                                    )

                                    Divider(
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                }

                                item { Spacer(modifier = Modifier.height(20.dp)) }
                            }
                        }

                        // Floating NewContentPill
                        AnimatedVisibility(
                            visible = newJobIds.isNotEmpty(),
                            enter = slideInVertically(initialOffsetY = { -it }),
                            exit = slideOutVertically(targetOffsetY = { -it }),
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 8.dp)
                        ) {
                            NewContentPill(count = newJobIds.size) {
                                viewModel.markAsViewed()
                                viewModel.fetchJobs()
                                scope.launch { listState.animateScrollToItem(0) }
                            }
                        }
                    }
                }
            }
        }
    }
}
