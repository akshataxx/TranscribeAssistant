package com.example.transcribeassistant.di

import com.example.transcribeassistant.data.auth.JwtManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface JwtManagerEntryPoint {
    fun jwtManager(): JwtManager
}