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
 *
 * Use submitVideo(videoUrl) when user first shares the video
 * Use getTranscriptById(transcriptId) in TranscribeDetailsScreen to fetch the stored transcript
 */

@HiltViewModel
class TranscriptViewModel @Inject constructor(
    private val repository: TranscriptRepository
): ViewModel() {

    private val _transcript = MutableStateFlow<Transcript?>(null)
    val transcript: StateFlow<Transcript?> = _transcript
    private val _transcriptsByCategory = MutableStateFlow<List<Transcript>>(emptyList())
    val transcriptsByCategory: StateFlow<List<Transcript>> = _transcriptsByCategory
    private val _isSavingNotes = MutableStateFlow(false)
    val isSavingNotes: StateFlow<Boolean> = _isSavingNotes

    private val _saveNotesError = MutableStateFlow<String?>(null)
    val saveNotesError: StateFlow<String?> = _saveNotesError

    private val _saveNotesSuccess = MutableStateFlow(false)
    val saveNotesSuccess: StateFlow<Boolean> = _saveNotesSuccess

    fun saveNotes(transcriptId: String, notes: String?) {
        viewModelScope.launch {
            _isSavingNotes.value = true
            _saveNotesError.value = null
            _saveNotesSuccess.value = false
            try {
                repository.updateNotes(transcriptId, notes)
                // Update the local transcript state with the new notes
                _transcript.value = _transcript.value?.copy(notes = notes)
                _saveNotesSuccess.value = true
                Log.d("TranscriptVM", "Notes saved for $transcriptId")
            } catch (e: Exception) {
                Log.e("TranscriptVM", "Error saving notes: ${e.message}")
                _saveNotesError.value = e.message ?: "Failed to save notes"
            } finally {
                _isSavingNotes.value = false
            }
        }
    }

    fun dismissSaveNotesSuccess() {
        _saveNotesSuccess.value = false
    }

    fun dismissSaveNotesError() {
        _saveNotesError.value = null
    }

    // For initial video submission
    fun submitNewVideo(videoUrl: String) {
        viewModelScope.launch {
            try {
                val response = repository.transcribeVideo(videoUrl)
                Log.d("TranscriptVM", "Transcript created: ${response.transcript}")
                _transcript.value = response
            } catch(e: Exception) {
                Log.e("TranscriptVM", "Error: ${e.message}")
            }
        }
    }

    // For For fetching existing transcript by ID and displaying it on TranscribeDetailsScreen
    fun loadExistingTranscript(transcriptId: String) {
        viewModelScope.launch {
            try {
                val response = repository.getTranscriptById(transcriptId)
                Log.d("TranscriptVM", "Transcript fetched by ID: ${response.transcript}")
                _transcript.value = response
            } catch(e: Exception) {
                Log.e("TranscriptVM", "Error: ${e.message}")
            }
        }
    }

    // Fetch transcripts by category ID
    fun fetchTranscriptsByCategory(categoryId: String) {
        viewModelScope.launch {
            try {
                val transcripts = repository.getTranscriptsByCategoryId(categoryId)
                _transcriptsByCategory.value = transcripts
            } catch (e: Exception) {
                Log.e("TranscriptVM", "Error fetching transcripts by category: ${e.message}")
            }
        }
    }
}