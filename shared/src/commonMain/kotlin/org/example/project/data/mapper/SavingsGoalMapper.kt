package org.example.project.data.mapper

import org.example.project.database.entity.SavingsGoalEntity
import org.example.project.domain.model.CategoryIcon
import org.example.project.domain.model.SavingsGoal

fun SavingsGoalEntity.toDomain(): SavingsGoal = SavingsGoal(
    id = id,
    name = name,
    targetAmount = targetAmount,
    currentAmount = currentAmount,
    colorHex = colorHex,
    icon = runCatching { CategoryIcon.valueOf(icon) }.getOrDefault(CategoryIcon.SAVINGS),
    deadline = deadline
)

fun SavingsGoal.toEntity(): SavingsGoalEntity = SavingsGoalEntity(
    id = id,
    name = name,
    targetAmount = targetAmount,
    currentAmount = currentAmount,
    colorHex = colorHex,
    icon = icon.name,
    deadline = deadline
)
