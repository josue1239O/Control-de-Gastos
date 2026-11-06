package com.bolivia.platica.ui.screens.recurrentes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bolivia.platica.data.local.dao.RecurringDao
import com.bolivia.platica.data.local.entity.RecurringTransactionEntity
import com.bolivia.platica.domain.model.enums.Frequency
import com.bolivia.platica.domain.model.enums.TransactionType
import com.bolivia.platica.domain.repository.AccountRepository
import com.bolivia.platica.domain.repository.CategoryRepository
import com.bolivia.platica.data.local.entity.AccountEntity
import com.bolivia.platica.data.local.entity.CategoryEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RecurrentesUiState(
    val recurrentes: List<RecurringTransactionEntity> = emptyList(),
    val cuentas: List<AccountEntity> = emptyList(),
    val categorias: List<CategoryEntity> = emptyList()
)

@HiltViewModel
class RecurrentesViewModel @Inject constructor(
    private val dao: RecurringDao,
    private val accountRepository: AccountRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    val uiState: StateFlow<RecurrentesUiState> =
        combine(
            dao.observarTodas(),
            accountRepository.observarActivas(),
            categoryRepository.observarActivas()
        ) { recs, cuentas, cats ->
            RecurrentesUiState(recurrentes = recs, cuentas = cuentas, categorias = cats)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = RecurrentesUiState()
        )

    fun crear(
        monto: Double,
        tipo: TransactionType,
        accountId: Long,
        categoryId: Long?,
        frecuencia: Frequency,
        nota: String?
    ) {
        viewModelScope.launch {
            dao.insertar(
                RecurringTransactionEntity(
                    monto = monto,
                    tipo = tipo,
                    accountId = accountId,
                    categoryId = categoryId,
                    frecuencia = frecuencia,
                    proximaFecha = System.currentTimeMillis(),
                    nota = nota
                )
            )
        }
    }

    fun eliminar(item: RecurringTransactionEntity) {
        viewModelScope.launch { dao.eliminar(item) }
    }
}
