package com.example.transcribeassistant.data.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StripeCheckoutRequest(
    @Json(name = "priceId")
    val priceId: String
)