package com.example.transcribeassistant.utils

import android.annotation.SuppressLint
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

object TimeUtils {

    fun platformFromUrl(url: String): String {
        val lower = url.lowercase()
        return when {
            "tiktok.com" in lower         -> "TikTok"
            "youtube.com" in lower ||
            "youtu.be" in lower           -> "YouTube"
            "instagram.com" in lower      -> "Instagram"
            "twitter.com" in lower ||
            "x.com" in lower              -> "X"
            "facebook.com" in lower       -> "Facebook"
            "reddit.com" in lower         -> "Reddit"
            else                          -> "Web"
        }
    }

    fun timeAgo(instant: Instant): String {
        val today = LocalDate.now()
        val date = instant.atZone(ZoneId.systemDefault()).toLocalDate()

        val days = ChronoUnit.DAYS.between(date, today)
        val months = ChronoUnit.MONTHS.between(date, today)
        val years = ChronoUnit.YEARS.between(date, today)

        return when {
            days < 30 -> "$days day${if (days == 1L) "" else "s"} ago"
            months < 12 -> "$months month${if (months == 1L) "" else "s"} ago"
            else -> "$years year${if (years == 1L) "" else "s"} ago"
        }
    }

    @SuppressLint("DefaultLocale")
    fun formatDuration(seconds: Int): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }
}
