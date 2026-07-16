package org.example.project.domain.usecase.investment

import org.example.project.domain.model.Investment
import org.example.project.domain.repository.InvestmentRepository

class UpdateInvestmentUseCase(private val repository: InvestmentRepository) {
    suspend operator fun invoke(investment: Investment) =
        repository.updateInvestment(investment)
}
