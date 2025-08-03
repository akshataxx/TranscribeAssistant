package com.example.transcribeassistant.di

import com.example.transcribeassistant.data.RetrofitClient
import com.example.transcribeassistant.data.network.AuthApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideAuthApi(): AuthApi {
        return RetrofitClient.authApi
    }
}
