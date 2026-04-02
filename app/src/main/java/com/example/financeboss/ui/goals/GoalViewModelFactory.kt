package com.example.financeboss.ui.goals



import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.financeboss.data.repository.GoalRepository
import com.example.financeboss.data.repository.TransactionRepository

class GoalViewModelFactory(
    private val goalRepository: GoalRepository,
    private val transactionRepository: TransactionRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GoalViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GoalViewModel(goalRepository, transactionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    }
}