package com.bolivia.platica.domain.repository

import com.bolivia.platica.data.local.entity.InvestmentReturnEntity
import kotlinx.coroutines.flow.Flow

interface InvestmentRepository {
    fun observarTodos(): Flow<List<InvestmentReturnEntity>>
    fun observarGananciaTotal(): Flow<Double>
    suspend fun crear(retorno: InvestmentReturnEntity): Long
    suspend fun eliminar(retorno: InvestmentReturnEntity)
}
