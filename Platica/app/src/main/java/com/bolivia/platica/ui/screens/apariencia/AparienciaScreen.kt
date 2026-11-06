package com.bolivia.platica.ui.screens.apariencia

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bolivia.platica.data.prefs.ModoTema
import com.bolivia.platica.ui.components.FilaOpcion

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AparienciaScreen(
    onVolver: () -> Unit,
    viewModel: AparienciaViewModel = hiltViewModel()
) {
    val modo by viewModel.modoTema.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Apariencia", fontWeight = FontWeight.Bold) },
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
                Modifier.fillMaxSize(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = MaterialTheme.shapes.large
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text(
                        "TEMA",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    FilaOpcion(
                        titulo = "Claro",
                        descripcion = "Fondo blanco, texto negro.",
                        seleccionado = modo == ModoTema.CLARO,
                        onClick = { viewModel.cambiarTema(ModoTema.CLARO) }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                    FilaOpcion(
                        titulo = "Oscuro",
                        descripcion = "Fondo negro, texto claro.",
                        seleccionado = modo == ModoTema.OSCURO,
                        onClick = { viewModel.cambiarTema(ModoTema.OSCURO) }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                    FilaOpcion(
                        titulo = "Automático",
                        descripcion = "Sigue la configuración del teléfono.",
                        seleccionado = modo == ModoTema.SISTEMA,
                        onClick = { viewModel.cambiarTema(ModoTema.SISTEMA) }
                    )
                }
            }
        }
    }
}
