package com.example.transcribeassistant.domain.repository

import com.example.transcribeassistant.domain.model.ActivityItem

interface JobRepository {
    suspend fun getJobs(page: Int = 0, size: Int = 100): List<ActivityItem>
    suspend fun submitJob(videoUrl: String): String   // returns jobId
}
