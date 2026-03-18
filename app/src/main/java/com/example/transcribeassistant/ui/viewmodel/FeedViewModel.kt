package com.example.transcribeassistant.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.transcribeassistant.common.AppEventBus
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
        if (categoryId != null) fetchTranscriptsByCategory(categoryId)
        else fetchTranscripts()
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
