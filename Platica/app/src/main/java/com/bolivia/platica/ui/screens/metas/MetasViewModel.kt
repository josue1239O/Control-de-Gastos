package com.bolivia.platica.ui.screens.metas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bolivia.platica.data.local.entity.SavingsGoalEntity
import com.bolivia.platica.domain.model.enums.AccountType
import com.bolivia.platica.domain.repository.AccountRepository
import com.bolivia.platica.domain.repository.GoalRepository
import com.bolivia.platica.domain.repository.TransactionRepository
import com.bolivia.platica.domain.usecase.CalculadoraSaldos
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MetasUiState(
    val metas: List<SavingsGoalEntity> = emptyList(),
    /** Saldo real de las cuentas de tipo Ahorro (se recalcula con cada movimiento). */
    val saldoAhorro: Double = 0.0,
    val hayCuentaAhorro: Boolean = false
)

@HiltViewModel
class MetasViewModel @Inject constructor(
    private val goalRepository: GoalRepository,
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    val uiState: StateFlow<MetasUiState> =
        combine(
            goalRepository.observarTodas(),
            accountRepository.observarTodas(),
            transactionRepository.observarTodas(),
            goalRepository.observarTodosLosAportes()
        ) { metas, cuentas, transacciones, aportes ->
            val saldos = CalculadoraSaldos.saldosPorCuenta(cuentas, transacciones, aportes)
            val cuentasAhorro = cuentas.filter { it.activa && it.tipo == AccountType.AHORRO }
            MetasUiState(
                metas = metas,
                saldoAhorro = cuentasAhorro.sumOf { saldos[it.id] ?: it.saldoInicial },
                hayCuentaAhorro = cuentasAhorro.isNotEmpty()
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MetasUiState()
        )

    fun crear(meta: SavingsGoalEntity) {
        viewModelScope.launch { goalRepository.crear(meta) }
    }

    fun actualizar(meta: SavingsGoalEntity) {
        viewModelScope.launch { goalRepository.actualizar(meta) }
    }

    fun eliminar(meta: SavingsGoalEntity) {
        viewModelScope.launch { goalRepository.eliminar(meta) }
    }
}
