package org.example.project.ui.budget

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.example.project.core.util.ClockSystem
import org.example.project.core.util.CurrencyFormatter
import org.example.project.core.util.DateTimeUtils
import org.example.project.domain.model.Budget
import org.example.project.domain.model.Category
import org.example.project.ui.components.AnimatedCircularProgress
import org.example.project.ui.components.AnimatedLinearProgress
import org.example.project.ui.components.AppCard
import org.example.project.ui.components.getProgressColor
import org.example.project.ui.components.parseHexColor
import org.example.project.ui.components.toIcon
import org.example.project.ui.theme.PrimaryPurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(viewModel: BudgetViewModel) {
    val state by viewModel.uiState.collectAsState()
    var showAddSheet by remember { mutableStateOf(false) }
    val today = ClockSystem.todayIn(TimeZone.currentSystemDefault())

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Budget")
                        Text(
                            "${DateTimeUtils.monthName(today.monthNumber)} ${today.year}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddSheet = true },
                containerColor = PrimaryPurple
            ) {
                Icon(Icons.Filled.Add, "Add Budget", tint = Color.White)
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Overall progress
            item {
                OverallBudgetCard(
                    totalBudget = state.totalBudget,
                    totalSpent = state.totalSpent
                )
            }

            // Per-category budgets
            if (state.budgets.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("No budgets set", style = MaterialTheme.typography.titleMedium)
                            Text(
                                "Tap + to create a budget",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(state.budgets, key = { it.id }) { budget ->
                    BudgetItem(budget = budget)
                }
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }

    if (showAddSheet) {
        AddBudgetSheet(
            categories = state.expenseCategories,
            onDismiss = { showAddSheet = false },
            onSave = { categoryId, amount ->
                viewModel.saveBudget(categoryId, amount, today.monthNumber, today.year)
                showAddSheet = false
            }
        )
    }
}

@Composable
private fun OverallBudgetCard(totalBudget: Double, totalSpent: Double) {
    val progress =
        if (totalBudget > 0) (totalSpent / totalBudget).toFloat().coerceIn(0f, 1f) else 0f
    AppCard {
        Text(
            "Overall Budget",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            AnimatedCircularProgress(
                progress = progress,
                size = 110.dp,
                strokeWidth = 14.dp,
                label = "Spent"
            )
            Spacer(Modifier.width(20.dp))
            Column(Modifier.weight(1f)) {
                BudgetInfoRow("Total Budget", CurrencyFormatter.format(totalBudget))
                BudgetInfoRow("Spent", CurrencyFormatter.format(totalSpent))
                BudgetInfoRow(
                    "Remaining",
                    CurrencyFormatter.format((totalBudget - totalSpent).coerceAtLeast(0.0))
                )
            }
        }
    }
}

@Composable
private fun BudgetInfoRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
    }
    Spacer(Modifier.height(4.dp))
}

@Composable
private fun BudgetItem(budget: Budget) {
    val cat = budget.category
    val catColor =
        if (cat != null) parseHexColor(cat.colorHex) else MaterialTheme.colorScheme.primary

    AppCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(catColor.copy(0.15f)),
                contentAlignment = Alignment.Center
            ) {
                cat?.let {
                    Icon(
                        it.icon.toIcon(),
                        it.name,
                        tint = catColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        cat?.name ?: "Category",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "${CurrencyFormatter.format(budget.spent)} / ${
                            CurrencyFormatter.format(
                                budget.amount
                            )
                        }",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(Modifier.height(8.dp))
                AnimatedLinearProgress(
                    progress = budget.progress,
                    height = 8.dp,
                    trackColor = parseHexColor(budget.category?.colorHex?:""),
                    showLabel = false
                )
                Spacer(Modifier.height(4.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    val remainColor =
                        if (budget.isOverBudget) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    Text(
                        if (budget.isOverBudget) "Over budget!" else "Remaining: ${
                            CurrencyFormatter.format(
                                budget.remaining
                            )
                        }",
                        style = MaterialTheme.typography.labelSmall,
                        color = remainColor
                    )
                    Text(
                        "${(budget.progress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = getProgressColor(budget.progress)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddBudgetSheet(
    categories: List<Category>,
    onDismiss: () -> Unit,
    onSave: (Long, Double) -> Unit
) {
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var amountText by remember { mutableStateOf("") }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Set Budget",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            // Category dropdown
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                OutlinedTextField(
                    value = selectedCategory?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat.name) },
                            onClick = { selectedCategory = cat; expanded = false }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it.filter { c -> c.isDigit() || c == '.' } },
                label = { Text("Budget Amount (₹)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Button(
                onClick = {
                    val cat = selectedCategory
                    val amt = amountText.toDoubleOrNull()
                    if (cat != null && amt != null && amt > 0) {
                        onSave(cat.id, amt)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save Budget")
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
