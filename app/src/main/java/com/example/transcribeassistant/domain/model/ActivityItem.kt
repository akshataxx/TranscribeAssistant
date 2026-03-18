package com.example.transcribeassistant.domain.model

import androidx.compose.ui.graphics.Color
import java.time.Instant
import java.time.format.DateTimeParseException

enum class ActivityStatus(val raw: String) {
    PENDING("PENDING"),
    PROCESSING("PROCESSING"),
    COMPLETED("COMPLETED"),
    FAILED("FAILED");

    val displayName: String get() = when (this) {
        PENDING    -> "Queued"
        PROCESSING -> "Processing"
        COMPLETED  -> "Ready"
        FAILED     -> "Failed"
    }

    companion object {
        fun from(value: String): ActivityStatus? = entries.firstOrNull { it.raw == value }
    }
}

data class ActivityItem(
    val id: String,
    val videoUrl: String,
    val status: ActivityStatus,
    val baseTranscriptId: String?,
    val userTranscriptId: String?,
    val errorMessage: String?,
    val retryCount: Int,
    val updatedAt: String   // ISO 8601
) {
    /** Last path component of the URL, falling back to host, then raw URL. */
    val displayTitle: String get() {
        return try {
            val url = java.net.URL(videoUrl)
            val path = url.path?.trimEnd('/')
            if (!path.isNullOrEmpty()) {
                val segment = path.substringAfterLast('/')
                if (segment.isNotEmpty()) return segment
            }
            url.host ?: videoUrl
        } catch (e: Exception) {
            videoUrl
        }
    }

    /** Human-readable relative time (e.g. "2 min ago"). */
    val relativeTimestamp: String get() {
        return try {
            val updated = Instant.parse(updatedAt)
            val now = Instant.now()
            val seconds = now.epochSecond - updated.epochSecond
            when {
                seconds < 60   -> "Just now"
                seconds < 3600 -> "${seconds / 60} min ago"
                seconds < 86400 -> {
                    val h = seconds / 3600
                    "$h hour${if (h == 1L) "" else "s"} ago"
                }
                else -> {
                    val d = seconds / 86400
                    "$d day${if (d == 1L) "" else "s"} ago"
                }
            }
        } catch (e: DateTimeParseException) {
            ""
        }
    }
}
