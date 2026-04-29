package com.example.transcribeassistant.data.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SubcategoryDto(
    @Json(name = "id")          val id: String,
    @Json(name = "parentId")    val parentId: String,
    @Json(name = "name")        val name: String,
    @Json(name = "description") val description: String? = null
)
