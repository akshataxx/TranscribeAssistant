package com.example.transcribeassistant.data.repository

import android.util.Log
import com.example.transcribeassistant.data.dto.GooglePlayPurchaseVerificationRequest
import com.example.transcribeassistant.data.network.SubscriptionApi
import com.example.transcribeassistant.domain.model.UsageInfo
import com.example.transcribeassistant.domain.repository.SubscriptionRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubscriptionRepositoryImpl @Inject constructor(
    private val subscriptionApi: SubscriptionApi
) : SubscriptionRepository {

    override suspend fun getUsageInfo(): UsageInfo {
        val dto = subscriptionApi.getUsageInfo()
        Log.d("SubscriptionRepo", "Received usage DTO: isPremium=${dto.isPremium}, remaining=${dto.remainingFreeTranscriptions}")
        return UsageInfo(
            isPremium = dto.isPremium,
            remainingFreeTranscriptions = dto.remainingFreeTranscriptions,
            totalFreeTranscriptions = dto.totalFreeTranscriptions
        )
    }

    override suspend fun verifyPurchase(productId: String, purchaseToken: String, orderId: String?): Boolean {
        val request = GooglePlayPurchaseVerificationRequest(
            productId = productId,
            purchaseToken = purchaseToken,
            orderId = orderId
        )
        val response = subscriptionApi.verifyGooglePlayPurchase(request)
        Log.d("SubscriptionRepo", "Google Play verification: verified=${response.verified}, active=${response.subscriptionActive}")

        if (!response.verified) {
            Log.w("SubscriptionRepo", "Verification failed: ${response.errorMessage}")
        }

        return response.verified && response.subscriptionActive
    }

    override suspend fun cancelSubscription() {
        subscriptionApi.cancelSubscription()
    }
}
