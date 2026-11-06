package com.bolivia.platica.di

import com.bolivia.platica.data.repository.AccountRepositoryImpl
import com.bolivia.platica.data.repository.BudgetRepositoryImpl
import com.bolivia.platica.data.repository.CategoryRepositoryImpl
import com.bolivia.platica.data.repository.GoalRepositoryImpl
import com.bolivia.platica.data.repository.InvestmentRepositoryImpl
import com.bolivia.platica.data.repository.QuickTemplateRepositoryImpl
import com.bolivia.platica.data.repository.TransactionRepositoryImpl
import com.bolivia.platica.domain.repository.AccountRepository
import com.bolivia.platica.domain.repository.BudgetRepository
import com.bolivia.platica.domain.repository.CategoryRepository
import com.bolivia.platica.domain.repository.GoalRepository
import com.bolivia.platica.domain.repository.InvestmentRepository
import com.bolivia.platica.domain.repository.QuickTemplateRepository
import com.bolivia.platica.domain.repository.TransactionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Enlaza cada interfaz de repositorio con su implementación concreta.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindAccountRepository(impl: AccountRepositoryImpl): AccountRepository

    @Binds @Singleton
    abstract fun bindCategoryRepository(impl: CategoryRepositoryImpl): CategoryRepository

    @Binds @Singleton
    abstract fun bindTransactionRepository(impl: TransactionRepositoryImpl): TransactionRepository

    @Binds @Singleton
    abstract fun bindGoalRepository(impl: GoalRepositoryImpl): GoalRepository

    @Binds @Singleton
    abstract fun bindBudgetRepository(impl: BudgetRepositoryImpl): BudgetRepository

    @Binds @Singleton
    abstract fun bindInvestmentRepository(impl: InvestmentRepositoryImpl): InvestmentRepository

    @Binds @Singleton
    abstract fun bindQuickTemplateRepository(impl: QuickTemplateRepositoryImpl): QuickTemplateRepository
}
