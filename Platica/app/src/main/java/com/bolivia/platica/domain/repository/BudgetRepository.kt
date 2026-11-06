package com.bolivia.platica.domain.repository

import com.bolivia.platica.data.local.entity.BudgetEntity
import kotlinx.coroutines.flow.Flow

interface BudgetRepository {
    fun observarActivos(): Flow<List<BudgetEntity>>
    suspend fun crear(budget: BudgetEntity): Long
}
