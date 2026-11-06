package com.bolivia.platica.data.local.security

import android.content.Context
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

/**
 * Fábrica del soporte de cifrado para Room usando SQLCipher.
 *
 * Room abre la base SQLite a través de esta SupportFactory, de modo que
 * el archivo `.db` queda cifrado con AES-256 (Ver `docs/07` §3).
 */
object DatabaseFactory {

    fun crearSupportFactory(context: Context): SupportFactory {
        val passphrase = SecureKeyManager(context).obtenerPassphrase()
        return SupportFactory(passphrase)
    }

    /** Carga las librerías nativas de SQLCipher. Debe llamarse antes de abrir la BD. */
    fun cargarLibrerias(context: Context) {
        SQLiteDatabase.loadLibs(context)
    }
}
