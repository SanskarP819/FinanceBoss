package com.example.financeboss.ui.transactions


import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.financeboss.data.local.entity.TransactionEntity
import com.example.financeboss.data.local.entity.TransactionType
import com.example.financeboss.ui.components.EmptyState
import com.example.financeboss.ui.components.TransactionCard
import com.example.financeboss.ui.theme.ExpenseRed
import com.example.financeboss.ui.theme.Primary
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.rememberSwipeToDismissBoxState

import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    onAddTransaction: () -> Unit,
    onEditTransaction: (Long) -> Unit,
    onBack: () -> Unit,
    viewModel: TransactionViewModel
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(state.deletedTransaction) {
        state.deletedTransaction?.let {
            val result = snackbarHostState.showSnackbar(
                message = "Transaction deleted",
                actionLabel = "Undo",
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) viewModel.undoDelete()
            else viewModel.clearDeletedTransaction()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transactions", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddTransaction, containerColor = Primary) {
                Icon(Icons.Filled.Add, "Add", tint = Color.White)
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
        ) {
            // Search Bar
            OutlinedTextField(
                value = state.filter.searchQuery,
                onValueChange = viewModel::updateSearch,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search transactions...") },
                leadingIcon = { Icon(Icons.Filled.Search, null) },
                trailingIcon = {
                    if (state.filter.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateSearch("") }) {
                            Icon(Icons.Filled.Clear, null)
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // Filter Chips
            FilterChips(
                selectedType = state.filter.type,
                onTypeSelected = viewModel::updateTypeFilter,
                onClearFilters = viewModel::clearFilters
            )

            // Transactions List
            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Primary)
                }
            } else if (state.transactions.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    EmptyState(
                        icon = Icons.Filled.SearchOff,
                        title = "No transactions found",
                        subtitle = if (state.filter.searchQuery.isNotEmpty() || state.filter.type != null)
                            "Try adjusting your filters" else "Add your first transaction using the + button"
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 80.dp, top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.transactions, key = { it.id }) { txn ->
                        SwipeToDismissTransactionCard(
                            transaction = txn,
                            onClick = { onEditTransaction(txn.id) },
                            onDelete = { viewModel.deleteTransaction(txn) },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterChips(
    selectedType: TransactionType?,
    onTypeSelected: (TransactionType?) -> Unit,
    onClearFilters: () -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        item {
            FilterChip(
                selected = selectedType == null,
                onClick = onClearFilters,
                label = { Text("All") }
            )
        }
        item {
            FilterChip(
                selected = selectedType == TransactionType.INCOME,
                onClick = { onTypeSelected(if (selectedType == TransactionType.INCOME) null else TransactionType.INCOME) },
                label = { Text("Income") },
                leadingIcon = { if (selectedType == TransactionType.INCOME) Icon(Icons.Filled.Check, null, Modifier.size(16.dp)) }
            )
        }
        item {
            FilterChip(
                selected = selectedType == TransactionType.EXPENSE,
                onClick = { onTypeSelected(if (selectedType == TransactionType.EXPENSE) null else TransactionType.EXPENSE) },
                label = { Text("Expense") },
                leadingIcon = { if (selectedType == TransactionType.EXPENSE) Icon(Icons.Filled.Check, null, Modifier.size(16.dp)) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeToDismissTransactionCard(
    transaction: TransactionEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) { onDelete(); true } else false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ExpenseRed, RoundedCornerShape(12.dp))
                    .padding(end = 16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(Icons.Filled.Delete, "Delete", tint = Color.White)
            }
        },
        content = {
            TransactionCard(transaction = transaction, onClick = onClick)
        }
    )
}