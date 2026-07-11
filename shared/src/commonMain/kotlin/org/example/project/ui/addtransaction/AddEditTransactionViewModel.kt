package org.example.project.ui.addtransaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import org.example.project.core.util.DateTimeUtils
import org.example.project.domain.model.*
import org.example.project.domain.repository.CategoryRepository
import org.example.project.domain.repository.TransactionRepository
import org.example.project.domain.usecase.transaction.AddTransactionUseCase
import org.example.project.domain.usecase.transaction.UpdateTransactionUseCase

data class AddEditTransactionUiState(
    val isLoading: Boolean = false,
    val isEditMode: Boolean = false,
    val amount: String = "",
    val selectedType: TransactionType = TransactionType.EXPENSE,
    val selectedCategory: Category? = null,
    val note: String = "",
    val dateTime: LocalDateTime = DateTimeUtils.currentDateTime(),
    val paymentMethod: PaymentMethod = PaymentMethod.CASH,
    val isFavorite: Boolean = false,
    val incomeCategories: List<Category> = emptyList(),
    val expenseCategories: List<Category> = emptyList(),
    val isSaved: Boolean = false,
    val error: String? = null
)

class AddEditTransactionViewModel(
    private val addTransactionUseCase: AddTransactionUseCase,
    private val updateTransactionUseCase: UpdateTransactionUseCase,
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditTransactionUiState())
    val uiState: StateFlow<AddEditTransactionUiState> = _uiState.asStateFlow()

    init {
        loadCategories()
    }

    fun loadForEdit(transactionId: Long) {
        if (transactionId == -1L) return
        viewModelScope.launch {
            transactionRepository.getTransactionById(transactionId)?.let { tx ->
                val category = categoryRepository.getCategoryById(tx.categoryId)
                _uiState.update {
                    it.copy(
                        isEditMode = true,
                        amount = tx.amount.toString(),
                        selectedType = tx.type,
                        selectedCategory = category,
                        note = tx.note,
                        dateTime = tx.dateTime,
                        paymentMethod = tx.paymentMethod,
                        isFavorite = tx.isFavorite
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
        viewModelScope.launch {
            categoryRepository.getCategoriesByType(TransactionType.INCOME).collect { cats ->
                _uiState.update { it.copy(incomeCategories = cats) }
            }
        }
    }

    fun setAmount(amount: String) {
        _uiState.update { it.copy(amount = amount.filter { c -> c.isDigit() || c == '.' }) }
    }

    fun setType(type: TransactionType) {
        _uiState.update { it.copy(selectedType = type, selectedCategory = null) }
    }

    fun setCategory(category: Category) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun setNote(note: String) {
        _uiState.update { it.copy(note = note) }
    }

    fun setDateTime(dateTime: LocalDateTime) {
        _uiState.update { it.copy(dateTime = dateTime) }
    }

    fun setPaymentMethod(method: PaymentMethod) {
        _uiState.update { it.copy(paymentMethod = method) }
    }

    fun toggleFavorite() {
        _uiState.update { it.copy(isFavorite = !it.isFavorite) }
    }

    fun save(existingId: Long = -1L) {
        val state = _uiState.value
        val amount = state.amount.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            _uiState.update { it.copy(error = "Please enter a valid amount") }
            return
        }
        val category = state.selectedCategory
        if (category == null) {
            _uiState.update { it.copy(error = "Please select a category") }
            return
        }

        viewModelScope.launch {
            val transaction = Transaction(
                id = if (existingId == -1L) 0L else existingId,
                amount = amount,
                type = state.selectedType,
                categoryId = category.id,
                note = state.note,
                dateTime = state.dateTime,
                paymentMethod = state.paymentMethod,
                isFavorite = state.isFavorite
            )
            try {
                if (existingId == -1L) addTransactionUseCase(transaction)
                else updateTransactionUseCase(transaction)
                _uiState.update { it.copy(isSaved = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
}
