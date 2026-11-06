# 04 — Modelo de Datos Local

> **Tecnología:** Room (SQLite) + SQLCipher (cifrado AES-256).
> Fuente única de verdad de la aplicación. No hay base de datos remota.

---

## 1. Convenciones

| Aspecto | Convención |
|---|---|
| Nombres de tabla | `snake_case`, plural (`accounts`, `transactions`). |
| Clave primaria | `id` → `Long` autogenerado. |
| Claves foráneas | `tabla_id` con `@ForeignKey`. |
| Fechas | `Long` (epoch millis, UTC). |
| Montos | `Double` (o `Long` en centavos en implementación futura). |
| Booleanos | `Boolean` (Room convierte a 0/1). |
| Enums | Almacenados como `String` vía `TypeConverter`. |
| Borrados | `onDelete = CASCADE` o `SET_NULL`/`RESTRICT` según regla. |

> **Nota de precisión monetaria:** Room/SQLite no tiene tipo decimal. Para evitar errores de redondeo en sumas grandes se recomienda en la implementación guardar montos como **enteros en centavos** (`Long`). El modelo se documenta con `Double` por claridad, pero se deja indicada la variante `Long` (centavos).

---

## 2. Diagrama Entidad-Relación

```
┌───────────────┐          ┌────────────────┐
│   accounts    │1        N│  transactions  │N        1┌───────────────┐
│───────────────│──────────│────────────────│──────────│  categories   │
│ id (PK)       │          │ id (PK)        │          │───────────────│
│ nombre        │          │ monto          │          │ id (PK)       │
│ tipo          │          │ fecha          │          │ nombre        │
│ saldo_inicial │          │ tipo           │          │ tipo          │
│ moneda        │          │ account_id(FK) │          │ es_fijo       │
│ icono         │          │ category_id(FK)│          │ icono         │
│ color         │          │ goal_id (FK)   │          │ color         │
│ activa        │          │ nota           │          │ activa        │
│ orden         │          │ es_irregular   │          └───────────────┘
│ creada_en     │          │ es_recurrente  │                  │1
└───────┬───────┘          │ creada_en      │                  │N
        │1                 └───────┬────────┘          ┌───────────────┐
        │                         │N                   │   budgets     │
        │N                        │                    │───────────────│
┌───────┴────────────┐   ┌───────┴──────────┐         │ id (PK)       │
│ goal_contributions │   │  savings_goals   │1       N│ category_id FK│
│────────────────────│N 1│──────────────────│─────────│ monto_limite  │
│ id (PK)            │────│ id (PK)          │         │ periodo       │
│ goal_id (FK)       │    │ nombre           │         │ activo        │
│ monto              │    │ monto_objetivo   │         └───────────────┘
│ fecha              │    │ monto_actual     │
│ account_id (FK)    │    │ fecha_limite     │
│ nota               │    │ account_id (FK)  │
└────────────────────┘    │ estado           │
                          │ creada_en        │
                          └──────────────────┘

                ┌───────────────────────────┐
                │  recurring_transactions   │
                │───────────────────────────│
                │ id (PK)                    │
                │ monto · tipo               │
                │ account_id (FK)            │
                │ category_id (FK)           │
                │ nota                       │
                │ frecuencia · proxima_fecha │
                │ ultima_generada · activa   │
                └───────────────────────────┘
```

---

## 3. Definición de tablas

### 3.1 `accounts` — Fuentes de dinero

| Campo | Tipo | Restricciones | Descripción |
|---|---|---|---|
| `id` | Long | PK, autoGenerate | Identificador. |
| `nombre` | String | NOT NULL | "Efectivo", "Tigo Money", "Plazo Fijo". |
| `tipo` | AccountType | NOT NULL | EFECTIVO / BILLETERA_QR / INVERSION. |
| `saldo_inicial` | Double | NOT NULL, default 0 | Saldo al crear la cuenta. |
| `moneda` | String | NOT NULL, default "BOB" | Código de moneda. |
| `icono` | String? | | Identificador de ícono. |
| `color` | Int? | | Color ARGB. |
| `activa` | Boolean | NOT NULL, default true | Si aparece en selectores. |
| `orden` | Int | NOT NULL, default 0 | Orden de visualización. |
| `creada_en` | Long | NOT NULL | Epoch millis. |

