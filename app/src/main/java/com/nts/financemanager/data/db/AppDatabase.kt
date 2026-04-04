package com.nts.financemanager.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nts.financemanager.data.model.Budget
import com.nts.financemanager.data.model.Category
import com.nts.financemanager.data.model.FinancialGoal
import com.nts.financemanager.data.model.Transaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Transaction::class, Budget::class, Category::class, FinancialGoal::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
    abstract fun budgetDao(): BudgetDao
    abstract fun categoryDao(): CategoryDao
    abstract fun financialGoalDao(): FinancialGoalDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `financial_goals` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `name` TEXT NOT NULL,
                        `targetAmount` REAL NOT NULL,
                        `savedAmount` REAL NOT NULL DEFAULT 0.0,
                        `deadline` INTEGER NOT NULL,
                        `createdAt` INTEGER NOT NULL,
                        `iconName` TEXT NOT NULL DEFAULT 'Savings'
                    )
                """.trimIndent())
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE transactions ADD COLUMN goalId INTEGER DEFAULT NULL")
            }
        }

        private val DEFAULT_CATEGORIES = listOf(
            Category(name = "Food", isDefault = true),
            Category(name = "Transport", isDefault = true),
            Category(name = "Rent", isDefault = true),
            Category(name = "Health", isDefault = true),
            Category(name = "Education", isDefault = true),
            Category(name = "Salary", isDefault = true),
            Category(name = "Business", isDefault = true),
            Category(name = "Other", isDefault = true)
        )

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "finance_manager_db"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            INSTANCE?.let { database ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    database.categoryDao().insertAll(DEFAULT_CATEGORIES)
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
