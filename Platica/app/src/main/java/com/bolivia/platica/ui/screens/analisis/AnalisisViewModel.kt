package com.bolivia.platica.ui.screens.analisis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bolivia.platica.data.local.dao.CategoriaTotal
import com.bolivia.platica.data.local.entity.SavingsGoalEntity
import com.bolivia.platica.data.local.entity.TransactionEntity
import com.bolivia.platica.domain.repository.AccountRepository
import com.bolivia.platica.domain.repository.CategoryRepository
import com.bolivia.platica.domain.repository.GoalRepository
import com.bolivia.platica.domain.repository.TransactionRepository
import com.bolivia.platica.domain.usecase.Analitica
import com.bolivia.platica.domain.usecase.CalculadoraMetas
import com.bolivia.platica.domain.usecase.CalculadoraSaldos
import com.bolivia.platica.domain.usecase.CicloFinanciero
import com.bolivia.platica.domain.model.enums.TransactionType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class MetaProyeccion(
    val meta: SavingsGoalEntity,
    val progreso: Double,
    val restante: Double,
    val ahorroMensualEstimado: Double,
    val aporteNecesarioMensual: Double?,
    val seCumple: Boolean,
    val mesesParaLograr: Int?
)

data class CuentaResumen(
    val nombre: String,
    val ingresos: Double,
    val gastos: Double
)

data class AnalisisUiState(
    val nombreMes: String = CicloFinanciero.nombreMes(0),
    val ingresos: Double = 0.0,
    val gastos: Double = 0.0,
    val balance: Double = 0.0,
    val tasaAhorro: Double? = null,
    val porCategoria: List<CategoriaTotal> = emptyList(),
    val porCuenta: List<CuentaResumen> = emptyList(),
    val gastoProyectado: Double = 0.0,
    val balanceProyectado: Double = 0.0,
    val saldoAhorro: Double = 0.0,
    val proyecciones: List<MetaProyeccion> = emptyList()
)

@HiltViewModel
class AnalisisViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val goalRepository: GoalRepository,
    private val accountRepository: AccountRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val offsetMes = MutableStateFlow(0)

    val uiState: StateFlow<AnalisisUiState> =
        combine(
            offsetMes,
            transactionRepository.observarTodas(),
            goalRepository.observarTodas(),
            combine(
                accountRepository.observarTodas(),
                goalRepository.observarTodosLosAportes()
            ) { cuentas, aportes -> cuentas to aportes },
            categoryRepository.observarActivas()
        ) { offset, todasTx, metas, cuentasYAportes, categorias ->
            val (cuentas, aportes) = cuentasYAportes
            val rango = CicloFinanciero.rangoMes(offset)
            val delMes = todasTx.filter { it.fecha in rango }
            val ingresos = delMes.filter { it.tipo == TransactionType.INGRESO }.sumOf { it.monto }
            val gastos = delMes.filter { it.tipo == TransactionType.GASTO }.sumOf { it.monto }
            val balance = Analitica.balance(ingresos, gastos)

            val mapaCategorias = categorias.associate { it.id to it.nombre }
            val porCategoria = delMes
                .filter { it.tipo == TransactionType.GASTO }
                .groupBy { it.categoryId }
                .map { (catId, movs) ->
                    CategoriaTotal(
                        categoryId = catId,
                        categoria = catId?.let { mapaCategorias[it] } ?: "Sin categoría",
                        total = movs.sumOf { it.monto }
                    )
                }
                .sortedByDescending { it.total }

            val gastoProyectado = Analitica.gastoProyectado(gastos, diasTranscurridos(rango), diasTotales(rango))
            val ahorroMensual = if (balance > 0) balance else 0.0

            val nombresCuentas = cuentas.associate { it.id to it.nombre }
            val porCuenta = delMes
                .groupBy { it.accountId }
                .map { (cuentaId, movs) ->
                    CuentaResumen(
                        nombre = nombresCuentas[cuentaId] ?: "Cuenta eliminada",
                        ingresos = movs
                            .filter { it.tipo == TransactionType.INGRESO }
                            .sumOf { it.monto },
                        gastos = movs
                            .filter { it.tipo == TransactionType.GASTO }
                            .sumOf { it.monto }
                    )
                }
                .sortedByDescending { it.gastos + it.ingresos }

            val saldos = CalculadoraSaldos.saldosPorCuenta(cuentas, todasTx, aportes)
            val saldoAhorro = CalculadoraSaldos.reservaAhorro(saldos, cuentas)

            val proyecciones = metas.map { meta ->
                val meses = meta.fechaLimite?.let { CicloFinanciero.mesesRestantes(it) }
                MetaProyeccion(
                    meta = meta,
                    progreso = CalculadoraMetas.progreso(saldoAhorro, meta.montoObjetivo),
                    restante = CalculadoraMetas.restante(saldoAhorro, meta.montoObjetivo),
                    ahorroMensualEstimado = ahorroMensual,
                    aporteNecesarioMensual = CalculadoraMetas.aporteNecesarioMensual(
                        saldoAhorro, meta.montoObjetivo, meses
                    ),
                    seCumple = CalculadoraMetas.seCumpleEnFecha(
                        saldoAhorro, meta.montoObjetivo, ahorroMensual, meses
                    ),
                    mesesParaLograr = CalculadoraMetas.mesesParaLograr(
                        saldoAhorro, meta.montoObjetivo, ahorroMensual
                    )
                )
            }

            AnalisisUiState(
                nombreMes = CicloFinanciero.nombreMes(offset),
                ingresos = ingresos,
                gastos = gastos,
                balance = balance,
                tasaAhorro = Analitica.tasaAhorro(ingresos, gastos),
                porCategoria = porCategoria,
                porCuenta = porCuenta,
                gastoProyectado = gastoProyectado,
                balanceProyectado = ingresos - gastoProyectado,
                saldoAhorro = saldoAhorro,
                proyecciones = proyecciones
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AnalisisUiState()
        )

    fun mesAnterior() { offsetMes.value -= 1 }
    fun mesSiguiente() { offsetMes.value += 1 }

    private fun diasTranscurridos(rango: LongRange): Int {
        val hoy = System.currentTimeMillis()
        if (hoy < rango.first) return 1
        val unDia = 24L * 60 * 60 * 1000
        return (((minOf(hoy, rango.last) - rango.first) / unDia).toInt() + 1).coerceAtLeast(1)
    }

    private fun diasTotales(rango: LongRange): Int {
        val unDia = 24L * 60 * 60 * 1000
        return (((rango.last - rango.first) / unDia).toInt() + 1).coerceAtLeast(1)
    }
}
