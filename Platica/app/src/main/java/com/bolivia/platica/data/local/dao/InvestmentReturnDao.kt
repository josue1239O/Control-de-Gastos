package com.bolivia.platica.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bolivia.platica.data.local.entity.InvestmentReturnEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InvestmentReturnDao {

    @Query("SELECT * FROM investment_returns ORDER BY fecha DESC")
    fun observarTodos(): Flow<List<InvestmentReturnEntity>>

    /** Ganancia total acumulada de todas las inversiones. */
    @Query("SELECT COALESCE(SUM(ganancia), 0) FROM investment_returns")
    fun observarGananciaTotal(): Flow<Double>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(retorno: InvestmentReturnEntity): Long

    @Delete
    suspend fun eliminar(retorno: InvestmentReturnEntity)
}
