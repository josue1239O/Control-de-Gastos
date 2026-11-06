# 02 — Especificación de Requisitos de Software (SRS)

> **Normas de referencia:** IEEE 830-1998 / ISO/IEC/IEEE 29148:2018 — Sección 9.
> Este documento sigue la plantilla de la SRS: Introducción, Descripción general, Requisitos específicos, y Trazabilidad.

---

## 1. Introducción

### 1.1 Propósito
Definir de forma completa y verificable los **requisitos funcionales y no funcionales** de la aplicación **Platica**, sirviendo de contrato entre las partes interesadas y como base para el diseño, la implementación y las pruebas.

### 1.2 Alcance
Aplicación Android de finanzas personales **100% offline** para estudiantes bolivianos. Ver `01_vision_y_alcance.md`.

### 1.3 Definiciones y acrónimos
Ver `11_glosario.md`.

### 1.4 Referencias
- `01_vision_y_alcance.md`
- `03_arquitectura.md`
- `04_modelo_de_datos.md`
- `05_logica_de_negocio.md`

### 1.5 Visión general del documento
Secciones: introducción → descripción general → interfaz → características → requisitos funcionales (RF) → requisitos no funcionales (RNF) → restricciones → trazabilidad.

---

## 2. Descripción general

### 2.1 Perspectiva del producto
Producto **autónomo y autocontenido**. No forma parte de un sistema mayor; no interactúa con otros sistemas por red. Su entorno es exclusivamente el dispositivo Android.

### 2.2 Funciones del producto (resumen)

| Grupo | Funciones |
|---|---|
| Gestión de cuentas | Crear/editar/desactivar cuentas; clasificar por tipo; calcular saldos. |
| Registro de movimientos | Alta/edición/baja de ingresos y gastos; categorización; notas. |
| Recurrencia | Plantillas de ingresos/gastos periódicos con generación automática. |
| Presupuestos | Definir límites por categoría; alertas al superar umbral. |
| Metas | Crear metas; aportar saldo; ver progreso; historial. |
| Análisis | Balance, % por categoría, tasa de ahorro, proyecciones, insights. |
| Seguridad | Cifrado de BD; bloqueo por PIN/biometría; auto-bloqueo. |
| Respaldo | Exportar/importar JSON cifrado y copia de BD. |
| Extras | Widget de saldo; recordatorios; gráficos. |

### 2.3 Clases de usuarios

| Clase | Descripción | Frecuencia de uso |
|---|---|---|
| Estudiante (usuario principal) | Registra ingresos irregulares (bono/mesada), controla gastos diarios. | Diaria |
| Usuario ocasional | Revisa saldos y metas, hace respaldo. | Semanal/Mensual |

### 2.4 Entorno operativo
- Android 8.0 (API 26) a Android 15 (API 35).
- Dispositivos con/sin internet (irrelevante para la app).
- Pantallas de 4" a 13"; orientación vertical y horizontal.
- Modo oscuro/claro.

### 2.5 Restricciones de diseño
- **CR-01:** Prohibido declarar el permiso `INTERNET`.
- **CR-02:** Arquitectura en capas (UI / Dominio / Datos).
- **CR-03:** Base de datos cifrada AES-256.
- **CR-04:** UI responsive con Jetpack Compose.
- **CR-05:** Moneda por defecto BOB.

### 2.6 Supuestos y dependencias
- El dispositivo tiene almacenamiento libre.
- El usuario recuerda su PIN (aviso de irrecuperabilidad).
- WorkManager disponible (API 26+).

---

## 3. Interfaces externas

### 3.1 Interfaces de usuario
- Material Design 3 (accesibilidad WCAG 2.1 AA).
- Navegación inferior (celular) / rail lateral (tablet).
- Formularios con validación en línea.
- Feedback: snackbars, diálogos, animaciones.

### 3.2 Interfaces de hardware
- **Biométrico** (Huella/Face) vía `BiometricPrompt`.
- **Almacenamiento** vía SAF para respaldos.
- **Notificaciones** locales.
- **Widget** de pantalla de inicio (Glance).

### 3.3 Interfaces de software
- Room (SQLite), SQLCipher, DataStore, WorkManager, Vico (gráficos), Hilt.
- **Sin SDK de red.**

### 3.4 Interfaces de comunicación
- **Ninguna.** No hay protocolos de red. La única "transferencia" es el archivo de respaldo que el usuario gestiona manualmente.

---

## 4. Requisitos funcionales (RF)

> Formato: **ID — Título** | Descripción | Prioridad (Must/Should/Could) | Criterio de verificación.

