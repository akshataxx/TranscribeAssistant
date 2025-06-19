package com.example.transcribeassistant.data.repository

import com.example.transcribeassistant.data.cache.dao.TranscriptDao
import com.example.transcribeassistant.data.network.TranscriptApi
import com.example.transcribeassistant.domain.model.Transcript
import com.example.transcribeassistant.domain.mapper.toDomain
import com.example.transcribeassistant.domain.mapper.toEntity
import com.example.transcribeassistant.domain.repository.TranscriptRepository

/**
 * Repository implementation for managing transcripts.
 * This class handles fetching transcripts from the API and caching them in the local database.
 * * @property api The API service for fetching transcripts.
 * * @property dao The DAO for accessing cached transcripts.
 * * This class implements the [TranscriptRepository] interface.
 */
class TranscriptRepositoryImpl (
    private val api: TranscriptApi,
    private val dao: TranscriptDao
): TranscriptRepository {

    override suspend fun getTranscript(videoUrl: String): Transcript {
        val dto = api.getTranscriptFromVideo(mapOf("videoUrl" to videoUrl))
        val model = dto.toDomain()
        model.toEntity()?.let { dao.insert(it) } //cache it
        return model
    }

    override suspend fun getCachedTranscripts(): List<Transcript>{
        return dao.getAll().map{it.toDomain()}
    }
}