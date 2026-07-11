package org.example.project.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("categoryId"), Index("dateTime")]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val type: String,  // INCOME, EXPENSE, TRANSFER
    val categoryId: Long,
    val note: String = "",
    val dateTime: String,  // ISO-8601 format
    val paymentMethod: String,
    val imagePath: String? = null,
    val isFavorite: Boolean = false,
    val tags: String = ""  // Comma-separated
)
