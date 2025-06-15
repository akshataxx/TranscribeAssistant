package com.example.transcribeassistant.network

import okhttp3.Request
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    /**
     * Sends a request to the server to transcribe a video URL.
     * @param request A map containing the video URL.
     * @return The transcript as a String.
     */
    @POST("video/url")
    suspend fun getTranscriptFromVideo(@Body request: Map<String, String>): String
}