package com.example.transcribeassistant.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.transcribeassistant.TranscribeAssistantApplication.Companion.TRANSCRIPTION_CHANNEL_ID
import com.example.transcribeassistant.activity.MainActivity
import com.example.transcribeassistant.common.AppEventBus
import com.example.transcribeassistant.common.PendingDeepLinkManager
import com.example.transcribeassistant.data.auth.JwtManager
import com.example.transcribeassistant.domain.repository.DeviceRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TranscribeFirebaseMessagingService : FirebaseMessagingService() {

    @Inject lateinit var deviceRepository: DeviceRepository
    @Inject lateinit var jwtManager: JwtManager

    private val job = SupervisorJob()
    private val serviceScope = CoroutineScope(job + Dispatchers.IO)

    override fun onNewToken(token: String) {
        val isLoggedIn = jwtManager.getAccessToken() != null
        if (isLoggedIn) {
            serviceScope.launch {
                runCatching { deviceRepository.registerDevice(token) }
            }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        when (message.data["type"]) {
            "TRANSCRIPT_COMPLETE" -> {
                // Refresh UI, increment badge, and show a visible system notification
                AppEventBus.emitRefresh()
                AppEventBus.incrementNewCompletion()
                showTranscriptReadyNotification(message.data["transcriptId"])
            }

            "TRANSCRIPT_READY" -> {
                // User-visible notification — tapping navigates to the transcript detail
                val transcriptId = message.data["transcriptId"]
                showTranscriptReadyNotification(transcriptId)
            }

            "TRANSCRIPT_FAILED" -> {
                // User-visible notification — tapping navigates to Activity tab
                showFailureNotification(message.data["errorMessage"])
            }
        }
    }

    private fun showTranscriptReadyNotification(transcriptId: String?) {
        val tapIntent = PendingIntent.getActivity(
            this,
            REQUEST_CODE_TRANSCRIPT,
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(PendingDeepLinkManager.EXTRA_DEEP_LINK_TYPE, PendingDeepLinkManager.TYPE_TRANSCRIPT)
                if (transcriptId != null) {
                    putExtra(PendingDeepLinkManager.EXTRA_TRANSCRIPT_ID, transcriptId)
                }
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, TRANSCRIPTION_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Transcript Ready")
            .setContentText("Your transcription is ready to view.")
            .setAutoCancel(true)
            .setContentIntent(tapIntent)
            .build()

        getSystemService(NotificationManager::class.java)
            .notify(NOTIFICATION_ID_READY, notification)
    }

    private fun showFailureNotification(errorMessage: String?) {
        val tapIntent = PendingIntent.getActivity(
            this,
            REQUEST_CODE_FAILED,
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(PendingDeepLinkManager.EXTRA_DEEP_LINK_TYPE, PendingDeepLinkManager.TYPE_ACTIVITY)
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, TRANSCRIPTION_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Transcription Failed")
            .setContentText(errorMessage ?: "Your transcription could not be completed.")
            .setAutoCancel(true)
            .setContentIntent(tapIntent)
            .build()

        getSystemService(NotificationManager::class.java)
            .notify(NOTIFICATION_ID_FAILED, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    companion object {
        private const val NOTIFICATION_ID_READY = 1002
        private const val NOTIFICATION_ID_FAILED = 1001
        private const val REQUEST_CODE_TRANSCRIPT = 100
        private const val REQUEST_CODE_FAILED = 101
    }
}
