package com.example.transcribeassistant.data.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CategoryWithSubcategoriesDto(
    @Json(name = "id")             val id: String,
    @Json(name = "name")           val name: String,
    @Json(name = "description")    val description: String? = null,
    @Json(name = "subcategories")  val subcategories: List<SubcategoryDto> = emptyList()
)
