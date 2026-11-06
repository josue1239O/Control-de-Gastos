package com.bolivia.platica.data.local

import androidx.room.TypeConverter
import com.bolivia.platica.domain.model.enums.AccountType
import com.bolivia.platica.domain.model.enums.BudgetPeriod
import com.bolivia.platica.domain.model.enums.CategoryType
import com.bolivia.platica.domain.model.enums.Frequency
import com.bolivia.platica.domain.model.enums.GoalPriority
import com.bolivia.platica.domain.model.enums.GoalStatus
import com.bolivia.platica.domain.model.enums.TransactionType

/**
 * Convierte enumeraciones a String y viceversa, para su almacenamiento en SQLite.
 */
class Converters {

    @TypeConverter
    fun accountTypeToString(value: AccountType): String = value.name

    @TypeConverter
    fun stringToAccountType(value: String): AccountType = AccountType.valueOf(value)

    @TypeConverter
    fun categoryTypeToString(value: CategoryType): String = value.name

    @TypeConverter
    fun stringToCategoryType(value: String): CategoryType = CategoryType.valueOf(value)

    @TypeConverter
    fun transactionTypeToString(value: TransactionType): String = value.name

    @TypeConverter
    fun stringToTransactionType(value: String): TransactionType = TransactionType.valueOf(value)

    @TypeConverter
    fun goalStatusToString(value: GoalStatus): String = value.name

    @TypeConverter
    fun stringToGoalStatus(value: String): GoalStatus = GoalStatus.valueOf(value)

    @TypeConverter
    fun goalPriorityToString(value: GoalPriority): String = value.name

    @TypeConverter
    fun stringToGoalPriority(value: String): GoalPriority = GoalPriority.valueOf(value)

    @TypeConverter
    fun budgetPeriodToString(value: BudgetPeriod): String = value.name

    @TypeConverter
    fun stringToBudgetPeriod(value: String): BudgetPeriod = BudgetPeriod.valueOf(value)

    @TypeConverter
    fun frequencyToString(value: Frequency): String = value.name

    @TypeConverter
    fun stringToFrequency(value: String): Frequency = Frequency.valueOf(value)
}
