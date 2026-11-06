package com.bolivia.platica.ui.screens.metas

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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bolivia.platica.data.local.entity.SavingsGoalEntity
import com.bolivia.platica.domain.model.enums.GoalPriority
import com.bolivia.platica.domain.usecase.CalculadoraMetas
import com.bolivia.platica.ui.components.MoneyFormat
import com.bolivia.platica.ui.theme.platica

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetasScreen(viewModel: MetasViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val platica = MaterialTheme.platica
    var mostrandoDialogo by remember { mutableStateOf(false) }
    var metaEditando by remember { mutableStateOf<SavingsGoalEntity?>(null) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Metas", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    metaEditando = null
                    mostrandoDialogo = true
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Nueva meta")
            }
        }
    ) { padding ->
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // El saldo de Ahorro es la única fuente de verdad para las metas.
            item {
                Card(
                    Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = platica.heroFondo),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(Modifier.padding(24.dp)) {
                        Text(
                            "SALDO EN AHORRO",
                            color = platica.heroTextoSecundario,
                            style = MaterialTheme.typography.labelSmall
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            MoneyFormat.formatear(state.saldoAhorro),
                            color = platica.heroTexto,
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Tus metas se calculan solas con este saldo.",
                            color = platica.heroTextoSecundario,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }

            if (!state.hayCuentaAhorro) {
                item {
                    Card(
                        Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Text(
                            "Para usar las metas, creá una cuenta de tipo Ahorro en Ajustes → Cuentas. " +
                                    "Todo lo que guardes ahí se compara automáticamente con el precio de tus metas.",
                            Modifier.padding(16.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (state.metas.isEmpty()) {
                item {
                    Text(
                        "Todavía no tenés metas. Tocá + para crear la primera (ej: \"Comprar laptop\").",
                        Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(state.metas, key = { it.id }) { meta ->
                    val progreso = CalculadoraMetas.progreso(state.saldoAhorro, meta.montoObjetivo)
                    val restante = CalculadoraMetas.restante(state.saldoAhorro, meta.montoObjetivo)
                    val alcanzable = restante <= 0.0

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Column(Modifier.padding(20.dp)) {
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(Modifier.weight(1f)) {
                                    EtiquetaPrioridad(meta.prioridad)
                                    Spacer(Modifier.height(6.dp))
                                    Text(
                                        "Quiero: ${meta.nombre}",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    if (!meta.descripcion.isNullOrBlank()) {
                                        Text(
                                            meta.descripcion,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                IconButton(
                                    onClick = {
                                        metaEditando = meta
                                        mostrandoDialogo = true
                                    }
                                ) {
                                    Icon(
                                        Icons.Filled.Edit,
                                        contentDescription = "Editar meta",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                IconButton(onClick = { viewModel.eliminar(meta) }) {
                                    Icon(
                                        Icons.Filled.Delete,
                                        contentDescription = "Eliminar",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            Text("Cuesta: ${MoneyFormat.formatear(meta.montoObjetivo)}")
                            Text("Tenés en Ahorro: ${MoneyFormat.formatear(state.saldoAhorro)}")
                            Spacer(Modifier.height(12.dp))
                            LinearProgressIndicator(
                                progress = { (progreso / 100.0).toFloat() },
                                modifier = Modifier.fillMaxWidth(),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                            Spacer(Modifier.height(12.dp))
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("${"%.0f".format(progreso)} %")
                                Text(
                                    if (alcanzable) "¡Alcanza!"
                                    else "Falta ${MoneyFormat.formatear(restante)}"
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                            if (alcanzable) {
                                Text(
                                    "¡Ya la podés comprar con tu ahorro!",
                                    fontWeight = FontWeight.SemiBold
                                )
                            } else {
                                Text(
                                    "Guardá ${MoneyFormat.formatear(restante)} más en tu cuenta de Ahorro para llegar.",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
        DialogoMeta(
            meta = metaEditando,
            onDismiss = {
                mostrandoDialogo = false
                metaEditando = null
            },
            onGuardar = { meta ->
                if (metaEditando == null) viewModel.crear(meta) else viewModel.actualizar(meta)
                mostrandoDialogo = false
                metaEditando = null
            }
        )
    }
}

@Composable
private fun EtiquetaPrioridad(prioridad: GoalPriority) {
    val fondo = when (prioridad) {
        GoalPriority.ALTA -> MaterialTheme.colorScheme.primary
        GoalPriority.MEDIA -> MaterialTheme.colorScheme.secondaryContainer
        GoalPriority.BAJA -> MaterialTheme.colorScheme.surfaceVariant
    }
    val texto = when (prioridad) {
        GoalPriority.ALTA -> MaterialTheme.colorScheme.onPrimary
        GoalPriority.MEDIA -> MaterialTheme.colorScheme.onSecondaryContainer
        GoalPriority.BAJA -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    Surface(color = fondo, shape = RoundedCornerShape(50)) {
        Text(
            "PRIORIDAD ${prioridad.etiqueta.uppercase()}",
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = texto
        )
    }
}

@Composable
private fun DialogoMeta(
    meta: SavingsGoalEntity?,
    onDismiss: () -> Unit,
    onGuardar: (SavingsGoalEntity) -> Unit
) {
    var nombre by remember(meta) { mutableStateOf(meta?.nombre ?: "") }
    var descripcion by remember(meta) { mutableStateOf(meta?.descripcion ?: "") }
    var costo by remember(meta) {
        mutableStateOf(
            meta?.montoObjetivo?.let {
                if (it % 1.0 == 0.0) it.toLong().toString() else it.toString()
            } ?: ""
        )
    }
    var prioridad by remember(meta) { mutableStateOf(meta?.prioridad ?: GoalPriority.MEDIA) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (meta == null) "Nueva meta" else "Editar meta") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("¿Qué querés comprar?") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Detalle (opcional)") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = costo,
                    onValueChange = { costo = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("¿Cuánto cuesta? (Bs.)") },
                    singleLine = true
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    "¿Qué tan importante es para vos?",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    GoalPriority.entries.forEach { opcion ->
                        FilterChip(
                            selected = prioridad == opcion,
                            onClick = { prioridad = opcion },
                            label = { Text(opcion.etiqueta) }
                        )
                    }
                }
                Text(
                    "Las de prioridad Alta aparecen primero.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val valor = costo.toDoubleOrNull()
                    if (nombre.isNotBlank() && valor != null && valor > 0) {
                        val base = meta ?: SavingsGoalEntity(
                            nombre = nombre.trim(),
                            montoObjetivo = valor
                        )
                        onGuardar(
                            base.copy(
                                nombre = nombre.trim(),
                                descripcion = descripcion.ifBlank { null },
                                montoObjetivo = valor,
                                prioridad = prioridad
                            )
                        )
                    }
                }
            ) { Text(if (meta == null) "Crear" else "Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
