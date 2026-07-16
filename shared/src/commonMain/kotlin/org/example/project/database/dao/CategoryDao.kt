package org.example.project.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import org.example.project.database.entity.CategoryEntity

@Dao
interface CategoryDao {

    @Query("SELECT * FROM categories WHERE isArchived = 0 ORDER BY name ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE isArchived = 0 ORDER BY name ASC")
    suspend fun getAllCategoriesOnce(): List<CategoryEntity>

    @Query("SELECT * FROM categories WHERE type = :type AND isArchived = 0 ORDER BY name ASC")
    fun getCategoriesByType(type: String): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getCategoryById(id: Long): CategoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)

    @Update
    suspend fun updateCategory(category: CategoryEntity)

    @Delete
    suspend fun deleteCategory(category: CategoryEntity)

    @Query("DELETE FROM categories WHERE id = :id")
    suspend fun deleteCategoryById(id: Long)

    @Query("SELECT COUNT(*) FROM categories")
    suspend fun getCategoryCount(): Int
}


/*
*
*
* WITH category_totals AS (
    SELECT
        categoryId,
        SUM(amount) AS total,
        COUNT(*) AS transactionCount
    FROM transactions
    WHERE
        type = 'EXPENSE'
        AND dateTime BETWEEN :startDate AND :endDate
    GROUP BY categoryId
),
grand_total AS (
    SELECT SUM(total) AS totalExpense
    FROM category_totals
)
SELECT
    c.*,
    ct.total,
    ct.transactionCount,
    (ct.total * 100.0 / gt.totalExpense) AS percentage
FROM category_totals ct
JOIN categories c
    ON c.id = ct.categoryId
CROSS JOIN grand_total gt
ORDER BY ct.total DESC;
*
* */
