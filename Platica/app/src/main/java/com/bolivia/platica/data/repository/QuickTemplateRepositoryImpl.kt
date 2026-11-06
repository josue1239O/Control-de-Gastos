package com.bolivia.platica.data.repository

import com.bolivia.platica.data.local.dao.QuickTemplateDao
import com.bolivia.platica.data.local.entity.QuickTemplateEntity
import com.bolivia.platica.domain.model.enums.TransactionType
import com.bolivia.platica.domain.repository.QuickTemplateRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuickTemplateRepositoryImpl @Inject constructor(
    private val dao: QuickTemplateDao
) : QuickTemplateRepository {
    override fun observarTodas(): Flow<List<QuickTemplateEntity>> = dao.observarTodas()
    override suspend fun contar(): Int = dao.contar()

    /** Crea plantillas de gasto rápido de ejemplo la primera vez. */
    suspend fun sembrarIniciales(cuentaId: Long, categoriaTransporteId: Long) {
        if (dao.contar() > 0) return
        val ejemplos = listOf(
            QuickTemplateEntity(
                nombre = "Pasaje",
                monto = 3.0,
                tipo = TransactionType.GASTO,
                accountId = cuentaId,
                categoryId = categoriaTransporteId,
                orden = 0
            ),
            QuickTemplateEntity(
                nombre = "Almuerzo",
                monto = 15.0,
                tipo = TransactionType.GASTO,
                accountId = cuentaId,
                categoryId = null,
                orden = 1
            )
        )
        ejemplos.forEach { dao.insertar(it) }
    }
}
