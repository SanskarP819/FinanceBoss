package com.example.financeboss.ui.transactions



import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.financeboss.data.local.entity.TransactionCategory
import com.example.financeboss.data.local.entity.TransactionType
import com.example.financeboss.ui.theme.Primary

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTransactionScreen(
    transactionId: Long?,
    onBack: () -> Unit,
    viewModel: AddEditTransactionViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(transactionId) {
        transactionId?.let { viewModel.loadTransaction(it) }
    }

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) onBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (state.isEditing) "Edit Transaction" else "Add Transaction",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Transaction Type Selector
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
                    TransactionType.values().forEach { type ->
                        val selected = state.type == type
                        Button(
                            onClick = { viewModel.onTypeChange(type) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selected) Primary else Color.Transparent,
                                contentColor = if (selected) Color.White else MaterialTheme.colorScheme.onSurface
                            ),
                            elevation = ButtonDefaults.buttonElevation(if (selected) 2.dp else 0.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(type.name.lowercase().replaceFirstChar { it.uppercase() })
                        }
                    }
                }
            }

            // Title
            OutlinedTextField(
                value = state.title,
                onValueChange = viewModel::onTitleChange,
                label = { Text("Title") },
                placeholder = { Text("e.g. Coffee, Salary...") },
                isError = state.titleError != null,
                supportingText = state.titleError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Amount
            OutlinedTextField(
                value = state.amount,
                onValueChange = viewModel::onAmountChange,
                label = { Text("Amount (₹)") },
                placeholder = { Text("0.00") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = state.amountError != null,
                supportingText = state.amountError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                leadingIcon = { Text("₹", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(start = 4.dp)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Category
            Text("Category", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)

            val categories = if (state.type == TransactionType.EXPENSE) {
                listOf(TransactionCategory.FOOD, TransactionCategory.TRANSPORT,
                    TransactionCategory.SHOPPING, TransactionCategory.ENTERTAINMENT,
                    TransactionCategory.HEALTH, TransactionCategory.BILLS,
                    TransactionCategory.EDUCATION, TransactionCategory.OTHER)
            } else {
                listOf(TransactionCategory.SALARY, TransactionCategory.FREELANCE,
                    TransactionCategory.INVESTMENT, TransactionCategory.OTHER)
            }

            CategoryGrid(
                categories = categories,
                selectedCategory = state.category,
                onCategorySelected = viewModel::onCategoryChange
            )

            // Date (display only — tap to show date picker)
            var showDatePicker by remember { mutableStateOf(false) }
            OutlinedTextField(
                value = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(state.date)),
                onValueChange = {},
                label = { Text("Date") },
                trailingIcon = { Icon(Icons.Filled.CalendarToday, null) },
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            if (showDatePicker) {
                val datePickerState = rememberDatePickerState(initialSelectedDateMillis = state.date)
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            datePickerState.selectedDateMillis?.let { viewModel.onDateChange(it) }
                            showDatePicker = false
                        }) { Text("OK") }
                    }
                ) { DatePicker(state = datePickerState) }
            }

            // Notes
            OutlinedTextField(
                value = state.notes,
                onValueChange = viewModel::onNotesChange,
                label = { Text("Notes (optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4,
                shape = RoundedCornerShape(12.dp)
            )

            // Save Button
            Button(
                onClick = viewModel::save,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Icon(if (state.isEditing) Icons.Filled.Check else Icons.Filled.Add, null)
                Spacer(Modifier.width(8.dp))
                Text(
                    if (state.isEditing) "Update Transaction" else "Save Transaction",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                )
            }
        }
    }
}

@Composable
private fun CategoryGrid(
    categories: List<TransactionCategory>,
    selectedCategory: TransactionCategory,
    onCategorySelected: (TransactionCategory) -> Unit
) {
    val rows = categories.chunked(4)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { cat ->
                    val selected = cat == selectedCategory
                    FilterChip(
                        selected = selected,
                        onClick = { onCategorySelected(cat) },
                        label = { Text("${cat.emoji} ${cat.displayName}", style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.weight(1f)
                    )
                }
                // Fill empty spots in last row
                repeat(4 - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}