package org.example.project.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.example.project.data.mapper.toDomain
import org.example.project.data.mapper.toEntity
import org.example.project.database.dao.SavingsGoalDao
import org.example.project.domain.model.SavingsGoal
import org.example.project.domain.repository.SavingsGoalRepository

class SavingsGoalRepositoryImpl(
    private val savingsGoalDao: SavingsGoalDao
) : SavingsGoalRepository {

    override fun getAllSavingsGoals(): Flow<List<SavingsGoal>> =
        savingsGoalDao.getAllSavingsGoals().map { list -> list.map { it.toDomain() } }

    override suspend fun getSavingsGoalById(id: Long): SavingsGoal? =
        savingsGoalDao.getSavingsGoalById(id)?.toDomain()

    override suspend fun insertSavingsGoal(goal: SavingsGoal): Long =
        savingsGoalDao.insertSavingsGoal(goal.toEntity())

    override suspend fun updateSavingsGoal(goal: SavingsGoal) =
        savingsGoalDao.updateSavingsGoal(goal.toEntity())

    override suspend fun deleteSavingsGoalById(id: Long) =
        savingsGoalDao.deleteSavingsGoalById(id)
}
