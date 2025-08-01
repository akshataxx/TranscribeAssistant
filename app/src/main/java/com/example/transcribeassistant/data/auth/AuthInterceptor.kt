package com.example.transcribeassistant.data.auth


import android.util.Log
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Attaches the current access token to every outgoing request
 */

class AuthInterceptor(private val jwtManager: JwtManager): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        // get token and cache in memory temporarily
        val accessToken = runBlocking { jwtManager.getAccessToken() }
        val requestBuilder = original.newBuilder()
        if (!accessToken.isNullOrBlank()) {
            requestBuilder.addHeader("Authorization", "Bearer $accessToken")
        } else {
            Log.d("AuthInterceptor", "no access token available")
        }
        val request = requestBuilder.build()
        Log.d("AuthInterceptor", "sending request with auth header: ${request.header("Authorization")}")
        return chain.proceed(request)
    }
}
