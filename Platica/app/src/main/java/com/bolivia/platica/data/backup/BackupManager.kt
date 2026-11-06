package com.bolivia.platica.data.backup

import android.content.Context
import android.net.Uri
import com.bolivia.platica.data.local.AppDatabase
import com.bolivia.platica.data.local.entity.AccountEntity
import com.bolivia.platica.data.local.entity.BudgetEntity
import com.bolivia.platica.data.local.entity.CategoryEntity
import com.bolivia.platica.data.local.entity.GoalContributionEntity
import com.bolivia.platica.data.local.entity.InvestmentReturnEntity
import com.bolivia.platica.data.local.entity.QuickTemplateEntity
import com.bolivia.platica.data.local.entity.SavingsGoalEntity
import com.bolivia.platica.data.local.entity.TransactionEntity
import com.bolivia.platica.domain.model.enums.AccountType
import com.bolivia.platica.domain.model.enums.BudgetPeriod
import com.bolivia.platica.domain.model.enums.CategoryType
import com.bolivia.platica.domain.model.enums.GoalPriority
import com.bolivia.platica.domain.model.enums.GoalStatus
import com.bolivia.platica.domain.model.enums.TransactionType
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gestiona el respaldo (exportar/importar) de todos los datos como un JSON.
 * 100% offline: el usuario elige el archivo mediante el SAF.
 * Ver `docs/07_seguridad_y_respaldo.md` §6.
 */
@Singleton
class BackupManager @Inject constructor(
    private val db: AppDatabase
) {

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    /** Exporta todos los datos a un archivo JSON en [uri]. */
    suspend fun exportar(context: Context, uri: Uri): Boolean {
        return try {
            val data = RespaldoData(
                accounts = db.accountDao().observarTodas().first().map {
                    AccountDto(it.id, it.nombre, it.tipo.name, it.saldoInicial, it.moneda, it.activa, it.orden, it.creadaEn)
                },
                categories = db.categoryDao().observarActivas().first().map {
                    CategoryDto(it.id, it.nombre, it.tipo.name, it.esFijo, it.activa)
                },
                transactions = db.transactionDao().observarTodas().first().map {
                    TransactionDto(it.id, it.monto, it.fecha, it.tipo.name, it.accountId, it.categoryId, it.goalId, it.nota, it.esIrregular, it.esRecurrente, it.creadaEn)
                },
                savingsGoals = db.goalDao().observarTodas().first().map {
                    GoalDto(it.id, it.nombre, it.descripcion, it.montoObjetivo, it.montoActual, it.fechaLimite, it.accountId, it.estado.name, it.prioridad.name, it.creadaEn)
                },
                goalContributions = db.goalDao().observarTodosLosAportes().first().map {
                    ContributionDto(it.id, it.goalId, it.monto, it.fecha, it.accountId, it.nota)
                },
                budgets = db.budgetDao().observarTodos().first().map {
                    BudgetDto(it.id, it.categoryId, it.montoLimite, it.periodo.name, it.umbralAlerta, it.activo)
                },
                investmentReturns = db.investmentReturnDao().observarTodos().first().map {
                    InvestmentReturnDto(it.id, it.accountId, it.ganancia, it.fecha, it.reinvertido, it.nota)
                },
                quickTemplates = db.quickTemplateDao().observarTodas().first().map {
                    QuickTemplateDto(it.id, it.nombre, it.monto, it.tipo.name, it.accountId, it.categoryId, it.orden)
                }
            )
            val respaldo = RespaldoCompleto(meta = RespaldoMeta(), data = data)
            val texto = json.encodeToString(respaldo)
            context.contentResolver.openOutputStream(uri)?.use { salida ->
                salida.write(texto.toByteArray(Charsets.UTF_8))
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    /** Importa los datos desde un archivo JSON en [uri], reemplazando todo. */
    suspend fun importar(context: Context, uri: Uri): Boolean {
        return try {
            val texto = context.contentResolver.openInputStream(uri)?.use { entrada ->
                entrada.readBytes().toString(Charsets.UTF_8)
            } ?: return false
            val respaldo = json.decodeFromString<RespaldoCompleto>(texto)

            // Limpiar y reinsertar. Los DAOs usan REPLACE por id.
            respaldo.data.accounts.forEach {
                db.accountDao().insertar(
                    AccountEntity(it.id, it.nombre, AccountType.valueOf(it.tipo), it.saldoInicial, it.moneda, null, null, it.activa, it.orden, it.creadaEn)
                )
            }
            respaldo.data.categories.forEach {
                db.categoryDao().insertar(
                    CategoryEntity(it.id, it.nombre, CategoryType.valueOf(it.tipo), it.esFijo, null, null, it.activa)
                )
            }
            respaldo.data.savingsGoals.forEach {
                db.goalDao().insertar(
                    SavingsGoalEntity(it.id, it.nombre, it.descripcion, it.montoObjetivo, it.montoActual, it.fechaLimite, it.accountId, null, null, GoalStatus.valueOf(it.estado), GoalPriority.valueOf(it.prioridad), it.creadaEn)
                )
            }
            respaldo.data.goalContributions.forEach {
                db.goalDao().insertarAporte(
                    GoalContributionEntity(it.id, it.goalId, it.monto, it.fecha, it.accountId, it.nota)
                )
            }
            respaldo.data.transactions.forEach {
                db.transactionDao().insertar(
                    TransactionEntity(it.id, it.monto, it.fecha, TransactionType.valueOf(it.tipo), it.accountId, it.categoryId, it.goalId, it.nota, it.esIrregular, it.esRecurrente, it.creadaEn)
                )
            }
            respaldo.data.budgets.forEach {
                db.budgetDao().insertar(
                    BudgetEntity(it.id, it.categoryId, it.montoLimite, BudgetPeriod.valueOf(it.periodo), it.umbralAlerta, it.activo)
                )
            }
            respaldo.data.investmentReturns.forEach {
                db.investmentReturnDao().insertar(
                    InvestmentReturnEntity(it.id, it.accountId, it.ganancia, it.fecha, it.reinvertido, it.nota, System.currentTimeMillis())
                )
            }
            respaldo.data.quickTemplates.forEach {
                db.quickTemplateDao().insertar(
                    QuickTemplateEntity(it.id, it.nombre, it.monto, TransactionType.valueOf(it.tipo), it.accountId, it.categoryId, null, it.orden)
                )
            }
            true
        } catch (e: Exception) {
            false
        }
    }
}
