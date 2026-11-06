# 03 — Arquitectura del Sistema

> **Normas de referencia:** ISO/IEC/IEEE 12207:2017 §6.4 (Procesos técnicos) e ISO/IEC/IEEE 42010 (Descripción de arquitectura).

---

## 1. Visión arquitectónica

**Platica** adopta una **arquitectura en capas (Clean Architecture) aplicada de forma minimalista**, promoviendo separación de responsabilidades, testabilidad y desacoplamiento, sin sobre-ingeniería.

### 1.1 Principios rectores

1. **Offline-first por diseño:** la fuente de verdad es la base de datos local. No existe capa de red.
2. **Unidirectional Data Flow (UDF):** el estado fluye de datos → dominio → UI; los eventos, de UI → dominio → datos.
3. **UI reactiva:** Compose observa `StateFlow`/`Flow` emitidos por los ViewModels.
4. **Minimalismo:** solo las dependencias necesarias.
5. **Seguridad por defecto:** cifrado en reposo y bloqueo de acceso.

---

## 2. Vista de capas

```
┌─────────────────────────────────────────────────────────────┐
│                     CAPA DE PRESENTACIÓN (UI)                │
│   Jetpack Compose · ViewModels · Navigation · Theme          │
│   - Pantallas, componentes, estado UI, eventos de usuario    │
└───────────────────────────────┬─────────────────────────────┘
                                │ invoca casos de uso / observa
                                ▼
┌─────────────────────────────────────────────────────────────┐
│                      CAPA DE DOMINIO                         │
│   Modelos de negocio · Interfaces de repositorio · Casos     │
│   de uso (cálculos, reglas)                                  │
│   - Independiente de Android y de la BD                      │
└───────────────────────────────┬─────────────────────────────┘
                                │ implementa / consulta
                                ▼
┌─────────────────────────────────────────────────────────────┐
│                       CAPA DE DATOS                          │
│   Room (SQLite + SQLCipher) · DAOs · Entidades · DataStore   │
│   Backup (JSON) · Repositorios concretos                     │
│   - Fuente única de verdad local, cifrada                    │
└─────────────────────────────────────────────────────────────┘
```

**Regla de dependencia:** las capas externas dependen de las internas, nunca al revés. El dominio no conoce a Android ni a Room.

---

## 3. Estructura de paquetes

```
com.bolivia.Platica/
├─ PlaticaApp.kt              (Application + Hilt)
├─ MainActivity.kt
│
├─ data/
│  ├─ local/
│  │  ├─ entity/
│  │  │  ├─ AccountEntity.kt
│  │  │  ├─ CategoryEntity.kt
│  │  │  ├─ TransactionEntity.kt
│  │  │  ├─ SavingsGoalEntity.kt
│  │  │  ├─ GoalContributionEntity.kt
│  │  │  ├─ BudgetEntity.kt
│  │  │  └─ RecurringTransactionEntity.kt
│  │  ├─ dao/
│  │  │  ├─ AccountDao.kt
│  │  │  ├─ CategoryDao.kt
│  │  │  ├─ TransactionDao.kt
│  │  │  ├─ GoalDao.kt
│  │  │  ├─ BudgetDao.kt
│  │  │  └─ RecurringDao.kt
│  │  ├─ AppDatabase.kt
│  │  ├─ Converters.kt
│  │  └─ security/
│  │     ├─ SecureKeyManager.kt
│  │     └─ DatabaseFactory.kt
│  ├─ repository/
│  │  ├─ AccountRepositoryImpl.kt
│  │  ├─ TransactionRepositoryImpl.kt
│  │  ├─ GoalRepositoryImpl.kt
│  │  └─ ...
│  ├─ backup/
│  │  ├─ BackupManager.kt
│  │  └─ BackupModels.kt
│  └─ prefs/
│     └─ SettingsDataStore.kt
│
├─ domain/
│  ├─ model/
│  │  ├─ Account.kt  ·  Category.kt  ·  Transaction.kt
│  │  ├─ SavingsGoal.kt  ·  Contribution.kt  ·  Budget.kt
│  │  └─ enums/ (AccountType, TransactionType, GoalStatus, ...)
│  ├─ repository/           (interfaces)
│  └─ usecase/
│     ├─ balance/  (GetAccountBalance, GetTotalAvailable, ...)
│     ├─ analytics/ (GetMonthlyBalance, GetSpendingByCategory,
│     │              GetSavingsRate, GetProjection, GenerateInsights)
│     └─ goals/    (GetGoalProgress, AddContribution, GetSuggestedContribution)
│
├─ ui/
│  ├─ theme/       (Color, Type, Shape, Theme)
│  ├─ navigation/  (NavGraph, Routes)
│  ├─ components/  (MoneyText, ProgressBar, etc.)
│  ├─ screens/
│  │  ├─ onboarding/ · lock/ · dashboard/ · accounts/ · transactions/
│  │  ├─ goals/ · analytics/ · settings/
│  │  └─ (cada pantalla: Screen.kt + ViewModel.kt + UiState.kt)
│  └─ widget/      (GlanceAppWidget)
│
├─ notification/   (Notifier, Channels)
├─ worker/         (RecurringWorker, BudgetAlertWorker, BackupReminderWorker)
└─ di/             (DatabaseModule, RepositoryModule, UseCaseModule)
```

---

## 4. Patrón de presentación (MVVM + UDF)

