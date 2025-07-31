package com.example.transcribeassistant.di

import android.content.Context
import androidx.media3.database.DatabaseProvider
import androidx.room.Room
import com.example.transcribeassistant.data.RetrofitClient
import com.example.transcribeassistant.data.cache.AppDatabase
import com.example.transcribeassistant.data.cache.dao.TranscriptDao
import com.example.transcribeassistant.data.network.TranscriptApi
import com.example.transcribeassistant.data.repository.TranscriptRepositoryImpl
import com.example.transcribeassistant.domain.repository.TranscriptRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TranscriptModule {

    /*@Provides
    fun provideTranscriptApi(): TranscriptApi {
        return RetrofitClient.apiService
    }*/

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "transcribe_db"
        ).fallbackToDestructiveMigration().build()
    }


    @Provides
    fun provideTranscriptDao(db: AppDatabase): TranscriptDao {
        return db.transcriptDao()
    }


    @Provides
    fun provideTranscriptRepository(
        api: TranscriptApi,
        dao: TranscriptDao
    ): TranscriptRepository {
        return TranscriptRepositoryImpl(api, dao)
    }
}