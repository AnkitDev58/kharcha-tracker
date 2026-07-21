package org.example.project.database.dao

import androidx.room3.*
import kotlinx.coroutines.flow.Flow
import org.example.project.database.entity.LoanEntity

@Dao
interface LoanDao {

    @Query("SELECT * FROM loans ORDER BY startDate DESC")
    fun getAllLoans(): Flow<List<LoanEntity>>

    @Query("SELECT * FROM loans WHERE id = :id")
    suspend fun getLoanById(id: Long): LoanEntity?

    @Query("SELECT SUM(outstandingBalance) FROM loans")
    suspend fun getTotalOutstanding(): Double?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoan(entity: LoanEntity): Long

    @Update
    suspend fun updateLoan(entity: LoanEntity)

    @Delete
    suspend fun deleteLoan(entity: LoanEntity)

    @Query("DELETE FROM loans WHERE id = :id")
    suspend fun deleteLoanById(id: Long)
}
