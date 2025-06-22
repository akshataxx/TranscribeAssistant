package com.example.transcribeassistant.domain.model

import java.time.Instant

/**
 * Data class representing a transcript of a video.
 * This class contains all the necessary information about a video transcript,
 * It is part of the business logic layer of the application.
 */
data class Transcript (
    val id: String,
    val videoUrl: String,
    val transcript: String,
    val description: String,
    val title: String,
    val duration: Int,
    val uploadedAt: Instant,
    val accountId: String,
    val account: String,
    val identifierId: String,
    val identifier: String,
    val categories: List<String>?, // nullable
    val createdAt: Instant
)
