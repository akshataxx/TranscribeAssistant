package com.example.transcribeassistant

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.example.transcribeassistant.common.AppContextProvider
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TranscribeAssistantApplication : Application() {

    companion object {
        const val TRANSCRIPTION_CHANNEL_ID = "transcription_status"
    }

    override fun onCreate() {
        super.onCreate()
        AppContextProvider.init(this)
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        val channel = NotificationChannel(
            TRANSCRIPTION_CHANNEL_ID,
            "Transcription Status",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifies you when a transcription is ready or fails"
        }
        getSystemService(NotificationManager::class.java)
            .createNotificationChannel(channel)
    }
}