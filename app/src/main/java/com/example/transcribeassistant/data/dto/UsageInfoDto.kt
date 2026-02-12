package com.example.transcribeassistant.data.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UsageInfoDto(
    @Json(name = "isPremium")
    val isPremium: Boolean,

    @Json(name = "remainingFreeTranscriptions")
    val remainingFreeTranscriptions: Int,

    @Json(name = "totalFreeTranscriptions")
    val totalFreeTranscriptions: Int = 10
)