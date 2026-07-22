package org.example.project.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
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
import androidx.compose.ui.unit.sp
import org.example.project.core.util.CurrencyFormatter
import org.example.project.domain.model.CategorySummary
import org.example.project.domain.model.FinancialSummary
import org.example.project.domain.model.InsightItem
import org.example.project.domain.model.InsightType
import org.example.project.domain.model.Transaction
import org.example.project.ui.components.*
import org.example.project.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onAddTransactionClick: () -> Unit,
    onTransactionClick: (Long) -> Unit,
    onNavigateToCalendar: () -> Unit = {},
    onNavigateToReports: () -> Unit = {},
    onNavigateToAnnualBills: () -> Unit = {},
    onNavigateToLoans: () -> Unit = {},
    onNavigateToInvestments: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    AdaptiveContent { sizeClass ->
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Kharcha Tracker", fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    actions = {
                        IconButton(onClick = onNavigateToSettings) {
                            Icon(Icons.Filled.Settings, "Settings")
                        }
                    }
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
                HomeScreenResponsiveContent(
                    state = state,
                    sizeClass = sizeClass,
                    padding = padding,
                    onTransactionClick = onTransactionClick,
                    onNavigateToCalendar = onNavigateToCalendar,
                    onNavigateToReports = onNavigateToReports,
                    onNavigateToAnnualBills = onNavigateToAnnualBills,
                    onNavigateToLoans = onNavigateToLoans,
                    onNavigateToInvestments = onNavigateToInvestments
                )
            }
        }
    }
}

@Composable
private fun HomeScreenResponsiveContent(
    state: HomeUiState,
    sizeClass: WindowSizeClass,
    padding: PaddingValues,
    onTransactionClick: (Long) -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToReports: () -> Unit,
    onNavigateToAnnualBills: () -> Unit,
    onNavigateToLoans: () -> Unit,
    onNavigateToInvestments: () -> Unit
) {
    if (sizeClass == WindowSizeClass.COMPACT) {
        HomeScreenCompact(
            state = state,
            padding = padding,
            onTransactionClick = onTransactionClick,
            onNavigateToCalendar = onNavigateToCalendar,
            onNavigateToReports = onNavigateToReports,
            onNavigateToAnnualBills = onNavigateToAnnualBills,
            onNavigateToLoans = onNavigateToLoans,
            onNavigateToInvestments = onNavigateToInvestments
        )
    } else {
        HomeScreenExpanded(
            state = state,
            padding = padding,
            onTransactionClick = onTransactionClick,
            onNavigateToCalendar = onNavigateToCalendar,
            onNavigateToReports = onNavigateToReports,
            onNavigateToAnnualBills = onNavigateToAnnualBills,
            onNavigateToLoans = onNavigateToLoans,
            onNavigateToInvestments = onNavigateToInvestments
        )
    }
}

@Composable
private fun HomeScreenCompact(
    state: HomeUiState,
    padding: PaddingValues,
    onTransactionClick: (Long) -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToReports: () -> Unit,
    onNavigateToAnnualBills: () -> Unit,
    onNavigateToLoans: () -> Unit,
    onNavigateToInvestments: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(top = padding.calculateTopPadding()),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { BalanceCard(state.summary.balance) }
        item { MiniCardsRow(state.summary) }
        item {
            QuickAccessGrid(
                onCalendar = onNavigateToCalendar,
                onReports = onNavigateToReports,
                onAnnualBills = onNavigateToAnnualBills,
                onLoans = onNavigateToLoans,
                onInvestments = onNavigateToInvestments
            )
        }
        if (state.summary.monthlyBudget > 0) {
            item { BudgetCard(state.summary) }
        }
        if (state.insights.isNotEmpty()) {
            item { InsightsCard(state.insights) }
        }
        if (state.categorySummaries.isNotEmpty()) {
            item { CategoryBreakdownCard(state.categorySummaries.take(5)) }
        }
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
        item { Spacer(Modifier.height(80.dp)) }
    }
}

