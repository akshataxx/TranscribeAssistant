package com.example.transcribeassistant.domain.model

data class Transcript (
    val id: String,
    val videoUrl: String,
    val transcript: String,
    val description: String,
    val title: String,
    val duration: Int,
    val uploadedAt: String,
    val accountId: String,
    val account: String,
    val identifierId: String,
    val identifier: String,
    val categories: List<String>?, // nullable
    val createdAt: String
)