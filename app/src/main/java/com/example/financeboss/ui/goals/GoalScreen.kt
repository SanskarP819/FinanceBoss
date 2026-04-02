package com.example.financeboss.ui.goals



import androidx.compose.foundation.layout.*

import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

import com.example.financeboss.ui.components.EmptyState



import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.financeboss.data.local.entity.TransactionCategory
import com.example.financeboss.ui.theme.CategoryColors
import com.example.financeboss.ui.theme.ExpenseRed
import com.example.financeboss.ui.theme.IncomeGreen
import com.example.financeboss.ui.theme.Primary
import com.example.financeboss.ui.theme.WarningAmber

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalScreen(viewModel: GoalViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val currentMonth = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date())

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Budget Goals", fontWeight = FontWeight.Bold) })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = viewModel::showAddDialog, containerColor = Primary) {
                Icon(Icons.Filled.Add, "Add Budget", tint = Color.White)
            }
        }
    ) { padding ->

        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
        ) {
            Text(
                currentMonth,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            if (state.budgetProgressList.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    EmptyState(
                        icon = Icons.Filled.Flag,
                        title = "No budgets set",
                        subtitle = "Tap + to set a monthly budget limit for any spending category",
                        action = {
                            Button(
                                onClick = viewModel::showAddDialog,
                                colors = ButtonDefaults.buttonColors(containerColor = Primary)
                            ) {
                                Icon(Icons.Filled.Add, null)
                                Spacer(Modifier.width(4.dp))
                                Text("Set First Budget")
                            }
                        }
                    )
                }
            } else {
                val overBudgetCount = state.budgetProgressList.count { it.isOverBudget }
                if (overBudgetCount > 0) {
                    OverBudgetBanner(overBudgetCount)
                }

                LazyColumn(
                    contentPadding = PaddingValues(
                        start = 16.dp, end = 16.dp, top = 8.dp, bottom = 100.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.budgetProgressList, key = { it.goal.id }) { progress ->
                        BudgetCard(
                            progress = progress,
                            onDelete = { viewModel.deleteGoal(progress.goal) }
                        )
                    }
                }
            }
        }

        if (state.showAddDialog) {
            AddBudgetDialog(
                selectedCategory = state.selectedCategory,
                budgetAmount = state.budgetAmount,
                budgetAmountError = state.budgetAmountError,
                existingCategories = state.budgetProgressList.map { it.goal.category },
                onCategoryChange = viewModel::onCategoryChange,
                onAmountChange = viewModel::onBudgetAmountChange,
                onConfirm = viewModel::saveGoal,
                onDismiss = viewModel::hideAddDialog
            )
        }
    }
}

@Composable
private fun OverBudgetBanner(count: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = ExpenseRed.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Warning, null, tint = ExpenseRed, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text(
                "$count ${if (count == 1) "category has" else "categories have"} exceeded the budget limit!",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                color = ExpenseRed
            )
        }
    }
}

@Composable
private fun BudgetCard(progress: BudgetProgress, onDelete: () -> Unit) {
    val categoryColor = CategoryColors[progress.goal.category.name] ?: Primary
    val progressColor = when {
        progress.isOverBudget -> ExpenseRed
        progress.percentage >= 80f -> WarningAmber
        else -> IncomeGreen
    }
    val statusLabel = when {
        progress.isOverBudget ->
            "Over budget by ₹${"%,.0f".format(-progress.remaining)}"
        progress.percentage >= 80f ->
            "₹${"%,.0f".format(progress.remaining)} remaining — running low!"
        else ->
            "₹${"%,.0f".format(progress.remaining)} remaining"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(categoryColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(progress.goal.category.emoji, fontSize = 18.sp)
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            progress.goal.category.displayName,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            "Budget: ₹${"%,.0f".format(progress.goal.budgetLimit)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (progress.isOverBudget) {
                        Icon(
                            Icons.Filled.Warning, null,
                            tint = ExpenseRed,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(
                            Icons.Filled.Delete, "Delete",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = progress.percentage / 100f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = progressColor,
                trackColor = progressColor.copy(alpha = 0.15f)
            )

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Spent: ₹${"%,.0f".format(progress.spent)}",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    color = progressColor
                )
                Text(
                    "${"%.0f".format(progress.percentage)}%",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = progressColor
                )
            }

            Spacer(Modifier.height(4.dp))
            Text(
                statusLabel,
                style = MaterialTheme.typography.labelSmall,
                color = progressColor.copy(alpha = 0.8f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddBudgetDialog(
    selectedCategory: TransactionCategory,
    budgetAmount: String,
    budgetAmountError: String?,
    existingCategories: List<TransactionCategory>,
    onCategoryChange: (TransactionCategory) -> Unit,
    onAmountChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val expenseCategories = listOf(
        TransactionCategory.FOOD, TransactionCategory.TRANSPORT,
        TransactionCategory.SHOPPING, TransactionCategory.ENTERTAINMENT,
        TransactionCategory.HEALTH, TransactionCategory.BILLS,
        TransactionCategory.EDUCATION, TransactionCategory.OTHER
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set Budget Limit", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Choose a category and set your monthly spending limit",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text("Category", style = MaterialTheme.typography.labelMedium)

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    expenseCategories.chunked(2).forEach { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            row.forEach { cat ->
                                val alreadySet = existingCategories.contains(cat)
                                FilterChip(
                                    selected = selectedCategory == cat,
                                    onClick = { if (!alreadySet) onCategoryChange(cat) },
                                    label = {
                                        Text(
                                            "${cat.emoji} ${cat.displayName}",
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    },
                                    enabled = !alreadySet,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (row.size == 1) Spacer(Modifier.weight(1f))
                        }
                    }
                }

                OutlinedTextField(
                    value = budgetAmount,
                    onValueChange = onAmountChange,
                    label = { Text("Monthly Budget (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = budgetAmountError != null,
                    supportingText = budgetAmountError?.let {
                        { Text(it, color = MaterialTheme.colorScheme.error) }
                    },
                    leadingIcon = {
                        Text("₹", modifier = Modifier.padding(start = 4.dp))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) { Text("Set Budget") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        shape = RoundedCornerShape(16.dp)
    )
}