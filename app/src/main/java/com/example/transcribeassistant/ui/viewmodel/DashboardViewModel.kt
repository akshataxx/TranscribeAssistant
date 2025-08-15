package com.example.transcribeassistant.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.transcribeassistant.data.auth.JwtManager
import com.example.transcribeassistant.domain.model.Transcript
import com.example.transcribeassistant.domain.repository.TranscriptRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CategoryGroup(
    val displayName: String, // This will be alias if available, otherwise category name
    val categoryId: String,
    val categoryName: String,
    val transcripts: List<Transcript>
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: TranscriptRepository,
    private val jwtManager: JwtManager
): ViewModel() {
    private val _transcripts = MutableStateFlow<List<Transcript>>(emptyList())
    val transcripts: StateFlow<List<Transcript>> = _transcripts

    private val _categoryGroups = MutableStateFlow<List<CategoryGroup>>(emptyList())
    val categoryGroups: StateFlow<List<CategoryGroup>> = _categoryGroups

    private val _transcriptsByCategory = MutableStateFlow<List<Transcript>>(emptyList())
    val transcriptsByCategory: StateFlow<List<Transcript>> = _transcriptsByCategory

    // For a real app, this should be fetched from a user session or preferences
    val userId: String = "1c9a16ba-1e25-4de0-bc8f-4414669bc0de"

    fun fetchTranscripts() {
        viewModelScope.launch {
            try{
                val response = repository.getAllTranscripts(userId = userId)
                _transcripts.value = response
                _categoryGroups.value = groupTranscripts(response)
                Log.d("DashboardVM", "Transcripts fetched and grouped: ${_categoryGroups.value}")
            }catch(e: Exception) {
                Log.e("DashboardVM", "Error fetching transcripts: ${e.message}")
            }
        }
    }

    fun updateAlias(categoryId: String, newAlias: String) {
        viewModelScope.launch {
            try {
                // Update the alias in the local database
                repository.upsertAlias(userId = userId, categoryId = categoryId, newAlias = newAlias)
                // Refetch the transcripts to update the UI
                fetchTranscripts()
                Log.d("DashboardVM", "Alias updated and transcripts refetched.")
            } catch (e: Exception) {
                Log.e("DashboardVM", "Error updating alias: ${e.message}")
            }
        }
    }

    // Fetch transcripts by category ID
    fun fetchTranscriptsByCategory(categoryId: String) {
        viewModelScope.launch {
            try {
                val transcripts = repository.getTranscriptsByCategoryId(categoryId)
                _transcriptsByCategory.value = transcripts
                Log.d("DashboardVM", "Transcripts fetched for category $categoryId: ${transcripts.size} items")
            } catch (e: Exception) {
                Log.e("DashboardVM", "Error fetching transcripts by category: ${e.message}")
            }
        }
    }

    private fun groupTranscripts(transcripts: List<Transcript>): List<CategoryGroup> {
        if (transcripts.isEmpty()) {
            return emptyList()
        }
        return transcripts.groupBy { it.categoryId }
            .map { (_, transcriptsInGroup) ->
                val first = transcriptsInGroup.first()
                CategoryGroup(
                    displayName = first.alias ?: first.category,
                    categoryId = first.categoryId,
                    categoryName = first.category,
                    transcripts = transcriptsInGroup
                )
            }
    }

    fun logout(){
        viewModelScope.launch {
            try{
                jwtManager.clearTokens()
            }catch(e: Exception) {
                Log.e("DashboardVM", "Error logging out: ${e.message}")
            }
        }
    }
}