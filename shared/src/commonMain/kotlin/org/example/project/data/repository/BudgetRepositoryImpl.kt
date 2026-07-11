package org.example.project.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.example.project.data.mapper.toDomain
import org.example.project.data.mapper.toEntity
import org.example.project.database.dao.BudgetDao
import org.example.project.domain.model.Budget
import org.example.project.domain.repository.BudgetRepository

class BudgetRepositoryImpl(
    private val budgetDao: BudgetDao
) : BudgetRepository {

    override fun getAllBudgets(): Flow<List<Budget>> =
        budgetDao.getAllBudgets().map { list -> list.map { it.toDomain() } }

    override fun getBudgetsForMonth(month: Int, year: Int): Flow<List<Budget>> =
        budgetDao.getBudgetsForMonth(month, year).map { list -> list.map { it.toDomain() } }

    override suspend fun getBudgetByCategoryAndMonth(categoryId: Long, month: Int, year: Int): Budget? =
        budgetDao.getBudgetByCategoryAndMonth(categoryId, month, year)?.toDomain()

    override suspend fun getBudgetById(id: Long): Budget? =
        budgetDao.getBudgetById(id)?.toDomain()

    override suspend fun insertBudget(budget: Budget): Long =
        budgetDao.insertBudget(budget.toEntity())

    override suspend fun updateBudget(budget: Budget) =
        budgetDao.updateBudget(budget.toEntity())

    override suspend fun deleteBudgetById(id: Long) =
        budgetDao.deleteBudgetById(id)

    override suspend fun getTotalBudgetForMonth(month: Int, year: Int): Double =
        budgetDao.getTotalBudgetForMonth(month, year) ?: 0.0
}
