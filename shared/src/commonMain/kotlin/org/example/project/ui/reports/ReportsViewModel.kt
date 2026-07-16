package org.example.project.ui.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import org.example.project.core.util.ClockSystem
import org.example.project.domain.model.MonthlyReport
import org.example.project.domain.usecase.reports.GetMonthlyReportUseCase

data class ReportsUiState(
    val isLoading: Boolean = true,
    val report: MonthlyReport? = null,
    val month: Int = 0,
    val year: Int = 0,
    val error: String? = null
)

class ReportsViewModel(
    private val getMonthlyReport: GetMonthlyReportUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()

    init {
        val today = ClockSystem.todayIn(TimeZone.currentSystemDefault())
        _uiState.update { it.copy(month = today.monthNumber, year = today.year) }
        load(today.monthNumber, today.year)
    }

    fun previousMonth() {
        val s = _uiState.value
        val d = LocalDate(s.year, s.month, 1).minus(1, DateTimeUnit.MONTH)
        _uiState.update { it.copy(month = d.monthNumber, year = d.year) }
        load(d.monthNumber, d.year)
    }

    fun nextMonth() {
        val s = _uiState.value
        val d = LocalDate(s.year, s.month, 1).plus(1, DateTimeUnit.MONTH)
        _uiState.update { it.copy(month = d.monthNumber, year = d.year) }
        load(d.monthNumber, d.year)
    }

    private fun load(month: Int, year: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching { getMonthlyReport(month, year) }
                .onSuccess { r -> _uiState.update { it.copy(isLoading = false, report = r) } }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
        }
    }
}
