package org.example.project.data.mapper

import org.example.project.database.entity.CategoryEntity
import org.example.project.domain.model.Category
import org.example.project.domain.model.CategoryIcon
import org.example.project.domain.model.TransactionType

fun CategoryEntity.toDomain(): Category = Category(
    id = id,
    name = name,
    icon = runCatching { CategoryIcon.valueOf(icon) }.getOrDefault(CategoryIcon.OTHERS),
    colorHex = colorHex,
    type = runCatching { TransactionType.valueOf(type) }.getOrDefault(TransactionType.EXPENSE),
    isDefault = isDefault,
    isArchived = isArchived
)

fun Category.toEntity(): CategoryEntity = CategoryEntity(
    id = id,
    name = name,
    icon = icon.name,
    colorHex = colorHex,
    type = type.name,
    isDefault = isDefault,
    isArchived = isArchived
)
