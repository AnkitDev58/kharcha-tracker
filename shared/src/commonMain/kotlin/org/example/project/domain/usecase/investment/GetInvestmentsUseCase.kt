package org.example.project.domain.usecase.investment

import kotlinx.coroutines.flow.Flow
import org.example.project.domain.model.Investment
import org.example.project.domain.repository.InvestmentRepository

class GetInvestmentsUseCase(private val repository: InvestmentRepository) {
    operator fun invoke(): Flow<List<Investment>> = repository.getAllInvestments()
}
