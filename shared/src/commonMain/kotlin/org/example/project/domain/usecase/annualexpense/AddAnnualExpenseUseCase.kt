package org.example.project.domain.usecase.annualexpense

import org.example.project.domain.model.AnnualExpense
import org.example.project.domain.repository.AnnualExpenseRepository

class AddAnnualExpenseUseCase(private val repository: AnnualExpenseRepository) {
    suspend operator fun invoke(expense: AnnualExpense): Long =
        repository.insertAnnualExpense(expense)
}
