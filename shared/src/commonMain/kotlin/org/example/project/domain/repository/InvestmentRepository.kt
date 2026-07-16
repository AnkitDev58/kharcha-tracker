package org.example.project.domain.repository

import kotlinx.coroutines.flow.Flow
import org.example.project.domain.model.Investment
import org.example.project.domain.model.InvestmentType

interface InvestmentRepository {
    fun getAllInvestments(): Flow<List<Investment>>
    fun getInvestmentsByType(type: InvestmentType): Flow<List<Investment>>
    suspend fun getInvestmentById(id: Long): Investment?
    suspend fun getTotalInvested(): Double
    suspend fun getTotalCurrentValue(): Double
    suspend fun insertInvestment(investment: Investment): Long
    suspend fun updateInvestment(investment: Investment)
    suspend fun deleteInvestmentById(id: Long)
}
