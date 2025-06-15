package com.example.transcribeassistant.ui.screen

import android.content.Intent
import android.net.Uri
import android.speech.tts.TextToSpeech
import androidx.compose.foundation.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.transcribeassistant.viewmodel.TranscriptViewModel
import java.util.Locale
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic


@Composable
fun TranscribeDetailsScreen(transcriptId: String) {
    val viewModel: TranscriptViewModel = viewModel()
    val transcript= viewModel.transcript.collectAsState()

    val context = LocalContext.current
    val scroll = rememberScrollState()
    var notes by remember { mutableStateOf("") }

    val videoUrl = "https://www.tiktok.com/@simple.home.edit/video/7309754078010051841?q=recipe&t=1749454564398"

    // setup for TextToSpeech
    val textToSpeech = remember {
        TextToSpeech(context, null).apply {
            language = Locale.US
        }
    }

    LaunchedEffect(Unit) {
        viewModel.fetchTranscript(videoUrl)
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(scroll)
    ) {
        Text(
            text = "Transcript for Video",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = videoUrl,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl))
                context.startActivity(intent)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (transcript.value == null) {
            CircularProgressIndicator()
        } else {
            Text(
                text = transcript.value ?: "",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Microphone icon button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = {
                    transcript.value?.let {
                        textToSpeech.speak(it, TextToSpeech.QUEUE_FLUSH, null, null)
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Read Aloud"
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Add your notes...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        )
    }

    // Cleanup TextToSpeech resources
    DisposableEffect(Unit) {
        onDispose {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
    }
}