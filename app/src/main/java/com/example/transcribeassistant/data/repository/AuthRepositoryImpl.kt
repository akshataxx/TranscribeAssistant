package com.example.transcribeassistant.data.repository

import com.example.transcribeassistant.data.dto.GoogleAuthRequest
import com.example.transcribeassistant.data.dto.JwtAuthResponse
import com.example.transcribeassistant.data.network.TranscriptApi
import com.example.transcribeassistant.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: TranscriptApi
) : AuthRepository {
    override suspend fun authenticateWithGoogle(request: GoogleAuthRequest): Result<JwtAuthResponse> {
        return try {
            val response = api.authenticateWithGoogle(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 