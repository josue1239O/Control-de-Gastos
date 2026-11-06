package com.bolivia.platica.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bolivia.platica.ui.components.BarraNavegacionInferior
import com.bolivia.platica.ui.screens.ajustes.AjustesScreen
import com.bolivia.platica.ui.screens.analisis.AnalisisScreen
import com.bolivia.platica.ui.screens.apariencia.AparienciaScreen
import com.bolivia.platica.ui.screens.categorias.CategoriasScreen
import com.bolivia.platica.ui.screens.cuentas.CuentaDetalleScreen
import com.bolivia.platica.ui.screens.cuentas.CuentasScreen
import com.bolivia.platica.ui.screens.inicio.InicioScreen
import com.bolivia.platica.ui.screens.inversiones.InversionesScreen
import com.bolivia.platica.ui.screens.metas.MetasScreen
import com.bolivia.platica.ui.screens.movimientos.EditarTransaccionScreen
import com.bolivia.platica.ui.screens.movimientos.MovimientosScreen
import com.bolivia.platica.ui.screens.presupuestos.PresupuestosScreen
import com.bolivia.platica.ui.screens.recurrentes.RecurrentesScreen
import com.bolivia.platica.ui.screens.respaldo.RespaldoScreen
import com.bolivia.platica.ui.screens.seguridad.SeguridadScreen

/**
 * Gráfico de navegación principal de Platica.
 * Responsive: barra inferior en celular, rail lateral en pantallas anchas (tablet).
 * Ver `docs/06_flujo_ui_ux.md` §4.
 */
@Composable
fun PlaticaNavGraph(
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = backStackEntry?.destination?.route
    val destinoActual = Destino.porRuta(rutaActual)
    val enPantallaPrincipal = Destino.entries.any { it.ruta == rutaActual }

    val anchoDp = LocalConfiguration.current.screenWidthDp
    val esAncho = anchoDp >= 600

    if (enPantallaPrincipal) {
        if (esAncho) {
            Row {
                NavigationRail {
                    Destino.inferiores.forEach { destino ->
                        NavigationRailItem(
                            selected = destinoActual == destino,
                            onClick = { navController.navegarA(destino) },
                            icon = {
                                Icon(
                                    if (destinoActual == destino) destino.iconoSeleccionado else destino.iconoNoSeleccionado,
                                    contentDescription = destino.etiqueta
                                )
                            },
                            label = { Text(destino.etiqueta) }
                        )
                    }
                }
                NavHostContenido(navController)
            }
        } else {
            Scaffold(
                containerColor = androidx.compose.material3.MaterialTheme.colorScheme.background,
                bottomBar = {
                    BarraNavegacionInferior(
                        destinoActual = destinoActual,
                        onSeleccionar = { navController.navegarA(it) }
                    )
                }
            ) { padding ->
                Box(Modifier.padding(padding)) {
                    NavHostContenido(navController)
                }
            }
        }
    } else {
        NavHostContenido(navController)
    }
}

@Composable
private fun NavHostContenido(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Destino.Inicio.ruta) {
        composable(Destino.Inicio.ruta) { InicioScreen() }
        composable(Destino.Movimientos.ruta) {
            MovimientosScreen(
                onNuevo = { navController.navigate("editar_transaccion") },
                onEditar = { id -> navController.navigate("editar_transaccion?id=$id") }
            )
        }
        composable(Destino.Metas.ruta) { MetasScreen() }
        composable(Destino.Analisis.ruta) { AnalisisScreen() }
        composable(Destino.Ajustes.ruta) {
            AjustesScreen(
                onCuentas = { navController.navigate("cuentas") },
                onCategorias = { navController.navigate("categorias") },
                onPresupuestos = { navController.navigate("presupuestos") },
                onRecurrentes = { navController.navigate("recurrentes") },
                onInversiones = { navController.navigate("inversiones") },
                onRespaldo = { navController.navigate("respaldo") },
                onSeguridad = { navController.navigate("seguridad") },
                onApariencia = { navController.navigate("apariencia") }
            )
        }
        composable("cuentas") {
            CuentasScreen(
                onVolver = { navController.popBackStack() },
                onAbrirCuenta = { id -> navController.navigate("cuenta/$id") }
            )
        }
        composable(
            route = "cuenta/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) {
            CuentaDetalleScreen(onVolver = { navController.popBackStack() })
        }
        composable("categorias") { CategoriasScreen(onVolver = { navController.popBackStack() }) }
        composable("presupuestos") { PresupuestosScreen(onVolver = { navController.popBackStack() }) }
        composable("recurrentes") { RecurrentesScreen(onVolver = { navController.popBackStack() }) }
        composable("inversiones") { InversionesScreen(onVolver = { navController.popBackStack() }) }
        composable("respaldo") { RespaldoScreen(onVolver = { navController.popBackStack() }) }
        composable("seguridad") { SeguridadScreen(onVolver = { navController.popBackStack() }) }
        composable("apariencia") { AparienciaScreen(onVolver = { navController.popBackStack() }) }
        composable(
            route = "editar_transaccion?id={id}",
            arguments = listOf(
                navArgument("id") { type = NavType.StringType; defaultValue = "" }
            )
        ) {
            EditarTransaccionScreen(
                onGuardado = { navController.popBackStack() },
                onCancelar = { navController.popBackStack() }
            )
        }
    }
}

private fun NavHostController.navegarA(destino: Destino) {
    navigate(destino.ruta) {
        popUpTo(graph.startDestinationId) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
