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
    val transcriptTitle: String?,
    val errorMessage: String?,
    val retryCount: Int,
    val updatedAt: String   // ISO 8601
) {
    /** Display title matching iOS logic:
     *  1. Backend transcript title if available
     *  2. Friendly label for known platforms
     *  3. Filename without extension for direct file URLs
     *  4. Cleaned-up domain
     *  5. Raw URL fallback
     */
    val displayTitle: String get() {
        if (!transcriptTitle.isNullOrEmpty()) return transcriptTitle
        return try {
            val url = java.net.URL(videoUrl)
            val host = url.host?.lowercase() ?: ""
            when {
                host.contains("youtube.com") || host.contains("youtu.be") -> "YouTube video"
                host.contains("tiktok.com") -> "TikTok video"
                host.contains("vimeo.com") -> "Vimeo video"
                host.contains("instagram.com") -> "Instagram video"
                host.contains("twitter.com") || host.contains("x.com") -> "X post"
                host.contains("reddit.com") -> "Reddit post"
                else -> {
                    val lastSegment = url.path?.trimEnd('/')?.substringAfterLast('/') ?: ""
                    if (lastSegment.isNotEmpty() && lastSegment.contains('.')) {
                        lastSegment.substringBeforeLast('.')
                    } else {
                        host.removePrefix("www.")
                    }
                }
            }
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
