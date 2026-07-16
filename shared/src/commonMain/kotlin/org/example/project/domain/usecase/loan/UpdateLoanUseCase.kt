package org.example.project.domain.usecase.loan

import org.example.project.domain.model.Loan
import org.example.project.domain.repository.LoanRepository

class UpdateLoanUseCase(private val repository: LoanRepository) {
    suspend operator fun invoke(loan: Loan) = repository.updateLoan(loan)
}
