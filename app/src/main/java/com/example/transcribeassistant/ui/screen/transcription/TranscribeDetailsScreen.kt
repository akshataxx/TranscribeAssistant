package com.example.transcribeassistant.ui.screen.transcription

import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.transcribeassistant.ui.screen.components.AnimatedBlobsBackground
import com.example.transcribeassistant.ui.screen.components.CategoryChip
import com.example.transcribeassistant.ui.screen.components.PrimaryText
import com.example.transcribeassistant.ui.screen.components.SecondaryText
import com.example.transcribeassistant.ui.screen.components.ScoopBlue
import com.example.transcribeassistant.ui.screen.components.ScoopCyan
import com.example.transcribeassistant.ui.screen.components.ScoopPurple
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
    val isSavingNotes by viewModel.isSavingNotes.collectAsState()
    val saveNotesError by viewModel.saveNotesError.collectAsState()
    val saveNotesSuccess by viewModel.saveNotesSuccess.collectAsState()

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scroll = rememberScrollState()
    var notes by remember { mutableStateOf("") }
    var initialNotes by remember { mutableStateOf("") }
    val notesChanged = notes != initialNotes

    val videoUrl = "https://www.tiktok.com/@simple.home.edit/video/7309754078010051841?q=recipe&t=1749454564398"

    // Initialize notes from transcript when loaded
    LaunchedEffect(transcript?.notes) {
        if (transcript != null) {
            val serverNotes = transcript.notes ?: ""
            notes = serverNotes
            initialNotes = serverNotes
        }
    }

    // Auto-dismiss success after 2s
    LaunchedEffect(saveNotesSuccess) {
        if (saveNotesSuccess) {
            delay(2000)
            viewModel.dismissSaveNotesSuccess()
        }
    }

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

    AnimatedBlobsBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = transcript?.title ?: "Details",
                            color = PrimaryText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = PrimaryText
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
                // Source info
                Text(
                    "Source TikTok • @${transcript?.account ?: "..."}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SecondaryText
                )
                Text(
                    "⏱ ${transcript?.let { TimeUtils.formatDuration(it.duration.toInt()) } ?: "..."}   •   ${transcript?.let { TimeUtils.timeAgo(it.uploadedAt) } ?: "..."}",
                    style = MaterialTheme.typography.bodySmall,
                    color = SecondaryText
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Notes Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(14.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Notes",
                                fontWeight = FontWeight.Bold,
                                color = PrimaryText
                            )
                            Button(
                                onClick = {
                                    viewModel.saveNotes(transcriptId, notes.ifBlank { null })
                                    initialNotes = notes
                                },
                                enabled = notesChanged && !isSavingNotes,
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ScoopPurple,
                                    disabledContainerColor = ScoopPurple.copy(alpha = 0.3f)
                                ),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
                            ) {
                                if (isSavingNotes) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Saving...", color = Color.White, style = MaterialTheme.typography.labelMedium)
                                } else {
                                    Text("Save", color = Color.White, style = MaterialTheme.typography.labelMedium)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            placeholder = { Text("Write your thoughts or action points here...") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = ScoopPurple,
                                unfocusedIndicatorColor = SecondaryText.copy(alpha = 0.3f),
                                focusedTextColor = PrimaryText,
                                unfocusedTextColor = PrimaryText,
                                cursorColor = ScoopPurple,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            )
                        )

                        // Success message
                        if (saveNotesSuccess) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Notes saved",
                                color = Color(0xFF10B981),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Error message with retry
                        if (saveNotesError != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    saveNotesError ?: "Error",
                                    color = Color(0xFFEF4444),
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.weight(1f)
                                )
                                TextButton(onClick = {
                                    viewModel.dismissSaveNotesError()
                                    viewModel.saveNotes(transcriptId, notes.ifBlank { null })
                                }) {
                                    Text("Retry", color = ScoopPurple, style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Transcript Summary with Read Aloud button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Transcript Summary",
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        onClick = {
                            transcript?.transcript?.let { text ->
                                Log.d("TTS", "Speaking: $text")
                                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
                            }
                        },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        border = BorderStroke(
                            1.dp,
                            Brush.linearGradient(colors = listOf(ScoopPurple, ScoopBlue, ScoopCyan))
                        )
                    ) {
                        Icon(
                            Icons.Default.VolumeUp,
                            contentDescription = null,
                            tint = ScoopPurple
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Read Aloud",
                            style = LocalTextStyle.current.copy(
                                brush = Brush.linearGradient(
                                    colors = listOf(ScoopPurple, ScoopBlue, ScoopCyan)
                                )
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Summary preview
                Text(
                    text = "\"${transcript?.transcript?.take(120) ?: ""}...\"",
                    fontStyle = FontStyle.Italic,
                    color = SecondaryText
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (transcript == null) {
                    CircularProgressIndicator(color = ScoopPurple)
                } else {
                    // Display structured content if available, otherwise show raw transcript
                    if (transcript.structuredContent != null && transcript.structuredContent.isNotEmpty()) {
                        StructuredContentDisplay(transcript.structuredContent)
                    } else {
                        Text(
                            text = transcript.transcript,
                            style = MaterialTheme.typography.bodyLarge,
                            color = PrimaryText
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Categories
                Text("Categories", fontWeight = FontWeight.Bold, color = PrimaryText)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (transcript != null) {
                        CategoryChip(transcript.category)
                    }
                    if (transcript?.alias != null && transcript.alias.isNotEmpty()) {
                        CategoryChip(transcript.alias)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }


    // Cleanup + auto-save unsaved notes
    DisposableEffect(Unit) {
        onDispose {
            textToSpeech.stop()
            textToSpeech.shutdown()
            if (notes != initialNotes) {
                viewModel.saveNotes(transcriptId, notes.ifBlank { null })
            }
        }
    }
}