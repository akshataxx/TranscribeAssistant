package com.example.transcribeassistant.data.repository

import com.example.transcribeassistant.data.dto.CreateSubcategoryRequest
import com.example.transcribeassistant.data.network.CategoryApi
import com.example.transcribeassistant.domain.model.Subcategory
import com.example.transcribeassistant.domain.repository.CategoryRepository

class CategoryRepositoryImpl(
    private val api: CategoryApi
) : CategoryRepository {

    override suspend fun getSubcategoriesForCategory(categoryId: String): List<Subcategory> {
        return api.getSubcategoriesForCategory(categoryId).map { dto ->
            Subcategory(id = dto.id, categoryId = dto.parentId, name = dto.name)
        }
    }

    override suspend fun createSubcategory(categoryId: String, name: String): Subcategory {
        val dto = api.createSubcategory(categoryId, CreateSubcategoryRequest(name))
        return Subcategory(id = dto.id, categoryId = dto.parentId, name = dto.name)
    }
}
