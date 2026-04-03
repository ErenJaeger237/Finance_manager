package com.nts.financemanager.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a financial transaction (income or expense).
 */
@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val amount: Double,
    val type: TransactionType,
    val category: String,
    val date: Long, // epoch millis
    val note: String = ""
)
