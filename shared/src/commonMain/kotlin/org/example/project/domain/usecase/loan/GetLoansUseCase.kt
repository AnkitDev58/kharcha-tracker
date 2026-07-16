package org.example.project.domain.usecase.loan

import kotlinx.coroutines.flow.Flow
import org.example.project.domain.model.Loan
import org.example.project.domain.repository.LoanRepository

class GetLoansUseCase(private val repository: LoanRepository) {
    operator fun invoke(): Flow<List<Loan>> = repository.getAllLoans()
}
