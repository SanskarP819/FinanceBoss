package com.example.financeboss



import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.financeboss.data.preferences.UserPreferences
import com.example.financeboss.data.repository.GoalRepository
import com.example.financeboss.data.repository.TransactionRepository

class MainViewModelFactory(
    private val prefs: UserPreferences,
    private val transactionRepository: TransactionRepository,
    private val goalRepository: GoalRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(prefs, transactionRepository, goalRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    }
}