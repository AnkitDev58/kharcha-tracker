package org.example.project.domain.usecase.summary

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.todayIn
import org.example.project.core.util.ClockSystem
import org.example.project.domain.model.FinancialSummary
import org.example.project.domain.repository.BudgetRepository
import org.example.project.domain.repository.TransactionRepository

class GetFinancialSummaryUseCase(
    private val transactionRepository: TransactionRepository,
    private val budgetRepository: BudgetRepository
) {
    suspend operator fun invoke(): FinancialSummary {
        val today = ClockSystem.todayIn(TimeZone.currentSystemDefault())
        val year = today.year
        val month = today.month.number

        val monthStart = "$year-${month.toString().padStart(2, '0')}-01T00:00:00"
        val monthEnd = "$year-${month.toString().padStart(2, '0')}-31T23:59:59"

        val allTimeIncome = transactionRepository.getTotalIncomeForPeriod("2000-01-01T00:00:00", "2099-12-31T23:59:59")
        val allTimeExpense = transactionRepository.getTotalExpenseForPeriod("2000-01-01T00:00:00", "2099-12-31T23:59:59")

        val monthlyIncome = transactionRepository.getTotalIncomeForPeriod(monthStart, monthEnd)
        val monthlyExpense = transactionRepository.getTotalExpenseForPeriod(monthStart, monthEnd)
        val monthlyBudget = budgetRepository.getTotalBudgetForMonth(month, year)

        return FinancialSummary(
            totalIncome = allTimeIncome,
            totalExpense = allTimeExpense,
            totalSavings = allTimeIncome - allTimeExpense,
            monthlyBudget = monthlyBudget,
            monthlySpent = monthlyExpense
        )
    }
}
