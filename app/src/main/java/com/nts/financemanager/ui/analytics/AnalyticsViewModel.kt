package com.nts.financemanager.ui.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.nts.financemanager.data.model.Transaction
import com.nts.financemanager.data.model.TransactionType
import com.nts.financemanager.data.repository.FinanceRepository
import kotlinx.coroutines.flow.*
import java.util.Calendar

data class CategoryInsight(
    val category: String,
    val thisMonth: Double,
    val lastMonth: Double
) {
    val changePercent: Double
        get() = if (lastMonth > 0) ((thisMonth - lastMonth) / lastMonth * 100) else 0.0
}

data class AnalyticsUiState(
    val thisMonthIncome: Double = 0.0,
    val thisMonthExpense: Double = 0.0,
    val lastMonthIncome: Double = 0.0,
    val lastMonthExpense: Double = 0.0,
    val savingsRate: Double = 0.0,
    val categoryInsights: List<CategoryInsight> = emptyList(),
    val dailyExpenses: List<Pair<Int, Double>> = emptyList() // day-of-month to amount
)

class AnalyticsViewModel(private val repository: FinanceRepository) : ViewModel() {

    val uiState: StateFlow<AnalyticsUiState> = repository.getAllTransactions()
        .map { transactions -> computeAnalytics(transactions) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AnalyticsUiState())

    private fun computeAnalytics(transactions: List<Transaction>): AnalyticsUiState {
        val cal = Calendar.getInstance()
        val thisYear = cal.get(Calendar.YEAR)
        val thisMonth = cal.get(Calendar.MONTH)

        cal.set(thisYear, thisMonth, 1, 0, 0, 0)
        val thisMonthStart = cal.timeInMillis
        cal.add(Calendar.MONTH, 1)
        val thisMonthEnd = cal.timeInMillis

        cal.set(thisYear, thisMonth - 1, 1, 0, 0, 0)
        val lastMonthStart = cal.timeInMillis
        cal.set(thisYear, thisMonth, 1, 0, 0, 0)
        val lastMonthEnd = cal.timeInMillis

        val thisMonthTxns = transactions.filter { it.date in thisMonthStart until thisMonthEnd }
        val lastMonthTxns = transactions.filter { it.date in lastMonthStart until lastMonthEnd }

        val thisIncome = thisMonthTxns.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val thisExpense = thisMonthTxns.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
        val lastIncome = lastMonthTxns.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val lastExpense = lastMonthTxns.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }

        val savingsRate = if (thisIncome > 0) ((thisIncome - thisExpense) / thisIncome * 100) else 0.0

        // Category insights
        val thisExpenseByCategory = thisMonthTxns
            .filter { it.type == TransactionType.EXPENSE }
            .groupBy { it.category }
            .mapValues { it.value.sumOf { t -> t.amount } }

        val lastExpenseByCategory = lastMonthTxns
            .filter { it.type == TransactionType.EXPENSE }
            .groupBy { it.category }
            .mapValues { it.value.sumOf { t -> t.amount } }

        val allCategories = (thisExpenseByCategory.keys + lastExpenseByCategory.keys).distinct()
        val insights = allCategories.map { cat ->
            CategoryInsight(
                category = cat,
                thisMonth = thisExpenseByCategory[cat] ?: 0.0,
                lastMonth = lastExpenseByCategory[cat] ?: 0.0
            )
        }.sortedByDescending { it.thisMonth }

        // Daily expense breakdown for chart
        val dailyCal = Calendar.getInstance()
        val dailyExpenses = thisMonthTxns
            .filter { it.type == TransactionType.EXPENSE }
            .groupBy {
                dailyCal.timeInMillis = it.date
                dailyCal.get(Calendar.DAY_OF_MONTH)
            }
            .map { (day, txns) -> day to txns.sumOf { it.amount } }
            .sortedBy { it.first }

        return AnalyticsUiState(
            thisMonthIncome = thisIncome,
            thisMonthExpense = thisExpense,
            lastMonthIncome = lastIncome,
            lastMonthExpense = lastExpense,
            savingsRate = savingsRate,
            categoryInsights = insights,
            dailyExpenses = dailyExpenses
        )
    }

    class Factory(private val repository: FinanceRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AnalyticsViewModel(repository) as T
        }
    }
}
