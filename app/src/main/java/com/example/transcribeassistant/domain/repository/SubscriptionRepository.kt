package com.example.transcribeassistant.domain.repository

import com.example.transcribeassistant.domain.model.Subscription
import com.example.transcribeassistant.domain.model.UsageInfo

/**
 * Repository interface for subscription management
 */
interface SubscriptionRepository {
    
    suspend fun getSubscriptionStatus(): Subscription?
    
    suspend fun getUsageInfo(): UsageInfo
    
    suspend fun upgradeSubscription(purchaseToken: String, productId: String): Subscription
    
    suspend fun cancelSubscription()
}