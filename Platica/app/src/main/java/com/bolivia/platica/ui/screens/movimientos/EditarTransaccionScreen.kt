package com.bolivia.platica.ui.screens.movimientos

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.bolivia.platica.domain.model.enums.TransactionType
import com.bolivia.platica.ui.components.Fechas
import com.bolivia.platica.ui.components.MoneyFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarTransaccionScreen(
    onGuardado: () -> Unit,
    onCancelar: () -> Unit,
    viewModel: EditarTransaccionViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var mostrarFecha by remember { mutableStateOf(false) }

    LaunchedEffect(state.guardado) {
        if (state.guardado) onGuardado()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (state.esEdicion) "Editar movimiento" else "Nuevo movimiento",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onCancelar) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
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
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Selector de tipo
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SelectorTipo(
                    texto = "Gasto",
                    seleccionado = state.tipo == TransactionType.GASTO,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.cambiarTipo(TransactionType.GASTO) }
                )
                SelectorTipo(
                    texto = "Ingreso",
                    seleccionado = state.tipo == TransactionType.INGRESO,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.cambiarTipo(TransactionType.INGRESO) }
                )
            }

            // Plantillas de gasto rápido
            if (state.plantillas.isNotEmpty() && state.tipo == TransactionType.GASTO) {
                Text(
                    "GASTO RÁPIDO",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.plantillas.forEach { plantilla ->
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.aplicarPlantilla(plantilla) },
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                Text(plantilla.nombre, fontWeight = FontWeight.SemiBold)
                                Text(
                                    MoneyFormat.formatear(plantilla.monto),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Monto
            OutlinedTextField(
                value = state.monto,
                onValueChange = viewModel::cambiarMonto,
                label = { Text("Monto (Bs.)") },
                isError = state.mensajeError != null,
                supportingText = state.mensajeError?.let { { Text(it) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Fecha del movimiento
            Etiqueta("Fecha y hora")
            OutlinedButton(
                onClick = { mostrarFecha = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("${Fechas.dia(state.fecha)} · ${Fechas.hora(state.fecha)}")
            }

            // Cuenta
            Etiqueta("Cuenta")
            state.cuentas.forEach { cuenta ->
                OpcionSeleccionable(
                    texto = "${cuenta.nombre} · ${MoneyFormat.formatear(state.saldos[cuenta.id] ?: cuenta.saldoInicial)}",
                    seleccionado = state.accountId == cuenta.id,
                    onClick = { viewModel.cambiarCuenta(cuenta.id) }
                )
            }

            // Categoría
            Etiqueta("Categoría")
            val cats = viewModel.categoriasDelTipo()
            cats.chunked(2).forEach { fila ->
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    fila.forEach { cat ->
                        OpcionSeleccionable(
                            texto = cat.nombre,
                            seleccionado = state.categoryId == cat.id,
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.cambiarCategoria(cat.id) }
                        )
                    }
                    if (fila.size == 1) Spacer(Modifier.weight(1f))
                }
            }

            // Nota
            OutlinedTextField(
                value = state.nota,
                onValueChange = viewModel::cambiarNota,
                label = { Text("Nota (opcional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            if (state.tipo == TransactionType.INGRESO) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Ingreso irregular / extraordinario")
                    Switch(checked = state.esIrregular, onCheckedChange = viewModel::cambiarIrregular)
                }
            }

            Spacer(Modifier.height(8.dp))
            Button(
                onClick = viewModel::guardar,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("GUARDAR", fontWeight = FontWeight.Bold)
            }
        }
    }

    if (mostrarFecha) {
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = Fechas.aUtcMedianoche(state.fecha)
        )
        DatePickerDialog(
            onDismissRequest = { mostrarFecha = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let { seleccionada ->
                        viewModel.cambiarFecha(Fechas.desdeUtcMedianoche(seleccionada, state.fecha))
                    }
                    mostrarFecha = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarFecha = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = pickerState)
        }
    }
}

@Composable
private fun Etiqueta(texto: String) {
    Text(
        texto.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun SelectorTipo(
    texto: String,
    seleccionado: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val fondo = if (seleccionado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val colorTxt = if (seleccionado) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    Column(
        modifier
            .height(48.dp)
            .background(fondo, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(texto, color = colorTxt, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun OpcionSeleccionable(
    texto: String,
    seleccionado: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val fondo = if (seleccionado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val colorTxt = if (seleccionado) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    Column(
        modifier
            .background(fondo, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(texto, color = colorTxt, style = MaterialTheme.typography.bodyLarge)
    }
}
