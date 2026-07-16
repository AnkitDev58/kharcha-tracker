package org.example.project.domain.usecase.loan

import org.example.project.domain.repository.LoanRepository

class DeleteLoanUseCase(private val repository: LoanRepository) {
    suspend operator fun invoke(id: Long) = repository.deleteLoanById(id)
}
