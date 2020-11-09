package com.example.habitsexchangehelper.repository

import com.example.habitsexchangehelper.entity.Currency
import com.example.habitsexchangehelper.net.RatesApiService
import io.reactivex.rxjava3.core.Single
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RatesRepository @Inject constructor(private val ratesApiService: RatesApiService) {


    fun getRate(base: Currency, target: Currency) : Single<BigDecimal> {
        return ratesApiService.getRates(base.name)
            .map { it.rates.get(target.name)}
            /*.doOnComplete({
                //save to prefs
            })*/
    }

}