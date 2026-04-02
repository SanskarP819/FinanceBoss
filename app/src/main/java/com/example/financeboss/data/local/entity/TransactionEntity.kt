package com.example.financeboss.data.local.entity



import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TransactionType { INCOME, EXPENSE }

enum class TransactionCategory(val displayName: String, val emoji: String) {
    FOOD("Food", "🍔"),
    TRANSPORT("Transport", "🚗"),
    SHOPPING("Shopping", "🛍️"),
    ENTERTAINMENT("Entertainment", "🎬"),
    HEALTH("Health", "💊"),
    BILLS("Bills", "📄"),
    EDUCATION("Education", "📚"),
    SALARY("Salary", "💼"),
    FREELANCE("Freelance", "💻"),
    INVESTMENT("Investment", "📈"),
    OTHER("Other", "📦")
}

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Double,
    val type: TransactionType,
    val category: TransactionCategory,
    val title: String,
    val notes: String = "",
    val date: Long = System.currentTimeMillis()
)