package com.example.financeboss.data.local.entity



import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: TransactionCategory,
    val budgetLimit: Double,
    val month: Int,  // 1-12
    val year: Int
)