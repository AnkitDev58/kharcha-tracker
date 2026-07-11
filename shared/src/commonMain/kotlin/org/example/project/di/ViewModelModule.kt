package org.example.project.di

import org.example.project.ui.addtransaction.AddEditTransactionViewModel
import org.example.project.ui.budget.BudgetViewModel
import org.example.project.ui.goals.GoalsViewModel
import org.example.project.ui.home.HomeViewModel
import org.example.project.ui.statistics.StatisticsViewModel
import org.example.project.ui.transactions.TransactionsViewModel
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::TransactionsViewModel)
    viewModelOf(::AddEditTransactionViewModel)
    viewModelOf(::BudgetViewModel)
    viewModelOf(::StatisticsViewModel)
    viewModelOf(::GoalsViewModel)
}
