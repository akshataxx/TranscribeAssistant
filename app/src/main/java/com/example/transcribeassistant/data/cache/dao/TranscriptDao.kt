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

    @Query("SELECT * FROM transcripts WHERE categoryId = :categoryId")
    suspend fun getByCategoryId(categoryId: String): List<TranscriptEntity>

    @Query("UPDATE transcripts SET alias = :newAlias WHERE categoryId = :categoryId")
    suspend fun updateAlias(categoryId: String, newAlias: String)

    @Query("DELETE FROM transcripts WHERE id IN (:transcriptIds)")
    suspend fun deleteByIds(transcriptIds: List<String>)

    @Query("UPDATE transcripts SET subcategoryId = :subcategoryId, subcategoryName = :subcategoryName WHERE id = :transcriptId")
    suspend fun updateSubcategoryById(transcriptId: String, subcategoryId: String?, subcategoryName: String?)
}
