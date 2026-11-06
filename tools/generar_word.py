"""
Generador del documento Word (.docx) con toda la documentación del proyecto Platica.
Uso: python tools/generar_word.py
Requiere: python-docx
"""
import os
from docx import Document
from docx.shared import Pt, RGBColor, Inches, Cm
from docx.enum.text import WD_ALIGN_PARAGRAPH, WD_BREAK
from docx.enum.table import WD_TABLE_ALIGNMENT
from docx.enum.section import WD_SECTION
from docx.oxml.ns import qn
from docx.oxml import OxmlElement

BASE = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
DOCS = os.path.join(BASE, "docs")
SALIDA = os.path.join(BASE, "Platica_Documentacion.docx")

AZUL = RGBColor(0x1B, 0x3A, 0x6B)
AZUL_CLARO = RGBColor(0x2E, 0x5A, 0x9C)
GRIS = RGBColor(0x55, 0x55, 0x55)

# Orden de los documentos a incluir
ARCHIVOS = [
    "01_vision_y_alcance.md",
    "02_requisitos.md",
    "03_arquitectura.md",
    "04_modelo_de_datos.md",
    "05_logica_de_negocio.md",
    "06_flujo_ui_ux.md",
    "07_seguridad_y_respaldo.md",
    "08_tecnologia.md",
    "09_calidad_y_pruebas.md",
    "10_plan_de_proyecto.md",
    "11_glosario.md",
    "12_trazabilidad.md",
]

TITULOS = {
    "01": "Visión y Alcance del Proyecto",
    "02": "Especificación de Requisitos (SRS)",
    "03": "Arquitectura del Sistema",
    "04": "Modelo de Datos Local",
    "05": "Lógica de Negocio y Cálculos",
    "06": "Flujo UI/UX y Manual de Usuario",
    "07": "Seguridad y Respaldo Offline",
    "08": "Tecnología y Stack Técnico",
    "09": "Calidad, Verificación y Pruebas",
    "10": "Plan de Proyecto y Ciclo de Vida",
    "11": "Glosario y Acrónimos",
    "12": "Matriz de Trazabilidad",
}


def set_cell_bg(cell, color_hex):
    tc_pr = cell._tc.get_or_add_tcPr()
    shd = OxmlElement("w:shd")
    shd.set(qn("w:val"), "clear")
    shd.set(qn("w:color"), "auto")
    shd.set(qn("w:fill"), color_hex)
    tc_pr.append(shd)


def add_page_number(paragraph):
    run = paragraph.add_run()
    fld1 = OxmlElement("w:fldChar"); fld1.set(qn("w:fldCharType"), "begin")
    instr = OxmlElement("w:instrText"); instr.set(qn("xml:space"), "preserve"); instr.text = "PAGE"
    fld2 = OxmlElement("w:fldChar"); fld2.set(qn("w:fldCharType"), "end")
    run._r.append(fld1); run._r.append(instr); run._r.append(fld2)


def configurar_documento(doc):
    sec = doc.sections[0]
    sec.page_height = Cm(29.7)
    sec.page_width = Cm(21.0)
    sec.top_margin = Cm(2.5)
    sec.bottom_margin = Cm(2.5)
    sec.left_margin = Cm(2.5)
    sec.right_margin = Cm(2.5)

    normal = doc.styles["Normal"]
    normal.font.name = "Calibri"
    normal.font.size = Pt(11)

    for lvl, size, color in [("Heading 1", 20, AZUL), ("Heading 2", 15, AZUL_CLARO),
                             ("Heading 3", 13, AZUL_CLARO)]:
        st = doc.styles[lvl]
        st.font.name = "Calibri Light"
        st.font.size = Pt(size)
        st.font.color.rgb = color
        st.font.bold = True


def portada(doc):
    for _ in range(4):
        doc.add_paragraph()
    p = doc.add_paragraph(); p.alignment = WD_ALIGN_PARAGRAPH.CENTER
    r = p.add_run("PLATICA"); r.font.size = Pt(54); r.font.bold = True; r.font.color.rgb = AZUL
    p = doc.add_paragraph(); p.alignment = WD_ALIGN_PARAGRAPH.CENTER
    r = p.add_run("Control de Gastos y Gestión de Metas"); r.font.size = Pt(20); r.font.color.rgb = AZUL_CLARO
    p = doc.add_paragraph(); p.alignment = WD_ALIGN_PARAGRAPH.CENTER
    r = p.add_run("Aplicación móvil Android — 100% offline (funciona sin internet ni megas)")
    r.font.size = Pt(13); r.font.color.rgb = GRIS
    doc.add_paragraph()
    p = doc.add_paragraph(); p.alignment = WD_ALIGN_PARAGRAPH.CENTER
    r = p.add_run("Documentación técnica y de requisitos"); r.font.size = Pt(14); r.font.bold = True
    p = doc.add_paragraph(); p.alignment = WD_ALIGN_PARAGRAPH.CENTER
    r = p.add_run("Conforme a ISO/IEC/IEEE 29148, 26514 y 12207 (IEEE 830)"); r.font.size = Pt(11); r.italic = True
    for _ in range(6):
        doc.add_paragraph()
    for txt in ["Versión: 1.0", "Fecha: 2026-11-05",
                "Autor: Arquitecto de Software", "Lugar: Bolivia — Moneda: BOB (Bs.)"]:
        p = doc.add_paragraph(); p.alignment = WD_ALIGN_PARAGRAPH.CENTER
        r = p.add_run(txt); r.font.size = Pt(11); r.font.color.rgb = GRIS
    doc.add_page_break()


def tabla_de_contenido(doc):
    doc.add_heading("Índice de contenido", level=1)
    for clave, titulo in TITULOS.items():
        p = doc.add_paragraph()
        r = p.add_run(f"{clave}.  {titulo}")
        r.font.size = Pt(12)
    doc.add_page_break()


