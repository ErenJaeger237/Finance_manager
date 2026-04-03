package com.nts.financemanager

import android.app.Application
import com.nts.financemanager.data.db.AppDatabase
import com.nts.financemanager.data.repository.FinanceRepository

/**
 * Application class that initializes the database and repository.
 * Provides access to the repository for ViewModels.
 */
class FinanceApplication : Application() {

    val database by lazy { AppDatabase.getDatabase(this) }

    val repository by lazy {
        FinanceRepository(
            transactionDao = database.transactionDao(),
            budgetDao = database.budgetDao(),
            categoryDao = database.categoryDao()
        )
    }
}
