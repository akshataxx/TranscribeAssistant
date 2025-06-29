package com.example.transcribeassistant.ui.screen.feed

import androidx.compose.foundation.clickable
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
import com.example.transcribeassistant.ui.screen.components.CategoryChip
import com.example.transcribeassistant.utils.TimeUtils

@Composable
fun TranscriptCard(
    transcript: Transcript,
    onClick: () -> Unit,
    modifier: Modifier = Modifier) {
    Surface(
        modifier = Modifier.fillMaxWidth()
            .clickable (onClick = onClick),
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

            // TODO: add source to Transcript model
            Text("Source TikTok • @${transcript.account}", style = MaterialTheme.typography.bodySmall)
            Text("⏱ ${TimeUtils.formatDuration(transcript.duration)}   •   ${TimeUtils.timeAgo(transcript.uploadedAt)}", style = MaterialTheme.typography.bodySmall)


            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = transcript.transcript.take(100) + "...",
                fontStyle = FontStyle.Italic,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CategoryChip(transcript.category)
                if (!transcript.alias.isNullOrEmpty()) CategoryChip(transcript.alias)
            }
        }
    }
}
