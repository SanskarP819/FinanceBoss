package com.example.financeboss.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.financeboss.data.local.dao.GoalDao
import com.example.financeboss.data.local.dao.TransactionDao
import com.example.financeboss.data.local.entity.GoalEntity
import com.example.financeboss.data.local.entity.TransactionCategory
import com.example.financeboss.data.local.entity.TransactionEntity
import com.example.financeboss.data.local.entity.TransactionType


class Converters {
    @TypeConverter fun fromTransactionType(type: TransactionType) = type.name
    @TypeConverter fun toTransactionType(name: String) = TransactionType.valueOf(name)
    @TypeConverter fun fromCategory(cat: TransactionCategory) = cat.name
    @TypeConverter fun toCategory(name: String) = TransactionCategory.valueOf(name)
}

@Database(entities = [TransactionEntity::class, GoalEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun goalDao(): GoalDao


    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "finance_db")
                    .build().also { INSTANCE = it }
            }
        }
    }
}


