package com.example.transcribeassistant.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.transcribeassistant.network.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for managing the transcript data.
 * This ViewModel fetches the transcript from a video URL using Retrofit.
 * Calls the API service to get the transcript and exposes it as a StateFlow.
 * On error, it updates the StateFlow with an error message.
 */
class TranscriptViewModel: ViewModel() {
    private val _transcript = MutableStateFlow<String?>(null)
    val transcript: StateFlow<String?> = _transcript

    fun fetchTranscript(videoUrl: String) {
        viewModelScope.launch {
            try{
                val result = RetrofitClient.apiService.getTranscriptFromVideo(mapOf("videoUrl" to videoUrl))
                _transcript.value = result
            }catch(e: Exception) {
                _transcript.value = "Error fetching transcript: ${e.message}"
            }
        }
    }

}