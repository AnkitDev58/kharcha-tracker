package org.example.project.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import org.example.project.database.entity.InvestmentEntity

@Dao
interface InvestmentDao {

    @Query("SELECT * FROM investments ORDER BY investDate DESC")
    fun getAllInvestments(): Flow<List<InvestmentEntity>>

    @Query("SELECT * FROM investments WHERE id = :id")
    suspend fun getInvestmentById(id: Long): InvestmentEntity?

    @Query("SELECT SUM(investedAmount) FROM investments")
    suspend fun getTotalInvested(): Double?

    @Query("SELECT SUM(currentValue) FROM investments")
    suspend fun getTotalCurrentValue(): Double?

    @Query("SELECT * FROM investments WHERE investmentType = :type ORDER BY investDate DESC")
    fun getInvestmentsByType(type: String): Flow<List<InvestmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvestment(entity: InvestmentEntity): Long

    @Update
    suspend fun updateInvestment(entity: InvestmentEntity)

    @Delete
    suspend fun deleteInvestment(entity: InvestmentEntity)

    @Query("DELETE FROM investments WHERE id = :id")
    suspend fun deleteInvestmentById(id: Long)
}
