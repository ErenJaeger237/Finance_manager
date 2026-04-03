package com.nts.financemanager.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.nts.financemanager.data.model.Transaction
import com.nts.financemanager.data.model.TransactionType
import com.nts.financemanager.data.repository.FinanceRepository
import kotlinx.coroutines.flow.*
import java.util.*

data class DashboardUiState(
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val balance: Double = 0.0,
    val recentTransactions: List<Transaction> = emptyList(),
    val expenseByCategory: Map<String, Double> = emptyMap()
)

class DashboardViewModel(private val repository: FinanceRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        // Combine income, expense, and transactions into a single UI state
        combine(
            repository.getTotalIncome(),
            repository.getTotalExpense(),
            repository.getAllTransactions()
        ) { income, expense, transactions ->
            // Calculate expense by category from current transactions
            val categoryExpenses = transactions
                .filter { it.type == TransactionType.EXPENSE }
                .groupBy { it.category }
                .mapValues { (_, txns) -> txns.sumOf { it.amount } }

            DashboardUiState(
                totalIncome = income,
                totalExpense = expense,
                balance = income - expense,
                recentTransactions = transactions.take(5),
                expenseByCategory = categoryExpenses
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DashboardUiState()
        ).also { flow ->
            flow.onEach { _uiState.value = it }
                .launchIn(viewModelScope)
        }
    }

    class Factory(private val repository: FinanceRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(repository) as T
        }
    }
}
