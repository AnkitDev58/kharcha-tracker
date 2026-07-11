package org.example.project.domain.usecase.transaction

import kotlinx.coroutines.flow.Flow
import org.example.project.domain.model.Transaction
import org.example.project.domain.model.TransactionType
import org.example.project.domain.repository.TransactionRepository

class GetTransactionsUseCase(private val repository: TransactionRepository) {
    fun all(): Flow<List<Transaction>> = repository.getAllTransactions()
    fun latest10Transactions(): Flow<List<Transaction>> = repository.getLatest10Transactions()
    fun byType(type: TransactionType): Flow<List<Transaction>> =
        repository.getTransactionsByType(type)

    fun byDateRange(start: String, end: String): Flow<List<Transaction>> =
        repository.getTransactionsByDateRange(start, end)

    fun byCategory(categoryId: Long): Flow<List<Transaction>> =
        repository.getTransactionsByCategory(categoryId)

    fun search(query: String): Flow<List<Transaction>> = repository.searchTransactions(query)
    fun favorites(): Flow<List<Transaction>> = repository.getFavoriteTransactions()
    fun paged(
        limit: Int,
        offset: Int,
        startDate: String?,
        endDate: String?
    ): Flow<List<Transaction>> = repository.getTransactionsPaged(limit, offset, startDate, endDate)

    suspend fun count(): Int = repository.getTransactionCount()
}
