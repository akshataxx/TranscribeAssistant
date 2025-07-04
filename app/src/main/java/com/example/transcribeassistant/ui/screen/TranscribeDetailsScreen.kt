package com.example.transcribeassistant.ui.screen

import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.transcribeassistant.ui.screen.components.CategoryChip
import com.example.transcribeassistant.ui.viewmodel.TranscriptViewModel
import com.example.transcribeassistant.utils.TimeUtils
import java.util.Locale

/**
 * TranscribeDetailsScreen displays the details of a transcript including the title, source, notes,
 * transcript summary, full transcript, and categories.
 * It also allows users to read the transcript aloud using TextToSpeech.
 * Part of the UI/Presentation layer of the Transcribe Assistant app.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranscribeDetailsScreen(
    transcriptId: String,
    onBackClick: () -> Unit
) {
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = transcript?.title ?: "Details",
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(scroll)
        ) {
            // TODO: add source to Transcript model
            Text("Source TikTok • @${transcript?.account?: "..."}", style = MaterialTheme.typography.bodyMedium, color = Color.White)
            Text("⏱ ${transcript?.let { TimeUtils.formatDuration(it.duration.toInt()) } ?: "..."}   •   ${transcript?.let { TimeUtils.timeAgo(it.uploadedAt) } ?: "..."}", style = MaterialTheme.typography.bodySmall, color = Color.White)


            Spacer(modifier = Modifier.height(16.dp))

            // Notes
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes") },
                placeholder = { Text("Write your thoughts or action points here…") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.White,
                    unfocusedIndicatorColor = Color.Gray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.Gray,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Transcript Summary
            Text("Transcript Summary", fontWeight = FontWeight.Bold, color = Color.White)
            Text(
                text = "“${transcript?.transcript?.take(120) ?: ""}...”",
                fontStyle = FontStyle.Italic,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Full Transcript + Read Aloud

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Full Transcript", fontWeight = FontWeight.Medium, color = Color.White)
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
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Categories
            Text("Categories", fontWeight = FontWeight.Bold, color = Color.White)
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
    }


    // Cleanup
    DisposableEffect(Unit) {
        onDispose {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
    }
}