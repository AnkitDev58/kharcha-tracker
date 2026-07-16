package org.example.project.di

import org.example.project.domain.usecase.annualexpense.AddAnnualExpenseUseCase
import org.example.project.domain.usecase.annualexpense.DeleteAnnualExpenseUseCase
import org.example.project.domain.usecase.annualexpense.GetAnnualExpensesUseCase
import org.example.project.domain.usecase.annualexpense.UpdateAnnualExpenseUseCase
import org.example.project.domain.usecase.budget.AddBudgetUseCase
import org.example.project.domain.usecase.budget.GetBudgetsWithSpendingUseCase
import org.example.project.domain.usecase.goals.AddContributionUseCase
import org.example.project.domain.usecase.goals.AddSavingsGoalUseCase
import org.example.project.domain.usecase.goals.DeleteSavingsGoalUseCase
import org.example.project.domain.usecase.goals.GetSavingsGoalsUseCase
import org.example.project.domain.usecase.goals.UpdateSavingsGoalUseCase
import org.example.project.domain.usecase.investment.AddInvestmentUseCase
import org.example.project.domain.usecase.investment.DeleteInvestmentUseCase
import org.example.project.domain.usecase.investment.GetInvestmentsUseCase
import org.example.project.domain.usecase.investment.UpdateInvestmentUseCase
import org.example.project.domain.usecase.loan.AddLoanUseCase
import org.example.project.domain.usecase.loan.DeleteLoanUseCase
import org.example.project.domain.usecase.loan.GetLoansUseCase
import org.example.project.domain.usecase.loan.UpdateLoanUseCase
import org.example.project.domain.usecase.reports.GetMonthlyReportUseCase
import org.example.project.domain.usecase.settings.GetUserSettingsUseCase
import org.example.project.domain.usecase.settings.UpdateUserSettingsUseCase
import org.example.project.domain.usecase.summary.GetCategorySummaryUseCase
import org.example.project.domain.usecase.summary.GetFinancialSummaryUseCase
import org.example.project.domain.usecase.transaction.AddTransactionUseCase
import org.example.project.domain.usecase.transaction.DeleteTransactionUseCase
import org.example.project.domain.usecase.transaction.GetTransactionsUseCase
import org.example.project.domain.usecase.transaction.UpdateTransactionUseCase
import org.koin.dsl.module

val useCaseModule = module {
    // Transactions
    factory { AddTransactionUseCase(get()) }
    factory { UpdateTransactionUseCase(get()) }
    factory { DeleteTransactionUseCase(get()) }
    factory { GetTransactionsUseCase(get()) }

    // Summary
    factory { GetFinancialSummaryUseCase(get(), get()) }
    factory { GetCategorySummaryUseCase(get(), get()) }

    // Budget
    factory { AddBudgetUseCase(get()) }
    factory { GetBudgetsWithSpendingUseCase(get(), get(), get()) }

    // Savings Goals
    factory { GetSavingsGoalsUseCase(get()) }
    factory { AddSavingsGoalUseCase(get()) }
    factory { UpdateSavingsGoalUseCase(get()) }
    factory { DeleteSavingsGoalUseCase(get()) }
    factory { AddContributionUseCase(get()) }

    // Annual Expenses
    factory { GetAnnualExpensesUseCase(get()) }
    factory { AddAnnualExpenseUseCase(get()) }
    factory { UpdateAnnualExpenseUseCase(get()) }
    factory { DeleteAnnualExpenseUseCase(get()) }

    // Loans
    factory { GetLoansUseCase(get()) }
    factory { AddLoanUseCase(get()) }
    factory { UpdateLoanUseCase(get()) }
    factory { DeleteLoanUseCase(get()) }

    // Investments
    factory { GetInvestmentsUseCase(get()) }
    factory { AddInvestmentUseCase(get()) }
    factory { UpdateInvestmentUseCase(get()) }
    factory { DeleteInvestmentUseCase(get()) }

    // Reports
    factory { GetMonthlyReportUseCase(get(), get(), get(), get()) }

    // Settings
    factory { GetUserSettingsUseCase(get()) }
    factory { UpdateUserSettingsUseCase(get()) }
}
