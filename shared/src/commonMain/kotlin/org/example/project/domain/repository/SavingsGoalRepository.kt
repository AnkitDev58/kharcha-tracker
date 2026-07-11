package org.example.project.domain.repository

import kotlinx.coroutines.flow.Flow
import org.example.project.domain.model.SavingsGoal

interface SavingsGoalRepository {
    fun getAllSavingsGoals(): Flow<List<SavingsGoal>>
    suspend fun getSavingsGoalById(id: Long): SavingsGoal?
    suspend fun insertSavingsGoal(goal: SavingsGoal): Long
    suspend fun updateSavingsGoal(goal: SavingsGoal)
    suspend fun deleteSavingsGoalById(id: Long)
}
