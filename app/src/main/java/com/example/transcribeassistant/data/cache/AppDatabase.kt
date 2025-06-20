package com.example.transcribeassistant.data.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.transcribeassistant.data.cache.dao.TranscriptDao
import com.example.transcribeassistant.data.cache.entity.TranscriptEntity

@Database(
    entities = [TranscriptEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transcriptDao(): TranscriptDao

}