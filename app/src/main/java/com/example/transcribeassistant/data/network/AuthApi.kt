package com.example.transcribeassistant.data.network

import com.example.transcribeassistant.data.dto.JwtAuthResponse
import com.example.transcribeassistant.data.dto.RefreshTokenRequest
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Defines authentication endpoints.
 */
interface AuthApi {
    @POST("api/auth/google")
    suspend fun loginWithGoogle(@Body body: Map<String, String>): JwtAuthResponse

    @POST("api/auth/refresh")
    suspend fun refreshToken(@Body body: RefreshTokenRequest): JwtAuthResponse
}