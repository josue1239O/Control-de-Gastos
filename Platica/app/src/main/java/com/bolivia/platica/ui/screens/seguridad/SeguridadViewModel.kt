package com.bolivia.platica.ui.screens.seguridad

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bolivia.platica.data.prefs.MetodoBloqueo
import com.bolivia.platica.data.prefs.SettingsDataStore
import com.bolivia.platica.security.PinUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SeguridadUiState(
    val metodo: MetodoBloqueo = MetodoBloqueo.NINGUNO,
    val tienePin: Boolean = false
)

@HiltViewModel
class SeguridadViewModel @Inject constructor(
    private val settings: SettingsDataStore
) : ViewModel() {

    val uiState: StateFlow<SeguridadUiState> =
        combine(settings.metodoBloqueo, settings.pinGuardado) { metodo, pin ->
            SeguridadUiState(metodo = metodo, tienePin = !pin.isNullOrBlank())
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SeguridadUiState()
        )

    fun cambiarMetodo(metodo: MetodoBloqueo) {
        viewModelScope.launch {
            settings.setMetodoBloqueo(metodo)
            settings.setBloqueActivo(metodo != MetodoBloqueo.NINGUNO)
        }
    }

    fun guardarPin(pin: String) {
        viewModelScope.launch {
            settings.setPin(PinUtils.hashear(pin))
            settings.setMetodoBloqueo(MetodoBloqueo.PIN)
            settings.setBloqueActivo(true)
        }
    }

    fun quitarPin() {
        viewModelScope.launch {
            settings.setPin(null)
        }
    }
}
