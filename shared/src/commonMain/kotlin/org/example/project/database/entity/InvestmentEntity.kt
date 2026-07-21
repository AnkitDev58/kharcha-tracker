package org.example.project.database.entity

import androidx.room3.*

@Entity(tableName = "investments")
data class InvestmentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    // FD, RD, PPF, EPF, NPS, LIC, MUTUAL_FUND, STOCKS, ETF, GOLD, BONDS, SGB, REAL_ESTATE, CRYPTO, OTHER
    val investmentType: String,
    val investedAmount: Double,
    val currentValue: Double,
    val investDate: String,          // ISO date
    val maturityDate: String? = null, // ISO date, nullable
    val notes: String = "",
    val isSIP: Boolean = false,
    val sipMonthlyAmount: Double = 0.0
)
