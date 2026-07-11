package org.example.project.domain.usecase.transaction

import org.example.project.domain.repository.TransactionRepository

class DeleteTransactionUseCase(private val repository: TransactionRepository) {
    suspend operator fun invoke(id: Long) = repository.deleteTransactionById(id)
}
