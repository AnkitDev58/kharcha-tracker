package org.example.project.di

import org.example.project.data.repository.AnnualExpenseRepositoryImpl
import org.example.project.data.repository.BudgetRepositoryImpl
import org.example.project.data.repository.CategoryRepositoryImpl
import org.example.project.data.repository.InvestmentRepositoryImpl
import org.example.project.data.repository.LoanRepositoryImpl
import org.example.project.data.repository.SavingsGoalRepositoryImpl
import org.example.project.data.repository.TransactionRepositoryImpl
import org.example.project.data.repository.UserSettingsRepositoryImpl
import org.example.project.domain.repository.AnnualExpenseRepository
import org.example.project.domain.repository.BudgetRepository
import org.example.project.domain.repository.CategoryRepository
import org.example.project.domain.repository.InvestmentRepository
import org.example.project.domain.repository.LoanRepository
import org.example.project.domain.repository.SavingsGoalRepository
import org.example.project.domain.repository.TransactionRepository
import org.example.project.domain.repository.UserSettingsRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<TransactionRepository> { TransactionRepositoryImpl(get()) }
    single<CategoryRepository> { CategoryRepositoryImpl(get()) }
    single<BudgetRepository> { BudgetRepositoryImpl(get()) }
    single<SavingsGoalRepository> { SavingsGoalRepositoryImpl(get()) }
    single<AnnualExpenseRepository> { AnnualExpenseRepositoryImpl(get()) }
    single<LoanRepository> { LoanRepositoryImpl(get()) }
    single<InvestmentRepository> { InvestmentRepositoryImpl(get()) }
    single<UserSettingsRepository> { UserSettingsRepositoryImpl(get()) }
}