def parsear_y_escribir(doc, texto):
    """Parser minimalista de Markdown -> Word."""
    lineas = texto.split("\n")
    i = 0
    while i < len(lineas):
        linea = lineas[i].rstrip()

        # Tablas markdown
        if linea.startswith("|") and i + 1 < len(lineas) and set(lineas[i + 1].replace("|", "").strip()) <= set("-: "):
            filas = []
            while i < len(lineas) and lineas[i].strip().startswith("|"):
                if not set(lineas[i].replace("|", "").strip()) <= set("-: "):
                    celdas = [c.strip() for c in lineas[i].strip().strip("|").split("|")]
                    filas.append(celdas)
                i += 1
            if filas:
                ncol = max(len(f) for f in filas)
                tabla = doc.add_table(rows=0, cols=ncol)
                tabla.style = "Light Grid Accent 1"
                tabla.alignment = WD_TABLE_ALIGNMENT.CENTER
                for ridx, fila in enumerate(filas):
                    cells = tabla.add_row().cells
                    for cidx in range(ncol):
                        txt = fila[cidx] if cidx < len(fila) else ""
                        txt = limpiar(txt)
                        cells[cidx].text = txt
                        for par in cells[cidx].paragraphs:
                            for run in par.runs:
                                run.font.size = Pt(9)
                                if ridx == 0:
                                    run.font.bold = True
                    if ridx == 0:
                        for c in range(ncol):
                            set_cell_bg(cells[c], "1B3A6B")
                            for par in cells[c].paragraphs:
                                for run in par.runs:
                                    run.font.color.rgb = RGBColor(0xFF, 0xFF, 0xFF)
                doc.add_paragraph()
            continue

        # Bloques de código
        if linea.strip().startswith("```"):
            i += 1
            bloque = []
            while i < len(lineas) and not lineas[i].strip().startswith("```"):
                bloque.append(lineas[i])
                i += 1
            i += 1
            p = doc.add_paragraph()
            p.paragraph_format.left_indent = Cm(0.4)
            p.paragraph_format.space_before = Pt(4)
            p.paragraph_format.space_after = Pt(8)
            r = p.add_run("\n".join(bloque))
            r.font.name = "Consolas"; r.font.size = Pt(9)
            continue

        if linea.startswith("#"):
            nivel = len(linea) - len(linea.lstrip("#"))
            texto_h = limpiar(linea.lstrip("#").strip())
            doc.add_heading(texto_h, level=min(nivel, 4))
            i += 1
            continue

        if linea.strip().startswith("> "):
            p = doc.add_paragraph()
            p.paragraph_format.left_indent = Cm(0.5)
            r = p.add_run(limpiar(linea.strip()[2:]))
            r.italic = True; r.font.color.rgb = GRIS; r.font.size = Pt(10)
            i += 1
            continue

        if linea.strip() in ("---", "***", "___"):
            p = doc.add_paragraph()
            pPr = p._p.get_or_add_pPr()
            pbdr = OxmlElement("w:pBdr")
            bottom = OxmlElement("w:bottom")
            bottom.set(qn("w:val"), "single"); bottom.set(qn("w:sz"), "6")
            bottom.set(qn("w:color"), "BBBBBB")
            pbdr.append(bottom); pPr.append(pbdr)
            i += 1
            continue

        # Viñetas y numeradas
        if linea.strip().startswith("- ") or linea.strip().startswith("* "):
            p = doc.add_paragraph(style="List Bullet")
            agregar_runs(p, limpiar(linea.strip()[2:]))
            i += 1
            continue

        if linea.strip() == "":
            i += 1
            continue

        p = doc.add_paragraph()
        agregar_runs(p, limpiar(linea))
        i += 1


def limpiar(t):
    t = t.replace("✅", "[OK]").replace("⚠️", "[!]").replace("⚠", "[!]")
    t = t.replace("❌", "[X]").replace("🔒", "").replace("🔔", "").replace("🎯", "")
    t = t.replace("📊", "").replace("🌟", "").replace("⭐", "").replace("🚀", "")
    t = t.replace("∪", "U").replace("∧", "y").replace("Σ", "Suma")
    return t


def agregar_runs(p, texto):
    """Procesa **negrita** y `código` simples."""
    import re
    partes = re.split(r"(\*\*.*?\*\*|`.*?`)", texto)
    for parte in partes:
        if not parte:
            continue
        if parte.startswith("**") and parte.endswith("**"):
            r = p.add_run(parte[2:-2]); r.bold = True
        elif parte.startswith("`") and parte.endswith("`"):
            r = p.add_run(parte[1:-1]); r.font.name = "Consolas"; r.font.size = Pt(10)
        else:
            p.add_run(parte)


def pie_de_pagina(doc):
    for sec in doc.sections:
        footer = sec.footer
        p = footer.paragraphs[0]
        p.alignment = WD_ALIGN_PARAGRAPH.CENTER
        r = p.add_run("Platica — Documentación v1.0   |   Página ")
        r.font.size = Pt(9); r.font.color.rgb = GRIS
        add_page_number(p)


def main():
    doc = Document()
    configurar_documento(doc)
    portada(doc)
    tabla_de_contenido(doc)
    pie_de_pagina(doc)
    for archivo in ARCHIVOS:
        ruta = os.path.join(DOCS, archivo)
        with open(ruta, "r", encoding="utf-8") as f:
            contenido = f.read()
        parsear_y_escribir(doc, contenido)
        doc.add_page_break()

    doc.save(SALIDA)
    print(f"Documento generado: {SALIDA}")


if __name__ == "__main__":
    main()
