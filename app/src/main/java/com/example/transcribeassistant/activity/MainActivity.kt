package com.example.transcribeassistant.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.transcribeassistant.common.PendingDeepLinkManager
import com.example.transcribeassistant.ui.screen.TranscribeAssistantApp
import com.example.transcribeassistant.ui.theme.TranscribeAssistantTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.statusBarColor = android.graphics.Color.TRANSPARENT

        // Handle deep link from notification tap (cold start)
        handleDeepLinkIntent(intent)

        setContent {
            TranscribeAssistantTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    TranscribeAssistantApp()
                }
            }
        }
    }

    /** Called when app is already running and a notification is tapped (warm start). */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleDeepLinkIntent(intent)
    }

    private fun handleDeepLinkIntent(intent: Intent?) {
        val type = intent?.getStringExtra(PendingDeepLinkManager.EXTRA_DEEP_LINK_TYPE) ?: return

        when (type) {
            PendingDeepLinkManager.TYPE_TRANSCRIPT -> {
                val transcriptId = intent.getStringExtra(PendingDeepLinkManager.EXTRA_TRANSCRIPT_ID)
                if (!transcriptId.isNullOrBlank()) {
                    PendingDeepLinkManager.set(PendingDeepLinkManager.DeepLink.Transcript(transcriptId))
                }
            }
            PendingDeepLinkManager.TYPE_ACTIVITY -> {
                PendingDeepLinkManager.set(PendingDeepLinkManager.DeepLink.ActivityTab)
            }
        }
    }
}
