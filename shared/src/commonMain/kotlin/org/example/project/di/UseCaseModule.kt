package org.example.project.di

import org.example.project.domain.usecase.budget.AddBudgetUseCase
import org.example.project.domain.usecase.budget.GetBudgetsWithSpendingUseCase
import org.example.project.domain.usecase.goals.AddContributionUseCase
import org.example.project.domain.usecase.goals.AddSavingsGoalUseCase
import org.example.project.domain.usecase.goals.DeleteSavingsGoalUseCase
import org.example.project.domain.usecase.goals.GetSavingsGoalsUseCase
import org.example.project.domain.usecase.goals.UpdateSavingsGoalUseCase
import org.example.project.domain.usecase.summary.GetCategorySummaryUseCase
import org.example.project.domain.usecase.summary.GetFinancialSummaryUseCase
import org.example.project.domain.usecase.transaction.AddTransactionUseCase
import org.example.project.domain.usecase.transaction.DeleteTransactionUseCase
import org.example.project.domain.usecase.transaction.GetTransactionsUseCase
import org.example.project.domain.usecase.transaction.UpdateTransactionUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory { AddTransactionUseCase(get()) }
    factory { UpdateTransactionUseCase(get()) }
    factory { DeleteTransactionUseCase(get()) }
    factory { GetTransactionsUseCase(get()) }
    factory { GetFinancialSummaryUseCase(get(), get()) }
    factory { GetCategorySummaryUseCase(get(), get()) }
    factory { AddBudgetUseCase(get()) }
    factory { GetBudgetsWithSpendingUseCase(get(), get(), get()) }
    // Savings goals
    factory { GetSavingsGoalsUseCase(get()) }
    factory { AddSavingsGoalUseCase(get()) }
    factory { UpdateSavingsGoalUseCase(get()) }
    factory { DeleteSavingsGoalUseCase(get()) }
    factory { AddContributionUseCase(get()) }
}
