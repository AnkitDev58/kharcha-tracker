package org.example.project.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collectLatest
import org.example.project.domain.model.UserSettings
import org.example.project.ui.components.AppCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is SettingsEffect.Saved -> snackbarHostState.showSnackbar("Saved")
                is SettingsEffect.Reset -> snackbarHostState.showSnackbar("Reset to defaults")
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }
        val s = state.settings
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { SectionHeader("General") }
            item {
                AppCard {
                    SettingsTextRow(
                        icon = Icons.Filled.CurrencyRupee,
                        label = "Currency Symbol",
                        value = s.currencySymbol,
                        onSave = { viewModel.updateCurrency(it) }
                    )
                    HorizontalDivider(Modifier.padding(vertical = 4.dp))
                    SwitchRow(
                        icon = Icons.Filled.DarkMode,
                        label = "Dark Theme",
                        checked = s.isDarkTheme,
                        onToggle = { viewModel.updateDarkTheme(it) }
                    )
                    HorizontalDivider(Modifier.padding(vertical = 4.dp))
                    SettingsNumberRow(
                        icon = Icons.Filled.CalendarMonth,
                        label = "Monthly Budget Ceiling (₹)",
                        value = s.monthlyBudgetCeiling,
                        onSave = { viewModel.updateMonthlyBudget(it) }
                    )
                }
            }

            item { SectionHeader("Emergency Fund & FIRE") }
            item {
                AppCard {
                    SettingsIntRow(
                        icon = Icons.Filled.Shield,
                        label = "Emergency Fund Months",
                        value = s.emergencyFundMonths,
                        onSave = { viewModel.updateEmergencyMonths(it) }
                    )
                    HorizontalDivider(Modifier.padding(vertical = 4.dp))
                    SettingsIntRow(
                        icon = Icons.Filled.LocalFireDepartment,
                        label = "FIRE Multiplier (× annual exp.)",
                        value = s.fireMultiplier,
                        onSave = { viewModel.updateFireMultiplier(it) }
                    )
                    HorizontalDivider(Modifier.padding(vertical = 4.dp))
                    AgeRow(
                        currentAge = s.currentAge,
                        retirementAge = s.retirementAge,
                        onSave = { cur, ret -> viewModel.updateAges(cur, ret) }
                    )
                }
            }

            item { SectionHeader("Expected Returns (%)") }
            item {
                ReturnsCard(s, onSave = { eq, d, g, fd ->
                    viewModel.updateReturns(eq, d, g, fd)
                })
            }

            item { SectionHeader("Asset Allocation (%)") }
            item {
                AllocationsCard(s, onSave = { eq, d, g, fd ->
                    viewModel.updateAllocations(eq, d, g, fd)
                })
            }

            item { SectionHeader("Growth Rates (%)") }
            item {
                AppCard {
                    SettingsNumberRow(
                        icon = Icons.Filled.TrendingUp,
                        label = "Inflation Rate",
                        value = s.inflationRate,
                        onSave = { viewModel.updateGrowthRates(it, s.salaryGrowthRate, s.taxRate) }
                    )
                    HorizontalDivider(Modifier.padding(vertical = 4.dp))
                    SettingsNumberRow(
                        icon = Icons.Filled.Work,
                        label = "Salary Growth Rate",
                        value = s.salaryGrowthRate,
                        onSave = { viewModel.updateGrowthRates(s.inflationRate, it, s.taxRate) }
                    )
                    HorizontalDivider(Modifier.padding(vertical = 4.dp))
                    SettingsNumberRow(
                        icon = Icons.Filled.AccountBalance,
                        label = "Tax Rate",
                        value = s.taxRate,
                        onSave = { viewModel.updateGrowthRates(s.inflationRate, s.salaryGrowthRate, it) }
                    )
                }
            }

            item { SectionHeader("Data") }
            item {
                AppCard {
                    OutlinedButton(
                        onClick = { viewModel.resetToDefaults() },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(Icons.Filled.RestartAlt, null, Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Reset All Settings to Defaults")
                    }
                }
            }

            item { Spacer(Modifier.height(32.dp)) }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
    )
}

@Composable
private fun SwitchRow(
    icon: ImageVector,
    label: String,
    checked: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            Text(label, style = MaterialTheme.typography.bodyMedium)
        }
        Switch(checked = checked, onCheckedChange = onToggle)
    }
}

