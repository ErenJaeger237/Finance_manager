package com.nts.financemanager.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nts.financemanager.util.CurrencyFormatter
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.nts.financemanager.ui.theme.glassmorphic
import com.nts.financemanager.ui.theme.neomorphic
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    isDarkMode: Boolean,
    onThemeToggle: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val modelProducer = remember { CartesianChartModelProducer() }

    // Update chart data whenever expenseByCategory changes
    LaunchedEffect(uiState.expenseByCategory) {
        if (uiState.expenseByCategory.isNotEmpty()) {
            modelProducer.runTransaction {
                columnSeries {
                    series(uiState.expenseByCategory.values)
                }
            }
        }
    }

    // Pre-calculate colors for Canvas to avoid @Composable calls in DrawScope
    val primaryGlow = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
    val tertiaryGlow = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)

    // Wrap in Box to add background gradients for true Glassmorphism
    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        // Dynamic background glows for glassmorphism refraction
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize().blur(80.dp)) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            drawCircle(
                color = primaryGlow,
                radius = canvasWidth * 0.7f,
                center = androidx.compose.ui.geometry.Offset(canvasWidth, 0f)
            )
            drawCircle(
                color = tertiaryGlow,
                radius = canvasWidth * 0.6f,
                center = androidx.compose.ui.geometry.Offset(0f, canvasHeight)
            )
        }

        Scaffold(
            containerColor = Color.Transparent,
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                LargeTopAppBar(
                    title = { Text("Overview", fontWeight = FontWeight.Bold) },
                    scrollBehavior = scrollBehavior,
                    actions = {
                        IconButton(onClick = onThemeToggle) {
                            Icon(
                                imageVector = if (isDarkMode) Icons.Filled.WbSunny else Icons.Filled.NightsStay,
                                contentDescription = "Toggle Theme"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.largeTopAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.8f)
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
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                    SmallSummaryCard(
                        modifier = Modifier.weight(1f),
                        title = "Outflow",
                        amount = uiState.totalExpense,
                        icon = Icons.AutoMirrored.Filled.TrendingDown,
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.error
                    )
                }
            }

            // ── 3. Visual Graph (Digital Vault Style) ─────────────────────────
            if (uiState.expenseByCategory.isNotEmpty()) {
                item {
                    Text(
                        text = "Spending Trends",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp, start = 4.dp)
                    )
                }

                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            .glassmorphic(
                                backgroundColor = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha=0.5f),
                                cornerRadius = 24.dp
                            )
                    ) {
                        Box(modifier = Modifier.padding(16.dp)) {
                            CartesianChartHost(
                                chart = rememberCartesianChart(
                                    rememberColumnCartesianLayer(),
                                    startAxis = VerticalAxis.rememberStart(),
                                    bottomAxis = HorizontalAxis.rememberBottom()
                                ),
                                modelProducer = modelProducer,
                                modifier = Modifier.fillMaxSize()
                            )
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
    } // Close Box wrapper
}

@Composable
private fun BalanceHero(balance: Double) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .glassmorphic(
                backgroundColor = if (balance < 0) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f) 
                                 else MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                borderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                cornerRadius = 28.dp
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
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = CurrencyFormatter.format(balance),
                style = MaterialTheme.typography.displayLarge,
                color = if (balance < 0) MaterialTheme.colorScheme.error 
                        else MaterialTheme.colorScheme.primary
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
    Box(
        modifier = modifier
            .neomorphic(
                shadowColor = Color.Black.copy(alpha = 0.2f),
                highlightColor = Color.White.copy(alpha = 0.5f),
                cornerRadius = 20.dp
            )
            .background(MaterialTheme.colorScheme.background, androidx.compose.foundation.shape.RoundedCornerShape(20.dp))
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

// M3CategoryProgress and M3DetailedItem remain the same as they are M3 compliant.

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
