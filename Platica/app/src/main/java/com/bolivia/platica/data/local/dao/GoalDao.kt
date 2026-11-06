package com.bolivia.platica.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.bolivia.platica.data.local.entity.GoalContributionEntity
import com.bolivia.platica.data.local.entity.SavingsGoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {

    @Query(
        """
        SELECT * FROM savings_goals
        ORDER BY estado ASC,
                 CASE prioridad WHEN 'ALTA' THEN 0 WHEN 'MEDIA' THEN 1 ELSE 2 END ASC,
                 creadaEn DESC
        """
    )
    fun observarTodas(): Flow<List<SavingsGoalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(goal: SavingsGoalEntity): Long

    @Update
    suspend fun actualizar(goal: SavingsGoalEntity)

    @Delete
    suspend fun eliminar(goal: SavingsGoalEntity)

    @Query("SELECT * FROM goal_contributions ORDER BY fecha DESC")
    fun observarTodosLosAportes(): Flow<List<GoalContributionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarAporte(aporte: GoalContributionEntity): Long
}
