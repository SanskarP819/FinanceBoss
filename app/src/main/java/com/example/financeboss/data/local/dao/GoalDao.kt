package com.example.financeboss.data.local.dao



import androidx.room.*
import com.example.financeboss.data.local.entity.GoalEntity
import com.example.financeboss.data.local.entity.TransactionCategory

import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {

    @Query("SELECT * FROM goals WHERE month = :month AND year = :year")
    fun getGoalsByMonthYear(month: Int, year: Int): Flow<List<GoalEntity>>

    @Query("SELECT * FROM goals WHERE category = :category AND month = :month AND year = :year")
    suspend fun getGoalByCategoryAndMonth(
        category: TransactionCategory, month: Int, year: Int
    ): GoalEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: GoalEntity): Long

    @Update
    suspend fun updateGoal(goal: GoalEntity)

    @Delete
    suspend fun deleteGoal(goal: GoalEntity)
}