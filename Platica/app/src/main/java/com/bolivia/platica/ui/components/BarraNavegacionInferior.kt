package com.bolivia.platica.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bolivia.platica.ui.navigation.Destino

/**
 * Barra de navegación inferior minimalista en blanco y negro.
 *
 * - Fondo blanco (o de superficie) con una línea superior sutil.
 * - El ítem seleccionado muestra su icono relleno en negro y una barra
 *   indicadora arriba; los demás quedan en gris de contorno.
 */
@Composable
fun BarraNavegacionInferior(
    destinoActual: Destino,
    onSeleccionar: (Destino) -> Unit,
    modifier: Modifier = Modifier
) {
    val fondo = MaterialTheme.colorScheme.surface
    val borde = MaterialTheme.colorScheme.outline
    val colorActivo = MaterialTheme.colorScheme.onSurface
    val colorInactivo = MaterialTheme.colorScheme.onSurfaceVariant

    Column(modifier.fillMaxWidth().background(fondo)) {
        // Línea superior fina
        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(borde)
        )
        Row(
            Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Destino.inferiores.forEach { destino ->
                val seleccionado = destino == destinoActual
                ItemNavegacion(
                    destino = destino,
                    seleccionado = seleccionado,
                    colorActivo = colorActivo,
                    colorInactivo = colorInactivo,
                    modifier = Modifier.weight(1f),
                    onClick = { onSeleccionar(destino) }
                )
            }
        }
    }
}

@Composable
private fun ItemNavegacion(
    destino: Destino,
    seleccionado: Boolean,
    colorActivo: androidx.compose.ui.graphics.Color,
    colorInactivo: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val color = if (seleccionado) colorActivo else colorInactivo
    val escala by animateFloatAsState(if (seleccionado) 1f else 0.92f, label = "escala")

    Column(
        modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Barra indicadora del seleccionado
        Box(
            Modifier
                .width(18.dp)
                .height(3.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(if (seleccionado) colorActivo else androidx.compose.ui.graphics.Color.Transparent)
        )
        Spacer(Modifier.height(6.dp))
        Icon(
            imageVector = if (seleccionado) destino.iconoSeleccionado else destino.iconoNoSeleccionado,
            contentDescription = destino.etiqueta,
            tint = color,
            modifier = Modifier.size(24.dp * escala)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = destino.etiqueta,
            color = color,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal,
            maxLines = 1
        )
    }
}
