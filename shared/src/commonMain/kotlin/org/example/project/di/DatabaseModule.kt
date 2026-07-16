package org.example.project.di

import org.example.project.database.TrackerDatabase
import org.koin.dsl.module

val databaseModule = module {
    single { get<TrackerDatabase>().transactionDao() }
    single { get<TrackerDatabase>().categoryDao() }
    single { get<TrackerDatabase>().budgetDao() }
    single { get<TrackerDatabase>().savingsGoalDao() }
    single { get<TrackerDatabase>().annualExpenseDao() }
    single { get<TrackerDatabase>().loanDao() }
    single { get<TrackerDatabase>().investmentDao() }
}
