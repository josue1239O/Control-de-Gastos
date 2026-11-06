package com.bolivia.platica.ui.screens.bloqueo

import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bolivia.platica.data.prefs.MetodoBloqueo
import com.bolivia.platica.ui.theme.platica

/**
 * Pantalla de bloqueo. Pide huella y/o PIN según la configuración.
 * Ver `docs/07_seguridad_y_respaldo.md` §4.
 */
@Composable
fun BloqueoScreen(
    onDesbloqueado: () -> Unit,
    viewModel: BloqueoViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var pin by remember { mutableStateOf("") }
    val platica = MaterialTheme.platica

    // Intentar huella automáticamente al abrir (si corresponde)
    LaunchedEffect(state.metodo) {
        val usaHuella = state.metodo == MetodoBloqueo.HUELLA || state.metodo == MetodoBloqueo.HUELLA_O_PIN
        if (usaHuella && context is FragmentActivity) {
            val prompt = BiometricPrompt(
                context,
                ContextCompat.getMainExecutor(context),
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        onDesbloqueado()
                    }
                }
            )
            val info = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Platica")
                .setSubtitle("Desbloqueá para entrar")
                .setNegativeButtonText("Usar PIN")
                .setAllowedAuthenticators(
                    androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG or
                            androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
                )
                .build()
            prompt.authenticate(info)
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .padding(28.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Filled.Lock,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(12.dp))
            Text("Platica está bloqueada", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(
                "Tus datos están protegidos.",
                style = MaterialTheme.typography.labelSmall,
                color = platica.textoSecundario
            )
            Spacer(Modifier.height(28.dp))

            val usaPin = state.metodo == MetodoBloqueo.PIN || state.metodo == MetodoBloqueo.HUELLA_O_PIN
            if (usaPin) {
                OutlinedTextField(
                    value = pin,
                    onValueChange = { pin = it.filter { c -> c.isDigit() }.take(6) },
                    label = { Text("PIN") },
                    isError = state.pinError,
                    supportingText = if (state.pinError) {
                        { Text("PIN incorrecto") }
                    } else null,
                    singleLine = true
                )
                Spacer(Modifier.height(8.dp))
                TextButton(onClick = {
                    viewModel.verificarPin(pin) { ok -> if (ok) onDesbloqueado() }
                }) {
                    Text("DESBLOQUEAR", fontWeight = FontWeight.Bold)
                }
            }

            val usaHuella = state.metodo == MetodoBloqueo.HUELLA || state.metodo == MetodoBloqueo.HUELLA_O_PIN
            if (usaHuella && context is FragmentActivity) {
                Spacer(Modifier.height(16.dp))
                Icon(
                    Icons.Filled.Fingerprint,
                    contentDescription = "Usar huella",
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
                TextButton(onClick = {
                    val prompt = BiometricPrompt(
                        context,
                        ContextCompat.getMainExecutor(context),
                        object : BiometricPrompt.AuthenticationCallback() {
                            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                                onDesbloqueado()
                            }
                        }
                    )
                    val info = BiometricPrompt.PromptInfo.Builder()
                        .setTitle("Platica")
                        .setSubtitle("Desbloqueá para entrar")
                        .setNegativeButtonText("Cancelar")
                        .setAllowedAuthenticators(
                            androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG or
                                    androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
                        )
                        .build()
                    prompt.authenticate(info)
                }) { Text("Usar huella") }
            }
        }
    }
}
