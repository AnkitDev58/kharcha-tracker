package org.example.project.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.example.project.data.mapper.toDomain
import org.example.project.data.mapper.toEntity
import org.example.project.database.dao.LoanDao
import org.example.project.domain.model.Loan
import org.example.project.domain.repository.LoanRepository

class LoanRepositoryImpl(
    private val dao: LoanDao
) : LoanRepository {

    override fun getAllLoans(): Flow<List<Loan>> =
        dao.getAllLoans().map { it.map { e -> e.toDomain() } }

    override suspend fun getLoanById(id: Long): Loan? =
        dao.getLoanById(id)?.toDomain()

    override suspend fun getTotalOutstanding(): Double =
        dao.getTotalOutstanding() ?: 0.0

    override suspend fun insertLoan(loan: Loan): Long =
        dao.insertLoan(loan.toEntity())

    override suspend fun updateLoan(loan: Loan) =
        dao.updateLoan(loan.toEntity())

    override suspend fun deleteLoanById(id: Long) =
        dao.deleteLoanById(id)
}
