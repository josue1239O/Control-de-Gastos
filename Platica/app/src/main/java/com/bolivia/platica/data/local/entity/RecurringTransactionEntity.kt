package com.bolivia.platica.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.bolivia.platica.domain.model.enums.Frequency
import com.bolivia.platica.domain.model.enums.TransactionType

/**
 * Plantilla de transacción recurrente (sueldo, suscripciones, gastos fijos).
 * Ver `docs/04_modelo_de_datos.md` §3.7.
 */
@Entity(
    tableName = "recurring_transactions",
    foreignKeys = [
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["accountId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("proximaFecha"), Index("activa"), Index("accountId"), Index("categoryId")]
)
data class RecurringTransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val monto: Double,
    val tipo: TransactionType,
    val accountId: Long,
    val categoryId: Long? = null,
    val nota: String? = null,
    val frecuencia: Frequency,
    /** Próxima fecha (epoch millis) en que debe generarse. */
    val proximaFecha: Long,
    val ultimaGenerada: Long? = null,
    val activa: Boolean = true
)
