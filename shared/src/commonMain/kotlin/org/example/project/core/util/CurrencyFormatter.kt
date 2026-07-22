package org.example.project.core.util

import kotlin.math.roundToInt

object CurrencyFormatter {
    fun format(amount: Double, symbol: String = "₹"): String {
        return DateTimeUtils.formatAmount(amount, symbol)
    }

    fun formatCompact(amount: Double, symbol: String = "₹"): String {
        return when {
            amount >= 10_000_000 -> "${symbol}${oneDecimal(amount / 10_000_000)}Cr"
            amount >= 100_000 -> "${symbol}${oneDecimal(amount / 100_000)}L"
            amount >= 1_000 -> "${symbol}${oneDecimal(amount / 1_000)}K"
            else -> "$symbol${DateTimeUtils.formatNumber(amount)}"
        }
    }

    private fun oneDecimal(value: Double): String {
        val rounded = kotlin.math.round(value * 10) / 10.0
        val intPart = rounded.toLong()
        val decPart = kotlin.math.round((rounded - intPart) * 10).toLong()
        return if (decPart == 0L) "$intPart" else "$intPart.$decPart"
    }


    fun Double.format(): String {
        return ((this * 10).roundToInt() / 10.0).toString()
    }
}
