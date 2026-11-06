package com.bolivia.platica.ui.screens.cuentas

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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bolivia.platica.ui.components.Fechas
import com.bolivia.platica.ui.components.MoneyFormat
import com.bolivia.platica.ui.theme.platica

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CuentaDetalleScreen(
    onVolver: () -> Unit,
    viewModel: CuentaDetalleViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val platica = MaterialTheme.platica
    val cuenta = state.cuenta

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(cuenta?.nombre ?: "Cuenta", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = platica.heroFondo),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(Modifier.padding(24.dp)) {
                        Text(
                            "SALDO ACTUAL",
                            color = platica.heroTextoSecundario,
                            style = MaterialTheme.typography.labelSmall
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            MoneyFormat.formatear(state.saldo),
                            color = platica.heroTexto,
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "${cuenta?.tipo?.etiqueta ?: ""} · ${state.movimientos.size} movimiento(s)",
                            color = platica.heroTextoSecundario,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }

            item {
                Card(
                    Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Fila("Saldo inicial", MoneyFormat.formatear(cuenta?.saldoInicial ?: 0.0))
                        Spacer(Modifier.height(8.dp))
                        Fila("Ingresos", "+ ${MoneyFormat.formatear(state.ingresos)}")
                        Spacer(Modifier.height(8.dp))
                        Fila("Gastos", "- ${MoneyFormat.formatear(state.gastos)}")
                    }
                }
            }

            item {
                Text(
                    "MOVIMIENTOS DE ESTA CUENTA",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (state.movimientos.isEmpty()) {
                item {
                    Text(
                        "Esta cuenta todavía no tiene movimientos.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(state.movimientos, key = { it.transaccion.id }) { item ->
                    val t = item.transaccion
                    val esIngreso = t.tipo == com.bolivia.platica.domain.model.enums.TransactionType.INGRESO
                    Column(Modifier.padding(vertical = 8.dp)) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(
                                    t.nota ?: item.categoria ?: t.tipo.etiqueta,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    "${t.tipo.etiqueta} · ${Fechas.dia(t.fecha)} · ${Fechas.hora(t.fecha)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    item.categoria ?: "Sin categoría",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                MoneyFormat.formatearConSigno(t.monto, esIngreso),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                    }
                }
            }

            item { Spacer(Modifier.height(30.dp)) }
        }
    }
}

@Composable
private fun Fila(etiqueta: String, valor: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(etiqueta, style = MaterialTheme.typography.bodyLarge)
        Text(valor, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
    }
}
