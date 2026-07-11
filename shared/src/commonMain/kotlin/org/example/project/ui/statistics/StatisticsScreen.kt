package org.example.project.ui.statistics

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json
import org.example.project.core.util.CurrencyFormatter
import org.example.project.core.util.DateTimeUtils
import org.example.project.domain.model.Category
import org.example.project.domain.model.CategorySummary
import org.example.project.domain.model.DailySpending
import org.example.project.domain.repository.CategoryRepository
import org.example.project.domain.repository.TransactionRepository
import org.example.project.domain.usecase.summary.GetCategorySummaryUseCase
import org.example.project.ui.components.AnimatedLinearProgress
import org.example.project.ui.components.AppCard
import org.example.project.ui.components.BarChart
import org.example.project.ui.components.ChartDataPoint
import org.example.project.ui.components.ChartLegend
import org.example.project.ui.components.DonutChart
import org.example.project.ui.components.parseHexColor
import org.example.project.ui.theme.GradientExpenseStart
import org.example.project.ui.theme.GradientIncomeStart
import org.example.project.ui.theme.PrimaryPurple
import kotlin.random.Random
import kotlin.time.Instant

// -- ViewModel --

data class StatisticsUiState(
    val isLoading: Boolean = true,
    val dailySpending: List<DailySpending> = emptyList(),
    val categoryBreakdown: List<CategorySummary> = emptyList(),
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val selectedPeriod: StatPeriod = StatPeriod.MONTH
)

enum class StatPeriod(val label: String) {
    WEEK("Last 7 Days"), MONTH("Current Month"), YEAR("Current Year"), CUSTOM_RANGE("Custom Range")
}

class StatisticsViewModel(
    private val transactionRepository: TransactionRepository,
    private val getCategorySummaryUseCase: GetCategorySummaryUseCase,
    private val categoryRepository: CategoryRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    val isShow = mutableStateOf(false)
    private var statsJob: kotlinx.coroutines.Job? = null

    init {
        loadStats(StatPeriod.MONTH)
    }

    var start = ""
    var end = ""

    fun loadStats(period: StatPeriod) {
        statsJob?.cancel()
        statsJob = viewModelScope.launch {
            updatePeriod(period)
            val (start, end) = when (period) {
                StatPeriod.WEEK -> DateTimeUtils.last7DaysRange()
                StatPeriod.MONTH -> DateTimeUtils.currentMonthRange()
                StatPeriod.YEAR -> DateTimeUtils.yearStart().toString() to DateTimeUtils.yearEnd()
                    .toString()

                else -> {
                    start to end
                }
            }
            transactionRepository.getTransactionsByDateRange(start, end).collect {
                val daily = transactionRepository.getDailySpending(start, end)
                    .map {
                        try {
                            val model = Json {
                                ignoreUnknownKeys = true
                            }.decodeFromString<List<Category>>(it.categories)
                            DailySpending(it.dateTime.take(10), it.amount, list = model)
                        } catch (e: Exception) {
                            e.message
                            DailySpending(it.dateTime.take(10), it.amount, list = emptyList())
                        }
                    }
                val income = transactionRepository.getTotalIncomeForPeriod(start, end)
                val expense = transactionRepository.getTotalExpenseForPeriod(start, end)
                val cats = getCategorySummaryUseCase(start, end)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        dailySpending = daily,
                        totalIncome = income,
                        totalExpense = expense,
                        categoryBreakdown = cats
                    )
                }
            }
        }
    }


    fun updatePeriod(period: StatPeriod) {
        _uiState.update { it.copy(isLoading = true, selectedPeriod = period) }

    }
}

