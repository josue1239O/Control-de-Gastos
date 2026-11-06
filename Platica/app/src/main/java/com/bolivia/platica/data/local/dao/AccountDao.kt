package com.bolivia.platica.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.bolivia.platica.data.local.entity.AccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {

    @Query("SELECT * FROM accounts ORDER BY orden ASC, creadaEn ASC")
    fun observarTodas(): Flow<List<AccountEntity>>

    @Query("SELECT * FROM accounts WHERE activa = 1 ORDER BY orden ASC")
    fun observarActivas(): Flow<List<AccountEntity>>

    @Query("SELECT * FROM accounts WHERE id = :id")
    fun observarPorId(id: Long): Flow<AccountEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(account: AccountEntity): Long

    @Update
    suspend fun actualizar(account: AccountEntity)

    @Delete
    suspend fun eliminar(account: AccountEntity)

    @Query("SELECT COUNT(*) FROM accounts")
    suspend fun contar(): Int
}
