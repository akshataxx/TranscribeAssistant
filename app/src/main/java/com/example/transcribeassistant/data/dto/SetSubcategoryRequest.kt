package com.example.transcribeassistant.data.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SetSubcategoryRequest(
    @Json(name = "subcategoryId") val subcategoryId: String
)
