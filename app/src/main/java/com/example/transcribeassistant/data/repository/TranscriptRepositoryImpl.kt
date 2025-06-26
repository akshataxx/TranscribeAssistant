package com.example.transcribeassistant.data.repository

import com.example.transcribeassistant.data.cache.dao.TranscriptDao
import com.example.transcribeassistant.data.network.TranscriptApi
import com.example.transcribeassistant.domain.model.Transcript
import com.example.transcribeassistant.domain.mapper.toDomain
import com.example.transcribeassistant.domain.mapper.toEntity
import com.example.transcribeassistant.domain.repository.TranscriptRepository
import java.time.Instant

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

    override suspend fun getAllTranscripts(
        id: String?,
        categories: List<String>?,
        account: String?,
        from: Instant?,
        to: Instant?
    ): List<Transcript> {
        val dtoList = api.getAllTranscripts(id, categories, account, from, to)
        val modelList = dtoList.map { it.toDomain() }
        modelList.forEach { it.toEntity()?.let { entity -> dao.insert(entity) } }
        return modelList
    }

    override suspend fun getCachedTranscripts(): List<Transcript>{
        return dao.getAll().map{it.toDomain()}
    }

    override suspend fun getTranscriptById(transcriptId: String): Transcript {
        // First try to fetch from cache
        dao.getById(transcriptId)?.let{
            return it.toDomain()
        }
        val dto = api.getTranscriptById(transcriptId)
        val model = dto.toDomain()
        model.toEntity()?.let { dao.insert(it) } // cache it
        return model
    }
}
