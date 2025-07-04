package com.example.transcribeassistant.domain.repository

import com.example.transcribeassistant.domain.model.Transcript
import java.time.Instant

/**
 * Interface for managing transcripts.
 * This interface defines methods for fetching transcripts from a video URL and retrieving cached transcripts.
 * Implementation of this interface(data.repository.TranscriptRepositoryImpl) should handle the actual data fetching and caching logic.
 */
interface TranscriptRepository {
    suspend fun transcribeVideo(videoUrl: String, userId: String): Transcript

    suspend fun getAllTranscripts(
        categories: List<String>? = null,
        account: String? = null,
        from: Instant? = null,
        to: Instant? = null,
        userId: String? = null
    ): List<Transcript>

    suspend fun getCachedTranscripts(): List<Transcript>

    suspend fun getTranscriptById(id: String, userId: String?): Transcript

    suspend fun upsertAlias(userId: String, categoryId: String, newAlias: String)
}
