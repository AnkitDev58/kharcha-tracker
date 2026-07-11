package org.example.project.domain.model

data class FinancialSummary(
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val totalSavings: Double = 0.0,
    val monthlyBudget: Double = 0.0,
    val monthlySpent: Double = 0.0
) {
    val balance: Double get() = totalIncome - totalExpense
    val budgetRemaining: Double get() = monthlyBudget - monthlySpent
    val budgetProgress: Float get() = if (monthlyBudget > 0) (monthlySpent / monthlyBudget).toFloat().coerceIn(0f, 1f) else 0f
}

data class CategorySummary(
    val category: Category,
    val total: Double,
    val percentage: Float,
    val transactionCount: Int
)

data class DailySpending(
    val date: String,
    val amount: Double,
    val list: List<Category>
)

data class MonthlyTrend(
    val month: Int,
    val year: Int,
    val income: Double,
    val expense: Double,
    val savings: Double
)

data class InsightItem(
    val message: String,
    val type: InsightType,
    val percentageChange: Double? = null
)

enum class InsightType {
    POSITIVE, NEGATIVE, NEUTRAL, WARNING
}
