package com.example.transcribeassistant.common

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object AppEventBus {
    private val _transcriptRefresh = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val transcriptRefresh = _transcriptRefresh.asSharedFlow()

    fun emitRefresh() {
        _transcriptRefresh.tryEmit(Unit)
    }
}
