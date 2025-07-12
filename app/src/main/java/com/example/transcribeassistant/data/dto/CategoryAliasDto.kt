package com.example.transcribeassistant.data.dto

import com.google.gson.annotations.SerializedName

data class CategoryAliasDto(
    @SerializedName("id") val id: String,
    @SerializedName("userId") val userId: String,
    @SerializedName("categoryId") val categoryId: String,
    @SerializedName("alias") val alias: String
) 