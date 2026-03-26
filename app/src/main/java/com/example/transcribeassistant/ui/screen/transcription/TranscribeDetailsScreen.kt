package com.example.transcribeassistant.ui.screen.transcription

import android.content.Intent
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.draw.shadow
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import org.json.JSONObject
import java.util.Locale

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
    val clipboardManager = LocalClipboardManager.current
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
                            text = transcript?.title ?: "",
                            fontWeight = FontWeight.Bold,
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
                    .fillMaxWidth()
                    .padding(paddingValues)
                    .padding(top = 8.dp)
                    .verticalScroll(scroll)
            ) {
                if (transcript == null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = ScoopPurple)
                    }
                } else {
                    // Source info + category chips (no "Source" prefix, no "Categories" heading)
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "${TimeUtils.platformFromUrl(transcript.videoUrl)} • @${transcript.account}",
                            fontSize = 14.sp,
                            color = SecondaryText
                        )
                        Text(
                            text = "⏱ ${TimeUtils.formatDuration(transcript.duration.toInt())}   •   ${TimeUtils.timeAgo(transcript.uploadedAt)}",
                            fontSize = 14.sp,
                            color = SecondaryText
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            CategoryChip(transcript.category)
                            if (transcript.alias != null && transcript.alias.isNotEmpty()) {
                                CategoryChip(transcript.alias)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Content card — white box matching iOS
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .shadow(
                                elevation = 6.dp,
                                shape = RoundedCornerShape(16.dp),
                                ambientColor = Color.Black.copy(alpha = 0.05f),
                                spotColor = Color.Black.copy(alpha = 0.05f)
                            ),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            // Volume icon only (no label text), reads structured content
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                IconButton(
                                    onClick = {
                                        val readText = buildReadAloudText(
                                            transcript.structuredContent,
                                            transcript.transcript
                                        )
                                        textToSpeech.speak(readText, TextToSpeech.QUEUE_FLUSH, null, null)
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        Icons.Filled.VolumeUp,
                                        contentDescription = "Read aloud",
                                        tint = ScoopPurple,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            if (transcript.structuredContent != null && transcript.structuredContent.isNotEmpty()) {
                                StructuredContentDisplay(transcript.structuredContent)
                            } else {
                                Text(
                                    text = transcript.transcript,
                                    fontSize = 16.sp,
                                    color = SecondaryText,
                                    lineHeight = 22.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Notes card — matching iOS styling
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .shadow(
                                elevation = 6.dp,
                                shape = RoundedCornerShape(16.dp),
                                ambientColor = Color.Black.copy(alpha = 0.05f),
                                spotColor = Color.Black.copy(alpha = 0.05f)
                            ),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Notes",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = PrimaryText
                                )
                                Surface(
                                    onClick = {
                                        viewModel.saveNotes(transcriptId, notes.ifBlank { null })
                                        initialNotes = notes
                                    },
                                    enabled = notesChanged && !isSavingNotes,
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (notesChanged && !isSavingNotes) ScoopPurple else Color.Gray
                                ) {
                                    Box(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isSavingNotes) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(16.dp),
                                                color = Color.White,
                                                strokeWidth = 2.dp
                                            )
                                        } else {
                                            Text(
                                                "Save",
                                                color = Color.White,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White, RoundedCornerShape(8.dp))
                                    .border(1.dp, Color.Black.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                    .padding(12.dp)
                            ) {
                                if (notes.isEmpty()) {
                                    Text(
                                        "Add your notes here...",
                                        fontSize = 16.sp,
                                        color = SecondaryText.copy(alpha = 0.5f)
                                    )
                                }
                                BasicTextField(
                                    value = notes,
                                    onValueChange = { notes = it },
                                    textStyle = TextStyle(fontSize = 16.sp, color = PrimaryText),
                                    modifier = Modifier.fillMaxWidth(),
                                    minLines = 3,
                                    cursorBrush = SolidColor(ScoopPurple)
                                )
                            }

                            if (saveNotesSuccess) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Filled.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF10B981),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        "Notes saved",
                                        color = Color(0xFF10B981),
                                        fontSize = 14.sp
                                    )
                                }
                            }

                            if (saveNotesError != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        saveNotesError ?: "Error",
                                        color = Color(0xFFEF4444),
                                        fontSize = 14.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                    TextButton(onClick = {
                                        viewModel.dismissSaveNotesError()
                                        viewModel.saveNotes(transcriptId, notes.ifBlank { null })
                                    }) {
                                        Text(
                                            "Retry",
                                            color = ScoopPurple,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Copy + Share buttons (matching iOS layout)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(transcript.transcript))
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.5.dp, ScoopPurple),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ScoopPurple),
                            contentPadding = PaddingValues(vertical = 12.dp)
                        ) {
                            Icon(
                                Icons.Filled.ContentCopy,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Copy", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(ScoopPurple, ScoopBlue, ScoopCyan)
                                    )
                                )
                                .clickable {
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT, transcript.transcript)
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, "Share"))
                                }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Filled.Share,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "Share",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

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

/**
 * Builds readable text from structured content JSON for TTS.
 * Falls back to raw transcript if structured content is unavailable or unparseable.
 */
fun buildReadAloudText(structuredContent: String?, fallback: String): String {
    if (structuredContent.isNullOrEmpty()) return fallback
    return try {
        val json = JSONObject(structuredContent)
        val type = json.optString("type", "general")
        val sb = StringBuilder()
        when (type) {
            "recipe" -> {
                val ingredients = json.optJSONArray("ingredients")
                if (ingredients != null && ingredients.length() > 0) {
                    sb.append("Ingredients. ")
                    for (i in 0 until ingredients.length()) sb.append(ingredients.getString(i)).append(". ")
                }
                val steps = json.optJSONArray("steps")
                if (steps != null && steps.length() > 0) {
                    sb.append("Steps. ")
                    for (i in 0 until steps.length()) sb.append("Step ${i + 1}. ").append(steps.getString(i)).append(". ")
                }
            }
            "beauty" -> {
                val products = json.optJSONArray("products")
                if (products != null && products.length() > 0) {
                    sb.append("Products. ")
                    for (i in 0 until products.length()) sb.append(products.getString(i)).append(". ")
                }
                val steps = json.optJSONArray("steps")
                if (steps != null && steps.length() > 0) {
                    sb.append("Steps. ")
                    for (i in 0 until steps.length()) sb.append("Step ${i + 1}. ").append(steps.getString(i)).append(". ")
                }
            }
            else -> {
                val keyPoints = json.optJSONArray("keyPoints")
                if (keyPoints != null && keyPoints.length() > 0) {
                    sb.append("Key points. ")
                    for (i in 0 until keyPoints.length()) sb.append(keyPoints.getString(i)).append(". ")
                }
            }
        }
        if (sb.isEmpty()) fallback else sb.toString()
    } catch (e: Exception) {
        fallback
    }
}
