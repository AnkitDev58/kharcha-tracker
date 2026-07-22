package org.example.project.domain.usecase.budget

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.todayIn
import org.example.project.core.util.ClockSystem
import org.example.project.domain.model.Budget
import org.example.project.domain.repository.BudgetRepository
import org.example.project.domain.repository.CategoryRepository
import org.example.project.domain.repository.TransactionRepository

@OptIn(ExperimentalCoroutinesApi::class)
class GetBudgetsWithSpendingUseCase(
    private val budgetRepository: BudgetRepository,
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository
) {
    operator fun invoke(month: Int? = null, year: Int? = null): Flow<List<Budget>> {
        val today = ClockSystem.todayIn(TimeZone.currentSystemDefault())
        val targetMonth = month ?: today.month.number
        val targetYear = year ?: today.year

        val monthStart = "$targetYear-${targetMonth.toString().padStart(2, '0')}-01T00:00:00"
        val monthEnd = "$targetYear-${targetMonth.toString().padStart(2, '0')}-31T23:59:59"

        return budgetRepository.getBudgetsForMonth(targetMonth, targetYear)
            .flatMapLatest { budgets ->
                flow {
                    val categoryTotals =
                        transactionRepository.getCategoryTotals(monthStart, monthEnd)
                    val enriched = budgets.map { budget ->
                        val category = categoryRepository.getCategoryById(budget.categoryId)
                        val spent =
                            categoryTotals.find { it.categoryId == budget.categoryId }?.total ?: 0.0
                        budget.copy(category = category, spent = spent)
                    }
                    emit(enriched)
                }
            }
    }
}
