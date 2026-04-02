package com.example.financeboss.ui.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.financeboss.data.repository.TransactionRepository


class InsightsViewModelFactory(
    private val repository: TransactionRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InsightsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InsightsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    }
}