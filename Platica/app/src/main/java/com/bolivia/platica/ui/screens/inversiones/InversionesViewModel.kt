package com.bolivia.platica.ui.screens.inversiones

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bolivia.platica.data.local.entity.AccountEntity
import com.bolivia.platica.data.local.entity.InvestmentReturnEntity
import com.bolivia.platica.domain.model.enums.AccountType
import com.bolivia.platica.domain.repository.AccountRepository
import com.bolivia.platica.domain.repository.GoalRepository
import com.bolivia.platica.domain.repository.InvestmentRepository
import com.bolivia.platica.domain.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class InversionesUiState(
    val cuentasInversion: List<AccountEntity> = emptyList(),
    val ganancias: List<InvestmentReturnEntity> = emptyList(),
    val gananciaTotal: Double = 0.0,
    val capitalTotal: Double = 0.0
)

@HiltViewModel
class InversionesViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val investmentRepository: InvestmentRepository,
    private val transactionRepository: TransactionRepository,
    private val goalRepository: GoalRepository
) : ViewModel() {

    val uiState: StateFlow<InversionesUiState> =
        combine(
            accountRepository.observarTodas(),
            investmentRepository.observarTodos(),
            transactionRepository.observarTodas(),
            goalRepository.observarTodosLosAportes()
        ) { cuentas, ganancias, transacciones, aportes ->
            val inv = cuentas.filter { it.tipo == AccountType.INVERSION && it.activa }
            val saldos = com.bolivia.platica.domain.usecase.CalculadoraSaldos
                .saldosPorCuenta(cuentas, transacciones, aportes)
            InversionesUiState(
                cuentasInversion = inv,
                ganancias = ganancias,
                gananciaTotal = ganancias.sumOf { it.ganancia },
                capitalTotal = inv.sumOf { saldos[it.id] ?: it.saldoInicial }
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = InversionesUiState()
        )

    fun registrarGanancia(accountId: Long, ganancia: Double, reinvertido: Double, nota: String?) {
        viewModelScope.launch {
            investmentRepository.crear(
                InvestmentReturnEntity(
                    accountId = accountId,
                    ganancia = ganancia,
                    fecha = System.currentTimeMillis(),
                    reinvertido = reinvertido,
                    nota = nota
                )
            )
        }
    }

    fun eliminar(item: InvestmentReturnEntity) {
        viewModelScope.launch { investmentRepository.eliminar(item) }
    }
}
