package com.bolivia.platica.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.bolivia.platica.domain.model.enums.TransactionType

/**
 * Registro de un movimiento: ingreso, gasto o aporte a una meta.
 * Ver `docs/04_modelo_de_datos.md` §3.3.
 */
@Entity(
    tableName = "transactions",
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
        ),
        ForeignKey(
            entity = SavingsGoalEntity::class,
            parentColumns = ["id"],
            childColumns = ["goalId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("fecha"),
        Index("accountId"),
        Index("categoryId"),
        Index("goalId"),
        Index("tipo")
    ]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    /** Monto siempre positivo; el tipo define el signo en los cálculos. */
    val monto: Double,
    val fecha: Long,
    val tipo: TransactionType,
    val accountId: Long,
    val categoryId: Long? = null,
    val goalId: Long? = null,
    val nota: String? = null,
    /** Ingreso extraordinario (ej. bono que llegó tarde). Se excluye de promedios. */
    val esIrregular: Boolean = false,
    /** Generado automáticamente por una plantilla recurrente. */
    val esRecurrente: Boolean = false,
    val creadaEn: Long = System.currentTimeMillis()
)
