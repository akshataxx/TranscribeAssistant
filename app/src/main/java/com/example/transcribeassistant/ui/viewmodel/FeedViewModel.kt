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


/**
 * ViewModel for managing the transcript data.
 * This ViewModel fetches the transcript from a video URL using Retrofit.
 * Calls the API service to get the transcript and exposes it as a StateFlow.
 * On error, it updates the StateFlow with an error message.
 */

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val repository: TranscriptRepository
): ViewModel() {

    private val _transcripts = MutableStateFlow<List<Transcript>>(emptyList())
    val transcripts: StateFlow<List<Transcript>> = _transcripts
    fun fetchTranscripts() {
        viewModelScope.launch {
            try{
                val response = repository.getAllTranscripts()
                Log.d("TranscriptVM", "Transcripts fetched: ${response}")
                _transcripts.value = response
            }catch(e: Exception) {
                Log.e("TranscriptVM", "Error: ${e.message}")
            }
        }
    }
}
