package com.example.financeboss.ui.home

import android.app.Application
import com.example.financeboss.ui.components.EmptyState
import com.example.financeboss.ui.components.SummaryCard
import com.example.financeboss.ui.components.TransactionCard
import com.example.financeboss.ui.theme.ExpenseRed
import com.example.financeboss.ui.theme.IncomeGreen
import com.example.financeboss.ui.theme.Primary



import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.financeboss.ui.components.ShimmerHomeScreen
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.core.entry.entryModelOf

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAddTransaction: () -> Unit,
    onSeeAllTransactions: () -> Unit,
    viewModel: HomeViewModel,
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Finance", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onToggleDarkMode) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Filled.LightMode
                            else Icons.Filled.DarkMode,
                            contentDescription = "Toggle dark mode"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddTransaction, containerColor = Primary) {
                Icon(Icons.Filled.Add, "Add Transaction", tint = Color.White)
            }
        }
    ) { padding ->

        if (state.isLoading) {
            Box(Modifier.padding(padding)) { ShimmerHomeScreen() }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(
                top = padding.calculateTopPadding(),
                bottom = padding.calculateBottomPadding() + 80.dp
            )
        ) {
            item {
                BalanceHeader(
                    balance = state.balance,
                    month = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date())
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SummaryCard(
                        title = "Income",
                        amount = state.totalIncome,
                        icon = Icons.Filled.TrendingUp,
                        iconTint = IncomeGreen,
                        modifier = Modifier.weight(1f)
                    )
                    SummaryCard(
                        title = "Expenses",
                        amount = state.totalExpense,
                        icon = Icons.Filled.TrendingDown,
                        iconTint = ExpenseRed,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                WeeklySpendingChart(
                    weeklyData = state.weeklySpending,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Recent Transactions",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    TextButton(onClick = onSeeAllTransactions) {
                        Text("See All", color = Primary)
                    }
                }
            }

            if (state.recentTransactions.isEmpty()) {
                item {
                    EmptyState(
                        icon = Icons.Filled.Receipt,
                        title = "No transactions yet",
                        subtitle = "Tap + to add your first transaction",
                        modifier = Modifier.padding(32.dp)
                    )
                }
            } else {
                items(state.recentTransactions) { txn ->
                    TransactionCard(
                        transaction = txn,
                        onClick = {},
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun BalanceHeader(balance: Double, month: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(listOf(Primary, Primary.copy(alpha = 0.85f)))
            )
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        Column {
            Text(
                month,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Total Balance",
                style = MaterialTheme.typography.labelLarge,
                color = Color.White.copy(alpha = 0.8f)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "₹${"%,.0f".format(balance)}",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }
    }
}

@Composable
private fun WeeklySpendingChart(
    weeklyData: List<Pair<String, Double>>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Weekly Spending",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(Modifier.height(16.dp))
            if (weeklyData.all { it.second == 0.0 }) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No spending data this week",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            } else {
                val entries = weeklyData.mapIndexed { i, (_, v) ->
                    com.patrykandpatrick.vico.core.entry.FloatEntry(i.toFloat(), v.toFloat())
                }
                Chart(
                    chart = columnChart(),
                    model = entryModelOf(entries),
                    startAxis = rememberStartAxis(),
                    bottomAxis = rememberBottomAxis(
                        valueFormatter = { value, _ ->
                            weeklyData.getOrNull(value.toInt())?.first ?: ""
                        }
                    ),
                    modifier = Modifier.fillMaxWidth().height(160.dp)
                )
            }
        }
    }
}