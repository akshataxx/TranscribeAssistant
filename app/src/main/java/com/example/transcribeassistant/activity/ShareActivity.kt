package com.example.transcribeassistant.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.transcribeassistant.ui.screen.share.ShareScreen
import com.example.transcribeassistant.ui.theme.TranscribeAssistantTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShareActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedText = intent?.getStringExtra(Intent.EXTRA_TEXT)

        setContent {
            TranscribeAssistantTheme {
                if (!sharedText.isNullOrEmpty()) {
                    ShareScreen(
                        sharedLink = sharedText,
                        viewModel = hiltViewModel(),
                        onDone = { finish() }
                    ) {
                        finish() // Finish after saving or canceling
                    }
                } else {
                    Text("Invalid shared link")
                }
            }
        }
    }
}