package com.example.transcribeassistant.domain.model

data class UsageInfo(
    val isPremium: Boolean,
    val remainingFreeTranscriptions: Int,
    val totalFreeTranscriptions: Int = 10
) {
    val hasReachedFreeLimit: Boolean
        get() = !isPremium && remainingFreeTranscriptions <= 0

    val usedTranscriptions: Int
        get() = maxOf(0, totalFreeTranscriptions - remainingFreeTranscriptions)

    val usageProgress: Double
        get() = if (totalFreeTranscriptions > 0) usedTranscriptions.toDouble() / totalFreeTranscriptions else 0.0

    val usageMessage: String
        get() = when {
            isPremium -> "Premium - Unlimited transcriptions"
            remainingFreeTranscriptions > 0 -> "$remainingFreeTranscriptions free transcriptions remaining"
            else -> "Free transcriptions exhausted - Upgrade to Premium"
        }
}
