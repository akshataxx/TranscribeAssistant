package com.example.transcribeassistant.data.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UpdateNotesRequest(val notes: String?)
