package com.example.transcribeassistant.data.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DeviceRegistrationResponse(
    val id: String,
    val registered: Boolean
)
