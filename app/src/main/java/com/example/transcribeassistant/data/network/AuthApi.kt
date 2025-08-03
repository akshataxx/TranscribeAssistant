package com.example.transcribeassistant.data.network

import com.example.transcribeassistant.data.dto.GoogleAuthRequest
import com.example.transcribeassistant.data.dto.JwtAuthResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/auth/google")
    suspend fun authenticate(@Body req: GoogleAuthRequest): JwtAuthResponse
}