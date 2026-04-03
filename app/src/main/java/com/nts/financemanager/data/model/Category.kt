package com.nts.financemanager.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a transaction category.
 * Default categories are seeded on first launch.
 * Users can add custom categories.
 */
@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val isDefault: Boolean = false
)
