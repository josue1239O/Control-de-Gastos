# 07 — Seguridad y Respaldo (Offline)

> **Norma de referencia:** ISO/IEC/IEEE 12207:2017 §6.4.7 (Procesos de aseguramiento) y buenas prácticas OWASP MASVS.

---

## 1. Modelo de amenazas (resumen)

| Amenaza | Mitigación |
|---|---|
| Robo/pérdida del dispositivo | BD cifrada AES-256; PIN/biometría. |
| Acceso físico a los archivos | Datos cifrados; clave en Keystore. |
| Root / inspección de la BD | Cifrado + llave ligada al hardware (StrongBox cuando esté disponible). |
| Interceptación de red | **No hay red** (sin permiso INTERNET). |
| Fuga de datos a terceros | Cero telemetría, cero SDK de red. |
| Pérdida de datos por fallo | Respaldos locales verificables. |
| Copia de respaldo expuesta | Respaldo JSON cifrado. |

---

## 2. Requisito cero de red (la garantía más fuerte)

⚠️ **La aplicación NO declara `android.permission.INTERNET` en el `AndroidManifest.xml`.**

Consecuencia técnica: el sistema operativo Android **deniega a nivel de kernel/gestión de permisos** cualquier intento de socket de red por parte del proceso. No es una promesa de código: es una restricción del sistema.

### Verificación

```xml
<!-- app/src/main/AndroidManifest.xml -->
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <!-- ⚠️ NO existe <uses-permission android:name="android.permission.INTERNET"/> -->
    <!-- Esta ausencia es INTENCIONAL y es un requisito (CR-01, RF-31). -->

    <application ...>
        ...
    </application>
</manifest>
```

**Prueba automatizada sugerida:**
```bash
# Falla si aparece el permiso INTERNET en el manifest fusionado
./gradlew :app:processDebugMainManifest
grep -i "android.permission.INTERNET" app/build/intermediates/merged_manifests/*/AndroidManifest.xml \
  && echo "ERROR: permiso INTERNET presente" && exit 1 || echo "OK: sin permiso INTERNET"
```

Además, en v1 no se incluye **ninguna** dependencia de red (Retrofit, OkHttp, Firebase, Volley, Ktor, etc.).

---

## 3. Cifrado de la base de datos (SQLCipher)

### 3.1 Flujo de clave

```
1. Primer arranque:
   - SecureKeyManager genera una passphrase aleatoria (32 bytes).
   - Se guarda CIFRADA en Android Keystore (clave AES del sistema).
2. Arranques siguientes:
   - SecureKeyManager recupera la passphrase desde Keystore.
3. DatabaseFactory:
   - Construye Room con SupportFactory(SQLiteDatabase.getBytes(passphrase)).
   - SQLCipher cifra el archivo .db con AES-256.
```

### 3.2 Propiedades
- Cifrado **AES-256** en reposo.
- La clave **nunca** se escribe en texto plano.
- Si el Keystore se invalida (cambio de credenciales del dispositivo), se avisa al usuario y se ofrece restaurar desde respaldo.

---

## 4. Autenticación local (PIN y biometría)

| Mecanismo | Descripción |
|---|---|
| **PIN numérico** | 4–6 dígitos, almacenado como **hash** (PBKDF2/BCrypt), nunca en texto plano. |
| **Biometría** | `BiometricPrompt` (huella/rostro); no almacena datos biométricos, solo delega al sistema. |
| **Auto-bloqueo** | Tras X minutos de inactividad (configurable) o al pasar a segundo plano. |
| **Bloqueo progresivo** | Tras varios intentos fallidos de PIN, se aplican esperas crecientes. |

⚠️ **Aviso al usuario:** si olvida el PIN y no tiene respaldo, los datos **no** se pueden recuperar (diseño intencional por seguridad).

---

## 5. Protección de pantalla y copia

- `FLAG_SECURE` en pantallas sensibles → impide capturas de pantalla y oculta en el selector de apps recientes (configurable).
- Sin exportación de datos salvo por respaldo explícito.
- Sin logs con datos sensibles en `Logcat` (se ofusca/omite).

---

