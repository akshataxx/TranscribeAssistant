package com.example.transcribeassistant.data.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.Instant

/**
 * Request DTO for verifying a Google Play purchase with the backend
 */
@JsonClass(generateAdapter = true)
data class GooglePlayPurchaseVerificationRequest(
    @Json(name = "productId")
    val productId: String,
    @Json(name = "purchaseToken")
    val purchaseToken: String,
    @Json(name = "orderId")
    val orderId: String?
)

/**
 * Response DTO from backend after verifying a Google Play purchase
 */
@JsonClass(generateAdapter = true)
data class GooglePlayVerificationResponse(
    @Json(name = "verified")
    val verified: Boolean,
    @Json(name = "subscriptionActive")
    val subscriptionActive: Boolean,
    @Json(name = "expirationTime")
    val expirationTime: Instant?,
    @Json(name = "errorMessage")
    val errorMessage: String?
)
