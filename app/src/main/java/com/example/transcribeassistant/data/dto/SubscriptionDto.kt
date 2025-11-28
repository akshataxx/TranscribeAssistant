package com.example.transcribeassistant.data.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.Instant
import java.util.UUID

@JsonClass(generateAdapter = true)
data class SubscriptionDto(
    @Json(name = "id")
    val id: UUID,
    
    @Json(name = "userId")
    val userId: UUID,
    
    @Json(name = "subscriptionType")
    val subscriptionType: String,
    
    @Json(name = "status")
    val status: String,
    
    @Json(name = "subscriptionStartDate")
    val subscriptionStartDate: Instant,
    
    @Json(name = "subscriptionEndDate")
    val subscriptionEndDate: Instant?,
    
    @Json(name = "autoRenew")
    val autoRenew: Boolean,
    
    @Json(name = "isPremium")
    val isPremium: Boolean
)