package org.example.project.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

@Serializable
data class TransactionsRoute(val date: String?=null)

@Serializable
data object StatisticsRoute

@Serializable
data object BudgetRoute

@Serializable
data object SettingsRoute

@Serializable
data class AddEditTransactionRoute(val transactionId: Long = -1L)

@Serializable
data object AddEditCategoryRoute

@Serializable
data class CategoryDetailRoute(val categoryId: Long)

@Serializable
data object GoalsRoute

@Serializable
data class AddEditGoalRoute(val goalId: Long = -1L)

@Serializable
data object CalendarRoute

@Serializable
data object ReportsRoute
