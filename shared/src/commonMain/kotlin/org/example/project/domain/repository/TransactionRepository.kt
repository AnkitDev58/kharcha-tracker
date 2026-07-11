package org.example.project.domain.repository

import kotlinx.coroutines.flow.Flow
import org.example.project.database.dao.CategoryTotalResult
import org.example.project.database.dao.DailySpendingResult
import org.example.project.domain.model.Transaction
import org.example.project.domain.model.TransactionType

interface TransactionRepository {
    fun getAllTransactions(): Flow<List<Transaction>>
    fun getLatest10Transactions(): Flow<List<Transaction>>
    fun getTransactionsByType(type: TransactionType): Flow<List<Transaction>>
    fun getTransactionsByDateRange(startDate: String, endDate: String): Flow<List<Transaction>>
    fun getTransactionsByCategory(categoryId: Long): Flow<List<Transaction>>
    fun searchTransactions(query: String): Flow<List<Transaction>>
    fun getFavoriteTransactions(): Flow<List<Transaction>>
    fun getTransactionsPaged( limit: Int  ,
                              offset: Int  ,
                              startDate: String?,
                              endDate: String?): Flow<List<Transaction>>
    suspend fun getTransactionById(id: Long): Transaction?
    suspend fun insertTransaction(transaction: Transaction): Long
    suspend fun updateTransaction(transaction: Transaction)
    suspend fun deleteTransaction(transaction: Transaction)
    suspend fun deleteTransactionById(id: Long)
    suspend fun getTotalIncomeForPeriod(startDate: String, endDate: String): Double
    suspend fun getTotalExpenseForPeriod(startDate: String, endDate: String): Double
    suspend fun getSpentByCategory(categoryId: Long, startDate: String, endDate: String): Double
    suspend fun getDailySpending(startDate: String, endDate: String): List<DailySpendingResult>
    suspend fun getCategoryTotals(startDate: String, endDate: String): List<CategoryTotalResult>
    suspend fun getHighestTransaction(startDate: String, endDate: String, type: TransactionType): Transaction?
    suspend fun getTransactionCount(): Int
}
