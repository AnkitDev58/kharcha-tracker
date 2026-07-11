package org.example.project.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: Long = 0,
    val name: String,
    val icon: CategoryIcon,
    val colorHex: String,
    val type: TransactionType,  // INCOME or EXPENSE
    val isDefault: Boolean = false,
    val isArchived: Boolean = false
)