@Composable
private fun SettingsTextRow(icon: ImageVector, label: String, value: String, onSave: (String) -> Unit) {
    var text by remember(value) { mutableStateOf(value) }
    var editing by remember { mutableStateOf(false) }
    Row(
        Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            if (editing) {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    label = { Text(label) }
                )
            } else {
                Column {
                    Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                }
            }
        }
        IconButton(onClick = {
            if (editing) { onSave(text) }
            editing = !editing
        }) {
            Icon(if (editing) Icons.Filled.Check else Icons.Filled.Edit, null, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun SettingsNumberRow(icon: ImageVector, label: String, value: Double, onSave: (Double) -> Unit) {
    var text by remember(value) { mutableStateOf(value.toString()) }
    var editing by remember { mutableStateOf(false) }
    Row(
        Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            if (editing) {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it.filter { c -> c.isDigit() || c == '.' } },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    label = { Text(label) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
            } else {
                Column {
                    Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                }
            }
        }
        IconButton(onClick = {
            if (editing) { text.toDoubleOrNull()?.let { onSave(it) } }
            editing = !editing
        }) {
            Icon(if (editing) Icons.Filled.Check else Icons.Filled.Edit, null, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun SettingsIntRow(icon: ImageVector, label: String, value: Int, onSave: (Int) -> Unit) {
    var text by remember(value) { mutableStateOf(value.toString()) }
    var editing by remember { mutableStateOf(false) }
    Row(
        Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            if (editing) {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it.filter { c -> c.isDigit() } },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    label = { Text(label) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            } else {
                Column {
                    Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                }
            }
        }
        IconButton(onClick = {
            if (editing) { text.toIntOrNull()?.let { onSave(it) } }
            editing = !editing
        }) {
            Icon(if (editing) Icons.Filled.Check else Icons.Filled.Edit, null, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun AgeRow(currentAge: Int, retirementAge: Int, onSave: (Int, Int) -> Unit) {
    var curText by remember(currentAge) { mutableStateOf(currentAge.toString()) }
    var retText by remember(retirementAge) { mutableStateOf(retirementAge.toString()) }
    var editing by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(Icons.Filled.Person, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                Text("Ages", style = MaterialTheme.typography.bodyMedium)
            }
            IconButton(onClick = {
                if (editing) {
                    val c = curText.toIntOrNull() ?: currentAge
                    val r = retText.toIntOrNull() ?: retirementAge
                    onSave(c, r)
                }
                editing = !editing
            }) {
                Icon(if (editing) Icons.Filled.Check else Icons.Filled.Edit, null, modifier = Modifier.size(18.dp))
            }
        }
        if (editing) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(curText, { curText = it.filter { c -> c.isDigit() } }, label = { Text("Current Age") },
                    modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(retText, { retText = it.filter { c -> c.isDigit() } }, label = { Text("Retirement Age") },
                    modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            }
        } else {
            Text("Current: $currentAge  |  Retirement: $retirementAge",
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun ReturnsCard(s: UserSettings, onSave: (Double, Double, Double, Double) -> Unit) {
    var eq by remember(s.expectedReturnEquity) { mutableStateOf(s.expectedReturnEquity.toString()) }
    var debt by remember(s.expectedReturnDebt) { mutableStateOf(s.expectedReturnDebt.toString()) }
    var gold by remember(s.expectedReturnGold) { mutableStateOf(s.expectedReturnGold.toString()) }
    var fd by remember(s.expectedReturnFD) { mutableStateOf(s.expectedReturnFD.toString()) }
    AppCard {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Returns", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Button(onClick = {
                onSave(
                    eq.toDoubleOrNull() ?: s.expectedReturnEquity,
                    debt.toDoubleOrNull() ?: s.expectedReturnDebt,
                    gold.toDoubleOrNull() ?: s.expectedReturnGold,
                    fd.toDoubleOrNull() ?: s.expectedReturnFD
                )
            }, modifier = Modifier.height(36.dp), shape = RoundedCornerShape(8.dp)) { Text("Save") }
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(eq, { eq = it.filter { c -> c.isDigit() || c == '.' } }, label = { Text("Equity %") },
                modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
            OutlinedTextField(debt, { debt = it.filter { c -> c.isDigit() || c == '.' } }, label = { Text("Debt %") },
                modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(gold, { gold = it.filter { c -> c.isDigit() || c == '.' } }, label = { Text("Gold %") },
                modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
            OutlinedTextField(fd, { fd = it.filter { c -> c.isDigit() || c == '.' } }, label = { Text("FD/RD %") },
                modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
        }
    }
}

@Composable
private fun AllocationsCard(s: UserSettings, onSave: (Double, Double, Double, Double) -> Unit) {
    var eq by remember(s.allocationEquity) { mutableStateOf(s.allocationEquity.toString()) }
    var debt by remember(s.allocationDebt) { mutableStateOf(s.allocationDebt.toString()) }
    var gold by remember(s.allocationGold) { mutableStateOf(s.allocationGold.toString()) }
    var fd by remember(s.allocationFD) { mutableStateOf(s.allocationFD.toString()) }
    val total = (eq.toDoubleOrNull() ?: 0.0) + (debt.toDoubleOrNull() ?: 0.0) +
            (gold.toDoubleOrNull() ?: 0.0) + (fd.toDoubleOrNull() ?: 0.0)
    AppCard {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("Allocation", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text("Total: ${total.toInt()}% ${if (total == 100.0) "✓" else "(should be 100%)"}",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (total == 100.0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
            }
            Button(onClick = {
                onSave(
                    eq.toDoubleOrNull() ?: s.allocationEquity,
                    debt.toDoubleOrNull() ?: s.allocationDebt,
                    gold.toDoubleOrNull() ?: s.allocationGold,
                    fd.toDoubleOrNull() ?: s.allocationFD
                )
            }, modifier = Modifier.height(36.dp), shape = RoundedCornerShape(8.dp)) { Text("Save") }
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(eq, { eq = it.filter { c -> c.isDigit() || c == '.' } }, label = { Text("Equity %") },
                modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
            OutlinedTextField(debt, { debt = it.filter { c -> c.isDigit() || c == '.' } }, label = { Text("Debt %") },
                modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(gold, { gold = it.filter { c -> c.isDigit() || c == '.' } }, label = { Text("Gold %") },
                modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
            OutlinedTextField(fd, { fd = it.filter { c -> c.isDigit() || c == '.' } }, label = { Text("FD/RD %") },
                modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
        }
    }
}
