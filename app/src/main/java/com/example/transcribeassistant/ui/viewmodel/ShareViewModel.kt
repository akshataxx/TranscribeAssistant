package com.example.transcribeassistant.ui.viewmodel


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.transcribeassistant.domain.model.Transcript
import com.example.transcribeassistant.domain.repository.TranscriptRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ShareViewModel @Inject constructor(
    private val repository: TranscriptRepository
): ViewModel() {

    private val _transcript = MutableStateFlow<Transcript?>(null)
    val transcript: StateFlow<Transcript?> = _transcript
    val userId: String = "1c9a16ba-1e25-4de0-bc8f-4414669bc0de"


    // For initial video submission when user shares a video with the app
    fun submitNewVideo(videoUrl: String) {
        viewModelScope.launch {
            try {
                val response = repository.transcribeVideo(videoUrl, userId)
                Log.d("TranscriptVM", "Transcript created: ${response.transcript}")
                _transcript.value = response
            } catch(e: Exception) {
                Log.e("TranscriptVM", "Error: ${e.message}")
            }
        }
    }
}