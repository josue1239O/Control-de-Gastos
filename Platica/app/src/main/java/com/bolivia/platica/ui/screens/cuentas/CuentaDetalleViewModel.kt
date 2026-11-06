package com.bolivia.platica.ui.screens.cuentas

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bolivia.platica.data.local.entity.AccountEntity
import com.bolivia.platica.data.local.entity.TransactionEntity
import com.bolivia.platica.domain.model.enums.TransactionType
import com.bolivia.platica.domain.repository.AccountRepository
import com.bolivia.platica.domain.repository.CategoryRepository
import com.bolivia.platica.domain.repository.GoalRepository
import com.bolivia.platica.domain.repository.TransactionRepository
import com.bolivia.platica.domain.usecase.CalculadoraSaldos
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class MovimientoCuenta(
    val transaccion: TransactionEntity,
    val categoria: String?
)

data class CuentaDetalleUiState(
    val cuenta: AccountEntity? = null,
    val saldo: Double = 0.0,
    val ingresos: Double = 0.0,
    val gastos: Double = 0.0,
    val movimientos: List<MovimientoCuenta> = emptyList()
)

/**
 * Detalle de una cuenta: saldo real y todos los movimientos que la afectan.
 */
@HiltViewModel
class CuentaDetalleViewModel @Inject constructor(
    accountRepository: AccountRepository,
    transactionRepository: TransactionRepository,
    categoryRepository: CategoryRepository,
    goalRepository: GoalRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val cuentaId: Long = savedStateHandle.get<String>("id")?.toLongOrNull() ?: 0L

    val uiState: StateFlow<CuentaDetalleUiState> =
        combine(
            accountRepository.observarPorId(cuentaId),
            transactionRepository.observarTodas(),
            categoryRepository.observarActivas(),
            goalRepository.observarTodosLosAportes()
        ) { cuenta, transacciones, categorias, aportes ->
            val nombres = categorias.associate { it.id to it.nombre }
            val propias = transacciones.filter { it.accountId == cuentaId }
            val saldos = CalculadoraSaldos.saldosPorCuenta(
                listOfNotNull(cuenta), transacciones, aportes
            )

            CuentaDetalleUiState(
                cuenta = cuenta,
                saldo = saldos[cuentaId] ?: cuenta?.saldoInicial ?: 0.0,
                ingresos = propias
                    .filter { it.tipo == TransactionType.INGRESO }
                    .sumOf { it.monto },
                gastos = propias
                    .filter { it.tipo == TransactionType.GASTO }
                    .sumOf { it.monto },
                movimientos = propias.map {
                    MovimientoCuenta(
                        transaccion = it,
                        categoria = it.categoryId?.let { id -> nombres[id] }
                    )
                }
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CuentaDetalleUiState()
        )
}
