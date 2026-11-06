package com.bolivia.platica.ui.screens.inicio

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bolivia.platica.ui.components.Fechas
import com.bolivia.platica.ui.components.MoneyFormat
import com.bolivia.platica.ui.components.SelectorMes
import com.bolivia.platica.ui.theme.platica

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InicioScreen(viewModel: InicioViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val platica = MaterialTheme.platica

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "¡Bienvenido!",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            "Platica",
                            style = MaterialTheme.typography.labelSmall,
                            color = platica.textoSecundario
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tarjeta destacada: Saldo disponible
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = platica.heroFondo),
                shape = MaterialTheme.shapes.large
            ) {
                Column(Modifier.padding(24.dp)) {
                    Text(
                        "SALDO DISPONIBLE",
                        color = platica.heroTextoSecundario,
                        style = MaterialTheme.typography.labelSmall
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        MoneyFormat.formatear(state.saldoDisponible),
                        color = platica.heroTexto,
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Es tu dinero gastable (efectivo + billeteras QR)",
                        color = platica.heroTextoSecundario,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            // Reservas
            Text(
                "TUS RESERVAS",
                style = MaterialTheme.typography.labelSmall,
                color = platica.textoSecundario
            )
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TarjetaReserva(
                    modifier = Modifier.weight(1f),
                    titulo = "Emergencias",
                    monto = state.reservaEmergencias,
                    fondo = platica.tarjetaMedia,
                    tituloColor = platica.textoSecundario
                )
                TarjetaReserva(
                    modifier = Modifier.weight(1f),
                    titulo = "Ahorro",
                    monto = state.reservaAhorro,
                    fondo = platica.tarjetaMedia,
                    tituloColor = platica.textoSecundario
                )
            }

            // Aviso metas alcanzables
            if (state.metasAlcanzables.isNotEmpty()) {
                Card(
                    Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = platica.heroFondo),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Text(
                            "¡BUENA NOTICIA!",
                            color = platica.heroTextoSecundario,
                            style = MaterialTheme.typography.labelSmall
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Ya podés comprar ${state.metasAlcanzables.size} de tus metas:",
                            color = platica.heroTexto,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(8.dp))
                        state.metasAlcanzables.forEach { meta ->
                            Text(
                                "• ${meta.nombre}  (${MoneyFormat.formatear(meta.montoObjetivo)})",
                                color = platica.heroTexto,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }

            // Invertido
            Text(
                "DINERO INVERTIDO",
                style = MaterialTheme.typography.labelSmall,
                color = platica.textoSecundario
            )
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = platica.tarjetaFondo),
                shape = MaterialTheme.shapes.large
            ) {
                Column(Modifier.padding(20.dp)) {
                    FilaResumen("Capital invertido", MoneyFormat.formatear(state.patrimonioInvertido), platica.textoSecundario)
                    Spacer(Modifier.height(8.dp))
                    FilaResumen("Ganancia generada", MoneyFormat.formatear(state.gananciaInversiones), platica.textoSecundario, destacado = true)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Tu dinero sigue circulando y generando ganancia.",
                        style = MaterialTheme.typography.labelSmall,
                        color = platica.textoSecundario
                    )
                }
            }

            // Ciclo actual
            Text(
                "CICLO ACTUAL",
                style = MaterialTheme.typography.labelSmall,
                color = platica.textoSecundario
            )
            SelectorMes(
                nombreMes = state.nombreMes,
                onAnterior = viewModel::mesAnterior,
                onSiguiente = viewModel::mesSiguiente
            )
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = platica.tarjetaFondo),
                shape = MaterialTheme.shapes.large
            ) {
                Column(Modifier.padding(20.dp)) {
                    FilaResumen("Ingresos", MoneyFormat.formatear(state.ingresosCiclo), platica.textoSecundario)
                    Spacer(Modifier.height(8.dp))
                    FilaResumen("Gastos", MoneyFormat.formatear(state.gastosCiclo), platica.textoSecundario)
                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider(color = platica.borde)
                    Spacer(Modifier.height(12.dp))
                    FilaResumen("Balance", MoneyFormat.formatear(state.balance), platica.textoSecundario, destacado = true)
                    Spacer(Modifier.height(8.dp))
                    val tasa = state.tasaAhorro
                    FilaResumen(
                        "Tasa de ahorro",
                        if (tasa == null) "—" else "${"%.1f".format(tasa)} %",
                        platica.textoSecundario
                    )
                }
            }

            // Últimos movimientos
            Text(
                "ÚLTIMOS MOVIMIENTOS",
                style = MaterialTheme.typography.labelSmall,
                color = platica.textoSecundario
            )
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = platica.tarjetaFondo),
                shape = MaterialTheme.shapes.large
            ) {
                Column(Modifier.padding(20.dp)) {
                    if (state.ultimas.isEmpty()) {
                        Text(
                            "Aún no hay datos. Empezá registrando tu primer movimiento.",
                            color = platica.textoSecundario
                        )
                    } else {
                        state.ultimas.forEachIndexed { index, item ->
                            if (index > 0) {
                                Spacer(Modifier.height(12.dp))
                                HorizontalDivider(color = platica.borde)
                                Spacer(Modifier.height(12.dp))
                            }
                            val mov = item.transaccion
                            FilaResumen(
                                "${mov.nota ?: mov.tipo.etiqueta}\n${item.cuenta} · ${Fechas.dia(mov.fecha)}",
                                MoneyFormat.formatearConSigno(mov.monto, mov.tipo.name == "INGRESO"),
                                platica.textoSecundario
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(90.dp))
        }
    }
}

@Composable
private fun TarjetaReserva(
    modifier: Modifier,
    titulo: String,
    monto: Double,
    fondo: androidx.compose.ui.graphics.Color,
    tituloColor: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = fondo),
        shape = MaterialTheme.shapes.large
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                titulo.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = tituloColor
            )
            Spacer(Modifier.height(6.dp))
            Text(
                MoneyFormat.formatear(monto),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun FilaResumen(
    etiqueta: String,
    valor: String,
    colorSecundario: androidx.compose.ui.graphics.Color,
    destacado: Boolean = false
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            etiqueta,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            valor,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = if (destacado) FontWeight.Bold else FontWeight.Normal
        )
    }
}
