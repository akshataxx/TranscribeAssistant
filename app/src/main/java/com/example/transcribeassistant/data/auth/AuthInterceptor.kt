package com.example.transcribeassistant.data.auth


import okhttp3.Interceptor
import okhttp3.Response

/**
 * Attaches the current access token to every outgoing request
 */

class AuthInterceptor(private val jwtManager: JwtManager): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = jwtManager.getAccessToken()
        val request = if (token != null) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }
        return chain.proceed(request)
    }
}
