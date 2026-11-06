package com.bolivia.platica.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.bolivia.platica.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

/**
 * Resultado de una suma agrupada por categoría (para el análisis).
 */
data class CategoriaTotal(
    val categoryId: Long?,
    val categoria: String?,
    val total: Double
)

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions ORDER BY fecha DESC, id DESC")
    fun observarTodas(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun obtenerPorId(id: Long): TransactionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(transaction: TransactionEntity): Long

    @Update
    suspend fun actualizar(transaction: TransactionEntity)

    @Delete
    suspend fun eliminar(transaction: TransactionEntity)
}
