package org.example.project.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.example.project.data.mapper.toDomain
import org.example.project.data.mapper.toEntity
import org.example.project.database.dao.InvestmentDao
import org.example.project.domain.model.Investment
import org.example.project.domain.model.InvestmentType
import org.example.project.domain.repository.InvestmentRepository

class InvestmentRepositoryImpl(
    private val dao: InvestmentDao
) : InvestmentRepository {

    override fun getAllInvestments(): Flow<List<Investment>> =
        dao.getAllInvestments().map { it.map { e -> e.toDomain() } }

    override fun getInvestmentsByType(type: InvestmentType): Flow<List<Investment>> =
        dao.getInvestmentsByType(type.name).map { it.map { e -> e.toDomain() } }

    override suspend fun getInvestmentById(id: Long): Investment? =
        dao.getInvestmentById(id)?.toDomain()

    override suspend fun getTotalInvested(): Double =
        dao.getTotalInvested() ?: 0.0

    override suspend fun getTotalCurrentValue(): Double =
        dao.getTotalCurrentValue() ?: 0.0

    override suspend fun insertInvestment(investment: Investment): Long =
        dao.insertInvestment(investment.toEntity())

    override suspend fun updateInvestment(investment: Investment) =
        dao.updateInvestment(investment.toEntity())

    override suspend fun deleteInvestmentById(id: Long) =
        dao.deleteInvestmentById(id)
}
