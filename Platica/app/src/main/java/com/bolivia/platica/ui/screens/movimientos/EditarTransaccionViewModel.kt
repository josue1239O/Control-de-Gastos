package com.bolivia.platica.ui.screens.movimientos

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bolivia.platica.data.local.entity.AccountEntity
import com.bolivia.platica.data.local.entity.CategoryEntity
import com.bolivia.platica.data.local.entity.QuickTemplateEntity
import com.bolivia.platica.data.local.entity.TransactionEntity
import com.bolivia.platica.domain.model.enums.CategoryType
import com.bolivia.platica.domain.model.enums.TransactionType
import com.bolivia.platica.domain.repository.AccountRepository
import com.bolivia.platica.domain.repository.CategoryRepository
import com.bolivia.platica.domain.repository.GoalRepository
import com.bolivia.platica.domain.repository.QuickTemplateRepository
import com.bolivia.platica.domain.repository.TransactionRepository
import com.bolivia.platica.domain.usecase.CalculadoraSaldos
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditarTransaccionUiState(
    val esEdicion: Boolean = false,
    val tipo: TransactionType = TransactionType.GASTO,
    val monto: String = "",
    val fecha: Long = System.currentTimeMillis(),
    val accountId: Long? = null,
    val categoryId: Long? = null,
    val nota: String = "",
    val esIrregular: Boolean = false,
    val cuentas: List<AccountEntity> = emptyList(),
    /** Saldo real de cada cuenta (inicial + movimientos) para mostrarlo al elegir. */
    val saldos: Map<Long, Double> = emptyMap(),
    val categorias: List<CategoryEntity> = emptyList(),
    val plantillas: List<QuickTemplateEntity> = emptyList(),
    val mensajeError: String? = null,
    val guardado: Boolean = false
)

/**
 * ViewModel para crear o editar una transacción (gasto/ingreso).
 * Incluye plantillas de gasto rápido de precio fijo (ej: Pasaje).
 */
@HiltViewModel
class EditarTransaccionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository,
    private val categoryRepository: CategoryRepository,
    private val quickTemplateRepository: QuickTemplateRepository,
    private val goalRepository: GoalRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val transaccionId: Long = savedStateHandle.get<String>("id")?.toLongOrNull() ?: 0L

    /** Movimiento original cuando se está editando (para no perder sus datos). */
    private var original: TransactionEntity? = null

    private val _state = MutableStateFlow(EditarTransaccionUiState())
    val state: StateFlow<EditarTransaccionUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                accountRepository.observarActivas(),
                transactionRepository.observarTodas(),
                goalRepository.observarTodosLosAportes()
            ) { cuentas, transacciones, aportes ->
                Triple(cuentas, transacciones, aportes)
            }.collect { (cuentas, transacciones, aportes) ->
                val saldos = CalculadoraSaldos.saldosPorCuenta(cuentas, transacciones, aportes)
                _state.update {
                    it.copy(
                        cuentas = cuentas,
                        saldos = saldos,
                        accountId = it.accountId ?: cuentas.firstOrNull()?.id
                    )
                }
            }
        }
        viewModelScope.launch {
            categoryRepository.observarActivas().collect { cats ->
                _state.update { it.copy(categorias = cats) }
            }
        }
        viewModelScope.launch {
            quickTemplateRepository.observarTodas().collect { pl ->
                _state.update { it.copy(plantillas = pl) }
            }
        }
        if (transaccionId > 0L) {
            viewModelScope.launch {
                transactionRepository.obtenerPorId(transaccionId)?.let { t ->
                    original = t
                    _state.update {
                        it.copy(
                            esEdicion = true,
                            tipo = t.tipo,
                            monto = t.monto.toString(),
                            fecha = t.fecha,
                            accountId = t.accountId,
                            categoryId = t.categoryId,
                            nota = t.nota ?: "",
                            esIrregular = t.esIrregular
                        )
                    }
                }
            }
        }
    }

    fun cambiarTipo(tipo: TransactionType) = _state.update { it.copy(tipo = tipo, categoryId = null) }

    fun cambiarMonto(valor: String) =
        _state.update { it.copy(monto = valor.filter { c -> c.isDigit() || c == '.' }, mensajeError = null) }

    fun cambiarFecha(fecha: Long) = _state.update { it.copy(fecha = fecha) }

    fun cambiarCuenta(id: Long) = _state.update { it.copy(accountId = id) }

    fun cambiarCategoria(id: Long) = _state.update { it.copy(categoryId = id) }

    fun cambiarNota(valor: String) = _state.update { it.copy(nota = valor) }

    fun cambiarIrregular(valor: Boolean) = _state.update { it.copy(esIrregular = valor) }

    /** Aplica una plantilla de gasto rápido: precarga monto, cuenta y categoría. */
    fun aplicarPlantilla(plantilla: QuickTemplateEntity) {
        _state.update {
            it.copy(
                tipo = plantilla.tipo,
                monto = plantilla.monto.toString(),
                accountId = plantilla.accountId,
                categoryId = plantilla.categoryId,
                nota = plantilla.nombre,
                mensajeError = null
            )
        }
    }

    fun categoriasDelTipo(): List<CategoryEntity> {
        val tipoCat = if (_state.value.tipo == TransactionType.INGRESO) CategoryType.INGRESO else CategoryType.GASTO
        return _state.value.categorias.filter { it.tipo == tipoCat }
    }

    fun guardar() {
        val s = _state.value
        val monto = s.monto.toDoubleOrNull()
        if (monto == null || monto <= 0.0) {
            _state.update { it.copy(mensajeError = "Ingresá un monto válido") }
            return
        }
        if (s.accountId == null) {
            _state.update { it.copy(mensajeError = "Elegí una cuenta") }
            return
        }
        viewModelScope.launch {
            val entidad = TransactionEntity(
                id = transaccionId,
                monto = monto,
                fecha = s.fecha,
                tipo = s.tipo,
                accountId = s.accountId,
                categoryId = s.categoryId,
                goalId = original?.goalId,
                nota = s.nota.ifBlank { null },
                esIrregular = s.esIrregular,
                esRecurrente = original?.esRecurrente ?: false,
                creadaEn = original?.creadaEn ?: System.currentTimeMillis()
            )
            if (s.esEdicion) transactionRepository.actualizar(entidad)
            else transactionRepository.crear(entidad)
            _state.update { it.copy(guardado = true) }
        }
    }
}
