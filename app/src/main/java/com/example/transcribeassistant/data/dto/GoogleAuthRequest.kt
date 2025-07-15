package com.example.transcribeassistant.data.dto

import com.squareup.moshi.Json

data class GoogleAuthRequest(
    @field:Json(name = "idToken") val idToken: String
) 