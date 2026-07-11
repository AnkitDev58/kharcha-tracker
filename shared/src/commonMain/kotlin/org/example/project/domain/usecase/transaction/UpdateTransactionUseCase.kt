package org.example.project.domain.usecase.transaction

import org.example.project.domain.model.Transaction
import org.example.project.domain.repository.TransactionRepository

class UpdateTransactionUseCase(private val repository: TransactionRepository) {
    suspend operator fun invoke(transaction: Transaction) = repository.updateTransaction(transaction)
}
