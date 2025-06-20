package com.example.transcribeassistant.domain.repository

import com.example.transcribeassistant.domain.model.Transcript

/**
 * Interface for managing transcripts.
 * This interface defines methods for fetching transcripts from a video URL and retrieving cached transcripts.
 * Implementation of this interface(data.repository.TranscriptRepositoryImpl) should handle the actual data fetching and caching logic.
 */
interface TranscriptRepository {
    suspend fun getTranscript(videoUrl: String): Transcript

    suspend fun getAllTranscripts(
        id: String? = null,
        categories: List<String>? = null,
        account: String? = null,
        from: String? = null, // ISO 8601 string
        to: String? = null      // ISO 8601 string
    ): List<Transcript>

    suspend fun getCachedTranscripts(): List<Transcript>


}