package com.bolivia.platica.security

import java.security.MessageDigest

/**
 * Utilidades para guardar el PIN de forma segura (nunca en texto plano).
 * Ver `docs/07_seguridad_y_respaldo.md` §4.
 */
object PinUtils {

    private const val SALT = "platica_v1_salt"

    /** Devuelve el hash SHA-256 del PIN (hex). */
    fun hashear(pin: String): String {
        val bytes = MessageDigest.getInstance("SHA-256")
            .digest((SALT + pin).toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    /** Verifica si un PIN coincide con el hash guardado. */
    fun verificar(pin: String, hashGuardado: String?): Boolean {
        if (hashGuardado.isNullOrBlank()) return false
        return hashear(pin) == hashGuardado
    }
}
