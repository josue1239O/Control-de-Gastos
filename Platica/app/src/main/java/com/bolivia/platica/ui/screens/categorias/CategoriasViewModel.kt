package com.bolivia.platica.ui.screens.categorias

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bolivia.platica.data.local.entity.CategoryEntity
import com.bolivia.platica.domain.model.enums.CategoryType
import com.bolivia.platica.domain.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CategoriasUiState(
    val gastos: List<CategoryEntity> = emptyList(),
    val ingresos: List<CategoryEntity> = emptyList()
)

@HiltViewModel
class CategoriasViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    val uiState: StateFlow<CategoriasUiState> =
        categoryRepository.observarActivas()
            .map { lista ->
                CategoriasUiState(
                    gastos = lista.filter { it.tipo == CategoryType.GASTO },
                    ingresos = lista.filter { it.tipo == CategoryType.INGRESO }
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = CategoriasUiState()
            )

    fun crear(nombre: String, tipo: CategoryType, esFijo: Boolean) {
        viewModelScope.launch {
            categoryRepository.crear(CategoryEntity(nombre = nombre, tipo = tipo, esFijo = esFijo))
        }
    }

    fun actualizar(categoria: CategoryEntity) {
        viewModelScope.launch { categoryRepository.actualizar(categoria) }
    }

    fun eliminar(categoria: CategoryEntity) {
        viewModelScope.launch { categoryRepository.eliminar(categoria) }
    }
}
