package com.nts.financemanager.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a financial savings goal.
 * Progress is auto-tracked from income transactions tagged to this goal.
 */
@Entity(tableName = "financial_goals")
data class FinancialGoal(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val targetAmount: Double,
    val savedAmount: Double = 0.0,
    val deadline: Long, // epoch millis
    val createdAt: Long = System.currentTimeMillis(),
    val iconName: String = "Savings"
)
