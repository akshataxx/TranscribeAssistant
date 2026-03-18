package com.example.transcribeassistant.data.network

import com.example.transcribeassistant.data.dto.JobListResponseDto
import com.example.transcribeassistant.data.dto.JobSubmissionRequest
import com.example.transcribeassistant.data.dto.JobSubmissionResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface JobApi {

    @GET("api/video/jobs")
    suspend fun getJobs(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 100
    ): JobListResponseDto

    @POST("api/video/transcribe-async")
    suspend fun submitJob(
        @Body request: JobSubmissionRequest
    ): JobSubmissionResponseDto
}
