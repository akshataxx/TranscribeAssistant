package com.example.transcribeassistant.data.network

import com.example.transcribeassistant.data.dto.UpgradeSubscriptionRequest
import com.example.transcribeassistant.data.dto.UsageInfoDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * API interface for subscription management
 */
interface SubscriptionApi {
    
    @GET("api/subscription/usage")
    suspend fun getUsageInfo(): UsageInfoDto
    
    @POST("api/subscription/upgrade")
    suspend fun upgradeSubscription(@Body request: UpgradeSubscriptionRequest)
    
    @POST("api/subscription/cancel")
    suspend fun cancelSubscription()
}
