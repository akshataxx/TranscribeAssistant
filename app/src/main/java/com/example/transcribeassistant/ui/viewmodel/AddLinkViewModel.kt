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
import kotlinx.coroutines.flow.StateFlow
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

    val isValidUrl: Boolean
        get() {
            val url = _urlText.value.trim()
            return url.startsWith("http://") || url.startsWith("https://")
        }

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
        if (!isValidUrl) {
            _errorMessage.value = "Please enter a valid URL starting with http:// or https://"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val transcript = repository.transcribeVideo(url)
                _createdTranscript.value = transcript
                Log.d("AddLinkVM", "Transcript created: ${transcript.title}")
            } catch (e: Exception) {
                Log.e("AddLinkVM", "Error transcribing: ${e.message}")
                _errorMessage.value = e.message ?: "Failed to transcribe video"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun dismissSuccess() {
        _createdTranscript.value = null
        _urlText.value = ""
    }
}
