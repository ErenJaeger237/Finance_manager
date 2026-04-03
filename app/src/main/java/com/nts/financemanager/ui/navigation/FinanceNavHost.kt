package com.nts.financemanager.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nts.financemanager.ui.budget.BudgetScreen
import com.nts.financemanager.ui.budget.BudgetViewModel
import com.nts.financemanager.ui.dashboard.DashboardScreen
import com.nts.financemanager.ui.dashboard.DashboardViewModel
import com.nts.financemanager.ui.transaction.AddTransactionScreen
import com.nts.financemanager.ui.transaction.TransactionListScreen
import com.nts.financemanager.ui.transaction.TransactionViewModel

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    data object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Home)
    data object AddTransaction : Screen("add_transaction", "Add", Icons.Default.Add)
    data object Transactions : Screen("transactions", "Transactions", Icons.AutoMirrored.Filled.List)
    data object Budget : Screen("budget", "Budget", Icons.Default.AccountBalanceWallet)
}

private val bottomNavItems = listOf(
    Screen.Dashboard,
    Screen.AddTransaction,
    Screen.Transactions,
    Screen.Budget
)

@Composable
fun FinanceNavHost(
    dashboardViewModel: DashboardViewModel,
    transactionViewModel: TransactionViewModel,
    budgetViewModel: BudgetViewModel
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                bottomNavItems.forEach { screen ->
                    val selected = currentRoute == screen.route
                    NavigationBarItem(
                        icon = { 
                            Icon(
                                imageVector = screen.icon, 
                                contentDescription = screen.label,
                                modifier = Modifier.size(26.dp)
                            ) 
                        },
                        label = { 
                            Text(
                                screen.label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                            ) 
                        },
                        selected = selected,
                        alwaysShowLabel = true,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        ),
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
                DashboardScreen(viewModel = dashboardViewModel)
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
        }
    }
}
