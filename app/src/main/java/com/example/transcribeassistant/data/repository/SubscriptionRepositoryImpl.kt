package com.example.transcribeassistant.data.repository

import com.example.transcribeassistant.data.dto.UpgradeSubscriptionRequest
import com.example.transcribeassistant.data.network.SubscriptionApi
import com.example.transcribeassistant.domain.mapper.toDomain
import com.example.transcribeassistant.domain.model.Subscription
import com.example.transcribeassistant.domain.model.UsageInfo
import com.example.transcribeassistant.domain.repository.SubscriptionRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubscriptionRepositoryImpl @Inject constructor(
    private val subscriptionApi: SubscriptionApi
) : SubscriptionRepository {
    
    override suspend fun getSubscriptionStatus(): Subscription? {
        return subscriptionApi.getSubscriptionStatus()?.toDomain()
    }
    
    override suspend fun getUsageInfo(): UsageInfo {
        return subscriptionApi.getUsageInfo().toDomain()
    }
    
    override suspend fun upgradeSubscription(purchaseToken: String, productId: String): Subscription {
        val request = UpgradeSubscriptionRequest(purchaseToken, productId)
        return subscriptionApi.upgradeSubscription(request).toDomain()
    }
    
    override suspend fun cancelSubscription() {
        subscriptionApi.cancelSubscription()
    }
}