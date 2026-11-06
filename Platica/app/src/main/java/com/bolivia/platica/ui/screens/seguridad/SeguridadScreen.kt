package com.bolivia.platica.ui.screens.seguridad

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bolivia.platica.data.prefs.MetodoBloqueo
import com.bolivia.platica.security.BiometriaUtils
import com.bolivia.platica.ui.components.FilaOpcion

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeguridadScreen(
    onVolver: () -> Unit,
    viewModel: SeguridadViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var mostrandoPin by remember { mutableStateOf(false) }

    val huellaOk = BiometriaUtils.huellaDisponible(context)
    val descHuella = BiometriaUtils.descripcionDisponibilidad(context)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Seguridad", fontWeight = FontWeight.Bold) },
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
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = MaterialTheme.shapes.large
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text(
                        "BLOQUEO DE LA APP",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    FilaOpcion(
                        titulo = "Sin bloqueo",
                        descripcion = "La app abre directo.",
                        seleccionado = state.metodo == MetodoBloqueo.NINGUNO,
                        onClick = { viewModel.cambiarMetodo(MetodoBloqueo.NINGUNO) }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                    FilaOpcion(
                        titulo = "Huella",
                        descripcion = if (huellaOk) descHuella else descHuella,
                        seleccionado = state.metodo == MetodoBloqueo.HUELLA,
                        onClick = { if (huellaOk) viewModel.cambiarMetodo(MetodoBloqueo.HUELLA) }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                    FilaOpcion(
                        titulo = "PIN",
                        descripcion = if (state.tienePin) "PIN configurado." else "Tocá para crear un PIN.",
                        seleccionado = state.metodo == MetodoBloqueo.PIN,
                        onClick = { mostrandoPin = true }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                    FilaOpcion(
                        titulo = "Huella o PIN",
                        descripcion = "Podés entrar con cualquiera de los dos.",
                        seleccionado = state.metodo == MetodoBloqueo.HUELLA_O_PIN,
                        onClick = { if (huellaOk && state.tienePin) viewModel.cambiarMetodo(MetodoBloqueo.HUELLA_O_PIN) }
                    )
                }
            }

            Card(
                Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = MaterialTheme.shapes.large
            ) {
                Column(Modifier.padding(20.dp)) {
                    Icon(
                        Icons.Filled.Fingerprint,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Sobre las huellas",
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Platica usa las huellas que ya registraste en tu teléfono (podés tener varias). " +
                                "Si querés agregar otra huella, hacelo en Ajustes del sistema > Seguridad > Huellas. " +
                                "La app nunca guarda tus huellas.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    if (mostrandoPin) {
        DialogoPin(
            onDismiss = { mostrandoPin = false },
            onGuardar = { pin ->
                viewModel.guardarPin(pin)
                mostrandoPin = false
            }
        )
    }
}

@Composable
private fun DialogoPin(
    onDismiss: () -> Unit,
    onGuardar: (String) -> Unit
) {
    var pin by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Crear PIN") },
        text = {
            Column {
                Text("Elegí un PIN de 4 a 6 dígitos.", style = MaterialTheme.typography.labelSmall)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = pin,
                    onValueChange = { pin = it.filter { c -> c.isDigit() }.take(6) },
                    label = { Text("PIN") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (pin.length in 4..6) onGuardar(pin) }
            ) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
