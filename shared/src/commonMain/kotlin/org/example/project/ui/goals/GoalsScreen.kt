package org.example.project.ui.goals

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.core.util.CurrencyFormatter
import org.example.project.domain.model.SavingsGoal
import org.example.project.ui.components.AnimatedCircularProgress
import org.example.project.ui.components.AnimatedLinearProgress
import org.example.project.ui.components.AppCard
import org.example.project.ui.components.parseHexColor
import org.example.project.ui.components.toIcon
import org.example.project.ui.theme.AccentGreen
import org.example.project.ui.theme.ExpenseRed
import org.example.project.ui.theme.GradientSavingsEnd
import org.example.project.ui.theme.GradientSavingsStart
import org.example.project.ui.theme.IncomeGreen
import org.example.project.ui.theme.PrimaryPurple
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun GoalsScreen(viewModel: GoalsViewModel = koinViewModel()) {
    val state by viewModel.uiState.collectAsState()

    // Sheet visibility state
    var showAddEditSheet by remember { mutableStateOf(false) }
    var showContributionSheet by remember { mutableStateOf(false) }
    var editingGoal by remember { mutableStateOf<SavingsGoal?>(null) }
    var contributionGoal by remember { mutableStateOf<SavingsGoal?>(null) }

    // Collect one-shot effects
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is GoalsEffect.GoalSaved -> {
                    showAddEditSheet = false
                    editingGoal = null
                }

                is GoalsEffect.ContributionAdded -> {
                    showContributionSheet = false
                    contributionGoal = null
                }

                is GoalsEffect.GoalDeleted -> { /* list updates reactively */
                }

                is GoalsEffect.Error -> { /* error shown inline in state */
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Savings Goals", fontWeight = FontWeight.Bold)
                        Text(
                            "${state.goals.size} goals · ${state.completedCount} completed",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingGoal = null
                    showAddEditSheet = true
                },
                containerColor = PrimaryPurple
            ) {
                Icon(Icons.Filled.Add, "Add Goal", tint = Color.White)
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Summary header card
            item {
                GoalsSummaryCard(
                    totalSaved = state.totalSaved,
                    totalTarget = state.totalTarget,
                    overallProgress = state.overallProgress,
                    completedCount = state.completedCount,
                    totalCount = state.goals.size
                )
            }

            if (state.isLoading) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryPurple)
                    }
                }
            } else if (state.goals.isEmpty()) {
                item { GoalsEmptyState() }
            } else {
                // Active goals first, completed at the bottom
                val (active, completed) = state.goals.partition { !it.isCompleted }

                if (active.isNotEmpty()) {
                    item {
                        Text(
                            "Active",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                        )
                    }
                    items(active, key = { it.id }) { goal ->
                        GoalCard(
                            goal = goal,
                            onAddClick = {
                                contributionGoal = goal
                                showContributionSheet = true
                            },
                            onEditClick = {
                                editingGoal = goal
                                showAddEditSheet = true
                            },
                            onDeleteClick = {
                                viewModel.onEvent(GoalsEvent.DeleteGoal(goal.id))
                            }
                        )
                    }
                }

                if (completed.isNotEmpty()) {
                    item {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Completed",
                            style = MaterialTheme.typography.labelLarge,
                            color = IncomeGreen,
                            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                        )
                    }
                    items(completed, key = { it.id }) { goal ->
                        GoalCard(
                            goal = goal,
                            onAddClick = {
                                contributionGoal = goal
                                showContributionSheet = true
                            },
                            onEditClick = {
                                editingGoal = goal
                                showAddEditSheet = true
                            },
                            onDeleteClick = {
                                viewModel.onEvent(GoalsEvent.DeleteGoal(goal.id))
                            }
                        )
                    }
                }
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }

    // Add / Edit bottom sheet
    if (showAddEditSheet) {
        AddEditGoalSheet(
            goal = editingGoal,
            onDismiss = {
                showAddEditSheet = false
                editingGoal = null
            },
            onSave = { event -> viewModel.onEvent(event) }
        )
    }

    // Add contribution bottom sheet
    if (showContributionSheet) {
        contributionGoal?.let { goal ->
            AddContributionSheet(
                goal = goal,
                onDismiss = {
                    showContributionSheet = false
                    contributionGoal = null
                },
                onConfirm = { amount ->
                    viewModel.onEvent(GoalsEvent.AddContribution(goal.id, amount))
                }
            )
        }
    }
}

