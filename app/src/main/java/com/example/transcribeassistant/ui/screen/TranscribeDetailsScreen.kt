package com.example.transcribeassistant.ui.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun TranscribeDetailsScreen(transcriptId: String) {
    // TODO: Replace with actual ViewModel + Retrofit later
    val mockTranscript = remember {
        TranscriptUIModel(
            title = "How to Make Pasta",
            videoUrl = "https://www.tiktok.com/@chef123/video/123456789",
            fullTranscript = """
                Step 1: Boil water. 
                Step 2: Add pasta. 
                Step 3: Cook until al dente. 
                Step 4: Drain and serve with sauce.
            """.trimIndent()
        )
    }

    val context = LocalContext.current
    val scroll = rememberScrollState()
    var notes by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(scroll)
    ) {
        Text(
            text = mockTranscript.title,
            style = MaterialTheme.typography.headlineSmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = mockTranscript.videoUrl,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(mockTranscript.videoUrl))
                context.startActivity(intent)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = mockTranscript.fullTranscript,
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                // TODO: Add Text-to-Speech integration
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Read Aloud")
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
}

data class TranscriptUIModel(
    val title: String,
    val videoUrl: String,
    val fullTranscript: String
)