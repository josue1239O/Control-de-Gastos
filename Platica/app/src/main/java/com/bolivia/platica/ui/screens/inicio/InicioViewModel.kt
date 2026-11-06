package com.bolivia.platica.ui.screens.inicio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bolivia.platica.data.local.entity.SavingsGoalEntity
import com.bolivia.platica.data.local.entity.TransactionEntity
import com.bolivia.platica.domain.model.enums.TransactionType
import com.bolivia.platica.domain.repository.AccountRepository
import com.bolivia.platica.domain.repository.GoalRepository
import com.bolivia.platica.domain.repository.InvestmentRepository
import com.bolivia.platica.domain.repository.TransactionRepository
import com.bolivia.platica.domain.usecase.Analitica
import com.bolivia.platica.domain.usecase.CalculadoraMetas
import com.bolivia.platica.domain.usecase.CalculadoraSaldos
import com.bolivia.platica.domain.usecase.CicloFinanciero
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class MovimientoInicio(
    val transaccion: TransactionEntity,
    val cuenta: String
)

data class InicioUiState(
    val cargando: Boolean = true,
    val nombreMes: String = CicloFinanciero.nombreMes(0),
    val saldoDisponible: Double = 0.0,
    val patrimonioInvertido: Double = 0.0,
    val reservaEmergencias: Double = 0.0,
    val reservaAhorro: Double = 0.0,
    val gananciaInversiones: Double = 0.0,
    val ingresosCiclo: Double = 0.0,
    val gastosCiclo: Double = 0.0,
    val balance: Double = 0.0,
    val tasaAhorro: Double? = null,
    val metasAlcanzables: List<SavingsGoalEntity> = emptyList(),
    val ultimas: List<MovimientoInicio> = emptyList()
)

@HiltViewModel
class InicioViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
    private val investmentRepository: InvestmentRepository,
    private val goalRepository: GoalRepository
) : ViewModel() {

    private val offsetMes = MutableStateFlow(0)

    val uiState: StateFlow<InicioUiState> =
        combine(
            offsetMes,
            combine(
                accountRepository.observarTodas(),
                goalRepository.observarTodosLosAportes()
            ) { cuentas, aportes -> cuentas to aportes },
            transactionRepository.observarTodas(),
            investmentRepository.observarGananciaTotal(),
            goalRepository.observarTodas()
        ) { offset, cuentasYAportes, todasTx, ganancia, metas ->
            val (cuentas, aportes) = cuentasYAportes
            val rango = CicloFinanciero.rangoMes(offset)
            val delMes = todasTx.filter { it.fecha in rango }
            val ingresos = delMes.filter { it.tipo == TransactionType.INGRESO }.sumOf { it.monto }
            val gastos = delMes.filter { it.tipo == TransactionType.GASTO }.sumOf { it.monto }

            val saldos = CalculadoraSaldos.saldosPorCuenta(cuentas, todasTx, aportes)
            val saldoAhorro = CalculadoraSaldos.reservaAhorro(saldos, cuentas)
            val nombresCuentas = cuentas.associate { it.id to it.nombre }

            InicioUiState(
                cargando = false,
                nombreMes = CicloFinanciero.nombreMes(offset),
                saldoDisponible = CalculadoraSaldos.saldoDisponible(saldos, cuentas),
                patrimonioInvertido = CalculadoraSaldos.patrimonioInvertido(saldos, cuentas),
                reservaEmergencias = CalculadoraSaldos.reservaEmergencias(saldos, cuentas),
                reservaAhorro = saldoAhorro,
                gananciaInversiones = ganancia,
                ingresosCiclo = ingresos,
                gastosCiclo = gastos,
                balance = Analitica.balance(ingresos, gastos),
                tasaAhorro = Analitica.tasaAhorro(ingresos, gastos),
                metasAlcanzables = CalculadoraMetas.metasAlcanzables(metas, saldoAhorro),
                ultimas = todasTx
                    .sortedByDescending { it.fecha }
                    .take(5)
                    .map { t ->
                        MovimientoInicio(
                            transaccion = t,
                            cuenta = nombresCuentas[t.accountId] ?: "Cuenta"
                        )
                    }
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = InicioUiState()
        )

    fun mesAnterior() { offsetMes.value -= 1 }
    fun mesSiguiente() { offsetMes.value += 1 }
}
