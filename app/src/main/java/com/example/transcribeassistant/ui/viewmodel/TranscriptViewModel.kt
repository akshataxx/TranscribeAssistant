package com.example.transcribeassistant.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.transcribeassistant.domain.model.Subcategory
import com.example.transcribeassistant.domain.model.Transcript
import com.example.transcribeassistant.domain.repository.CategoryRepository
import com.example.transcribeassistant.domain.repository.TranscriptRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TranscriptViewModel @Inject constructor(
    private val repository: TranscriptRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

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

    // Subcategory state
    private val _subcategories = MutableStateFlow<List<Subcategory>>(emptyList())
    val subcategories: StateFlow<List<Subcategory>> = _subcategories

    private val _isLoadingSubcategories = MutableStateFlow(false)
    val isLoadingSubcategories: StateFlow<Boolean> = _isLoadingSubcategories

    private val _isSavingSubcategory = MutableStateFlow(false)
    val isSavingSubcategory: StateFlow<Boolean> = _isSavingSubcategory

    fun saveNotes(transcriptId: String, notes: String?) {
        viewModelScope.launch {
            _isSavingNotes.value = true
            _saveNotesError.value = null
            _saveNotesSuccess.value = false
            try {
                repository.updateNotes(transcriptId, notes)
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

    fun submitNewVideo(videoUrl: String) {
        viewModelScope.launch {
            try {
                val response = repository.transcribeVideo(videoUrl)
                Log.d("TranscriptVM", "Transcript created: ${response.transcript}")
                _transcript.value = response
            } catch (e: Exception) {
                Log.e("TranscriptVM", "Error: ${e.message}")
            }
        }
    }

    fun loadExistingTranscript(transcriptId: String) {
        viewModelScope.launch {
            try {
                val response = repository.getTranscriptById(transcriptId)
                Log.d("TranscriptVM", "Transcript fetched by ID: ${response.transcript}")
                _transcript.value = response
            } catch (e: Exception) {
                Log.e("TranscriptVM", "Error: ${e.message}")
            }
        }
    }

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

    fun loadSubcategoriesForCategory(categoryId: String) {
        viewModelScope.launch {
            _isLoadingSubcategories.value = true
            try {
                _subcategories.value = categoryRepository.getSubcategoriesForCategory(categoryId)
            } catch (e: Exception) {
                Log.e("TranscriptVM", "Error loading subcategories: ${e.message}")
                _subcategories.value = emptyList()
            } finally {
                _isLoadingSubcategories.value = false
            }
        }
    }

    fun setSubcategoryForTranscript(transcriptId: String, subcategoryId: String) {
        viewModelScope.launch {
            _isSavingSubcategory.value = true
            try {
                val updated = repository.setTranscriptSubcategory(transcriptId, subcategoryId)
                _transcript.value = updated
            } catch (e: Exception) {
                Log.e("TranscriptVM", "Error setting subcategory: ${e.message}")
            } finally {
                _isSavingSubcategory.value = false
            }
        }
    }

    fun createSubcategory(categoryId: String, name: String, transcriptId: String) {
        viewModelScope.launch {
            try {
                val newSub = categoryRepository.createSubcategory(categoryId, name)
                _subcategories.update { it + newSub }
                setSubcategoryForTranscript(transcriptId, newSub.id)
            } catch (e: Exception) {
                Log.e("TranscriptVM", "Error creating subcategory: ${e.message}")
            }
        }
    }
}
