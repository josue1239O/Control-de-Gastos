# 01 — Visión y Alcance del Proyecto

> **Norma de referencia:** ISO/IEC/IEEE 29148:2018 — Sección 5 (Visión general) y Sección 9 (Contenido de la SRS).

---

## 1. Identificación del documento

| Campo | Valor |
|---|---|
| Nombre del proyecto | **Platica** |
| Tipo de documento | Visión y Alcance (Vision & Scope) |
| Versión | 1.0 |
| Fecha | 2026-11-04 |
| Autor | Arquitecto de Software |
| Estado | Aprobado (línea base) |

---

## 2. En qué consiste el proyecto

### 2.1 Definición

**Platica** es una **aplicación móvil nativa para Android** de **control de finanzas personales y gestión de metas de ahorro**, cuyo atributo distintivo —y **requisito crítico e indispensable**— es funcionar **100% sin conexión a internet (offline-first)**.

Todos los datos del usuario (cuentas, transacciones, categorías, metas, presupuestos) se almacenan **exclusivamente en el dispositivo**, en una base de datos local cifrada. La aplicación **no se conecta a la red en ningún momento**, ni siquiera si el dispositivo dispone de conexión.

### 2.2 Problema que resuelve

Los estudiantes bolivianos —y en general las personas con **ingresos bajos, variables e irregulares**— enfrentan dificultades para:

- **Saber cuánto dinero tienen realmente disponible** al día, distribuido entre efectivo, billeteras digitales (QR) y ahorros.
- **Llevar registro de sus gastos** sin herramientas complejas ni cuentas en línea.
- **Entender en qué se les va el dinero** (alimentación, transporte, servicios, ocio).
- **Ahorrar con un objetivo concreto** (una laptop, un viaje, un fondo de emergencia).
- **Proteger su privacidad**: no quieren que sus datos financieros viajen a servidores de terceros.

Las aplicaciones existentes suelen requerir **registro en línea, conexión permanente a internet y sincronización en la nube**, lo que las hace inaccesibles o poco fiables para este público (datos móviles costosos o intermitentes en Bolivia).

### 2.3 Solución propuesta

Una aplicación **simple, minimalista, rápida y 100% privada** que:

1. Se instala y **funciona sin nunca conectarse a internet**.
2. Guarda todo **localmente y cifrado** en el teléfono.
3. Permite registrar un ingreso o gasto **en segundos**.
4. Ofrece **análisis claro** de la situación financiera.
5. Ayuda a **cumplir metas de ahorro** con progreso visual.

### 2.4 Características esenciales (resumen)

| # | Módulo | Descripción breve |
|---|---|---|
| M1 | Gestión de patrimonio | Registro y visualización de saldos por Efectivo, Billetera QR e Inversiones, con Saldo Total Disponible. |
| M2 | Registro de ingresos/gastos | Formulario rápido, categorización, clasificación fijo/variable, ingresos periódicos o eventuales. |
| M3 | Análisis y estadísticas | Balance mensual, % por categoría, tasa de ahorro, proyecciones e insights, todo local. |
| M4 | Metas de ahorro | Definición de metas, progreso visual y asignación de saldo a cada meta. |
| M5 | Arquitectura offline-first | Base de datos local cifrada, respaldo/importación JSON, cero red. |

---

## 3. Alcance del proyecto

### 3.1 Dentro del alcance (In-Scope)

- Aplicación **nativa Android** (Kotlin + Jetpack Compose).
- Funcionamiento **completamente offline** (sin permiso de Internet).
- Almacenamiento local cifrado (**Room + SQLCipher**).
- Autenticación local por **PIN y/o biometría**.
- Los cinco módulos (M1–M5) descritos.
- **Widget** de saldo en la pantalla de inicio.
- **Recordatorios** locales de gastos fijos (WorkManager).
- **Gráficos** (torta, barras, líneas) generados localmente.
- **Presupuestos** por categoría con alertas.
- **Insights** automáticos generados localmente.
- **Exportación/importación** de respaldos (JSON cifrado y copia de BD).
- Idioma: **Español (Bolivia)**; moneda: **BOB (Bs.)**.

### 3.2 Fuera del alcance (Out-of-Scope) — v1

- ❌ Sincronización en la nube o cualquier servicio de red.
- ❌ Registro de usuarios, login remoto o multiusuario/social.
- ❌ Transferencias entre usuarios (la app es **monousuario y autónoma**).
- ❌ Conexión automática a bancos o billeteras (sin APIs financieras).
- ❌ Versión iOS (se contempla arquitectura migrable a KMP **a futuro**).
- ❌ Publicidad, compras dentro de la app o monetización en v1.
- ❌ Cifrado de respaldos con clave derivada de contraseña maestra externa (se evalúa para v2).

### 3.3 Supuestos (Assumptions)

- El dispositivo corre **Android 8.0 (API 26)** o superior.
- El usuario tiene nociones básicas de uso de aplicaciones móviles.
- El usuario conoce los montos de sus ingresos y gastos.
- El dispositivo cuenta con almacenamiento suficiente (la app ocupa < 50 MB).

### 3.4 Restricciones (Constraints)

- **CR-01:** No se declara el permiso `android.permission.INTERNET`. Obligatorio.
- **CR-02:** Toda la lógica analítica se ejecuta en el dispositivo.
- **CR-03:** La UI debe ser **responsive** (celular, tablet, plegables).
- **CR-04:** El respaldo se realiza por el usuario mediante el Storage Access Framework (SAF).
- **CR-05:** Los datos financieros deben almacenarse cifrados (AES-256).

