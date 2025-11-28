package com.example.transcribeassistant.ui.screen.share

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.transcribeassistant.ui.viewmodel.ShareViewModel

@Composable
fun ShareScreen(
    sharedLink: String,
    viewModel: ShareViewModel = hiltViewModel(),
    onDone: () -> Unit,
    onNavigateToSubscription: () -> Unit = {}
) {
    var isSaving by remember { mutableStateOf(false) }
    val showUpgradePrompt by viewModel.showUpgradePrompt.collectAsState()
    val usageInfo by viewModel.usageInfo.collectAsState()

    LaunchedEffect(sharedLink) {
        isSaving = true
        // Fetch metadata, transcribe, categorize, save
        processSharedLink(sharedLink, viewModel)
        isSaving = false
        if (!showUpgradePrompt) {
            onDone() // Notify that saving is done only if no upgrade prompt
        }
    }

    if (showUpgradePrompt) {
        UpgradePromptDialog(
            usageInfo = usageInfo,
            onUpgradeClick = onNavigateToSubscription,
            onDismiss = {
                viewModel.dismissUpgradePrompt()
                onDone()
            }
        )
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (isSaving) {
                CircularProgressIndicator()
            } else {
                Text("Saved!")
            }
        }
    }
}

@Composable
private fun UpgradePromptDialog(
    usageInfo: com.example.transcribeassistant.domain.model.UsageInfo?,
    onUpgradeClick: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Upgrade Required",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text("You've reached your free transcription limit.")
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = usageInfo?.usageMessage ?: "Upgrade to Premium for unlimited transcriptions",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onUpgradeClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF5856D6)
                )
            ) {
                Text("Upgrade to Premium", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Not Now")
            }
        }
    )
}

suspend fun processSharedLink(link: String, viewModel: ShareViewModel) {
    println(link)
    viewModel.submitNewVideo(link)
}
