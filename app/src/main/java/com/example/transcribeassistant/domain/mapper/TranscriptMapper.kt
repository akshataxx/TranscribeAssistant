package com.example.transcribeassistant.domain.mapper

import com.example.transcribeassistant.data.cache.entity.TranscriptEntity
import com.example.transcribeassistant.data.dto.TranscriptDto
import com.example.transcribeassistant.domain.model.Transcript

/**
 * Mapper class for converting between TranscriptDto, TranscriptEntity, and Transcript domain model.
 * This class provides methods to map data transfer objects to domain models and vice versa.
 * It is used to separate the data layer from the domain layer, ensuring that the domain logic is focused on the domain model.
 */
    /**
     * Maps a TranscriptDto to a Transcript domain model.
     * @param dto The TranscriptDto to map.
     * @return The mapped Transcript domain model.
     */
    fun TranscriptDto.toDomain() = Transcript(
        id, videoUrl, transcript, description, title,
        duration, uploadedAt, accountId, account,
        identifierId, identifier, categories, createdAt
    )

    /**
     * Maps a Transcript domain model to a TranscriptEntity
     * for database storage.
     * @param domain The Transcript domain model to map.
     * @return The mapped TranscriptEntity for database storage.
     */
    fun Transcript.toEntity() = categories?.let {
        TranscriptEntity(
        id, videoUrl, transcript, description, title,
        duration, uploadedAt, accountId, account,
        identifierId, identifier, it.joinToString(","),
        createdAt
    )
    }

    /**
     * Maps a TranscriptEntity to a Transcript domain model.
     * @param entity The TranscriptEntity to map.
     * @return The mapped Transcript domain model.
     */
    fun TranscriptEntity.toDomain() = Transcript(
        id, videoUrl, transcript, description, title,
        duration, uploadedAt, accountId, account,
        identifierId, identifier, categories.split(","), createdAt
    )
