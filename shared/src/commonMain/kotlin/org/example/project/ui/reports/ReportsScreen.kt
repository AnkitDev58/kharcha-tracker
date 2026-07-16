package org.example.project.ui.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.project.core.util.CurrencyFormatter
import org.example.project.core.util.DateTimeUtils
import org.example.project.domain.model.MonthlyReport
import org.example.project.ui.components.*
import org.example.project.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    viewModel: ReportsViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Monthly Report", fontWeight = FontWeight.Bold)
                        Text(
                            "${DateTimeUtils.monthName(state.month)} ${state.year}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.previousMonth() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Prev month")
                    }
                    IconButton(onClick = { viewModel.nextMonth() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, "Next month")
                    }
                }
            )
        }
    ) { padding ->
        when {
            state.isLoading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            state.report == null -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No data for this month", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            else -> ReportContent(
                report = state.report!!,
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@Composable
private fun ReportContent(report: MonthlyReport, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Cash flow summary banner
        item {
            GradientCard(
                modifier = Modifier.fillMaxWidth(),
                gradientColors = listOf(GradientPurpleStart, GradientPurpleEnd),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text(
                        "Cash Flow",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White.copy(0.85f)
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                        CashFlowPill("Income", report.totalIncome, IncomeGreen)
                        CashFlowPill("Expense", report.totalExpense, ExpenseRed)
                        CashFlowPill("Savings", report.totalSavings, SavingsBlue)
                    }
                }
            }
        }

        // Key metrics grid
        item {
            AppCard {
                Text("Key Metrics", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                MetricRow(Icons.Filled.Receipt, "Total Transactions", report.totalTransactions.toString())
                MetricRow(Icons.Filled.TrendingDown, "Avg Daily Spend", CurrencyFormatter.format(report.avgDailySpend))
                MetricRow(Icons.Filled.ArrowUpward, "Highest Single Expense", CurrencyFormatter.format(report.highestSingleExpense))
                MetricRow(Icons.Filled.Category, "Top Spend Category", report.highestExpenseCategory)
                MetricRow(Icons.Filled.Savings, "Savings Rate", "${(report.savingsRate * 100).toInt()}%")
                MetricRow(Icons.Filled.CreditCard, "Total Liabilities", CurrencyFormatter.format(report.totalLiabilities))
            }
        }

        // Budget utilisation
        if (report.monthlyBudget > 0) {
            item {
                AppCard {
                    Text("Budget Utilisation", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AnimatedCircularProgress(
                            progress = report.budgetUtilization,
                            size = 96.dp,
                            strokeWidth = 12.dp,
                            label = "Used"
                        )
                        Spacer(Modifier.width(16.dp))
                        Column(Modifier.weight(1f)) {
                            BudgetLine("Budget", CurrencyFormatter.format(report.monthlyBudget))
                            BudgetLine("Spent", CurrencyFormatter.format(report.totalExpense))
                            BudgetLine(
                                "Remaining",
                                CurrencyFormatter.format((report.monthlyBudget - report.totalExpense).coerceAtLeast(0.0)),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }

        // Category breakdown
        if (report.categoryBreakdown.isNotEmpty()) {
            item {
                AppCard {
                    Text("Expense by Category", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))

                    val grandTotal = report.categoryBreakdown.sumOf { it.total }.takeIf { it > 0 } ?: 1.0
                    report.categoryBreakdown.take(8).forEach { cat ->
                        val pct = (cat.total / grandTotal).toFloat()
                        Row(
                            Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                Modifier.size(10.dp).clip(CircleShape)
                                    .background(parseHexColor(cat.colorHex))
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                cat.categoryName,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                CurrencyFormatter.format(cat.total),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "${(pct * 100).toInt()}%",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.width(36.dp)
                            )
                        }
                        AnimatedLinearProgress(
                            progress = pct,
                            height = 5.dp,
                            trackColor = parseHexColor(cat.colorHex),
                            showLabel = false
                        )
                        Spacer(Modifier.height(4.dp))
                    }
                }
            }
        }

        item { Spacer(Modifier.height(32.dp)) }
    }
}

@Composable
private fun CashFlowPill(label: String, amount: Double, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(0.75f))
        Spacer(Modifier.height(4.dp))
        Text(
            CurrencyFormatter.formatCompact(amount),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
private fun MetricRow(icon: ImageVector, label: String, value: String) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(0.1f))
}

@Composable
private fun BudgetLine(label: String, value: String, color: Color = MaterialTheme.colorScheme.onSurface) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = color)
    }
    Spacer(Modifier.height(4.dp))
}
