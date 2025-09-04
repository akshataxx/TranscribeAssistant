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
    // For initial video submission when user shares a video with the app
    fun submitNewVideo(videoUrl: String) {
        viewModelScope.launch {
            try {
                // Check usage limits first
                val usage = subscriptionRepository.getUsageInfo()
                _usageInfo.value = usage
                
                if (usage.hasReachedFreeLimit) {
                    _showUpgradePrompt.value = true
                    return@launch
                }
                
                val response = repository.transcribeVideo(videoUrl)
                Log.d("TranscriptVM", "Transcript created: ${response.transcript}")
                _transcript.value = response
            } catch(e: Exception) {
                Log.e("TranscriptVM", "Error: ${e.message}")
            }
        }
    }
    
    fun dismissUpgradePrompt() {
        _showUpgradePrompt.value = false
    }
}