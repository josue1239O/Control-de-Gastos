# Platica — Documentación del Proyecto

> **Aplicación Android de Control de Finanzas Personales y Gestión de Metas — 100% Offline para Estudiantes de Bolivia**

[![Estado](https://img.shields.io/badge/Estado-Documentación%20v1.0-blue)]()
[![Plataforma](https://img.shields.io/badge/Plataforma-Android%20(Nativo)-green)]()
[![Offline](https://img.shields.io/badge/Modo-100%25%20Offline-orange)]()
[![Moneda](https://img.shields.io/badge/Moneda-BOB%20(Bs.)-yellow)]()

---

## 1. Propósito de esta documentación

Este repositorio contiene el **conjunto documental del proyecto Platica**, elaborado conforme a las siguientes normas internacionales:

| Norma | Aplicación en este proyecto |
|---|---|
| **IEEE 830 / ISO/IEC/IEEE 29148:2018** | Especificación de Requisitos de Software (SRS): requisitos funcionales, no funcionales, casos de uso y trazabilidad. |
| **ISO/IEC/IEEE 26514:2022** | Diseño y desarrollo de documentación de usuario e información del software. |
| **ISO/IEC/IEEE 12207:2017** | Procesos del ciclo de vida del software (obtención, desarrollo, operación, mantenimiento). |

---

## 2. Índice de documentos

| N.º | Documento | Norma de referencia | Descripción |
|---|---|---|---|
| 00 | [README.md](README.md) | — | Este índice general. |
| 01 | [01_vision_y_alcance.md](01_vision_y_alcance.md) | 29148 §5 | Visión, alcance, en qué consiste el proyecto, stakeholders y contexto. |
| 02 | [02_requisitos.md](02_requisitos.md) | IEEE 830 / 29148 §9 | Especificación de Requisitos de Software (SRS) completa. |
| 03 | [03_arquitectura.md](03_arquitectura.md) | 12207 §6.4 / ISO 42010 | Arquitectura del sistema, capas, componentes y vistas. |
| 04 | [04_modelo_de_datos.md](04_modelo_de_datos.md) | — | Esquema entidad-relación, tablas, campos, FKs e índices. |
| 05 | [05_logica_de_negocio.md](05_logica_de_negocio.md) | 29148 §9 | Reglas de negocio, fórmulas analíticas y de metas. |
| 06 | [06_flujo_ui_ux.md](06_flujo_ui_ux.md) | 26514 | Flujo de navegación, pantallas y documentación de usuario. |
| 07 | [07_seguridad_y_respaldo.md](07_seguridad_y_respaldo.md) | 12207 §6.4.7 | Cifrado, autenticación local y estrategia de respaldo offline. |
| 08 | [08_tecnologia.md](08_tecnologia.md) | 12207 §6.4.5 | Stack tecnológico, justificación y matriz de decisión. |
| 09 | [09_calidad_y_pruebas.md](09_calidad_y_pruebas.md) | ISO/IEC 25010 / 12207 | Atributos de calidad, verificación y validación. |
| 10 | [10_plan_de_proyecto.md](10_plan_de_proyecto.md) | 12207 §6.3 | Procesos del ciclo de vida, fases, roadmap y entregables. |
| 11 | [11_glosario.md](11_glosario.md) | 26514 | Glosario de términos y acrónimos. |
| 12 | [12_trazabilidad.md](12_trazabilidad.md) | 29148 §9.5 | Matriz de trazabilidad requisitos → diseño → prueba. |

---

## 3. Resumen ejecutivo del proyecto

**Platica** es una aplicación móvil Android de finanzas personales diseñada específicamente para **estudiantes bolivianos** que reciben ingresos irregulares (bonos, mesadas, ayudas familiares) y necesitan llevar un control ordenado de su dinero **sin depender de internet ni de servicios en la nube**.

La aplicación permite:

1. **Gestionar el patrimonio** distribuido en Efectivo, Billeteras digitales/QR e Inversiones/Ahorro.
2. **Registrar ingresos y gastos** de forma rápida y diaria.
3. **Analizar** la situación financiera (balance, tasa de ahorro, gasto por categoría).
4. **Definir y seguir metas** de ahorro con progreso visual.
5. **Respaldar** los datos localmente en un archivo cifrado.

**Principio rector:** todos los datos viven **exclusivamente en el dispositivo**. La aplicación no solicita el permiso `INTERNET`, por lo que el sistema operativo le impide, a nivel de hardware/lógica, establecer cualquier conexión de red.

---

## 4. Convenciones de lectura

- `RF-XX` → Requisito Funcional.
- `RNF-XX` → Requisito No Funcional.
- `RN-XX` → Regla de Negocio.
- `CU-XX` → Caso de Uso.
- Los montos se expresan en **Bolivianos (Bs.)**, salvo indicación contraria.
- 🔒 indica un elemento relacionado con seguridad.
- ⚠️ indica una restricción o advertencia importante.

---

## 5. Estado y control de versiones del documento

| Versión | Fecha | Autor | Descripción |
|---|---|---|---|
| 1.0 | 2026-11-04 | Arquitecto de Software | Versión inicial completa de la documentación. |

> **Aviso:** Este set documental es la **línea base** del proyecto. Cualquier cambio debe registrarse aquí siguiendo el control de cambios descrito en `10_plan_de_proyecto.md`.

---

© 2026 — Documento vivo. Generado bajo lineamientos ISO/IEC/IEEE 29148, 26514 y 12207.
