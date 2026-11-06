package com.bolivia.platica.domain.repository

import com.bolivia.platica.data.local.entity.QuickTemplateEntity
import kotlinx.coroutines.flow.Flow

interface QuickTemplateRepository {
    fun observarTodas(): Flow<List<QuickTemplateEntity>>
    suspend fun contar(): Int
}
