package com.bolivia.platica.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bolivia.platica.data.local.entity.RecurringTransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecurringDao {

    @Query("SELECT * FROM recurring_transactions ORDER BY proximaFecha ASC")
    fun observarTodas(): Flow<List<RecurringTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(recurring: RecurringTransactionEntity): Long

    @Delete
    suspend fun eliminar(recurring: RecurringTransactionEntity)
}
