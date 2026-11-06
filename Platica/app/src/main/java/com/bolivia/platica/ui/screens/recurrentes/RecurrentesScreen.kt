package com.bolivia.platica.ui.screens.recurrentes

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
import com.bolivia.platica.domain.model.enums.Frequency
import com.bolivia.platica.domain.model.enums.TransactionType
import com.bolivia.platica.ui.components.MoneyFormat

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun RecurrentesScreen(
    onVolver: () -> Unit,
    viewModel: RecurrentesViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var mostrandoDialogo by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Recurrentes", fontWeight = FontWeight.Bold) },
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
            ) { Icon(Icons.Filled.Add, contentDescription = "Nuevo recurrente") }
        }
    ) { padding ->
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (state.recurrentes.isEmpty()) {
                item {
                    Text(
                        "No hay transacciones recurrentes.\n\nAutomatizá tus gastos fijos que se repiten " +
                                "(plan de datos, suscripción) y se registrarán solos.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
            } else {
                items(state.recurrentes, key = { it.id }) { r ->
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
                                Text(r.nota ?: r.tipo.etiqueta, style = MaterialTheme.typography.bodyLarge)
                                Text(
                                    r.frecuencia.name.lowercase().replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(MoneyFormat.formatear(r.monto), fontWeight = FontWeight.Bold)
                            IconButton(onClick = { viewModel.eliminar(r) }) {
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
        DialogoNuevoRecurrente(
            cuentas = state.cuentas.map { it.id to it.nombre },
            categorias = state.categorias.map { it.id to it.nombre },
            onDismiss = { mostrandoDialogo = false },
            onGuardar = { monto, tipo, acc, cat, frec, nota ->
                viewModel.crear(monto, tipo, acc, cat, frec, nota)
                mostrandoDialogo = false
            }
        )
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun DialogoNuevoRecurrente(
    cuentas: List<Pair<Long, String>>,
    categorias: List<Pair<Long, String>>,
    onDismiss: () -> Unit,
    onGuardar: (Double, TransactionType, Long, Long?, Frequency, String?) -> Unit
) {
    var monto by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf(TransactionType.GASTO) }
    var accId by remember { mutableStateOf(cuentas.firstOrNull()?.first) }
    var catId by remember { mutableStateOf(categorias.firstOrNull()?.first) }
    var frec by remember { mutableStateOf(Frequency.MENSUAL) }
    var nota by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo recurrente") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ChipSel("Gasto", tipo == TransactionType.GASTO) { tipo = TransactionType.GASTO }
                    ChipSel("Ingreso", tipo == TransactionType.INGRESO) { tipo = TransactionType.INGRESO }
                }
                OutlinedTextField(
                    value = monto,
                    onValueChange = { monto = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Monto (Bs.)") },
                    singleLine = true
                )
                Text("Cuenta", style = MaterialTheme.typography.labelSmall)
                cuentas.forEach { (id, nombre) ->
                    if (id == accId) Text("• $nombre", fontWeight = FontWeight.SemiBold)
                    else Text(nombre, modifier = Modifier.clickable { accId = id })
                }
                OutlinedTextField(
                    value = nota,
                    onValueChange = { nota = it.take(40) },
                    label = { Text("Nombre / nota (ej: Plan de datos)") },
                    singleLine = true
                )
                Text("Frecuencia", style = MaterialTheme.typography.labelSmall)
                androidx.compose.foundation.layout.FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Frequency.entries.forEach { f ->
                        ChipSel(f.name.lowercase().replaceFirstChar { it.uppercase() }, frec == f) { frec = f }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val valor = monto.toDoubleOrNull()
                if (valor != null && valor > 0 && accId != null) {
                    onGuardar(valor, tipo, accId!!, catId, frec, nota.ifBlank { null })
                }
            }) { Text("Crear") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
private fun ChipSel(texto: String, activo: Boolean, onClick: () -> Unit) {
    val fondo = if (activo) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val color = if (activo) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    Text(
        texto,
        color = color,
        modifier = Modifier
            .background(fondo, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    )
}
