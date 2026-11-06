package com.bolivia.platica.ui.screens.respaldo

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bolivia.platica.data.backup.BackupManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RespaldoViewModel @Inject constructor(
    private val backupManager: BackupManager
) : ViewModel() {

    fun exportar(context: Context, uri: Uri, onResultado: (Boolean) -> Unit) {
        viewModelScope.launch {
            val ok = withContext(Dispatchers.IO) { backupManager.exportar(context, uri) }
            onResultado(ok)
        }
    }

    fun importar(context: Context, uri: Uri, onResultado: (Boolean) -> Unit) {
        viewModelScope.launch {
            val ok = withContext(Dispatchers.IO) { backupManager.importar(context, uri) }
            onResultado(ok)
        }
    }
}
