package com.bolivia.platica.domain.repository

import com.bolivia.platica.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun observarActivas(): Flow<List<CategoryEntity>>
    suspend fun crear(category: CategoryEntity): Long
    suspend fun actualizar(category: CategoryEntity)
    suspend fun eliminar(category: CategoryEntity)
    suspend fun contar(): Int
    suspend fun sembrarIniciales()
}
