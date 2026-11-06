# 10 — Plan de Proyecto y Ciclo de Vida

> **Norma de referencia:** ISO/IEC/IEEE 12207:2017 — Procesos del ciclo de vida del software.

---

## 1. Procesos del ciclo de vida aplicados (12207)

| Proceso (12207) | Aplicación en Platica |
|---|---|
| **Acuerdo** | Definición del alcance con el interesado (estudiante). |
| **Ciclo de vida del proyecto** | Planificación por fases (roadmap abajo). |
| **Ingeniería de requisitos** | `02_requisitos.md` (SRS 29148). |
| **Diseño (arquitectura)** | `03`, `04`, `05`. |
| **Construcción** | Implementación Kotlin/Compose/Room. |
| **Integración** | Ensamblado de módulos + pruebas de integración. |
| **Verificación** | `09_calidad_y_pruebas.md`. |
| **Transición** | Generación de APK firmado, guía al usuario (`06`). |
| **Operación** | Uso por el usuario; respaldos. |
| **Mantenimiento** | Correcciones, mejoras, migraciones (v2+). |
| **Gestión de configuración** | Control de versiones y del documento (abajo). |
| **Gestión de riesgos** | Sección 5. |
| **Aseguramiento de calidad** | `09`. |

---

## 2. Modelo de ciclo de vida

Se adopta un modelo **incremental-iterativo** con entregas por fases (MVP primero), lo que permite valorar resultados tempranos y reducir riesgo.

```
Fase 1 ─▶ Fase 2 ─▶ Fase 3 ─▶ Fase 4 ─▶ Fase 5
(MVP)      (Análisis) (Metas)   (Seguridad) (Extras)
```

---

## 3. Roadmap de implementación

### Fase 0 — Fundación (Setup)
- Crear proyecto Android (Kotlin DSL, Compose, Hilt).
- Configurar Room + SQLCipher + DataStore.
- Configurar manifest **sin permiso INTERNET**.
- Pipeline de lint/detekt.

### Fase 1 — MVP núcleo (M1 + M2 básico)
- Entidades, DAOs y repositorios de accounts, categories, transactions.
- CRUD de cuentas y categorías semilla.
- Registro rápido de ingresos/gastos.
- Dashboard con Saldo Total Disponible y últimas transacciones.
- **Entregable:** APK funcional offline de registro.

### Fase 2 — Analítica (M3)
- Cálculos de balance, % por categoría, tasa de ahorro, proyección.
- Pantalla de Análisis con gráficos (Vico).
- Presupuestos y alertas.
- **Entregable:** APK con análisis completo.

### Fase 3 — Metas (M4)
- Entidades y DAO de metas y aportes.
- Pantallas de metas, detalle y aporte (operación atómica).
- Progreso y aporte sugerido.
- **Entregable:** APK con gestión de metas.

### Fase 4 — Seguridad y respaldo (M5)
- SQLCipher + SecureKeyManager + PIN/biometría.
- Export/import JSON cifrado + copia `.db`.
- Recordatorio de respaldo (WorkManager).
- **Entregable:** APK seguro y con respaldo.

### Fase 5 — Extras v1
- Widget de saldo (Glance).
- Recordatorios de gastos fijos.
- Insights automáticos.
- Recurrentes con generación automática.
- Estilos y accesibilidad final.
- **Entregable:** v1.0 completa.

### Fase 6 — Post-v1 (backlog)
- Exportar CSV.
- Multi-moneda con tipo de cambio manual.
- Migración a KMP (iOS).
- Categorías con subcategorías.
- Gráficos comparativos entre ciclos.

---

## 4. Estimación de esfuerzo (relativa)

| Fase | Complejidad | Porcentaje de esfuerzo |
|---|---|---|
| 0 — Fundación | Baja | 8 % |
| 1 — MVP núcleo | Alta | 25 % |
| 2 — Analítica | Media-alta | 18 % |
| 3 — Metas | Media | 12 % |
| 4 — Seguridad/respaldo | Alta | 17 % |
| 5 — Extras | Media | 15 % |
| 6 — Pruebas/pulido | Media | 5 % |

---

## 5. Gestión de riesgos

| ID | Riesgo | Probabilidad | Impacto | Mitigación |
|---|---|---|---|---|
| R-01 | Pérdida de datos por fallo | Media | Alto | BD cifrada + respaldos + transacciones atómicas. |
| R-02 | Olvido del PIN | Media | Alto | Aviso claro; fomentar respaldos. |
| R-03 | Dispositivos de gama baja lentos | Media | Medio | Room con índices; UI ligera; minSdk 26. |
| R-04 | Complejidad de SQLCipher/Keystore | Media | Medio | Pruebas tempranas; documentar recuperación. |
| R-05 | Migración de esquema fallida | Baja | Alto | Migraciones versionadas + pruebas. |
| R-06 | UI no responsive | Baja | Medio | WindowSizeClass desde el inicio. |
| R-07 | Inclusión accidental de dependencia de red | Baja | Alto | Regla de lint + checklist CI. |

---

## 6. Control de configuración

- **Repositorio:** Git (local; sin servicios que requieran red para operar).
- **Ramas sugeridas:** `main` (estable), `develop`, `feature/*`.
- **Versionado:** SemVer (`MAJOR.MINOR.PATCH`).
- **Documentos:** control de cambios al inicio de cada doc.
- **Artefactos versionados:** esquemas Room (`schemas/`).

---

## 7. Entregables

| ID | Entregable | Fase |
|---|---|---|
| E-01 | Documentación (este set) | 0 |
| E-02 | APK MVP (registro offline) | 1 |
| E-03 | APK con analítica | 2 |
| E-04 | APK con metas | 3 |
| E-05 | APK seguro con respaldo | 4 |
| E-06 | APK v1.0 (extras + widget) | 5 |
| E-07 | Manual de usuario | 5 |
| E-08 | Suite de pruebas | Transversal |

---

## 8. Roles (proyecto pequeño)

| Rol | Responsabilidad |
|---|---|
| Product Owner (estudiante) | Define necesidades y prioridades. |
| Arquitecto/Dev | Diseño, implementación, pruebas. |
| QA (rol compartido) | Verificación y validación. |

---

## 9. Métricas de seguimiento

- Requisitos Must completados / total.
- Cobertura de pruebas por capa.
- Defectos abiertos por severidad.
- Tiempo de registro de transacción (usabilidad).
- Tamaño del APK.

---

**Fin del documento 10.**
