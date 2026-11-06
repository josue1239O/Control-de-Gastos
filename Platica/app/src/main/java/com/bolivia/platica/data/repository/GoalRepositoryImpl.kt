package com.bolivia.platica.data.repository

import com.bolivia.platica.data.local.dao.GoalDao
import com.bolivia.platica.data.local.entity.GoalContributionEntity
import com.bolivia.platica.data.local.entity.SavingsGoalEntity
import com.bolivia.platica.domain.repository.GoalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoalRepositoryImpl @Inject constructor(
    private val dao: GoalDao
) : GoalRepository {
    override fun observarTodas(): Flow<List<SavingsGoalEntity>> = dao.observarTodas()
    override suspend fun crear(goal: SavingsGoalEntity) = dao.insertar(goal)
    override suspend fun actualizar(goal: SavingsGoalEntity) = dao.actualizar(goal)
    override suspend fun eliminar(goal: SavingsGoalEntity) = dao.eliminar(goal)
    override fun observarTodosLosAportes(): Flow<List<GoalContributionEntity>> = dao.observarTodosLosAportes()
}
