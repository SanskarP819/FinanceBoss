package com.example.financeboss.ui.home



import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.financeboss.data.local.AppDatabase
import com.example.financeboss.data.local.entity.TransactionEntity
import com.example.financeboss.data.preferences.UserPreferences
import com.example.financeboss.data.repository.TransactionRepository


import kotlinx.coroutines.flow.*
import java.util.Calendar


data class HomeUiState(
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val recentTransactions: List<TransactionEntity> = emptyList(),
    val weeklySpending: List<Pair<String, Double>> = emptyList(),
    val isLoading: Boolean = true
) {
    val balance get() = totalIncome - totalExpense
}


class HomeViewModel (
    private val repository: TransactionRepository
) : ViewModel() {



    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        combine(
            repository.getCurrentMonthIncome(),
            repository.getCurrentMonthExpense(),
            repository.getAllTransactions()
        ) { income, expense, all ->
            HomeUiState(
                totalIncome = income,
                totalExpense = expense,
                recentTransactions = all.take(5),
                weeklySpending = buildWeeklyData(all),
                isLoading = false
            )
        }.onEach { _uiState.value = it }
            .launchIn(viewModelScope)
    }

    private fun buildWeeklyData(transactions: List<TransactionEntity>): List<Pair<String, Double>> {
        val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        val cal = Calendar.getInstance()
        val today = cal.get(Calendar.DAY_OF_WEEK)

        return days.mapIndexed { index, day ->
            val target = Calendar.getInstance()
            val diff = ((index + 2) - today + 7) % 7
            target.add(Calendar.DAY_OF_YEAR, -(6 - index))
            val start = target.apply {
                set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0)
            }.timeInMillis
            val end = start + 86_400_000L - 1

            val total = transactions
                .filter { it.type.name == "EXPENSE" && it.date in start..end }
                .sumOf { it.amount }
            Pair(day, total)
        }
    }
}