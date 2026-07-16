package org.example.project.core.util

import kotlin.math.ln
import kotlin.math.pow

/**
 * Financial math utilities matching the reference implementation patterns.
 * All rates passed as percentages (e.g. 12.0 = 12%).
 */
object FinancialMath {

    /**
     * PMT — Monthly payment for a loan.
     * @param annualRatePercent  Annual interest rate in % (e.g. 9.0)
     * @param tenureMonths       Loan tenure in months
     * @param principal          Loan principal
     */
    fun pmt(annualRatePercent: Double, tenureMonths: Int, principal: Double): Double {
        if (annualRatePercent == 0.0) return principal / tenureMonths
        val r = annualRatePercent / 100.0 / 12.0
        val n = tenureMonths.toDouble()
        return principal * r * (1 + r).pow(n) / ((1 + r).pow(n) - 1)
    }

    /**
     * NPER — Number of periods (months) to reach a target saving.
     * Returns months needed; guard against already-achieved and zero-SIP cases.
     * @param annualRatePercent  Expected annual return in %
     * @param monthlyContrib     Monthly contribution (positive number)
     * @param currentAmount      Current saved amount
     * @param targetAmount       Target amount
     * @return months, or null if not achievable (zero contribution)
     */
    fun nperMonths(
        annualRatePercent: Double,
        monthlyContrib: Double,
        currentAmount: Double,
        targetAmount: Double
    ): Int? {
        if (currentAmount >= targetAmount) return 0
        if (monthlyContrib <= 0) return null
        val r = annualRatePercent / 100.0 / 12.0
        return if (r == 0.0) {
            ((targetAmount - currentAmount) / monthlyContrib).toInt() + 1
        } else {
            val fv = targetAmount
            val pv = currentAmount
            val pmt = monthlyContrib
            // NPER formula: ln((pmt + fv*r) / (pmt - pv*r)) / ln(1+r)
            val numerator = pmt + fv * r
            val denominator = pmt - pv * r
            if (denominator <= 0) return null
            (ln(numerator / denominator) / ln(1 + r)).toInt() + 1
        }
    }

    /**
     * Wealth projection using growing-annuity formula.
     * Guards the divide-by-zero when expectedReturn == salaryGrowth (per reference doc).
     * @param existingValue      Current portfolio value
     * @param monthlySip         Current monthly SIP
     * @param annualReturnPct    Expected blended annual return in %
     * @param annualGrowthPct    Annual SIP/salary growth rate in %
     * @param years              Projection horizon
     * @return Nominal projected value
     */
    fun wealthProjection(
        existingValue: Double,
        monthlySip: Double,
        annualReturnPct: Double,
        annualGrowthPct: Double,
        years: Int
    ): Double {
        val r = annualReturnPct / 100.0
        val g = annualGrowthPct / 100.0
        val corpusGrowth = existingValue * (1 + r).pow(years)
        val sipAnnualized = monthlySip * 12
        val sipComponent = if (kotlin.math.abs(r - g) < 0.0001) {
            // Guard: r ≈ g — linear formula
            sipAnnualized * years * (1 + r).pow(years - 1)
        } else {
            sipAnnualized * (((1 + r).pow(years) - (1 + g).pow(years)) / (r - g))
        }
        return corpusGrowth + sipComponent
    }

    /**
     * FIRE target corpus = annual expenses × multiplier (default 25 = 4% rule)
     */
    fun fireTargetCorpus(monthlyExpenses: Double, multiplier: Int): Double =
        monthlyExpenses * 12 * multiplier

    /**
     * FIRE years remaining using NPER.
     * @param annualReturnPct    Expected return %
     * @param monthlySip         Monthly investment amount
     * @param currentCorpus      Current portfolio value
     * @param targetCorpus       FIRE corpus target
     * @return Years (fractional), or null if SIP is zero
     */
    fun fireYearsRemaining(
        annualReturnPct: Double,
        monthlySip: Double,
        currentCorpus: Double,
        targetCorpus: Double
    ): Double? {
        val months = nperMonths(annualReturnPct, monthlySip, currentCorpus, targetCorpus)
            ?: return null
        return months / 12.0
    }

    /**
     * Inflation-adjusted value: nominal / (1 + inflation)^years
     */
    fun inflationAdjusted(nominalValue: Double, inflationPct: Double, years: Int): Double {
        val i = inflationPct / 100.0
        return nominalValue / (1 + i).pow(years)
    }
}
