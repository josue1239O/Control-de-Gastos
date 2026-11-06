package com.bolivia.platica.ui.components

import java.text.NumberFormat
import java.util.Locale

/**
 * Formateo de dinero en Bolivianos (Bs.).
 */
object MoneyFormat {

    private val formato: NumberFormat = NumberFormat.getNumberInstance(Locale("es", "BO")).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }

    /** Ej: 1250.5 -> "Bs. 1.250,50" */
    fun formatear(monto: Double): String = "Bs. ${formato.format(monto)}"

    /** Con signo explícito para ingresos/gastos. */
    fun formatearConSigno(monto: Double, esIngreso: Boolean): String {
        val signo = if (esIngreso) "+" else "-"
        return "$signo ${formatear(monto)}"
    }

    /** Sin símbolo, solo el número. */
    fun numero(monto: Double): String = formato.format(monto)
}
