package org.example.project.domain.model

data class MonthlyReport(
    val month: Int,
    val year: Int,
    val totalIncome: Double,
    val totalExpense: Double,
    val totalSavings: Double,
    val monthlyBudget: Double,
    val budgetUtilization: Float,
    val categoryBreakdown: List<CategoryLine>,
    val highestExpenseCategory: String,
    val lowestExpenseCategory: String,
    val highestSingleExpense: Double,
    val avgDailySpend: Double,
    val totalTransactions: Int,
    val totalLiabilities: Double
) {
    data class CategoryLine(
        val categoryName: String,
        val colorHex: String,
        val total: Double
    )

    val savingsRate: Float
        get() = if (totalIncome > 0) (totalSavings / totalIncome).toFloat().coerceIn(0f, 1f) else 0f
}
