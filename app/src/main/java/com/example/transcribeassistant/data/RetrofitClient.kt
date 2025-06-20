package com.example.transcribeassistant.data

import com.example.transcribeassistant.data.network.TranscriptApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Singleton class that provides an instance of the ApiService
 * This class is responsible for creating the Retrofit client and make actual network requests.
 *
 */
object RetrofitClient {

    private val BASE_URL = "http://10.0.2.2:8080/"

    //private val moshi = Moshi.Builder().build()

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    val apiService: TranscriptApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(TranscriptApi::class.java)
}