@Composable
private fun HomeScreenExpanded(
    state: HomeUiState,
    padding: PaddingValues,
    onTransactionClick: (Long) -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToReports: () -> Unit,
    onNavigateToAnnualBills: () -> Unit,
    onNavigateToLoans: () -> Unit,
    onNavigateToInvestments: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = padding.calculateTopPadding())
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Left Column: Overview and Budget
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item { BalanceCard(state.summary.balance) }
            item { MiniCardsRow(state.summary) }
            if (state.summary.monthlyBudget > 0) {
                item { BudgetCard(state.summary) }
            }
            if (state.insights.isNotEmpty()) {
                item { InsightsCard(state.insights) }
            }
        }

        // Right Column: Tools and Details
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item {
                QuickAccessGrid(
                    onCalendar = onNavigateToCalendar,
                    onReports = onNavigateToReports,
                    onAnnualBills = onNavigateToAnnualBills,
                    onLoans = onNavigateToLoans,
                    onInvestments = onNavigateToInvestments
                )
            }
            if (state.categorySummaries.isNotEmpty()) {
                item { CategoryBreakdownCard(state.categorySummaries.take(5)) }
            }
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
        }
    }
}

@Composable
private fun MiniCardsRow(summary: FinancialSummary) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SummaryMiniCard(
            label = "Income",
            amount = CurrencyFormatter.formatCompact(summary.totalIncome),
            icon = Icons.AutoMirrored.Filled.TrendingUp,
            gradientStart = GradientIncomeStart,
            gradientEnd = GradientIncomeEnd,
            modifier = Modifier.weight(1f)
        )
        SummaryMiniCard(
            label = "Expense",
            amount = CurrencyFormatter.formatCompact(summary.totalExpense),
            icon = Icons.AutoMirrored.Filled.TrendingDown,
            gradientStart = GradientExpenseStart,
            gradientEnd = GradientExpenseEnd,
            modifier = Modifier.weight(1f)
        )
        SummaryMiniCard(
            label = "Savings",
            amount = CurrencyFormatter.formatCompact(summary.totalSavings),
            icon = Icons.Filled.Savings,
            gradientStart = GradientSavingsStart,
            gradientEnd = GradientSavingsEnd,
            modifier = Modifier.weight(1f)
        )
    }
}

// ── Balance card ───────────────────────────────────────────────────────────────

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

// ── Quick-access feature grid ─────────────────────────────────────────────────

@Composable
private fun QuickAccessGrid(
    onCalendar: () -> Unit,
    onReports: () -> Unit,
    onAnnualBills: () -> Unit,
    onLoans: () -> Unit,
    onInvestments: () -> Unit
) {
    AppCard {
        Text(
            "Tools",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            QuickAccessItem(
                icon = Icons.Filled.CalendarMonth,
                label = "Calendar",
                tint = Color(0xFF6C63FF),
                onClick = onCalendar
            )
            QuickAccessItem(
                icon = Icons.Filled.BarChart,
                label = "Reports",
                tint = Color(0xFF00C896),
                onClick = onReports
            )
            QuickAccessItem(
                icon = Icons.AutoMirrored.Filled.EventNote,
                label = "Bills",
                tint = Color(0xFF7030A0),
                onClick = onAnnualBills
            )
            QuickAccessItem(
                icon = Icons.Filled.CreditCard,
                label = "Loans",
                tint = Color(0xFFFF6B6B),
                onClick = onLoans
            )
            QuickAccessItem(
                icon = Icons.AutoMirrored.Filled.TrendingUp,
                label = "Invest",
                tint = Color(0xFF00B894),
                onClick = onInvestments
            )
        }
    }
}

@Composable
private fun QuickAccessItem(
    icon: ImageVector,
    label: String,
    tint: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(tint.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, label, tint = tint, modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.height(4.dp))
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ── Budget card ────────────────────────────────────────────────────────────────

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
                BudgetRow("Budget",    CurrencyFormatter.format(summary.monthlyBudget))
                BudgetRow("Spent",     CurrencyFormatter.format(summary.monthlySpent))
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
        Text(label, style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold, color = color)
    }
    Spacer(Modifier.height(6.dp))
}

// ── Insights card ──────────────────────────────────────────────────────────────

@Composable
private fun InsightsCard(insights: List<InsightItem>) {
    AppCard {
        Text("Insights", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        insights.forEach { insight ->
            val dotColor = when (insight.type) {
                InsightType.POSITIVE -> IncomeGreen
                InsightType.NEGATIVE -> ExpenseRed
                InsightType.WARNING  -> ProgressOrange
                InsightType.NEUTRAL  -> PrimaryPurple
            }
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 3.dp)
            ) {
                Box(
                    Modifier
                        .size(7.dp)
                        .clip(androidx.compose.foundation.shape.CircleShape)
                        .background(dotColor)
                        .padding(top = 5.dp)
                )
                Text(
                    insight.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ── Category breakdown card ────────────────────────────────────────────────────

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
