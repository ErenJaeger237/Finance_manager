package com.nts.financemanager.ui.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.nts.financemanager.data.model.Budget
import com.nts.financemanager.data.model.Category
import com.nts.financemanager.data.model.TransactionType
import com.nts.financemanager.data.repository.FinanceRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId

data class BudgetUiState(
    val budgets: List<Budget> = emptyList(),
    val categories: List<Category> = emptyList(),
    val categorySpending: Map<String, Double> = emptyMap(),
    val currentMonth: String = YearMonth.now().toString()
)

@OptIn(ExperimentalCoroutinesApi::class)
class BudgetViewModel(private val repository: FinanceRepository) : ViewModel() {

    private val _currentMonth = MutableStateFlow(YearMonth.now().toString())

    val uiState: StateFlow<BudgetUiState> = combine(
        _currentMonth.flatMapLatest { month -> repository.getBudgetsByMonth(month) },
        repository.getAllCategories(),
        repository.getAllTransactions()
    ) { budgets, categories, transactions ->
        val ym = YearMonth.parse(_currentMonth.value)
        val startOfMonth = ym.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endOfMonth = ym.atEndOfMonth().atStartOfDay(ZoneId.systemDefault())
            .plusDays(1).toInstant().toEpochMilli() - 1

        val spending = transactions
            .filter { it.type == TransactionType.EXPENSE && it.date in startOfMonth..endOfMonth }
            .groupBy { it.category }
            .mapValues { (_, txns) -> txns.sumOf { it.amount } }

        BudgetUiState(
            budgets = budgets,
            categories = categories,
            categorySpending = spending,
            currentMonth = _currentMonth.value
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BudgetUiState()
    )

    fun saveBudget(category: String, limitAmount: Double) {
        if (category.isBlank() || limitAmount <= 0) return
        viewModelScope.launch {
            val existing = repository.getBudgetByCategoryAndMonth(category, _currentMonth.value)
            if (existing != null) {
                repository.updateBudget(existing.copy(limitAmount = limitAmount))
            } else {
                repository.insertBudget(
                    Budget(
                        category = category,
                        limitAmount = limitAmount,
                        month = _currentMonth.value
                    )
                )
            }
        }
    }

    fun deleteBudget(budget: Budget) {
        viewModelScope.launch {
            repository.deleteBudget(budget)
        }
    }

    class Factory(private val repository: FinanceRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return BudgetViewModel(repository) as T
        }
    }
}
