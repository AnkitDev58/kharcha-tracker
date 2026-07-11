package org.example.project.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import org.example.project.database.dao.BudgetDao
import org.example.project.database.dao.CategoryDao
import org.example.project.database.dao.SavingsGoalDao
import org.example.project.database.dao.TransactionDao
import org.example.project.database.entity.BudgetEntity
import org.example.project.database.entity.CategoryEntity
import org.example.project.database.entity.SavingsGoalEntity
import org.example.project.database.entity.TransactionEntity

@Database(
    entities = [
        TransactionEntity::class,
        CategoryEntity::class,
        BudgetEntity::class,
        SavingsGoalEntity::class
    ],
    version = 1,
    exportSchema = true
)
@ConstructedBy(TrackerDatabaseConstructor::class)
abstract class TrackerDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun budgetDao(): BudgetDao
    abstract fun savingsGoalDao(): SavingsGoalDao
}

const val DB_NAME = "tracker_database.db"
