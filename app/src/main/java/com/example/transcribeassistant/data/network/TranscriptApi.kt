package com.example.transcribeassistant.data.network

import com.example.transcribeassistant.data.dto.TranscriptDto
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Interface for the Transcript API.
 * This interface defines the endpoints for the transcript service.
 * It uses Retrofit annotations to specify HTTP methods and request bodies.
 */
interface TranscriptApi {

    /**
     * Sends a request to the server to transcribe a video URL.
     * @param request A map containing the video URL.
     * @return The transcript as a String.
     */
    @POST("video/url")
    suspend fun getTranscriptFromVideo(@Body request: Map<String, String>): TranscriptDto
}