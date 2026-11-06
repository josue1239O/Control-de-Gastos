package com.bolivia.platica.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bolivia.platica.data.local.entity.QuickTemplateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuickTemplateDao {

    @Query("SELECT * FROM quick_templates ORDER BY orden ASC, id ASC")
    fun observarTodas(): Flow<List<QuickTemplateEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(plantilla: QuickTemplateEntity): Long

    @Query("SELECT COUNT(*) FROM quick_templates")
    suspend fun contar(): Int
}
