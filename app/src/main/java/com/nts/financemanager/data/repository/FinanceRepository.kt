package com.nts.financemanager.data.repository

import com.nts.financemanager.data.db.BudgetDao
import com.nts.financemanager.data.db.CategoryDao
import com.nts.financemanager.data.db.TransactionDao
import com.nts.financemanager.data.model.Budget
import com.nts.financemanager.data.model.Category
import com.nts.financemanager.data.model.Transaction
import kotlinx.coroutines.flow.Flow

/**
 * Repository that serves as the single source of truth for all financial data.
 * Abstracts the data layer from ViewModels.
 */
class FinanceRepository(
    private val transactionDao: TransactionDao,
    private val budgetDao: BudgetDao,
    private val categoryDao: CategoryDao
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
}
