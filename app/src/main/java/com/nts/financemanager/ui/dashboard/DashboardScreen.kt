package com.nts.financemanager.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nts.financemanager.ui.theme.ExpenseRed
import com.nts.financemanager.ui.theme.IncomeGreen
import com.nts.financemanager.util.CurrencyFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: DashboardViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text("Overview", fontWeight = FontWeight.Bold) },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── 1. M3 Elevated Balance Card ─────────────────────────
            item {
                BalanceHero(balance = uiState.balance)
            }

            // ── 2. Quick Action Chips ─────────────────────────
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SmallSummaryCard(
                        modifier = Modifier.weight(1f),
                        title = "Inflow",
                        amount = uiState.totalIncome,
                        icon = Icons.AutoMirrored.Filled.TrendingUp,
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    SmallSummaryCard(
                        modifier = Modifier.weight(1f),
                        title = "Outflow",
                        amount = uiState.totalExpense,
                        icon = Icons.AutoMirrored.Filled.TrendingDown,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            // ── 3. Expense Breakdown (M3 Style) ─────────────────────────
            if (uiState.expenseByCategory.isNotEmpty()) {
                item {
                    Text(
                        text = "Spending Breakdown",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp, start = 4.dp)
                    )
                }
                
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.extraLarge,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            val maxExpense = uiState.expenseByCategory.values.maxOrNull() ?: 1.0
                            uiState.expenseByCategory.forEach { (category, amount) ->
                                M3CategoryProgress(category, amount, maxExpense)
                            }
                        }
                    }
                }
            }

            // ── 4. Detailed History ─────────────────────────
            if (uiState.expenseByCategory.isNotEmpty()) {
                item {
                    Text(
                        text = "Top Categories",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp, start = 4.dp)
                    )
                }

                items(uiState.expenseByCategory.toList()) { (category, amount) ->
                    M3DetailedItem(category, amount)
                }
            }
            
            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }
}

@Composable
private fun BalanceHero(balance: Double) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (balance < 0) MaterialTheme.colorScheme.errorContainer 
                             else MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Available Balance",
                style = MaterialTheme.typography.labelLarge,
                color = if (balance < 0) MaterialTheme.colorScheme.onErrorContainer 
                        else MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = CurrencyFormatter.format(balance),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 36.sp,
                color = if (balance < 0) MaterialTheme.colorScheme.onErrorContainer 
                        else MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun SmallSummaryCard(
    modifier: Modifier,
    title: String,
    amount: Double,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = containerColor
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(20.dp))
            Column {
                Text(title, style = MaterialTheme.typography.labelSmall, color = contentColor.copy(alpha = 0.7f))
                Text(
                    CurrencyFormatter.format(amount),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
            }
        }
    }
}

@Composable
private fun M3CategoryProgress(category: String, amount: Double, maxAmount: Double) {
    val progress = (amount / maxAmount).toFloat()
    
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(category, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            Text(CurrencyFormatter.format(amount), style = MaterialTheme.typography.bodySmall)
        }
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(CircleShape),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
        )
    }
}

@Composable
private fun M3DetailedItem(category: String, amount: Double) {
    val icon = when {
        category.contains("Food", ignoreCase = true) -> Icons.Default.Restaurant
        category.contains("Transport", ignoreCase = true) -> Icons.Default.DirectionsCar
        category.contains("Business", ignoreCase = true) -> Icons.Default.BusinessCenter
        category.contains("Rent", ignoreCase = true) -> Icons.Default.Home
        category.contains("Salary", ignoreCase = true) -> Icons.Default.Payments
        else -> Icons.Default.Category
    }

    ListItem(
        modifier = Modifier.clip(MaterialTheme.shapes.large),
        headlineContent = { Text(category, fontWeight = FontWeight.SemiBold) },
        supportingContent = { Text("Total Monthly Spend") },
        trailingContent = {
            Text(
                CurrencyFormatter.format(amount),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.error
            )
        },
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}
