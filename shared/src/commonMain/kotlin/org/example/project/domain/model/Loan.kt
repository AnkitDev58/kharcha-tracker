package org.example.project.domain.model

import kotlin.math.pow

enum class LoanType(val label: String) {
    PERSONAL("Personal Loan"),
    HOME("Home Loan"),
    VEHICLE("Vehicle Loan"),
    EDUCATION("Education Loan"),
    CREDIT_CARD("Credit Card"),
    OTHER("Other")
}

data class Loan(
    val id: Long = 0,
    val name: String,
    val principal: Double,
    val outstandingBalance: Double,
    val interestRatePercent: Double,
    val tenureMonths: Int,
    val paidMonths: Int = 0,
    val startDate: String,
    val notes: String = "",
    val loanType: LoanType = LoanType.PERSONAL
) {
    /** Monthly EMI using standard PMT formula: P × r(1+r)^n / ((1+r)^n - 1) */
    val emi: Double
        get() {
            if (interestRatePercent == 0.0) return principal / tenureMonths.toDouble()
            val r = interestRatePercent / 100.0 / 12.0
            val n = tenureMonths.toDouble()
            return principal * r * (1 + r).pow(n) / ((1 + r).pow(n) - 1)
        }

    val remainingMonths: Int
        get() = (tenureMonths - paidMonths).coerceAtLeast(0)

    val progressFraction: Float
        get() = if (tenureMonths > 0) (paidMonths.toFloat() / tenureMonths).coerceIn(0f, 1f) else 0f

    val totalInterest: Double
        get() = (emi * tenureMonths) - principal

    val amountPaid: Double
        get() = principal - outstandingBalance


    val pendingAmount get() = emi * remainingMonths

    val isLoanComplete get() = tenureMonths == paidMonths

    val totalAmount get() = emi * tenureMonths

}
