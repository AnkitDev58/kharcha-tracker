package org.example.project.ui.goals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.project.core.util.CurrencyFormatter
import org.example.project.domain.model.SavingsGoal
import org.example.project.ui.components.AnimatedLinearProgress
import org.example.project.ui.components.parseHexColor
import org.example.project.ui.components.toIcon
import org.example.project.ui.theme.IncomeGreen
import org.example.project.ui.theme.PrimaryPurple

// Quick-add preset amounts
private val presetAmounts = listOf(500.0, 1_000.0, 2_000.0, 5_000.0, 10_000.0, 25_000.0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddContributionSheet(
    goal: SavingsGoal,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var amountText by remember { mutableStateOf("") }
    val goalColor = parseHexColor(goal.colorHex)

    val enteredAmount = amountText.toDoubleOrNull() ?: 0.0
    val projectedAmount = (goal.currentAmount + enteredAmount).coerceAtMost(goal.targetAmount)
    val projectedProgress = if (goal.targetAmount > 0)
        (projectedAmount / goal.targetAmount).toFloat().coerceIn(0f, 1f)
    else 0f

    val canConfirm = enteredAmount > 0

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header — goal identity
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(goalColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        goal.icon.toIcon(),
                        goal.name,
                        tint = goalColor,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Column {
                    Text(
                        "Add to \"${goal.name}\"",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "${CurrencyFormatter.format(goal.currentAmount)} of ${CurrencyFormatter.format(goal.targetAmount)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Current progress bar
            AnimatedLinearProgress(
                progress = goal.progress,
                height = 8.dp,
                trackColor = goalColor,
                showLabel = false
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

            // Amount input
            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it.filter { c -> c.isDigit() || c == '.' } },
                label = { Text("Amount to Add (₹)") },
                placeholder = { Text("Enter amount") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Quick-add presets
            Text(
                "Quick add",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                presetAmounts.take(3).forEach { preset ->
                    PresetChip(
                        label = CurrencyFormatter.formatCompact(preset),
                        selected = amountText == preset.toLong().toString(),
                        color = goalColor,
                        modifier = Modifier.weight(1f),
                        onClick = { amountText = preset.toLong().toString() }
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                presetAmounts.drop(3).forEach { preset ->
                    PresetChip(
                        label = CurrencyFormatter.formatCompact(preset),
                        selected = amountText == preset.toLong().toString(),
                        color = goalColor,
                        modifier = Modifier.weight(1f),
                        onClick = { amountText = preset.toLong().toString() }
                    )
                }
            }

            // Live preview — show updated progress after contribution
            if (canConfirm) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = goalColor.copy(alpha = 0.08f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "After contribution",
                            style = MaterialTheme.typography.labelMedium,
                            color = goalColor,
                            fontWeight = FontWeight.SemiBold
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                CurrencyFormatter.format(projectedAmount),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                "${(projectedProgress * 100).toInt()}%",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (projectedProgress >= 1f) IncomeGreen else goalColor
                            )
                        }
                        AnimatedLinearProgress(
                            progress = projectedProgress,
                            height = 6.dp,
                            trackColor = goalColor,
                            showLabel = false
                        )
                        if (projectedProgress >= 1f) {
                            Text(
                                "Goal completed!",
                                style = MaterialTheme.typography.labelMedium,
                                color = IncomeGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                        } else {
                            Text(
                                "Remaining: ${CurrencyFormatter.format(goal.targetAmount - projectedAmount)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Confirm button
            Button(
                onClick = { if (canConfirm) onConfirm(enteredAmount) },
                enabled = canConfirm,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
            ) {
                Text("Add ₹${if (canConfirm) amountText else "0"}", fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun PresetChip(
    label: String,
    selected: Boolean,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = if (selected) color.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
        border = if (selected) androidx.compose.foundation.BorderStroke(1.dp, color) else null
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(
                label,
                style = MaterialTheme.typography.labelMedium,
                color = if (selected) color else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}
