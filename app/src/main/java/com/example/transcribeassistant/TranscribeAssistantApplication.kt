package com.example.transcribeassistant

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.transcribeassistant.common.AppContextProvider
import com.example.transcribeassistant.lifecycle.AppLifecycleObserver
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject


@HiltAndroidApp
class TranscribeAssistantApplication : Application() {

    @Inject
    lateinit var appLifecycleObserver: AppLifecycleObserver

    override fun onCreate() {
        super.onCreate()
        AppContextProvider.init(this)

        // Register app lifecycle observer to detect foreground/background events
        ProcessLifecycleOwner.get().lifecycle.addObserver(appLifecycleObserver)
    }
}