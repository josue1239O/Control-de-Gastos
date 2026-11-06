package com.bolivia.platica.ui.screens.bloqueo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bolivia.platica.data.prefs.MetodoBloqueo
import com.bolivia.platica.data.prefs.SettingsDataStore
import com.bolivia.platica.security.PinUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BloqueoUiState(
    val metodo: MetodoBloqueo = MetodoBloqueo.NINGUNO,
    val pinError: Boolean = false
)

@HiltViewModel
class BloqueoViewModel @Inject constructor(
    private val settings: SettingsDataStore
) : ViewModel() {

    private val _pinError = MutableStateFlow(false)

    val uiState: StateFlow<BloqueoUiState> =
        combine(settings.metodoBloqueo, _pinError) { metodo, error ->
            BloqueoUiState(metodo = metodo, pinError = error)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = BloqueoUiState()
        )

    /** Verifica el PIN ingresado. Devuelve true si es correcto. */
    fun verificarPin(pin: String, onResultado: (Boolean) -> Unit) {
        viewModelScope.launch {
            val hash = settings.pinGuardado.first()
            val ok = PinUtils.verificar(pin, hash)
            _pinError.value = !ok
            onResultado(ok)
        }
    }
}
