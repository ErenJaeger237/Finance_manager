package com.nts.financemanager.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a monthly budget for a specific category.
 */
@Entity(tableName = "budgets")
data class Budget(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val category: String,
    val limitAmount: Double,
    val month: String // format: "2026-04"
)
