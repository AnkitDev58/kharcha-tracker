package org.example.project.data.mapper

import org.example.project.database.entity.LoanEntity
import org.example.project.domain.model.Loan
import org.example.project.domain.model.LoanType

fun LoanEntity.toDomain(): Loan = Loan(
    id = id,
    name = name,
    principal = principal,
    outstandingBalance = outstandingBalance,
    interestRatePercent = interestRatePercent,
    tenureMonths = tenureMonths,
    paidMonths = paidMonths,
    startDate = startDate,
    notes = notes,
    loanType = runCatching { LoanType.valueOf(loanType) }.getOrDefault(LoanType.PERSONAL)
)

fun Loan.toEntity(): LoanEntity = LoanEntity(
    id = id,
    name = name,
    principal = principal,
    outstandingBalance = outstandingBalance,
    interestRatePercent = interestRatePercent,
    tenureMonths = tenureMonths,
    paidMonths = paidMonths,
    startDate = startDate,
    notes = notes,
    loanType = loanType.name
)
