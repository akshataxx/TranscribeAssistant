package com.example.transcribeassistant.ui.screen.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class VideoPlatform(val raw: String) {
    YOUTUBE("YOUTUBE"),
    TIKTOK("TIKTOK"),
    INSTAGRAM("INSTAGRAM"),
    VIMEO("VIMEO"),
    TWITTER("TWITTER"),
    FACEBOOK("FACEBOOK"),
    REDDIT("REDDIT"),
    TWITCH("TWITCH"),
    DAILYMOTION("DAILYMOTION"),
    UNKNOWN("UNKNOWN");

    val displayName: String get() = when (this) {
        YOUTUBE     -> "YouTube"
        TIKTOK      -> "TikTok"
        INSTAGRAM   -> "Instagram"
        VIMEO       -> "Vimeo"
        TWITTER     -> "X"
        FACEBOOK    -> "Facebook"
        REDDIT      -> "Reddit"
        TWITCH      -> "Twitch"
        DAILYMOTION -> "Dailymotion"
        UNKNOWN     -> "Web"
    }

    val brandColor: Color get() = when (this) {
        YOUTUBE     -> Color(0xFFFF0000)
        TIKTOK      -> Color(0xFF000000)
        INSTAGRAM   -> Color(0xFFE4405F)
        VIMEO       -> Color(0xFF1AB7EA)
        TWITTER     -> Color(0xFF000000)
        FACEBOOK    -> Color(0xFF1877F2)
        REDDIT      -> Color(0xFFFF4500)
        TWITCH      -> Color(0xFF9146FF)
        DAILYMOTION -> Color(0xFF00D2F3)
        UNKNOWN     -> SecondaryText
    }

    companion object {
        fun from(raw: String?): VideoPlatform {
            if (raw == null) return UNKNOWN
            return entries.firstOrNull { it.raw == raw.uppercase() } ?: UNKNOWN
        }
    }
}

/**
 * Renders the platform name in its brand color.
 * When platform icon assets (ic_youtube, ic_tiktok, etc.) are added to res/drawable,
 * this composable can be updated to show the image instead.
 */
@Composable
fun PlatformLabel(
    platform: VideoPlatform,
    fontSize: TextUnit = 12.sp,
    modifier: Modifier = Modifier
) {
    Text(
        text = platform.displayName,
        fontSize = fontSize,
        fontWeight = FontWeight.Medium,
        color = platform.brandColor,
        modifier = modifier
    )
}

/**
 * Convenience overload that accepts the raw backend platform string.
 */
@Composable
fun PlatformLabel(
    platformRaw: String?,
    fontSize: TextUnit = 12.sp,
    modifier: Modifier = Modifier
) {
    PlatformLabel(
        platform = VideoPlatform.from(platformRaw),
        fontSize = fontSize,
        modifier = modifier
    )
}
