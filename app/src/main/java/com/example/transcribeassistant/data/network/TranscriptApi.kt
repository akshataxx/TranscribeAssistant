package com.example.transcribeassistant.data.network

import com.example.transcribeassistant.data.dto.TranscriptDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.time.Instant

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

    @GET("transcript")
    suspend fun getAllTranscripts(
        @Query("id") id: String? = null,
        @Query("categories") categories: List<String>? = null,
        @Query("account") account: String? = null,
        @Query("from") from: Instant? = null,
        @Query("to") to: Instant? = null
    ): List<TranscriptDto>
}
