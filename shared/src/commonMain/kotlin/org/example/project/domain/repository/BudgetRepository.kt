package org.example.project.domain.repository

import kotlinx.coroutines.flow.Flow
import org.example.project.domain.model.Budget

interface BudgetRepository {
    fun getAllBudgets(): Flow<List<Budget>>
    fun getBudgetsForMonth(month: Int, year: Int): Flow<List<Budget>>
    suspend fun getBudgetByCategoryAndMonth(categoryId: Long, month: Int, year: Int): Budget?
    suspend fun getBudgetById(id: Long): Budget?
    suspend fun insertBudget(budget: Budget): Long
    suspend fun updateBudget(budget: Budget)
    suspend fun deleteBudgetById(id: Long)
    suspend fun getTotalBudgetForMonth(month: Int, year: Int): Double
}
