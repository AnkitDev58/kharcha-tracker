package org.example.project.domain.usecase.annualexpense

import kotlinx.coroutines.flow.Flow
import org.example.project.domain.model.AnnualExpense
import org.example.project.domain.repository.AnnualExpenseRepository

class GetAnnualExpensesUseCase(private val repository: AnnualExpenseRepository) {
    operator fun invoke(): Flow<List<AnnualExpense>> = repository.getAllAnnualExpenses()
}