// -- Screen --

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(viewModel: StatisticsViewModel,onClick:(String)->Unit) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Statistics") })
        }) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(top = padding.calculateTopPadding()),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Period selector
            item {
                PeriodSelector(
                    selected = state.selectedPeriod, onSelect = {
                        if (it == StatPeriod.CUSTOM_RANGE) {
                            viewModel.isShow.value = true
                        } else viewModel.loadStats(it)
                    })
            }

            // Income vs Expense
            item {
                AppCard {
                    Text(
                        "Income vs Expense",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        StatSummaryItem("Income", state.totalIncome, GradientIncomeStart)
                        StatSummaryItem("Expense", state.totalExpense, GradientExpenseStart)
                        StatSummaryItem(
                            "Net", state.totalIncome - state.totalExpense, PrimaryPurple
                        )
                    }
                }
            }

            // Daily spending chart
            if (state.dailySpending.isNotEmpty()) {
                item {
                    AppCard {
                        Text(
                            "Daily Spending",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(16.dp))
                        BarChart(
                            data = state.dailySpending.map {
                                val color = it.list.map { parseHexColor(it.colorHex) }
                                ChartDataPoint(
                                    label = it.date,
                                    value = it.amount.toFloat(),
                                    color = color.getOrNull(0),
                                    //  colors = color
                                )
                            }, modifier = Modifier.fillMaxWidth().height(150.dp),
                            onClick = onClick
                        )
                    }
                }
            }

            // Category pie/donut
            if (state.categoryBreakdown.isNotEmpty()) {
                item {
                    AppCard {
                        Text(
                            "Category Breakdown",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(16.dp))
                        Row(
                            Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
                        ) {
                            DonutChart(
                                data = state.categoryBreakdown.mapIndexed { i, s ->
                                    ChartDataPoint(
                                        label = s.category.name,
                                        value = s.total.toFloat(),
                                        color = parseHexColor(s.category.colorHex)
                                    )
                                }, modifier = Modifier.size(140.dp), strokeWidth = 28.dp
                            )
                            Spacer(Modifier.width(16.dp))
                            ChartLegend(
                                items = state.categoryBreakdown.map {
                                    it.category.name to parseHexColor(it.category.colorHex)
                                }, modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                        state.categoryBreakdown.forEach { summary ->
                            CategoryStatRow(summary)
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }
            }
        }

        if (viewModel.isShow.value) {
            CustomDateRangePicker(viewModel.isShow.value, {}, onConfirm = { start, end ->
                viewModel.isShow.value = false
                viewModel.start = start
                viewModel.end = end
                viewModel.loadStats(StatPeriod.CUSTOM_RANGE)
                print("check datataa $start   $end")
            })
        }
    }
}

@Composable
private fun PeriodSelector(selected: StatPeriod, onSelect: (StatPeriod) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.horizontalScroll(
            rememberScrollState()
        )
    ) {
        StatPeriod.entries.forEach { period ->

            if (period == StatPeriod.CUSTOM_RANGE) {
                FilterChip(selected = selected == period, onClick = {
                    onSelect(period)
                }, label = {
                    Text("Custom")
                }, leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.DateRange, contentDescription = null
                    )
                })
            } else {

                FilterChip(
                    selected = selected == period,
                    onClick = { onSelect(period) },
                    label = { Text(period.label) })
            }

        }
    }
}

@Composable
private fun StatSummaryItem(
    label: String, amount: Double, color: androidx.compose.ui.graphics.Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            CurrencyFormatter.formatCompact(amount),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CategoryStatRow(summary: CategorySummary) {
    Column {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(summary.category.name, style = MaterialTheme.typography.bodyMedium)
            Text(
                "${summary.percentage.toInt()}% · ${CurrencyFormatter.format(summary.total)}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(Modifier.height(4.dp))

        val randomValue = Random.nextInt(45, 91)
        AnimatedLinearProgress(
            progress = randomValue / 100f,
            height = 6.dp,
            trackColor = parseHexColor(summary.category.colorHex).copy(alpha = 0.15f),
            showLabel = false
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDateRangePicker(
    show: Boolean, onDismiss: () -> Unit, onConfirm: (String, String) -> Unit
) {
    if (!show) return

    val state = rememberDateRangePickerState()

    DatePickerDialog(onDismissRequest = onDismiss, confirmButton = {
        TextButton(
            onClick = {
                val timeZone = TimeZone.currentSystemDefault()

                val startDateTime = state.selectedStartDateMillis?.let {
                    val date = Instant.fromEpochMilliseconds(it).toLocalDateTime(timeZone).date

                    LocalDateTime(
                        date, LocalTime(0, 0, 0)
                    )
                }

                val endDateTime = state.selectedEndDateMillis?.let {
                    val date = Instant.fromEpochMilliseconds(it).toLocalDateTime(timeZone).date

                    LocalDateTime(
                        date, LocalTime(23, 59, 59, 999_999_999)
                    )
                }

                if (startDateTime != null && endDateTime != null) {
                    onConfirm(startDateTime.toString(), endDateTime.toString())
                }

                onDismiss()
            }) {
            Text("Apply")
        }
    }, dismissButton = {
        TextButton(onClick = onDismiss) {
            Text("Cancel")
        }
    }) {
        DateRangePicker(state = state)
    }
}