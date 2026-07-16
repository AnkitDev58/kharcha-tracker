package org.example.project.domain.usecase.loan

import org.example.project.domain.model.Loan
import org.example.project.domain.repository.LoanRepository

class AddLoanUseCase(private val repository: LoanRepository) {
    suspend operator fun invoke(loan: Loan): Long = repository.insertLoan(loan)
}
