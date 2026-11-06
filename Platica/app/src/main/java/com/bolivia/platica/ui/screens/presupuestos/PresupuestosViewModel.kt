package com.bolivia.platica.ui.screens.presupuestos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bolivia.platica.data.local.entity.BudgetEntity
import com.bolivia.platica.data.local.entity.CategoryEntity
import com.bolivia.platica.domain.model.enums.CategoryType
import com.bolivia.platica.domain.repository.BudgetRepository
import com.bolivia.platica.domain.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PresupuestoFila(
    val categoria: String,
    val montoLimite: Double
)

data class PresupuestosUiState(
    val presupuestos: List<PresupuestoFila> = emptyList(),
    val categoriasGasto: List<CategoryEntity> = emptyList()
)

@HiltViewModel
class PresupuestosViewModel @Inject constructor(
    private val budgetRepository: BudgetRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    val uiState: StateFlow<PresupuestosUiState> =
        combine(
            budgetRepository.observarActivos(),
            categoryRepository.observarActivas()
        ) { budgets, cats ->
            val mapa = cats.associate { it.id to it.nombre }
            PresupuestosUiState(
                presupuestos = budgets.map {
                    PresupuestoFila(mapa[it.categoryId] ?: "Categoría", it.montoLimite)
                },
                categoriasGasto = cats.filter { it.tipo == CategoryType.GASTO }
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PresupuestosUiState()
        )

    fun crear(categoryId: Long, limite: Double) {
        viewModelScope.launch {
            budgetRepository.crear(BudgetEntity(categoryId = categoryId, montoLimite = limite))
        }
    }
}
