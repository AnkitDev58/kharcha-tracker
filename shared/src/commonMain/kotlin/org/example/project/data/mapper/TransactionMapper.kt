package org.example.project.data.mapper

import kotlinx.datetime.LocalDateTime
import org.example.project.database.entity.TransactionEntity
import org.example.project.domain.model.PaymentMethod
import org.example.project.domain.model.Transaction
import org.example.project.domain.model.TransactionType

fun TransactionEntity.toDomain(): Transaction = Transaction(
    id = id,
    amount = amount,
    type = runCatching { TransactionType.valueOf(type) }.getOrDefault(TransactionType.EXPENSE),
    categoryId = categoryId,
    note = note,
    dateTime = runCatching { LocalDateTime.parse(dateTime) }.getOrDefault(LocalDateTime(2024, 1, 1, 0, 0)),
    paymentMethod = runCatching { PaymentMethod.valueOf(paymentMethod) }.getOrDefault(PaymentMethod.CASH),
    imagePath = imagePath,
    isFavorite = isFavorite,
    tags = if (tags.isBlank()) emptyList() else tags.split(",").map { it.trim() }
)

fun Transaction.toEntity(): TransactionEntity = TransactionEntity(
    id = id,
    amount = amount,
    type = type.name,
    categoryId = categoryId,
    note = note,
    dateTime = dateTime.toString(),
    paymentMethod = paymentMethod.name,
    imagePath = imagePath,
    isFavorite = isFavorite,
    tags = tags.joinToString(",")
)
