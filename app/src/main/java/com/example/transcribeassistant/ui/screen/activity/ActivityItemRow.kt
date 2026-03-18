package com.example.transcribeassistant.ui.screen.activity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.transcribeassistant.domain.model.ActivityItem
import com.example.transcribeassistant.domain.model.ActivityStatus
import com.example.transcribeassistant.ui.screen.components.PrimaryText
import com.example.transcribeassistant.ui.screen.components.ScoopPurple
import com.example.transcribeassistant.ui.screen.components.SecondaryText

private val ActivityStatus.icon: ImageVector
    get() = when (this) {
        ActivityStatus.PENDING    -> Icons.Default.AccessTime
        ActivityStatus.PROCESSING -> Icons.Default.Sync
        ActivityStatus.COMPLETED  -> Icons.Default.CheckCircle
        ActivityStatus.FAILED     -> Icons.Default.Error
    }

private val ActivityStatus.color: Color
    get() = when (this) {
        ActivityStatus.PENDING    -> Color(0xFF9CA3AF)   // gray
        ActivityStatus.PROCESSING -> Color(0xFF3B82F6)   // blue
        ActivityStatus.COMPLETED  -> Color(0xFF10B981)   // green
        ActivityStatus.FAILED     -> Color(0xFFEF4444)   // red
    }

@Composable
fun ActivityItemRow(
    item: ActivityItem,
    isNew: Boolean = false,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Status icon
        Icon(
            imageVector = item.status.icon,
            contentDescription = item.status.displayName,
            tint = item.status.color,
            modifier = Modifier.size(22.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Title + status row
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = item.displayTitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = PrimaryText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (isNew) {
                    Spacer(modifier = Modifier.width(6.dp))
                    NewBadge()
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusPill(item.status)
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = item.relativeTimestamp,
                    style = MaterialTheme.typography.labelSmall,
                    color = SecondaryText
                )
            }

            // Error message for failed jobs
            if (item.status == ActivityStatus.FAILED && !item.errorMessage.isNullOrBlank()) {
                Text(
                    text = item.errorMessage,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFEF4444).copy(alpha = 0.8f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Chevron only for completed (tappable) items
        if (item.status == ActivityStatus.COMPLETED) {
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = SecondaryText.copy(alpha = 0.6f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun StatusPill(status: ActivityStatus) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = status.color.copy(alpha = 0.12f)
    ) {
        Text(
            text = status.displayName,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = status.color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
private fun NewBadge() {
    Surface(
        shape = RoundedCornerShape(50),
        color = ScoopPurple
    ) {
        Text(
            text = "NEW",
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}
