package com.example.transcribeassistant.ui.screen.feed

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.draw.clip
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

private val DockBg = Color(0xFF1F2937)
private val DangerRed = Color(0xF2EF4444)

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
    val subcategoryPickerOpen by viewModel.subcategoryPickerOpen.collectAsState()
    val subcategories by viewModel.subcategories.collectAsState()
    val isLoadingSubcategories by viewModel.isLoadingSubcategories.collectAsState()
    val isSavingSubcategory by viewModel.isSavingSubcategory.collectAsState()

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    val selectedCount = selectedTranscriptIds.size
    val hasSelectedAll = transcripts.isNotEmpty() && selectedTranscriptIds == transcripts.map { it.id }.toSet()

    // Determine which category context applies for the subcategory picker.
    // In drill-down mode use the passed categoryId; in bulk from main feed, derive from selected.
    val effectiveCategoryId: String? = categoryId
        ?: run {
            val selectedCategoryIds = transcripts
                .filter { it.id in selectedTranscriptIds }
                .map { it.categoryId }
                .toSet()
            if (selectedCategoryIds.size == 1) selectedCategoryIds.first() else null
        }

    // The category name shown in the picker subtitle
    val categoryName = transcripts.firstOrNull { it.categoryId == effectiveCategoryId }
        ?.let { it.alias ?: it.category } ?: ""

    // Initial fetch
    LaunchedEffect(categoryId) {
        viewModel.checkNotificationsEnabled()
        if (categoryId != null) viewModel.fetchTranscriptsByCategory(categoryId)
        else viewModel.fetchTranscripts()
    }

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

    // Subcategory picker sheet
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    if (subcategoryPickerOpen && effectiveCategoryId != null) {
        SubcategoryPickerSheet(
            categoryName = categoryName,
            subcategories = subcategories,
            currentSubcategoryId = null,
            isBulkMode = true,
            bulkCount = selectedCount,
            isLoading = isLoadingSubcategories,
            isSaving = isSavingSubcategory,
            sheetState = sheetState,
            onDismiss = { viewModel.dismissCategorizePicker() },
            onSave = { subcategoryId -> viewModel.applySubcategoryToSelected(subcategoryId) },
            onCreateSubcategory = { name ->
                effectiveCategoryId.let { catId -> viewModel.createSubcategory(catId, name) }
            }
        )
    }

    AnimatedBlobsBackground {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                when {
                    // Actions mode header (replaces both normal and category headers)
                    isSelectionMode -> {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White.copy(alpha = 0.70f))
                                .padding(horizontal = 16.dp)
                                .padding(top = 8.dp, bottom = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = { viewModel.cancelSelection() },
                                enabled = !isDeleting
                            ) {
                                Text("✕  Cancel", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = SecondaryText)
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            Text(
                                text = "$selectedCount selected",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            TextButton(
                                onClick = { viewModel.toggleSelectAllTranscripts() },
                                enabled = !isDeleting
                            ) {
                                Text(
                                    text = if (hasSelectedAll) "Deselect all" else "Select all",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ScoopPurple
                                )
                            }
                        }
                    }

                    // Category drill-down header
                    categoryId != null && onBackClick != null -> {
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
                    }

                    // Main feed header
                    else -> {
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
                                        Spacer(modifier = Modifier.height(if (isSelectionMode) 120.dp else 20.dp))
                                    }
                                }
                            }

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

            // Floating Actions dock — visible in selection mode when items are selected
            AnimatedVisibility(
                visible = isSelectionMode && selectedCount > 0,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = 18.dp)
            ) {
                ActionsDock(
                    selectedCount = selectedCount,
                    isDeleting = isDeleting,
                    canCategorize = effectiveCategoryId != null,
                    onCategorize = {
                        effectiveCategoryId?.let { viewModel.openCategorizePicker(it) }
                    },
                    onDelete = { showDeleteConfirmation = true }
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
                    if (selectedCount == 1) "This will permanently delete the selected transcript."
                    else "This will permanently delete the selected transcripts."
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
private fun ActionsDock(
    selectedCount: Int,
    isDeleting: Boolean,
    canCategorize: Boolean,
    onCategorize: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(9999.dp))
            .background(DockBg)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Categorize — gradient pill
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(9999.dp))
                .background(
                    if (canCategorize && !isDeleting)
                        Brush.linearGradient(colors = listOf(ScoopPurple, ScoopBlue, ScoopCyan))
                    else
                        Brush.linearGradient(colors = listOf(Color.Gray, Color.Gray))
                )
                .clickable(enabled = canCategorize && !isDeleting, onClick = onCategorize)
                .padding(horizontal = 13.dp, vertical = 9.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Categorize", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }

        // Delete — danger pill
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(9999.dp))
                .background(if (isDeleting) DangerRed.copy(alpha = 0.5f) else DangerRed)
                .clickable(enabled = !isDeleting, onClick = onDelete)
                .padding(horizontal = 13.dp, vertical = 9.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isDeleting) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(14.dp)
                )
            } else {
                Text("Delete", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        // Share — ghost pill
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(9999.dp))
                .clickable { /* TODO */ }
                .padding(horizontal = 13.dp, vertical = 9.dp)
        ) {
            Text("Share", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }

        // More — ghost pill
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(9999.dp))
                .clickable { /* TODO */ }
                .padding(horizontal = 13.dp, vertical = 9.dp)
        ) {
            Text("More ⋯", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}
