package com.example.financeboss.data.repository




import com.example.financeboss.data.local.dao.GoalDao
import com.example.financeboss.data.local.entity.GoalEntity
import kotlinx.coroutines.flow.Flow
import java.util.Calendar



class GoalRepository (private val dao: GoalDao) {

    fun getCurrentMonthGoals(): Flow<List<GoalEntity>> {
        val cal = Calendar.getInstance()
        return dao.getGoalsByMonthYear(cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR))
    }

    suspend fun upsertGoal(goal: GoalEntity) {
        val cal = Calendar.getInstance()
        val existing = dao.getGoalByCategoryAndMonth(
            goal.category, cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR)
        )
        if (existing != null) dao.updateGoal(goal.copy(id = existing.id))
        else dao.insertGoal(goal)
    }

    suspend fun deleteGoal(goal: GoalEntity) = dao.deleteGoal(goal)
}