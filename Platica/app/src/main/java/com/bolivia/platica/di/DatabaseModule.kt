package com.bolivia.platica.di

import android.content.Context
import androidx.room.Room
import com.bolivia.platica.data.local.AppDatabase
import com.bolivia.platica.data.local.MIGRATIONS
import com.bolivia.platica.data.local.security.DatabaseFactory
import com.bolivia.platica.data.local.security.SecureKeyManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.sqlcipher.database.SupportFactory
import javax.inject.Singleton

/**
 * Módulo de Hilt que provee la base de datos cifrada y los DAOs.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideSecureKeyManager(@ApplicationContext context: Context): SecureKeyManager =
        SecureKeyManager(context)

    @Provides
    @Singleton
    fun provideSupportFactory(keyManager: SecureKeyManager): SupportFactory {
        val passphrase = keyManager.obtenerPassphrase()
        return SupportFactory(passphrase)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        supportFactory: SupportFactory
    ): AppDatabase {
        DatabaseFactory.cargarLibrerias(context)
        return Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.NOMBRE)
            .openHelperFactory(supportFactory)
            .addMigrations(*MIGRATIONS)
            .build()
    }

    @Provides fun provideAccountDao(db: AppDatabase) = db.accountDao()
    @Provides fun provideCategoryDao(db: AppDatabase) = db.categoryDao()
    @Provides fun provideTransactionDao(db: AppDatabase) = db.transactionDao()
    @Provides fun provideGoalDao(db: AppDatabase) = db.goalDao()
    @Provides fun provideBudgetDao(db: AppDatabase) = db.budgetDao()
    @Provides fun provideRecurringDao(db: AppDatabase) = db.recurringDao()
    @Provides fun provideInvestmentReturnDao(db: AppDatabase) = db.investmentReturnDao()
    @Provides fun provideQuickTemplateDao(db: AppDatabase) = db.quickTemplateDao()
}
