package com.bolivia.platica.domain.usecase

import com.bolivia.platica.data.local.entity.AccountEntity
import com.bolivia.platica.data.local.entity.GoalContributionEntity
import com.bolivia.platica.data.local.entity.TransactionEntity
import com.bolivia.platica.domain.model.enums.AccountType
import com.bolivia.platica.domain.model.enums.TransactionType

/**
 * Casos de uso de cálculo de saldos (Módulo M1).
 * Ver `docs/05_logica_de_negocio.md` §2.
 */
object CalculadoraSaldos {

    /**
     * Saldo real de cada cuenta:
     *   saldo inicial + ingresos − gastos − aportes a metas.
     *
     * Los movimientos son la fuente de verdad: si registrás un gasto,
     * el saldo disponible baja; si registrás un ingreso, sube.
     */
    fun saldosPorCuenta(
        cuentas: List<AccountEntity>,
        transacciones: List<TransactionEntity>,
        aportes: List<GoalContributionEntity> = emptyList()
    ): Map<Long, Double> {
        val saldos = cuentas.associate { it.id to it.saldoInicial }.toMutableMap()

        transacciones.forEach { t ->
            val delta = when (t.tipo) {
                TransactionType.INGRESO -> t.monto
                TransactionType.GASTO -> -t.monto
                TransactionType.APORTE_META -> -t.monto
            }
            if (saldos.containsKey(t.accountId)) {
                saldos[t.accountId] = (saldos[t.accountId] ?: 0.0) + delta
            }
        }

        aportes.forEach { a ->
            if (saldos.containsKey(a.accountId)) {
                saldos[a.accountId] = (saldos[a.accountId] ?: 0.0) - a.monto
            }
        }

        return saldos
    }

    /**
     * Saldo total disponible = efectivo + billeteras QR.
     * Excluye inversión, emergencias y ahorro (RN-02).
     */
    fun saldoDisponible(saldosPorCuenta: Map<Long, Double>, cuentas: List<AccountEntity>): Double {
        return cuentas
            .filter { it.activa && (it.tipo == AccountType.EFECTIVO || it.tipo == AccountType.BILLETERA_QR) }
            .sumOf { saldosPorCuenta[it.id] ?: it.saldoInicial }
    }

    /** Patrimonio invertido = suma de cuentas de tipo Inversión. */
    fun patrimonioInvertido(saldosPorCuenta: Map<Long, Double>, cuentas: List<AccountEntity>): Double {
        return cuentas
            .filter { it.activa && it.tipo == AccountType.INVERSION }
            .sumOf { saldosPorCuenta[it.id] ?: it.saldoInicial }
    }

    /** Total reservado para emergencias. */
    fun reservaEmergencias(saldosPorCuenta: Map<Long, Double>, cuentas: List<AccountEntity>): Double {
        return cuentas
            .filter { it.activa && it.tipo == AccountType.EMERGENCIA }
            .sumOf { saldosPorCuenta[it.id] ?: it.saldoInicial }
    }

    /** Total reservado como ahorro. */
    fun reservaAhorro(saldosPorCuenta: Map<Long, Double>, cuentas: List<AccountEntity>): Double {
        return cuentas
            .filter { it.activa && it.tipo == AccountType.AHORRO }
            .sumOf { saldosPorCuenta[it.id] ?: it.saldoInicial }
    }
}

/**
 * Casos de uso de análisis (Módulo M3).
 * Ver `docs/05_logica_de_negocio.md` §3–§6.
 */
object Analitica {

    /** Balance del ciclo = ingresos − gastos. */
    fun balance(ingresos: Double, gastos: Double): Double = ingresos - gastos

    /** Tasa de ahorro (%); null si no hay ingresos. */
    fun tasaAhorro(ingresos: Double, gastos: Double): Double? {
        if (ingresos <= 0.0) return null
        return ((ingresos - gastos) / ingresos) * 100.0
    }

    /** Gasto proyectado a fin de ciclo según el ritmo actual. */
    fun gastoProyectado(gastosActuales: Double, diasTranscurridos: Int, diasTotales: Int): Double {
        if (diasTranscurridos <= 0) return 0.0
        return (gastosActuales / diasTranscurridos) * diasTotales
    }

    /** Porcentaje que representa [parte] sobre [total]. */
    fun porcentaje(parte: Double, total: Double): Double {
        if (total <= 0.0) return 0.0
        return (parte / total) * 100.0
    }
}

/**
 * Casos de uso de metas (Módulo M4).
 * Ver `docs/05_logica_de_negocio.md` §7.
 */
object CalculadoraMetas {

    fun progreso(montoActual: Double, montoObjetivo: Double): Double {
        if (montoObjetivo <= 0.0) return 0.0
        return ((montoActual / montoObjetivo) * 100.0).coerceIn(0.0, 100.0)
    }

    fun restante(montoActual: Double, montoObjetivo: Double): Double =
        (montoObjetivo - montoActual).coerceAtLeast(0.0)

    /**
     * Proyección/cumplimiento: ¿a este ritmo de ahorro llegaré a la meta?
     *
     * @param ahorroMensual cuánto se puede guardar por mes (estimado).
     * @return null si no hay fecha límite o si el ahorro mensual es <= 0.
     */
    fun mesesParaLograr(montoActual: Double, montoObjetivo: Double, ahorroMensual: Double): Int? {
        val falta = restante(montoActual, montoObjetivo)
        if (falta <= 0.0) return 0
        if (ahorroMensual <= 0.0) return null
        return kotlin.math.ceil(falta / ahorroMensual).toInt()
    }

    /**
     * ¿Se cumple la meta en la fecha deseada dado un ahorro mensual?
     */
    fun seCumpleEnFecha(
        montoActual: Double,
        montoObjetivo: Double,
        ahorroMensual: Double,
        mesesDisponibles: Int?
    ): Boolean {
        if (mesesDisponibles == null) return false
        if (restante(montoActual, montoObjetivo) <= 0.0) return true
        if (ahorroMensual <= 0.0) return false
        val mesesNecesarios = mesesParaLograr(montoActual, montoObjetivo, ahorroMensual) ?: return false
        return mesesNecesarios <= mesesDisponibles
    }

    /** Aporte mensual necesario para cumplir la meta en la fecha deseada. */
    fun aporteNecesarioMensual(
        montoActual: Double,
        montoObjetivo: Double,
        mesesDisponibles: Int?
    ): Double? {
        if (mesesDisponibles == null || mesesDisponibles <= 0) return null
        val falta = restante(montoActual, montoObjetivo)
        if (falta <= 0.0) return 0.0
        return falta / mesesDisponibles
    }

    /**
     * Dado el saldo de ahorro, indica qué metas se pueden comprar ya
     * (aquellas cuyo monto_objetivo <= saldoAhorro).
     * Devuelve la lista ordenada de metas alcanzables y cuántas son.
     */
    fun metasAlcanzables(
        metas: List<com.bolivia.platica.data.local.entity.SavingsGoalEntity>,
        saldoAhorro: Double
    ): List<com.bolivia.platica.data.local.entity.SavingsGoalEntity> {
        return metas
            .filter { it.montoObjetivo <= saldoAhorro && it.montoObjetivo > 0 }
            .sortedWith(compareBy({ it.prioridad.ordinal }, { it.montoObjetivo }))
    }
}