---

## 4. Partes interesadas (Stakeholders)

| Stakeholder | Rol | Interés principal |
|---|---|---|
| **Estudiante usuario** | Usuario final | Control fácil de su dinero, privacidad total, offline. |
| **Desarrollador** | Autor | Código limpio, mantenible, sin dependencias de red. |
| **Arquitecto** | Diseño técnico | Cumplimiento offline-first y de seguridad. |
| **Docente/Tutor** | Evaluador (ámbito académico) | Cumplimiento de normas de ingeniería de software. |
| **Familias (indirecto)** | Beneficiario | Que el estudiante administre mejor su bono. |

---

## 5. Objetivos del proyecto (SMART)

| ID | Objetivo | Métrica de éxito |
|---|---|---|
| OBJ-01 | Registrar un ingreso/gasto en menos de **15 segundos**. | Medición de tiempo en prueba de usabilidad. |
| OBJ-02 | Funcionar **sin conexión el 100 % del tiempo**. | 0 llamadas de red (auditoría del manifest + test). |
| OBJ-03 | Proteger datos con cifrado **AES-256**. | Verificación de SQLCipher sobre la BD. |
| OBJ-04 | Explicar la situación financiera del mes en **una pantalla**. | Dashboard con balance, ahorro y categorías. |
| OBJ-05 | Permitir exportar un respaldo completo en **≤ 5 segundos**. | Prueba de tiempo con 1000 transacciones. |
| OBJ-06 | Adaptarse a pantallas de **4" a 13"** sin corte de UI. | Pruebas en múltiples densidades. |

---

## 6. Contexto del sistema (Contexto del negocio)

```
┌──────────────────────────────────────────────────────────────┐
│                    DISPOSITIVO ANDROID                        │
│                                                                │
│   ┌──────────────────────────────────────────────────────┐    │
│   │              Platica (App)                    │    │
│   │   ┌──────────┐   ┌──────────┐   ┌──────────────┐    │    │
│   │   │    UI    │──▶│ Dominio  │──▶│ Datos (Room) │    │    │
│   │   │ Compose  │   │  Casos   │   │  + SQLCipher │    │    │
│   │   └──────────┘   │ de uso   │   └──────┬───────┘    │    │
│   │        ▲         └──────────┘          │            │    │
│   │        │                               ▼            │    │
│   │   ┌────┴─────┐                   ┌───────────┐     │    │
│   │   │ Keystore │                   │ BD Local  │     │    │
│   │   │ Biometría│                   │ cifrada   │     │    │
│   │   └──────────┘                   └───────────┘     │    │
│   └──────────────────────────────────────────────────────┘    │
│                          ▲                                    │
│                          │ (opcional, manual)                 │
│                   ┌──────┴────────┐                          │
│                   │ Archivo de    │  ← SAF (el usuario       │
│                   │ respaldo .json│    elige dónde guardarlo)│
│                   └───────────────┘                          │
└──────────────────────────────────────────────────────────────┘
              ║                                        ║
              ║  SIN CONEXIÓN DE RED                   ║
              ║  (permiso INTERNET NO declarado)       ║
              ▼                                        ▼
        ┌───────────┐                            ┌───────────┐
        │ INTERNET  │        ✗ BLOQUEADO         │  NUBE /   │
        │ (datos)   │                            │ SERVIDORES│
        └───────────┘                            └───────────┘
```

**Frontera del sistema:** La aplicación no tiene interfaces de red. Sus únicas "salidas" son el almacenamiento local y la exportación manual de archivos que controla el usuario.

---

## 7. Beneficios esperados

| Beneficio | Descripción |
|---|---|
| **Privacidad total** | Los datos nunca salen del dispositivo. |
| **Disponibilidad** | Funciona en cualquier lugar, con o sin datos móviles. |
| **Ahorro de datos** | Cero consumo de megas. |
| **Rapidez** | Sin latencia de red. |
| **Autonomía** | No depende de servidores que puedan caer. |
| **Educación financiera** | Ayuda al estudiante a entender y mejorar sus hábitos. |

---

## 8. Criterios de éxito del proyecto

El proyecto se considera exitoso si:

1. La aplicación **no contiene el permiso INTERNET** y funciona con el dispositivo en modo avión. ✅
2. Los cinco módulos cumplen todos sus requisitos funcionales. ✅
3. Los requisitos no funcionales (seguridad, rendimiento, usabilidad) se verifican. ✅
4. El usuario puede **respaldar y restaurar** toda su información. ✅
5. La documentación cumple la trazabilidad (ver `12_trazabilidad.md`). ✅

---

## 9. Referencias normativas

- ISO/IEC/IEEE 29148:2018 — Ingeniería de sistemas y software — Procesos del ciclo de vida — Ingeniería de requisitos.
- IEEE 830-1998 — IEEE Recommended Practice for Software Requirements Specifications.
- ISO/IEC/IEEE 26514:2022 — Diseño y desarrollo de información para usuarios.
- ISO/IEC/IEEE 12207:2017 — Procesos del ciclo de vida del software.
- ISO/IEC 25010:2011 — Modelos de calidad de sistemas y software.

---

**Fin del documento 01.**
