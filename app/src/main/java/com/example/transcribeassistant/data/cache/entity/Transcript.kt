package com.example.transcribeassistant.data.cache.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

/**
 * Entity class representing a transcript in the local database.
 * This class is used to define the structure of the transcript table in the Room database.
 */
@Entity(tableName = "transcripts")
data class TranscriptEntity(
    @PrimaryKey val id: String,
    val videoUrl: String,
    val transcript: String,
    val description: String,
    val title: String,
    val duration: Double,
    val uploadedAt: Instant,
    val accountId: String,
    val account: String,
    val identifierId: String,
    val identifier: String,
    val categoryId: String,
    val category: String,
    val alias: String?,
    val createdAt: Instant
)
