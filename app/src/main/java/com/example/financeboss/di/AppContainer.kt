package com.example.financeboss.di



import android.content.Context
import androidx.room.Room
import com.example.financeboss.data.local.AppDatabase
import com.example.financeboss.data.preferences.UserPreferences
import com.example.financeboss.data.repository.GoalRepository
import com.example.financeboss.data.repository.TransactionRepository

class AppContainer(context: Context) {

    // Database — single instance
    private val database: AppDatabase by lazy {
        Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "finance_db"
        ).fallbackToDestructiveMigration().build()
    }

    // Repositories
    val transactionRepository: TransactionRepository by lazy {
        TransactionRepository(database.transactionDao())
    }

    val goalRepository: GoalRepository by lazy {
        GoalRepository(database.goalDao())
    }

    // Preferences
    val userPreferences: UserPreferences by lazy {
        UserPreferences(context.applicationContext)
    }
}