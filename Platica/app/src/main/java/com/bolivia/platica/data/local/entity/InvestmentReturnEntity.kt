package com.bolivia.platica.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Registro de rendimiento/ganancia de una inversión.
 *
 * Permite ver cuánto generó una cuenta de inversión en una fecha concreta,
 * y cuánto seguirá circulando/invirtiéndose.
 * Ver `docs/04_modelo_de_datos.md`.
 */
@Entity(
    tableName = "investment_returns",
    foreignKeys = [
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["accountId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("accountId"), Index("fecha")]
)
data class InvestmentReturnEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    /** Cuenta de inversión a la que corresponde el rendimiento. */
    val accountId: Long,
    /** Ganancia generada (positiva = ganancia; puede ser negativa = pérdida). */
    val ganancia: Double,
    /** Fecha en que se registró/generó la ganancia. */
    val fecha: Long,
    /** Monto que se reinvierte (sigue circulando). 0 = se retiró todo. */
    val reinvertido: Double = 0.0,
    val nota: String? = null,
    val creadaEn: Long = System.currentTimeMillis()
)
