package com.bolivia.platica.ui.screens.inversiones

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
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
import com.bolivia.platica.ui.components.MoneyFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InversionesScreen(
    onVolver: () -> Unit,
    viewModel: InversionesViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var mostrandoDialogo by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Inversiones", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { mostrandoDialogo = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) { Icon(Icons.Filled.Add, contentDescription = "Registrar ganancia") }
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
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Text(
                            "TU DINERO INVERTIDO",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(10.dp))
                        Fila("Capital invertido", MoneyFormat.formatear(state.capitalTotal))
                        Spacer(Modifier.height(6.dp))
                        Fila("Ganancia acumulada", MoneyFormat.formatear(state.gananciaTotal), true)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Tu dinero sigue circulando y generando ganancia.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                Text(
                    "HISTORIAL DE GANANCIAS",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (state.ganancias.isEmpty()) {
                item {
                    Text(
                        "Sin ganancias registradas. Tocá + para anotar cuánto generó tu inversión.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(state.ganancias, key = { it.id }) { g ->
                    Card(
                        Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(start = 20.dp, end = 8.dp, top = 12.dp, bottom = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(
                                    g.nota ?: "Ganancia",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    if (g.reinvertido > 0) "Reinvertido: ${MoneyFormat.formatear(g.reinvertido)}"
                                    else "Retirado",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                MoneyFormat.formatearConSigno(g.ganancia, g.ganancia >= 0),
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(onClick = { viewModel.eliminar(g) }) {
                                Icon(
                                    Icons.Filled.Delete,
                                    contentDescription = "Eliminar",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }

    if (mostrandoDialogo) {
        DialogoNuevaGanancia(
            cuentas = state.cuentasInversion.map { it.id to it.nombre },
            onDismiss = { mostrandoDialogo = false },
            onGuardar = { acc, gan, reinv, nota ->
                viewModel.registrarGanancia(acc, gan, reinv, nota)
                mostrandoDialogo = false
            }
        )
    }
}

@Composable
private fun Fila(etiqueta: String, valor: String, destacado: Boolean = false) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(etiqueta, style = MaterialTheme.typography.bodyLarge)
        Text(
            valor,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (destacado) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun DialogoNuevaGanancia(
    cuentas: List<Pair<Long, String>>,
    onDismiss: () -> Unit,
    onGuardar: (Long, Double, Double, String?) -> Unit
) {
    var ganancia by remember { mutableStateOf("") }
    var reinvertir by remember { mutableStateOf(true) }
    var nota by remember { mutableStateOf("") }
    var accId by remember { mutableStateOf(cuentas.firstOrNull()?.first) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Registrar ganancia") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (cuentas.isEmpty()) {
                    Text("No tenés cuentas de inversión. Creá una en Cuentas.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    Text("Cuenta", style = MaterialTheme.typography.labelSmall)
                    cuentas.forEach { (id, nombre) ->
                        val sel = id == accId
                        Text(
                            nombre,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (sel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { accId = id }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            color = if (sel) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    OutlinedTextField(
                        value = ganancia,
                        onValueChange = { ganancia = it.filter { c -> c.isDigit() || c == '.' || c == '-' } },
                        label = { Text("Ganancia (Bs.)") },
                        singleLine = true
                    )
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Lo reinvierto (sigue circulando)")
                        Switch(checked = reinvertir, onCheckedChange = { reinvertir = it })
                    }
                    OutlinedTextField(
                        value = nota,
                        onValueChange = { nota = it.take(40) },
                        label = { Text("Nota (opcional)") },
                        singleLine = true
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val g = ganancia.toDoubleOrNull()
                if (g != null && accId != null) {
                    onGuardar(accId!!, g, if (reinvertir) g else 0.0, nota.ifBlank { null })
                }
            }) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