### 4.1 Módulo M1 — Gestión de patrimonio

| ID | Requisito | Prioridad | Verificación |
|---|---|---|---|
| **RF-01** | El sistema permitirá **crear una cuenta** con: nombre, tipo (Efectivo / Billetera QR / Inversión), saldo inicial, moneda, color e ícono. | Must | Crear y ver en lista. |
| **RF-02** | El sistema permitirá **editar y desactivar** cuentas, preservando el historial asociado. | Must | Editar/desactivar. |
| **RF-03** | El sistema **calculará el saldo** de cada cuenta como: saldo_inicial + ingresos − gastos − aportes a metas. | Must | Comparar con cálculo manual. |
| **RF-04** | El sistema mostrará el **Saldo Total Disponible** = Σ saldos de cuentas activas de tipo Efectivo + Billetera QR. | Must | Comprobación visual del dashboard. |
| **RF-05** | El sistema mostrará por separado el **Patrimonio Invertido** (cuentas tipo Inversión). | Should | Sección de patrimonio. |
| **RF-06** | El sistema permitirá **reordenar** las cuentas en la vista de patrimonio. | Could | Drag & drop. |

### 4.2 Módulo M2 — Registro de ingresos y gastos

| ID | Requisito | Prioridad | Verificación |
|---|---|---|---|
| **RF-07** | El sistema permitirá **registrar una transacción** con: tipo (Ingreso/Gasto), monto, fecha, cuenta, categoría y nota opcional. | Must | Alta de transacción. |
| **RF-08** | El formulario de alta será de **acceso rápido** (FAB) y registrará en ≤ 4 toques para el caso común. | Must | Cronometraje. |
| **RF-09** | El sistema permitirá **categorizar** cada gasto/ingreso con categorías predefinidas y **editables** (Alimentación, Transporte, Servicios, Entretenimiento, Educación, Salud, etc.). | Must | CRUD de categorías. |
| **RF-10** | El sistema permitirá clasificar un gasto como **Fijo/Común** o **Variable**. | Must | Marcar y filtrar. |
| **RF-11** | El sistema permitirá registrar **ingresos en cualquier día** (fecha libre) y marcarlos como **periódicos** o **eventuales/irregulares**. | Must | Alta con fecha libre. |
| **RF-12** | El sistema permitirá **editar y eliminar** transacciones. | Must | Editar/borrar. |
| **RF-13** | El sistema permitirá **filtrar y buscar** transacciones por rango de fechas, categoría, tipo, cuenta y texto en la nota. | Should | Aplicar filtros. |
| **RF-14** | El sistema permitirá definir **transacciones recurrentes** (frecuencia diaria/semanal/mensual/anual) que se generen automáticamente. | Must | Crear recurrente y ver generación. |
| **RF-15** | El sistema mostrará la **lista de movimientos** del ciclo/período seleccionado, ordenada por fecha descendente. | Must | Vista de movimientos. |

### 4.3 Módulo M3 — Análisis y estadísticas

| ID | Requisito | Prioridad | Verificación |
|---|---|---|---|
| **RF-16** | El sistema calculará el **Balance del ciclo** = Σ ingresos − Σ gastos del período seleccionado. | Must | Comparar con cálculo manual. |
| **RF-17** | El sistema calculará el **Porcentaje de gasto por categoría** = gasto_categoría / gasto_total × 100. | Must | Gráfico de torta correcto. |
| **RF-18** | El sistema calculará la **Tasa de Ahorro** = (ingresos − gastos) / ingresos × 100 (si ingresos = 0 muestra "—"). | Must | Cálculo correcto. |
| **RF-19** | El sistema mostrará **gráficos** (torta de categorías, barras/líneas de evolución) generados localmente. | Must | Renderizado offline. |
| **RF-20** | El sistema calculará la **proyección de fin de ciclo** a partir del ritmo de gasto actual. | Should | Comparar con fórmula. |
| **RF-21** | El sistema permitirá **excluir ingresos irregulares/extraordinarios** de los promedios. | Should | Toggle y comparación. |
| **RF-22** | El sistema generará **insights** textuales automáticos (ej. "gastaste 20 % más en Alimentación que el ciclo pasado"). | Could | Aparición de insight. |
| **RF-23** | El sistema permitirá **definir presupuestos** por categoría (límite mensual/semanal) y alertará al alcanzar un umbral configurable (por defecto 80 %). | Must | Crear presupuesto y ver alerta. |
| **RF-24** | El sistema permitirá **configurar el ciclo financiero** (día de inicio, por defecto 1). | Should | Cambio de ciclo. |

