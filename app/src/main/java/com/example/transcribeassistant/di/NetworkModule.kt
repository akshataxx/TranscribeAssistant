package com.example.transcribeassistant.di

import android.content.Context
import android.util.Log
import com.example.transcribeassistant.BuildConfig
import com.example.transcribeassistant.data.auth.AuthInterceptor
import com.example.transcribeassistant.data.auth.AuthStateManager
import com.example.transcribeassistant.data.auth.JwtManager
import com.example.transcribeassistant.data.auth.TokenAuthenticator
import com.example.transcribeassistant.data.network.AuthApi
import com.example.transcribeassistant.data.network.DeviceApi
import com.example.transcribeassistant.data.network.JobApi
import com.example.transcribeassistant.data.network.SubscriptionApi
import com.example.transcribeassistant.data.network.TranscriptApi
import com.example.transcribeassistant.data.network.adapter.InstantAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton


/**
 * Binds together under Hilt(provides JwtManager, AuthApi and TranscriptApi with interceptor+ authenticator)
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private val BASE_URL = BuildConfig.API_BASE_URL

    private val moshi = Moshi.Builder()
        .add(InstantAdapter())
        .add(KotlinJsonAdapterFactory())
        .build()

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.HEADERS
    }

    @Provides
    @Singleton
    fun provideJwtManager(@ApplicationContext ctx: Context) = JwtManager(ctx)

    @Provides
    @Singleton
    fun provideAuthApi(): AuthApi =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(OkHttpClient.Builder()
                .addInterceptor(logging)
                .build())
            .build()
            .create(AuthApi::class.java)

    @Provides
    @Singleton
    fun provideTranscriptApi(
        authApi: AuthApi,
        jwtManager: JwtManager,
        authStateManager: AuthStateManager
    ): TranscriptApi {
        val authInterceptor = AuthInterceptor(jwtManager, authApi)
        val tokenAuthenticator = TokenAuthenticator(authApi, jwtManager, authStateManager)
        
        Log.d("NetworkModule", "Creating TranscriptApi with AuthInterceptor that handles token refresh")
        
        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .authenticator(tokenAuthenticator)
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(
                MoshiConverterFactory.create(moshi))
            .build()
            .create(TranscriptApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSubscriptionApi(
        authApi: AuthApi,
        jwtManager: JwtManager,
        authStateManager: AuthStateManager
    ): SubscriptionApi {
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(jwtManager, authApi))
            .authenticator(TokenAuthenticator(authApi, jwtManager, authStateManager))
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(SubscriptionApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDeviceApi(
        authApi: AuthApi,
        jwtManager: JwtManager,
        authStateManager: AuthStateManager
    ): DeviceApi {
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(jwtManager, authApi))
            .authenticator(TokenAuthenticator(authApi, jwtManager, authStateManager))
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(DeviceApi::class.java)
    }

    @Provides
    @Singleton
    fun provideJobApi(
        authApi: AuthApi,
        jwtManager: JwtManager,
        authStateManager: AuthStateManager
    ): JobApi {
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(jwtManager, authApi))
            .authenticator(TokenAuthenticator(authApi, jwtManager, authStateManager))
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(JobApi::class.java)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator): OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .authenticator(tokenAuthenticator)
            .addInterceptor(logging)
            .build()
}