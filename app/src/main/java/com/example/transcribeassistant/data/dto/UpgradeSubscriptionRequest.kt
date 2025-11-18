package com.example.transcribeassistant.data.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UpgradeSubscriptionRequest(
    @Json(name = "purchaseToken")
    val purchaseToken: String,
    
    @Json(name = "productId")
    val productId: String
)