package org.example.project.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.example.project.data.mapper.toDomain
import org.example.project.data.mapper.toEntity
import org.example.project.database.dao.AnnualExpenseDao
import org.example.project.domain.model.AnnualExpense
import org.example.project.domain.repository.AnnualExpenseRepository

class AnnualExpenseRepositoryImpl(
    private val dao: AnnualExpenseDao
) : AnnualExpenseRepository {

    override fun getAllAnnualExpenses(): Flow<List<AnnualExpense>> =
        dao.getAllAnnualExpenses().map { it.map { e -> e.toDomain() } }

    override suspend fun getAnnualExpenseById(id: Long): AnnualExpense? =
        dao.getAnnualExpenseById(id)?.toDomain()

    override suspend fun getTotalMonthlyReserveRequired(): Double =
        dao.getTotalMonthlyReserveRequired() ?: 0.0

    override suspend fun insertAnnualExpense(expense: AnnualExpense): Long =
        dao.insertAnnualExpense(expense.toEntity())

    override suspend fun updateAnnualExpense(expense: AnnualExpense) =
        dao.updateAnnualExpense(expense.toEntity())

    override suspend fun deleteAnnualExpenseById(id: Long) =
        dao.deleteAnnualExpenseById(id)
}
