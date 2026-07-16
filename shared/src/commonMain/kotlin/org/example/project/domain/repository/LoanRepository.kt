package org.example.project.domain.repository

import kotlinx.coroutines.flow.Flow
import org.example.project.domain.model.Loan

interface LoanRepository {
    fun getAllLoans(): Flow<List<Loan>>
    suspend fun getLoanById(id: Long): Loan?
    suspend fun getTotalOutstanding(): Double
    suspend fun insertLoan(loan: Loan): Long
    suspend fun updateLoan(loan: Loan)
    suspend fun deleteLoanById(id: Long)
}
