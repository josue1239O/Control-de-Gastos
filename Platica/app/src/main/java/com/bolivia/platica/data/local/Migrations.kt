package com.bolivia.platica.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * v1 -> v2:
 *  - Nuevos tipos de cuenta (EMERGENCIA, AHORRO): son String, no requieren cambio de esquema.
 *  - savings_goals: se agrega la columna `descripcion`.
 *  - Nueva tabla `investment_returns`.
 */
private val migracion1a2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE savings_goals ADD COLUMN descripcion TEXT")
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `investment_returns` (
                `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                `accountId` INTEGER NOT NULL,
                `ganancia` REAL NOT NULL,
                `fecha` INTEGER NOT NULL,
                `reinvertido` REAL NOT NULL DEFAULT 0,
                `nota` TEXT,
                `creadaEn` INTEGER NOT NULL,
                FOREIGN KEY(`accountId`) REFERENCES `accounts`(`id`) ON DELETE CASCADE
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_investment_returns_accountId` ON `investment_returns`(`accountId`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_investment_returns_fecha` ON `investment_returns`(`fecha`)")
    }
}

/**
 * v2 -> v3:
 *  - Nueva tabla `quick_templates` (plantillas de gasto rápido de precio fijo).
 */
private val migracion2a3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `quick_templates` (
                `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                `nombre` TEXT NOT NULL,
                `monto` REAL NOT NULL,
                `tipo` TEXT NOT NULL,
                `accountId` INTEGER NOT NULL,
                `categoryId` INTEGER,
                `icono` TEXT,
                `orden` INTEGER NOT NULL DEFAULT 0,
                FOREIGN KEY(`accountId`) REFERENCES `accounts`(`id`) ON DELETE CASCADE,
                FOREIGN KEY(`categoryId`) REFERENCES `categories`(`id`) ON DELETE SET NULL
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_quick_templates_accountId` ON `quick_templates`(`accountId`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_quick_templates_categoryId` ON `quick_templates`(`categoryId`)")
    }
}

/**
 * v3 -> v4:
 *  - savings_goals: se agrega `prioridad` (ALTA / MEDIA / BAJA).
 *    Las metas existentes quedan en prioridad MEDIA.
 */
private val migracion3a4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "ALTER TABLE savings_goals ADD COLUMN prioridad TEXT NOT NULL DEFAULT 'MEDIA'"
        )
    }
}

/**
 * Lista de migraciones de esquema de Room.
 * Al subir la versión de la base de datos se agregan aquí las migraciones.
 * Ver `docs/04_modelo_de_datos.md` §6.
 */
val MIGRATIONS: Array<Migration> = arrayOf(migracion1a2, migracion2a3, migracion3a4)
