package com.example.transcribeassistant.ui.screen.feed

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.example.transcribeassistant.domain.model.Transcript
import com.example.transcribeassistant.ui.screen.components.CategoryChip
import com.example.transcribeassistant.ui.screen.components.PlatformLabel
import com.example.transcribeassistant.ui.screen.components.ScoopPurple
import com.example.transcribeassistant.ui.screen.components.SecondaryText
import com.example.transcribeassistant.utils.TimeUtils

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TranscriptCard(
    transcript: Transcript,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: () -> Unit = {},
    isSelectionMode: Boolean = false,
    isSelected: Boolean = false,
    enabled: Boolean = true
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                enabled = enabled,
                onClick = onClick,
                onLongClick = onLongClick
            ),
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 4.dp,
        color = Color.White.copy(alpha = if (isSelectionMode && !isSelected) 0.82f else 0.9f),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 0.dp,
            color = if (isSelected) ScoopPurple else Color.Transparent
        )
    ) {
        Box {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = transcript.generatedTitle ?: transcript.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    modifier = if (isSelectionMode) Modifier.padding(end = 36.dp) else Modifier
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    PlatformLabel(platformRaw = transcript.platform, fontSize = 12.sp)
                    if (!transcript.account.isNullOrEmpty()) {
                        Text(
                            text = " • @${transcript.account}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                Text("⏱ ${TimeUtils.formatDuration(transcript.duration.toInt())}   •   ${TimeUtils.timeAgo(transcript.uploadedAt)}", style = MaterialTheme.typography.bodySmall)

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = transcript.transcript.take(100) + "...",
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CategoryChip(transcript.category)
                    if (!transcript.alias.isNullOrEmpty()) {
                        CategoryChip(transcript.alias)
                    }
                }
            }

            if (isSelectionMode) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = null,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(28.dp),
                    colors = CheckboxDefaults.colors(
                        checkedColor = ScoopPurple,
                        uncheckedColor = SecondaryText.copy(alpha = 0.7f),
                        checkmarkColor = Color.White
                    )
                )
            }
        }
    }
}
