package org.example.project.domain.usecase.annualexpense

import org.example.project.domain.model.AnnualExpense
import org.example.project.domain.repository.AnnualExpenseRepository

class UpdateAnnualExpenseUseCase(private val repository: AnnualExpenseRepository) {
    suspend operator fun invoke(expense: AnnualExpense) =
        repository.updateAnnualExpense(expense)
}
