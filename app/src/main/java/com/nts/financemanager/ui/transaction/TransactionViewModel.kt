package com.nts.financemanager.ui.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.nts.financemanager.data.model.Category
import com.nts.financemanager.data.model.Transaction
import com.nts.financemanager.data.model.TransactionType
import com.nts.financemanager.data.repository.FinanceRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class TransactionListUiState(
    val transactions: List<Transaction> = emptyList(),
    val categories: List<Category> = emptyList(),
    val selectedCategory: String? = null
)

class TransactionViewModel(private val repository: FinanceRepository) : ViewModel() {

    private val _selectedCategory = MutableStateFlow<String?>(null)

    val listUiState: StateFlow<TransactionListUiState> = combine(
        repository.getAllTransactions(),
        repository.getAllCategories(),
        _selectedCategory
    ) { transactions, categories, selectedCat ->
        val filtered = if (selectedCat != null) {
            transactions.filter { it.category == selectedCat }
        } else {
            transactions
        }
        TransactionListUiState(
            transactions = filtered,
            categories = categories,
            selectedCategory = selectedCat
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TransactionListUiState()
    )

    // ── Add Transaction Form State ────────────────────
    var formTitle = MutableStateFlow("")
    var formAmount = MutableStateFlow("")
    var formType = MutableStateFlow(TransactionType.EXPENSE)
    var formCategory = MutableStateFlow("")
    var formDate = MutableStateFlow(System.currentTimeMillis())
    var formNote = MutableStateFlow("")

    fun filterByCategory(category: String?) {
        _selectedCategory.value = category
    }

    fun saveTransaction(onSuccess: () -> Unit) {
        val amount = formAmount.value.toDoubleOrNull() ?: return
        if (formTitle.value.isBlank() || formCategory.value.isBlank()) return

        viewModelScope.launch {
            repository.insertTransaction(
                Transaction(
                    title = formTitle.value.trim(),
                    amount = amount,
                    type = formType.value,
                    category = formCategory.value,
                    date = formDate.value,
                    note = formNote.value.trim()
                )
            )
            resetForm()
            onSuccess()
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }

    fun addCustomCategory(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.insertCategory(Category(name = name.trim()))
        }
    }

    private fun resetForm() {
        formTitle.value = ""
        formAmount.value = ""
        formType.value = TransactionType.EXPENSE
        formCategory.value = ""
        formDate.value = System.currentTimeMillis()
        formNote.value = ""
    }

    class Factory(private val repository: FinanceRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return TransactionViewModel(repository) as T
        }
    }
}
