package com.example.transcribeassistant.data.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RenameAliasRequest(
    @Json(name = "userId")     val userId: String,
    @Json(name = "categoryId") val categoryId: String,
    @Json(name = "newAlias")   val newAlias: String
)