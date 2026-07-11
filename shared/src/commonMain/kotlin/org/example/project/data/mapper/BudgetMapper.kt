package org.example.project.data.mapper

import org.example.project.database.entity.BudgetEntity
import org.example.project.domain.model.Budget

fun BudgetEntity.toDomain(): Budget = Budget(
    id = id,
    categoryId = categoryId,
    amount = amount,
    month = month,
    year = year
)

fun Budget.toEntity(): BudgetEntity = BudgetEntity(
    id = id,
    categoryId = categoryId,
    amount = amount,
    month = month,
    year = year
)