```
   Evento de usuario
        │
        ▼
   ┌─────────┐   llama    ┌──────────────┐  consulta   ┌──────────────┐
   │  Compose │──────────▶│  ViewModel   │────────────▶│  Caso de uso │
   │  Screen  │            │ (UiState +   │             │   / Repo     │
   │          │◀───────────│ StateFlow)   │◀────────────│              │
   └─────────┘  observa    └──────────────┘   Flow      └──────┬───────┘
                                                                │
                                                                ▼
                                                       ┌────────────────┐
                                                       │ Room (Flow)    │
                                                       │ Local cifrada  │
                                                       └────────────────┘
```

- Cada pantalla expone una clase `XxxUiState` inmutable.
- Los ViewModels reciben dependencias vía **Hilt**.
- La UI **nunca** accede a Room directamente.

---

## 5. Flujo de persistencia y manejo de estado

### 5.1 Escritura (comando)

1. La UI emite un evento (ej. "guardar transacción").
2. El ViewModel valida y llama al caso de uso.
3. El caso de uso aplica reglas de negocio y llama al repositorio.
4. El repositorio persiste en Room (transacción atómica si toca varias tablas).
5. Room emite nuevas emisiones por `Flow`.

### 5.2 Lectura (consulta reactiva)

1. La UI observa un `StateFlow<UiState>`.
2. El ViewModel combina flujos de casos de uso.
3. Los casos de uso consultan DAOs que devuelven `Flow`.
4. Cualquier cambio en la BD **propaga** automáticamente la actualización a la UI.

### 5.3 Transaccionalidad

Operaciones que tocan varias tablas (ej. aporte a meta = insertar contribución + actualizar meta + afectar saldo) se ejecutan con `@Transaction` de Room para garantizar atomicidad.

---

## 6. Componentes clave

| Componente | Responsabilidad |
|---|---|
| `AppDatabase` | Punto de acceso a Room con SQLCipher configurado. |
| `SecureKeyManager` | Genera/recupera la clave desde Android Keystore. |
| `DatabaseFactory` | Construye Room usando `SupportFactory` (SQLCipher). |
| `BackupManager` | Serializa/deserializa JSON cifrado; exporta/importa. |
| `SettingsDataStore` | Preferencias (tema, ciclo, PIN, umbrales). |
| `WorkManager` | Tareas programadas locales (recurrentes, alertas, respaldos). |
| `GlanceAppWidget` | Widget de saldo. |

---

## 7. Gestión de dependencias (Hilt)

```
AppModule
 ├─ provideDatabase(context)      → AppDatabase (SQLCipher)
 ├─ provideAccountDao(db)         → AccountDao
 ├─ provideTransactionDao(db)     → TransactionDao
 ├─ ... (un provider por DAO)
 ├─ provideAccountRepository(...) → AccountRepositoryImpl
 ├─ ... (un provider por repositorio)
 └─ provideSettingsDataStore(...) → SettingsDataStore
```

Alcances: `@Singleton` para BD/repos/preferencias; `@ViewModelScoped` para ViewModels.

---

## 8. Diagrama de despliegue (dispositivo único)

```
┌─────────────────────────────────────────────┐
│            DISPOSITIVO ANDROID               │
│                                              │
│  ┌────────────┐        ┌──────────────────┐  │
│  │  App APK   │        │  Android Keystore│  │
│  │ (BolsiSeg) │◀──────▶│   (clave AES)    │  │
│  └─────┬──────┘        └──────────────────┘  │
│        │                                     │
│        ▼                                     │
│  ┌──────────────────┐    ┌────────────────┐  │
│  │  BD SQLite       │    │  DataStore     │  │
│  │  cifrada (AES)   │    │  (prefs)       │  │
│  └──────────────────┘    └────────────────┘  │
│        │                                     │
│        ▼ (acción manual del usuario)         │
│  ┌──────────────────┐                        │
│  │  Respaldo .json  │  → almacenamiento del  │
│  │  cifrado (SAF)   │    propio usuario      │
│  └──────────────────┘                        │
└─────────────────────────────────────────────┘
```

---

## 9. Atributos de calidad y su soporte arquitectónico

| Atributo (ISO 25010) | Cómo lo logra la arquitectura |
|---|---|
| **Seguridad** | SQLCipher + Keystore + biometría; sin red. |
| **Rendimiento** | Queries agregadas en Room; índices; Flow reactivo. |
| **Mantenibilidad** | Capas separadas; interfaces; tests por capa. |
| **Portabilidad** | Compose responsive; KMP-ready. |
| **Confiabilidad** | Transacciones atómicas; respaldos verificables. |
| **Usabilidad** | Material 3; flujo corto; feedback inmediato. |

---

## 10. Decisiones de arquitectura (ADR resumidas)

| ADR | Decisión | Justificación |
|---|---|---|
| ADR-01 | Room en lugar de Realm | Mejor soporte, integración con Compose/Flow, SQLCipher. |
| ADR-02 | SQLCipher para cifrado | Estándar maduro, AES-256, transparente a Room. |
| ADR-03 | Sin capa de red | Requisito CR-01 / privacidad. |
| ADR-04 | Hilt | Estándar Android, reduce boilerplate. |
| ADR-05 | WorkManager | Tareas locales confiables con restricciones. |
| ADR-06 | Vico para gráficos | Offline, declarativo, sin dependencias de red. |

---

**Fin del documento 03.**
