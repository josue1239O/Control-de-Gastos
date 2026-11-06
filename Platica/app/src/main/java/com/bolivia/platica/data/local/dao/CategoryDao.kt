package com.bolivia.platica.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.bolivia.platica.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Query("SELECT * FROM categories WHERE activa = 1 ORDER BY nombre ASC")
    fun observarActivas(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(category: CategoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarTodas(categories: List<CategoryEntity>)

    @Update
    suspend fun actualizar(category: CategoryEntity)

    @Delete
    suspend fun eliminar(category: CategoryEntity)

    @Query("SELECT COUNT(*) FROM categories")
    suspend fun contar(): Int
}
