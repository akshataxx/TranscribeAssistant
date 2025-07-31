package com.example.transcribeassistant.data.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CategoryAliasDto(
    @Json(name = "id")         val id: String,
    @Json(name = "userId")     val userId: String,
    @Json(name = "categoryId") val categoryId: String,
    @Json(name = "alias")      val alias: String
)