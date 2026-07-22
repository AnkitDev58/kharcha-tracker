package org.example.project.ui.transactions

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.project.ui.components.AdaptiveScreenWrapper
import org.example.project.ui.components.TransactionItem
import org.example.project.ui.components.WindowSizeClass

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    viewModel: TransactionsViewModel,
    onAddClick: () -> Unit,
    onNavigateBack: () -> Unit,
    onTransactionClick: (Long) -> Unit,
    sizeClass: WindowSizeClass = WindowSizeClass.COMPACT
) {
    val state by viewModel.uiState.collectAsState()
    var showSearch by remember { mutableStateOf(false) }

    AdaptiveScreenWrapper(sizeClass = sizeClass, maxWidth = 1000.dp) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Transactions") },
                    navigationIcon = {
                        if (viewModel.selectedDate != null) {
                            IconButton(onClick = onNavigateBack) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = { showSearch = !showSearch }) {
                            Icon(if (showSearch) Icons.Filled.Close else Icons.Filled.Search, "Search")
                        }
                        FilterMenu(
                            current = state.filter,
                            onSelect = viewModel::setFilter
                        )
                        SortMenu(
                            current = state.sortOrder,
                            onSelect = viewModel::setSortOrder
                        )
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = onAddClick) {
                    Icon(Icons.Filled.Add, "Add")
                }
            }
        ) { padding ->
            Column(modifier = Modifier.fillMaxSize().padding(top = padding.calculateTopPadding())) {
                // Search bar
                AnimatedVisibility(visible = showSearch) {
                    OutlinedTextField(
                        value = state.searchQuery,
                        onValueChange = viewModel::setSearchQuery,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                        placeholder = { Text("Search transactions...") },
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Filled.Search, null) }
                    )
                }

                // Filter chips
                FilterChipRow(
                    current = state.filter,
                    onSelect = viewModel::setFilter
                )

                // Summary row
                SummaryRow(income = state.totalIncome, expense = state.totalExpense)

                when {
                    state.isLoading -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }

                    state.transactions.isEmpty() -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ReceiptLong,
                                    null,
                                    modifier = Modifier.size(72.dp),
                                    tint = MaterialTheme.colorScheme.outline.copy(0.5f)
                                )
                                Spacer(Modifier.height(16.dp))
                                Text(
                                    "No transactions yet",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    "Tap + to add your first transaction",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    else -> {
                        PaginatedTransactionList(
                            state = state,
                            onTransactionClick = onTransactionClick,
                            onFavoriteToggle = { viewModel.toggleFavorite(it) },
                            onLoadMore = viewModel::loadMoreTransactions
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PaginatedTransactionList(
    state: TransactionsUiState,
    onTransactionClick: (Long) -> Unit,
    onFavoriteToggle: (org.example.project.domain.model.Transaction) -> Unit,
    onLoadMore: () -> Unit
) {
    val listState = rememberLazyListState()

    // Trigger load-more when the user is within 3 items of the end
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val total = listState.layoutInfo.totalItemsCount
            total > 0 && lastVisible >= total - 3
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) onLoadMore()
    }

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(state.transactions, key = { it.id }) { tx ->
            TransactionItem(
                transaction = tx,
                onClick = { onTransactionClick(tx.id) },
                onFavoriteToggle = { onFavoriteToggle(tx) }
            )
        }

        // Loading-more footer
        if (state.isLoadingMore) {
            item(key = "loading_more") {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(28.dp))
                }
            }
        }

        // End-of-list label (only show once everything is loaded)
        if (!state.hasMore && !state.isLoadingMore && state.transactions.isNotEmpty()) {
            item(key = "end_of_list") {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "You're all caught up",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item { Spacer(Modifier.height(80.dp)) }
    }
}

@Composable
private fun FilterChipRow(
    current: TransactionFilter,
    onSelect: (TransactionFilter) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TransactionFilter.entries.forEach { filter ->
            FilterChip(
                selected = current == filter,
                onClick = { onSelect(filter) },
                label = { Text(filter.name.lowercase().replaceFirstChar { it.uppercase() }) }
            )
        }
    }
}

@Composable
private fun SummaryRow(income: Double, expense: Double) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            "Income: ₹${income.toLong()}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            "Expense: ₹${expense.toLong()}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
private fun FilterMenu(current: TransactionFilter, onSelect: (TransactionFilter) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    IconButton(onClick = { expanded = true }) {
        Icon(Icons.Filled.FilterList, "Filter")
    }
    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
        TransactionFilter.entries.forEach { filter ->
            DropdownMenuItem(
                text = { Text(filter.name) },
                onClick = { onSelect(filter); expanded = false },
                leadingIcon = {
                    if (current == filter) Icon(Icons.Filled.Check, null)
                }
            )
        }
    }
}

@Composable
private fun SortMenu(current: SortOrder, onSelect: (SortOrder) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    IconButton(onClick = { expanded = true }) {
        Icon(Icons.AutoMirrored.Filled.Sort, "Sort")
    }
    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
        SortOrder.entries.forEach { sort ->
            DropdownMenuItem(
                text = { Text(sort.name.replace("_", " ")) },
                onClick = { onSelect(sort); expanded = false },
                leadingIcon = {
                    if (current == sort) Icon(Icons.Filled.Check, null)
                }
            )
        }
    }
}
