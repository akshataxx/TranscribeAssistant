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
class DashboardViewModel @Inject constructor(
    private val repository: TranscriptRepository
): ViewModel() {
    private val _transcripts = MutableStateFlow<List<Transcript>>(emptyList())
    val transcripts: StateFlow<List<Transcript>> = _transcripts
    val userId: String = "1c9a16ba-1e25-4de0-bc8f-4414669bc0de"

    fun fetchTranscripts() {
        viewModelScope.launch {
            try{
                val response = repository.getAllTranscripts(userId = userId)
                Log.d("TranscriptVM", "Transcripts fetched: ${response}")
                _transcripts.value = response
            }catch(e: Exception) {
                Log.e("TranscriptVM", "Error: ${e.message}")
            }
        }
    }
}