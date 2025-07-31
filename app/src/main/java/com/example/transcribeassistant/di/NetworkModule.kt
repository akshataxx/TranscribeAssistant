package com.example.transcribeassistant.di

import android.content.Context
import com.example.transcribeassistant.data.auth.AuthInterceptor
import com.example.transcribeassistant.data.auth.JwtManager
import com.example.transcribeassistant.data.auth.TokenAuthenticator
import com.example.transcribeassistant.data.network.AuthApi
import com.example.transcribeassistant.data.network.TranscriptApi
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

    @Provides
    @Singleton
    fun provideJwtManager(@ApplicationContext ctx: Context) = JwtManager(ctx)

    @Provides @Singleton
    fun provideAuthApi(): AuthApi {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/")
            .addConverterFactory(
                MoshiConverterFactory.create(
                    Moshi.Builder()
                        .add(KotlinJsonAdapterFactory())
                        .build()
                )
            )
            .build()
        return retrofit.create(AuthApi::class.java)
    }

    @Provides @Singleton
    fun provideTranscriptApi(
        authApi: AuthApi,
        jwtManager: JwtManager
    ): TranscriptApi {
        val logging = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(jwtManager))
            .authenticator(TokenAuthenticator(authApi, jwtManager))
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/")
            .client(client)
            .addConverterFactory(
                MoshiConverterFactory.create(
                    Moshi.Builder()
                        .add(KotlinJsonAdapterFactory())
                        .build()
                )
            )
            .build()
            .create(TranscriptApi::class.java)
    }
}