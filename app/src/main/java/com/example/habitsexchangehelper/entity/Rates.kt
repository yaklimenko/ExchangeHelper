package com.example.habitsexchangehelper.entity

import java.math.BigDecimal
import java.util.*

data class Rates (
    val query: Query,
    val data: Map<String, BigDecimal>,
    )

data class Query (
    val base_currency: Currency,
    val date: Date, //TODO json adapter
)