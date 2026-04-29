package com.example.transcribeassistant.ui.screen.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.transcribeassistant.ui.screen.components.AnimatedBlobsBackground
import com.example.transcribeassistant.ui.screen.components.EnableNotificationsBanner
import com.example.transcribeassistant.ui.screen.components.NewContentPill
import com.example.transcribeassistant.ui.screen.components.ScoopBlue
import com.example.transcribeassistant.ui.screen.components.ScoopCyan
import com.example.transcribeassistant.ui.screen.components.ScoopPurple
import com.example.transcribeassistant.ui.screen.components.SecondaryText
import com.example.transcribeassistant.ui.viewmodel.FeedViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    onTranscriptClick: (String) -> Unit,
    viewModel: FeedViewModel = hiltViewModel(),
    categoryId: String? = null,
    onBackClick: (() -> Unit)? = null
) {
    val transcripts by viewModel.transcripts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val showNewContentPill by viewModel.showNewContentPill.collectAsState()
    val newTranscriptCount by viewModel.newTranscriptCount.collectAsState()
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()
    val bannerDismissed by viewModel.bannerDismissed.collectAsState()
    val isSelectionMode by viewModel.isSelectionMode.collectAsState()
    val selectedTranscriptIds by viewModel.selectedTranscriptIds.collectAsState()
    val isDeleting by viewModel.isDeleting.collectAsState()
    val deleteSummary by viewModel.deleteSummary.collectAsState()

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    val selectedCount = selectedTranscriptIds.size
    val hasSelectedAll = transcripts.isNotEmpty() && selectedTranscriptIds == transcripts.map { it.id }.toSet()

    // Initial fetch
    LaunchedEffect(categoryId) {
        viewModel.checkNotificationsEnabled()
        if (categoryId != null) viewModel.fetchTranscriptsByCategory(categoryId)
        else viewModel.fetchTranscripts()
    }

    // Recheck notification status when screen resumes (user may have changed in Settings)
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.checkNotificationsEnabled()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    AnimatedBlobsBackground {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                if (categoryId != null && onBackClick != null) {
                    // Category drill-down: top bar with back button and category name
                    val categoryTitle = transcripts.firstOrNull()?.let { it.alias ?: it.category } ?: ""
                    TopAppBar(
                        title = {
                            Text(
                                text = categoryTitle,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    brush = Brush.linearGradient(
                                        colors = listOf(ScoopPurple, ScoopBlue, ScoopCyan)
                                    )
                                )
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = onBackClick) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = ScoopPurple
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                    )
                } else {
                    if (isSelectionMode) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .padding(top = 8.dp, bottom = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = { viewModel.cancelSelection() },
                                enabled = !isDeleting
                            ) {
                                Text("Cancel", color = SecondaryText)
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            Text(
                                text = "$selectedCount Selected",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.weight(1f))
                        }
                    } else {
                        // Main feed tab: gradient "Your Feed" header
                        Text(
                            text = "Your Feed",
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

                        // Notifications banner only on the main feed tab
                        if (!notificationsEnabled && !bannerDismissed) {
                            EnableNotificationsBanner(
                                onDismiss = { viewModel.dismissBanner() },
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                    }
                }

                when {
                    isLoading && transcripts.isEmpty() -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = ScoopPurple, modifier = Modifier.size(36.dp))
                        }
                    }

                    transcripts.isEmpty() -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = "No transcripts yet.\nAdd a link to get started.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = SecondaryText
                            )
                        }
                    }

                    else -> {
                        Box(modifier = Modifier.fillMaxSize()) {
                            PullToRefreshBox(
                                isRefreshing = isRefreshing,
                                onRefresh = { viewModel.refresh(categoryId) }
                            ) {
                                LazyColumn(
                                    state = listState,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    // Spacer so pill doesn't cover the first card
                                    if (showNewContentPill) {
                                        item { Spacer(modifier = Modifier.height(44.dp)) }
                                    }

                                    items(transcripts, key = { it.id }) { transcript ->
                                        TranscriptCard(
                                            transcript = transcript,
                                            onClick = {
                                                if (isSelectionMode) {
                                                    viewModel.toggleSelection(transcript.id)
                                                } else {
                                                    onTranscriptClick(transcript.id)
                                                }
                                            },
                                            onLongClick = { viewModel.beginSelection(transcript.id) },
                                            isSelectionMode = isSelectionMode,
                                            isSelected = transcript.id in selectedTranscriptIds,
                                            enabled = !isDeleting,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp)
                                                .padding(bottom = 12.dp)
                                        )
                                    }

                                    item {
                                        Spacer(modifier = Modifier.height(if (isSelectionMode) 96.dp else 20.dp))
                                    }
                                }
                            }

                            // Floating NewContentPill
                            if (showNewContentPill) {
                                NewContentPill(
                                    count = newTranscriptCount,
                                    modifier = Modifier
                                        .align(Alignment.TopCenter)
                                        .padding(top = 8.dp)
                                ) {
                                    viewModel.clearNewContentIndicator()
                                    viewModel.refresh(categoryId)
                                    scope.launch { listState.animateScrollToItem(0) }
                                }
                            }
                        }
                    }
                }
            }

            if (isSelectionMode) {
                BulkDeleteBottomBar(
                    selectedCount = selectedCount,
                    hasSelectedAll = hasSelectedAll,
                    isDeleting = isDeleting,
                    onCancel = { viewModel.cancelSelection() },
                    onToggleSelectAll = { viewModel.toggleSelectAllTranscripts() },
                    onDelete = { showDeleteConfirmation = true },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { if (!isDeleting) showDeleteConfirmation = false },
            title = { Text("Delete transcripts?") },
            text = {
                Text(
                    if (selectedCount == 1) {
                        "This will permanently delete the selected transcript."
                    } else {
                        "This will permanently delete the selected transcripts."
                    }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmation = false
                        viewModel.deleteSelectedTranscripts()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    enabled = !isDeleting
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteConfirmation = false },
                    enabled = !isDeleting
                ) {
                    Text("Cancel", color = SecondaryText)
                }
            }
        )
    }

    deleteSummary?.let { summary ->
        AlertDialog(
            onDismissRequest = { viewModel.clearDeleteSummary() },
            title = { Text(summary.title) },
            text = { Text(summary.message) },
            confirmButton = {
                TextButton(onClick = { viewModel.clearDeleteSummary() }) {
                    Text("OK", color = ScoopPurple)
                }
            }
        )
    }
}

@Composable
private fun BulkDeleteBottomBar(
    selectedCount: Int,
    hasSelectedAll: Boolean,
    isDeleting: Boolean,
    onCancel: () -> Unit,
    onToggleSelectAll: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.96f))
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(
            onClick = onCancel,
            enabled = !isDeleting,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = SecondaryText)
        ) {
            Text("Cancel")
        }

        OutlinedButton(
            onClick = onToggleSelectAll,
            enabled = !isDeleting,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = ScoopPurple)
        ) {
            Text(if (hasSelectedAll) "Deselect All" else "Select All")
        }

        Button(
            onClick = onDelete,
            enabled = selectedCount > 0 && !isDeleting,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Red,
                disabledContainerColor = Color.Red.copy(alpha = 0.5f)
            )
        ) {
            if (isDeleting) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(16.dp)
                )
            } else {
                Text("Delete $selectedCount")
            }
        }
    }
}
