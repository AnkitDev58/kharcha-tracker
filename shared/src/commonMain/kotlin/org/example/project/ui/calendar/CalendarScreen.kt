package org.example.project.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.LocalDate
import org.example.project.core.util.CurrencyFormatter
import org.example.project.core.util.DateTimeUtils
import org.example.project.ui.components.AppCard
import org.example.project.ui.theme.*

private val DAY_LABELS = listOf("M", "T", "W", "T", "F", "S", "S")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel,
    onDayClick: (String) -> Unit   // navigates to TransactionsRoute(date)
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "${DateTimeUtils.monthName(state.month)} ${state.year}",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.previousMonth() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Previous month")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.nextMonth() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, "Next month")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(Modifier.height(4.dp))

            // Day-of-week header row
            Row(Modifier.fillMaxWidth()) {
                DAY_LABELS.forEach { label ->
                    Text(
                        label,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Calendar grid
            if (!state.isLoading && state.month > 0) {
                CalendarGrid(
                    year = state.year,
                    month = state.month,
                    daySummaries = state.daySummaries,
                    selectedDay = state.selectedDay,
                    onDayClick = { day ->
                        viewModel.selectDay(day)
                        val dateStr = "${state.year}-${state.month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}"
                        onDayClick(dateStr)
                    }
                )
            }

            // Selected day detail
            state.selectedDay?.let { day ->
                state.daySummaries[day]?.let { summary ->
                    DayDetailCard(day = day, month = state.month, year = state.year, summary = summary)
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun CalendarGrid(
    year: Int,
    month: Int,
    daySummaries: Map<Int, DaySummary>,
    selectedDay: Int?,
    onDayClick: (Int) -> Unit
) {
    val firstDay = LocalDate(year, month, 1)
    // Monday=1 … Sunday=7; we want Monday as column 0
    val startOffset = ((firstDay.dayOfWeek.ordinal)) // Mon=0 … Sun=6 in kotlinx.datetime
    val daysInMonth = DateTimeUtils.monthEnd(month, year).day

    val cells = startOffset + daysInMonth

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        userScrollEnabled = false
    ) {
        // Empty leading cells
        items(startOffset) { Box(Modifier.aspectRatio(1f)) }

        // Day cells
        items(daysInMonth) { idx ->
            val day = idx + 1
            val summary = daySummaries[day]
            val isSelected = selectedDay == day
            DayCell(
                day = day,
                summary = summary,
                isSelected = isSelected,
                onClick = { onDayClick(day) }
            )
        }
    }
}

@Composable
private fun DayCell(
    day: Int,
    summary: DaySummary?,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val hasExpense = (summary?.expense ?: 0.0) > 0
    val hasIncome  = (summary?.income  ?: 0.0) > 0

    val bg = when {
        isSelected            -> MaterialTheme.colorScheme.primary
        summary?.hasTransactions == true -> MaterialTheme.colorScheme.primaryContainer
        else                  -> Color.Transparent
    }
    val textColor = when {
        isSelected -> MaterialTheme.colorScheme.onPrimary
        else       -> MaterialTheme.colorScheme.onSurface
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                day.toString(),
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = textColor
            )
            // Dot indicators: green = income, red = expense
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                if (hasIncome) {
                    Box(Modifier.size(4.dp).clip(CircleShape).background(IncomeGreen))
                }
                if (hasExpense) {
                    Box(Modifier.size(4.dp).clip(CircleShape).background(ExpenseRed))
                }
            }
        }
    }
}

@Composable
private fun DayDetailCard(day: Int, month: Int, year: Int, summary: DaySummary) {
    AppCard {
        Text(
            "$day ${DateTimeUtils.monthShortName(month)} $year",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SummaryPill(
                label = "Income",
                amount = summary.income,
                color = IncomeGreen,
                modifier = Modifier.weight(1f)
            )
            SummaryPill(
                label = "Expense",
                amount = summary.expense,
                color = ExpenseRed,
                modifier = Modifier.weight(1f)
            )
            SummaryPill(
                label = "Net",
                amount = summary.income - summary.expense,
                color = SavingsBlue,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SummaryPill(label: String, amount: Double, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.1f))
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = color)
        Spacer(Modifier.height(2.dp))
        Text(
            CurrencyFormatter.formatCompact(amount),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}
