package com.nts.financemanager.data.db

import androidx.room.*
import com.nts.financemanager.data.model.Budget
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Budget operations.
 */
@Dao
interface BudgetDao {

    @Query("SELECT * FROM budgets ORDER BY category ASC")
    fun getAllBudgets(): Flow<List<Budget>>

    @Query("SELECT * FROM budgets WHERE month = :month")
    fun getBudgetsByMonth(month: String): Flow<List<Budget>>

    @Query("SELECT * FROM budgets WHERE category = :category AND month = :month LIMIT 1")
    suspend fun getBudgetByCategoryAndMonth(category: String, month: String): Budget?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(budget: Budget)

    @Update
    suspend fun update(budget: Budget)

    @Delete
    suspend fun delete(budget: Budget)
}
