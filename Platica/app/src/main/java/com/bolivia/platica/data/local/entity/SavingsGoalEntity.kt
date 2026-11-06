package com.bolivia.platica.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.bolivia.platica.domain.model.enums.GoalPriority
import com.bolivia.platica.domain.model.enums.GoalStatus

/**
 * Meta de ahorro / compra (ej. "Comprar laptop", "Fondo de emergencia", "Viaje").
 * Ver `docs/04_modelo_de_datos.md` §3.4.
 */
@Entity(
    tableName = "savings_goals",
    foreignKeys = [
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["accountId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("estado"), Index("accountId")]
)
data class SavingsGoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    /** ¿Qué se quiere comprar o lograr? Ej: "Laptop", "Viaje a Sucre". */
    val nombre: String,
    /** Descripción opcional (marca, modelo, detalle). */
    val descripcion: String? = null,
    /** ¿Cuánto cuesta? (monto objetivo). */
    val montoObjetivo: Double,
    /** Monto acumulado (desnormalizado para lectura rápida). */
    val montoActual: Double = 0.0,
    /** Fecha límite deseada (epoch millis). */
    val fechaLimite: Long? = null,
    val accountId: Long? = null,
    val icono: String? = null,
    val color: Int? = null,
    val estado: GoalStatus = GoalStatus.ACTIVA,
    /** Importancia relativa: las de prioridad Alta aparecen primero. */
    @ColumnInfo(defaultValue = "MEDIA")
    val prioridad: GoalPriority = GoalPriority.MEDIA,
    val creadaEn: Long = System.currentTimeMillis()
)
