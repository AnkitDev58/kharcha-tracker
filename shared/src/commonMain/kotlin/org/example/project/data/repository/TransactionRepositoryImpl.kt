package org.example.project.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.example.project.data.mapper.toDomain
import org.example.project.data.mapper.toEntity
import org.example.project.database.dao.CategoryTotalResult
import org.example.project.database.dao.DailySpendingResult
import org.example.project.database.dao.TransactionDao
import org.example.project.domain.model.Transaction
import org.example.project.domain.model.TransactionType
import org.example.project.domain.repository.TransactionRepository

class TransactionRepositoryImpl(
    private val transactionDao: TransactionDao
) : TransactionRepository {

    override fun getAllTransactions(): Flow<List<Transaction>> =
        transactionDao.getAllTransactions().map { list -> list.map { it.toDomain() } }

    override fun getLatest10Transactions(): Flow<List<Transaction>> =
        transactionDao.getLatest10Transactions().map { list -> list.map { it.toDomain() } }

    override fun getTransactionsByType(type: TransactionType): Flow<List<Transaction>> =
        transactionDao.getTransactionsByType(type.name).map { list -> list.map { it.toDomain() } }

    override fun getTransactionsByDateRange(
        startDate: String,
        endDate: String
    ): Flow<List<Transaction>> =
        transactionDao.getTransactionsByDateRange(startDate, endDate)
            .map { list -> list.map { it.toDomain() } }

    override fun getTransactionsByCategory(categoryId: Long): Flow<List<Transaction>> =
        transactionDao.getTransactionsByCategory(categoryId)
            .map { list -> list.map { it.toDomain() } }

    override fun searchTransactions(query: String): Flow<List<Transaction>> =
        transactionDao.searchTransactions("%$query%").map { list -> list.map { it.toDomain() } }

    override fun getFavoriteTransactions(): Flow<List<Transaction>> =
        transactionDao.getFavoriteTransactions().map { list -> list.map { it.toDomain() } }

    override fun getTransactionsPaged(
        limit: Int,
        offset: Int,
        startDate: String?,
        endDate: String?
    ): Flow<List<Transaction>>  = transactionDao.getTransactionsPaged(limit, offset,startDate,endDate)
        .map { list -> list.map { it.toDomain() } }

    override suspend fun getTransactionById(id: Long): Transaction? =
        transactionDao.getTransactionById(id)?.toDomain()

    override suspend fun insertTransaction(transaction: Transaction): Long =
        transactionDao.insertTransaction(transaction.toEntity())

    override suspend fun updateTransaction(transaction: Transaction) =
        transactionDao.updateTransaction(transaction.toEntity())

    override suspend fun deleteTransaction(transaction: Transaction) =
        transactionDao.deleteTransaction(transaction.toEntity())

    override suspend fun deleteTransactionById(id: Long) =
        transactionDao.deleteTransactionById(id)

    override suspend fun getTotalIncomeForPeriod(startDate: String, endDate: String): Double =
        transactionDao.getTotalIncomeForPeriod(startDate, endDate) ?: 0.0

    override suspend fun getTotalExpenseForPeriod(startDate: String, endDate: String): Double =
        transactionDao.getTotalExpenseForPeriod(startDate, endDate) ?: 0.0

    override suspend fun getSpentByCategory(
        categoryId: Long,
        startDate: String,
        endDate: String
    ): Double =
        transactionDao.getSpentByCategory(categoryId, startDate, endDate) ?: 0.0

    override suspend fun getDailySpending(
        startDate: String,
        endDate: String
    ): List<DailySpendingResult> =
        transactionDao.getDailySpending(startDate, endDate)

    override suspend fun getCategoryTotals(
        startDate: String,
        endDate: String
    ): List<CategoryTotalResult> =
        transactionDao.getCategoryTotals(startDate, endDate)

    override suspend fun getHighestTransaction(
        startDate: String,
        endDate: String,
        type: TransactionType
    ): Transaction? =
        transactionDao.getHighestTransaction(startDate, endDate, type.name)?.toDomain()

    override suspend fun getTransactionCount(): Int =
        transactionDao.getTransactionCount()
}
