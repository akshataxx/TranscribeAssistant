package com.example.transcribeassistant.data.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.transcribeassistant.data.cache.dao.TranscriptDao
import com.example.transcribeassistant.data.cache.entity.TranscriptEntity

@Database(
    entities = [TranscriptEntity::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transcriptDao(): TranscriptDao
}
