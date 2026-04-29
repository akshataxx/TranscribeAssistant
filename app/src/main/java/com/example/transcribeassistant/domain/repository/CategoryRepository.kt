package com.example.transcribeassistant.domain.repository

import com.example.transcribeassistant.domain.model.Subcategory

interface CategoryRepository {
    suspend fun getSubcategoriesForCategory(categoryId: String): List<Subcategory>
    suspend fun createSubcategory(categoryId: String, name: String): Subcategory
}
