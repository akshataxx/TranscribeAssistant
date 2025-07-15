package com.example.transcribeassistant.data

import com.example.transcribeassistant.data.cache.AuthManager
import com.example.transcribeassistant.data.network.TranscriptApi
import com.example.transcribeassistant.data.network.adapter.InstantAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitClient {

    private const val BASE_URL = "http://10.0.2.2:8080"

    fun getApiService(authManager: AuthManager): TranscriptApi {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(logging)
        httpClient.addInterceptor { chain ->
            val requestBuilder = chain.request().newBuilder()
            authManager.getToken()?.let { token ->
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }
            chain.proceed(requestBuilder.build())
        }

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .add(InstantAdapter())
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(httpClient.build())
            .build()
            .create(TranscriptApi::class.java)
    }
}