package com.example.transcribeassistant.data.repository

import android.util.Log
import com.example.transcribeassistant.data.dto.StripeCheckoutRequest
import com.example.transcribeassistant.data.dto.UpgradeSubscriptionRequest
import com.example.transcribeassistant.data.dto.UsageInfoDto
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
            remainingFreeTranscriptions = dto.remainingFreeTranscriptions
        )
    }

    override suspend fun createStripeCheckout(priceId: String): String {
        val request = StripeCheckoutRequest(priceId)
        val response = subscriptionApi.createStripeCheckout(request)
        Log.d("SubscriptionRepo", "Created Stripe checkout: ${response.checkoutUrl}")
        return response.checkoutUrl
    }

    override suspend fun cancelSubscription() {
        subscriptionApi.cancelSubscription()
    }
}

