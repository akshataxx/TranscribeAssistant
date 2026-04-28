package com.example.transcribeassistant.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.transcribeassistant.common.AppEventBus
import com.example.transcribeassistant.domain.model.BulkDeleteSummary
import com.example.transcribeassistant.domain.model.Transcript
import com.example.transcribeassistant.domain.repository.TranscriptRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val FEED_PREF_NAME = "feed_prefs"
private const val KEY_BANNER_DISMISSED = "notificationBannerDismissed"

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val repository: TranscriptRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _transcripts = MutableStateFlow<List<Transcript>>(emptyList())
    val transcripts: StateFlow<List<Transcript>> = _transcripts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isSelectionMode = MutableStateFlow(false)
    val isSelectionMode: StateFlow<Boolean> = _isSelectionMode.asStateFlow()

    private val _selectedTranscriptIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedTranscriptIds: StateFlow<Set<String>> = _selectedTranscriptIds.asStateFlow()

    private val _isDeleting = MutableStateFlow(false)
    val isDeleting: StateFlow<Boolean> = _isDeleting.asStateFlow()

    private val _deleteSummary = MutableStateFlow<BulkDeleteSummary?>(null)
    val deleteSummary: StateFlow<BulkDeleteSummary?> = _deleteSummary.asStateFlow()

    private val _showNewContentPill = MutableStateFlow(false)
    val showNewContentPill: StateFlow<Boolean> = _showNewContentPill.asStateFlow()

    private val _newTranscriptCount = MutableStateFlow(0)
    val newTranscriptCount: StateFlow<Int> = _newTranscriptCount.asStateFlow()

    /** Whether the "notifications off" banner has been dismissed this session. */
    private val _bannerDismissed = MutableStateFlow(
        context.getSharedPreferences(FEED_PREF_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_BANNER_DISMISSED, false)
    )
    val bannerDismissed: StateFlow<Boolean> = _bannerDismissed.asStateFlow()

    /** Re-checked every time the screen calls checkNotificationsEnabled(). */
    private val _notificationsEnabled = MutableStateFlow(true)
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    init {
        observeRefreshEvents()
    }

    private fun observeRefreshEvents() {
        viewModelScope.launch {
            AppEventBus.transcriptRefresh.collect {
                _newTranscriptCount.update { it + 1 }
                _showNewContentPill.value = true
            }
        }
    }

    fun fetchTranscripts() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _transcripts.value = repository.getAllTranscripts()
            } catch (e: Exception) {
                Log.e("FeedViewModel", "fetchTranscripts error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchTranscriptsByCategory(categoryId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _transcripts.value = repository.getTranscriptsByCategoryId(categoryId)
            } catch (e: Exception) {
                Log.e("FeedViewModel", "fetchByCategory error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refresh(categoryId: String? = null) {
        if (_isDeleting.value) return
        if (categoryId != null) fetchTranscriptsByCategory(categoryId)
        else fetchTranscripts()
    }

    fun beginSelection(transcriptId: String) {
        if (_isDeleting.value) return
        _isSelectionMode.value = true
        _selectedTranscriptIds.value = setOf(transcriptId)
    }

    fun toggleSelection(transcriptId: String) {
        if (_isDeleting.value) return

        if (!_isSelectionMode.value) {
            beginSelection(transcriptId)
            return
        }

        _selectedTranscriptIds.update { selectedIds ->
            if (transcriptId in selectedIds) selectedIds - transcriptId else selectedIds + transcriptId
        }

        if (_selectedTranscriptIds.value.isEmpty()) {
            _isSelectionMode.value = false
        }
    }

    fun cancelSelection() {
        if (_isDeleting.value) return
        _isSelectionMode.value = false
        _selectedTranscriptIds.value = emptySet()
    }

    fun toggleSelectAllTranscripts() {
        if (_isDeleting.value) return

        val allIds = _transcripts.value.map { it.id }.toSet()
        if (allIds.isNotEmpty() && _selectedTranscriptIds.value == allIds) {
            cancelSelection()
        } else {
            _isSelectionMode.value = true
            _selectedTranscriptIds.value = allIds
        }
    }

    fun isSelected(transcriptId: String): Boolean {
        return transcriptId in _selectedTranscriptIds.value
    }

    fun deleteSelectedTranscripts() {
        val requestedIds = _selectedTranscriptIds.value.toList()
        if (requestedIds.isEmpty() || _isDeleting.value) return

        viewModelScope.launch {
            _isDeleting.value = true
            try {
                val summary = repository.deleteTranscripts(requestedIds)
                val deletedIds = requestedIds.toSet()
                _transcripts.update { transcripts ->
                    transcripts.filterNot { it.id in deletedIds }
                }
                _selectedTranscriptIds.value = emptySet()
                _isSelectionMode.value = false
                _deleteSummary.value = summary
            } catch (e: Exception) {
                Log.e("FeedViewModel", "deleteSelectedTranscripts error: ${e.message}")
                _deleteSummary.value = BulkDeleteSummary(
                    requestedCount = requestedIds.size,
                    deletedCount = 0,
                    failedCount = requestedIds.size,
                    failureMessages = listOf("We couldn't delete the selected transcripts. Please try again.")
                )
            } finally {
                _isDeleting.value = false
            }
        }
    }

    fun clearDeleteSummary() {
        _deleteSummary.value = null
    }

    fun clearNewContentIndicator() {
        _showNewContentPill.value = false
        _newTranscriptCount.value = 0
    }

    fun dismissBanner() {
        _bannerDismissed.value = true
        context.getSharedPreferences(FEED_PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_BANNER_DISMISSED, true)
            .apply()
    }

    /** Call on screen resume to get the latest permission state. */
    fun checkNotificationsEnabled() {
        _notificationsEnabled.value = NotificationManagerCompat.from(context).areNotificationsEnabled()
    }
}
