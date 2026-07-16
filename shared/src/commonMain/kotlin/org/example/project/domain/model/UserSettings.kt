package org.example.project.domain.model

data class UserSettings(
    val currencySymbol: String = "₹",
    val isDarkTheme: Boolean = false,
    val firstDayOfWeek: Int = 1, // 1 = Monday, 7 = Sunday
    val monthlyBudgetCeiling: Double = 40_000.0,
    // Emergency fund
    val emergencyFundMonths: Int = 6,
    // FIRE
    val fireMultiplier: Int = 25,
    val currentAge: Int = 30,
    val retirementAge: Int = 60,
    // Expected returns
    val expectedReturnEquity: Double = 12.0, // %
    val expectedReturnDebt: Double = 7.0,
    val expectedReturnGold: Double = 8.0,
    val expectedReturnFD: Double = 7.0,
    // Allocation %
    val allocationEquity: Double = 60.0,
    val allocationDebt: Double = 10.0,
    val allocationGold: Double = 10.0,
    val allocationFD: Double = 20.0,
    // Growth rates
    val inflationRate: Double = 6.0,
    val salaryGrowthRate: Double = 8.0,
    val taxRate: Double = 20.0
)
