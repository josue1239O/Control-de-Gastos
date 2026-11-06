package com.bolivia.platica.ui.screens.apariencia

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bolivia.platica.data.prefs.ModoTema
import com.bolivia.platica.data.prefs.SettingsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AparienciaViewModel @Inject constructor(
    private val settings: SettingsDataStore
) : ViewModel() {

    val modoTema: StateFlow<ModoTema> = settings.modoTema.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ModoTema.CLARO
    )

    fun cambiarTema(modo: ModoTema) {
        viewModelScope.launch { settings.setModoTema(modo) }
    }
}
