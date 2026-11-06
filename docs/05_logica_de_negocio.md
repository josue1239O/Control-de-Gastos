# 05 — Lógica de Negocio y Cálculos

> **Norma de referencia:** ISO/IEC/IEEE 29148 — especificación de comportamiento y reglas.
> Todas las fórmulas se ejecutan **localmente** en el dispositivo.

---

## 1. Conceptos temporales

### 1.1 Ciclo financiero
El **ciclo** es el período contable del usuario. Por defecto es el mes calendario (día 1 al último día), pero es **configurable** (ej. del 10 al 9) porque en Bolivia los estudiantes reciben el bono en fechas irregulares.

```
Inicio del ciclo = día configurado (por defecto 1) del mes M a las 00:00.
Fin del ciclo    = inicio del ciclo del mes M+1 menos 1 ms.
```

**RN-07:** Un movimiento pertenece al ciclo cuyo rango contiene su `fecha`.

### 1.2 Ingreso irregular/extraordinario
Un ingreso marcado `es_irregular = true` (ej. bono que llegó tarde, un trabajo puntual) **se excluye** de promedios y proyecciones salvo que el usuario indique lo contrario (RN-08).

---

## 2. Cálculo de saldos (Módulo M1)

### 2.1 Saldo de una cuenta

```
saldo(cuenta) = saldo_inicial
              + Σ monto(t.type = INGRESO   ∧ t.account_id = cuenta)
              − Σ monto(t.type = GASTO     ∧ t.account_id = cuenta)
              − Σ monto(t.type = APORTE_META ∧ t.account_id = cuenta)
```

> Nota: un aporte a meta es una **salida** de la cuenta de origen (el dinero se "reserva" en la meta, pero deja de estar disponible para gasto).

### 2.2 Saldo Total Disponible

```
saldo_disponible = Σ saldo(cuenta)  para cuenta.tipo ∈ { EFECTIVO, BILLETERA_QR }
                    ∧ cuenta.activa = true
```

**RN-02:** Las cuentas de tipo **Inversión** NO se suman aquí.

### 2.3 Patrimonio invertido

```
patrimonio_invertido = Σ saldo(cuenta)  para cuenta.tipo = INVERSION ∧ activa
```

### 2.4 Patrimonio total

```
patrimonio_total = saldo_disponible + patrimonio_invertido
```

---

## 3. Balance del ciclo (Módulo M3)

```
ingresos_ciclo = Σ monto(t.type = INGRESO ∧ fecha ∈ [inicio, fin])
gastos_ciclo   = Σ monto(t.type = GASTO   ∧ fecha ∈ [inicio, fin])
balance        = ingresos_ciclo − gastos_ciclo
```

- `balance > 0` → superávit (ahorró).
- `balance = 0` → equilibrio.
- `balance < 0` → déficit (gastó más de lo que ingresó).

**Interpretación:** un balance negativo también puede deberse a gastar ahorros acumulados; el sistema lo muestra con claridad.

---

## 4. Porcentaje de gasto por categoría

```
gasto_total = Σ monto(t.type = GASTO ∧ fecha ∈ ciclo)
pct(categoría) = ( Σ monto(t.type = GASTO ∧ t.category_id = categoría ∧ fecha ∈ ciclo)
                   / gasto_total ) × 100
```

- Si `gasto_total = 0` → todos los porcentajes son 0 (o "sin datos").
- Se ordena de mayor a menor para el gráfico de torta.
- Las categorías sin gasto no se incluyen.

---

## 5. Tasa de ahorro

```
tasa_ahorro = ( (ingresos_ciclo − gastos_ciclo) / ingresos_ciclo ) × 100
```

- Si `ingresos_ciclo = 0` → **no definida**; la UI muestra "—".
- Puede ser negativa (gastó más de lo que ingresó).
- Versión alternativa (por si se excluyen irregulares):
  ```
  ingresos_regulares = Σ monto(t.type = INGRESO ∧ ¬t.es_irregular ∧ ciclo)
  tasa_ahorro_reg = ((ingresos_regulares − gastos_ciclo) / ingresos_regulares) × 100
  ```

---

## 6. Proyección y promedios

### 6.1 Promedio de ingreso diario
```
días_transcurridos = días desde inicio_ciclo hasta hoy (inclusive)
ingreso_diario_prom = ingresos_regulares_ciclo / días_transcurridos
```

### 6.2 Proyección de gasto a fin de ciclo
```
días_totales_ciclo = (fin − inicio) en días
ritmo_gasto_diario = gastos_ciclo / días_transcurridos
gasto_proyectado   = ritmo_gasto_diario × días_totales_ciclo
balance_proyectado = ingresos_ciclo − gasto_proyectado
```

Mensaje al usuario: *"A este ritmo, terminarás el ciclo con un balance de X Bs."*

---

## 7. Metas de ahorro (Módulo M4)

### 7.1 Progreso
```
pct_progreso = (monto_actual / monto_objetivo) × 100     (acotado a [0,100] en UI)
restante     = max(monto_objetivo − monto_actual, 0)
```

**RN-05:** cuando `monto_actual ≥ monto_objetivo` → `estado = COMPLETADA` (automático).

