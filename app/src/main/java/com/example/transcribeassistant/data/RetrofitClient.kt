package com.example.transcribeassistant.data

import com.example.transcribeassistant.data.network.AuthApi
import com.example.transcribeassistant.data.network.TranscriptApi
import com.example.transcribeassistant.data.network.adapter.InstantAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * Singleton class that provides an instance of the ApiService
 * This class is responsible for creating the Retrofit client and make actual network requests.
 *
 */
object RetrofitClient {

    private val BASE_URL = "http://10.0.2.2:8080"

    private val moshi = Moshi.Builder()
        .add(InstantAdapter())
        .add(KotlinJsonAdapterFactory())
        .build()

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val apiService: TranscriptApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(TranscriptApi::class.java)

    val transcriptApi: TranscriptApi = retrofit.create(TranscriptApi::class.java)
    val authApi: AuthApi = retrofit.create(AuthApi::class.java)
}
