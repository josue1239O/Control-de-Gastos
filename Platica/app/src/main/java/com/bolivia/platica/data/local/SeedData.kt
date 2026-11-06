package com.bolivia.platica.data.local

import com.bolivia.platica.data.local.entity.AccountEntity
import com.bolivia.platica.data.local.entity.CategoryEntity
import com.bolivia.platica.domain.model.enums.AccountType
import com.bolivia.platica.domain.model.enums.CategoryType

/**
 * Datos semilla que se insertan la primera vez que se abre la app,
 * para que el usuario tenga categorías y cuentas útiles desde el inicio.
 */
object SeedData {

    val cuentas: List<AccountEntity> = listOf(
        AccountEntity(nombre = "Efectivo", tipo = AccountType.EFECTIVO, saldoInicial = 0.0, orden = 0),
        AccountEntity(nombre = "Billetera QR", tipo = AccountType.BILLETERA_QR, saldoInicial = 0.0, orden = 1),
        AccountEntity(nombre = "Fondo de emergencias", tipo = AccountType.EMERGENCIA, saldoInicial = 0.0, orden = 2),
        AccountEntity(nombre = "Ahorro", tipo = AccountType.AHORRO, saldoInicial = 0.0, orden = 3),
        AccountEntity(nombre = "Inversión", tipo = AccountType.INVERSION, saldoInicial = 0.0, orden = 4)
    )

    val categorias: List<CategoryEntity> = listOf(
        // ---------- GASTOS ----------
        CategoryEntity(nombre = "Alimentación", tipo = CategoryType.GASTO, esFijo = false, icono = "restaurant"),
        CategoryEntity(nombre = "Transporte", tipo = CategoryType.GASTO, esFijo = false, icono = "directions_bus"),
        CategoryEntity(nombre = "Servicios", tipo = CategoryType.GASTO, esFijo = true, icono = "bolt"),
        CategoryEntity(nombre = "Vivienda", tipo = CategoryType.GASTO, esFijo = true, icono = "home"),
        CategoryEntity(nombre = "Educación", tipo = CategoryType.GASTO, esFijo = true, icono = "school"),
        CategoryEntity(nombre = "Salud", tipo = CategoryType.GASTO, esFijo = false, icono = "favorite"),
        CategoryEntity(nombre = "Entretenimiento", tipo = CategoryType.GASTO, esFijo = false, icono = "sports_esports"),
        CategoryEntity(nombre = "Ropa", tipo = CategoryType.GASTO, esFijo = false, icono = "checkroom"),
        CategoryEntity(nombre = "Otros gastos", tipo = CategoryType.GASTO, esFijo = false, icono = "more_horiz"),

        // ---------- INGRESOS ----------
        CategoryEntity(nombre = "Bono", tipo = CategoryType.INGRESO, esFijo = true, icono = "card_giftcard"),
        CategoryEntity(nombre = "Mesada", tipo = CategoryType.INGRESO, esFijo = true, icono = "savings"),
        CategoryEntity(nombre = "Trabajo eventual", tipo = CategoryType.INGRESO, esFijo = false, icono = "work"),
        CategoryEntity(nombre = "Ayuda familiar", tipo = CategoryType.INGRESO, esFijo = false, icono = "family_restroom"),
        CategoryEntity(nombre = "Otros ingresos", tipo = CategoryType.INGRESO, esFijo = false, icono = "more_horiz")
    )
}
