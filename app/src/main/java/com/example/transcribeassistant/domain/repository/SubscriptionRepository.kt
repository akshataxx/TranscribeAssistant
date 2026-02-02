package com.example.transcribeassistant.domain.repository

import com.example.transcribeassistant.domain.model.UsageInfo

/**
 * Repository interface for subscription management
 */
interface SubscriptionRepository {

    suspend fun getUsageInfo(): UsageInfo

    /**
     * Verify a Google Play purchase with the backend
     *
     * @param productId The product ID (e.g., "premium_monthly")
     * @param purchaseToken The purchase token from Google Play
     * @param orderId The order ID from Google Play (optional)
     * @return true if verification succeeded and subscription is active
     */
    suspend fun verifyPurchase(productId: String, purchaseToken: String, orderId: String?): Boolean

    suspend fun cancelSubscription()
}
