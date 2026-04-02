package com.example.financeboss.ui.goals



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeboss.data.local.entity.GoalEntity
import com.example.financeboss.data.local.entity.TransactionCategory
import com.example.financeboss.data.local.entity.TransactionType
import com.example.financeboss.data.repository.GoalRepository
import com.example.financeboss.data.repository.TransactionRepository

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

data class BudgetProgress(
    val goal: GoalEntity,
    val spent: Double,
    val percentage: Float,
    val isOverBudget: Boolean,
    val remaining: Double
)

data class GoalUiState(
    val budgetProgressList: List<BudgetProgress> = emptyList(),
    val isLoading: Boolean = true,
    val showAddDialog: Boolean = false,
    val selectedCategory: TransactionCategory = TransactionCategory.FOOD,
    val budgetAmount: String = "",
    val budgetAmountError: String? = null
)

class GoalViewModel(
    private val goalRepository: GoalRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GoalUiState())
    val uiState: StateFlow<GoalUiState> = _uiState.asStateFlow()

    private val cal = Calendar.getInstance()
    private val currentMonth = cal.get(Calendar.MONTH) + 1
    private val currentYear = cal.get(Calendar.YEAR)

    init { loadGoals() }

    private fun loadGoals() {
        combine(
            goalRepository.getCurrentMonthGoals(),
            transactionRepository.getAllTransactions()
        ) { goals, transactions ->
            val monthStart = monthStart()
            val monthEnd = monthEnd()

            val monthExpenses = transactions.filter {
                it.type == TransactionType.EXPENSE && it.date in monthStart..monthEnd
            }

            val progressList = goals.map { goal ->
                val spent = monthExpenses
                    .filter { it.category == goal.category }
                    .sumOf { it.amount }
                val percentage = if (goal.budgetLimit > 0)
                    (spent / goal.budgetLimit * 100).toFloat() else 0f
                BudgetProgress(
                    goal = goal,
                    spent = spent,
                    percentage = percentage.coerceAtMost(100f),
                    isOverBudget = spent > goal.budgetLimit,
                    remaining = goal.budgetLimit - spent
                )
            }.sortedByDescending { it.percentage }

            _uiState.value = _uiState.value.copy(
                budgetProgressList = progressList,
                isLoading = false
            )
        }.launchIn(viewModelScope)
    }

    fun showAddDialog() {
        _uiState.value = _uiState.value.copy(
            showAddDialog = true,
            budgetAmount = "",
            budgetAmountError = null,
            selectedCategory = TransactionCategory.FOOD
        )
    }

    fun hideAddDialog() {
        _uiState.value = _uiState.value.copy(showAddDialog = false)
    }

    fun onCategoryChange(cat: TransactionCategory) {
        _uiState.value = _uiState.value.copy(selectedCategory = cat)
    }

    fun onBudgetAmountChange(v: String) {
        _uiState.value = _uiState.value.copy(budgetAmount = v, budgetAmountError = null)
    }

    fun saveGoal() {
        val amount = _uiState.value.budgetAmount.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            _uiState.value = _uiState.value.copy(budgetAmountError = "Enter a valid amount")
            return
        }
        viewModelScope.launch {
            goalRepository.upsertGoal(
                GoalEntity(
                    category = _uiState.value.selectedCategory,
                    budgetLimit = amount,
                    month = currentMonth,
                    year = currentYear
                )
            )
            hideAddDialog()
        }
    }

    fun deleteGoal(goal: GoalEntity) {
        viewModelScope.launch { goalRepository.deleteGoal(goal) }
    }

    private fun monthStart(): Long = Calendar.getInstance().apply {
        set(currentYear, currentMonth - 1, 1, 0, 0, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    private fun monthEnd(): Long = Calendar.getInstance().apply {
        set(currentYear, currentMonth - 1, 1)
        set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
    }.timeInMillis
}