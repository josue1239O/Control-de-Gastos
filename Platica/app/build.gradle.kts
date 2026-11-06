import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.plugin.serialization")
}

/*
 * Firma del APK de release.
 *
 * Las credenciales NO se guardan en el repositorio. Se leen, en este orden:
 *   1. `keystore.properties` en la raíz del proyecto (ignorado por Git).
 *   2. Variables de entorno: PLATICA_STORE_FILE, PLATICA_STORE_PASSWORD,
 *      PLATICA_KEY_ALIAS y PLATICA_KEY_PASSWORD.
 *
 * Si no hay credenciales, el release se genera sin firmar.
 */
val keystoreProperties = Properties().apply {
    val archivo = rootProject.file("keystore.properties")
    if (archivo.exists()) archivo.inputStream().use { load(it) }
}

fun valorFirma(propiedad: String, variableEntorno: String): String? =
    keystoreProperties.getProperty(propiedad) ?: System.getenv(variableEntorno)

val rutaKeystore = valorFirma("storeFile", "PLATICA_STORE_FILE")
val passwordKeystore = valorFirma("storePassword", "PLATICA_STORE_PASSWORD")
val aliasClave = valorFirma("keyAlias", "PLATICA_KEY_ALIAS")
val passwordClave = valorFirma("keyPassword", "PLATICA_KEY_PASSWORD")
val firmaConfigurada = listOf(rutaKeystore, passwordKeystore, aliasClave, passwordClave)
    .all { !it.isNullOrBlank() }

android {
    namespace = "com.bolivia.platica"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.bolivia.platica"
        minSdk = 26
        targetSdk = 35
        versionCode = 3
        versionName = "1.2.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables { useSupportLibrary = true }
    }

    signingConfigs {
        if (firmaConfigurada) {
            create("release") {
                storeFile = rootProject.file(rutaKeystore!!)
                storePassword = passwordKeystore
                keyAlias = aliasClave
                keyPassword = passwordClave
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            if (firmaConfigurada) {
                signingConfig = signingConfigs.getByName("release")
            }
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {
    // ---------- Compose ----------
    implementation(platform("androidx.compose:compose-bom:2024.09.03"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.navigation:navigation-compose:2.8.4")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")

    // ---------- Room ----------
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // ---------- SQLCipher (cifrado AES-256) ----------
    implementation("net.zetetic:android-database-sqlcipher:4.5.4")
    implementation("androidx.sqlite:sqlite-ktx:2.4.0")

    // ---------- DataStore ----------
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // ---------- Seguridad ----------
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    implementation("androidx.biometric:biometric:1.1.0")

    // ---------- WorkManager ----------
    implementation("androidx.work:work-runtime-ktx:2.10.0")
    implementation("androidx.hilt:hilt-work:1.2.0")

    // ---------- Hilt ----------
    implementation("com.google.dagger:hilt-android:2.52")
    ksp("com.google.dagger:hilt-android-compiler:2.52")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    ksp("androidx.hilt:hilt-compiler:1.2.0")

    // ---------- Serialización JSON (respaldos) ----------
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

    // ---------- Coroutines ----------
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    // ================================================================
    //  ⚠️ IMPORTANTE: NO se incluye NINGUNA dependencia de red.
    //  Sin Retrofit, sin OkHttp, sin Firebase, sin Volley, sin Ktor.
    //  Esto, junto con la ausencia del permiso INTERNET en el
    //  AndroidManifest.xml, garantiza que la app funcione SIN MEGAS.
    // ================================================================

    // ---------- Pruebas ----------
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.09.03"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
