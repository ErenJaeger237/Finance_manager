package com.nts.financemanager.data.db

import androidx.room.*
import com.nts.financemanager.data.model.FinancialGoal
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Financial Goal operations.
 */
@Dao
interface FinancialGoalDao {

    @Query("SELECT * FROM financial_goals ORDER BY deadline ASC")
    fun getAllGoals(): Flow<List<FinancialGoal>>

    @Query("SELECT * FROM financial_goals WHERE id = :id")
    suspend fun getGoalById(id: Int): FinancialGoal?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(goal: FinancialGoal)

    @Update
    suspend fun update(goal: FinancialGoal)

    @Delete
    suspend fun delete(goal: FinancialGoal)
}
