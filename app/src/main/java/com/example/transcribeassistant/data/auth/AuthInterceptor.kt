package com.example.transcribeassistant.data.auth

import android.util.Log
import com.example.transcribeassistant.data.dto.RefreshTokenRequest
import com.example.transcribeassistant.data.network.AuthApi
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Attaches the current access token to every outgoing request
 * Also handles token refresh for 403/401 responses
 */
class AuthInterceptor(
    private val jwtManager: JwtManager,
    private val authApi: AuthApi
): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        
        // Get current token
        var accessToken = runBlocking { jwtManager.getAccessToken() }
        
        // Build request with current token
        val requestBuilder = original.newBuilder()
        if (!accessToken.isNullOrBlank()) {
            requestBuilder.addHeader("Authorization", "Bearer $accessToken")
        } else {
            Log.d("AuthInterceptor", "no access token available")
        }
        
        val request = requestBuilder.build()
        Log.d("AuthInterceptor", "sending request with auth header: ${request.header("Authorization")}")
        
        // Make the request
        val response = chain.proceed(request)
        
        // If we get 401 or 403, try to refresh the token and retry once
        if ((response.code == 401 || response.code == 403) && !accessToken.isNullOrBlank()) {
            Log.d("AuthInterceptor", "Received ${response.code}, attempting token refresh")
            
            val refreshToken = runBlocking { jwtManager.getRefreshToken() }
            if (refreshToken != null) {
                try {
                    Log.d("AuthInterceptor", "Calling refresh token API")
                    val refreshResponse = runBlocking { 
                        authApi.refreshToken(RefreshTokenRequest(refreshToken))
                    }
                    
                    // Save new tokens
                    runBlocking {
                        jwtManager.saveTokens(refreshResponse.accessToken, refreshResponse.refreshToken)
                    }
                    
                    Log.d("AuthInterceptor", "Token refreshed successfully, retrying request")
                    
                    // Close the original response
                    response.close()
                    
                    // Retry the request with new token
                    val newRequest = original.newBuilder()
                        .header("Authorization", "Bearer ${refreshResponse.accessToken}")
                        .build()
                    
                    return chain.proceed(newRequest)
                    
                } catch (e: Exception) {
                    Log.e("AuthInterceptor", "Token refresh failed: ${e.message}")
                }
            } else {
                Log.w("AuthInterceptor", "No refresh token available")
            }
        }
        
        return response
    }
}