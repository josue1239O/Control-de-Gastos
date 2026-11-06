package com.bolivia.platica.security

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL

/**
 * Utilidades de biometría. Android usa las huellas que el usuario ya
 * registró en el teléfono (la app no guarda huellas propias).
 * Ver `docs/07_seguridad_y_respaldo.md` §4.
 */
object BiometriaUtils {

    const val AUTENTICADORES = BIOMETRIC_STRONG or BIOMETRIC_WEAK or DEVICE_CREDENTIAL

    /** ¿El dispositivo puede autenticar con huella? */
    fun huellaDisponible(context: Context): Boolean {
        val manager = BiometricManager.from(context)
        return manager.canAuthenticate(BIOMETRIC_STRONG or BIOMETRIC_WEAK) ==
                BiometricManager.BIOMETRIC_SUCCESS
    }

    /** ¿El dispositivo tiene configurado algún método (huella o PIN del sistema)? */
    fun algunMetodoDisponible(context: Context): Boolean {
        val manager = BiometricManager.from(context)
        return manager.canAuthenticate(AUTENTICADORES) == BiometricManager.BIOMETRIC_SUCCESS
    }

    /** Mensaje de estado de la biometría. */
    fun descripcionDisponibilidad(context: Context): String {
        val manager = BiometricManager.from(context)
        return when (manager.canAuthenticate(BIOMETRIC_STRONG or BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS ->
                "Listo. Usá tu huella para entrar."
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                "Este teléfono no tiene sensor de huella."
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                "El sensor no está disponible ahora."
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                "No tenés huellas registradas. Registrá una en los ajustes del teléfono."
            else -> "Biometría no disponible."
        }
    }
}
