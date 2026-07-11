package org.example.project.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.core.data.SeedDataManager
import org.example.project.core.util.DateTimeUtils
import org.example.project.domain.model.Budget
import org.example.project.domain.model.CategorySummary
import org.example.project.domain.model.FinancialSummary
import org.example.project.domain.model.InsightItem
import org.example.project.domain.model.InsightType
import org.example.project.domain.model.Transaction
import org.example.project.domain.repository.CategoryRepository
import org.example.project.domain.usecase.budget.GetBudgetsWithSpendingUseCase
import org.example.project.domain.usecase.summary.GetCategorySummaryUseCase
import org.example.project.domain.usecase.summary.GetFinancialSummaryUseCase
import org.example.project.domain.usecase.transaction.GetTransactionsUseCase

data class HomeUiState(
    val isLoading: Boolean = true,
    val summary: FinancialSummary = FinancialSummary(),
    val recentTransactions: List<Transaction> = emptyList(),
    val categorySummaries: List<CategorySummary> = emptyList(),
    val budgets: List<Budget> = emptyList(),
    val insights: List<InsightItem> = emptyList(),
    val error: String? = null
)

class HomeViewModel(
    private val seedDataManager: SeedDataManager,
    private val getFinancialSummaryUseCase: GetFinancialSummaryUseCase,
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val getCategorySummaryUseCase: GetCategorySummaryUseCase,
    private val getBudgetsWithSpendingUseCase: GetBudgetsWithSpendingUseCase,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getTransactionsUseCase.latest10Transactions().collect { allTransactions ->
                try {
                    val monthStart = DateTimeUtils.monthStart().toString()
                    val monthEnd = DateTimeUtils.monthEnd().toString()

                    // Map categories to transactions
                    val recent = allTransactions.map { tx ->
                        val cat = categoryRepository.getCategoryById(tx.categoryId)
                        tx.copy(category = cat)
                    }

                    val summary = getFinancialSummaryUseCase()
                    val categorySummaries = getCategorySummaryUseCase(monthStart, monthEnd)
                    val insights = generateInsights(summary, categorySummaries)

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            summary = summary,
                            recentTransactions = recent,
                            categorySummaries = categorySummaries,
                            insights = insights
                        )
                    }
                } catch (e: Exception) {
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
            }
        }

        viewModelScope.launch {
            getBudgetsWithSpendingUseCase().collect { budgets ->
                _uiState.update { it.copy(budgets = budgets) }
            }
        }
    }

    private fun generateInsights(
        summary: FinancialSummary,
        categories: List<CategorySummary>
    ): List<InsightItem> {
        val insights = mutableListOf<InsightItem>()

        if (summary.budgetProgress > 0.8f) {
            insights.add(
                InsightItem(
                    message = "You've used ${(summary.budgetProgress * 100).toInt()}% of your monthly budget.",
                    type = InsightType.WARNING
                )
            )
        }

        categories.firstOrNull()?.let { top ->
            insights.add(
                InsightItem(
                    message = "${top.category.name} is your biggest expense this month at ${top.percentage.toInt()}%.",
                    type = InsightType.NEUTRAL
                )
            )
        }

        if (summary.totalSavings > 0) {
            insights.add(
                InsightItem(
                    message = "Great! You're saving ₹${summary.totalSavings.toLong()} overall.",
                    type = InsightType.POSITIVE
                )
            )
        }

        return insights
    }


    init {
        viewModelScope.launch {
            seedDataManager.seedIfEmpty()
        }
    }
}
