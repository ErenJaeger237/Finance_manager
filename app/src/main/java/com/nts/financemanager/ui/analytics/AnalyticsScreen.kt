package com.nts.financemanager.ui.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nts.financemanager.ui.theme.glassmorphic
import com.nts.financemanager.util.CurrencyFormatter
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(viewModel: AnalyticsViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(uiState.dailyExpenses) {
        if (uiState.dailyExpenses.isNotEmpty()) {
            modelProducer.runTransaction {
                lineSeries {
                    series(uiState.dailyExpenses.map { it.second })
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                title = { Text("Financial Analytics", fontWeight = FontWeight.Bold) },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── 1. Month Comparison ─────────────────────────
            item {
                ComparisonCard(uiState)
            }

            // ── 2. Spending Trends (Line Chart) ─────────────────────────
            item {
                Text(
                    text = "Daily Spending Trend",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .glassmorphic(
                            backgroundColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                            cornerRadius = 24.dp
                        )
                ) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        CartesianChartHost(
                            chart = rememberCartesianChart(
                                rememberLineCartesianLayer(),
                                startAxis = VerticalAxis.rememberStart(
                                    valueFormatter = { _, value, _ -> CurrencyFormatter.formatCompact(value) }
                                ),
                                bottomAxis = HorizontalAxis.rememberBottom(
                                    valueFormatter = { _, value, _ -> "Day ${value.toInt() + 1}" }
                                )
                            ),
                            modelProducer = modelProducer,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            // ── 3. Category Insights ─────────────────────────
            item {
                Text(
                    text = "Category Comparison",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp, top = 8.dp)
                )
            }

            items(uiState.categoryInsights) { insight ->
                InsightItem(insight)
            }
            
            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }
}

@Composable
fun ComparisonCard(uiState: AnalyticsUiState) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .glassmorphic(
                backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                cornerRadius = 28.dp
            )
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ComparisonColumn("This Month", uiState.thisMonthExpense, MaterialTheme.colorScheme.primary)
                ComparisonColumn("Last Month", uiState.lastMonthExpense, MaterialTheme.colorScheme.onSurfaceVariant)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            val savingsRate = uiState.savingsRate.toInt()
            LinearProgressIndicator(
                progress = { (savingsRate.coerceIn(0, 100) / 100f) },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Savings Rate: $savingsRate%",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ComparisonColumn(label: String, amount: Double, color: Color) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            text = CurrencyFormatter.format(amount),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = color
        )
    }
}

@Composable
fun InsightItem(insight: CategoryInsight) {
    val isIncrease = insight.thisMonth > insight.lastMonth
    
    ListItem(
        modifier = Modifier.clip(MaterialTheme.shapes.large),
        headlineContent = { Text(insight.category, fontWeight = FontWeight.Bold) },
        supportingContent = { 
            Text(
                text = if (isIncrease) "+${insight.changePercent.toInt()}% from last month" 
                       else "${insight.changePercent.toInt()}% from last month",
                color = if (isIncrease) MaterialTheme.colorScheme.error else Color(0xFF4CAF50)
            )
        },
        trailingContent = {
            Text(
                CurrencyFormatter.format(insight.thisMonth),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        },
        leadingContent = {
            Icon(
                imageVector = if (isIncrease) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                contentDescription = null,
                tint = if (isIncrease) MaterialTheme.colorScheme.error else Color(0xFF4CAF50),
                modifier = Modifier.size(24.dp)
            )
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}
