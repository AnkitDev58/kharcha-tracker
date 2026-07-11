package org.example.project.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.core.util.CurrencyFormatter
import org.example.project.domain.model.CategorySummary
import org.example.project.domain.model.FinancialSummary
import org.example.project.domain.model.InsightItem
import org.example.project.ui.components.*
import org.example.project.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onAddTransactionClick: () -> Unit,
    onTransactionClick: (Long) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kharcha Tracker", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTransactionClick,
                containerColor = PrimaryPurple
            ) {
                Icon(Icons.Filled.Add, "Add Transaction", tint = Color.White)
            }
        }
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(top = padding.calculateTopPadding()),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Balance Card
                item {
                    BalanceCard(state.summary.balance)
                }

                // Summary Cards
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SummaryMiniCard(
                            label = "Income",
                            amount = CurrencyFormatter.formatCompact(state.summary.totalIncome),
                            icon = Icons.Filled.TrendingUp,
                            gradientStart = GradientIncomeStart,
                            gradientEnd = GradientIncomeEnd,
                            modifier = Modifier.weight(1f)
                        )
                        SummaryMiniCard(
                            label = "Expense",
                            amount = CurrencyFormatter.formatCompact(state.summary.totalExpense),
                            icon = Icons.Filled.TrendingDown,
                            gradientStart = GradientExpenseStart,
                            gradientEnd = GradientExpenseEnd,
                            modifier = Modifier.weight(1f)
                        )
                        SummaryMiniCard(
                            label = "Savings",
                            amount = CurrencyFormatter.formatCompact(state.summary.totalSavings),
                            icon = Icons.Filled.Savings,
                            gradientStart = GradientSavingsStart,
                            gradientEnd = GradientSavingsEnd,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Budget Progress
                if (state.summary.monthlyBudget > 0) {
                    item {
                        BudgetCard(state.summary)
                    }
                }

                // Insights
                if (state.insights.isNotEmpty()) {
                    item {
                        InsightsCard(state.insights)
                    }
                }

                // Category Breakdown
                if (state.categorySummaries.isNotEmpty()) {
                    item {
                        CategoryBreakdownCard(state.categorySummaries.take(5))
                    }
                }

                // Recent Transactions
                item {
                    Text(
                        "Recent Transactions",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(state.recentTransactions) { tx ->
                    TransactionItem(
                        transaction = tx,
                        onClick = { onTransactionClick(tx.id) }
                    )
                }

                // Spacing for FAB
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
private fun BalanceCard(balance: Double) {
    GradientCard(
        modifier = Modifier.fillMaxWidth().height(140.dp),
        gradientColors = listOf(GradientPurpleStart, GradientPurpleEnd),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Current Balance",
                style = MaterialTheme.typography.labelLarge,
                color = Color.White.copy(alpha = 0.85f)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                CurrencyFormatter.format(balance),
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 40.sp
                ),
                color = Color.White
            )
        }
    }
}

@Composable
private fun BudgetCard(summary: FinancialSummary) {
    AppCard {
        Text(
            "Monthly Budget",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            AnimatedCircularProgress(
                progress = summary.budgetProgress,
                size = 100.dp,
                strokeWidth = 12.dp,
                label = "Used"
            )
            Spacer(Modifier.width(20.dp))
            Column(modifier = Modifier.weight(1f)) {
                BudgetRow("Budget", CurrencyFormatter.format(summary.monthlyBudget))
                BudgetRow("Spent", CurrencyFormatter.format(summary.monthlySpent))
                BudgetRow(
                    "Remaining",
                    CurrencyFormatter.format(summary.budgetRemaining),
                    TrackerTheme.extendedColors.income
                )
            }
        }
    }
}

@Composable
private fun BudgetRow(
    label: String,
    value: String,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }
    Spacer(Modifier.height(6.dp))
}

@Composable
private fun InsightsCard(insights: List<InsightItem>) {
    AppCard {
        Text("Insights", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        insights.forEach { insight ->
            Text(
                "• ${insight.message}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(6.dp))
        }
    }
}

@Composable
private fun CategoryBreakdownCard(summaries: List<CategorySummary>) {
    AppCard {
        Text(
            "Top Categories",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(12.dp))
        summaries.forEach { sum ->
            CategorySummaryItem(sum)
            Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
private fun CategorySummaryItem(summary: CategorySummary) {
    Column {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                summary.category.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                CurrencyFormatter.format(summary.total),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(Modifier.height(4.dp))
        AnimatedLinearProgress(
            progress = summary.percentage / 100f,
            height = 6.dp,
            trackColor = parseHexColor(summary.category.colorHex),
            showLabel = false
        )
    }
}
