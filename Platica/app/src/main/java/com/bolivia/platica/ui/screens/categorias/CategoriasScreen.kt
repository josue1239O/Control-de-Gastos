package com.bolivia.platica.ui.screens.categorias

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bolivia.platica.data.local.entity.CategoryEntity
import com.bolivia.platica.domain.model.enums.CategoryType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriasScreen(
    onVolver: () -> Unit,
    viewModel: CategoriasViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var tipoNueva by remember { mutableStateOf<CategoryType?>(null) }
    var categoriaEditar by remember { mutableStateOf<CategoryEntity?>(null) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Categorías", fontWeight = FontWeight.Bold) },
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
                onClick = { tipoNueva = CategoryType.GASTO },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Nueva categoría")
            }
        }
    ) { padding ->
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { Encabezado("GASTOS") }
            items(state.gastos, key = { it.id }) { cat ->
                FilaCategoria(cat, onEditar = { categoriaEditar = cat }, onEliminar = { viewModel.eliminar(cat) })
            }
            item {
                Spacer(Modifier.height(8.dp))
                Encabezado("INGRESOS")
            }
            items(state.ingresos, key = { it.id }) { cat ->
                FilaCategoria(cat, onEditar = { categoriaEditar = cat }, onEliminar = { viewModel.eliminar(cat) })
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }

    if (tipoNueva != null) {
        DialogoNuevaCategoria(
            tipo = tipoNueva!!,
            onCambiarTipo = { tipoNueva = it },
            onDismiss = { tipoNueva = null },
            onGuardar = { nombre, tipo, fijo ->
                viewModel.crear(nombre, tipo, fijo)
                tipoNueva = null
            }
        )
    }

    categoriaEditar?.let { cat ->
        DialogoEditarCategoria(
            categoria = cat,
            onDismiss = { categoriaEditar = null },
            onGuardar = { actualizada ->
                viewModel.actualizar(actualizada)
                categoriaEditar = null
            }
        )
    }
}

@Composable
private fun DialogoEditarCategoria(
    categoria: CategoryEntity,
    onDismiss: () -> Unit,
    onGuardar: (CategoryEntity) -> Unit
) {
    var nombre by remember { mutableStateOf(categoria.nombre) }
    var esFijo by remember { mutableStateOf(categoria.esFijo) }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar categoría") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    singleLine = true
                )
                if (categoria.tipo == CategoryType.GASTO) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { esFijo = !esFijo }
                            .padding(vertical = 4.dp)
                    ) {
                        Text(if (esFijo) "☑" else "☐")
                        Spacer(Modifier.padding(4.dp))
                        Text("Es un gasto fijo / común")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (nombre.isNotBlank()) onGuardar(categoria.copy(nombre = nombre.trim(), esFijo = esFijo))
            }) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
private fun Encabezado(texto: String) {
    Text(
        texto,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
private fun FilaCategoria(cat: CategoryEntity, onEditar: () -> Unit, onEliminar: () -> Unit) {
    Card(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onEditar),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 8.dp, top = 10.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(cat.nombre, style = MaterialTheme.typography.bodyLarge)
                Text(
                    if (cat.esFijo) "Fijo / común" else "Variable",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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

@Composable
private fun DialogoNuevaCategoria(
    tipo: CategoryType,
    onCambiarTipo: (CategoryType) -> Unit,
    onDismiss: () -> Unit,
    onGuardar: (String, CategoryType, Boolean) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var esFijo by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva categoría") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    singleLine = true
                )
                Text("Tipo", style = MaterialTheme.typography.labelSmall)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    BotonTipo("Gasto", tipo == CategoryType.GASTO) { onCambiarTipo(CategoryType.GASTO) }
                    BotonTipo("Ingreso", tipo == CategoryType.INGRESO) { onCambiarTipo(CategoryType.INGRESO) }
                }
                if (tipo == CategoryType.GASTO) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { esFijo = !esFijo }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(if (esFijo) "☑" else "☐")
                        Spacer(Modifier.padding(4.dp))
                        Text("Es un gasto fijo / común")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { if (nombre.isNotBlank()) onGuardar(nombre.trim(), tipo, esFijo) }) {
                Text("Crear")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
private fun BotonTipo(texto: String, activo: Boolean, onClick: () -> Unit) {
    val fondo = if (activo) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val color = if (activo) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    Column(
        Modifier
            .background(fondo, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(texto, color = color)
    }
}