### 7.1.1 Prioridad
Cada meta tiene una prioridad **ALTA / MEDIA / BAJA** (por defecto MEDIA).
- El orden de la lista es: estado, prioridad (ALTA → MEDIA → BAJA) y fecha de creación.
- La prioridad **no cambia** el cálculo del progreso; solo el orden y el destacado visual.
- El usuario puede cambiarla al crear o editar la meta.

### 7.2 Aporte sugerido
Si la meta tiene fecha límite:
```
meses_restantes = meses entre hoy y fecha_limite (redondeado hacia arriba)
aporte_sugerido = restante / max(meses_restantes, 1)
```
Si no tiene fecha límite → se muestra el `restante` sin dividir.

### 7.3 Registro de aporte (operación atómica)
```
1. Validar: monto > 0  y  monto ≤ saldo(cuenta_origen)   [RN-04]
2. INSERT goal_contributions(goal_id, monto, fecha, account_id, nota)
3. UPDATE savings_goals SET monto_actual = monto_actual + monto
4. INSERT transactions(tipo = APORTE_META, account_id, goal_id, monto)
5. Si monto_actual ≥ monto_objetivo → UPDATE estado = COMPLETADA
   (todo dentro de una sola @Transaction)
```

### 7.4 Ritmo de avance
```
aportado_por_mes = monto_actual / meses_desde_creación
¿va_en_ritmo?    = aportado_por_mes ≥ aporte_sugerido
```

---

## 8. Presupuestos (Módulo M3)

```
gasto_periodo(cat) = Σ monto(t.type = GASTO ∧ t.category_id = cat ∧ fecha ∈ período)
pct_uso = gasto_periodo(cat) / monto_limite × 100
```

**RN-09:** alerta cuando `pct_uso ≥ umbral_alerta` (por defecto 80 %). Se emite **una sola vez por umbral por ciclo** (se guarda bandera del ciclo en DataStore o en la BD).

Estados visuales:
- Verde: < 80 %
- Ámbar: 80 % – 99 %
- Rojo: ≥ 100 % (superado)

---

## 9. Insights automáticos (Módulo M3)

Reglas de generación **local** (RF-22). Ejemplos:

| Condición | Mensaje generado |
|---|---|
| `pct(cat) > pct(cat)_ciclo_anterior + 15` | "Gastaste más en {cat} este ciclo." |
| `balance_proyectado < 0` | "A este ritmo terminarás en déficit." |
| `tasa_ahorro ≥ 20` | "¡Buen ahorro! Guardaste {tasa}% de tus ingresos." |
| `tasa_ahorro < 0` | "Gastaste más de lo que ingresaste." |
| `pct_uso(presupuesto) ≥ 100` | "Superaste el presupuesto de {cat}." |
| sin movimiento en 7 días | "No registraste gastos en 7 días. ¿Todo bien?" |

Los insights se calculan al abrir la pantalla de Análisis y/o en un Worker nocturno; nunca salen del dispositivo.

---

## 10. Transacciones recurrentes (Módulo M2)

```
En cada ejecución del Worker:
  para cada plantilla activa donde proxima_fecha ≤ ahora:
     INSERT transactions(desde plantilla, es_recurrente = true)
     proxima_fecha += intervalo(frecuencia)
     ultima_generada = ahora
```

Intervalos: DIARIA (+1d), SEMANAL (+7d), MENSUAL (+1 mes según calendario), ANUAL (+1 año).
**Idempotencia:** no se genera dos veces la misma fecha (se compara con `ultima_generada`).

---

## 11. Orden de operaciones y consistencia

| Operación | Tablas afectadas | Atomicidad |
|---|---|---|
| Alta de ingreso/gasto | transactions | simple |
| Aporte a meta | goal_contributions + savings_goals + transactions | `@Transaction` |
| Completar meta | savings_goals | simple |
| Generar recurrente | transactions + recurring_transactions | `@Transaction` |
| Importar respaldo | todas | `@Transaction` masiva o BD temporal + reemplazo |

---

## 12. Casos borde contemplados

| Caso | Comportamiento |
|---|---|
| Ciclo sin ingresos | Tasa de ahorro = "—". |
| División por cero (gasto_total = 0) | % = 0, sin error. |
| Meta con objetivo 0 | No permitido al crear (validación). |
| Aporte mayor al saldo | Bloqueado (RN-04). |
| Fecha límite en el pasado | Aporte sugerido = restante completo; aviso. |
| Monto negativo o cero | Bloqueado en validación de formularios. |
| Cambio de ciclo a mitad de mes | Los movimientos se reclasifican por rango; se avisa. |
| Importar respaldo incompatible (versión mayor) | Se rechaza con mensaje claro. |
| Reloj del dispositivo cambiado | Los cálculos usan la fecha del dispositivo; se advierte que puede alterar reportes. |

---

## 13. Fórmulas resumidas (hoja rápida)

```
saldo_cuenta       = inicial + ingresos − gastos − aportes
saldo_disponible   = Σ(Efectivo, BilleteraQR activas)
patrimonio_total   = saldo_disponible + inversiones
balance_ciclo      = Σingresos − Σgastos
pct_categoria      = gasto_cat / gasto_total × 100
tasa_ahorro        = (ingresos − gastos) / ingresos × 100
gasto_proyectado   = (gastos / días_transcurridos) × días_totales
progreso_meta      = monto_actual / monto_objetivo × 100
aporte_sugerido    = restante / meses_restantes
pct_presupuesto    = gasto_periodo / límite × 100
```

---

**Fin del documento 05.**
