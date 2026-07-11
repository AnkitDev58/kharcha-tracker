package org.example.project.domain.usecase.budget

import org.example.project.domain.model.Budget
import org.example.project.domain.repository.BudgetRepository

class AddBudgetUseCase(private val repository: BudgetRepository) {
    suspend operator fun invoke(budget: Budget): Long {
        require(budget.amount > 0) { "Budget amount must be positive" }
        // Replace existing budget for same category/month
        val existing = repository.getBudgetByCategoryAndMonth(
            budget.categoryId, budget.month, budget.year
        )
        return if (existing != null) {
            repository.updateBudget(budget.copy(id = existing.id))
            existing.id
        } else {
            repository.insertBudget(budget)
        }
    }
}
