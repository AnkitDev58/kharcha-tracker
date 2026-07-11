package org.example.project.domain.usecase.goals

import kotlinx.coroutines.flow.Flow
import org.example.project.domain.model.SavingsGoal
import org.example.project.domain.repository.SavingsGoalRepository

class GetSavingsGoalsUseCase(private val repository: SavingsGoalRepository) {
    operator fun invoke(): Flow<List<SavingsGoal>> = repository.getAllSavingsGoals()
}