## 6. Estrategia de respaldo offline

### 6.1 Formatos soportados

| Formato | Uso | Cifrado |
|---|---|---|
| **JSON cifrado** (`.json`) | Portabilidad, versión entre versiones, inspeccionable si el usuario lo descifra. | Sí (AES). |
| **Copia de BD** (`.db`) | Respaldos completos de la base cifrada. | Sí (SQLCipher). |
| **CSV** (Could) | Análisis externo del usuario. | No (opcional). |

### 6.2 Exportación (SAF)

```
Ajustes → Respaldo → Exportar JSON
   → ACTION_CREATE_DOCUMENT (SAF) → el usuario elige carpeta/nombre
   → BackupManager serializa todas las tablas a un JSON
   → se cifra el JSON con la clave del Keystore
   → se escribe el archivo con un encabezado de versión y metadatos
   → snackbar: "Respaldo guardado"
```

**Estructura del archivo de respaldo:**
```json
{
  "meta": {
    "app": "Platica",
    "schemaVersion": 1,
    "appVersion": "1.0.0",
    "exportedAt": 1762300000000,
    "moneda": "BOB",
    "cifrado": "AES-256-GCM"
  },
  "data": {
    "accounts": [ ... ],
    "categories": [ ... ],
    "transactions": [ ... ],
    "savings_goals": [ ... ],
    "goal_contributions": [ ... ],
    "budgets": [ ... ],
    "recurring_transactions": [ ... ]
  },
  "checksum": "sha256:..."
}
```
> El bloque `data` (y opcionalmente todo el documento) va cifrado; el `checksum` permite detectar corrupción.

### 6.3 Importación (SAF)

```
Ajustes → Respaldo → Importar
   → ACTION_OPEN_DOCUMENT (SAF) → el usuario elige el archivo
   → BackupManager valida: versión de esquema, checksum, estructura
   → [si hay datos] diálogo: "Esto reemplazará tus datos actuales. ¿Continuar?"
   → se realiza en una transacción; en caso de error, se revierte
   → reinicio de la UI con los datos restaurados
```

**Validaciones de importación:**
- El archivo es un JSON de Platica válido.
- `schemaVersion` ≤ versión que la app comprende (si es mayor → rechazar con aviso).
- `checksum` correcto (integridad).
- Estructura de tablas completa.

### 6.4 Recordatorio de respaldo

- `BackupReminderWorker` (WorkManager) notifica cada N días (configurable, por defecto 15).
- Todo local; sin red.

### 6.5 Buenas prácticas para el usuario
- Guardar el respaldo **fuera del teléfono** (SD, PC) además del propio teléfono.
- Hacer un respaldo antes de actualizar la app o cambiar de dispositivo.
- No compartir el archivo de respaldo (contiene datos cifrados, pero es privado).

---

## 7. Privacidad

- **Cero recolección de datos.** No hay analítica, ni publicidad, ni crash-reporting remoto.
- **Cero cuentas.** No hay registro, ni email, ni teléfono.
- **Cero telemetría.** La app no envía nada a ningún lado.
- Aviso de privacidad en Ajustes → Acerca de: *"Platica no accede a internet ni comparte tu información."*

---

## 8. Cumplimiento y verificación (checklist)

| # | Verificación | Método |
|---|---|---|
| 1 | Sin permiso INTERNET | Inspección del manifest fusionado. |
| 2 | Sin dependencias de red | Revisión de `build.gradle`. |
| 3 | BD cifrada | Intentar abrir el `.db` con un visor SQLite → ilegible. |
| 4 | Clave no en texto plano | Revisión de SharedPreferences/archivos. |
| 5 | PIN con hash | Inspección del almacenamiento del PIN. |
| 6 | Auto-bloqueo funciona | Prueba de inactividad. |
| 7 | FLAG_SECURE activo | Intentar captura de pantalla. |
| 8 | Export/import íntegros | Ciclo completo con datos de prueba. |
| 9 | Import rechaza versión futura | Prueba con archivo manipulado. |
| 10 | Checksum detecta corrupción | Alterar byte del respaldo. |

---

**Fin del documento 07.**
