package com.bolivia.platica.data.repository

import com.bolivia.platica.data.local.dao.BudgetDao
import com.bolivia.platica.data.local.entity.BudgetEntity
import com.bolivia.platica.domain.repository.BudgetRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BudgetRepositoryImpl @Inject constructor(
    private val dao: BudgetDao
) : BudgetRepository {
    override fun observarActivos(): Flow<List<BudgetEntity>> = dao.observarActivos()
    override suspend fun crear(budget: BudgetEntity) = dao.insertar(budget)
}
