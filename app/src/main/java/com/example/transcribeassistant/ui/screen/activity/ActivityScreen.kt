package com.example.transcribeassistant.ui.screen.activity

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
                    val activeItems = items.filter {
                        it.status == ActivityStatus.PENDING || it.status == ActivityStatus.PROCESSING
                    }
                    val recentItems = items.filter {
                        it.status == ActivityStatus.COMPLETED || it.status == ActivityStatus.FAILED
                    }
                    var isRecentExpanded by remember { mutableStateOf(true) }

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

                                // Active section (pending/processing)
                                if (activeItems.isNotEmpty()) {
                                    item {
                                        Text(
                                            text = "Active",
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.SemiBold,
                                            color = SecondaryText,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp, vertical = 10.dp)
                                        )
                                    }
                                    items(activeItems, key = { it.id }) { item ->
                                        ActivityItemRow(
                                            item = item,
                                            isNew = viewModel.isNew(item),
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        Divider(
                                            modifier = Modifier.padding(horizontal = 16.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant
                                        )
                                    }
                                }

                                // Recent section header — collapsible
                                if (recentItems.isNotEmpty()) {
                                    item {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { isRecentExpanded = !isRecentExpanded }
                                                .padding(horizontal = 16.dp, vertical = 10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Recent",
                                                style = MaterialTheme.typography.labelLarge,
                                                fontWeight = FontWeight.SemiBold,
                                                color = SecondaryText,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Icon(
                                                imageVector = if (isRecentExpanded)
                                                    Icons.Default.KeyboardArrowUp
                                                else
                                                    Icons.Default.KeyboardArrowDown,
                                                contentDescription = null,
                                                tint = SecondaryText,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }

                                    if (isRecentExpanded) {
                                        items(recentItems, key = { it.id }) { item ->
                                            val transcriptId = item.userTranscriptId
                                            val isClickable = item.status == ActivityStatus.COMPLETED &&
                                                    transcriptId != null

                                            ActivityItemRow(
                                                item = item,
                                                isNew = viewModel.isNew(item),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .then(
                                                        if (isClickable) Modifier.clickable {
                                                            onTranscriptClick(transcriptId!!)
                                                        } else Modifier
                                                    )
                                            )
                                            Divider(
                                                modifier = Modifier.padding(horizontal = 16.dp),
                                                color = MaterialTheme.colorScheme.surfaceVariant
                                            )
                                        }
                                    }
                                }

                                item { Spacer(modifier = Modifier.height(20.dp)) }
                            }
                        }

                        // Floating NewContentPill
                        if (newJobIds.isNotEmpty()) {
                            NewContentPill(
                                count = newJobIds.size,
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(top = 8.dp)
                            ) {
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
