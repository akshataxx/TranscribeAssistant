package com.example.transcribeassistant.domain.model

data class UsageInfo(
    val isPremium: Boolean,
    val remainingFreeTranscriptions: Int
) {
    val hasReachedFreeLimit: Boolean
        get() = !isPremium && remainingFreeTranscriptions <= 0
        
    val usageMessage: String
        get() = when {
            isPremium -> "Premium - Unlimited transcriptions"
            remainingFreeTranscriptions > 0 -> "$remainingFreeTranscriptions free transcriptions remaining"
            else -> "Free transcriptions exhausted - Upgrade to Premium"
        }
}
