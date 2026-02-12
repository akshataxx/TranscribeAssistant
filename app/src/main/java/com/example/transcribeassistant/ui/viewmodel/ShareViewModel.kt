package com.example.transcribeassistant.ui.viewmodel


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.transcribeassistant.domain.model.Transcript
import com.example.transcribeassistant.domain.model.UsageInfo
import com.example.transcribeassistant.domain.repository.SubscriptionRepository
import com.example.transcribeassistant.domain.repository.TranscriptRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ShareViewModel @Inject constructor(
    private val repository: TranscriptRepository,
    private val subscriptionRepository: SubscriptionRepository
): ViewModel() {

    private val _transcript = MutableStateFlow<Transcript?>(null)
    val transcript: StateFlow<Transcript?> = _transcript

    private val _usageInfo = MutableStateFlow<UsageInfo?>(null)
    val usageInfo: StateFlow<UsageInfo?> = _usageInfo

    private val _showUpgradePrompt = MutableStateFlow(false)
    val showUpgradePrompt: StateFlow<Boolean> = _showUpgradePrompt

    private val _submissionAccepted = MutableStateFlow(false)
    val submissionAccepted: StateFlow<Boolean> = _submissionAccepted

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun submitNewVideo(videoUrl: String) {
        viewModelScope.launch {
            try {
                val usage = subscriptionRepository.getUsageInfo()
                _usageInfo.value = usage

                if (usage.hasReachedFreeLimit) {
                    _showUpgradePrompt.value = true
                    return@launch
                }

                val accepted = repository.transcribeVideoAsync(videoUrl)
                if (accepted) {
                    _submissionAccepted.value = true
                    Log.d("ShareVM", "Async transcription accepted for: $videoUrl")
                } else {
                    _errorMessage.value = "Server could not accept the request."
                }
            } catch (e: Exception) {
                Log.e("ShareVM", "Error: ${e.message}")
                _errorMessage.value = e.message ?: "Failed to submit video"
            }
        }
    }

    fun dismissUpgradePrompt() {
        _showUpgradePrompt.value = false
    }
}