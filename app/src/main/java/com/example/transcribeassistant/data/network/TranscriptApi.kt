package com.example.transcribeassistant.data.network

import com.example.transcribeassistant.domain.model.Transcript
import retrofit2.http.Body
import retrofit2.http.POST

interface TranscriptApi {

    /**
     * Sends a request to the server to transcribe a video URL.
     * @param request A map containing the video URL.
     * @return The transcript as a String.
     */
    @POST("video/url")
    suspend fun getTranscriptFromVideo(@Body request: Map<String, String>): Transcript
}