package org.example.project.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.example.project.domain.model.UserSettings
import org.example.project.domain.repository.UserSettingsRepository

class UserSettingsRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : UserSettingsRepository {

    companion object {
        val KEY_CURRENCY = stringPreferencesKey("currency_symbol")
        val KEY_DARK_THEME = booleanPreferencesKey("dark_theme")
        val KEY_FIRST_DAY = intPreferencesKey("first_day_of_week")
        val KEY_MONTHLY_BUDGET = doublePreferencesKey("monthly_budget_ceiling")
        val KEY_EMERGENCY_MONTHS = intPreferencesKey("emergency_fund_months")
        val KEY_FIRE_MULTIPLIER = intPreferencesKey("fire_multiplier")
        val KEY_CURRENT_AGE = intPreferencesKey("current_age")
        val KEY_RETIREMENT_AGE = intPreferencesKey("retirement_age")
        val KEY_RETURN_EQUITY = doublePreferencesKey("return_equity")
        val KEY_RETURN_DEBT = doublePreferencesKey("return_debt")
        val KEY_RETURN_GOLD = doublePreferencesKey("return_gold")
        val KEY_RETURN_FD = doublePreferencesKey("return_fd")
        val KEY_ALLOC_EQUITY = doublePreferencesKey("alloc_equity")
        val KEY_ALLOC_DEBT = doublePreferencesKey("alloc_debt")
        val KEY_ALLOC_GOLD = doublePreferencesKey("alloc_gold")
        val KEY_ALLOC_FD = doublePreferencesKey("alloc_fd")
        val KEY_INFLATION = doublePreferencesKey("inflation_rate")
        val KEY_SALARY_GROWTH = doublePreferencesKey("salary_growth_rate")
        val KEY_TAX_RATE = doublePreferencesKey("tax_rate")
    }

    override fun getUserSettings(): Flow<UserSettings> = dataStore.data.map { prefs ->
        UserSettings(
            currencySymbol = prefs[KEY_CURRENCY] ?: "₹",
            isDarkTheme = prefs[KEY_DARK_THEME] ?: false,
            firstDayOfWeek = prefs[KEY_FIRST_DAY] ?: 1,
            monthlyBudgetCeiling = prefs[KEY_MONTHLY_BUDGET] ?: 40_000.0,
            emergencyFundMonths = prefs[KEY_EMERGENCY_MONTHS] ?: 6,
            fireMultiplier = prefs[KEY_FIRE_MULTIPLIER] ?: 25,
            currentAge = prefs[KEY_CURRENT_AGE] ?: 30,
            retirementAge = prefs[KEY_RETIREMENT_AGE] ?: 60,
            expectedReturnEquity = prefs[KEY_RETURN_EQUITY] ?: 12.0,
            expectedReturnDebt = prefs[KEY_RETURN_DEBT] ?: 7.0,
            expectedReturnGold = prefs[KEY_RETURN_GOLD] ?: 8.0,
            expectedReturnFD = prefs[KEY_RETURN_FD] ?: 7.0,
            allocationEquity = prefs[KEY_ALLOC_EQUITY] ?: 60.0,
            allocationDebt = prefs[KEY_ALLOC_DEBT] ?: 10.0,
            allocationGold = prefs[KEY_ALLOC_GOLD] ?: 10.0,
            allocationFD = prefs[KEY_ALLOC_FD] ?: 20.0,
            inflationRate = prefs[KEY_INFLATION] ?: 6.0,
            salaryGrowthRate = prefs[KEY_SALARY_GROWTH] ?: 8.0,
            taxRate = prefs[KEY_TAX_RATE] ?: 20.0
        )
    }

    override suspend fun updateCurrencySymbol(symbol: String) {
        dataStore.edit { it[KEY_CURRENCY] = symbol }
    }

    override suspend fun updateDarkTheme(isDark: Boolean) {
        dataStore.edit { it[KEY_DARK_THEME] = isDark }
    }

    override suspend fun updateFirstDayOfWeek(day: Int) {
        dataStore.edit { it[KEY_FIRST_DAY] = day }
    }

    override suspend fun updateMonthlyBudgetCeiling(amount: Double) {
        dataStore.edit { it[KEY_MONTHLY_BUDGET] = amount }
    }

    override suspend fun updateEmergencyFundMonths(months: Int) {
        dataStore.edit { it[KEY_EMERGENCY_MONTHS] = months }
    }

    override suspend fun updateFireMultiplier(multiplier: Int) {
        dataStore.edit { it[KEY_FIRE_MULTIPLIER] = multiplier }
    }

    override suspend fun updateAges(currentAge: Int, retirementAge: Int) {
        dataStore.edit {
            it[KEY_CURRENT_AGE] = currentAge
            it[KEY_RETIREMENT_AGE] = retirementAge
        }
    }

    override suspend fun updateExpectedReturns(equity: Double, debt: Double, gold: Double, fd: Double) {
        dataStore.edit {
            it[KEY_RETURN_EQUITY] = equity
            it[KEY_RETURN_DEBT] = debt
            it[KEY_RETURN_GOLD] = gold
            it[KEY_RETURN_FD] = fd
        }
    }

    override suspend fun updateAllocations(equity: Double, debt: Double, gold: Double, fd: Double) {
        dataStore.edit {
            it[KEY_ALLOC_EQUITY] = equity
            it[KEY_ALLOC_DEBT] = debt
            it[KEY_ALLOC_GOLD] = gold
            it[KEY_ALLOC_FD] = fd
        }
    }

    override suspend fun updateGrowthRates(inflation: Double, salaryGrowth: Double, taxRate: Double) {
        dataStore.edit {
            it[KEY_INFLATION] = inflation
            it[KEY_SALARY_GROWTH] = salaryGrowth
            it[KEY_TAX_RATE] = taxRate
        }
    }

    override suspend fun resetToDefaults() {
        dataStore.edit { it.clear() }
    }
}
