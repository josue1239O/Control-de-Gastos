# 12 — Matriz de Trazabilidad

> **Norma de referencia:** ISO/IEC/IEEE 29148:2018 §9.5 — Trazabilidad de requisitos.

Esta matriz enlaza **Requisito → Regla de Negocio → Diseño (entidad/pantalla) → Prueba**.

| Requisito | Reglas | Entidad / Tabla | Pantalla | Prueba |
|---|---|---|---|---|
| RF-01 Crear cuenta | RN-06 | accounts | Patrimonio | IT-01, UI-02 |
| RF-02 Editar/desactivar cuenta | RN-06 | accounts | Patrimonio | IT-01 |
| RF-03 Saldo de cuenta | RN-01 | accounts, transactions | Dashboard/Patrimonio | UT-01 |
| RF-04 Saldo Total Disponible | RN-02 | accounts | Dashboard | UT-02 |
| RF-05 Patrimonio invertido | RN-02 | accounts | Patrimonio | UT-02 |
| RF-06 Reordenar cuentas | — | accounts.orden | Patrimonio | UI-02 |
| RF-07 Registrar transacción | RN-01 | transactions | Nueva transacción | UI-01 |
| RF-08 Alta rápida | — | — | Nueva transacción | UI-01 |
| RF-09 Categorizar | RN-10 | categories | Nueva transacción/Ajustes | IT-01 |
| RF-10 Fijo/Variable | — | categories.es_fijo | Nueva transacción | UI-01 |
| RF-11 Ingresos fecha libre/irregular | RN-08 | transactions.es_irregular | Nueva transacción | UT-13 |
| RF-12 Editar/eliminar transacción | — | transactions | Movimientos | IT-01 |
| RF-13 Filtrar/buscar | — | transactions | Movimientos | IT-01 |
| RF-14 Recurrentes | — | recurring_transactions | Ajustes/Recurrentes | UT-11, IT-07 |
| RF-15 Lista de movimientos | — | transactions | Movimientos | UI-02 |
| RF-16 Balance del ciclo | RN-07 | transactions | Análisis | UT-03 |
| RF-17 % por categoría | — | transactions, categories | Análisis | UT-04 |
| RF-18 Tasa de ahorro | RN-08 | transactions | Análisis | UT-05 |
| RF-19 Gráficos | — | — | Análisis | UI-04 |
| RF-20 Proyección fin de ciclo | — | transactions | Análisis | UT-06 |
| RF-21 Excluir irregulares | RN-08 | transactions.es_irregular | Análisis | UT-05 |
| RF-22 Insights | — | transactions | Análisis | UT-12 |
| RF-23 Presupuestos + alerta | RN-09 | budgets | Análisis/Ajustes | UT-10 |
| RF-24 Ciclo configurable | RN-07 | DataStore | Ajustes | UT-13 |
| RF-25 Crear meta | — | savings_goals | Metas | UI-03 |
| RF-26 Progreso visual | RN-05 | savings_goals | Detalle meta | UT-07 |
| RF-27 Aportar a meta | RN-03, RN-04 | goal_contributions | Detalle meta | UT-09, IT-02 |
| RF-28 Aporte sugerido | — | savings_goals | Detalle meta | UT-08 |
| RF-29 Editar/pausar/cancelar meta | — | savings_goals.estado | Detalle meta | UI-03 |
| RF-30 Meta completada | RN-05 | savings_goals | Detalle meta | UT-07 |
| RF-31 Sin permiso INTERNET | CR-01 | manifest | — | SEC-01 |
| RF-32 BD cifrada | CR-03 | AppDatabase | — | SEC-02, IT-04 |
| RF-33 Exportar JSON | — | BackupManager | Ajustes/Respaldo | IT-05 |
| RF-34 Importar JSON | — | BackupManager | Ajustes/Respaldo | IT-05, IT-06 |
| RF-35 Copia .db | — | BackupManager | Ajustes/Respaldo | IT-05 |
| RF-36 Recordatorio respaldo | — | BackupReminderWorker | — | IT-07 |
| RF-37 PIN/biometría | — | DataStore, Keystore | LockScreen | UI-05, SEC-04 |
| RF-38 Auto-bloqueo | — | DataStore | LockScreen | SEC-05 |
| RF-39 Clave en Keystore | — | SecureKeyManager | — | SEC-03 |
| RF-40 Widget de saldo | — | GlanceAppWidget | Widget | UI-02 |
| RF-41 Recordatorios gastos fijos | — | WorkManager | Notificación | IT-07 |
| RF-42 Temas | — | DataStore | Ajustes | UI-07 |
| RF-43 Exportar CSV | — | BackupManager | Ajustes/Respaldo | IT-05 |

---

## 1. Cobertura por módulo

| Módulo | Requisitos | Pruebas asociadas |
|---|---|---|
| M1 Patrimonio | RF-01…RF-06 | UT-01, UT-02, IT-01, UI-02 |
| M2 Registro | RF-07…RF-15 | UI-01, IT-01, UT-11, IT-07 |
| M3 Análisis | RF-16…RF-24 | UT-03…UT-06, UT-10, UT-12, UI-04 |
| M4 Metas | RF-25…RF-30 | UT-07…UT-09, IT-02, UI-03 |
| M5 Offline/Respaldo | RF-31…RF-36 | SEC-01…SEC-03, IT-04…IT-07 |
| Seguridad acceso | RF-37…RF-39 | SEC-04, SEC-05, UI-05 |
| Extras | RF-40…RF-43 | UI-02, IT-05, IT-07, UI-07 |

---

## 2. Requisitos no funcionales → verificación

| RNF | Verificación |
|---|---|
| RNF-01 | Prueba de usabilidad cronometrada. |
| RNF-02…04 | PERF-01…PERF-03. |
| RNF-05, RNF-06 | SEC-02, SEC-03. |
| RNF-07, RNF-08 | SEC-01 (ausencia de permiso). |
| RNF-09 | UI-07 (responsive) + matriz de dispositivos. |
| RNF-10 | Cobertura de pruebas (sección 8 doc 09). |
| RNF-11 | Revisión de textos y formato BOB. |
| RNF-12 | Auditoría de accesibilidad. |
| RNF-13 | IT-05 (export/import íntegro). |
| RNF-14 | UI-07. |
| RNF-15 | Perfilado de batería. |

---

## 3. Estado de trazabilidad

| Indicador | Valor |
|---|---|
| Requisitos funcionales | 43 |
| Requisitos no funcionales | 15 |
| Reglas de negocio | 10 |
| Requisitos con prueba asociada | 43/43 (100 %) |
| Requisitos con diseño asociado | 43/43 (100 %) |

> Esta matriz se actualiza en cada iteración. Un requisito sin prueba o sin diseño es considerado **incompleto**.

---

**Fin del documento 12.**
