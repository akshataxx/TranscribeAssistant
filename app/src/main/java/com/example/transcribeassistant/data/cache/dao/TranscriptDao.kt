package com.example.transcribeassistant.data.cache.dao

import androidx.room.*
import com.example.transcribeassistant.data.cache.entity.TranscriptEntity

/**
 * Data Access Object (DAO) for managing transcripts in the local database.
 * This class will handle operations such as inserting, updating, deleting, and querying transcripts.
 */

@Dao
interface TranscriptDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transcriptEntity: TranscriptEntity)

    @Query("SELECT * FROM transcripts")
    suspend fun getAll(): List<TranscriptEntity>

    @Query("SELECT * FROM transcripts WHERE id = :transcriptId")
    suspend fun getById(transcriptId: String): TranscriptEntity?
}
