package org.example.project.domain.usecase.transaction

import org.example.project.domain.model.Transaction
import org.example.project.domain.repository.TransactionRepository

class AddTransactionUseCase(private val repository: TransactionRepository) {
    suspend operator fun invoke(transaction: Transaction): Long {
        require(transaction.amount > 0) { "Amount must be positive" }
        return repository.insertTransaction(transaction)
    }
}
