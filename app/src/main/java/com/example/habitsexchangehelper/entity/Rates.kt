package com.example.habitsexchangehelper.entity

import java.math.BigDecimal
import java.util.*

data class Rates (val base: Currency, val date: Date, val rates: Map<String, BigDecimal>)