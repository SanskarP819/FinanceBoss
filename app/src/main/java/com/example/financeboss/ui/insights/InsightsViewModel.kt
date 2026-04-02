package com.example.financeboss.ui.insights



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeboss.data.local.entity.TransactionCategory
import com.example.financeboss.data.local.entity.TransactionType
import com.example.financeboss.data.repository.TransactionRepository

import kotlinx.coroutines.flow.*
import java.util.Calendar

data class CategorySpending(
    val category: TransactionCategory,
    val amount: Double,
    val percentage: Float
)

data class InsightsUiState(
    val totalExpenseThisMonth: Double = 0.0,
    val totalExpenseLastMonth: Double = 0.0,
    val totalIncomeThisMonth: Double = 0.0,
    val categoryBreakdown: List<CategorySpending> = emptyList(),
    val topCategory: TransactionCategory? = null,
    val thisWeekSpending: Double = 0.0,
    val lastWeekSpending: Double = 0.0,
    val monthlyTrend: List<Pair<String, Double>> = emptyList(),
    val isLoading: Boolean = true
) {
    val expenseChangePercent: Float
        get() = if (totalExpenseLastMonth == 0.0) 0f
        else ((totalExpenseThisMonth - totalExpenseLastMonth) / totalExpenseLastMonth * 100).toFloat()

    val weekChangePercent: Float
        get() = if (lastWeekSpending == 0.0) 0f
        else ((thisWeekSpending - lastWeekSpending) / lastWeekSpending * 100).toFloat()
}

class InsightsViewModel(
    private val repository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(InsightsUiState())
    val uiState: StateFlow<InsightsUiState> = _uiState.asStateFlow()

    init { loadInsights() }

    private fun loadInsights() {
        repository.getAllTransactions()
            .onEach { transactions ->
                val cal = Calendar.getInstance()

                val thisMonthStart = monthStart(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH))
                val thisMonthEnd = monthEnd(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH))

                val lastMonthCal = Calendar.getInstance().apply { add(Calendar.MONTH, -1) }
                val lastMonthStart = monthStart(lastMonthCal.get(Calendar.YEAR), lastMonthCal.get(Calendar.MONTH))
                val lastMonthEnd = monthEnd(lastMonthCal.get(Calendar.YEAR), lastMonthCal.get(Calendar.MONTH))

                val thisWeekStart = weekStart(0)
                val thisWeekEnd = System.currentTimeMillis()
                val lastWeekStart = weekStart(-1)
                val lastWeekEnd = weekStart(0) - 1

                val thisMonthExpenses = transactions.filter {
                    it.type == TransactionType.EXPENSE && it.date in thisMonthStart..thisMonthEnd
                }
                val lastMonthExpenses = transactions.filter {
                    it.type == TransactionType.EXPENSE && it.date in lastMonthStart..lastMonthEnd
                }
                val thisMonthIncome = transactions.filter {
                    it.type == TransactionType.INCOME && it.date in thisMonthStart..thisMonthEnd
                }

                val totalExpenseThisMonth = thisMonthExpenses.sumOf { it.amount }
                val totalExpenseLastMonth = lastMonthExpenses.sumOf { it.amount }
                val totalIncomeThisMonth = thisMonthIncome.sumOf { it.amount }

                val categoryMap = thisMonthExpenses
                    .groupBy { it.category }
                    .mapValues { (_, v) -> v.sumOf { it.amount } }
                    .toList()
                    .sortedByDescending { it.second }

                val categoryBreakdown = categoryMap.map { (cat, amount) ->
                    CategorySpending(
                        category = cat,
                        amount = amount,
                        percentage = if (totalExpenseThisMonth > 0)
                            (amount / totalExpenseThisMonth * 100).toFloat() else 0f
                    )
                }

                val thisWeekSpending = transactions.filter {
                    it.type == TransactionType.EXPENSE && it.date in thisWeekStart..thisWeekEnd
                }.sumOf { it.amount }

                val lastWeekSpending = transactions.filter {
                    it.type == TransactionType.EXPENSE && it.date in lastWeekStart..lastWeekEnd
                }.sumOf { it.amount }

                val monthlyTrend = (5 downTo 0).map { monthsAgo ->
                    val c = Calendar.getInstance().apply { add(Calendar.MONTH, -monthsAgo) }
                    val start = monthStart(c.get(Calendar.YEAR), c.get(Calendar.MONTH))
                    val end = monthEnd(c.get(Calendar.YEAR), c.get(Calendar.MONTH))
                    val label = java.text.SimpleDateFormat("MMM", java.util.Locale.getDefault())
                        .format(c.time)
                    val total = transactions.filter {
                        it.type == TransactionType.EXPENSE && it.date in start..end
                    }.sumOf { it.amount }
                    Pair(label, total)
                }

                _uiState.value = InsightsUiState(
                    totalExpenseThisMonth = totalExpenseThisMonth,
                    totalExpenseLastMonth = totalExpenseLastMonth,
                    totalIncomeThisMonth = totalIncomeThisMonth,
                    categoryBreakdown = categoryBreakdown,
                    topCategory = categoryBreakdown.firstOrNull()?.category,
                    thisWeekSpending = thisWeekSpending,
                    lastWeekSpending = lastWeekSpending,
                    monthlyTrend = monthlyTrend,
                    isLoading = false
                )
            }
            .launchIn(viewModelScope)
    }

    private fun monthStart(year: Int, month: Int): Long =
        Calendar.getInstance().apply {
            set(year, month, 1, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

    private fun monthEnd(year: Int, month: Int): Long =
        Calendar.getInstance().apply {
            set(year, month, 1)
            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis

    private fun weekStart(weeksAgo: Int): Long =
        Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            add(Calendar.WEEK_OF_YEAR, weeksAgo)
        }.timeInMillis
}