package com.example.transcribeassistant.data.refresh

import android.util.Log
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.channels.BufferOverflow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Centralized event bus for triggering app-wide refresh events.
 * Similar pattern to AuthStateManager for broadcasting events via SharedFlow.
 */
@Singleton
class AppRefreshManager @Inject constructor() {

    private val _refreshTrigger = MutableSharedFlow<RefreshEvent>(
        replay = 0, // No replay - only active collectors receive events
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val refreshTrigger: SharedFlow<RefreshEvent> = _refreshTrigger.asSharedFlow()

    private var lastRefreshTime = 0L
    private val MIN_REFRESH_INTERVAL_MS = 3000L // 3 seconds minimum between refreshes

    /**
     * Triggers a refresh event if minimum interval has elapsed.
     * Prevents refresh storms from rapid app switching.
     */
    fun triggerRefresh(event: RefreshEvent = RefreshEvent.AppForegrounded) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastRefreshTime >= MIN_REFRESH_INTERVAL_MS) {
            Log.d("AppRefreshManager", "Triggering refresh: $event")
            _refreshTrigger.tryEmit(event)
            lastRefreshTime = currentTime
        } else {
            Log.d("AppRefreshManager", "Refresh debounced (too soon): $event")
        }
    }
}

sealed class RefreshEvent {
    object AppForegrounded : RefreshEvent()
    object ManualRefresh : RefreshEvent()
}
