package com.example.financeboss.data


import com.example.financeboss.data.local.entity.GoalEntity
import com.example.financeboss.data.local.entity.TransactionCategory
import com.example.financeboss.data.local.entity.TransactionEntity
import com.example.financeboss.data.local.entity.TransactionType
import java.util.Calendar

object SeedData {
    fun getTransactions(): List<TransactionEntity> {
        val cal = Calendar.getInstance()
        fun daysAgo(days: Int): Long {
            val c = Calendar.getInstance()
            c.add(Calendar.DAY_OF_YEAR, -days)
            return c.timeInMillis
        }

        return listOf(
            TransactionEntity(amount = 55000.0, type = TransactionType.INCOME, category = TransactionCategory.SALARY, title = "Monthly Salary", notes = "June salary", date = daysAgo(2)),
            TransactionEntity(amount = 850.0, type = TransactionType.EXPENSE, category = TransactionCategory.FOOD, title = "Grocery Shopping", notes = "Vegetables & fruits", date = daysAgo(1)),
            TransactionEntity(amount = 200.0, type = TransactionType.EXPENSE, category = TransactionCategory.TRANSPORT, title = "Uber Ride", notes = "Office commute", date = daysAgo(1)),
            TransactionEntity(amount = 5000.0, type = TransactionType.EXPENSE, category = TransactionCategory.SHOPPING, title = "Clothes", notes = "Summer collection", date = daysAgo(3)),
            TransactionEntity(amount = 1200.0, type = TransactionType.EXPENSE, category = TransactionCategory.ENTERTAINMENT, title = "Netflix + Spotify", notes = "Subscriptions", date = daysAgo(4)),
            TransactionEntity(amount = 3000.0, type = TransactionType.EXPENSE, category = TransactionCategory.BILLS, title = "Electricity Bill", notes = "June bill", date = daysAgo(5)),
            TransactionEntity(amount = 500.0, type = TransactionType.EXPENSE, category = TransactionCategory.HEALTH, title = "Pharmacy", notes = "Vitamins", date = daysAgo(6)),
            TransactionEntity(amount = 8000.0, type = TransactionType.INCOME, category = TransactionCategory.FREELANCE, title = "Freelance Project", notes = "Logo design", date = daysAgo(7)),
            TransactionEntity(amount = 650.0, type = TransactionType.EXPENSE, category = TransactionCategory.FOOD, title = "Restaurant Dinner", notes = "Family dinner", date = daysAgo(8)),
            TransactionEntity(amount = 2000.0, type = TransactionType.EXPENSE, category = TransactionCategory.EDUCATION, title = "Udemy Course", notes = "Android Dev course", date = daysAgo(9)),
            TransactionEntity(amount = 450.0, type = TransactionType.EXPENSE, category = TransactionCategory.TRANSPORT, title = "Metro Card", notes = "Monthly recharge", date = daysAgo(10)),
            TransactionEntity(amount = 300.0, type = TransactionType.EXPENSE, category = TransactionCategory.FOOD, title = "Zomato Order", notes = "Lunch delivery", date = daysAgo(11)),
            TransactionEntity(amount = 10000.0, type = TransactionType.INCOME, category = TransactionCategory.INVESTMENT, title = "Stock Dividend", notes = "Q2 dividend", date = daysAgo(12)),
            TransactionEntity(amount = 1800.0, type = TransactionType.EXPENSE, category = TransactionCategory.SHOPPING, title = "Books", notes = "Programming books", date = daysAgo(13)),
            TransactionEntity(amount = 700.0, type = TransactionType.EXPENSE, category = TransactionCategory.ENTERTAINMENT, title = "Movie Tickets", notes = "Weekend outing", date = daysAgo(14)),
        )
    }


    fun getGoals(): List<GoalEntity> {
        val cal = Calendar.getInstance()
        val month = cal.get(Calendar.MONTH) + 1
        val year = cal.get(Calendar.YEAR)
        return listOf(
            GoalEntity(category = TransactionCategory.FOOD, budgetLimit = 3000.0, month = month, year = year),
            GoalEntity(category = TransactionCategory.TRANSPORT, budgetLimit = 1500.0, month = month, year = year),
            GoalEntity(category = TransactionCategory.SHOPPING, budgetLimit = 5000.0, month = month, year = year),
            GoalEntity(category = TransactionCategory.ENTERTAINMENT, budgetLimit = 2000.0, month = month, year = year),
        )
    }
}