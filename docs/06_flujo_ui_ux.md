# 06 — Flujo UI/UX y Documentación de Usuario

> **Norma de referencia:** ISO/IEC/IEEE 26514:2022 — Diseño y desarrollo de información para usuarios.

---

## 1. Principios de diseño de interfaz

1. **Minimalismo:** cada pantalla muestra lo esencial; acciones secundarias en menús.
2. **Rapidez:** registrar un movimiento en ≤ 4 toques (RF-08).
3. **Claridad financiera:** el "Saldo Total Disponible" siempre visible en el inicio.
4. **Consistencia:** Material Design 3, componentes reutilizables.
5. **Responsive:** inferior navigation en celular; navigation rail en tablet/plegable.
6. **Accesibilidad:** contraste AA, tamaños táctiles ≥ 48 dp, etiquetas semánticas.
7. **Feedback:** toda acción produce respuesta visual (snackbar, animación).
8. **Seguridad visible:** la app se ve "privada"; avisos claros sobre respaldo.

---

## 2. Mapa de navegación general

```
                           ┌──────────────┐
                           │    Splash    │
                           └──────┬───────┘
                                  │
                     ¿Primera vez?│
                   ┌──────────────┴───────────────┐
                   ▼                              ▼
        ┌────────────────────┐          ¿PIN/Biometría activo?
        │    Onboarding      │                   │
        │ (bienvenida +      │             ┌─────┴─────┐
        │  crear 1.ª cuenta) │            sí           no
        └─────────┬──────────┘             │            │
                  │                        ▼            │
                  │             ┌──────────────────┐    │
                  │             │   LockScreen     │    │
                  │             │ (PIN/Biométrico) │    │
                  │             └────────┬─────────┘    │
                  │                      │ ok           │
                  └──────────┬───────────┴──────────────┘
                             ▼
                 ┌────────────────────────┐
                 │       DASHBOARD        │  ← pantalla principal
                 └───────────┬────────────┘
                             │
     ┌───────────┬───────────┼───────────┬──────────────┐
     ▼           ▼           ▼           ▼              ▼
┌──────────┐┌──────────┐┌──────────┐┌──────────┐  ┌──────────┐
│Inicio    ││Movimien. ││  Metas   ││ Análisis │  │ Ajustes  │
│(Dashboard)││(lista)  ││(lista)   ││(stats)   │  │(config)  │
└──────────┘└────┬─────┘└────┬─────┘└──────────┘  └────┬─────┘
                 │            │                          │
                 ▼            ▼                          ▼
          Nueva/Editar   Detalle Metas            Cuentas · Categorías
          Transacción    └─ Aportar               Presupuestos · Recurrentes
                                                   Respaldo · Seguridad · Tema
```

---

## 3. Descripción de pantallas

### 3.1 Splash
- **Objetivo:** inicialización, verificación de primer uso y de seguridad.
- **Elementos:** logo, indicador de carga.
- **Transición:** a Onboarding (primer uso) o LockScreen/Dashboard.

### 3.2 Onboarding
- **Objetivo:** explicar el valor (offline, privado) y crear la primera cuenta.
- **Elementos:** 2–3 slides breves; formulario de cuenta inicial (nombre, tipo, saldo inicial).
- **Acciones:** "Comenzar", "Omitir configuración".

### 3.3 LockScreen 🔒
- **Objetivo:** proteger el acceso.
- **Elementos:** pad de PIN y/o botón de huella.
- **Acciones:** desbloquear; "¿Olvidaste el PIN?" (aviso: datos irrecuperables).
- **Auto-bloqueo:** tras inactividad configurable (RF-38).

### 3.4 Dashboard (Inicio) ⭐
- **Objetivo:** foto instantánea de la situación financiera.
- **Elementos:**
  - Tarjeta grande: **Saldo Total Disponible** (Bs.).
  - Secundario: **Patrimonio invertido**.
  - Resumen del ciclo: ingresos, gastos, balance.
  - Mini indicador de **tasa de ahorro**.
  - Carrusel de **metas** con progreso.
  - Últimas 5 transacciones.
  - FAB "+" (nueva transacción).
- **Acciones:** navegar a cada sección; tocar meta para aportar; tocar transacción para editar.

### 3.5 Nueva / Editar transacción
- **Objetivo:** registrar rápido.
- **Elementos:**
  - Selector segmentado: Ingreso / Gasto / Aporte a meta.
  - Campo monto (teclado numérico, formateo Bs.).
  - Fecha (por defecto hoy; selector).
  - Cuenta (dropdown; solo activas).
  - Categoría (chips; filtrada por tipo; botón "+ nueva").
  - Nota (opcional).
  - Toggle "Es fijo/común".
  - Toggle "Ingreso irregular" (solo si Ingreso).
