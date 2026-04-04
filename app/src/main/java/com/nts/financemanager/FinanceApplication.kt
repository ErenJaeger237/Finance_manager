package com.nts.financemanager

import android.app.Application
import com.nts.financemanager.data.db.AppDatabase
import com.nts.financemanager.data.repository.FinanceRepository

class FinanceApplication : Application() {

    val database by lazy { AppDatabase.getDatabase(this) }

    val repository by lazy {
        FinanceRepository(
            transactionDao = database.transactionDao(),
            budgetDao = database.budgetDao(),
            categoryDao = database.categoryDao(),
            financialGoalDao = database.financialGoalDao()
        )
    }
}
