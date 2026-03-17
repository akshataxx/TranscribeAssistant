package com.example.transcribeassistant.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.transcribeassistant.TranscribeAssistantApplication.Companion.TRANSCRIPTION_CHANNEL_ID
import com.example.transcribeassistant.activity.MainActivity
import com.example.transcribeassistant.common.AppEventBus
import com.example.transcribeassistant.data.auth.JwtManager
import com.example.transcribeassistant.domain.repository.DeviceRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TranscribeFirebaseMessagingService : FirebaseMessagingService() {

    @Inject lateinit var deviceRepository: DeviceRepository
    @Inject lateinit var jwtManager: JwtManager

    private val job = SupervisorJob()
    private val serviceScope = CoroutineScope(job + Dispatchers.IO)

    /**
     * Called when FCM issues a new token (first install or token rotation).
     * Re-register with the backend only if the user is already logged in.
     */
    override fun onNewToken(token: String) {
        val isLoggedIn = jwtManager.getAccessToken() != null
        if (isLoggedIn) {
            serviceScope.launch {
                runCatching { deviceRepository.registerDevice(token) }
            }
        }
    }

    /**
     * Called when a push message arrives while the app is in foreground,
     * or for data-only messages regardless of app state.
     */
    override fun onMessageReceived(message: RemoteMessage) {
        when (message.data["type"]) {
            "TRANSCRIPT_COMPLETE" -> {
                AppEventBus.emitRefresh()
                AppEventBus.incrementNewCompletion()
            }
            "TRANSCRIPT_FAILED"   -> showFailureNotification(message.data["errorMessage"])
        }
    }

    private fun showFailureNotification(errorMessage: String?) {
        val tapIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
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
            .notify(NOTIFICATION_ID, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    companion object {
        private const val NOTIFICATION_ID = 1001
    }
}
