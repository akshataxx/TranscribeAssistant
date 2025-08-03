package com.example.transcribeassistant.data.auth

import com.example.transcribeassistant.data.dto.RefreshTokenRequest
import com.example.transcribeassistant.data.network.AuthApi
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

/**
 * When an API call returns 401, this will:
 * 1) Call /refresh with the stored refresh token
 * 2) Save the new tokens
 * 3) Retry the original request with new access token
 */

class TokenAuthenticator(
    private val authApi: AuthApi,
    private val jwtManager: JwtManager
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // Prevent infinite loop
        if (response.request.header("Authorization") != null) {
            val refresh = jwtManager.getRefreshToken() ?: return null
            val refreshResponse = runBlocking {
                try {
                    authApi.refreshToken(RefreshTokenRequest(refresh))
                } catch (e: Exception) {
                    null
                }
            } ?: return null

            // Save new tokens
            runBlocking {
                jwtManager.saveTokens(
                    refreshResponse.accessToken,
                    refreshResponse.refreshToken
                )
            }

            // Retry original request with new access token
            return response.request.newBuilder()
                .header("Authorization", "Bearer ${refreshResponse.accessToken}")
                .build()
        }
        return null
    }
}