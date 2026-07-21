package org.example.project.database

import androidx.room3.AutoMigration
import androidx.room3.ConstructedBy
import androidx.room3.Database
import androidx.room3.RoomDatabase
import org.example.project.database.dao.AnnualExpenseDao
import org.example.project.database.dao.BudgetDao
import org.example.project.database.dao.CategoryDao
import org.example.project.database.dao.InvestmentDao
import org.example.project.database.dao.LoanDao
import org.example.project.database.dao.SavingsGoalDao
import org.example.project.database.dao.TransactionDao
import org.example.project.database.entity.AnnualExpenseEntity
import org.example.project.database.entity.BudgetEntity
import org.example.project.database.entity.CategoryEntity
import org.example.project.database.entity.InvestmentEntity
import org.example.project.database.entity.LoanEntity
import org.example.project.database.entity.SavingsGoalEntity
import org.example.project.database.entity.TransactionEntity

@Database(
    entities = [
        TransactionEntity::class,
        CategoryEntity::class,
        BudgetEntity::class,
        SavingsGoalEntity::class,
        AnnualExpenseEntity::class,
        LoanEntity::class,
        InvestmentEntity::class
    ],
    version = 2,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ]
)
@ConstructedBy(TrackerDatabaseConstructor::class)
abstract class TrackerDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun budgetDao(): BudgetDao
    abstract fun savingsGoalDao(): SavingsGoalDao
    abstract fun annualExpenseDao(): AnnualExpenseDao
    abstract fun loanDao(): LoanDao
    abstract fun investmentDao(): InvestmentDao
}

const val DB_NAME = "tracker_database.db"
