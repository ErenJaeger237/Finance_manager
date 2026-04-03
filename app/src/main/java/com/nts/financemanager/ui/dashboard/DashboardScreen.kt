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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nts.financemanager.ui.theme.ExpenseRed
import com.nts.financemanager.ui.theme.IncomeGreen
import com.nts.financemanager.ui.theme.OliveGreen
import com.nts.financemanager.util.CurrencyFormatter

@Composable
fun DashboardScreen(viewModel: DashboardViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // ── 1. Enhanced Balance Card ─────────────────────────
        item {
            BalanceCard(balance = uiState.balance)
        }

        // ── 2. Income/Expense Quick Summary ─────────────────────────
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CompactSummaryCard(
                    modifier = Modifier.weight(1f),
                    title = "Income",
                    amount = uiState.totalIncome,
                    color = IncomeGreen,
                    icon = Icons.AutoMirrored.Filled.TrendingUp
                )
                CompactSummaryCard(
                    modifier = Modifier.weight(1f),
                    title = "Expense",
                    amount = uiState.totalExpense,
                    color = ExpenseRed,
                    icon = Icons.AutoMirrored.Filled.TrendingDown
                )
            }
        }

        // ── 3. Category Breakdown (Clean Option B) ─────────────────────────
        if (uiState.expenseByCategory.isNotEmpty()) {
            item {
                SectionHeader("Expense Breakdown")
                Spacer(modifier = Modifier.height(12.dp))
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        val maxExpense = uiState.expenseByCategory.values.maxOrNull() ?: 1.0
                        uiState.expenseByCategory.forEach { (category, amount) ->
                            CategoryProgressRow(category, amount, maxExpense)
                        }
                    }
                }
            }
        }

        // ── 4. Details Section ─────────────────────────
        if (uiState.expenseByCategory.isNotEmpty()) {
            item {
                SectionHeader("Recent Details")
                Spacer(modifier = Modifier.height(12.dp))
            }

            items(uiState.expenseByCategory.toList()) { (category, amount) ->
                DetailedCategoryCard(category = category, amount = amount)
            }
        }
        
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface,
        letterSpacing = 0.5.sp
    )
}

@Composable
private fun BalanceCard(balance: Double) {
    val isNegative = balance < 0
    val displayColor = if (isNegative) ExpenseRed else IncomeGreen

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Total Balance",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = CurrencyFormatter.format(balance),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = displayColor,
                fontSize = 32.sp
            )
        }
    }
}

@Composable
private fun CompactSummaryCard(
    modifier: Modifier,
    title: String,
    amount: Double,
    color: Color,
    icon: ImageVector
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            }
            Column {
                Text(title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    CurrencyFormatter.format(amount),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
        }
    }
}

@Composable
private fun CategoryProgressRow(category: String, amount: Double, maxAmount: Double) {
    val progress = (amount / maxAmount).toFloat()
    
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(category, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Text(CurrencyFormatter.format(amount), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
        }
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = if (category.contains("Business", ignoreCase = true)) Color(0xFF5C6BC0) else OliveGreen,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
private fun DetailedCategoryCard(category: String, amount: Double) {
    val icon = when {
        category.contains("Food", ignoreCase = true) -> Icons.Default.Restaurant
        category.contains("Transport", ignoreCase = true) -> Icons.Default.DirectionsCar
        category.contains("Business", ignoreCase = true) -> Icons.Default.BusinessCenter
        category.contains("Rent", ignoreCase = true) -> Icons.Default.Home
        category.contains("Salary", ignoreCase = true) -> Icons.Default.Payments
        else -> Icons.Default.Category
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                }
                Text(category, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            }
            Text(
                CurrencyFormatter.format(amount),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = ExpenseRed
            )
        }
    }
}
