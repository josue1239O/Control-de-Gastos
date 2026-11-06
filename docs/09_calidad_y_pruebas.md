# 09 — Calidad, Verificación y Pruebas

> **Norma de referencia:** ISO/IEC 25010:2011 (Modelo de calidad) e ISO/IEC/IEEE 12207:2017 §6.4 (Verificación y validación).

---

## 1. Atributos de calidad (ISO/IEC 25010)

| Característica | Requisito asociado | Criterio medible |
|---|---|---|
| Adecuación funcional | RF-01…RF-43 | 100 % de RF Must implementados y probados. |
| Eficiencia de desempeño | RNF-02…RNF-04 | Arranque ≤ 2 s; análisis ≤ 300 ms. |
| Compatibilidad | RNF-09, RNF-14 | API 26–35; modo claro/oscuro. |
| Usabilidad | RNF-01, RNF-12 | Registro ≤ 15 s; WCAG AA. |
| Fiabilidad | RNF-13 | 0 pérdidas de datos en 100 % de respaldos. |
| Seguridad | RNF-05…RNF-07 | Cifrado verificado; sin permiso INTERNET. |
| Mantenibilidad | RNF-10 | Cobertura de lógica ≥ 70 %. |
| Portabilidad | RNF-09 | Layouts correctos 4"–13". |

---

## 2. Estrategia de pruebas (pirámide)

```
        ▲   Manuales / exploratorias (pocas)
       ╱ ╲
      ╱   ╲  UI (Compose UI Test)
     ╱─────╲
    ╱       ╲ Integración (Room en memoria, flujos)
   ╱─────────╲
  ╱           ╲ Unitarias (dominio, casos de uso, cálculos)  ← mayoría
 ╱─────────────╲
```

---

## 3. Pruebas unitarias (dominio y cálculos)

| ID | Objeto de prueba | Caso |
|---|---|---|
| UT-01 | Saldo de cuenta | Ingresos, gastos y aportes combinados. |
| UT-02 | Saldo total disponible | Excluye inversiones (RN-02). |
| UT-03 | Balance del ciclo | Ciclo con/sin movimientos. |
| UT-04 | % por categoría | Reparto correcto y división por cero. |
| UT-05 | Tasa de ahorro | Positiva, negativa y sin ingresos. |
| UT-06 | Proyección de gasto | Cálculo y extrapolación. |
| UT-07 | Progreso de meta | 0 %, parcial, 100 %, excedido. |
| UT-08 | Aporte sugerido | Con/sin fecha límite; fecha pasada. |
| UT-09 | Validación aporte | Bloqueo si monto > saldo (RN-04). |
| UT-10 | Presupuesto | Umbral 80 % y superación 100 %. |
| UT-11 | Recurrentes | Generación idempotente por intervalo. |
| UT-12 | Insights | Disparo correcto de cada regla. |
| UT-13 | Ciclo configurable | Reclasificación de movimientos. |

**Herramientas:** JUnit, Kotlin Test, MockK, Turbine (Flow).

---

## 4. Pruebas de integración (datos)

| ID | Objeto | Caso |
|---|---|---|
| IT-01 | DAO de transacciones | Insertar, filtrar, agregar. |
| IT-02 | DAO de metas | Aporte actualiza monto y completa meta. |
| IT-03 | Migración Room | v1→v2 preserva datos. |
| IT-04 | SQLCipher | BD ilegible sin clave. |
| IT-05 | BackupManager | Export → Import reproduce el estado. |
| IT-06 | Backup inválido | Se rechaza (versión/checksum). |
| IT-07 | RecurringWorker | Genera movimientos sin duplicar. |

**Herramientas:** Room in-memory, Robolectric, AndroidX Test.

---

## 5. Pruebas de interfaz (Compose)

| ID | Pantalla | Caso |
|---|---|---|
| UI-01 | Nueva transacción | Validaciones y guardado. |
| UI-02 | Dashboard | Saldo y resumen reactivos. |
| UI-03 | Metas | Barra de progreso correcta. |
| UI-04 | Análisis | Gráficos renderizan offline. |
| UI-05 | LockScreen | PIN correcto/incorrecto. |
| UI-06 | Ajustes/Respaldo | Flujo SAF simulado. |
| UI-07 | Responsive | Layout en celular/tablet. |

**Herramientas:** Compose UI Test, Espresso (donde aplique).

---

## 6. Pruebas de seguridad

| ID | Verificación |
|---|---|
| SEC-01 | Ausencia del permiso INTERNET. |
| SEC-02 | BD cifrada (no legible con visor SQLite). |
| SEC-03 | Clave no en texto plano. |
| SEC-04 | PIN almacenado con hash. |
| SEC-05 | Auto-bloqueo tras inactividad. |
| SEC-06 | FLAG_SECURE en pantallas sensibles. |
| SEC-07 | Import rechaza archivos manipulados. |

---

## 7. Pruebas de rendimiento

| ID | Escenario | Objetivo |
|---|---|---|
| PERF-01 | Arranque en frío | ≤ 2 s. |
| PERF-02 | Lista 5.000 transacciones | ≥ 55 fps. |
| PERF-03 | Análisis del ciclo | ≤ 300 ms. |
| PERF-04 | Exportar respaldo (1.000 movs) | ≤ 5 s. |

---

## 8. Matriz de cobertura objetivo

| Capa | Cobertura objetivo |
|---|---|
| Dominio (casos de uso, cálculos) | ≥ 90 % |
| Datos (repositorios, DAO) | ≥ 75 % |
| UI (ViewModels) | ≥ 70 % |
| UI (Compose) | Flujos críticos |

---

## 9. Criterios de aceptación (Definition of Done)

Una funcionalidad está "terminada" cuando:
1. Cumple su RF y sus reglas de negocio.
2. Tiene pruebas unitarias verdes.
3. Tiene prueba de integración si toca datos.
4. Pasa `./gradlew lint` sin errores.
5. Funciona en **modo avión**.
6. Está documentada en `06` y trazada en `12`.

---

## 10. Gestión de defectos

| Severidad | Descripción | Respuesta |
|---|---|---|
| Crítica | Pérdida de datos, fallo de cifrado, crash. | Inmediata. |
| Alta | Cálculo erróneo, flujo bloqueado. | Prioridad siguiente. |
| Media | UI defectuosa, texto incorrecto. | Planificada. |
| Baja | Cosmético. | Backlog. |

---

## 11. Herramientas de aseguramiento

- **Android Lint:** análisis estático.
- **Detekt:** estilo y complejidad Kotlin.
- **Ktlint:** formato.
- **Gradle test tasks:** unidad e integración.
- Sin servicios de CI obligatorios (puede correr local).

---

**Fin del documento 09.**
