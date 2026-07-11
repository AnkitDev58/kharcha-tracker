package org.example.project.ui.goals

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.project.core.data.DefaultCategories
import org.example.project.domain.model.CategoryIcon
import org.example.project.domain.model.SavingsGoal
import org.example.project.ui.components.parseHexColor
import org.example.project.ui.components.toIcon
import org.example.project.ui.theme.PrimaryPurple


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditGoalSheet(
    goal: SavingsGoal?,
    onDismiss: () -> Unit,
    onSave: (GoalsEvent.SaveGoal) -> Unit
) {
    val isEditing = goal != null

    // Form state — pre-fill when editing
    var name by remember { mutableStateOf(goal?.name ?: "") }
    var targetText by remember {
        mutableStateOf(
            if (isEditing) goal.targetAmount.toLong().toString() else ""
        )
    }
    var currentText by remember {
        mutableStateOf(
            if (isEditing) goal.currentAmount.toLong().toString() else ""
        )
    }
    var deadline by remember { mutableStateOf(goal?.deadline ?: "") }
    var selectedColor by remember { mutableStateOf(goal?.colorHex ?: "#6C63FF") }
    var selectedIcon by remember { mutableStateOf(goal?.icon ?: CategoryIcon.SAVINGS) }

    // Validation
    val nameError = name.isBlank()
    val targetError = targetText.toDoubleOrNull()?.let { it <= 0 } ?: true
    val canSave = !nameError && !targetError

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
            // Title
            Text(
                if (isEditing) "Edit Goal" else "New Savings Goal",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            // Goal name
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Goal Name") },
                placeholder = { Text("e.g. Buy a Car") },
                isError = name.isNotEmpty() && nameError,
                supportingText = {
                    if (name.isNotEmpty() && nameError) Text("Name cannot be empty")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Target amount
            OutlinedTextField(
                value = targetText,
                onValueChange = { targetText = it.filter { c -> c.isDigit() || c == '.' } },
                label = { Text("Target Amount (₹)") },
                placeholder = { Text("500000") },
                isError = targetText.isNotEmpty() && targetError,
                supportingText = {
                    if (targetText.isNotEmpty() && targetError) Text("Enter a valid amount")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Current saved amount (optional, default 0)
            OutlinedTextField(
                value = currentText,
                onValueChange = { currentText = it.filter { c -> c.isDigit() || c == '.' } },
                label = { Text("Already Saved (₹)") },
                placeholder = { Text("0") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Deadline (plain text — user types YYYY-MM-DD)
            OutlinedTextField(
                value = deadline,
                onValueChange = { deadline = it },
                label = { Text("Deadline (optional)") },
                placeholder = { Text("e.g. Dec 2025") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Icon picker
            Text(
                "Icon",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(6),
                modifier = Modifier
                    .fillMaxWidth()
                   ,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                userScrollEnabled = false
            ) {
                items(DefaultCategories.all.toSet().toList()) { icon ->
                    val iconColor = parseHexColor(icon.colorHex)
                    val isSelected = icon.icon == selectedIcon
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) iconColor.copy(alpha = 0.2f)
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .then(
                                if (isSelected) Modifier.border(2.dp, iconColor, CircleShape)
                                else Modifier
                            )
                            .clickable { selectedIcon = icon.icon },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            icon.icon.toIcon(),
                            icon.name,
                            tint = if (isSelected) iconColor else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(4.dp))

            // Save button
            Button(
                onClick = {
                    if (canSave) {
                        onSave(
                            GoalsEvent.SaveGoal(
                                id = goal?.id ?: 0L,
                                name = name.trim(),
                                targetAmount = targetText.toDouble(),
                                currentAmount = currentText.toDoubleOrNull() ?: 0.0,
                                colorHex = selectedColor,
                                icon = selectedIcon,
                                deadline = deadline.trim().takeIf { it.isNotEmpty() }
                            )
                        )
                    }
                },
                enabled = canSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
            ) {
                Text(
                    if (isEditing) "Update Goal" else "Create Goal",
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
