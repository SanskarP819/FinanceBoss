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
import com.example.financeboss.data.repository.TransactionRepository


import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


data class TransactionFilter(
    val searchQuery: String = "",
    val type: TransactionType? = null,
    val category: TransactionCategory? = null
)

data class TransactionUiState(
    val transactions: List<TransactionEntity> = emptyList(),
    val filter: TransactionFilter = TransactionFilter(),
    val isLoading: Boolean = true,
    val deletedTransaction: TransactionEntity? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionViewModel (
    private val repository: TransactionRepository
) : ViewModel() {



    private val _filter = MutableStateFlow(TransactionFilter())
    private val _deletedTransaction = MutableStateFlow<TransactionEntity?>(null)
    private val _isLoading = MutableStateFlow(true)

    val uiState: StateFlow<TransactionUiState> = combine(
        _filter.flatMapLatest { f ->
            when {
                f.searchQuery.isNotBlank() -> repository.searchTransactions(f.searchQuery)
                f.type != null -> repository.getTransactionsByType(f.type)
                f.category != null -> repository.getTransactionsByCategory(f.category)
                else -> repository.getAllTransactions()
            }
        },
        _filter,
        _deletedTransaction,
        _isLoading
    ) { txns, filter, deleted, loading ->
        TransactionUiState(
            transactions = txns,
            filter = filter,
            deletedTransaction = deleted,
            isLoading = loading
        )
    }.onStart { _isLoading.value = false }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TransactionUiState())

    fun updateSearch(query: String) {
        _filter.value = _filter.value.copy(searchQuery = query)
    }

    fun updateTypeFilter(type: TransactionType?) {
        _filter.value = _filter.value.copy(type = type, category = null)
    }

    fun updateCategoryFilter(category: TransactionCategory?) {
        _filter.value = _filter.value.copy(category = category, type = null)
    }

    fun clearFilters() {
        _filter.value = TransactionFilter()
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
            _deletedTransaction.value = transaction
        }
    }

    fun undoDelete() {
        viewModelScope.launch {
            _deletedTransaction.value?.let {
                repository.insertTransaction(it.copy(id = 0))
                _deletedTransaction.value = null
            }
        }
    }

    fun clearDeletedTransaction() {
        _deletedTransaction.value = null
    }
}