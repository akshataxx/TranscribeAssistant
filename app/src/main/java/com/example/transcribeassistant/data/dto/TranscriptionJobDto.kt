package com.example.transcribeassistant.data.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TranscriptionJobDto(
    val id: String,
    val videoUrl: String,
    val status: String,
    val errorMessage: String?,
    val retryCount: Int,
    val updatedAt: String,
    val baseTranscriptId: String?,
    val userTranscriptId: String?
)

@JsonClass(generateAdapter = true)
data class JobListResponseDto(
    val jobs: List<TranscriptionJobDto>,
    val page: Int,
    val size: Int,
    val totalElements: Int,
    val totalPages: Int
)

@JsonClass(generateAdapter = true)
data class JobSubmissionRequest(
    val videoUrl: String
)

@JsonClass(generateAdapter = true)
data class JobSubmissionResponseDto(
    val jobId: String,
    val status: String
)
