package com.example.financeboss.ui.transactions



import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.financeboss.data.local.AppDatabase
import com.example.financeboss.data.local.entity.TransactionCategory
import com.example.financeboss.data.local.entity.TransactionEntity
import com.example.financeboss.data.local.entity.TransactionType
import com.example.financeboss.data.preferences.UserPreferences
import com.example.financeboss.data.repository.TransactionRepository


import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar


data class AddEditUiState(
    val title: String = "",
    val amount: String = "",
    val type: TransactionType = TransactionType.EXPENSE,
    val category: TransactionCategory = TransactionCategory.FOOD,
    val notes: String = "",
    val date: Long = System.currentTimeMillis(),
    val isEditing: Boolean = false,
    val isSaved: Boolean = false,
    val titleError: String? = null,
    val amountError: String? = null
)


class AddEditTransactionViewModel(
    private val repository: TransactionRepository
) : ViewModel() {



    private val _state = MutableStateFlow(AddEditUiState())
    val state: StateFlow<AddEditUiState> = _state.asStateFlow()

    private var editingId: Long? = null

    fun loadTransaction(id: Long) {
        viewModelScope.launch {
            repository.getTransactionById(id)?.let { txn ->
                editingId = txn.id
                _state.value = AddEditUiState(
                    title = txn.title,
                    amount = txn.amount.toString(),
                    type = txn.type,
                    category = txn.category,
                    notes = txn.notes,
                    date = txn.date,
                    isEditing = true
                )
            }
        }
    }

    fun onTitleChange(v: String) { _state.value = _state.value.copy(title = v, titleError = null) }
    fun onAmountChange(v: String) { _state.value = _state.value.copy(amount = v, amountError = null) }
    fun onTypeChange(v: TransactionType) { _state.value = _state.value.copy(type = v) }
    fun onCategoryChange(v: TransactionCategory) { _state.value = _state.value.copy(category = v) }
    fun onNotesChange(v: String) { _state.value = _state.value.copy(notes = v) }
    fun onDateChange(v: Long) { _state.value = _state.value.copy(date = v) }

    fun save() {
        val s = _state.value
        var hasError = false

        if (s.title.isBlank()) {
            _state.value = s.copy(titleError = "Title is required"); hasError = true
        }
        val amount = s.amount.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            _state.value = _state.value.copy(amountError = "Enter a valid amount"); hasError = true
        }
        if (hasError) return

        viewModelScope.launch {
            val txn = TransactionEntity(
                id = editingId ?: 0,
                title = s.title.trim(),
                amount = amount!!,
                type = s.type,
                category = s.category,
                notes = s.notes.trim(),
                date = s.date
            )
            if (editingId != null) repository.updateTransaction(txn)
            else repository.insertTransaction(txn)
            _state.value = _state.value.copy(isSaved = true)
        }
    }
}