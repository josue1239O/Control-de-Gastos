package com.bolivia.platica.domain.repository

import com.bolivia.platica.data.local.entity.GoalContributionEntity
import com.bolivia.platica.data.local.entity.SavingsGoalEntity
import kotlinx.coroutines.flow.Flow

interface GoalRepository {
    fun observarTodas(): Flow<List<SavingsGoalEntity>>
    suspend fun crear(goal: SavingsGoalEntity): Long
    suspend fun actualizar(goal: SavingsGoalEntity)
    suspend fun eliminar(goal: SavingsGoalEntity)
    fun observarTodosLosAportes(): Flow<List<GoalContributionEntity>>
}
