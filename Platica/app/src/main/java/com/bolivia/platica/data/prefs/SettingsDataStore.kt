package com.bolivia.platica.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/** Modo de tema de la app. */
enum class ModoTema { CLARO, OSCURO, SISTEMA }

/** Método de bloqueo de la app. */
enum class MetodoBloqueo { NINGUNO, HUELLA, PIN, HUELLA_O_PIN }

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "platica_ajustes")

/**
 * Preferencias de la app (DataStore). Ver `docs/07_seguridad_y_respaldo.md`.
 */
@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private object Keys {
        val TEMA = stringPreferencesKey("tema")
        val METODO_BLOQUEO = stringPreferencesKey("metodo_bloqueo")
        val PIN = stringPreferencesKey("pin")
        val BLOQUEO_ACTIVO = booleanPreferencesKey("bloqueo_activo")
    }

    val modoTema: Flow<ModoTema> = context.dataStore.data.map { prefs ->
        runCatching { ModoTema.valueOf(prefs[Keys.TEMA] ?: ModoTema.CLARO.name) }
            .getOrDefault(ModoTema.CLARO)
    }

    val metodoBloqueo: Flow<MetodoBloqueo> = context.dataStore.data.map { prefs ->
        runCatching { MetodoBloqueo.valueOf(prefs[Keys.METODO_BLOQUEO] ?: MetodoBloqueo.NINGUNO.name) }
            .getOrDefault(MetodoBloqueo.NINGUNO)
    }

    val bloqueActivo: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.BLOQUEO_ACTIVO] ?: false
    }

    val pinGuardado: Flow<String?> = context.dataStore.data.map { it[Keys.PIN] }

    suspend fun setModoTema(modo: ModoTema) {
        context.dataStore.edit { it[Keys.TEMA] = modo.name }
    }

    suspend fun setMetodoBloqueo(metodo: MetodoBloqueo) {
        context.dataStore.edit { it[Keys.METODO_BLOQUEO] = metodo.name }
    }

    /** Guarda el PIN (hasheado se hace antes de llamar). */
    suspend fun setPin(pin: String?) {
        context.dataStore.edit { prefs ->
            if (pin == null) prefs.remove(Keys.PIN) else prefs[Keys.PIN] = pin
        }
    }

    suspend fun setBloqueActivo(activo: Boolean) {
        context.dataStore.edit { it[Keys.BLOQUEO_ACTIVO] = activo }
    }
}
