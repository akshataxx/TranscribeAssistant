package com.example.transcribeassistant.ui.viewmodel

import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.transcribeassistant.domain.repository.JobRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val PREF_NAME = "notification_prefs"
private const val KEY_HAS_SHOWN_PERMISSION = "hasShownNotificationPermission"

@HiltViewModel
class AddLinkViewModel @Inject constructor(
    private val jobRepository: JobRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _urlText = MutableStateFlow("")
    val urlText: StateFlow<String> = _urlText

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    /** True after a job is successfully submitted — shows the success card. */
    private val _jobSubmitted = MutableStateFlow(false)
    val jobSubmitted: StateFlow<Boolean> = _jobSubmitted.asStateFlow()

    /** True when the pre-permission educational sheet should be shown. */
    private val _showNotificationSheet = MutableStateFlow(false)
    val showNotificationSheet: StateFlow<Boolean> = _showNotificationSheet.asStateFlow()

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
                val jobId = jobRepository.submitJob(url)
                _jobSubmitted.value = true
                Log.d("AddLinkVM", "Job submitted: $jobId")
                checkAndPromptIfNeeded()
            } catch (e: Exception) {
                Log.e("AddLinkVM", "Error submitting job: ${e.message}")
                _errorMessage.value = e.message ?: "Failed to submit video"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /** Show the permission sheet only once — on the first successful submission. */
    private fun checkAndPromptIfNeeded() {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val alreadyShown = prefs.getBoolean(KEY_HAS_SHOWN_PERMISSION, false)
        if (!alreadyShown) {
            _showNotificationSheet.value = true
        }
    }

    /** Call when user taps "Not Now" or after permission is granted/denied. */
    fun markNotificationPermissionShown() {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_HAS_SHOWN_PERMISSION, true)
            .apply()
        _showNotificationSheet.value = false
    }

    fun dismissSuccess() {
        _jobSubmitted.value = false
        _urlText.value = ""
    }
}
