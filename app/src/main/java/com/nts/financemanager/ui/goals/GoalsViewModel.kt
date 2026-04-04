package com.nts.financemanager.ui.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.nts.financemanager.data.model.FinancialGoal
import com.nts.financemanager.data.repository.FinanceRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class GoalsUiState(
    val goals: List<FinancialGoal> = emptyList()
)

class GoalsViewModel(private val repository: FinanceRepository) : ViewModel() {

    val uiState: StateFlow<GoalsUiState> = repository.getAllGoals()
        .map { GoalsUiState(goals = it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), GoalsUiState())

    fun addGoal(name: String, targetAmount: Double, deadline: Long) {
        viewModelScope.launch {
            repository.insertGoal(
                FinancialGoal(
                    name = name,
                    targetAmount = targetAmount,
                    deadline = deadline
                )
            )
        }
    }

    fun addFunds(goal: FinancialGoal, amount: Double) {
        viewModelScope.launch {
            repository.updateGoal(goal.copy(savedAmount = goal.savedAmount + amount))
        }
    }

    fun deleteGoal(goal: FinancialGoal) {
        viewModelScope.launch {
            repository.deleteGoal(goal)
        }
    }

    class Factory(private val repository: FinanceRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return GoalsViewModel(repository) as T
        }
    }
}