- **Validaciones en línea:** monto > 0, cuenta obligatoria, saldo suficiente si aplica.
- **Acciones:** Guardar / Cancelar / Eliminar (si edita).

### 3.6 Movimientos (lista)
- **Objetivo:** explorar el historial.
- **Elementos:** lista agrupada por día; filtros (fecha, tipo, categoría, cuenta); buscador por nota; totales del período.
- **Acciones:** editar, eliminar (swipe), exportar CSV (Could).

### 3.7 Patrimonio / Cuentas
- **Objetivo:** gestionar fuentes de dinero.
- **Elementos:** tarjetas por cuenta con saldo; agrupadas por tipo; saldo total arriba.
- **Acciones:** crear, editar, desactivar, reordenar.

### 3.8 Metas (lista)
- **Objetivo:** gestionar objetivos de ahorro.
- **Elementos:** tarjeta por meta con barra de progreso, % y restante; estado.
- **Acciones:** crear, abrir detalle, filtrar por estado.

### 3.9 Detalle de meta
- **Objetivo:** seguimiento y aportes.
- **Elementos:** progreso grande; objetivo/actual/restante; fecha límite; aporte sugerido; gráfico de avance; historial de aportes.
- **Acciones:** Aportar; editar; pausar/reanudar; cancelar/eliminar.

### 3.10 Análisis (estadísticas)
- **Objetivo:** entender el comportamiento financiero.
- **Elementos:**
  - Selector de ciclo/período.
  - Balance (ingresos vs gastos) en tarjeta y gráfico de barras.
  - Gráfico de torta por categoría (con %).
  - Tasa de ahorro.
  - Proyección de fin de ciclo.
  - Presupuestos con barras de uso y semáforo.
  - Tarjeta de **insights**.
- **Acciones:** cambiar período; tocar categoría para ver detalle; gestionar presupuesto.

### 3.11 Ajustes
- **Objetivo:** configuración.
- **Subsecciones:**
  - **Cuentas** (acceso a gestión).
  - **Categorías** (CRUD, ícono, color, fijo/variable).
  - **Presupuestos** (crear/editar, umbral).
  - **Recurrentes** (plantillas).
  - **Respaldo** (exportar/importar JSON, copia .db, recordatorio).
  - **Seguridad** (PIN, biometría, auto-bloqueo).
  - **Ciclo financiero** (día de inicio).
  - **Apariencia** (tema claro/oscuro/dinámico).
  - **Acerca de** (versión, licencia, aviso de privacidad).

### 3.12 Widget de saldo
- **Objetivo:** ver el saldo sin abrir la app.
- **Elementos:** Saldo Total Disponible + balance del ciclo + botón de acceso rápido.
- **Actualización:** al cambiar datos o periódicamente (WorkManager).

---

## 4. Wireframes textuales (baja fidelidad)

```
┌──────────────────────────────┐    ┌──────────────────────────────┐
│  Inicio                🔒     │    │  Nueva transacción      ✕     │
│                              │    │                              │
│ ┌──────────────────────────┐ │    │  [ Ingreso | GASTO | Meta ]  │
│ │  SALDO DISPONIBLE        │ │    │                              │
│ │     Bs. 1.250,00         │ │    │  Monto:  [   150,00   ]      │
│ └──────────────────────────┘ │    │  Fecha:  [ 2026-11-04 ▾ ]    │
│  Invertido: Bs. 500,00       │    │  Cuenta: [ Efectivo   ▾ ]    │
│                              │    │  Categoría:                  │
│  Ciclo actual                │    │  (Alimentación)(Transporte)  │
│  ↑ Bs. 800   ↓ Bs. 540       │    │  (Servicios)(+ nueva)        │
│  Balance: Bs. 260 · Ahorro 32%│   │  Nota: [ ........... ]       │
│                              │    │  [x] Es gasto fijo           │
│  Metas                       │    │                              │
│  ▓▓▓▓▓░░░░ Laptop  55%       │    │      [   GUARDAR   ]         │
│  ▓▓░░░░░░░ Viaje   20%       │    │                              │
│                              │    └──────────────────────────────┘
│  Últimos movimientos         │
│  • Alimentación  -30          │    ┌──────────────────────────────┐
│  • Bono        +800           │    │  Análisis                    │
│                          (+)  │    │  Ciclo: [ Nov 2026 ▾ ]        │
└──────────────────────────────┘    │  Balance: +Bs. 260           │
                                     │  ┌───────┐  Tasa ahorro       │
 [Inicio][Movim.][Metas][Análisis]   │  │ 🥧 %  │  32%              │
 [Ajustes → en el 5.º ítem]          │  └───────┘                    │
                                     │  Presupuestos                │
                                     │  Alimentación ██████░ 78%    │
                                     │  Transporte   ███░░░░ 41%    │
                                     └──────────────────────────────┘
```

