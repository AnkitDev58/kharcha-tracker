package org.example.project.domain.usecase.investment

import org.example.project.domain.model.Investment
import org.example.project.domain.repository.InvestmentRepository

class AddInvestmentUseCase(private val repository: InvestmentRepository) {
    suspend operator fun invoke(investment: Investment): Long =
        repository.insertInvestment(investment)
}
