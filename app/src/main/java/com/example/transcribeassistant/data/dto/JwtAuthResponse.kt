package com.example.transcribeassistant.data.dto

import com.squareup.moshi.Json

data class JwtAuthResponse(
    @field:Json(name = "accessToken") val accessToken: String,
    @field:Json(name = "tokenType") val tokenType: String
) 