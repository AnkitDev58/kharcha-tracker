package org.example.project.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import org.example.project.database.entity.TransactionEntity

@Dao
interface TransactionDao {

    @Query(
        """
         SELECT * FROM transactions
  WHERE (:startDate IS NULL OR DATE(dateTime) >= DATE(:startDate))
  AND (:endDate IS NULL OR DATE(dateTime) <= DATE(:endDate))
    ORDER BY dateTime DESC
    LIMIT :limit OFFSET :offset
    """
    )
    fun getTransactionsPaged(
        limit: Int = 50,
        offset: Int = 0,
        startDate: String?,
        endDate: String?
    ): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions ORDER BY dateTime DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions ORDER BY dateTime DESC LIMIT 10")
    fun getLatest10Transactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY dateTime DESC")
    fun getTransactionsByType(type: String): Flow<List<TransactionEntity>>

    @Query(
        """
        SELECT * FROM transactions 
        WHERE dateTime >= :startDate AND dateTime <= :endDate 
        ORDER BY dateTime DESC
    """
    )
    fun getTransactionsByDateRange(
        startDate: String,
        endDate: String
    ): Flow<List<TransactionEntity>>

    @Query(
        """
        SELECT * FROM transactions 
        WHERE categoryId = :categoryId 
        ORDER BY dateTime DESC
    """
    )
    fun getTransactionsByCategory(categoryId: Long): Flow<List<TransactionEntity>>

//    @Query(
//        """
//        SELECT * FROM transactions
//        WHERE (note LIKE :query OR amount LIKE :query OR amount LIKE :query)
//        ORDER BY dateTime DESC
//    """
//    )

    @Query(
        """
         SELECT t.*
        FROM transactions t
        LEFT JOIN categories c
        ON t.categoryId = c.id
        WHERE
            t.note LIKE :query
        OR CAST(t.amount AS TEXT) LIKE :query
        OR c.name LIKE :query
        ORDER BY t.dateTime DESC
    """
    )
    fun searchTransactions(query: String): Flow<List<TransactionEntity>>

    @Query(
        """
        SELECT SUM(amount) FROM transactions 
        WHERE type = 'INCOME' AND dateTime >= :startDate AND dateTime <= :endDate
    """
    )
    suspend fun getTotalIncomeForPeriod(startDate: String, endDate: String): Double?

    @Query(
        """
        SELECT SUM(amount) FROM transactions 
        WHERE type = 'EXPENSE' AND dateTime >= :startDate AND dateTime <= :endDate
    """
    )
    suspend fun getTotalExpenseForPeriod(startDate: String, endDate: String): Double?

    @Query(
        """
        SELECT SUM(amount) FROM transactions 
        WHERE type = 'EXPENSE' AND categoryId = :categoryId 
        AND dateTime >= :startDate AND dateTime <= :endDate
    """
    )
    suspend fun getSpentByCategory(categoryId: Long, startDate: String, endDate: String): Double?

    @Query(
        """
    SELECT
        substr(t.dateTime, 1, 10) AS dateTime,
        SUM(t.amount) AS amount,
        json_group_array(
            json_object(
                'id', c.id,
                'name', c.name,
                'icon', c.icon,
                'colorHex', c.colorHex,
                'type', c.type
            )
        ) AS categories
    FROM transactions t
    INNER JOIN categories c
        ON c.id = t.categoryId
    WHERE t.type = 'EXPENSE'
        AND t.dateTime BETWEEN :startDate AND :endDate
    GROUP BY
        substr(t.dateTime, 1, 10)
    ORDER BY
        dateTime ASC
"""
    )
    suspend fun getDailySpending(
        startDate: String,
        endDate: String
    ): List<DailySpendingResult>

    @Query(
        """
        SELECT categoryId, SUM(amount) as total
        FROM transactions
        WHERE type = 'EXPENSE' AND dateTime >= :startDate AND dateTime <= :endDate
        GROUP BY categoryId
        ORDER BY total DESC
    """
    )
    suspend fun getCategoryTotals(startDate: String, endDate: String): List<CategoryTotalResult>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: Long): TransactionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<TransactionEntity>)

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransactionById(id: Long)

    @Query("SELECT COUNT(*) FROM transactions")
    suspend fun getTransactionCount(): Int

    @Query(
        """
        SELECT * FROM transactions 
        WHERE isFavorite = 1 
        ORDER BY dateTime DESC
    """
    )
    fun getFavoriteTransactions(): Flow<List<TransactionEntity>>

    @Query(
        """
        SELECT * FROM transactions 
        WHERE dateTime >= :startDate AND dateTime <= :endDate
        AND type = :type
        ORDER BY amount DESC
        LIMIT 1
    """
    )
    suspend fun getHighestTransaction(
        startDate: String,
        endDate: String,
        type: String
    ): TransactionEntity?
}

data class DailySpendingResult(
    val dateTime: String,
    val amount: Double,
    val categories: String
)

data class CategoryTotalResult(
    val categoryId: Long,
    val total: Double
)
