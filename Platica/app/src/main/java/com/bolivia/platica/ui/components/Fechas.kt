package com.bolivia.platica.ui.components

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Formateo y conversión de fechas para la interfaz (es-BO).
 */
object Fechas {

    private val locale = Locale("es", "BO")
    private val formatoDia = SimpleDateFormat("d MMM yyyy", locale)
    private val formatoCorto = SimpleDateFormat("dd/MM/yyyy", locale)
    private val formatoHora = SimpleDateFormat("HH:mm", locale)

    /** Ej: "5 nov 2026". */
    fun dia(millis: Long): String = formatoDia.format(Date(millis))

    /** Ej: "05/11/2026". */
    fun corta(millis: Long): String = formatoCorto.format(Date(millis))

    /** Ej: "14:35". */
    fun hora(millis: Long): String = formatoHora.format(Date(millis))

    /**
     * Convierte una fecha local al "mediodía UTC" que espera Material3 DatePicker
     * (el selector trabaja en UTC y, si no se ajusta, en Bolivia se corre un día).
     */
    fun aUtcMedianoche(millis: Long): Long {
        val local = Calendar.getInstance().apply { timeInMillis = millis }
        val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            clear()
            set(
                local.get(Calendar.YEAR),
                local.get(Calendar.MONTH),
                local.get(Calendar.DAY_OF_MONTH),
                0, 0, 0
            )
        }
        return utc.timeInMillis
    }

    /**
     * Toma la fecha elegida en el DatePicker (UTC) y devuelve el epoch local
     * conservando la hora del movimiento original.
     */
    fun desdeUtcMedianoche(utcMillis: Long, referencia: Long): Long {
        val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = utcMillis }
        val ref = Calendar.getInstance().apply { timeInMillis = referencia }
        val local = Calendar.getInstance().apply {
            clear()
            set(
                utc.get(Calendar.YEAR),
                utc.get(Calendar.MONTH),
                utc.get(Calendar.DAY_OF_MONTH),
                ref.get(Calendar.HOUR_OF_DAY),
                ref.get(Calendar.MINUTE),
                0
            )
        }
        return local.timeInMillis
    }
}
