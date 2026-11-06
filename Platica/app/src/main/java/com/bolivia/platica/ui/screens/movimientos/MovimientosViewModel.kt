package com.bolivia.platica.ui.screens.movimientos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bolivia.platica.data.local.entity.TransactionEntity
import com.bolivia.platica.domain.repository.AccountRepository
import com.bolivia.platica.domain.repository.CategoryRepository
import com.bolivia.platica.domain.repository.TransactionRepository
import com.bolivia.platica.domain.usecase.CicloFinanciero
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Un movimiento con los datos legibles de la cuenta y la categoría que afecta. */
data class MovimientoItem(
    val transaccion: TransactionEntity,
    val cuenta: String,
    val categoria: String?
)

data class MovimientosUiState(
    val movimientos: List<MovimientoItem> = emptyList(),
    val nombreMes: String = CicloFinanciero.nombreMes(0),
    val totalIngresos: Double = 0.0,
    val totalGastos: Double = 0.0
)

@HiltViewModel
class MovimientosViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val offsetMes = MutableStateFlow(0)

    val uiState: StateFlow<MovimientosUiState> =
        combine(
            offsetMes,
            transactionRepository.observarTodas(),
            accountRepository.observarTodas(),
            categoryRepository.observarActivas()
        ) { offset, todas, cuentas, categorias ->
            val nombresCuentas = cuentas.associate { it.id to it.nombre }
            val nombresCategorias = categorias.associate { it.id to it.nombre }
            val rango = CicloFinanciero.rangoMes(offset)
            val filtradas = todas
                .filter { it.fecha in rango }
                .map { t ->
                    MovimientoItem(
                        transaccion = t,
                        cuenta = nombresCuentas[t.accountId] ?: "Cuenta eliminada",
                        categoria = t.categoryId?.let { nombresCategorias[it] }
                    )
                }

            MovimientosUiState(
                movimientos = filtradas,
                nombreMes = CicloFinanciero.nombreMes(offset),
                totalIngresos = filtradas
                    .filter { it.transaccion.tipo.name == "INGRESO" }
                    .sumOf { it.transaccion.monto },
                totalGastos = filtradas
                    .filter { it.transaccion.tipo.name == "GASTO" }
                    .sumOf { it.transaccion.monto }
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MovimientosUiState()
        )

    fun mesAnterior() { offsetMes.value -= 1 }

    fun mesSiguiente() { offsetMes.value += 1 }

    fun eliminar(transaccion: TransactionEntity) {
        viewModelScope.launch { transactionRepository.eliminar(transaccion) }
    }
}
