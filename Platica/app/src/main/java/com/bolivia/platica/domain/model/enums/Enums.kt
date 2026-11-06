package com.bolivia.platica.domain.model.enums

/**
 * Tipo de cuenta / fuente de dinero.
 */
enum class AccountType {
    /** Dinero en mano. */
    EFECTIVO,

    /** Billeteras digitales y pagos por QR (dinero de uso rápido). */
    BILLETERA_QR,

    /** Inversiones / dinero que genera ganancia y sigue circulando. */
    INVERSION,

    /** Reserva para emergencias (dinero guardado para imprevistos). */
    EMERGENCIA,

    /** Reserva de ahorro (dinero guardado con un fin de ahorro). */
    AHORRO;

    val etiqueta: String
        get() = when (this) {
            EFECTIVO -> "Efectivo"
            BILLETERA_QR -> "Billetera / QR"
            INVERSION -> "Inversión"
            EMERGENCIA -> "Emergencias"
            AHORRO -> "Ahorro"
        }
}

/** Tipo de categoría. */
enum class CategoryType { INGRESO, GASTO }

/** Tipo de transacción. */
enum class TransactionType {
    INGRESO,
    GASTO,
    APORTE_META;

    val etiqueta: String
        get() = when (this) {
            INGRESO -> "Ingreso"
            GASTO -> "Gasto"
            APORTE_META -> "Aporte a meta"
        }
}

/** Estado de una meta de ahorro. */
enum class GoalStatus { ACTIVA, COMPLETADA, PAUSADA, CANCELADA }

/**
 * Prioridad de una meta: sirve para ordenar y destacar lo que el usuario
 * quiere lograr primero. El orden del enum es el orden de importancia.
 */
enum class GoalPriority {
    ALTA,
    MEDIA,
    BAJA;

    val etiqueta: String
        get() = when (this) {
            ALTA -> "Alta"
            MEDIA -> "Media"
            BAJA -> "Baja"
        }
}

/** Periodo de un presupuesto. */
enum class BudgetPeriod { MENSUAL, SEMANAL }

/** Frecuencia de una transacción recurrente. */
enum class Frequency { DIARIA, SEMANAL, MENSUAL, ANUAL }
