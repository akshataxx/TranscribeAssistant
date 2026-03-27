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
    /** Platform-based display title (e.g. "TikTok Video") matching iOS behaviour. */
    val displayTitle: String get() {
        val lower = videoUrl.lowercase()
        return when {
            "tiktok.com" in lower -> "TikTok Video"
            "youtube.com" in lower || "youtu.be" in lower -> "YouTube Video"
            "instagram.com" in lower -> "Instagram Video"
            "twitter.com" in lower || "x.com" in lower -> "X Video"
            "facebook.com" in lower -> "Facebook Video"
            "reddit.com" in lower -> "Reddit Video"
            else -> try {
                java.net.URL(videoUrl).host ?: "Video"
            } catch (e: Exception) {
                "Video"
            }
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
