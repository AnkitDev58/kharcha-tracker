package org.example.project.ui.addtransaction

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.unit.sp
import org.example.project.domain.model.Category
import org.example.project.domain.model.PaymentMethod
import org.example.project.domain.model.TransactionType
import org.example.project.ui.components.parseHexColor
import org.example.project.ui.components.toIcon
import org.example.project.ui.theme.GradientExpenseStart
import org.example.project.ui.theme.GradientIncomeStart
import org.example.project.ui.theme.PrimaryPurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTransactionScreen(
    viewModel: AddEditTransactionViewModel,
    transactionId: Long = -1L,
    onNavigateBack: () -> Unit
) {
    LaunchedEffect(transactionId) {
        viewModel.loadForEdit(transactionId)
    }

    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) onNavigateBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.isEditMode) "Edit Transaction" else "Add Transaction") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    TextButton(onClick = { viewModel.save(transactionId) }) {
                        Text("Save", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Type Toggle
            TypeToggle(
                selected = state.selectedType,
                onSelect = viewModel::setType
            )

            // Amount Input
            AmountField(
                value = state.amount,
                onValueChange = viewModel::setAmount,
                type = state.selectedType
            )

            // Category selection
            val categories = if (state.selectedType == TransactionType.INCOME)
                state.incomeCategories else state.expenseCategories

            if (categories.isNotEmpty()) {
                Text("Category", style = MaterialTheme.typography.titleSmall)
                CategoryGrid(
                    categories = categories,
                    selected = state.selectedCategory,
                    onSelect = viewModel::setCategory
                )
            }

            // Note
            OutlinedTextField(
                value = state.note,
                onValueChange = viewModel::setNote,
                label = { Text("Note (optional)") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Filled.Notes, null) },
                maxLines = 2
            )

            // Payment Method
            Text("Payment Method", style = MaterialTheme.typography.titleSmall)
            PaymentMethodRow(
                selected = state.paymentMethod,
                onSelect = viewModel::setPaymentMethod
            )

            // Favorite toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Mark as Favorite", style = MaterialTheme.typography.bodyMedium)
                Switch(
                    checked = state.isFavorite,
                    onCheckedChange = { viewModel.toggleFavorite() }
                )
            }

            // Error
            state.error?.let { error ->
                Text(
                    error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = { viewModel.save(transactionId) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    if (state.isEditMode) "Update Transaction" else "Save Transaction",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun TypeToggle(selected: TransactionType, onSelect: (TransactionType) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        listOf(TransactionType.EXPENSE, TransactionType.INCOME).forEach { type ->
            val isSelected = selected == type
            val bgColor = when {
                isSelected && type == TransactionType.EXPENSE -> GradientExpenseStart
                isSelected && type == TransactionType.INCOME -> GradientIncomeStart
                else -> Color.Transparent
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(bgColor)
                    .clickable { onSelect(type) }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    type.name.lowercase().replaceFirstChar { it.uppercase() },
                    fontWeight = FontWeight.SemiBold,
                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun AmountField(
    value: String,
    onValueChange: (String) -> Unit,
    type: TransactionType
) {
    val prefix = if (type == TransactionType.INCOME) "+" else "-"
    val accentColor = if (type == TransactionType.INCOME)
        GradientIncomeStart else GradientExpenseStart

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Amount") },
        placeholder = { Text("0.00") },
        prefix = { Text("₹$prefix", color = accentColor, fontWeight = FontWeight.Bold) },
        textStyle = MaterialTheme.typography.headlineMedium.copy(
            fontWeight = FontWeight.Bold,
            color = accentColor
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
private fun CategoryGrid(
    categories: List<Category>,
    selected: Category?,
    onSelect: (Category) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(80.dp),
        modifier = Modifier.heightIn(max = 280.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { cat ->
            val isSelected = selected?.id == cat.id
            val catColor = parseHexColor(cat.colorHex)
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) catColor.copy(0.2f) else MaterialTheme.colorScheme.surfaceVariant)
                    .border(
                        width = if (isSelected) 2.dp else 0.dp,
                        color = if (isSelected) catColor else Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onSelect(cat) }
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(catColor.copy(0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        cat.icon.toIcon(),
                        contentDescription = cat.name,
                        tint = catColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    cat.name,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun PaymentMethodRow(
    selected: PaymentMethod,
    onSelect: (PaymentMethod) -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(PaymentMethod.entries) { method ->
            FilterChip(
                selected = selected == method,
                onClick = { onSelect(method) },
                label = { Text(method.displayName) }
            )
        }
    }
}
