package org.example.project.domain.model

import kotlinx.datetime.LocalDateTime

data class Transaction(
    val id: Long = 0,
    val amount: Double,
    val type: TransactionType,
    val categoryId: Long,
    val category: Category? = null,
    val note: String = "",
    val dateTime: LocalDateTime,
    val paymentMethod: PaymentMethod = PaymentMethod.CASH,
    val imagePath: String? = null,
    val isFavorite: Boolean = false,
    val tags: List<String> = emptyList()
)
