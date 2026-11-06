package com.bolivia.platica.ui.screens.movimientos

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bolivia.platica.ui.components.Fechas
import com.bolivia.platica.ui.components.MoneyFormat
import com.bolivia.platica.ui.components.SelectorMes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovimientosScreen(
    onNuevo: () -> Unit,
    onEditar: (Long) -> Unit,
    viewModel: MovimientosViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var movimientoEliminar by remember {
        mutableStateOf<com.bolivia.platica.data.local.entity.TransactionEntity?>(null)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Movimientos", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNuevo,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Nuevo movimiento")
            }
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            SelectorMes(
                nombreMes = state.nombreMes,
                onAnterior = { viewModel.mesAnterior() },
                onSiguiente = { viewModel.mesSiguiente() }
            )

            // Resumen del mes: deja claro cómo afectan los movimientos.
            Card(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = MaterialTheme.shapes.large
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            "INGRESOS",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            MoneyFormat.formatear(state.totalIngresos),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            "GASTOS",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            MoneyFormat.formatear(state.totalGastos),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            if (state.movimientos.isEmpty()) {
                Column(Modifier.padding(20.dp)) {
                    Text(
                        "No hay movimientos en ${state.nombreMes}. Tocá el botón + para registrar uno.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(Modifier.fillMaxSize()) {
                    items(state.movimientos, key = { it.transaccion.id }) { item ->
                        val t = item.transaccion
                        val esIngreso = t.tipo.name == "INGRESO"
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clickable { onEditar(t.id) }
                                .padding(start = 20.dp, end = 8.dp, top = 12.dp, bottom = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(
                                    t.nota ?: item.categoria ?: t.tipo.etiqueta,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    "${t.tipo.etiqueta} · ${Fechas.dia(t.fecha)} · ${Fechas.hora(t.fecha)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    "Cuenta: ${item.cuenta} · Categoría: ${item.categoria ?: "Sin categoría"}" +
                                            if (t.esIrregular) " · irregular" else "",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    MoneyFormat.formatearConSigno(t.monto, esIngreso),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                IconButton(onClick = { movimientoEliminar = t }) {
                                    Icon(
                                        Icons.Filled.Delete,
                                        contentDescription = "Eliminar",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                    }
                }
            }
        }
    }

    movimientoEliminar?.let { t ->
        AlertDialog(
            onDismissRequest = { movimientoEliminar = null },
            title = { Text("¿Eliminar movimiento?") },
            text = {
                Text(
                    "${t.tipo.etiqueta} de ${MoneyFormat.formatear(t.monto)}. " +
                            "El saldo de la cuenta se actualizará automáticamente."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.eliminar(t)
                        movimientoEliminar = null
                    }
                ) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { movimientoEliminar = null }) { Text("Cancelar") }
            }
        )
    }
}
