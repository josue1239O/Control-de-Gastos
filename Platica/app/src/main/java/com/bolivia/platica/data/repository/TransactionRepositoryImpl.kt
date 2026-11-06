package com.bolivia.platica.data.repository

import com.bolivia.platica.data.local.dao.TransactionDao
import com.bolivia.platica.data.local.entity.TransactionEntity
import com.bolivia.platica.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepositoryImpl @Inject constructor(
    private val dao: TransactionDao
) : TransactionRepository {
    override fun observarTodas(): Flow<List<TransactionEntity>> = dao.observarTodas()
    override suspend fun obtenerPorId(id: Long) = dao.obtenerPorId(id)
    override suspend fun crear(transaction: TransactionEntity) = dao.insertar(transaction)
    override suspend fun actualizar(transaction: TransactionEntity) = dao.actualizar(transaction)
    override suspend fun eliminar(transaction: TransactionEntity) = dao.eliminar(transaction)
}
