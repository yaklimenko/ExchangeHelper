package com.example.habitsexchangehelper.entity

import org.joda.time.DateTime
import java.math.BigDecimal

data class Rates (
    val query: Query,
    val data: Map<String, BigDecimal>,
    )

data class Query (
    val base_currency: Currency,
    val timestamp: DateFromMilliseconds,
)

class DateFromMilliseconds (
    val date: DateTime
)