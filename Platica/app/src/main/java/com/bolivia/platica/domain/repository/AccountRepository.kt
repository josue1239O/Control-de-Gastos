package com.bolivia.platica.domain.repository

import com.bolivia.platica.data.local.entity.AccountEntity
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    fun observarTodas(): Flow<List<AccountEntity>>
    fun observarActivas(): Flow<List<AccountEntity>>
    fun observarPorId(id: Long): Flow<AccountEntity?>
    suspend fun crear(account: AccountEntity): Long
    suspend fun actualizar(account: AccountEntity)
    suspend fun eliminar(account: AccountEntity)
    suspend fun contar(): Int
    suspend fun sembrarCuentasIniciales()
}
