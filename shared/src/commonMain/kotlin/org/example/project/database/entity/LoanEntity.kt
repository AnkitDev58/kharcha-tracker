package org.example.project.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "loans")
data class LoanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val principal: Double,
    val outstandingBalance: Double,
    val interestRatePercent: Double, // annual %
    val tenureMonths: Int,
    val paidMonths: Int = 0,
    val startDate: String,           // ISO date
    val notes: String = "",
    // PERSONAL, HOME, VEHICLE, EDUCATION, CREDIT_CARD, OTHER
    val loanType: String = "PERSONAL"
)
