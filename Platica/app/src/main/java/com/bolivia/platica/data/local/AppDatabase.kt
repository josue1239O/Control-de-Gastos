package com.bolivia.platica.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bolivia.platica.data.local.dao.AccountDao
import com.bolivia.platica.data.local.dao.BudgetDao
import com.bolivia.platica.data.local.dao.CategoryDao
import com.bolivia.platica.data.local.dao.GoalDao
import com.bolivia.platica.data.local.dao.InvestmentReturnDao
import com.bolivia.platica.data.local.dao.QuickTemplateDao
import com.bolivia.platica.data.local.dao.RecurringDao
import com.bolivia.platica.data.local.dao.TransactionDao
import com.bolivia.platica.data.local.entity.AccountEntity
import com.bolivia.platica.data.local.entity.BudgetEntity
import com.bolivia.platica.data.local.entity.CategoryEntity
import com.bolivia.platica.data.local.entity.GoalContributionEntity
import com.bolivia.platica.data.local.entity.InvestmentReturnEntity
import com.bolivia.platica.data.local.entity.QuickTemplateEntity
import com.bolivia.platica.data.local.entity.RecurringTransactionEntity
import com.bolivia.platica.data.local.entity.SavingsGoalEntity
import com.bolivia.platica.data.local.entity.TransactionEntity

/**
 * Base de datos local de Platica (Room sobre SQLite, cifrada con SQLCipher).
 * Es la ÚNICA fuente de verdad de la aplicación. No hay base remota.
 *
 * Ver `docs/04_modelo_de_datos.md`.
 */
@Database(
    entities = [
        AccountEntity::class,
        CategoryEntity::class,
        TransactionEntity::class,
        SavingsGoalEntity::class,
        GoalContributionEntity::class,
        BudgetEntity::class,
        RecurringTransactionEntity::class,
        InvestmentReturnEntity::class,
        QuickTemplateEntity::class
    ],
    version = 4,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun accountDao(): AccountDao
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao
    abstract fun goalDao(): GoalDao
    abstract fun budgetDao(): BudgetDao
    abstract fun recurringDao(): RecurringDao
    abstract fun investmentReturnDao(): InvestmentReturnDao
    abstract fun quickTemplateDao(): QuickTemplateDao

    companion object {
        const val NOMBRE = "platica.db"
    }
}
