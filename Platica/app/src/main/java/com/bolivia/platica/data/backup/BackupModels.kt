package com.bolivia.platica.data.backup

import kotlinx.serialization.Serializable

/**
 * Modelos serializables del respaldo. Contienen todos los datos del usuario.
 * Ver `docs/07_seguridad_y_respaldo.md` §6.
 */

@Serializable
data class RespaldoCompleto(
    val meta: RespaldoMeta,
    val data: RespaldoData
)

@Serializable
data class RespaldoMeta(
    val app: String = "Platica",
    val schemaVersion: Int = 1,
    val appVersion: String = "1.2.0",
    val exportadoEn: Long = System.currentTimeMillis()
)

@Serializable
data class RespaldoData(
    val accounts: List<AccountDto> = emptyList(),
    val categories: List<CategoryDto> = emptyList(),
    val transactions: List<TransactionDto> = emptyList(),
    val savingsGoals: List<GoalDto> = emptyList(),
    val goalContributions: List<ContributionDto> = emptyList(),
    val budgets: List<BudgetDto> = emptyList(),
    val investmentReturns: List<InvestmentReturnDto> = emptyList(),
    val quickTemplates: List<QuickTemplateDto> = emptyList()
)

@Serializable
data class AccountDto(
    val id: Long, val nombre: String, val tipo: String, val saldoInicial: Double,
    val moneda: String, val activa: Boolean, val orden: Int, val creadaEn: Long
)

@Serializable
data class CategoryDto(
    val id: Long, val nombre: String, val tipo: String, val esFijo: Boolean, val activa: Boolean
)

@Serializable
data class TransactionDto(
    val id: Long, val monto: Double, val fecha: Long, val tipo: String,
    val accountId: Long, val categoryId: Long?, val goalId: Long?, val nota: String?,
    val esIrregular: Boolean, val esRecurrente: Boolean, val creadaEn: Long
)

@Serializable
data class GoalDto(
    val id: Long, val nombre: String, val descripcion: String?, val montoObjetivo: Double,
    val montoActual: Double, val fechaLimite: Long?, val accountId: Long?, val estado: String,
    /** Opcional para que los respaldos viejos sigan siendo compatibles. */
    val prioridad: String = "MEDIA",
    val creadaEn: Long
)

@Serializable
data class ContributionDto(
    val id: Long, val goalId: Long, val monto: Double, val fecha: Long,
    val accountId: Long, val nota: String?
)

@Serializable
data class BudgetDto(
    val id: Long, val categoryId: Long, val montoLimite: Double,
    val periodo: String, val umbralAlerta: Int, val activo: Boolean
)

@Serializable
data class InvestmentReturnDto(
    val id: Long, val accountId: Long, val ganancia: Double, val fecha: Long,
    val reinvertido: Double, val nota: String?
)

@Serializable
data class QuickTemplateDto(
    val id: Long, val nombre: String, val monto: Double, val tipo: String,
    val accountId: Long, val categoryId: Long?, val orden: Int
)
