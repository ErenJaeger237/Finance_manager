package com.nts.financemanager.data.repository

import com.nts.financemanager.data.db.BudgetDao
import com.nts.financemanager.data.db.CategoryDao
import com.nts.financemanager.data.db.FinancialGoalDao
import com.nts.financemanager.data.db.TransactionDao
import com.nts.financemanager.data.model.Budget
import com.nts.financemanager.data.model.Category
import com.nts.financemanager.data.model.FinancialGoal
import com.nts.financemanager.data.model.Transaction
import kotlinx.coroutines.flow.Flow

class FinanceRepository(
    private val transactionDao: TransactionDao,
    private val budgetDao: BudgetDao,
    private val categoryDao: CategoryDao,
    private val financialGoalDao: FinancialGoalDao
) {
    // ── Transactions ──────────────────────────────────────
    fun getAllTransactions(): Flow<List<Transaction>> = transactionDao.getAllTransactions()

    fun getTransactionsByDateRange(startDate: Long, endDate: Long): Flow<List<Transaction>> =
        transactionDao.getTransactionsByDateRange(startDate, endDate)

    fun getTransactionsByCategory(category: String): Flow<List<Transaction>> =
        transactionDao.getTransactionsByCategory(category)

    fun getTotalIncome(): Flow<Double> = transactionDao.getTotalIncome()
    fun getTotalExpense(): Flow<Double> = transactionDao.getTotalExpense()

    fun getExpenseByCategory(category: String, startDate: Long, endDate: Long): Flow<Double> =
        transactionDao.getExpenseByCategory(category, startDate, endDate)

    suspend fun insertTransaction(transaction: Transaction) = transactionDao.insert(transaction)
    
    suspend fun insertTransactionWithGoalUpdate(transaction: Transaction) {
        transactionDao.insert(transaction)
        if (transaction.goalId != null) {
            val goal = financialGoalDao.getGoalById(transaction.goalId)
            if (goal != null) {
                // Both income (direct deposit) and expense (allocation from balance) 
                // increase the goal's saved amount.
                financialGoalDao.update(goal.copy(savedAmount = goal.savedAmount + transaction.amount))
            }
        }
    }

    suspend fun updateTransaction(transaction: Transaction) = transactionDao.update(transaction)
    suspend fun deleteTransaction(transaction: Transaction) = transactionDao.delete(transaction)

    // ── Budgets ───────────────────────────────────────────
    fun getAllBudgets(): Flow<List<Budget>> = budgetDao.getAllBudgets()
    fun getBudgetsByMonth(month: String): Flow<List<Budget>> = budgetDao.getBudgetsByMonth(month)

    suspend fun getBudgetByCategoryAndMonth(category: String, month: String): Budget? =
        budgetDao.getBudgetByCategoryAndMonth(category, month)

    suspend fun insertBudget(budget: Budget) = budgetDao.insert(budget)
    suspend fun updateBudget(budget: Budget) = budgetDao.update(budget)
    suspend fun deleteBudget(budget: Budget) = budgetDao.delete(budget)

    // ── Categories ────────────────────────────────────────
    fun getAllCategories(): Flow<List<Category>> = categoryDao.getAllCategories()
    suspend fun insertCategory(category: Category) = categoryDao.insert(category)
    suspend fun deleteCategory(category: Category) = categoryDao.delete(category)

    // ── Financial Goals ───────────────────────────────────
    fun getAllGoals(): Flow<List<FinancialGoal>> = financialGoalDao.getAllGoals()
    suspend fun getGoalById(id: Int): FinancialGoal? = financialGoalDao.getGoalById(id)
    suspend fun insertGoal(goal: FinancialGoal) = financialGoalDao.insert(goal)
    suspend fun updateGoal(goal: FinancialGoal) = financialGoalDao.update(goal)
    suspend fun deleteGoal(goal: FinancialGoal) = financialGoalDao.delete(goal)
}
