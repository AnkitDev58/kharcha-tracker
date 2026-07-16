package org.example.project.domain.usecase.investment

import org.example.project.domain.repository.InvestmentRepository

class DeleteInvestmentUseCase(private val repository: InvestmentRepository) {
    suspend operator fun invoke(id: Long) = repository.deleteInvestmentById(id)
}
