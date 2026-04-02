package com.example.financeboss.ui.insights

import com.example.financeboss.data.local.entity.TransactionCategory
import com.example.financeboss.ui.components.EmptyState
import com.example.financeboss.ui.theme.CategoryColors
import com.example.financeboss.ui.theme.ExpenseRed
import com.example.financeboss.ui.theme.IncomeGreen
import com.example.financeboss.ui.theme.Primary




import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.core.entry.entryModelOf

import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsScreen(viewModel: InsightsViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Insights", fontWeight = FontWeight.Bold) })
        }
    ) { padding ->

        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary)
            }
            return@Scaffold
        }

        if (state.totalExpenseThisMonth == 0.0 && state.totalIncomeThisMonth == 0.0) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                EmptyState(
                    icon = Icons.Filled.BarChart,
                    title = "No data yet",
                    subtitle = "Add some transactions to see your spending insights"
                )
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { MonthComparisonCard(state) }
            item { WeekComparisonCard(state) }
            item { MonthlyTrendChart(state.monthlyTrend) }

            state.topCategory?.let { top ->
                item {
                    TopCategoryCard(
                        category = top,
                        amount = state.categoryBreakdown.firstOrNull()?.amount ?: 0.0
                    )
                }
            }

            if (state.categoryBreakdown.isNotEmpty()) {
                item {
                    Text(
                        "Spending by Category",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                items(state.categoryBreakdown) { item ->
                    CategoryBreakdownRow(item)
                }
            }
        }
    }
}

@Composable
private fun MonthComparisonCard(state: InsightsUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "This Month Overview",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MonthStatItem("Income", state.totalIncomeThisMonth, IncomeGreen)
                MonthStatItem("Expenses", state.totalExpenseThisMonth, ExpenseRed)
                MonthStatItem(
                    "Saved",
                    state.totalIncomeThisMonth - state.totalExpenseThisMonth,
                    Primary
                )
            }
            if (state.totalExpenseLastMonth > 0) {
                Spacer(Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(Modifier.height(12.dp))
                val change = state.expenseChangePercent
                val isIncrease = change > 0
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (isIncrease) Icons.Filled.TrendingUp else Icons.Filled.TrendingDown,
                        null,
                        tint = if (isIncrease) ExpenseRed else IncomeGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "${if (isIncrease) "+" else ""}${"%.1f".format(change)}% vs last month",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isIncrease) ExpenseRed else IncomeGreen
                    )
                }
            }
        }
    }
}

@Composable
private fun MonthStatItem(label: String, amount: Double, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "₹${"%,.0f".format(amount)}",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = color
        )
    }
}

@Composable
private fun WeekComparisonCard(state: InsightsUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Week vs Last Week",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                WeekItem("This Week", state.thisWeekSpending, Primary)
                Icon(
                    Icons.Filled.ArrowForward, null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                WeekItem("Last Week", state.lastWeekSpending, MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (state.lastWeekSpending > 0) {
                Spacer(Modifier.height(8.dp))
                val change = state.weekChangePercent
                val isIncrease = change > 0
                Text(
                    "${if (isIncrease) "Spent " else "Saved "}${"%.1f".format(abs(change))}% ${if (isIncrease) "more" else "less"} than last week",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isIncrease) ExpenseRed else IncomeGreen
                )
            }
        }
    }
}

@Composable
private fun WeekItem(label: String, amount: Double, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "₹${"%,.0f".format(amount)}",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = color
        )
    }
}

@Composable
private fun MonthlyTrendChart(monthlyTrend: List<Pair<String, Double>>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "6-Month Spending Trend",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(Modifier.height(16.dp))
            if (monthlyTrend.all { it.second == 0.0 }) {
                Box(
                    Modifier.fillMaxWidth().height(140.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No expense data available",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            } else {
                val entries = monthlyTrend.mapIndexed { i, (_, v) ->
                    com.patrykandpatrick.vico.core.entry.FloatEntry(i.toFloat(), v.toFloat())
                }
                Chart(
                    chart = columnChart(),
                    model = entryModelOf(entries),
                    startAxis = rememberStartAxis(),
                    bottomAxis = rememberBottomAxis(
                        valueFormatter = { value, _ ->
                            monthlyTrend.getOrNull(value.toInt())?.first ?: ""
                        }
                    ),
                    modifier = Modifier.fillMaxWidth().height(160.dp)
                )
            }
        }
    }
}

@Composable
private fun TopCategoryCard(category: TransactionCategory, amount: Double) {
    val color = CategoryColors[category.name] ?: Primary
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(category.emoji, fontSize = 22.sp)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Top Spending Category",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    category.displayName,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = color
                )
            }
            Text(
                "₹${"%,.0f".format(amount)}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = color
            )
        }
    }
}

@Composable
private fun CategoryBreakdownRow(item: CategorySpending) {
    val color = CategoryColors[item.category.name] ?: Primary
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(item.category.emoji, fontSize = 16.sp)
                Spacer(Modifier.width(8.dp))
                Text(item.category.displayName, style = MaterialTheme.typography.bodyMedium)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "${"%.1f".format(item.percentage)}%",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "₹${"%,.0f".format(item.amount)}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = item.percentage / 100f,
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.15f)
        )
        Spacer(Modifier.height(10.dp))
    }
}