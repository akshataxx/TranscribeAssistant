package com.example.transcribeassistant.data.repository

import com.example.transcribeassistant.data.dto.JobSubmissionRequest
import com.example.transcribeassistant.data.network.JobApi
import com.example.transcribeassistant.domain.model.ActivityItem
import com.example.transcribeassistant.domain.model.ActivityStatus
import com.example.transcribeassistant.domain.repository.JobRepository

class JobRepositoryImpl(
    private val api: JobApi
) : JobRepository {

    override suspend fun getJobs(page: Int, size: Int): List<ActivityItem> {
        val response = api.getJobs(page, size)
        return response.jobs
            .mapNotNull { dto ->
                val status = ActivityStatus.from(dto.status) ?: return@mapNotNull null
                ActivityItem(
                    id = dto.id,
                    videoUrl = dto.videoUrl,
                    status = status,
                    baseTranscriptId = dto.baseTranscriptId,
                    userTranscriptId = dto.userTranscriptId,
                    errorMessage = dto.errorMessage,
                    retryCount = dto.retryCount,
                    updatedAt = dto.updatedAt
                )
            }
            .sortedByDescending { it.updatedAt }
    }

    override suspend fun submitJob(videoUrl: String): String {
        val response = api.submitJob(JobSubmissionRequest(videoUrl))
        return response.jobId
    }
}
