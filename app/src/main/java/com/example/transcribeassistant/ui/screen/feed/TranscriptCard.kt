package com.example.transcribeassistant.ui.screen.feed

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.example.transcribeassistant.domain.model.Transcript

@Composable
fun TranscriptCard(transcript: Transcript) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 4.dp,
        color = Color.White.copy(alpha = 0.9f)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = transcript.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(4.dp))

            // TODO
            Text("Source TikTok • @${transcript.account}", style = MaterialTheme.typography.bodySmall)
            Text("⏱ ${transcript.duration}   •   ${transcript.createdAt}", style = MaterialTheme.typography.bodySmall)


            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = transcript.transcript.take(100) + "...",
                fontStyle = FontStyle.Italic,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                transcript.categories?.forEach { category ->
                    CategoryChip("• $category")
                }
            }
        }
    }
}
