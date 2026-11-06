package com.bolivia.platica.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.bolivia.platica.domain.model.enums.TransactionType

/**
 * Plantilla de gasto/ingreso rápido de precio fijo.
 *
 * Sirve para gastos comunes que siempre cuestan lo mismo (ej: "Pasaje Bs. 3").
 * Al registrar, el usuario solo toca la plantilla y se crea el movimiento
 * en un toque.
 * Ver `docs/04_modelo_de_datos.md`.
 */
@Entity(
    tableName = "quick_templates",
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
    indices = [Index("accountId"), Index("categoryId")]
)
data class QuickTemplateEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    /** Nombre visible de la plantilla. Ej: "Pasaje". */
    val nombre: String,
    val monto: Double,
    val tipo: TransactionType = TransactionType.GASTO,
    val accountId: Long,
    val categoryId: Long? = null,
    val icono: String? = null,
    val orden: Int = 0
)
