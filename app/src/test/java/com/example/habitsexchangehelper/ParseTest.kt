package com.example.habitsexchangehelper

import com.example.habitsexchangehelper.entity.Rates
import com.google.gson.Gson
import org.junit.Assert
import org.junit.Test


class ParseTest {

    @Test
    fun testParse() {
        val json = """
            {
               "rates":{
                  "CAD":1.5525,
                  "HKD":9.203,
                  "ISK":163.5,
                  "PHP":57.192,
                  "DKK":7.4493,
                  "HUF":359.02,
                  "CZK":26.667,
                  "AUD":1.6359,
                  "RON":4.867,
                  "SEK":10.2805,
                  "IDR":16943.12,
                  "INR":88.0085,
                  "BRL":6.6072,
                  "RUB":92.42,
                  "HRK":7.559,
                  "JPY":122.66,
                  "THB":36.287,
                  "CHF":1.0682,
                  "SGD":1.5999,
                  "PLN":4.5263,
                  "BGN":1.9558,
                  "TRY":10.1489,
                  "CNY":7.8468,
                  "NOK":10.9203,
                  "NZD":1.7507,
                  "ZAR":18.6933,
                  "USD":1.187,
                  "MXN":24.684,
                  "ILS":4.0076,
                  "GBP":0.9043,
                  "KRW":1332.6,
                  "MYR":4.9005
               },
               "base":"EUR",
               "date":"2010-04-06"
            }
        """.trimIndent()
        val gson = Gson()
        val rates = gson.fromJson(json, Rates::class.java)
        Assert.assertNotNull(rates);
    }
}
