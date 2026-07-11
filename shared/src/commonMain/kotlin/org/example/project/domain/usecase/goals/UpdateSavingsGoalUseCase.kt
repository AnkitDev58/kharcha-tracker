package org.example.project.domain.usecase.goals

import org.example.project.domain.model.SavingsGoal
import org.example.project.domain.repository.SavingsGoalRepository

class UpdateSavingsGoalUseCase(private val repository: SavingsGoalRepository) {
    suspend operator fun invoke(goal: SavingsGoal) {
        require(goal.id > 0) { "Goal id must be valid for update" }
        require(goal.name.isNotBlank()) { "Goal name must not be empty" }
        require(goal.targetAmount > 0) { "Target amount must be positive" }
        repository.updateSavingsGoal(goal)
    }
}
