package com.example.financeboss.ui.transactions



import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.financeboss.data.repository.TransactionRepository


class AddEditTransactionViewModelFactory(
    private val repository: TransactionRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddEditTransactionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddEditTransactionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    }
}