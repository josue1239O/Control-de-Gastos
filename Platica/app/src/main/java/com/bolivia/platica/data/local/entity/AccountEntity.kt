package com.bolivia.platica.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.bolivia.platica.domain.model.enums.AccountType

/**
 * Cuenta o fuente de dinero (Efectivo, Billetera QR, Inversión).
 * Ver `docs/04_modelo_de_datos.md` §3.1.
 */
@Entity(
    tableName = "accounts",
    indices = [Index("activa"), Index("orden")]
)
data class AccountEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val tipo: AccountType,
    /** Saldo con el que se creó la cuenta. */
    val saldoInicial: Double = 0.0,
    val moneda: String = "BOB",
    val icono: String? = null,
    val color: Int? = null,
    val activa: Boolean = true,
    val orden: Int = 0,
    val creadaEn: Long = System.currentTimeMillis()
)
