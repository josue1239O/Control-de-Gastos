# 08 — Tecnología y Stack Técnico

> **Norma de referencia:** ISO/IEC/IEEE 12207:2017 §6.4.5 (Proceso de definición de la arquitectura) e ISO/IEC 25010 (Calidad).

---

## 1. En qué consiste el proyecto (resumen técnico)

**Platica** es una app **Android nativa** de finanzas personales **offline-first**. Su arquitectura es en capas (UI/Dominio/Datos), con persistencia local cifrada y **cero dependencias de red**. Ver detalle de propósito en `01_vision_y_alcance.md`.

---

## 2. Stack tecnológico seleccionado

| Área | Tecnología | Versión objetivo | Motivo |
|---|---|---|---|
| Lenguaje | **Kotlin** | 2.x | Estándar Android, null-safety, coroutines. |
| UI | **Jetpack Compose** (Material 3) | BOM reciente | Declarativo, responsive, moderno. |
| Arquitectura | MVVM + Clean (minimalista) | — | Separación y testabilidad. |
| Persistencia | **Room** | 2.6+ | Estándar, integración con Flow, migraciones. |
| Cifrado BD | **SQLCipher (Android)** | 4.5+ | AES-256 transparente. |
| Preferencias | **DataStore (Proto/Preferences)** | 1.1+ | Reemplazo de SharedPreferences, seguro y asíncrono. |
| Seguridad | **Android Keystore** + **BiometricPrompt** | — | Clave protegida por hardware; biometría nativa. |
| Tareas | **WorkManager** | 2.9+ | Recurrentes, alertas y respaldos locales. |
| Gráficos | **Vico** | 2.x | Offline, Compose, sin red. |
| Widget | **Jetpack Glance** | 1.1+ | Widgets con Compose. |
| Inyección | **Hilt** | 2.5x | DI estándar. |
| Serialización | **kotlinx.serialization** | 1.6+ | JSON para respaldos, sin reflexión. |
| Pruebas | JUnit5/Kotlin Test, MockK, Turbine, Compose UI Test, Robolectric | — | Cobertura por capas. |
| Build | **Gradle (Kotlin DSL)** + AGP | 8.x | Moderno, rápido. |
| CI local | Gradle tasks + Lint | — | Sin servicios externos obligatorios. |

---

## 3. Matriz de decisión tecnológica

### 3.1 Framework multiplataforma

| Criterio (peso) | Kotlin Nativo | Flutter | React Native |
|---|---|---|---|
| Rendimiento (25%) | 5 | 4 | 3 |
| Offline/DB cifrada (20%) | 5 | 4 | 3 |
| APK liviano (15%) | 5 | 3 | 3 |
| Ecosistema seguridad (15%) | 5 | 3 | 3 |
| Migrable a iOS (10%) | 3 (KMP) | 5 | 5 |
| Familiaridad/curva (15%) | 4 | 4 | 3 |
| **Total ponderado** | **4.70** | **3.85** | **3.30** |

**Decisión:** **Kotlin nativo** ahora; arquitectura preparada para **KMP** en el futuro (separando lógica en módulos comunes).

### 3.2 Base de datos

| Criterio | Room + SQLCipher | Realm | ObjectBox |
|---|---|---|---|
| Integración Compose/Flow | Excelente | Buena | Buena |
| Cifrado | SQLCipher AES-256 | AES-256 | AES |
| Madurez | Muy alta | Alta | Media |
| Migraciones | Robustas | Medias | Medias |
| **Decisión** | ✅ **Elegida** | | |

---

## 4. Dependencias (referencia para `build.gradle.kts`)

```kotlin
dependencies {
    // Compose
    implementation(platform("androidx.compose:compose-bom:2024.09.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation("androidx.navigation:navigation-compose:2.8.0")

    // Room + SQLCipher
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    implementation("net.zetetic:android-database-sqlcipher:4.5.4")
    implementation("androidx.sqlite:sqlite-ktx:2.4.0")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // Seguridad
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    implementation("androidx.biometric:biometric:1.2.0-alpha05")

    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.9.1")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.52")
    ksp("com.google.dagger:hilt-android-compiler:2.52")
    implementation("androidx.hilt:hilt-work:1.2.0")
    ksp("androidx.hilt:hilt-compiler:1.2.0")

    // Gráficos
    implementation("com.patrykandpatrick.vico:compose-m3:2.0.0-beta.2")

    // Widget
    implementation("androidx.glance:glance-appwidget:1.1.0")

    // Serialización
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    // ⚠️ NINGUNA dependencia de red (no Retrofit/OkHttp/Firebase)
}
```

---

## 5. Configuración de seguridad del build

```kotlin
android {
    defaultConfig {
        minSdk = 26
        targetSdk = 35
        // ...
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"),
                          "proguard-rules.pro")
        }
    }
}
```

**Reglas adicionales:**
- R8/ProGuard activo con reglas para SQLCipher y kotlinx.serialization.
- `android:allowBackup="false"` para no exponer datos por backup de Android (se usa el respaldo propio).
- `android:usesCleartextTraffic` irrelevante (no hay red).

---

## 6. Compatibilidad y responsive

- **minSdk 26** (Android 8.0) → cubre equipos de gama baja comunes.
- **WindowSizeClass** para adaptar el layout a celular/tablet/plegable.
- Recursos y textos en **Español (Bolivia)**, formato de moneda BOB.
- Soporte de **modo oscuro** y **theme dinámico** (Material You).

---

## 7. Por qué es idóneo para Bolivia y estudiantes

| Necesidad | Cómo lo cubre el stack |
|---|---|
| Datos móviles caros/intermitentes | Funciona 100 % offline. |
| Equipos de gama baja | Nativo + minSdk 26 + APK liviano. |
| Privacidad | Cero red, BD cifrada. |
| Facilidad de uso | Compose + flujo de 4 toques. |
| Ingresos irregulares | Ciclo configurable y marcado de ingresos irregulares. |
| Moneda local | Formato BOB (Bs.). |

---

## 8. Alternativas evaluadas y descartadas

| Alternativa | Motivo de descarte |
|---|---|
| Firebase (Firestore/Auth) | Requiere red; contradice el requisito offline. |
| SQLite crudo (sin Room) | Más errores, sin Flow ni migraciones cómodas. |
| SharedPreferences | Obsoleto para datos estructurados. |
| Backend propio | Innecesario; costo y riesgo de privacidad. |
| Realm | Ecosistema y licencia menos convenientes que Room. |

---

**Fin del documento 08.**