### Comportamiento responsive

| Ancho | Navegación | Layout |
|---|---|---|
| < 600 dp (celular) | Navigation Bar inferior | 1 columna |
| 600–840 dp (tablet/plegable) | Navigation Rail lateral | 1–2 columnas |
| > 840 dp | Rail + panel doble | 2 columnas (lista/detalle) |

Se usa `WindowSizeClass` de Compose para adaptar automáticamente.

---

## 5. Flujo detallado de acciones frecuentes

### 5.1 Registrar un gasto (camino feliz)
```
Dashboard → FAB (+) → [Gasto] → monto → cuenta → categoría → Guardar
   (4 toques + tipeo) → snackbar "Gasto registrado" → dashboard actualizado
```

### 5.2 Aportar a una meta
```
Dashboard → tarjeta de meta → [Aportar] → monto → cuenta → Confirmar
   → progreso actualizado + snackbar
```

### 5.3 Ver el análisis
```
Análisis → (por defecto ciclo actual) → revisar balance, torta, ahorro,
   presupuestos, insights
```

### 5.4 Respaldar
```
Ajustes → Respaldo → [Exportar JSON] → SAF (elegir carpeta) → archivo guardado
```

### 5.5 Restaurar
```
Ajustes → Respaldo → [Importar] → SAF (elegir archivo) → confirmar reemplazo
   → datos restaurados + reinicio de vistas
```

---

## 6. Estados de UI

| Estado | Representación |
|---|---|
| Cargando | Skeleton / spinner. |
| Vacío | Ilustración + texto guía ("Aún no registraste movimientos. Toca +"). |
| Error | Mensaje claro + acción de reintento. |
| Éxito | Snackbar + actualización reactiva. |
| Bloqueado | LockScreen. |

---

## 7. Mensajes y microcopy (extractos)

- Bienvenida: *"Tus finanzas, 100 % en tu teléfono. Sin internet, sin cuentas, sin que nadie mire."*
- Sin datos: *"Empezá registrando tu primer movimiento."*
- Error de PIN: *"PIN incorrecto. Intentá de nuevo."* (bloqueo progresivo).
- Aviso de respaldo: *"Hacé una copia de seguridad para no perder tus datos."*
- Meta completada: *"🎉 ¡Meta lograda! Ya tenés lo que necesitabas."*
- Presupuesto: *"Atención: usaste el 80 % de tu presupuesto de Alimentación."*

---

## 8. Manual de usuario (resumen — ISO/IEC/IEEE 26514)

### 8.1 Primeros pasos
1. Instalá la app (no requiere conexión).
2. Creá tu primera cuenta (Efectivo, Billetera QR o Inversión) con su saldo inicial.
3. (Opcional) Activá PIN/biometría en Ajustes → Seguridad.

### 8.2 Registrar ingresos
- Tocá "+", elegí "Ingreso", ingresá el monto y la fecha (cualquier día).
- Si es un bono irregular, activá "Ingreso irregular" para no distorsionar promedios.

### 8.3 Registrar gastos
- Tocá "+", elegí "Gasto", monto, cuenta y categoría. Marcá "fijo" si es recurrente.

### 8.4 Ver tu situación
- En **Inicio** ves el saldo disponible y el balance del ciclo.
- En **Análisis** ves el detalle por categoría, tu tasa de ahorro y proyecciones.

### 8.5 Crear y aportar a metas
- **Metas** → "+" → nombre, monto objetivo, fecha límite.
- Abrí la meta y tocá "Aportar" para asignar dinero.

### 8.6 Respaldar tus datos
- **Ajustes → Respaldo → Exportar**. Guardá el archivo en un lugar seguro.
- Para restaurar: **Importar** y elegí el archivo.

### 8.7 Preguntas frecuentes
- *¿Necesito internet?* No, nunca.
- *¿Mis datos se suben a algún lado?* No. Quedan solo en tu teléfono.
- *¿Qué pasa si olvido el PIN?* Por seguridad, los datos no se pueden recuperar sin la clave.
- *¿Puedo usar la app en modo avión?* Sí, funciona igual.

---

**Fin del documento 06.**
