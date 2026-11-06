package com.bolivia.platica.ui.screens.ajustes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bolivia.platica.BuildConfig

/**
 * Pantalla de ajustes. Todas las secciones son navegables.
 * Ver `docs/06_flujo_ui_ux.md` §3.11.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AjustesScreen(
    onCuentas: () -> Unit = {},
    onCategorias: () -> Unit = {},
    onPresupuestos: () -> Unit = {},
    onRecurrentes: () -> Unit = {},
    onInversiones: () -> Unit = {},
    onRespaldo: () -> Unit = {},
    onSeguridad: () -> Unit = {},
    onApariencia: () -> Unit = {}
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Ajustes", fontWeight = FontWeight.Bold) },
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
                .padding(horizontal = 20.dp)
                .padding(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = MaterialTheme.shapes.large
            ) {
                Column(Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                    SeccionAjuste("Cuentas", "Tu efectivo, billeteras QR, emergencias, ahorro e inversión.", onCuentas)
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                    SeccionAjuste("Categorías", "Creá y editá tus categorías de ingreso y gasto.", onCategorias)
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                    SeccionAjuste("Presupuestos", "Límites de gasto y alertas.", onPresupuestos)
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                    SeccionAjuste("Recurrentes", "Sueldos, suscripciones y gastos fijos.", onRecurrentes)
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                    SeccionAjuste("Inversiones", "Registrá cuánto generan tus inversiones.", onInversiones)
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                    SeccionAjuste("Respaldo", "Exportá e importá tus datos.", onRespaldo)
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                    SeccionAjuste("Seguridad", "Huella y PIN.", onSeguridad)
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                    SeccionAjuste("Apariencia", "Tema claro u oscuro.", onApariencia)
                }
            }

            Spacer(Modifier.height(4.dp))
            Text(
                "Platica v${BuildConfig.VERSION_NAME}\nControl de gastos personal.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SeccionAjuste(titulo: String, descripcion: String, onClick: () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp)
    ) {
        Text(
            titulo,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(2.dp))
        Text(
            descripcion,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
