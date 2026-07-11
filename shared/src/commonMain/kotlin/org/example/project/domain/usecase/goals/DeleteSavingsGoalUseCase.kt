package org.example.project.domain.usecase.goals

import org.example.project.domain.repository.SavingsGoalRepository

class DeleteSavingsGoalUseCase(private val repository: SavingsGoalRepository) {
    suspend operator fun invoke(goalId: Long) {
        require(goalId > 0) { "Goal id must be valid for deletion" }
        repository.deleteSavingsGoalById(goalId)
    }
}
