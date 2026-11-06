package com.bolivia.platica.ui.screens.analisis

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bolivia.platica.data.reporte.ParCategoria
import com.bolivia.platica.data.reporte.ParMeta
import com.bolivia.platica.data.reporte.ReporteMes
import com.bolivia.platica.data.reporte.ReporteMesManager
import com.bolivia.platica.domain.usecase.Analitica
import com.bolivia.platica.ui.components.MoneyFormat
import com.bolivia.platica.ui.components.SelectorMes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalisisScreen(viewModel: AnalisisViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val totalGastos = state.gastos
    val context = LocalContext.current

    val lanzadorPdf = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri ->
        if (uri != null) {
            val reporte = ReporteMes(
                nombreMes = state.nombreMes,
                ingresos = state.ingresos,
                gastos = state.gastos,
                balance = state.balance,
                tasaAhorro = state.tasaAhorro,
                gastoProyectado = state.gastoProyectado,
                categorias = state.porCategoria.map {
                    ParCategoria(
                        nombre = it.categoria ?: "Sin categoría",
                        monto = it.total,
                        porcentaje = Analitica.porcentaje(it.total, totalGastos)
                    )
                },
                metas = state.proyecciones.map { p ->
                    ParMeta(
                        nombre = p.meta.nombre,
                        objetivo = p.meta.montoObjetivo,
                        actual = state.saldoAhorro,
                        alcanzable = p.meta.montoObjetivo <= state.saldoAhorro
                    )
                }
            )
            ReporteMesManager.generarPdf(context, reporte, uri)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Análisis", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SelectorMes(
                nombreMes = state.nombreMes,
                onAnterior = viewModel::mesAnterior,
                onSiguiente = viewModel::mesSiguiente
            )

            // Botón descargar reporte
            Button(
                onClick = { lanzadorPdf.launch("Platica_${state.nombreMes.replace(" ", "_")}.pdf") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Filled.Download, contentDescription = null)
                Spacer(Modifier.height(0.dp))
                Text("  Descargar reporte del mes")
            }

            // Balance del ciclo
            Card(
                Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = MaterialTheme.shapes.large
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text(
                        "BALANCE DEL MES",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(12.dp))
                    Fila("Ingresos", MoneyFormat.formatear(state.ingresos))
                    Spacer(Modifier.height(6.dp))
                    Fila("Gastos", MoneyFormat.formatear(state.gastos))
                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                    Spacer(Modifier.height(12.dp))
                    Fila("Balance", MoneyFormat.formatear(state.balance), destacado = true)
                    Spacer(Modifier.height(6.dp))
                    val tasa = state.tasaAhorro
                    Fila("Tasa de ahorro", if (tasa == null) "—" else "${"%.1f".format(tasa)} %")
                    Spacer(Modifier.height(6.dp))
                    Fila("Gasto proyectado", MoneyFormat.formatear(state.gastoProyectado))
                    Spacer(Modifier.height(6.dp))
                    Fila("Balance proyectado", MoneyFormat.formatear(state.balanceProyectado), destacado = true)
                }
            }

            // Gasto por categoría
            Card(
                Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = MaterialTheme.shapes.large
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text(
                        "GASTO POR CATEGORÍA",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(12.dp))
                    if (state.porCategoria.isEmpty()) {
                        Text(
                            "Sin gastos registrados este mes.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        state.porCategoria.forEach { c ->
                            val pct = Analitica.porcentaje(c.total, totalGastos)
                            Column(Modifier.padding(vertical = 8.dp)) {
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(c.categoria ?: "Sin categoría")
                                    Text(
                                        "${"%.0f".format(pct)} % · ${MoneyFormat.formatear(c.total)}",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Spacer(Modifier.height(6.dp))
                                LinearProgressIndicator(
                                    progress = { (pct / 100.0).toFloat() },
                                    modifier = Modifier.fillMaxWidth(),
                                    color = MaterialTheme.colorScheme.primary,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Movimientos por cuenta: conecta cada movimiento con la cuenta que afectó
            Card(
                Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = MaterialTheme.shapes.large
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text(
                        "MOVIMIENTOS POR CUENTA",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(12.dp))
                    if (state.porCuenta.isEmpty()) {
                        Text(
                            "Sin movimientos registrados este mes.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        state.porCuenta.forEachIndexed { index, c ->
                            if (index > 0) {
                                Spacer(Modifier.height(10.dp))
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                                Spacer(Modifier.height(10.dp))
                            }
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(c.nombre, fontWeight = FontWeight.SemiBold)
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        "Ingresos +${MoneyFormat.formatear(c.ingresos)}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        "Gastos -${MoneyFormat.formatear(c.gastos)}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Proyección de metas
            Card(
                Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = MaterialTheme.shapes.large
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text(
                        "¿LLEGO A MIS METAS?",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(12.dp))
                    if (state.proyecciones.isEmpty()) {
                        Text(
                            "No tenés metas definidas. Creá una en la pestaña Metas.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        state.proyecciones.forEachIndexed { index, p ->
                            if (index > 0) {
                                Spacer(Modifier.height(12.dp))
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                                Spacer(Modifier.height(12.dp))
                            }
                            Column {
                                Text(
                                    "Quiero: ${p.meta.nombre}",
                                    fontWeight = FontWeight.SemiBold,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Spacer(Modifier.height(4.dp))
                                Text("Cuesta: ${MoneyFormat.formatear(p.meta.montoObjetivo)}")
                                Text(
                                    "Prioridad: ${p.meta.prioridad.etiqueta}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text("Tenés en Ahorro: ${MoneyFormat.formatear(state.saldoAhorro)} (${"%.0f".format(p.progreso)} %)")
                                Spacer(Modifier.height(4.dp))
                                val alcanzable = p.meta.montoObjetivo <= state.saldoAhorro
                                val txt = when {
                                    alcanzable -> "¡Ya la podés comprar con tu ahorro!"
                                    p.meta.fechaLimite != null && p.seCumple -> "A este ritmo llegás a tiempo."
                                    p.meta.fechaLimite != null -> "No alcanza a este ritmo. Guardá más por mes."
                                    p.mesesParaLograr != null -> "A este ritmo, en ~${p.mesesParaLograr} mes(es)."
                                    else -> "Empezá a ahorrar para avanzar."
                                }
                                Text(txt, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Fila(etiqueta: String, valor: String, destacado: Boolean = false) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(etiqueta, style = MaterialTheme.typography.bodyLarge)
        Text(
            valor,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (destacado) FontWeight.Bold else FontWeight.Normal
        )
    }
}
