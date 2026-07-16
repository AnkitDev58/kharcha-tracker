package org.example.project.ui.annualexpense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.example.project.domain.model.AnnualExpense
import org.example.project.domain.model.ExpenseFrequency
import org.example.project.domain.usecase.annualexpense.*

data class AnnualExpenseUiState(
    val isLoading: Boolean = true,
    val expenses: List<AnnualExpense> = emptyList(),
    val totalMonthlyReserve: Double = 0.0,
    val error: String? = null
)

sealed interface AnnualExpenseEffect {
    data object Saved : AnnualExpenseEffect
    data object Deleted : AnnualExpenseEffect
    data class Error(val message: String) : AnnualExpenseEffect
}

class AnnualExpenseViewModel(
    private val getExpenses: GetAnnualExpensesUseCase,
    private val addExpense: AddAnnualExpenseUseCase,
    private val updateExpense: UpdateAnnualExpenseUseCase,
    private val deleteExpense: DeleteAnnualExpenseUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnnualExpenseUiState())
    val uiState: StateFlow<AnnualExpenseUiState> = _uiState.asStateFlow()

    private val _effect = Channel<AnnualExpenseEffect>(Channel.BUFFERED)
    val effect: Flow<AnnualExpenseEffect> = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            getExpenses().collect { list ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        expenses = list,
                        totalMonthlyReserve = list.sumOf { e -> e.monthlyReserve }
                    )
                }
            }
        }
    }

    fun save(
        id: Long,
        name: String,
        category: String,
        amount: Double,
        frequency: ExpenseFrequency,
        dueMonth: Int,
        notes: String
    ) {
        viewModelScope.launch {
            runCatching {
                val expense = AnnualExpense(
                    id = id, name = name, category = category,
                    amount = amount, frequency = frequency,
                    dueMonth = dueMonth, notes = notes
                )
                if (id == 0L) addExpense(expense) else updateExpense(expense)
            }.onSuccess { _effect.send(AnnualExpenseEffect.Saved) }
             .onFailure { _effect.send(AnnualExpenseEffect.Error(it.message ?: "Failed")) }
        }
    }

    fun delete(id: Long) {
        viewModelScope.launch {
            runCatching { deleteExpense(id) }
                .onSuccess { _effect.send(AnnualExpenseEffect.Deleted) }
                .onFailure { _effect.send(AnnualExpenseEffect.Error(it.message ?: "Failed")) }
        }
    }
}
