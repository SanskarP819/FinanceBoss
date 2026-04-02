package com.example.financeboss.data.preferences



import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")


class UserPreferences  ( private val context: Context) {
    private val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
    private val IS_SEEDED = booleanPreferencesKey("is_seeded")

    val isDarkMode: Flow<Boolean> = context.dataStore.data.map { it[IS_DARK_MODE] ?: false }
    val isSeeded: Flow<Boolean> = context.dataStore.data.map { it[IS_SEEDED] ?: false }

    suspend fun toggleDarkMode(enabled: Boolean) {
        context.dataStore.edit { it[IS_DARK_MODE] = enabled }
    }

    suspend fun markSeeded() {
        context.dataStore.edit { it[IS_SEEDED] = true }
    }
}