package com.example.transcribeassistant.ui.viewmodel

import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.transcribeassistant.domain.model.Transcript
import com.example.transcribeassistant.domain.repository.TranscriptRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddLinkViewModel @Inject constructor(
    private val repository: TranscriptRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _urlText = MutableStateFlow("")
    val urlText: StateFlow<String> = _urlText

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _createdTranscript = MutableStateFlow<Transcript?>(null)
    val createdTranscript: StateFlow<Transcript?> = _createdTranscript

    private val _submissionAccepted = MutableStateFlow(false)
    val submissionAccepted: StateFlow<Boolean> = _submissionAccepted

    // Reactive: recomposes when urlText changes
    val isValidUrl: StateFlow<Boolean> = _urlText.map { url ->
        val trimmed = url.trim()
        trimmed.startsWith("http://") || trimmed.startsWith("https://")
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    fun onUrlChanged(url: String) {
        _urlText.value = url
        _errorMessage.value = null
    }

    fun pasteFromClipboard() {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = clipboard.primaryClip
        if (clip != null && clip.itemCount > 0) {
            val text = clip.getItemAt(0).text?.toString() ?: ""
            _urlText.value = text
            _errorMessage.value = null
        }
    }

    fun submit() {
        val url = _urlText.value.trim()
        if (!isValidUrl.value) {
            _errorMessage.value = "Please enter a valid URL starting with http:// or https://"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val accepted = repository.transcribeVideoAsync(url)
                if (accepted) {
                    _submissionAccepted.value = true
                    Log.d("AddLinkVM", "Async transcription accepted for: $url")
                } else {
                    _errorMessage.value = "Server could not accept the request. Please try again."
                }
            } catch (e: Exception) {
                Log.e("AddLinkVM", "Error submitting: ${e.message}")
                _errorMessage.value = when {
                    e.message?.contains("Unable to resolve host") == true ->
                        "No internet connection. Please check your network."
                    e.message?.contains("timeout", ignoreCase = true) == true ->
                        "Request timed out. Please try again."
                    else -> e.message ?: "Failed to submit video"
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun dismissSuccess() {
        _submissionAccepted.value = false
        _createdTranscript.value = null
        _urlText.value = ""
    }
}
