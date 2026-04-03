package com.nts.financemanager.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nts.financemanager.data.model.Budget
import com.nts.financemanager.data.model.Category
import com.nts.financemanager.data.model.Transaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Room database for the Finance Manager app.
 * Pre-populates default categories on first creation.
 */
@Database(
    entities = [Transaction::class, Budget::class, Category::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
    abstract fun budgetDao(): BudgetDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Default expense/income categories seeded on first launch.
         */
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
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Seed default categories on first creation
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
