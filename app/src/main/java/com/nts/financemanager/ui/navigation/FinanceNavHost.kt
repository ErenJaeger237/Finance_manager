package com.nts.financemanager.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nts.financemanager.ui.analytics.AnalyticsScreen
import com.nts.financemanager.ui.analytics.AnalyticsViewModel
import com.nts.financemanager.ui.budget.BudgetScreen
import com.nts.financemanager.ui.budget.BudgetViewModel
import com.nts.financemanager.ui.dashboard.DashboardScreen
import com.nts.financemanager.ui.dashboard.DashboardViewModel
import com.nts.financemanager.ui.goals.GoalsScreen
import com.nts.financemanager.ui.goals.GoalsViewModel
import com.nts.financemanager.ui.transaction.AddTransactionScreen
import com.nts.financemanager.ui.transaction.TransactionListScreen
import com.nts.financemanager.ui.transaction.TransactionViewModel

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    data object Dashboard : Screen("dashboard", "Home", Icons.Default.Home)
    data object Analytics : Screen("analytics", "Insights", Icons.Default.Insights)
    data object Transactions : Screen("transactions", "History", Icons.AutoMirrored.Filled.List)
    data object Budget : Screen("budget", "Budget", Icons.Default.AccountBalanceWallet)
    data object Goals : Screen("goals", "Goals", Icons.Default.Flag)
    data object AddTransaction : Screen("add_transaction", "Add", Icons.Default.Add)
}

private val bottomNavItems = listOf(
    Screen.Dashboard,
    Screen.Analytics,
    Screen.Transactions,
    Screen.Budget,
    Screen.Goals
)

@Composable
fun FinanceNavHost(
    dashboardViewModel: DashboardViewModel,
    analyticsViewModel: AnalyticsViewModel,
    transactionViewModel: TransactionViewModel,
    budgetViewModel: BudgetViewModel,
    goalsViewModel: GoalsViewModel,
    isDarkMode: Boolean,
    onThemeToggle: () -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar(tonalElevation = 8.dp) {
                bottomNavItems.forEach { screen ->
                    val selected = currentRoute == screen.route
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label, modifier = Modifier.size(24.dp)) },
                        label = { Text(screen.label, style = MaterialTheme.typography.labelSmall, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium) },
                        selected = selected,
                        alwaysShowLabel = true,
                        onClick = {
                            if (currentRoute != screen.route) {
                                navController.navigate(screen.route) {
                                    popUpTo(Screen.Dashboard.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    viewModel = dashboardViewModel,
                    isDarkMode = isDarkMode,
                    onThemeToggle = onThemeToggle,
                    onAddTransaction = { navController.navigate(Screen.AddTransaction.route) }
                )
            }
            composable(Screen.Analytics.route) {
                AnalyticsScreen(viewModel = analyticsViewModel)
            }
            composable(Screen.AddTransaction.route) {
                AddTransactionScreen(
                    viewModel = transactionViewModel,
                    onTransactionSaved = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Dashboard.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Transactions.route) {
                TransactionListScreen(viewModel = transactionViewModel)
            }
            composable(Screen.Budget.route) {
                BudgetScreen(viewModel = budgetViewModel)
            }
            composable(Screen.Goals.route) {
                GoalsScreen(viewModel = goalsViewModel)
            }
        }
    }
}
