package com.example.transcribeassistant.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.transcribeassistant.ui.theme.TranscribeAssistantTheme
import kotlinx.coroutines.delay

/**
 * Activity to handle return from Stripe checkout
 */
class SubscriptionReturnActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sessionId = intent.data?.getQueryParameter("session_id")
        val isSuccess = intent.data?.path?.contains("success") == true

        setContent {
            TranscribeAssistantTheme {
                SubscriptionReturnScreen(
                    isSuccess = isSuccess,
                    sessionId = sessionId,
                    onClose = { finish() }
                )
            }
        }
    }
}

@Composable
fun SubscriptionReturnScreen(
    isSuccess: Boolean,
    sessionId: String?,
    onClose: () -> Unit
) {
    var countdown by remember { mutableStateOf(3) }

    LaunchedEffect(Unit) {
        // Auto-close after 3 seconds
        for (i in 3 downTo 1) {
            countdown = i
            delay(1000)
        }
        onClose()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (isSuccess) {
                Text(
                    text = "✓",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Subscription Activated!",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Welcome to Premium! You now have unlimited transcriptions.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(
                    text = "✕",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Subscription Cancelled",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "You can try again anytime from the subscription page.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Returning to app in $countdown...",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onClose) {
                Text("Continue")
            }
        }
    }
}
