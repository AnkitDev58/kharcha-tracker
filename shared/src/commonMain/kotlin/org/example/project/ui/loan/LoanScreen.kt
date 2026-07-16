package org.example.project.ui.loan

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
import org.example.project.domain.model.Loan
import org.example.project.domain.model.LoanType
import org.example.project.ui.components.*
import org.example.project.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanScreen(
    viewModel: LoanViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val snackbar = remember { SnackbarHostState() }
    var showSheet by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<Loan?>(null) }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is LoanEffect.Saved   -> { snackbar.showSnackbar("Saved"); showSheet = false }
                is LoanEffect.Deleted -> snackbar.showSnackbar("Loan deleted")
                is LoanEffect.Error   -> snackbar.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Debt & Loan Tracker", fontWeight = FontWeight.Bold) },
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
            ) { Icon(Icons.Filled.Add, "Add Loan", tint = Color.White) }
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Debt summary
            item {
                GradientCard(
                    modifier = Modifier.fillMaxWidth(),
                    gradientColors = listOf(Color(0xFF833C0C), Color(0xFFBF8F00)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Text("Total Debt", style = MaterialTheme.typography.labelLarge,
                            color = Color.White.copy(0.85f))
                        Spacer(Modifier.height(8.dp))
                        Text(
                            CurrencyFormatter.format(state.totalOutstanding),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold, color = Color.White
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Total EMI / month", style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(0.75f))
                                Text(CurrencyFormatter.format(state.totalEmi),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold, color = Color.White)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Active Loans", style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(0.75f))
                                Text("${state.loans.size}",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold, color = Color.White)
                            }
                        }
                    }
                }
            }

            if (state.loans.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Filled.CreditCard, null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(48.dp))
                            Spacer(Modifier.height(8.dp))
                            Text("No loans added", style = MaterialTheme.typography.titleMedium)
                            Text("Tap + to track a loan or credit card",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            } else {
                items(state.loans, key = { it.id }) { loan ->
                    LoanItem(
                        loan = loan,
                        onEdit = { editing = loan; showSheet = true },
                        onMarkPaid = { viewModel.markEmiPaid(loan) },
                        onDelete = { viewModel.delete(loan.id) }
                    )
                }
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }

    if (showSheet) {
        AddEditLoanSheet(
            initial = editing,
            onDismiss = { showSheet = false },
            onSave = { id, name, principal, outstanding, rate, tenure, paid, type, notes ->
                viewModel.save(id, name, principal, outstanding, rate, tenure, paid, type, notes)
            }
        )
    }
}

