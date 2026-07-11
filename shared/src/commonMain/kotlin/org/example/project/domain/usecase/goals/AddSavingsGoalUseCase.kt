package org.example.project.domain.usecase.goals

import org.example.project.domain.model.SavingsGoal
import org.example.project.domain.repository.SavingsGoalRepository

class AddSavingsGoalUseCase(private val repository: SavingsGoalRepository) {
    suspend operator fun invoke(goal: SavingsGoal): Long {
        require(goal.name.isNotBlank()) { "Goal name must not be empty" }
        require(goal.targetAmount > 0) { "Target amount must be positive" }
        require(goal.currentAmount >= 0) { "Current amount must be non-negative" }
        return repository.insertSavingsGoal(goal)
    }
}