**Índices:** `(activa)`, `(orden)`.

### 3.2 `categories` — Categorías de ingreso/gasto

| Campo | Tipo | Restricciones | Descripción |
|---|---|---|---|
| `id` | Long | PK, autoGenerate | |
| `nombre` | String | NOT NULL, UNIQUE(nombre,tipo) | "Alimentación". |
| `tipo` | CategoryType | NOT NULL | INGRESO / GASTO. |
| `es_fijo` | Boolean | NOT NULL, default false | Gasto fijo/común vs variable. |
| `icono` | String? | | |
| `color` | Int? | | |
| `activa` | Boolean | NOT NULL, default true | |

**Índices:** `(tipo)`, `(activa)`.

**Categorías semilla sugeridas:**
- *Gastos:* Alimentación, Transporte, Servicios (luz/agua/internet), Educación, Salud, Entretenimiento, Ropa, Vivienda/Alquiler, Otros.
- *Ingresos:* Bono, Mesada, Trabajo eventual, Ayuda familiar, Otros ingresos.

### 3.3 `transactions` — Movimientos

| Campo | Tipo | Restricciones | Descripción |
|---|---|---|---|
| `id` | Long | PK, autoGenerate | |
| `monto` | Double | NOT NULL, > 0 | Monto positivo. |
| `fecha` | Long | NOT NULL, INDEX | Epoch millis. |
| `tipo` | TransactionType | NOT NULL | INGRESO / GASTO / APORTE_META. |
| `account_id` | Long | FK→accounts, NOT NULL | Cuenta de origen/destino. |
| `category_id` | Long? | FK→categories, SET_NULL | Categoría. |
| `goal_id` | Long? | FK→savings_goals, CASCADE | Si es aporte a meta. |
| `nota` | String? | | Texto libre. |
| `es_irregular` | Boolean | NOT NULL, default false | Ingreso extraordinario. |
| `es_recurrente` | Boolean | NOT NULL, default false | Generado por plantilla. |
| `creada_en` | Long | NOT NULL | |

**Índices:** `(fecha)`, `(account_id)`, `(category_id)`, `(tipo)`, `(goal_id)`.

### 3.4 `savings_goals` — Metas de ahorro

| Campo | Tipo | Restricciones | Descripción |
|---|---|---|---|
| `id` | Long | PK, autoGenerate | |
| `nombre` | String | NOT NULL | "Comprar Laptop". |
| `monto_objetivo` | Double | NOT NULL, > 0 | Meta. |
| `monto_actual` | Double | NOT NULL, default 0 | Acumulado (desnormalizado). |
| `fecha_limite` | Long? | | Opcional. |
| `account_id` | Long? | FK→accounts, SET_NULL | Cuenta asociada. |
| `icono` | String? | | |
| `color` | Int? | | |
| `estado` | GoalStatus | NOT NULL | ACTIVA / COMPLETADA / PAUSADA / CANCELADA. |
| `prioridad` | GoalPriority | NOT NULL, default MEDIA | ALTA / MEDIA / BAJA. Ordena las metas y destaca lo más importante. |
| `creada_en` | Long | NOT NULL | |

**Índices:** `(estado)`.

### 3.5 `goal_contributions` — Historial de aportes

| Campo | Tipo | Restricciones | Descripción |
|---|---|---|---|
| `id` | Long | PK, autoGenerate | |
| `goal_id` | Long | FK→savings_goals, CASCADE, NOT NULL | Meta. |
| `monto` | Double | NOT NULL, > 0 | Aporte. |
| `fecha` | Long | NOT NULL, INDEX | |
| `account_id` | Long | FK→accounts, NOT NULL | Origen. |
| `nota` | String? | | |

### 3.6 `budgets` — Presupuestos por categoría

