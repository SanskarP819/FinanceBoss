package com.example.financeboss.ui.home



import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.financeboss.data.repository.TransactionRepository


class HomeViewModelFactory(
    private val transactionRepository: TransactionRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(transactionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    }
}