package com.bolivia.platica.data.reporte

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import com.bolivia.platica.ui.components.MoneyFormat

/**
 * Datos necesarios para generar el reporte del mes.
 */
data class ReporteMes(
    val nombreMes: String,
    val ingresos: Double,
    val gastos: Double,
    val balance: Double,
    val tasaAhorro: Double?,
    val gastoProyectado: Double,
    val categorias: List<ParCategoria>,
    val metas: List<ParMeta>
)

data class ParCategoria(val nombre: String, val monto: Double, val porcentaje: Double)
data class ParMeta(val nombre: String, val objetivo: Double, val actual: Double, val alcanzable: Boolean)

/**
 * Genera un reporte PDF (KPI) del mes, 100% offline usando PdfDocument nativo.
 * El usuario elige dónde guardarlo mediante el Storage Access Framework.
 */
object ReporteMesManager {

    fun generarPdf(context: Context, reporte: ReporteMes, uri: Uri) {
        val documento = PdfDocument()
        val ancho = 595 // A4 en puntos (72 dpi)
        val alto = 842
        val pagina = documento.startPage(PdfDocument.PageInfo.Builder(ancho, alto, 1).create())
        val canvas: Canvas = pagina.canvas

        val negro = Paint().apply { color = Color.BLACK; isAntiAlias = true }
        val gris = Paint().apply { color = Color.DKGRAY; isAntiAlias = true }
        val linea = Paint().apply { color = Color.LTGRAY; isAntiAlias = true; strokeWidth = 1f }

        var y = 60f
        val margen = 48f

        fun texto(t: String, tam: Float, p: Paint = negro, gap: Float = 26f) {
            p.textSize = tam
            canvas.drawText(t, margen, y, p)
            y += gap
        }

        // Encabezado
        texto("PLATICA", 26f, negro, 34f)
        texto("Reporte mensual - ${reporte.nombreMes}", 13f, gris, 40f)
        canvas.drawLine(margen, y, ancho - margen, y, linea)
        y += 30f

        // KPIs principales
        texto("RESUMEN DEL MES", 12f, gris, 26f)
        texto("Ingresos:   ${MoneyFormat.formatear(reporte.ingresos)}", 15f, negro, 24f)
        texto("Gastos:     ${MoneyFormat.formatear(reporte.gastos)}", 15f, negro, 24f)
        texto("Balance:    ${MoneyFormat.formatear(reporte.balance)}", 15f, negro, 24f)
        val tasa = reporte.tasaAhorro
        texto("Tasa de ahorro: ${if (tasa == null) "—" else "${"%.1f".format(tasa)} %"}", 15f, negro, 24f)
        texto("Gasto proyectado: ${MoneyFormat.formatear(reporte.gastoProyectado)}", 13f, gris, 40f)

        // Por categoría
        canvas.drawLine(margen, y, ancho - margen, y, linea)
        y += 28f
        texto("GASTO POR CATEGORIA", 12f, gris, 24f)
        if (reporte.categorias.isEmpty()) {
            texto("Sin gastos registrados.", 12f, gris, 22f)
        } else {
            reporte.categorias.forEach { c ->
                texto(
                    "${c.nombre}:  ${MoneyFormat.formatear(c.monto)}  (${"%.0f".format(c.porcentaje)} %)",
                    12f, negro, 22f
                )
            }
        }
        y += 20f

        // Metas
        canvas.drawLine(margen, y, ancho - margen, y, linea)
        y += 28f
        texto("METAS", 12f, gris, 24f)
        if (reporte.metas.isEmpty()) {
            texto("Sin metas definidas.", 12f, gris, 22f)
        } else {
            reporte.metas.forEach { m ->
                val estado = if (m.alcanzable) " - YA LA PODES CUMPLIR" else ""
                texto(
                    "${m.nombre}:  ${MoneyFormat.formatear(m.actual)} / ${MoneyFormat.formatear(m.objetivo)}$estado",
                    12f, negro, 22f
                )
            }
        }

        // Pie
        y = alto - 40f
        texto("Generado por Platica.", 9f, gris, 12f)

        documento.finishPage(pagina)

        context.contentResolver.openOutputStream(uri)?.use { salida ->
            documento.writeTo(salida)
        }
        documento.close()
    }
}
