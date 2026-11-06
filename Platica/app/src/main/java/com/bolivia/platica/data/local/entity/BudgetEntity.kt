package com.bolivia.platica.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.bolivia.platica.domain.model.enums.BudgetPeriod

/**
 * Presupuesto por categoría (límite de gasto).
 * Ver `docs/04_modelo_de_datos.md` §3.6.
 */
@Entity(
    tableName = "budgets",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("categoryId")]
)
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val categoryId: Long,
    val montoLimite: Double,
    val periodo: BudgetPeriod = BudgetPeriod.MENSUAL,
    /** % a partir del cual se alerta (por defecto 80). */
    val umbralAlerta: Int = 80,
    val activo: Boolean = true
)
