package org.example.project.ui.investment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
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
import org.example.project.core.util.CurrencyFormatter.format
import org.example.project.domain.model.Investment
import org.example.project.domain.model.InvestmentType
import org.example.project.ui.components.*
import org.example.project.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvestmentScreen(
    viewModel: InvestmentViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val snackbar = remember { SnackbarHostState() }
    var showSheet by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<Investment?>(null) }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is InvestmentEffect.Saved -> {
                    snackbar.showSnackbar("Saved"); showSheet = false
                }

                is InvestmentEffect.Deleted -> snackbar.showSnackbar("Investment deleted")
                is InvestmentEffect.Error -> snackbar.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Investment Portfolio", fontWeight = FontWeight.Bold) },
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
            ) { Icon(Icons.Filled.Add, "Add Investment", tint = Color.White) }
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(top = padding.calculateTopPadding()),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Portfolio summary banner
            item {
                GradientCard(
                    modifier = Modifier.fillMaxWidth(),
                    gradientColors = listOf(Color(0xFF375623), Color(0xFF9C6500)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Text(
                            "Portfolio Value", style = MaterialTheme.typography.labelLarge,
                            color = Color.White.copy(0.85f)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            CurrencyFormatter.format(state.totalCurrentValue),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold, color = Color.White
                        )
                        Spacer(Modifier.height(10.dp))
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            PortfolioPill("Invested", state.totalInvested)
                            PortfolioPill(
                                if (state.isProfit) "Gain" else "Loss",
                                state.totalGain,
                                if (state.isProfit) IncomeGreen else ExpenseRed
                            )
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    "Return", style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(0.75f)
                                )
                                Text(
                                    "${state.totalGainPct.format()}%",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (state.isProfit) IncomeGreen else ExpenseRed
                                )
                            }
                        }
                    }
                }
            }

            // Asset allocation donut
            if (state.buckets.isNotEmpty()) {
                item {
                    AppCard {
                        Text(
                            "Asset Allocation", style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(12.dp))
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val donutData = state.buckets.mapIndexed { i, b ->
                                ChartDataPoint(
                                    label = b.bucket.label,
                                    value = b.currentValue.toFloat(),
                                    color = bucketColor(i)
                                )
                            }
                            DonutChart(
                                data = donutData,
                                size = 120.dp,
                                strokeWidth = 28.dp,
                                modifier = Modifier.size(120.dp)
                            )
                            Spacer(Modifier.width(16.dp))
                            Column(
                                Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                state.buckets.forEachIndexed { i, b ->
                                    Row(
                                        Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Box(
                                                Modifier.size(8.dp).clip(CircleShape)
                                                    .background(bucketColor(i))
                                            )
                                            Text(
                                                b.bucket.label,
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                        }
                                        Text(
                                            "${b.percentage.toInt()}%",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Holdings list
            if (state.investments.isEmpty()) {
                item {
                    Box(
                        Modifier.fillMaxWidth().height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.AutoMirrored.Filled.TrendingUp, null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text("No investments yet", style = MaterialTheme.typography.titleMedium)
                            Text(
                                "Tap + to add mutual funds, stocks, FDs, etc.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                item {
                    Text(
                        "Holdings (${state.investments.size})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                items(state.investments, key = { it.id }) { inv ->
                    InvestmentItem(
                        investment = inv,
                        daysHeld = viewModel.daysHeld(inv.investDate),
                        onEdit = { editing = inv; showSheet = true },
                        onDelete = { viewModel.delete(inv.id) }
                    )
                }
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }

    if (showSheet) {
        AddEditInvestmentSheet(
            initial = editing,
            onDismiss = { showSheet = false },
            onSave = { id, name, type, inv, cur, date, mat, sip, sipAmt, notes ->
                viewModel.save(id, name, type, inv, cur, date, mat, sip, sipAmt, notes)
            }
        )
    }
}

@Composable
private fun PortfolioPill(label: String, amount: Double, color: Color = Color.White) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(0.75f))
        Text(
            CurrencyFormatter.formatCompact(amount),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold, color = color
        )
    }
}

@Composable
private fun InvestmentItem(
    investment: Investment,
    daysHeld: Int,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val gainColor = if (investment.isProfit) IncomeGreen else ExpenseRed
    AppCard(onClick = onEdit) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(44.dp).clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.TrendingUp, null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        investment.name, style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        CurrencyFormatter.format(investment.currentValue),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.height(2.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    InvChip(investment.investmentType.label)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            "${if (investment.isProfit) "+" else ""}${investment.gainPercent.format()}%",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = gainColor
                        )
                        Text(
                            "CAGR: ${investment.cagr(daysHeld).format()}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        "Invested: ${CurrencyFormatter.format(investment.investedAmount)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "${if (investment.isProfit) "+" else ""}${
                            CurrencyFormatter.format(
                                investment.gain
                            )
                        }",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = gainColor
                    )
                }
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Filled.Delete, "Delete",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun InvChip(label: String) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

private fun bucketColor(index: Int): Color = when (index % 4) {
    0 -> Color(0xFF6C63FF)
    1 -> Color(0xFF00C896)
    2 -> Color(0xFFFFBE76)
    else -> Color(0xFFFF6B6B)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddEditInvestmentSheet(
    initial: Investment?,
    onDismiss: () -> Unit,
    onSave: (Long, String, InvestmentType, Double, Double, String, String?, Boolean, Double, String) -> Unit
) {
    var name by remember(initial) { mutableStateOf(initial?.name ?: "") }
    var invType by remember(initial) {
        mutableStateOf(
            initial?.investmentType ?: InvestmentType.MUTUAL_FUND
        )
    }
    var invested by remember(initial) { mutableStateOf(initial?.investedAmount?.toString() ?: "") }
    var current by remember(initial) { mutableStateOf(initial?.currentValue?.toString() ?: "") }
    var investDate by remember(initial) { mutableStateOf(initial?.investDate ?: "") }
    var maturity by remember(initial) { mutableStateOf(initial?.maturityDate ?: "") }
    var isSIP by remember(initial) { mutableStateOf(initial?.isSIP ?: false) }
    var sipAmount by remember(initial) {
        mutableStateOf(
            initial?.sipMonthlyAmount?.toString() ?: ""
        )
    }
    var notes by remember(initial) { mutableStateOf(initial?.notes ?: "") }
    var typeExpanded by remember { mutableStateOf(false) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            Modifier.padding(16.dp).navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                if (initial == null) "Add Investment" else "Edit Investment",
                style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                name, { name = it }, label = { Text("Investment Name") },
                modifier = Modifier.fillMaxWidth(), singleLine = true
            )

            ExposedDropdownMenuBox(
                expanded = typeExpanded,
                onExpandedChange = { typeExpanded = it }) {
                OutlinedTextField(
                    value = invType.label, onValueChange = {}, readOnly = true,
                    label = { Text("Investment Type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(typeExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = typeExpanded,
                    onDismissRequest = { typeExpanded = false }) {
                    InvestmentType.entries.forEach { t ->
                        DropdownMenuItem(
                            text = { Text(t.label) },
                            onClick = { invType = t; typeExpanded = false })
                    }
                }
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    invested,
                    { invested = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Invested (₹)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                OutlinedTextField(
                    current,
                    { current = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Current Value (₹)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    investDate, { investDate = it },
                    label = { Text("Invest Date (YYYY-MM-DD)") }, modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    maturity, { maturity = it },
                    label = { Text("Maturity Date (optional)") }, modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            Row(
                Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("SIP Investment", style = MaterialTheme.typography.bodyMedium)
                Switch(checked = isSIP, onCheckedChange = { isSIP = it })
            }
            if (isSIP) {
                OutlinedTextField(
                    sipAmount,
                    { sipAmount = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Monthly SIP Amount (₹)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
            }

            OutlinedTextField(
                notes, { notes = it }, label = { Text("Notes (optional)") },
                modifier = Modifier.fillMaxWidth(), singleLine = true
            )

            // Gain preview
            val previewGain = (current.toDoubleOrNull() ?: 0.0) - (invested.toDoubleOrNull() ?: 0.0)
            if (invested.isNotBlank() && current.isNotBlank()) {
                val gainPct = if ((invested.toDoubleOrNull() ?: 0.0) > 0)
                    (previewGain / invested.toDouble()) * 100 else 0.0
                Text(
                    "Gain/Loss: ${CurrencyFormatter.format(previewGain)} (${
                        gainPct.format()
                    }%)",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (previewGain >= 0) IncomeGreen else ExpenseRed,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Button(
                onClick = {
                    val inv = invested.toDoubleOrNull() ?: return@Button
                    val cur = current.toDoubleOrNull() ?: return@Button
                    if (name.isBlank() || investDate.isBlank()) return@Button
                    val sipAmt = if (isSIP) sipAmount.toDoubleOrNull() ?: 0.0 else 0.0
                    onSave(
                        initial?.id ?: 0L, name.trim(), invType,
                        inv, cur, investDate.trim(),
                        maturity.trim().takeIf { it.isNotBlank() },
                        isSIP, sipAmt, notes.trim()
                    )
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) { Text("Save Investment") }

            Spacer(Modifier.height(8.dp))
        }
    }
}
