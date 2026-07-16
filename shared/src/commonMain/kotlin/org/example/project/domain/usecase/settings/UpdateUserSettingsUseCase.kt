package org.example.project.domain.usecase.settings

import org.example.project.domain.repository.UserSettingsRepository

class UpdateUserSettingsUseCase(private val repository: UserSettingsRepository) {
    suspend fun updateCurrency(symbol: String) = repository.updateCurrencySymbol(symbol)
    suspend fun updateDarkTheme(isDark: Boolean) = repository.updateDarkTheme(isDark)
    suspend fun updateFirstDayOfWeek(day: Int) = repository.updateFirstDayOfWeek(day)
    suspend fun updateMonthlyBudget(amount: Double) = repository.updateMonthlyBudgetCeiling(amount)
    suspend fun updateEmergencyMonths(months: Int) = repository.updateEmergencyFundMonths(months)
    suspend fun updateFireSettings(multiplier: Int) = repository.updateFireMultiplier(multiplier)
    suspend fun updateAges(currentAge: Int, retirementAge: Int) =
        repository.updateAges(currentAge, retirementAge)
    suspend fun updateReturns(equity: Double, debt: Double, gold: Double, fd: Double) =
        repository.updateExpectedReturns(equity, debt, gold, fd)
    suspend fun updateAllocations(equity: Double, debt: Double, gold: Double, fd: Double) =
        repository.updateAllocations(equity, debt, gold, fd)
    suspend fun updateGrowthRates(inflation: Double, salaryGrowth: Double, taxRate: Double) =
        repository.updateGrowthRates(inflation, salaryGrowth, taxRate)
    suspend fun resetToDefaults() = repository.resetToDefaults()
}
