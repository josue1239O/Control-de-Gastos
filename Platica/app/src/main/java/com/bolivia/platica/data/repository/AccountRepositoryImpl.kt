package com.bolivia.platica.data.repository

import com.bolivia.platica.data.local.SeedData
import com.bolivia.platica.data.local.dao.AccountDao
import com.bolivia.platica.data.local.entity.AccountEntity
import com.bolivia.platica.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepositoryImpl @Inject constructor(
    private val dao: AccountDao
) : AccountRepository {
    override fun observarTodas(): Flow<List<AccountEntity>> = dao.observarTodas()
    override fun observarActivas(): Flow<List<AccountEntity>> = dao.observarActivas()
    override fun observarPorId(id: Long) = dao.observarPorId(id)
    override suspend fun crear(account: AccountEntity) = dao.insertar(account)
    override suspend fun actualizar(account: AccountEntity) = dao.actualizar(account)
    override suspend fun eliminar(account: AccountEntity) = dao.eliminar(account)
    override suspend fun contar(): Int = dao.contar()

    override suspend fun sembrarCuentasIniciales() {
        if (dao.contar() == 0) {
            SeedData.cuentas.forEach { dao.insertar(it) }
        }
    }
}
