package com.example.transcribeassistant.common

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Singleton that bridges MainActivity (receives notification tap Intent)
 * with TranscribeNavGraph (handles navigation).
 */
object PendingDeepLinkManager {

    sealed class DeepLink {
        data class Transcript(val transcriptId: String) : DeepLink()
        object ActivityTab : DeepLink()
    }

    private val _pendingDeepLink = MutableStateFlow<DeepLink?>(null)
    val pendingDeepLink = _pendingDeepLink.asStateFlow()

    fun set(deepLink: DeepLink) {
        _pendingDeepLink.value = deepLink
    }

    fun clear() {
        _pendingDeepLink.value = null
    }

    // Intent extra keys — shared between FirebaseMessagingService and MainActivity
    const val EXTRA_DEEP_LINK_TYPE = "deepLinkType"
    const val EXTRA_TRANSCRIPT_ID = "transcriptId"
    const val TYPE_TRANSCRIPT = "transcript"
    const val TYPE_ACTIVITY = "activity"
}
