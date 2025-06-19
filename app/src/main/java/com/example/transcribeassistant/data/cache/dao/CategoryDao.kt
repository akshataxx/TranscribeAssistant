package com.example.transcribeassistant.data.cache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.transcribeassistant.data.cache.entity.Category

@Dao
interface CategoryDao {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insert(categoryEntity: Category)

        @Query("SELECT * FROM category")
        suspend fun getAll(): List<Category>
}