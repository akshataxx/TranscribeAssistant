package com.example.transcribeassistant.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import retrofit2.HttpException
import androidx.lifecycle.viewModelScope
import com.example.transcribeassistant.common.AppEventBus
import com.example.transcribeassistant.domain.model.ActivityItem
import com.example.transcribeassistant.domain.repository.JobRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val jobRepository: JobRepository
) : ViewModel() {

    private val _items = MutableStateFlow<List<ActivityItem>>(emptyList())
    val items: StateFlow<List<ActivityItem>> = _items.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /** IDs of jobs that completed via silent push and haven't been seen yet. */
    private val _newJobIds = MutableStateFlow<Set<String>>(emptySet())
    val newJobIds: StateFlow<Set<String>> = _newJobIds.asStateFlow()

    private var pollingJob: Job? = null

    init {
        observeSilentPush()
    }

    private fun observeSilentPush() {
        viewModelScope.launch {
            AppEventBus.transcriptRefresh.collect {
                fetchJobs()
            }
        }
    }

    fun fetchJobs() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val jobs = jobRepository.getJobs()
                // Mark newly completed jobs that weren't in the previous list.
                // Skip on initial load (_items empty) to avoid flagging all existing jobs as new.
                val previousIds = _items.value.map { it.id }.toSet()
                if (previousIds.isNotEmpty()) {
                    val freshCompletions = jobs
                        .filter { it.status.raw == "COMPLETED" && it.id !in previousIds }
                        .map { it.id }
                        .toSet()
                    if (freshCompletions.isNotEmpty()) {
                        _newJobIds.update { it + freshCompletions }
                    }
                }
                _items.value = jobs
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string() ?: "no body"
                Log.e("ActivityViewModel", "fetchJobs HTTP ${e.code()}: $errorBody")
                _error.value = "Failed to load activity. Please try again."
            } catch (e: Exception) {
                Log.e("ActivityViewModel", "fetchJobs error: ${e::class.simpleName}: ${e.message}", e)
                _error.value = "Failed to load activity. Please try again."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun startPolling() {
        if (pollingJob?.isActive == true) return
        pollingJob = viewModelScope.launch {
            while (true) {
                delay(30_000)
                fetchJobs()
            }
        }
    }

    fun stopPolling() {
        pollingJob?.cancel()
        pollingJob = null
    }

    fun isNew(item: ActivityItem): Boolean = item.id in _newJobIds.value

    /** Called when the user views the Activity tab — clears NEW labels and badge. */
    fun markAsViewed() {
        _newJobIds.value = emptySet()
        AppEventBus.clearNewCompletions()
    }
}
