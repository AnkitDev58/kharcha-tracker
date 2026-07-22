package org.example.project.domain.usecase.reports

import org.example.project.core.util.DateTimeUtils
import org.example.project.domain.model.MonthlyReport
import org.example.project.domain.model.TransactionType
import org.example.project.domain.repository.BudgetRepository
import org.example.project.domain.repository.CategoryRepository
import org.example.project.domain.repository.LoanRepository
import org.example.project.domain.repository.TransactionRepository

class GetMonthlyReportUseCase(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val budgetRepository: BudgetRepository,
    private val loanRepository: LoanRepository
) {
    suspend operator fun invoke(month: Int, year: Int): MonthlyReport {
        val start = DateTimeUtils.monthStart(month, year).toString()
        val end   = DateTimeUtils.monthEnd(month, year).toString()

        val totalIncome  = transactionRepository.getTotalIncomeForPeriod(start, end)
        val totalExpense = transactionRepository.getTotalExpenseForPeriod(start, end)
        val savings      = totalIncome - totalExpense

        val budget       = budgetRepository.getTotalBudgetForMonth(month, year)
        val budgetUtil   = if (budget > 0) (totalExpense / budget).toFloat().coerceIn(0f, 1f) else 0f

        val categoryTotals = transactionRepository.getCategoryTotals(start, end)
        val allCategories  = categoryRepository.getAllCategoriesOnce()

        val catSummaries = categoryTotals.map { ct ->
            val cat = allCategories.firstOrNull { it.id == ct.categoryId }
            MonthlyReport.CategoryLine(
                categoryName = cat?.name ?: "Unknown",
                colorHex     = cat?.colorHex ?: "#6C63FF",
                total        = ct.total
            )
        }.sortedByDescending { it.total }

        val highestExpense = transactionRepository.getHighestTransaction(start, end, TransactionType.EXPENSE)
        val txCount        = transactionRepository.getTransactionCount()

        val totalOutstanding = loanRepository.getTotalOutstanding()

        // Average daily spend: expense / days in month
        val daysInMonth = DateTimeUtils.monthEnd(month, year).day
        val avgDailySpend = if (daysInMonth > 0) totalExpense / daysInMonth else 0.0

        return MonthlyReport(
            month            = month,
            year             = year,
            totalIncome      = totalIncome,
            totalExpense     = totalExpense,
            totalSavings     = savings,
            monthlyBudget    = budget,
            budgetUtilization = budgetUtil,
            categoryBreakdown = catSummaries,
            highestExpenseCategory = catSummaries.firstOrNull()?.categoryName ?: "—",
            lowestExpenseCategory  = catSummaries.lastOrNull()?.categoryName ?: "—",
            highestSingleExpense   = highestExpense?.amount ?: 0.0,
            avgDailySpend    = avgDailySpend,
            totalTransactions = txCount,
            totalLiabilities = totalOutstanding
        )
    }
}
