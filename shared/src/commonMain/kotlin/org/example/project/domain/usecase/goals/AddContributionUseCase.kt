package org.example.project.domain.usecase.goals

import org.example.project.domain.repository.SavingsGoalRepository

class AddContributionUseCase(private val repository: SavingsGoalRepository) {
    suspend operator fun invoke(goalId: Long, amount: Double) {
        require(goalId > 0) { "Goal id must be valid" }
        require(amount > 0) { "Contribution amount must be positive" }
        val goal = repository.getSavingsGoalById(goalId)
            ?: error("Goal not found with id $goalId")
        repository.updateSavingsGoal(
            goal.copy(currentAmount = goal.currentAmount + amount)
        )
    }
}
