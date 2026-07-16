package org.example.project.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import org.example.project.core.util.ClockSystem
import org.example.project.core.util.DateTimeUtils
import org.example.project.domain.model.TransactionType
import org.example.project.domain.repository.TransactionRepository
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

data class DaySummary(
    val date: LocalDate,
    val income: Double,
    val expense: Double,
    val hasTransactions: Boolean
)

data class CalendarUiState(
    val isLoading: Boolean = true,
    val year: Int = 0,
    val month: Int = 0,
    val daySummaries: Map<Int, DaySummary> = emptyMap(), // day-of-month → summary
    val selectedDay: Int? = null
)

class CalendarViewModel(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    init {
        val today = ClockSystem.todayIn(TimeZone.currentSystemDefault())
        _uiState.update { it.copy(year = today.year, month = today.monthNumber) }
        loadMonth(today.year, today.monthNumber)
    }

    fun previousMonth() {
        val s = _uiState.value
        val d = LocalDate(s.year, s.month, 1).minus(1, DateTimeUnit.MONTH)
        _uiState.update { it.copy(year = d.year, month = d.monthNumber, selectedDay = null) }
        loadMonth(d.year, d.monthNumber)
    }

    fun nextMonth() {
        val s = _uiState.value
        val d = LocalDate(s.year, s.month, 1).plus(1, DateTimeUnit.MONTH)
        _uiState.update { it.copy(year = d.year, month = d.monthNumber, selectedDay = null) }
        loadMonth(d.year, d.monthNumber)
    }

    fun selectDay(day: Int) {
        _uiState.update { it.copy(selectedDay = day) }
    }

    private fun loadMonth(year: Int, month: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val start = DateTimeUtils.monthStart(month, year).toString()
            val end   = DateTimeUtils.monthEnd(month, year).toString()

            transactionRepository.getTransactionsByDateRange(start, end)
                .collect { transactions ->
                    // Group by day of month, compute income/expense per day
                    val map = mutableMapOf<Int, DaySummary>()
                    transactions.groupBy { it.dateTime.dayOfMonth }.forEach { (day, txs) ->
                        val income  = txs.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
                        val expense = txs.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
                        val date    = LocalDate(year, month, day)
                        map[day] = DaySummary(date, income, expense, txs.isNotEmpty())
                    }
                    _uiState.update { it.copy(isLoading = false, daySummaries = map) }
                }
        }
    }
}
