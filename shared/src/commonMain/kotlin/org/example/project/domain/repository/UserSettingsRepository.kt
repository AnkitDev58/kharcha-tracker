package org.example.project.domain.repository

import kotlinx.coroutines.flow.Flow
import org.example.project.domain.model.UserSettings

interface UserSettingsRepository {
    fun getUserSettings(): Flow<UserSettings>
    suspend fun updateCurrencySymbol(symbol: String)
    suspend fun updateDarkTheme(isDark: Boolean)
    suspend fun updateFirstDayOfWeek(day: Int)
    suspend fun updateMonthlyBudgetCeiling(amount: Double)
    suspend fun updateEmergencyFundMonths(months: Int)
    suspend fun updateFireMultiplier(multiplier: Int)
    suspend fun updateAges(currentAge: Int, retirementAge: Int)
    suspend fun updateExpectedReturns(equity: Double, debt: Double, gold: Double, fd: Double)
    suspend fun updateAllocations(equity: Double, debt: Double, gold: Double, fd: Double)
    suspend fun updateGrowthRates(inflation: Double, salaryGrowth: Double, taxRate: Double)
    suspend fun resetToDefaults()
}
