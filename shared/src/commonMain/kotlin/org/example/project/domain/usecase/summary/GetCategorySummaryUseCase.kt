package org.example.project.domain.usecase.summary

import org.example.project.domain.model.CategorySummary
import org.example.project.domain.repository.CategoryRepository
import org.example.project.domain.repository.TransactionRepository

class GetCategorySummaryUseCase(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(startDate: String, endDate: String): List<CategorySummary> {
        val categoryTotals = transactionRepository.getCategoryTotals(startDate, endDate)
        val totalExpense = categoryTotals.sumOf { it.total }.takeIf { it > 0 } ?: 1.0

        return categoryTotals.mapNotNull { result ->
            val category = categoryRepository.getCategoryById(result.categoryId) ?: return@mapNotNull null
            CategorySummary(
                category = category,
                total = result.total,
                percentage = ((result.total / totalExpense) * 100f).toFloat(),
                transactionCount = 0
            )
        }.sortedByDescending { it.total }
    }
}