### 4.4 Módulo M4 — Metas de ahorro

| ID | Requisito | Prioridad | Verificación |
|---|---|---|---|
| **RF-25** | El sistema permitirá **crear una meta** con: nombre, monto objetivo, fecha límite deseada (opcional), cuenta asociada (opcional). | Must | Alta de meta. |
| **RF-26** | El sistema mostrará el **progreso visual** (porcentaje completado) y el **monto faltante**. | Must | Barra de progreso correcta. |
| **RF-27** | El sistema permitirá **aportar saldo** a una meta desde una cuenta, registrando el aporte en el historial. | Must | Aportar y ver historial. |
| **RF-28** | El sistema calculará el **aporte sugerido** = monto_faltante / períodos restantes hasta la fecha límite. | Should | Comparar con fórmula. |
| **RF-29** | El sistema permitirá **editar, pausar, reanudar y cancelar** metas. | Should | Cambio de estado. |
| **RF-30** | Al alcanzar el 100 %, el sistema marcará la meta como **Completada** y notificará. | Must | Notificación de completado. |

### 4.5 Módulo M5 — Arquitectura offline-first y respaldo

| ID | Requisito | Prioridad | Verificación |
|---|---|---|---|
| **RF-31** | El sistema **no declarará el permiso INTERNET** en el `AndroidManifest.xml`. | Must | Inspección del manifest. |
| **RF-32** | El sistema almacenará todos los datos en una **base de datos local cifrada**. | Must | Verificación de cifrado. |
| **RF-33** | El sistema permitirá **exportar un respaldo** en JSON cifrado mediante SAF. | Must | Exportar y verificar archivo. |
| **RF-34** | El sistema permitirá **importar un respaldo**, restaurando los datos (con confirmación de reemplazo). | Must | Importar y verificar datos. |
| **RF-35** | El sistema permitirá **exportar una copia de la base de datos** (`.db` cifrada). | Should | Copia creada. |
| **RF-36** | El sistema podrá **recordar al usuario hacer respaldos** periódicamente. | Should | Notificación programada. |

### 4.6 Seguridad y acceso

| ID | Requisito | Prioridad | Verificación |
|---|---|---|---|
| **RF-37** | El sistema permitirá **proteger el acceso** con PIN numérico y/o biometría. | Must | Desbloqueo correcto. |
| **RF-38** | El sistema aplicará **auto-bloqueo** tras un tiempo de inactividad configurable. | Should | Bloqueo automático. |
| **RF-39** | El sistema gestionará la clave de cifrado en el **Android Keystore**, sin almacenarla en texto plano. | Must | Inspección de almacenamiento. |

### 4.7 Extras / v1 completos

| ID | Requisito | Prioridad | Verificación |
|---|---|---|---|
| **RF-40** | El sistema ofrecerá un **widget** en la pantalla de inicio con el Saldo Total Disponible. | Should | Widget visible y actualizado. |
| **RF-41** | El sistema enviará **recordatorios locales** de gastos fijos. | Should | Notificación a la hora configurada. |
| **RF-42** | El sistema permitirá **personalizar temas** (claro/oscuro/dinámico). | Could | Cambio de tema. |
| **RF-43** | El sistema permitirá **exportar a CSV** para uso externo. | Could | Archivo CSV válido. |

---

## 5. Requisitos no funcionales (RNF)

| ID | Categoría | Requisito | Métrica |
|---|---|---|---|
| **RNF-01** | Usabilidad | Registro de transacción rápida. | ≤ 15 s / ≤ 4 toques. |
| **RNF-02** | Rendimiento | Arranque en frío. | ≤ 2 s en dispositivo medio. |
| **RNF-03** | Rendimiento | Renderizado de lista con 5000 transacciones. | Scroll fluido ≥ 55 fps. |
| **RNF-04** | Rendimiento | Cálculo de análisis del mes. | ≤ 300 ms. |
| **RNF-05** | Seguridad | Cifrado de BD. | AES-256 (SQLCipher). |
| **RNF-06** | Seguridad | Clave protegida. | Android Keystore (sin texto plano). |
| **RNF-07** | Privacidad | Cero conexiones de red. | Permiso INTERNET ausente. |
| **RNF-08** | Disponibilidad | Funciona sin internet. | 100 % de funciones en modo avión. |
| **RNF-09** | Portabilidad | Rango de dispositivos. | API 26–35; 4"–13". |
| **RNF-10** | Mantenibilidad | Arquitectura en capas + tests. | Cobertura de lógica ≥ 70 %. |
| **RNF-11** | Localización | Idioma y moneda. | Español (BO) / BOB. |
| **RNF-12** | Accesibilidad | Contraste y tamaños. | WCAG 2.1 AA. |
| **RNF-13** | Confiabilidad | Sin pérdida de datos. | Integridad en 100 % de respaldos. |
| **RNF-14** | Compatibilidad | Modo claro/oscuro. | Correcto en ambos. |
| **RNF-15** | Eficiencia energética | Sin consumo en segundo plano no necesario. | Sin wake-locks abusivos. |

