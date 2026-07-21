package org.example.project.ui.navigation

import kotlinx.serialization.Serializable

// ── Auth destinations ──────────────────────────────────────────────────────────
@Serializable data object LoginRoute
@Serializable data class OtpRoute(val identifier: String)

// ── Bottom nav destinations ────────────────────────────────────────────────────
@Serializable data object HomeRoute
@Serializable data class TransactionsRoute(val date: String? = null)
@Serializable data object StatisticsRoute
@Serializable data object BudgetRoute
@Serializable data object GoalsRoute

// ── Top-level feature screens ─────────────────────────────────────────────────
@Serializable data object SettingsRoute
@Serializable data object CalendarRoute
@Serializable data object ReportsRoute
@Serializable data object AnnualExpenseRoute
@Serializable data object LoanRoute
@Serializable data object InvestmentRoute

// ── Detail / add-edit destinations ────────────────────────────────────────────
@Serializable data class AddEditTransactionRoute(val transactionId: Long = -1L)
@Serializable data object AddEditCategoryRoute
@Serializable data class CategoryDetailRoute(val categoryId: Long)
@Serializable data class AddEditGoalRoute(val goalId: Long = -1L)
