package com.example.transcribeassistant.di

import android.content.Context
import androidx.room.Room
import com.example.transcribeassistant.data.cache.AppDatabase
import com.example.transcribeassistant.data.cache.dao.TranscriptDao
import com.example.transcribeassistant.data.network.DeviceApi
import com.example.transcribeassistant.data.network.JobApi
import com.example.transcribeassistant.data.network.SubscriptionApi
import com.example.transcribeassistant.data.network.TranscriptApi
import com.example.transcribeassistant.data.repository.DeviceRepositoryImpl
import com.example.transcribeassistant.data.repository.JobRepositoryImpl
import com.example.transcribeassistant.data.repository.SubscriptionRepositoryImpl
import com.example.transcribeassistant.data.repository.TranscriptRepositoryImpl
import com.example.transcribeassistant.domain.repository.DeviceRepository
import com.example.transcribeassistant.domain.repository.JobRepository
import com.example.transcribeassistant.domain.repository.SubscriptionRepository
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

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "transcribe_db"
        )
            .addMigrations(AppDatabase.MIGRATION_5_6, AppDatabase.MIGRATION_6_7)
            .fallbackToDestructiveMigration()
            .build()
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
    
    @Provides
    fun provideSubscriptionRepository(
        api: SubscriptionApi
    ): SubscriptionRepository {
        return SubscriptionRepositoryImpl(api)
    }

    @Provides
    fun provideJobRepository(api: JobApi): JobRepository = JobRepositoryImpl(api)

    @Provides
    @Singleton
    fun provideDeviceRepository(
        api: DeviceApi,
        @ApplicationContext context: Context
    ): DeviceRepository {
        return DeviceRepositoryImpl(api, context)
    }
}