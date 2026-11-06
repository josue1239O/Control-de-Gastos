package com.bolivia.platica.domain.usecase

import java.util.Calendar

/**
 * Utilidades de fecha para el "ciclo financiero" de Platica.
 *
 * El ciclo por defecto es el mes calendario, pero es configurable
 * porque en Bolivia los estudiantes reciben el bono en fechas irregulares.
 * Ver `docs/05_logica_de_negocio.md` §1.1.
 */
object CicloFinanciero {

    /**
     * Calcula el rango [inicio, fin] del ciclo que contiene [referencia].
     *
     * @param diaInicio día del mes en que comienza el ciclo (1 = mes calendario).
     */
    fun rango(referencia: Long = System.currentTimeMillis(), diaInicio: Int = 1): LongRange {
        val cal = Calendar.getInstance().apply { timeInMillis = referencia }
        val dia = cal.get(Calendar.DAY_OF_MONTH)

        val inicio = Calendar.getInstance().apply {
            timeInMillis = referencia
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (dia < diaInicio) add(Calendar.MONTH, -1)
            set(Calendar.DAY_OF_MONTH, diaInicio)
        }
        val fin = (inicio.clone() as Calendar).apply { add(Calendar.MONTH, 1) }
        return inicio.timeInMillis..(fin.timeInMillis - 1)
    }

    /** Días transcurridos del ciclo hasta hoy (inclusive). */
    fun diasTranscurridos(rango: LongRange, hoy: Long = System.currentTimeMillis()): Int {
        val unDia = 24L * 60 * 60 * 1000
        val desde = maxOf(rango.first, minOf(hoy, rango.last))
        return (((desde - rango.first) / unDia).toInt() + 1).coerceAtLeast(1)
    }

    /** Total de días del ciclo. */
    fun diasTotales(rango: LongRange): Int {
        val unDia = 24L * 60 * 60 * 1000
        return (((rango.last - rango.first) / unDia).toInt() + 1).coerceAtLeast(1)
    }

    /** Meses (redondeados hacia arriba) entre hoy y una fecha límite. */
    fun mesesRestantes(fechaLimite: Long): Int {
        val hoy = Calendar.getInstance()
        val limite = Calendar.getInstance().apply { timeInMillis = fechaLimite }
        var meses = (limite.get(Calendar.YEAR) - hoy.get(Calendar.YEAR)) * 12 +
                (limite.get(Calendar.MONTH) - hoy.get(Calendar.MONTH))
        if (limite.get(Calendar.DAY_OF_MONTH) > hoy.get(Calendar.DAY_OF_MONTH)) meses += 1
        return meses.coerceAtLeast(1)
    }

    /**
     * Devuelve el rango [inicio, fin] de un mes concreto (offset relativo al actual).
     * offsetMes = 0 -> mes actual; -1 -> mes anterior; +1 -> mes siguiente.
     */
    fun rangoMes(offsetMes: Int = 0): LongRange {
        val cal = Calendar.getInstance().apply {
            add(Calendar.MONTH, offsetMes)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val inicio = cal.timeInMillis
        val fin = (cal.clone() as Calendar).apply { add(Calendar.MONTH, 1) }
        return inicio..(fin.timeInMillis - 1)
    }

    /** Nombre del mes (ej. "Noviembre 2026") para un offset dado. */
    fun nombreMes(offsetMes: Int = 0): String {
        val cal = Calendar.getInstance().apply { add(Calendar.MONTH, offsetMes) }
        val meses = arrayOf(
            "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        )
        return "${meses[cal.get(Calendar.MONTH)]} ${cal.get(Calendar.YEAR)}"
    }
}
