package com.bolivia.platica.domain.repository

import com.bolivia.platica.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun observarTodas(): Flow<List<TransactionEntity>>
    suspend fun obtenerPorId(id: Long): TransactionEntity?
    suspend fun crear(transaction: TransactionEntity): Long
    suspend fun actualizar(transaction: TransactionEntity)
    suspend fun eliminar(transaction: TransactionEntity)
}
