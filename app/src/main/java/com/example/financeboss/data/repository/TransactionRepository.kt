package com.example.financeboss.data.repository




import com.example.financeboss.data.local.dao.TransactionDao
import com.example.financeboss.data.local.entity.TransactionCategory
import com.example.financeboss.data.local.entity.TransactionEntity
import com.example.financeboss.data.local.entity.TransactionType
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class TransactionRepository  (
    private val dao: TransactionDao
) {
    fun getAllTransactions(): Flow<List<TransactionEntity>> = dao.getAllTransactions()

    fun searchTransactions(query: String): Flow<List<TransactionEntity>> =
        dao.searchTransactions(query)

    fun getTransactionsByType(type: TransactionType): Flow<List<TransactionEntity>> =
        dao.getTransactionsByType(type)

    fun getTransactionsByCategory(category: TransactionCategory): Flow<List<TransactionEntity>> =
        dao.getTransactionsByCategory(category)

    fun getCurrentMonthIncome(): Flow<Double> {
        val (start, end) = currentMonthRange()
        return dao.getTotalIncome(start, end)
    }

    fun getCurrentMonthExpense(): Flow<Double> {
        val (start, end) = currentMonthRange()
        return dao.getTotalExpense(start, end)
    }

    fun getExpenseByCategory(category: TransactionCategory): Flow<Double> {
        val (start, end) = currentMonthRange()
        return dao.getExpenseByCategory(category, start, end)
    }

    fun getTransactionsByDateRange(start: Long, end: Long): Flow<List<TransactionEntity>> =
        dao.getTransactionsByDateRange(start, end)

    suspend fun getTransactionById(id: Long): TransactionEntity? = dao.getTransactionById(id)

    suspend fun insertTransaction(transaction: TransactionEntity) =
        dao.insertTransaction(transaction)

    suspend fun updateTransaction(transaction: TransactionEntity) =
        dao.updateTransaction(transaction)

    suspend fun deleteTransaction(transaction: TransactionEntity) =
        dao.deleteTransaction(transaction)

    suspend fun deleteTransactionById(id: Long) = dao.deleteTransactionById(id)

    private fun currentMonthRange(): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0)
        val start = cal.timeInMillis
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        cal.set(Calendar.HOUR_OF_DAY, 23); cal.set(Calendar.MINUTE, 59); cal.set(Calendar.SECOND, 59)
        val end = cal.timeInMillis
        return Pair(start, end)
    }
}