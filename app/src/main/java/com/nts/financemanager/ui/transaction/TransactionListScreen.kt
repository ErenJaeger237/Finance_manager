package com.nts.financemanager.ui.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nts.financemanager.data.model.Transaction
import com.nts.financemanager.data.model.TransactionType
import com.nts.financemanager.ui.theme.*
import com.nts.financemanager.util.CurrencyFormatter
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListScreen(viewModel: TransactionViewModel) {
    val uiState by viewModel.listUiState.collectAsState()
    var categoryExpanded by remember { mutableStateOf(false) }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                title = { Text("History", fontWeight = FontWeight.Bold) },
                scrollBehavior = scrollBehavior,
                actions = {
                    Box {
                        FilterChip(
                            selected = uiState.selectedCategory != null,
                            onClick = { categoryExpanded = true },
                            label = { Text(uiState.selectedCategory ?: "All Categories") },
                            trailingIcon = { Icon(Icons.Default.FilterList, contentDescription = null, modifier = Modifier.size(18.dp)) },
                            modifier = Modifier.padding(end = 16.dp)
                        )

                        DropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("All Categories") },
                                onClick = {
                                    viewModel.filterByCategory(null)
                                    categoryExpanded = false
                                }
                            )
                            uiState.categories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat.name) },
                                    onClick = {
                                        viewModel.filterByCategory(cat.name)
                                        categoryExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        if (uiState.transactions.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text("No activities found", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                val grouped = uiState.transactions.groupBy {
                    SimpleDateFormat("EEEE, dd MMMM", Locale.getDefault()).format(Date(it.date))
                }

                grouped.forEach { (date, transactions) ->
                    item {
                        Surface(
                            color = Color.Transparent,
                            modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 8.dp)
                        ) {
                            Text(
                                text = date,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }

                    items(transactions, key = { it.id }) { transaction ->
                        M3TransactionListItem(
                            transaction = transaction,
                            onDelete = { viewModel.deleteTransaction(transaction) }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun M3TransactionListItem(transaction: Transaction, onDelete: () -> Unit) {
    val isExpense = transaction.type == TransactionType.EXPENSE
    val icon = when {
        transaction.category.contains("Food", ignoreCase = true) -> Icons.Default.Restaurant
        transaction.category.contains("Transport", ignoreCase = true) -> Icons.Default.DirectionsCar
        transaction.category.contains("Business", ignoreCase = true) -> Icons.Default.BusinessCenter
        transaction.category.contains("Rent", ignoreCase = true) -> Icons.Default.Home
        transaction.category.contains("Salary", ignoreCase = true) -> Icons.Default.Payments
        else -> Icons.Default.Category
    }

    ListItem(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clip(MaterialTheme.shapes.large),
        headlineContent = { Text(transaction.title, fontWeight = FontWeight.Bold) },
        supportingContent = { Text(transaction.category) },
        trailingContent = {
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${if (isExpense) "-" else "+"}${CurrencyFormatter.format(transaction.amount)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isExpense) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.4f), modifier = Modifier.size(16.dp))
                }
            }
        },
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            }
        },
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    )
}
