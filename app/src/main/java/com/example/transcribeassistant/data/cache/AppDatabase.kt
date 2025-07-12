package com.example.transcribeassistant.data.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.transcribeassistant.data.cache.dao.CategoryDao
import com.example.transcribeassistant.data.cache.dao.TranscriptDao
import com.example.transcribeassistant.data.cache.entity.Category
import com.example.transcribeassistant.data.cache.entity.TranscriptEntity

@Database(
    entities = [TranscriptEntity::class, Category::class],
    version = 6,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transcriptDao(): TranscriptDao

    abstract fun categoryDao(): CategoryDao
}
