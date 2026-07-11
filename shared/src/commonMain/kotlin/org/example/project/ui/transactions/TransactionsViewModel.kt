package org.example.project.ui.transactions

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.domain.model.Transaction
import org.example.project.domain.model.TransactionType
import org.example.project.domain.repository.CategoryRepository
import org.example.project.domain.usecase.transaction.DeleteTransactionUseCase
import org.example.project.domain.usecase.transaction.GetTransactionsUseCase
import org.example.project.domain.usecase.transaction.UpdateTransactionUseCase
import org.example.project.platform.logD
import org.example.project.ui.navigation.TransactionsRoute

enum class TransactionFilter { ALL, INCOME, EXPENSE }
enum class SortOrder { DATE_DESC, DATE_ASC, AMOUNT_DESC, AMOUNT_ASC }

private const val PAGE_SIZE = 20

data class TransactionsUiState(
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val transactions: List<Transaction> = emptyList(),
    val filter: TransactionFilter = TransactionFilter.ALL,
    val sortOrder: SortOrder = SortOrder.DATE_DESC,
    val searchQuery: String = "",
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val hasMore: Boolean = false,
    val currentPage: Int = 0,
    val error: String? = null
)

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class TransactionsViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val updateTransactionUseCase: UpdateTransactionUseCase,
    private val categoryRepository: CategoryRepository
) : ViewModel() {


    val route = savedStateHandle.toRoute<TransactionsRoute>()

    val selectedDate get() = route.date
    private val _uiState = MutableStateFlow(TransactionsUiState())
    val uiState: StateFlow<TransactionsUiState> = _uiState.asStateFlow()

    // Track the active load job so filter/sort changes can cancel it
    private var loadJob: Job? = null

    init {
        loadFirstPage()
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    fun setFilter(filter: TransactionFilter) {
        if (_uiState.value.filter == filter) return
        _uiState.update { it.copy(filter = filter) }
        resetAndReload()
    }

    fun setSortOrder(sort: SortOrder) {
        if (_uiState.value.sortOrder == sort) return
        _uiState.update { it.copy(sortOrder = sort) }
        resetAndReload()
    }

    fun setSearchQuery(query: String) {
        if (_uiState.value.searchQuery == query) return
        _uiState.update { it.copy(searchQuery = query) }
        resetAndReload()
    }

    /** Called when the list scrolls near the bottom. */
    fun loadMoreTransactions() {
        val state = _uiState.value
        if (!state.hasMore || state.isLoadingMore || state.isLoading) return
        loadPage(state.currentPage + 1, append = true)
    }

    fun deleteTransaction(id: Long) {
        viewModelScope.launch {
            deleteTransactionUseCase(id)
            // After deletion refresh from page 0 to keep offsets accurate
            resetAndReload()
        }
    }

    fun toggleFavorite(transaction: Transaction) {
        viewModelScope.launch {
            updateTransactionUseCase(transaction.copy(isFavorite = !transaction.isFavorite))
        }
    }

    // -------------------------------------------------------------------------
    // Internal helpers
    // -------------------------------------------------------------------------

    private fun resetAndReload() {
        loadJob?.cancel()
        _uiState.update {
            it.copy(
                isLoading = true,
                isLoadingMore = false,
                transactions = emptyList(),
                currentPage = 0,
                hasMore = false
            )
        }
        loadFirstPage()
    }

    private fun loadFirstPage() = loadPage(0, append = false)

    private fun loadPage(page: Int, append: Boolean) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            if (append) {
                _uiState.update { it.copy(isLoadingMore = true) }
            }

            logD("check data.  $page   $append")
            val state = _uiState.value
            val offset = page * PAGE_SIZE
            val query = state.searchQuery
            val filter = state.filter
            val sort = state.sortOrder

            try {

                val raw: List<Transaction> = when {
                    query.isNotBlank() -> getTransactionsUseCase.search(query).first()
                        .let { list -> list.drop(offset).take(PAGE_SIZE) }

                    filter == TransactionFilter.INCOME ->
                        getTransactionsUseCase.paged(PAGE_SIZE, offset,selectedDate,selectedDate).first()
                            .filter { it.type == TransactionType.INCOME }

                    filter == TransactionFilter.EXPENSE ->
                        getTransactionsUseCase.paged(PAGE_SIZE, offset,selectedDate,selectedDate).first()
                            .filter { it.type == TransactionType.EXPENSE }

                    else -> getTransactionsUseCase.paged(PAGE_SIZE, offset,selectedDate,selectedDate).first()
                }

                val sorted = raw.sortedWith(sort.comparator())

                val enriched = sorted.map { tx ->
                    tx.copy(category = categoryRepository.getCategoryById(tx.categoryId))
                }

                _uiState.update { current ->
                    val combined = if (append) current.transactions + enriched else enriched

                    // Totals are always computed over ALL loaded transactions
                    val income =
                        combined.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
                    val expense =
                        combined.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }

                    current.copy(
                        isLoading = false,
                        isLoadingMore = false,
                        transactions = combined,
                        currentPage = page,
                        // If we got a full page there might be more
                        hasMore = enriched.size == PAGE_SIZE,
                        totalIncome = income,
                        totalExpense = expense
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isLoadingMore = false,
                        error = e.message
                    )
                }
            }
        }
    }
}

private fun SortOrder.comparator(): Comparator<Transaction> = when (this) {
    SortOrder.DATE_DESC -> compareByDescending { it.dateTime }
    SortOrder.DATE_ASC -> compareBy { it.dateTime }
    SortOrder.AMOUNT_DESC -> compareByDescending { it.amount }
    SortOrder.AMOUNT_ASC -> compareBy { it.amount }
}
