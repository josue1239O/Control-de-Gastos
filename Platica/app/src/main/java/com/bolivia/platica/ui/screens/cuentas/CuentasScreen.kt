package com.bolivia.platica.ui.screens.cuentas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import com.bolivia.platica.domain.model.enums.AccountType
import com.bolivia.platica.ui.components.MoneyFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CuentasScreen(
    onVolver: () -> Unit,
    onAbrirCuenta: (Long) -> Unit,
    viewModel: CuentasViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var mostrandoDialogo by remember { mutableStateOf(false) }
    var cuentaEditar by remember { mutableStateOf<com.bolivia.platica.data.local.entity.AccountEntity?>(null) }
    var cuentaEliminar by remember { mutableStateOf<com.bolivia.platica.data.local.entity.AccountEntity?>(null) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Cuentas", fontWeight = FontWeight.Bold) },
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
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Nueva cuenta")
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
            item {
                Text(
                    "TUS CUENTAS",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            items(state.cuentas, key = { it.cuenta.id }) { item ->
                TarjetaCuenta(
                    nombre = item.cuenta.nombre,
                    tipo = item.cuenta.tipo,
                    saldo = item.saldo,
                    onAbrir = { onAbrirCuenta(item.cuenta.id) },
                    onEditar = { cuentaEditar = item.cuenta },
                    onEliminar = { cuentaEliminar = item.cuenta }
                )
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }

    if (mostrandoDialogo) {
        DialogoNuevaCuenta(
            onDismiss = { mostrandoDialogo = false },
            onGuardar = { nombre, tipo, saldo ->
                viewModel.crear(nombre, tipo, saldo)
                mostrandoDialogo = false
            }
        )
    }

    cuentaEditar?.let { cuenta ->
        DialogoEditarCuenta(
            cuenta = cuenta,
            onDismiss = { cuentaEditar = null },
            onGuardar = { actualizada ->
                viewModel.actualizar(actualizada)
                cuentaEditar = null
            }
        )
    }

    cuentaEliminar?.let { cuenta ->
        AlertDialog(
            onDismissRequest = { cuentaEliminar = null },
            title = { Text("¿Eliminar ${cuenta.nombre}?") },
            text = {
                Text(
                    "Se borrará la cuenta y TODOS sus movimientos asociados. " +
                            "Esta acción no se puede deshacer."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.eliminar(cuenta)
                        cuentaEliminar = null
                    }
                ) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { cuentaEliminar = null }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun DialogoEditarCuenta(
    cuenta: com.bolivia.platica.data.local.entity.AccountEntity,
    onDismiss: () -> Unit,
    onGuardar: (com.bolivia.platica.data.local.entity.AccountEntity) -> Unit
) {
    var nombre by remember { mutableStateOf(cuenta.nombre) }
    var saldo by remember { mutableStateOf(cuenta.saldoInicial.toString()) }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar cuenta") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = saldo,
                    onValueChange = { saldo = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Saldo inicial (Bs.)") },
                    singleLine = true
                )
                Text(
                    "Tipo: ${cuenta.tipo.etiqueta}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val valor = saldo.toDoubleOrNull() ?: cuenta.saldoInicial
                if (nombre.isNotBlank()) {
                    onGuardar(cuenta.copy(nombre = nombre.trim(), saldoInicial = valor))
                }
            }) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
private fun TarjetaCuenta(
    nombre: String,
    tipo: AccountType,
    saldo: Double,
    onAbrir: () -> Unit,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onAbrir),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Punto que identifica el tipo (gris según intensidad; B/N)
            Box(
                Modifier
                    .size(40.dp)
                    .background(colorPunto(tipo), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    inicial(nombre),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(nombre, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                Text(
                    tipo.etiqueta,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                MoneyFormat.formatear(saldo),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onEditar) {
                Icon(
                    Icons.Filled.Edit,
                    contentDescription = "Editar",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onEliminar) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = "Eliminar",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun inicial(nombre: String): String =
    nombre.trim().firstOrNull()?.uppercase() ?: "?"

/** Colores en escala de grises según el tipo (sin colores, estilo B/N). */
private fun colorPunto(tipo: AccountType) = when (tipo) {
    AccountType.EFECTIVO -> androidx.compose.ui.graphics.Color(0xFF0A0A0A)
    AccountType.BILLETERA_QR -> androidx.compose.ui.graphics.Color(0xFF3A3A3A)
    AccountType.EMERGENCIA -> androidx.compose.ui.graphics.Color(0xFF4A4A4A)
    AccountType.AHORRO -> androidx.compose.ui.graphics.Color(0xFF5A5A5A)
    AccountType.INVERSION -> androidx.compose.ui.graphics.Color(0xFF6B6B6B)
}

@Composable
private fun DialogoNuevaCuenta(
    onDismiss: () -> Unit,
    onGuardar: (String, AccountType, Double) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var saldo by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf(AccountType.EFECTIVO) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva cuenta") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = saldo,
                    onValueChange = { saldo = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Saldo inicial (Bs.)") },
                    singleLine = true
                )
                Text("Tipo", style = MaterialTheme.typography.labelSmall)
                AccountType.entries.forEach { t ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { tipo = t }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            Modifier
                                .size(18.dp)
                                .background(colorPunto(t), CircleShape)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(t.etiqueta)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val valor = saldo.toDoubleOrNull() ?: 0.0
                    if (nombre.isNotBlank()) onGuardar(nombre.trim(), tipo, valor)
                }
            ) { Text("Crear") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
