package com.example.transcribeassistant.ui.screen

import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.transcribeassistant.ui.viewmodel.TranscriptViewModel
import java.util.Locale
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.transcribeassistant.ui.screen.components.CategoryChip
import com.example.transcribeassistant.utils.TimeUtils

/**
 * TranscribeDetailsScreen displays the details of a transcript including the title, source, notes,
 * transcript summary, full transcript, and categories.
 * It also allows users to read the transcript aloud using TextToSpeech.
 * Part of the UI/Presentation layer of the Transcribe Assistant app.
 */
@Composable
fun TranscribeDetailsScreen(transcriptId: String) {
    val viewModel: TranscriptViewModel = hiltViewModel()
    val transcriptState = viewModel.transcript.collectAsState()
    val transcript = transcriptState.value

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
    Log.d("TTS", "Initialized: ${textToSpeech.isLanguageAvailable(Locale.US)}")


    LaunchedEffect(transcriptId) {
        if (transcriptId.isNotEmpty()) {
            // Fetch transcript using transcriptId
            Log.d("TranscribeDetails", "Fetching transcript for ID: $transcriptId")
            viewModel.loadExistingTranscript(transcriptId)
        } else {
            // Fetch transcript using videoUrl for the first time
            Log.d("TranscribeDetails", "Fetching transcript for video URL: $videoUrl")
            viewModel.submitNewVideo(videoUrl)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFFFDEE9), Color(0xFFB5FFFC))))
            .padding(16.dp)
            .verticalScroll(scroll)
    ) {
        // Back row
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            Spacer(modifier = Modifier.weight(1f))
            Text("Back", style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Title
        Text(
            text = transcript?.title ?: "Loading...",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        // TODO: add source to Transcript model
        Text("Source TikTok • @${transcript?.account?: "..."}", style = MaterialTheme.typography.bodyMedium)
        Text("⏱ ${transcript?.let { TimeUtils.formatDuration(it.duration) } ?: "..."}   •   ${transcript?.let { TimeUtils.timeAgo(it.uploadedAt) } ?: "..."}", style = MaterialTheme.typography.bodySmall)


        Spacer(modifier = Modifier.height(16.dp))

        // Notes
        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes") },
            placeholder = { Text("Write your thoughts or action points here…") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Transcript Summary
        Text("Transcript Summary", fontWeight = FontWeight.Bold)
        Text(
            text = "“${transcript?.transcript?.take(120) ?: ""}...”",
            fontStyle = FontStyle.Italic)

        Spacer(modifier = Modifier.height(16.dp))

        // Full Transcript + Read Aloud

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Full Transcript", fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    transcript?.transcript?.let{text ->
                        Log.d("TTS", "Speaking: $text")
                        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
                    }
                },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA8A8))
            ) {
                Icon(Icons.Default.VolumeUp, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Read Aloud")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (transcript == null) {
            CircularProgressIndicator()
        } else {
            Text(
                text = transcript.transcript,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Categories
        Text("Categories", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (transcript != null) {
                CategoryChip(transcript.category)
            }
            if (transcript?.alias != null &&  transcript.alias.isNotEmpty()) {
                CategoryChip(transcript.alias)
            }
        }
    }

    // Cleanup
    DisposableEffect(Unit) {
        onDispose {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
    }
}