package com.example.transcribeassistant.data.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StripeCheckoutResponse(
    @Json(name = "checkoutUrl")
    val checkoutUrl: String,

    @Json(name = "sessionId")
    val sessionId: String
)