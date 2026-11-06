package com.bolivia.platica.data.repository

import com.bolivia.platica.data.local.dao.InvestmentReturnDao
import com.bolivia.platica.data.local.entity.InvestmentReturnEntity
import com.bolivia.platica.domain.repository.InvestmentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InvestmentRepositoryImpl @Inject constructor(
    private val dao: InvestmentReturnDao
) : InvestmentRepository {
    override fun observarTodos(): Flow<List<InvestmentReturnEntity>> = dao.observarTodos()
    override fun observarGananciaTotal(): Flow<Double> = dao.observarGananciaTotal()
    override suspend fun crear(retorno: InvestmentReturnEntity) = dao.insertar(retorno)
    override suspend fun eliminar(retorno: InvestmentReturnEntity) = dao.eliminar(retorno)
}
