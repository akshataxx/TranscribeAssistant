package com.example.transcribeassistant.data.network

import com.example.transcribeassistant.data.dto.CategoryAliasDto
import com.example.transcribeassistant.data.dto.RenameAliasRequest
import com.example.transcribeassistant.data.dto.TranscriptDto
import com.example.transcribeassistant.data.dto.UpdateNotesRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
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
     * @return The created transcript as a TranscriptDto.
     */
    @POST("api/video/transcribe")
    suspend fun transcribeVideo(@Body request: Map<String, String>): TranscriptDto

    @POST("api/video/transcribe-async")
    suspend fun transcribeVideoAsync(@Body request: Map<String, String>): Response<Unit>

    @GET("transcript")
    suspend fun getAllTranscripts(
        @Query("categoryIds") categories: List<String>? = null,
        @Query("account") account: String? = null,
        @Query("from") from: Instant? = null,
        @Query("to") to: Instant? = null
    ): List<TranscriptDto>

    @GET("transcript/{id}")
    suspend fun getTranscriptById(
        @Path("id") transcriptId: String
    ): TranscriptDto

    @PUT("api/v1/aliases/upsert")
    suspend fun upsertAlias(@Body request: RenameAliasRequest): CategoryAliasDto

    @PATCH("transcript/{id}/notes")
    suspend fun updateNotes(
        @Path("id") transcriptId: String,
        @Body request: UpdateNotesRequest
    ): Response<Unit>
}