// ── Summary Card ───────────────────────────────────────────────────────────────

@Composable
private fun GoalsSummaryCard(
    totalSaved: Double,
    totalTarget: Double,
    overallProgress: Float,
    completedCount: Int,
    totalCount: Int
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(listOf(GradientSavingsStart, GradientSavingsEnd)))
            .padding(20.dp)
    ) {
        Column {
            Text(
                "Overall Progress",
                style = MaterialTheme.typography.labelLarge,
                color = Color.White.copy(alpha = 0.8f)
            )
            Spacer(Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                AnimatedCircularProgress(
                    progress = overallProgress,
                    size = 100.dp,
                    strokeWidth = 10.dp,
                    label = "saved"
                )
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    SummaryRow(
                        label = "Total Saved",
                        value = CurrencyFormatter.format(totalSaved),
                        valueColor = Color.White
                    )
                    SummaryRow(
                        label = "Total Target",
                        value = CurrencyFormatter.format(totalTarget),
                        valueColor = Color.White.copy(alpha = 0.85f)
                    )
                    SummaryRow(
                        label = "Remaining",
                        value = CurrencyFormatter.format(
                            (totalTarget - totalSaved).coerceAtLeast(
                                0.0
                            )
                        ),
                        valueColor = Color.White.copy(alpha = 0.85f)
                    )
                    SummaryRow(
                        label = "Completed",
                        value = "$completedCount / $totalCount",
                        valueColor = AccentGreen
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(0.7f))
        Text(
            value,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = valueColor
        )
    }
}

// ── Goal Card ─────────────────────────────────────────────────────────────────

@Composable
private fun GoalCard(
    goal: SavingsGoal,
    onAddClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val goalColor = parseHexColor(goal.colorHex)
    var expanded by remember { mutableStateOf(false) }

    AppCard(
        modifier = Modifier,
        onClick = { expanded = !expanded }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Icon bubble
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(goalColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                if (goal.isCompleted) {
                    Icon(
                        Icons.Filled.CheckCircle,
                        "Completed",
                        tint = IncomeGreen,
                        modifier = Modifier.size(28.dp)
                    )
                } else {
                    Icon(
                        goal.icon.toIcon(),
                        goal.name,
                        tint = goalColor,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        goal.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (goal.isCompleted) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = IncomeGreen.copy(alpha = 0.15f)
                        ) {
                            Text(
                                "Done",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = IncomeGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Text(
                            "${(goal.progress * 100).toInt()}%",
                            style = MaterialTheme.typography.labelMedium,
                            color = goalColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(Modifier.height(2.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        CurrencyFormatter.format(goal.currentAmount),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        "of ${CurrencyFormatter.format(goal.targetAmount)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(Modifier.height(8.dp))

                AnimatedLinearProgress(
                    progress = goal.progress,
                    height = 7.dp,
                    trackColor = goalColor,
                    showLabel = false
                )

                // Deadline
                goal.deadline?.let {
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.DateRange,
                            "Deadline",
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            it,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Expanded action row

        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column {
                Spacer(Modifier.height(12.dp))
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )
                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (!goal.isCompleted) {
                        OutlinedButton(
                            onClick = onAddClick,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Add, null, Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Add Money", fontSize = 13.sp)
                        }
                    }

                    OutlinedButton(
                        onClick = onEditClick,
                        modifier = Modifier,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Edit, null, Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Edit", fontSize = 13.sp)
                    }

                    OutlinedButton(
                        onClick = onDeleteClick,
                        modifier = Modifier,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = ExpenseRed
                        )
                    ) {
                        Icon(Icons.Default.Delete, null, Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Delete", fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

// ── Empty State ────────────────────────────────────────────────────────────────

@Composable
private fun GoalsEmptyState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(PrimaryPurple.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Savings,
                    "No goals",
                    tint = PrimaryPurple,
                    modifier = Modifier.size(36.dp)
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                "No savings goals yet",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                "Tap + to create your first goal",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
