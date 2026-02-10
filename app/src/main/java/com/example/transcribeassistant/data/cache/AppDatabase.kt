package com.example.transcribeassistant.data.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.transcribeassistant.data.cache.dao.CategoryDao
import com.example.transcribeassistant.data.cache.dao.TranscriptDao
import com.example.transcribeassistant.data.cache.entity.Category
import com.example.transcribeassistant.data.cache.entity.TranscriptEntity

@Database(
    entities = [TranscriptEntity::class, Category::class],
    version = 7,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transcriptDao(): TranscriptDao

    abstract fun categoryDao(): CategoryDao

    companion object {
        /**
         * Migration from version 5 to 6: Add structuredContent column
         */
        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE transcripts ADD COLUMN structuredContent TEXT")
            }
        }

        /**
         * Migration from version 6 to 7: Add notes column
         */
        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE transcripts ADD COLUMN notes TEXT")
            }
        }
    }
}
