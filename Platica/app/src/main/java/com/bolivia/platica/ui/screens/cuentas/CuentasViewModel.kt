package com.bolivia.platica.ui.screens.cuentas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bolivia.platica.data.local.entity.AccountEntity
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

data class CuentaConSaldo(
    val cuenta: AccountEntity,
    val saldo: Double
)

data class CuentasUiState(
    val cuentas: List<CuentaConSaldo> = emptyList(),
    val saldoDisponible: Double = 0.0,
    val invertido: Double = 0.0,
    val emergencias: Double = 0.0,
    val ahorro: Double = 0.0
)

@HiltViewModel
class CuentasViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
    private val goalRepository: GoalRepository
) : ViewModel() {

    val uiState: StateFlow<CuentasUiState> =
        combine(
            accountRepository.observarTodas(),
            transactionRepository.observarTodas(),
            goalRepository.observarTodosLosAportes()
        ) { cuentas, transacciones, aportes ->
            val saldos = CalculadoraSaldos.saldosPorCuenta(cuentas, transacciones, aportes)
            val cuentasConSaldo = cuentas.map { cuenta ->
                CuentaConSaldo(cuenta = cuenta, saldo = saldos[cuenta.id] ?: cuenta.saldoInicial)
            }
            CuentasUiState(
                cuentas = cuentasConSaldo,
                saldoDisponible = CalculadoraSaldos.saldoDisponible(saldos, cuentas),
                invertido = CalculadoraSaldos.patrimonioInvertido(saldos, cuentas),
                emergencias = CalculadoraSaldos.reservaEmergencias(saldos, cuentas),
                ahorro = CalculadoraSaldos.reservaAhorro(saldos, cuentas)
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CuentasUiState()
        )

    fun crear(nombre: String, tipo: AccountType, saldoInicial: Double) {
        viewModelScope.launch {
            accountRepository.crear(
                AccountEntity(nombre = nombre, tipo = tipo, saldoInicial = saldoInicial)
            )
        }
    }

    fun actualizar(cuenta: AccountEntity) {
        viewModelScope.launch { accountRepository.actualizar(cuenta) }
    }

    fun eliminar(cuenta: AccountEntity) {
        viewModelScope.launch { accountRepository.eliminar(cuenta) }
    }
}
