package org.example.project.domain.repository

import kotlinx.coroutines.flow.Flow
import org.example.project.domain.model.AnnualExpense

interface AnnualExpenseRepository {
    fun getAllAnnualExpenses(): Flow<List<AnnualExpense>>
    suspend fun getAnnualExpenseById(id: Long): AnnualExpense?
    suspend fun getTotalMonthlyReserveRequired(): Double
    suspend fun insertAnnualExpense(expense: AnnualExpense): Long
    suspend fun updateAnnualExpense(expense: AnnualExpense)
    suspend fun deleteAnnualExpenseById(id: Long)
}
