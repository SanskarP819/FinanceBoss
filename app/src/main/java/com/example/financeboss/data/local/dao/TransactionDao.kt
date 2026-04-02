package com.example.financeboss.data.local.dao



import androidx.room.*
import com.example.financeboss.data.local.entity.TransactionEntity
import com.example.financeboss.data.local.entity.TransactionCategory
import com.example.financeboss.data.local.entity.TransactionType

import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE date >= :startDate AND date <= :endDate ORDER BY date DESC")
    fun getTransactionsByDateRange(startDate: Long, endDate: Long): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY date DESC")
    fun getTransactionsByType(type: TransactionType): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE category = :category ORDER BY date DESC")
    fun getTransactionsByCategory(category: TransactionCategory): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE title LIKE '%' || :query || '%' OR notes LIKE '%' || :query || '%' ORDER BY date DESC")
    fun searchTransactions(query: String): Flow<List<TransactionEntity>>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE type = 'INCOME' AND date >= :startDate AND date <= :endDate")
    fun getTotalIncome(startDate: Long, endDate: Long): Flow<Double>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE type = 'EXPENSE' AND date >= :startDate AND date <= :endDate")
    fun getTotalExpense(startDate: Long, endDate: Long): Flow<Double>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE type = 'EXPENSE' AND category = :category AND date >= :startDate AND date <= :endDate")
    fun getExpenseByCategory(category: TransactionCategory, startDate: Long, endDate: Long): Flow<Double>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: Long): TransactionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransactionById(id: Long)
}