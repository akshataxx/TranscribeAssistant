package com.example.transcribeassistant.ui.screen.feed

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.transcribeassistant.domain.model.Transcript
import com.example.transcribeassistant.ui.screen.components.CategoryChip
import com.example.transcribeassistant.ui.screen.components.PlatformLabel
import com.example.transcribeassistant.ui.screen.components.PrimaryText
import com.example.transcribeassistant.ui.screen.components.ScoopPurple
import com.example.transcribeassistant.ui.screen.components.SecondaryText
import com.example.transcribeassistant.utils.TimeUtils

@Composable
fun SubcategoryPill(label: String) {
    Surface(
        color = Color(0xFFE0F7FA),
        shape = RoundedCornerShape(9999.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = PrimaryText
        )
    }
}

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
    val cardAlpha = when {
        isSelectionMode && !isSelected -> 0.92f
        else -> 0.9f
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = Color.Black.copy(alpha = 0.05f),
                spotColor = Color.Black.copy(alpha = 0.05f)
            )
            .combinedClickable(
                enabled = enabled,
                onClick = onClick,
                onLongClick = onLongClick
            ),
        shape = RoundedCornerShape(12.dp),
        color = Color.White.copy(alpha = cardAlpha),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 0.dp,
            color = if (isSelected) ScoopPurple else Color.Transparent
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Title row — checkbox sits inline at top-right (matches iOS HStack alignment: .top)
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = transcript.generatedTitle ?: transcript.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText,
                    maxLines = 2,
                    lineHeight = 24.sp,
                    modifier = Modifier.weight(1f)
                )
                if (isSelectionMode) {
                    Icon(
                        imageVector = if (isSelected) Icons.Filled.CheckCircle else Icons.Outlined.Circle,
                        contentDescription = null,
                        tint = if (isSelected) ScoopPurple else SecondaryText.copy(alpha = 0.7f),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Source info: platform · @account
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PlatformLabel(platformRaw = transcript.platform, fontSize = 12.sp)
                if (!transcript.account.isNullOrEmpty()) {
                    Text(
                        text = "@${transcript.account}",
                        fontSize = 12.sp,
                        color = SecondaryText
                    )
                }
            }

            // Duration • time ago
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "⏱ ${TimeUtils.formatDuration(transcript.duration.toInt())}",
                    fontSize = 12.sp,
                    color = SecondaryText
                )
                Text(text = "•", fontSize = 12.sp, color = SecondaryText)
                Text(
                    text = TimeUtils.timeAgo(transcript.uploadedAt),
                    fontSize = 12.sp,
                    color = SecondaryText
                )
            }

            // Content preview
            Text(
                text = transcript.transcript.take(100) + "…",
                fontSize = 14.sp,
                color = SecondaryText,
                fontStyle = FontStyle.Italic,
                maxLines = 2,
                lineHeight = 20.sp
            )

            // Chips — trailing Spacer pushes chips left (matches iOS HStack + Spacer())
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CategoryChip(transcript.category)
                if (!transcript.alias.isNullOrEmpty()) {
                    CategoryChip(transcript.alias)
                }
                if (!transcript.subcategoryName.isNullOrEmpty()) {
                    SubcategoryPill(transcript.subcategoryName)
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}
