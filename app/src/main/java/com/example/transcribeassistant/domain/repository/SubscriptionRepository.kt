package com.example.transcribeassistant.domain.repository

import com.example.transcribeassistant.domain.model.UsageInfo

/**
 * Repository interface for subscription management
 */
interface SubscriptionRepository {
    
    suspend fun getUsageInfo(): UsageInfo
    
    suspend fun upgradeSubscription(purchaseToken: String, productId: String)
    
    suspend fun cancelSubscription()
}
