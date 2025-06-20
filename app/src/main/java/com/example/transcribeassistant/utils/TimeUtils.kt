package com.example.transcribeassistant.utils

import android.annotation.SuppressLint
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

object TimeUtils {

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
