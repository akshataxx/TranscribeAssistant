package com.example.transcribeassistant.common

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object AppEventBus {
    private val _transcriptRefresh = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val transcriptRefresh = _transcriptRefresh.asSharedFlow()

    /** Tracks completions arriving via silent push that the user hasn't seen yet. */
    private val _newCompletionCount = MutableStateFlow(0)
    val newCompletionCount = _newCompletionCount.asStateFlow()

    fun emitRefresh() {
        _transcriptRefresh.tryEmit(Unit)
    }

    fun incrementNewCompletion() {
        _newCompletionCount.update { it + 1 }
    }

    /** Call when the user views the Activity tab. */
    fun clearNewCompletions() {
        _newCompletionCount.value = 0
    }
}
