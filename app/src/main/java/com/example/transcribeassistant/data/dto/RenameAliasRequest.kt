package com.example.transcribeassistant.data.dto

import com.google.gson.annotations.SerializedName

data class RenameAliasRequest(
    @SerializedName("userId") val userId: String,
    @SerializedName("categoryId") val categoryId: String,
    @SerializedName("newAlias") val newAlias: String
) 