package com.example.transcribeassistant.data.auth

import android.util.Log
import com.example.transcribeassistant.data.dto.RefreshTokenRequest
import com.example.transcribeassistant.data.network.AuthApi
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

/**
 * When an API call returns 401 or 403, this will:
 * 1) Call /refresh with the stored refresh token
 * 2) Save the new tokens
 * 3) Retry the original request with new access token
 */

class TokenAuthenticator(
    private val authApi: AuthApi,
    private val jwtManager: JwtManager,
    private val authStateManager: AuthStateManager
) : Authenticator {
    
    init {
        Log.d("TokenAuthenticator", "Created new instance")
    }

    override fun authenticate(route: Route?, response: Response): Request? {
        Log.d("TokenAuthenticator", "authenticate() called for response code: ${response.code}")
        
        // Handle both 401 (Unauthorized) and 403 (Forbidden) - both can indicate token issues
        if (response.code == 401 || response.code == 403) {
            // Prevent infinite loop
            if (response.request.header("Authorization") != null) {
                Log.d("TokenAuthenticator", "Attempting token refresh for ${response.code} response")
                val refresh = jwtManager.getRefreshToken()
                if (refresh == null) {
                    Log.w("TokenAuthenticator", "No refresh token available")
                    return null
                }
                
                val refreshResponse = runBlocking {
                    try {
                        Log.d("TokenAuthenticator", "Calling refresh token API")
                        authApi.refreshToken(RefreshTokenRequest(refresh))
                    } catch (e: Exception) {
                        Log.e("TokenAuthenticator", "Token refresh failed: ${e.message}")
                        null
                    }
                }
                
                if (refreshResponse == null) {
                    Log.e("TokenAuthenticator", "Token refresh returned null - clearing tokens")
                    // Clear tokens when refresh fails and signal authentication expired
                    runBlocking {
                        jwtManager.clearTokens()
                    }
                    authStateManager.signalAuthenticationExpired()
                    return null
                }

                // Save new tokens
                runBlocking {
                    jwtManager.saveTokens(
                        refreshResponse.accessToken,
                        refreshResponse.refreshToken
                    )
                }

                Log.d("TokenAuthenticator", "Token refreshed successfully, retrying request")
                // Retry original request with new access token
                return response.request.newBuilder()
                    .header("Authorization", "Bearer ${refreshResponse.accessToken}")
                    .build()
            } else {
                Log.w("TokenAuthenticator", "No Authorization header in original request")
            }
        } else {
            Log.d("TokenAuthenticator", "Response code ${response.code} not handled")
        }
        return null
    }
}