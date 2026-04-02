package com.example.financeboss

import android.app.Application
import com.example.financeboss.data.SeedData
import com.example.financeboss.data.preferences.UserPreferences
import com.example.financeboss.data.repository.TransactionRepository



import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.financeboss.data.local.AppDatabase
import com.example.financeboss.data.repository.GoalRepository


import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch



class MainViewModel (
    private val prefs: UserPreferences,
    private val transactionRepository: TransactionRepository,
    private val goalRepository: GoalRepository
) : ViewModel() {




    val isDarkMode = prefs.isDarkMode

    init {
        seedIfNeeded()
    }

    private fun seedIfNeeded() {
        viewModelScope.launch {
            val seeded = prefs.isSeeded.first()
            if (!seeded) {
                SeedData.getTransactions().forEach { transactionRepository.insertTransaction(it) }
                SeedData.getGoals().forEach {
                    goalRepository.upsertGoal(it)
                }
                prefs.markSeeded()
            }
        }
    }

    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch { prefs.toggleDarkMode(enabled) }
    }
}