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
    val updatedAt: String,   // ISO 8601
    val title: String? = null,
    val platform: String? = null
) {
    /**
     * Priority: transcript title (from backend join) → platform field → friendly domain label → URL fallback.
     */
    val displayTitle: String get() {
        if (!title.isNullOrBlank()) return title

        if (!platform.isNullOrBlank()) {
            val label = when (platform.uppercase()) {
                "YOUTUBE"     -> "YouTube video"
                "TIKTOK"      -> "TikTok video"
                "INSTAGRAM"   -> "Instagram video"
                "VIMEO"       -> "Vimeo video"
                "TWITTER"     -> "X post"
                "FACEBOOK"    -> "Facebook video"
                "REDDIT"      -> "Reddit post"
                "TWITCH"      -> "Twitch clip"
                "DAILYMOTION" -> "Dailymotion video"
                else          -> null
            }
            if (label != null) return label
        }

        val lower = videoUrl.lowercase()
        return when {
            "tiktok.com" in lower -> "TikTok video"
            "youtube.com" in lower || "youtu.be" in lower -> "YouTube video"
            "instagram.com" in lower -> "Instagram video"
            "twitter.com" in lower || "x.com" in lower -> "X post"
            "facebook.com" in lower -> "Facebook video"
            "reddit.com" in lower -> "Reddit post"
            "vimeo.com" in lower -> "Vimeo video"
            "twitch.tv" in lower -> "Twitch clip"
            else -> try {
                java.net.URL(videoUrl).host?.removePrefix("www.") ?: "Video"
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
