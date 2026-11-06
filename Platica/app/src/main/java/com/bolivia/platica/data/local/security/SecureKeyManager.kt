package com.bolivia.platica.data.local.security

import android.content.Context
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Gestiona la clave (passphrase) con la que se cifra la base de datos SQLCipher.
 *
 * La passphrase aleatoria NUNCA se guarda en texto plano: se almacena cifrada
 * dentro de EncryptedSharedPreferences, cuya clave maestra vive en el
 * Android Keystore (protegida por hardware cuando el equipo lo soporta).
 *
 * Ver `docs/07_seguridad_y_respaldo.md` §3.
 */
class SecureKeyManager(private val context: Context) {

    companion object {
        private const val PREFS_FILE = "platica_secure_prefs"
        private const val PREF_DB_PASSPHRASE = "db_passphrase"
        private const val PASSPHRASE_BYTES = 32
    }

    /**
     * Clave maestra administrada por androidx.security (alias gestionado
     * internamente, sin conflictos). Se usa para cifrar las preferencias.
     */
    private val masterKey: MasterKey by lazy {
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    private fun prefs() = EncryptedSharedPreferences.create(
        context,
        PREFS_FILE,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    /**
     * Devuelve la passphrase de la base de datos, generándola la primera vez.
     * Se obtiene como bytes para pasarla a SQLCipher (SupportFactory).
     */
    fun obtenerPassphrase(): ByteArray {
        val prefs = prefs()
        val existente = prefs.getString(PREF_DB_PASSPHRASE, null)
        if (existente != null) {
            return Base64.decode(existente, Base64.NO_WRAP)
        }
        val nueva = ByteArray(PASSPHRASE_BYTES).also { java.security.SecureRandom().nextBytes(it) }
        prefs.edit()
            .putString(PREF_DB_PASSPHRASE, Base64.encodeToString(nueva, Base64.NO_WRAP))
            .apply()
        return nueva
    }
}
