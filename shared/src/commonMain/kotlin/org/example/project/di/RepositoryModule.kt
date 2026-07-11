package org.example.project.di

import org.example.project.data.repository.BudgetRepositoryImpl
import org.example.project.data.repository.CategoryRepositoryImpl
import org.example.project.data.repository.SavingsGoalRepositoryImpl
import org.example.project.data.repository.TransactionRepositoryImpl
import org.example.project.domain.repository.BudgetRepository
import org.example.project.domain.repository.CategoryRepository
import org.example.project.domain.repository.SavingsGoalRepository
import org.example.project.domain.repository.TransactionRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<TransactionRepository> { TransactionRepositoryImpl(get()) }
    single<CategoryRepository> { CategoryRepositoryImpl(get()) }
    single<BudgetRepository> { BudgetRepositoryImpl(get()) }
    single<SavingsGoalRepository> { SavingsGoalRepositoryImpl(get()) }
}
