package com.bolivia.platica

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.bolivia.platica.data.repository.QuickTemplateRepositoryImpl
import com.bolivia.platica.domain.repository.AccountRepository
import com.bolivia.platica.domain.repository.CategoryRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Clase Application de Platica. Inicializa Hilt y siembra los datos
 * iniciales (categorías, cuentas y plantillas de gasto rápido) la primera vez.
 */
@HiltAndroidApp
class PlaticaApp : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory
    @Inject lateinit var categoryRepository: CategoryRepository
    @Inject lateinit var accountRepository: AccountRepository
    @Inject lateinit var quickTemplateRepository: QuickTemplateRepositoryImpl

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        appScope.launch {
            categoryRepository.sembrarIniciales()
            accountRepository.sembrarCuentasIniciales()

            val cuenta = accountRepository.observarActivas().first().firstOrNull()
            val categoriaTransporte = categoryRepository.observarActivas()
                .first().firstOrNull { it.nombre == "Transporte" }
            if (cuenta != null) {
                quickTemplateRepository.sembrarIniciales(
                    cuentaId = cuenta.id,
                    categoriaTransporteId = categoriaTransporte?.id ?: 0L
                )
            }
        }
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
