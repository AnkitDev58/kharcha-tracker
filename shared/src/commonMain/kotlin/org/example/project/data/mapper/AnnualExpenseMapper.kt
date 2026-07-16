package org.example.project.data.mapper

import org.example.project.database.entity.AnnualExpenseEntity
import org.example.project.domain.model.AnnualExpense
import org.example.project.domain.model.ExpenseFrequency

fun AnnualExpenseEntity.toDomain(): AnnualExpense = AnnualExpense(
    id = id,
    name = name,
    category = category,
    amount = amount,
    frequency = runCatching { ExpenseFrequency.valueOf(frequency) }.getOrDefault(ExpenseFrequency.YEARLY),
    dueMonth = dueMonth,
    lastPaidDate = lastPaidDate,
    notes = notes
)

fun AnnualExpense.toEntity(): AnnualExpenseEntity = AnnualExpenseEntity(
    id = id,
    name = name,
    category = category,
    amount = amount,
    frequency = frequency.name,
    dueMonth = dueMonth,
    lastPaidDate = lastPaidDate,
    notes = notes
)
