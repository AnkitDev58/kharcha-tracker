package org.example.project.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "annual_expenses")
data class AnnualExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String,
    val amount: Double,
    // MONTHLY, QUARTERLY, HALF_YEARLY, YEARLY
    val frequency: String = "YEARLY",
    val dueMonth: Int = 1,           // 1–12
    val lastPaidDate: String? = null, // ISO date string
    val notes: String = ""
)
