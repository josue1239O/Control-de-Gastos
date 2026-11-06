package com.bolivia.platica

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import com.bolivia.platica.data.prefs.MetodoBloqueo
import com.bolivia.platica.data.prefs.ModoTema
import com.bolivia.platica.data.prefs.SettingsDataStore
import com.bolivia.platica.ui.navigation.PlaticaNavGraph
import com.bolivia.platica.ui.screens.bloqueo.BloqueoScreen
import com.bolivia.platica.ui.theme.PlaticaTheme
import com.bolivia.platica.ui.theme.resolverTemaOscuro
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Actividad principal. Extiende FragmentActivity para poder usar BiometricPrompt.
 */
@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject lateinit var settings: SettingsDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val modo by settings.modoTema.collectAsState(initial = ModoTema.CLARO)
            val metodo by settings.metodoBloqueo.collectAsState(initial = null)

            PlaticaTheme(darkTheme = resolverTemaOscuro(modo)) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    when (val m = metodo) {
                        null -> {
                            // Todavía se está leyendo la preferencia: no mostramos nada sensible.
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                        MetodoBloqueo.NINGUNO -> PlaticaNavGraph()
                        else -> {
                            // Bloqueo activo: pedir desbloqueo.
                            var desbloqueado by remember { mutableStateOf(false) }
                            if (desbloqueado) {
                                PlaticaNavGraph()
                            } else {
                                BloqueoScreen(onDesbloqueado = { desbloqueado = true })
                            }
                        }
                    }
                }
            }
        }
    }
}
