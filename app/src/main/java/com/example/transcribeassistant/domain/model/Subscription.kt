package com.example.transcribeassistant.domain.model

import java.time.Instant
import java.util.UUID

data class Subscription(
    val id: UUID,
    val userId: UUID,
    val subscriptionType: SubscriptionType,
    val status: SubscriptionStatus,
    val subscriptionStartDate: Instant,
    val subscriptionEndDate: Instant?,
    val autoRenew: Boolean,
    val isPremium: Boolean
)

enum class SubscriptionType {
    FREE,
    PREMIUM_MONTHLY,
    PREMIUM_YEARLY
}

enum class SubscriptionStatus {
    ACTIVE,
    CANCELLED,
    EXPIRED,
    PENDING
}