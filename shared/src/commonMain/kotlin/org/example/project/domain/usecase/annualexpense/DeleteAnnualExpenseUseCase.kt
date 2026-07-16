package org.example.project.domain.usecase.annualexpense

import org.example.project.domain.repository.AnnualExpenseRepository

class DeleteAnnualExpenseUseCase(private val repository: AnnualExpenseRepository) {
    suspend operator fun invoke(id: Long) = repository.deleteAnnualExpenseById(id)
}
