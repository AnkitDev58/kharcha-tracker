package org.example.project.ui.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.example.project.domain.model.Budget
import org.example.project.domain.model.Category
import org.example.project.domain.model.TransactionType
import org.example.project.domain.repository.CategoryRepository
import org.example.project.domain.usecase.budget.AddBudgetUseCase
import org.example.project.domain.usecase.budget.GetBudgetsWithSpendingUseCase

data class BudgetUiState(
    val isLoading: Boolean = true,
    val budgets: List<Budget> = emptyList(),
    val totalBudget: Double = 0.0,
    val totalSpent: Double = 0.0,
    val expenseCategories: List<Category> = emptyList(),
    val error: String? = null
)

class BudgetViewModel(
    private val getBudgetsWithSpendingUseCase: GetBudgetsWithSpendingUseCase,
    private val addBudgetUseCase: AddBudgetUseCase,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BudgetUiState())
    val uiState: StateFlow<BudgetUiState> = _uiState.asStateFlow()

    init {
        loadBudgets()
        loadCategories()
    }

    private fun loadBudgets() {
        viewModelScope.launch {
            getBudgetsWithSpendingUseCase().collect { budgets ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        budgets = budgets,
                        totalBudget = budgets.sumOf { b -> b.amount },
                        totalSpent = budgets.sumOf { b -> b.spent }
                    )
                }
            }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            categoryRepository.getCategoriesByType(TransactionType.EXPENSE).collect { cats ->
                _uiState.update { it.copy(expenseCategories = cats) }
            }
        }
    }

    fun saveBudget(categoryId: Long, amount: Double, month: Int, year: Int) {
        viewModelScope.launch {
            addBudgetUseCase(Budget(categoryId = categoryId, amount = amount, month = month, year = year))
        }
    }
}
