package com.bolivia.platica.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Paleta minimalista en BLANCO Y NEGRO de Platica.
 *
 * Se evita el color para mantener una estética sobria y limpia.
 * Ver `docs/06_flujo_ui_ux.md`.
 */

// --- Escala de grises (claro) ---
private val Negro = Color(0xFF0A0A0A)
private val GrisMedio = Color(0xFF6B6B6B)
private val GrisClaro = Color(0xFFBDBDBD)
private val GrisFondo = Color(0xFFF5F5F5)
private val Blanco = Color(0xFFFFFFFF)

// --- Escala de grises (oscuro) ---
private val NegroFondo = Color(0xFF0B0B0D)      // fondo de la app
private val NegroTarjeta = Color(0xFF17171A)     // tarjetas (surface)
private val GrisElevado = Color(0xFF232327)      // superficies elevadas (variant)
private val GrisBorde = Color(0xFF3A3A40)        // bordes/líneas
private val GrisTextoSuave = Color(0xFF9E9EA5)   // texto secundario
private val BlancoTexto = Color(0xFFEDEDEF)      // texto principal claro
private val BlancoPuro = Color(0xFFFFFFFF)

private val ColorsLight = lightColorScheme(
    primary = Negro,
    onPrimary = Blanco,
    primaryContainer = GrisFondo,
    onPrimaryContainer = Negro,
    secondary = GrisMedio,
    onSecondary = Blanco,
    secondaryContainer = GrisFondo,
    onSecondaryContainer = Negro,
    tertiary = GrisMedio,
    background = Blanco,
    onBackground = Negro,
    surface = Blanco,
    onSurface = Negro,
    surfaceVariant = GrisFondo,
    onSurfaceVariant = GrisMedio,
    outline = GrisClaro,
    outlineVariant = Color(0xFFE0E0E0),
    error = Negro,
    onError = Blanco
)

private val ColorsDark = darkColorScheme(
    primary = BlancoPuro,
    onPrimary = Negro,
    primaryContainer = GrisElevado,
    onPrimaryContainer = BlancoTexto,
    secondary = GrisClaro,
    onSecondary = Negro,
    secondaryContainer = GrisElevado,
    onSecondaryContainer = BlancoTexto,
    tertiary = GrisClaro,
    background = NegroFondo,
    onBackground = BlancoTexto,
    surface = NegroTarjeta,
    onSurface = BlancoTexto,
    surfaceVariant = GrisElevado,
    onSurfaceVariant = GrisTextoSuave,
    outline = GrisBorde,
    outlineVariant = Color(0xFF2A2A2E),
    error = Color(0xFFEF9A9A),
    onError = Negro
)

/**
 * Colores semánticos propios de Platica, independientes del rol de Material,
 * para asegurar coherencia entre tema claro y oscuro.
 */
data class PlaticaColors(
    /** Fondo de la tarjeta destacada (saldo disponible): siempre oscuro, texto claro. */
    val heroFondo: Color,
    val heroTexto: Color,
    val heroTextoSecundario: Color,
    /** Fondo de las tarjetas normales. */
    val tarjetaFondo: Color,
    /** Superficies intermedias (reservas, chips no seleccionados). */
    val tarjetaMedia: Color,
    /** Líneas divisorias. */
    val borde: Color,
    /** Texto secundario. */
    val textoSecundario: Color,
    val esOscuro: Boolean
)

private val PlaticaColorsClaro = PlaticaColors(
    heroFondo = Color(0xFF0A0A0A),
    heroTexto = Color(0xFFFFFFFF),
    heroTextoSecundario = Color(0xFFBDBDBD),
    tarjetaFondo = Color(0xFFFFFFFF),
    tarjetaMedia = Color(0xFFF5F5F5),
    borde = Color(0xFFE0E0E0),
    textoSecundario = Color(0xFF6B6B6B),
    esOscuro = false
)

private val PlaticaColorsOscuro = PlaticaColors(
    heroFondo = Color(0xFF1E1E22),
    heroTexto = Color(0xFFFFFFFF),
    heroTextoSecundario = Color(0xFFB0B0B8),
    tarjetaFondo = Color(0xFF17171A),
    tarjetaMedia = Color(0xFF232327),
    borde = Color(0xFF3A3A40),
    textoSecundario = Color(0xFF9E9EA5),
    esOscuro = true
)

/** Local con los colores de Platica del tema actual. */
val LocalPlaticaColors = staticCompositionLocalOf { PlaticaColorsClaro }

/** Atajo para acceder a los colores de Platica. */
val MaterialTheme.platica: PlaticaColors
    @Composable
    get() = LocalPlaticaColors.current

@Composable
fun PlaticaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) PlaticaColorsOscuro else PlaticaColorsClaro
    CompositionLocalProvider(LocalPlaticaColors provides colors) {
        MaterialTheme(
            colorScheme = if (darkTheme) ColorsDark else ColorsLight,
            typography = PlaticaTypography,
            content = content
        )
    }
}

/** Resuelve si debe usarse el tema oscuro según el modo elegido. */
@Composable
fun resolverTemaOscuro(modo: com.bolivia.platica.data.prefs.ModoTema): Boolean =
    when (modo) {
        com.bolivia.platica.data.prefs.ModoTema.CLARO -> false
        com.bolivia.platica.data.prefs.ModoTema.OSCURO -> true
        com.bolivia.platica.data.prefs.ModoTema.SISTEMA -> isSystemInDarkTheme()
    }
