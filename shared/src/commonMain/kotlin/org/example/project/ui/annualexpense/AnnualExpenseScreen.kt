package org.example.project.ui.annualexpense

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collectLatest
import org.example.project.core.util.CurrencyFormatter
import org.example.project.domain.model.AnnualExpense
import org.example.project.domain.model.ExpenseFrequency
import org.example.project.ui.components.AppCard
import org.example.project.ui.components.AnimatedLinearProgress
import org.example.project.ui.theme.PrimaryPurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnualExpenseScreen(
    viewModel: AnnualExpenseViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val snackbar = remember { SnackbarHostState() }
    var showSheet by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<AnnualExpense?>(null) }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is AnnualExpenseEffect.Saved   -> { snackbar.showSnackbar("Saved"); showSheet = false }
                is AnnualExpenseEffect.Deleted -> snackbar.showSnackbar("Deleted")
                is AnnualExpenseEffect.Error   -> snackbar.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Annual & One-Time Bills", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { editing = null; showSheet = true },
                containerColor = PrimaryPurple
            ) { Icon(Icons.Filled.Add, "Add Bill", tint = Color.White) }
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(top = padding.calculateTopPadding()),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Summary card
            item {
                AppCard {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Monthly Reserve Needed", style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                CurrencyFormatter.format(state.totalMonthlyReserve),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Icon(Icons.Filled.Savings, null, tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp))
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Set aside this amount monthly to cover all annual/periodic bills.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (state.expenses.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Filled.EventNote, null, tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(48.dp))
                            Spacer(Modifier.height(8.dp))
                            Text("No bills added yet", style = MaterialTheme.typography.titleMedium)
                            Text("Tap + to add insurance, renewals, etc.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            } else {
                items(state.expenses, key = { it.id }) { expense ->
                    BillItem(
                        expense = expense,
                        onEdit = { editing = expense; showSheet = true },
                        onDelete = { viewModel.delete(expense.id) }
                    )
                }
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }

    if (showSheet) {
        AddEditBillSheet(
            initial = editing,
            onDismiss = { showSheet = false },
            onSave = { id, name, cat, amt, freq, month, notes ->
                viewModel.save(id, name, cat, amt, freq, month, notes)
            }
        )
    }
}

@Composable
private fun BillItem(
    expense: AnnualExpense,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    AppCard(onClick = onEdit) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(44.dp).clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.EventNote, null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(expense.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    Text(CurrencyFormatter.format(expense.amount),
                        style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(2.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FrequencyChip(expense.frequency.label)
                    Text("Due: ${expense.nextDueMonth}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(Modifier.height(6.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Monthly reserve",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(CurrencyFormatter.format(expense.monthlyReserve),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary)
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, "Delete", tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
private fun FrequencyChip(label: String) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Text(label,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddEditBillSheet(
    initial: AnnualExpense?,
    onDismiss: () -> Unit,
    onSave: (Long, String, String, Double, ExpenseFrequency, Int, String) -> Unit
) {
    var name     by remember(initial) { mutableStateOf(initial?.name ?: "") }
    var category by remember(initial) { mutableStateOf(initial?.category ?: "") }
    var amount   by remember(initial) { mutableStateOf(initial?.amount?.toString() ?: "") }
    var frequency by remember(initial) { mutableStateOf(initial?.frequency ?: ExpenseFrequency.YEARLY) }
    var dueMonth by remember(initial) { mutableStateOf(initial?.dueMonth ?: 1) }
    var notes    by remember(initial) { mutableStateOf(initial?.notes ?: "") }
    var freqExpanded by remember { mutableStateOf(false) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            Modifier.padding(16.dp).navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                if (initial == null) "Add Bill / Annual Expense" else "Edit Bill",
                style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold
            )

            OutlinedTextField(name, { name = it }, label = { Text("Bill Name") },
                modifier = Modifier.fillMaxWidth(), singleLine = true)

            OutlinedTextField(category, { category = it }, label = { Text("Category (e.g. Insurance)") },
                modifier = Modifier.fillMaxWidth(), singleLine = true)

            OutlinedTextField(
                amount, { amount = it.filter { c -> c.isDigit() || c == '.' } },
                label = { Text("Amount (₹)") },
                modifier = Modifier.fillMaxWidth(), singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            // Frequency dropdown
            ExposedDropdownMenuBox(expanded = freqExpanded, onExpandedChange = { freqExpanded = it }) {
                OutlinedTextField(
                    value = frequency.label, onValueChange = {}, readOnly = true,
                    label = { Text("Frequency") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(freqExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = freqExpanded, onDismissRequest = { freqExpanded = false }) {
                    ExpenseFrequency.entries.forEach { f ->
                        DropdownMenuItem(
                            text = { Text(f.label) },
                            onClick = { frequency = f; freqExpanded = false }
                        )
                    }
                }
            }

            // Due month slider
            Column {
                Text("Due Month: ${monthShortName(dueMonth)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Slider(
                    value = dueMonth.toFloat(),
                    onValueChange = { dueMonth = it.toInt() },
                    valueRange = 1f..12f,
                    steps = 10
                )
            }

            OutlinedTextField(notes, { notes = it }, label = { Text("Notes (optional)") },
                modifier = Modifier.fillMaxWidth(), singleLine = true)

            Button(
                onClick = {
                    val amt = amount.toDoubleOrNull() ?: return@Button
                    if (name.isBlank()) return@Button
                    onSave(initial?.id ?: 0L, name.trim(), category.trim(), amt, frequency, dueMonth, notes.trim())
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) { Text("Save Bill") }

            Spacer(Modifier.height(8.dp))
        }
    }
}

private fun monthShortName(m: Int) = when (m) {
    1 -> "Jan"; 2 -> "Feb"; 3 -> "Mar"; 4 -> "Apr"; 5 -> "May"; 6 -> "Jun"
    7 -> "Jul"; 8 -> "Aug"; 9 -> "Sep"; 10 -> "Oct"; 11 -> "Nov"; 12 -> "Dec"
    else -> "—"
}
