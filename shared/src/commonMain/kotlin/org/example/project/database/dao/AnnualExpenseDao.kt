package org.example.project.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import org.example.project.database.entity.AnnualExpenseEntity

@Dao
interface AnnualExpenseDao {

    @Query("SELECT * FROM annual_expenses ORDER BY dueMonth ASC")
    fun getAllAnnualExpenses(): Flow<List<AnnualExpenseEntity>>

    @Query("SELECT * FROM annual_expenses WHERE id = :id")
    suspend fun getAnnualExpenseById(id: Long): AnnualExpenseEntity?

    @Query("SELECT SUM(amount / CASE frequency WHEN 'MONTHLY' THEN 1 WHEN 'QUARTERLY' THEN 3 WHEN 'HALF_YEARLY' THEN 6 ELSE 12 END) FROM annual_expenses")
    suspend fun getTotalMonthlyReserveRequired(): Double?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnualExpense(entity: AnnualExpenseEntity): Long

    @Update
    suspend fun updateAnnualExpense(entity: AnnualExpenseEntity)

    @Delete
    suspend fun deleteAnnualExpense(entity: AnnualExpenseEntity)

    @Query("DELETE FROM annual_expenses WHERE id = :id")
    suspend fun deleteAnnualExpenseById(id: Long)
}