---

## 6. Reglas de negocio (RN)

| ID | Regla |
|---|---|
| **RN-01** | El monto de toda transacción es **positivo**; el tipo (Ingreso/Gasto) determina el signo en los cálculos. |
| **RN-02** | El Saldo Total Disponible **excluye** las cuentas de tipo Inversión. |
| **RN-03** | Un aporte a meta **descuenta** de la cuenta de origen y **suma** al monto_actual de la meta. |
| **RN-04** | No se puede aportar a una meta más dinero que el saldo disponible de la cuenta de origen. |
| **RN-05** | Una meta alcanzada al 100 % cambia a estado **Completada** automáticamente. |
| **RN-06** | Las cuentas desactivadas no aparecen en selectores pero conservan su historial. |
| **RN-07** | El ciclo financiero inicia el día configurado (por defecto 1) y termina el día anterior del mes siguiente. |
| **RN-08** | Un ingreso marcado como **irregular/extraordinario** se excluye de promedios y proyecciones salvo indicación. |
| **RN-09** | Las alertas de presupuesto se emiten una sola vez por umbral por ciclo. |
| **RN-10** | Borrar una categoría con transacciones asociadas mueve estas a "Sin categoría" (no permite borrado en cascada silencioso). |

---

## 7. Casos de uso (CU) — muestra

### CU-01: Registrar un ingreso

| Campo | Detalle |
|---|---|
| Actor | Estudiante |
| Precondición | App desbloqueada; existe al menos una cuenta. |
| Flujo principal | 1. Toca FAB "+". 2. Elige "Ingreso". 3. Ingresa monto, fecha, cuenta, categoría, nota (opcional). 4. Confirma. 5. El sistema guarda y recalcula saldos. 6. Muestra confirmación. |
| Flujos alternos | 3a. Fecha distinta a hoy. 3b. Marcar como irregular. |
| Postcondición | El saldo de la cuenta y el balance se actualizan. |
| Requisitos cubiertos | RF-07, RF-11, RF-15 |

### CU-02: Aportar a una meta

| Campo | Detalle |
|---|---|
| Actor | Estudiante |
| Precondición | Existe una meta activa y saldo suficiente en la cuenta. |
| Flujo principal | 1. Abre "Metas". 2. Selecciona meta. 3. Toca "Aportar". 4. Ingresa monto y cuenta. 5. Confirma. |
| Flujos alternos | 4a. Monto > saldo disponible → error RN-04. |
| Postcondición | monto_actual actualiza; progreso y saldo recalculan. |
| Requisitos cubiertos | RF-27, RF-28, RF-30 |

### CU-03: Exportar respaldo

| Campo | Detalle |
|---|---|
| Actor | Estudiante |
| Precondición | App desbloqueada; hay datos. |
| Flujo principal | 1. Ajustes → Respaldo. 2. "Exportar". 3. Elige JSON. 4. SAF pide ubicación. 5. Genera archivo cifrado. 6. Confirmación. |
| Flujos alternos | 5a. Cancelar SAF → no se genera. |
| Postcondición | Archivo `.json` cifrado guardado donde el usuario eligió. |
| Requisitos cubiertos | RF-33, RF-35 |

> El resto de los casos de uso (CRUD de cuentas/categorías, importar, presupuestos, recurrentes, análisis) se derivan directamente de los RF y se prueban en `09_calidad_y_pruebas.md`.

---

## 8. Matriz de priorización (MoSCoW)

| Prioridad | Cantidad | Enfoque |
|---|---|---|
| **Must** | 30 | Núcleo del MVP; sin ellos no se entrega. |
| **Should** | 13 | Importantes; entran en v1 si el cronograma lo permite (sí entran). |
| **Could** | 5 | Deseables; entran como mejoras. |

---

## 9. Trazabilidad

La matriz de trazabilidad requisito → diseño → prueba está en `12_trazabilidad.md`.

---

**Fin del documento 02.**
