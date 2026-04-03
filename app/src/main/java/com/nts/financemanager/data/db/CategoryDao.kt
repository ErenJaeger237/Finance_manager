package com.nts.financemanager.data.db

import androidx.room.*
import com.nts.financemanager.data.model.Category
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Category operations.
 */
@Dao
interface CategoryDao {

    @Query("SELECT * FROM categories ORDER BY isDefault DESC, name ASC")
    fun getAllCategories(): Flow<List<Category>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(category: Category)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(categories: List<Category>)

    @Delete
    suspend fun delete(category: Category)
}
