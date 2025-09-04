package com.example.transcribeassistant.domain.mapper

import com.example.transcribeassistant.data.dto.SubscriptionDto
import com.example.transcribeassistant.data.dto.UsageInfoDto
import com.example.transcribeassistant.domain.model.Subscription
import com.example.transcribeassistant.domain.model.SubscriptionStatus
import com.example.transcribeassistant.domain.model.SubscriptionType
import com.example.transcribeassistant.domain.model.UsageInfo

/**
 * Extension functions to map subscription DTOs to domain models
 */

fun SubscriptionDto.toDomain(): Subscription {
    return Subscription(
        id = id,
        userId = userId,
        subscriptionType = SubscriptionType.valueOf(subscriptionType),
        status = SubscriptionStatus.valueOf(status),
        subscriptionStartDate = subscriptionStartDate,
        subscriptionEndDate = subscriptionEndDate,
        autoRenew = autoRenew,
        isPremium = isPremium
    )
}

fun UsageInfoDto.toDomain(): UsageInfo {
    return UsageInfo(
        isPremium = isPremium,
        remainingFreeTranscriptions = remainingFreeTranscriptions
    )
}