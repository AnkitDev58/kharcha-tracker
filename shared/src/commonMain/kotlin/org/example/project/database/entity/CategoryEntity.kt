package org.example.project.database.entity

import androidx.room3.*

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val icon: String,
    val colorHex: String,
    val type: String,  // INCOME or EXPENSE
    val isDefault: Boolean = false,
    val isArchived: Boolean = false
)
