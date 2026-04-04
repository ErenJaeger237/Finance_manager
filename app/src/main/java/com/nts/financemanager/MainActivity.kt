package com.nts.financemanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModelProvider
import com.nts.financemanager.ui.budget.BudgetViewModel
import com.nts.financemanager.ui.dashboard.DashboardViewModel
import com.nts.financemanager.ui.navigation.FinanceNavHost
import com.nts.financemanager.ui.theme.FinanceManagerTheme
import com.nts.financemanager.ui.transaction.TransactionViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as FinanceApplication
        val repo = app.repository

        val dashboardViewModel = ViewModelProvider(this, DashboardViewModel.Factory(repo))[DashboardViewModel::class.java]
        val transactionViewModel = ViewModelProvider(this, TransactionViewModel.Factory(repo))[TransactionViewModel::class.java]
        val budgetViewModel = ViewModelProvider(this, BudgetViewModel.Factory(repo))[BudgetViewModel::class.java]

        setContent {
            var isDarkMode by remember { mutableStateOf(false) }
            
            FinanceManagerTheme(darkTheme = isDarkMode) {
                FinanceNavHost(
                    dashboardViewModel = dashboardViewModel,
                    transactionViewModel = transactionViewModel,
                    budgetViewModel = budgetViewModel,
                    isDarkMode = isDarkMode,
                    onThemeToggle = { isDarkMode = !isDarkMode }
                )
            }
        }
    }
}
