package com.example.transcribeassistant

import android.app.Application
import com.example.transcribeassistant.common.AppContextProvider
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class TranscribeAssistantApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppContextProvider.init(this)
    }
}