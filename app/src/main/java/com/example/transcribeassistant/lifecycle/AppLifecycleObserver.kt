package com.example.transcribeassistant.lifecycle

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.transcribeassistant.data.refresh.AppRefreshManager
import com.example.transcribeassistant.data.refresh.RefreshEvent
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Observes app lifecycle events and triggers refresh when app comes to foreground.
 * Uses ProcessLifecycleOwner to track app-level lifecycle (not individual activities).
 */
@Singleton
class AppLifecycleObserver @Inject constructor(
    private val appRefreshManager: AppRefreshManager
) : DefaultLifecycleObserver {

    private var wasInBackground = false

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        // App is coming to foreground (visible and interactive)
        if (wasInBackground) {
            Log.d("AppLifecycle", "App foregrounded - triggering refresh")
            appRefreshManager.triggerRefresh(RefreshEvent.AppForegrounded)
        } else {
            Log.d("AppLifecycle", "App started (initial launch)")
        }
        wasInBackground = false
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        // App is going to background (no longer visible)
        wasInBackground = true
        Log.d("AppLifecycle", "App backgrounded")
    }
}
