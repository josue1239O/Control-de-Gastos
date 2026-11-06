package com.bolivia.platica.data.repository

import com.bolivia.platica.data.local.SeedData
import com.bolivia.platica.data.local.dao.CategoryDao
import com.bolivia.platica.data.local.entity.CategoryEntity
import com.bolivia.platica.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val dao: CategoryDao
) : CategoryRepository {
    override fun observarActivas(): Flow<List<CategoryEntity>> = dao.observarActivas()
    override suspend fun crear(category: CategoryEntity) = dao.insertar(category)
    override suspend fun actualizar(category: CategoryEntity) = dao.actualizar(category)
    override suspend fun eliminar(category: CategoryEntity) = dao.eliminar(category)
    override suspend fun contar(): Int = dao.contar()

    override suspend fun sembrarIniciales() {
        if (dao.contar() == 0) {
            dao.insertarTodas(SeedData.categorias)
        }
    }
}
