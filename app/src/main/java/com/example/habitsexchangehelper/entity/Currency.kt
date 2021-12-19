package com.example.habitsexchangehelper.entity

enum class Currency {
    EUR, RUB, USD, GBP, CHF, CNY
}

fun Currency.getListExcept(except: Currency): String =
    enumValues<Currency>()
        .filter { c -> c != except }
        .joinToString()