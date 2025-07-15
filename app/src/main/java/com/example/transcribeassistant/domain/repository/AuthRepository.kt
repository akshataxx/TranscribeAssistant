package com.example.transcribeassistant.domain.repository

import com.example.transcribeassistant.data.dto.GoogleAuthRequest
import com.example.transcribeassistant.data.dto.JwtAuthResponse

interface AuthRepository {
    suspend fun authenticateWithGoogle(request: GoogleAuthRequest): Result<JwtAuthResponse>
} 