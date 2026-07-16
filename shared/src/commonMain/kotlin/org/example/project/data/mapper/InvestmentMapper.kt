package org.example.project.data.mapper

import org.example.project.database.entity.InvestmentEntity
import org.example.project.domain.model.Investment
import org.example.project.domain.model.InvestmentType

fun InvestmentEntity.toDomain(): Investment = Investment(
    id = id,
    name = name,
    investmentType = runCatching { InvestmentType.valueOf(investmentType) }.getOrDefault(InvestmentType.OTHER),
    investedAmount = investedAmount,
    currentValue = currentValue,
    investDate = investDate,
    maturityDate = maturityDate,
    notes = notes,
    isSIP = isSIP,
    sipMonthlyAmount = sipMonthlyAmount
)

fun Investment.toEntity(): InvestmentEntity = InvestmentEntity(
    id = id,
    name = name,
    investmentType = investmentType.name,
    investedAmount = investedAmount,
    currentValue = currentValue,
    investDate = investDate,
    maturityDate = maturityDate,
    notes = notes,
    isSIP = isSIP,
    sipMonthlyAmount = sipMonthlyAmount
)