@Composable
private fun LoanItem(
    loan: Loan,
    onEdit: () -> Unit,
    onMarkPaid: () -> Unit,
    onDelete: () -> Unit
) {
    AppCard(onClick = onEdit) {
        Column {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(44.dp).clip(CircleShape)
                        .background(MaterialTheme.colorScheme.errorContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.CreditCard, null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(22.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(loan.name, style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold)
                        Text(CurrencyFormatter.format(loan.outstandingBalance),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error)
                    }
                    Spacer(Modifier.height(2.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        LoanChip(loan.loanType.label)
                        Text("${loan.interestRatePercent}% p.a.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.Delete, "Delete",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp))
                }
            }

            Spacer(Modifier.height(10.dp))

            // Progress bar: paid / tenure
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${loan.paidMonths} / ${loan.tenureMonths} months paid",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("EMI: ${CurrencyFormatter.format(loan.emi)}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(4.dp))
            AnimatedLinearProgress(
                progress = loan.progressFraction,
                height = 7.dp,
                trackColor = MaterialTheme.colorScheme.primary,
                showLabel = false
            )
            Spacer(Modifier.height(8.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Remaining", style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${loan.remainingMonths} months",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Total Interest", style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(CurrencyFormatter.format(loan.totalInterest),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.error)
                }
                OutlinedButton(
                    onClick = onMarkPaid,
                    modifier = Modifier.height(32.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) { Text("Mark EMI Paid", style = MaterialTheme.typography.labelSmall) }
            }
        }
    }
}

@Composable
private fun LoanChip(label: String) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = MaterialTheme.colorScheme.errorContainer
    ) {
        Text(label,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onErrorContainer)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddEditLoanSheet(
    initial: Loan?,
    onDismiss: () -> Unit,
    onSave: (Long, String, Double, Double, Double, Int, Int, LoanType, String) -> Unit
) {
    var name        by remember(initial) { mutableStateOf(initial?.name ?: "") }
    var principal   by remember(initial) { mutableStateOf(initial?.principal?.toString() ?: "") }
    var outstanding by remember(initial) { mutableStateOf(initial?.outstandingBalance?.toString() ?: "") }
    var rate        by remember(initial) { mutableStateOf(initial?.interestRatePercent?.toString() ?: "") }
    var tenure      by remember(initial) { mutableStateOf(initial?.tenureMonths?.toString() ?: "") }
    var paid        by remember(initial) { mutableStateOf(initial?.paidMonths?.toString() ?: "0") }
    var loanType    by remember(initial) { mutableStateOf(initial?.loanType ?: LoanType.PERSONAL) }
    var notes       by remember(initial) { mutableStateOf(initial?.notes ?: "") }
    var typeExpanded by remember { mutableStateOf(false) }

    // Auto-fill outstanding = principal when adding new
    LaunchedEffect(principal) {
        if (initial == null && outstanding.isBlank()) {
            outstanding = principal
        }
    }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            Modifier.padding(16.dp).navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                if (initial == null) "Add Loan" else "Edit Loan",
                style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold
            )

            OutlinedTextField(name, { name = it }, label = { Text("Loan Name") },
                modifier = Modifier.fillMaxWidth(), singleLine = true)

            // Loan type dropdown
            ExposedDropdownMenuBox(expanded = typeExpanded, onExpandedChange = { typeExpanded = it }) {
                OutlinedTextField(
                    value = loanType.label, onValueChange = {}, readOnly = true,
                    label = { Text("Loan Type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(typeExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = typeExpanded, onDismissRequest = { typeExpanded = false }) {
                    LoanType.entries.forEach { t ->
                        DropdownMenuItem(text = { Text(t.label) },
                            onClick = { loanType = t; typeExpanded = false })
                    }
                }
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(principal, { principal = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Principal (₹)") }, modifier = Modifier.weight(1f), singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                OutlinedTextField(outstanding, { outstanding = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Outstanding (₹)") }, modifier = Modifier.weight(1f), singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(rate, { rate = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Rate % p.a.") }, modifier = Modifier.weight(1f), singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                OutlinedTextField(tenure, { tenure = it.filter { c -> c.isDigit() } },
                    label = { Text("Tenure (months)") }, modifier = Modifier.weight(1f), singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            }

            OutlinedTextField(paid, { paid = it.filter { c -> c.isDigit() } },
                label = { Text("EMIs Already Paid") }, modifier = Modifier.fillMaxWidth(),
                singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))

            OutlinedTextField(notes, { notes = it }, label = { Text("Notes (optional)") },
                modifier = Modifier.fillMaxWidth(), singleLine = true)

            // Live EMI preview
            val previewEmi = run {
                val p = principal.toDoubleOrNull() ?: return@run null
                val r = rate.toDoubleOrNull() ?: return@run null
                val n = tenure.toIntOrNull() ?: return@run null
                if (p > 0 && n > 0) {
                    val loan = Loan(0, "", p, p, r, n, 0, "")
                    loan.emi
                } else null
            }
            if (previewEmi != null) {
                Text(
                    "Monthly EMI: ${CurrencyFormatter.format(previewEmi)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Button(
                onClick = {
                    val p = principal.toDoubleOrNull() ?: return@Button
                    val o = outstanding.toDoubleOrNull() ?: p
                    val r = rate.toDoubleOrNull() ?: return@Button
                    val t = tenure.toIntOrNull() ?: return@Button
                    val pd = paid.toIntOrNull() ?: 0
                    if (name.isBlank()) return@Button
                    onSave(initial?.id ?: 0L, name.trim(), p, o, r, t, pd, loanType, notes.trim())
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) { Text("Save Loan") }

            Spacer(Modifier.height(8.dp))
        }
    }
}
