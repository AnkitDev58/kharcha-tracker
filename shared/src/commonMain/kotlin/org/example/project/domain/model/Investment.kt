package org.example.project.domain.model

import kotlin.math.pow

enum class InvestmentType(val label: String, val bucket: AssetBucket) {
    FD("Fixed Deposit", AssetBucket.DEBT),
    RD("Recurring Deposit", AssetBucket.DEBT),
    PPF("PPF", AssetBucket.DEBT),
    EPF("EPF", AssetBucket.DEBT),
    NPS("NPS", AssetBucket.DEBT),
    LIC("LIC / Insurance", AssetBucket.DEBT),
    MUTUAL_FUND("Mutual Fund", AssetBucket.EQUITY),
    STOCKS("Stocks", AssetBucket.EQUITY),
    ETF("ETF", AssetBucket.EQUITY),
    GOLD("Gold / Jewellery", AssetBucket.GOLD),
    SGB("Sovereign Gold Bond", AssetBucket.GOLD),
    BONDS("Bonds", AssetBucket.DEBT),
    REAL_ESTATE("Real Estate", AssetBucket.OTHER),
    CRYPTO("Crypto", AssetBucket.OTHER),
    OTHER("Other", AssetBucket.OTHER)
}

enum class AssetBucket(val label: String) {
    EQUITY("Equity"),
    DEBT("Debt"),
    GOLD("Gold"),
    OTHER("Other")
}

data class Investment(
    val id: Long = 0,
    val name: String,
    val investmentType: InvestmentType,
    val investedAmount: Double,
    val currentValue: Double,
    val investDate: String,
    val maturityDate: String? = null,
    val notes: String = "",
    val isSIP: Boolean = false,
    val sipMonthlyAmount: Double = 0.0
) {
    val gain: Double get() = currentValue - investedAmount
    val gainPercent: Double get() = if (investedAmount > 0) (gain / investedAmount) * 100 else 0.0
    val isProfit: Boolean get() = gain >= 0

    /** Approximate CAGR — requires daysHeld > 0, else returns 0 */
    fun cagr(daysHeld: Int): Double {
        if (daysHeld <= 0 || investedAmount <= 0) return 0.0
        val years = daysHeld / 365.0
        return ((currentValue / investedAmount).pow(1.0 / years) - 1.0) * 100.0
    }
}