| Campo | Tipo | Restricciones | Descripción |
|---|---|---|---|
| `id` | Long | PK, autoGenerate | |
| `category_id` | Long | FK→categories, CASCADE, NOT NULL | |
| `monto_limite` | Double | NOT NULL, > 0 | Límite. |
| `periodo` | BudgetPeriod | NOT NULL | MENSUAL / SEMANAL. |
| `umbral_alerta` | Int | NOT NULL, default 80 | % de alerta. |
| `activo` | Boolean | NOT NULL, default true | |

**Índices:** `(category_id)`, UNIQUE(category_id, periodo) cuando activo.

### 3.7 `recurring_transactions` — Plantillas recurrentes

| Campo | Tipo | Restricciones | Descripción |
|---|---|---|---|
| `id` | Long | PK, autoGenerate | |
| `monto` | Double | NOT NULL, > 0 | |
| `tipo` | TransactionType | NOT NULL | INGRESO / GASTO. |
| `account_id` | Long | FK→accounts, CASCADE | |
| `category_id` | Long? | FK→categories, SET_NULL | |
| `nota` | String? | | |
| `frecuencia` | Frequency | NOT NULL | DIARIA / SEMANAL / MENSUAL / ANUAL. |
| `proxima_fecha` | Long | NOT NULL | Cuándo generar la próxima. |
| `ultima_generada` | Long? | | |
| `activa` | Boolean | NOT NULL, default true | |

**Índices:** `(proxima_fecha)`, `(activa)`.

---

## 4. Enumeraciones

```
enum AccountType     { EFECTIVO, BILLETERA_QR, INVERSION }
enum CategoryType    { INGRESO, GASTO }
enum TransactionType { INGRESO, GASTO, APORTE_META }
enum GoalStatus      { ACTIVA, COMPLETADA, PAUSADA, CANCELADA }
enum BudgetPeriod    { MENSUAL, SEMANAL }
enum Frequency       { DIARIA, SEMANAL, MENSUAL, ANUAL }
```

Los enums se persisten como `String` mediante `Converters.kt`.

---

## 5. Información derivada (no almacenada, calculada)

| Dato | Cálculo | Ver |
|---|---|---|
| Saldo de cuenta | saldo_inicial + Σ ingresos − Σ gastos − Σ aportes | `05_logica_de_negocio.md` §2 |
| Saldo Total Disponible | Σ saldos (Efectivo + Billetera QR) | §2 |
| Balance del ciclo | Σ ingresos − Σ gastos | §3 |
| % por categoría | gasto_cat / gasto_total | §4 |
| Tasa de ahorro | (ingresos − gastos) / ingresos | §5 |
| Progreso de meta | monto_actual / monto_objetivo | §7 |

> **Decisión de diseño:** `savings_goals.monto_actual` se guarda **desnormalizado** para lectura rápida; se mantiene consistente actualizando dentro de la misma transacción que inserta la contribución.

---

## 6. Estrategia de migración

- Se usa `RoomDatabase.Builder.addMigrations(...)` con `Migration(1,2)`, etc.
- `exportSchema = true` → esquemas JSON versionados en `schemas/`.
- Prohibido `fallbackToDestructiveMigration()` en producción (perdería datos).
- Cada migración incluye prueba en `androidTest`.

---

## 7. Consideraciones de seguridad de datos

- La BD se abre con la clave obtenida del **Android Keystore**.
- No se almacena la clave en `SharedPreferences` ni en texto plano.
- El archivo de BD en disco permanece cifrado (ilegible sin la clave).
- Los respaldos JSON se cifran antes de escribirse (ver doc 07).

---

## 8. Volumen y rendimiento estimados

| Tabla | Registros típicos/año | Índice crítico |
|---|---|---|
| transactions | 1.000 – 5.000 | `fecha`, `account_id` |
| goal_contributions | 50 – 300 | `fecha` |
| accounts | 3 – 10 | — |
| categories | 15 – 40 | — |
| budgets | 5 – 20 | `category_id` |

Con los índices indicados, las consultas de agregación del ciclo responden en **< 300 ms** (RNF-04).

---

**Fin del documento 04.**
