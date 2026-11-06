package com.bolivia.platica.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.bolivia.platica.domain.model.enums.CategoryType

/**
 * Categoría de ingreso o gasto. Editables por el usuario.
 * Ver `docs/04_modelo_de_datos.md` §3.2.
 */
@Entity(
    tableName = "categories",
    indices = [Index("tipo"), Index("activa"), Index(value = ["nombre", "tipo"], unique = true)]
)
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val tipo: CategoryType,
    /** true = gasto fijo/común; false = gasto variable. */
    val esFijo: Boolean = false,
    val icono: String? = null,
    val color: Int? = null,
    val activa: Boolean = true
)
