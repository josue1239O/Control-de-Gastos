package com.bolivia.platica.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Destinos de navegación principales de la app.
 * Cada destino tiene un icono relleno (seleccionado) y uno de contorno (no seleccionado).
 * Ver `docs/06_flujo_ui_ux.md` §2.
 */
enum class Destino(
    val ruta: String,
    val etiqueta: String,
    val iconoSeleccionado: ImageVector,
    val iconoNoSeleccionado: ImageVector
) {
    Inicio("inicio", "Inicio", Icons.Filled.Home, Icons.Outlined.Home),
    Movimientos("movimientos", "Movimientos", Icons.Filled.ReceiptLong, Icons.Outlined.ReceiptLong),
    Metas("metas", "Metas", Icons.Filled.Flag, Icons.Outlined.Flag),
    Analisis("analisis", "Análisis", Icons.Filled.Analytics, Icons.Outlined.Analytics),
    Ajustes("ajustes", "Ajustes", Icons.Filled.Settings, Icons.Outlined.Settings);

    companion object {
        val inferiores: List<Destino> = entries.toList()

        fun porRuta(ruta: String?): Destino =
            entries.firstOrNull { it.ruta == ruta } ?: Inicio
    }
}
