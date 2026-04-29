package com.example.transcribeassistant.data.network

import com.example.transcribeassistant.data.dto.CategoryWithSubcategoriesDto
import com.example.transcribeassistant.data.dto.CreateSubcategoryRequest
import com.example.transcribeassistant.data.dto.SubcategoryDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CategoryApi {

    @GET("api/v1/categories")
    suspend fun getCategoriesWithSubcategories(): List<CategoryWithSubcategoriesDto>

    @GET("api/v1/categories/{rootId}/subcategories")
    suspend fun getSubcategoriesForCategory(
        @Path("rootId") categoryId: String
    ): List<SubcategoryDto>

    @POST("api/v1/categories/{rootId}/subcategories")
    suspend fun createSubcategory(
        @Path("rootId") categoryId: String,
        @Body request: CreateSubcategoryRequest
    ): SubcategoryDto
}
