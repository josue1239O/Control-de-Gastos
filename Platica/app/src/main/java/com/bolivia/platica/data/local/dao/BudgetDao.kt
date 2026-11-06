package com.bolivia.platica.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bolivia.platica.data.local.entity.BudgetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {

    @Query("SELECT * FROM budgets WHERE activo = 1")
    fun observarActivos(): Flow<List<BudgetEntity>>

    @Query("SELECT * FROM budgets")
    fun observarTodos(): Flow<List<BudgetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(budget: BudgetEntity): Long
}
