package com.example.habitsexchangehelper.repository

import com.example.habitsexchangehelper.entity.Currency
import com.example.habitsexchangehelper.entity.Rates
import com.example.habitsexchangehelper.net.RatesApiService
import com.example.habitsexchangehelper.persist.PrefsService
import io.reactivex.rxjava3.core.Single
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RatesRepository @Inject constructor(
    private val ratesApiService: RatesApiService,
    private val prefsService: PrefsService
) {

    private val cache: EnumMap<Currency, EnumMap<Currency, BigDecimal>> =
        EnumMap<Currency, EnumMap<Currency, BigDecimal>>(Currency::class.java)

    fun getRates(base: Currency): Single<Map<Currency, BigDecimal>> {
        if (cache.containsKey(base)) {
            return Single.just(cache[base])
        }
        return ratesApiService.getRates(base.name)
            .map {
                val rightMap = transformRawRates(it)
                cache[base] = rightMap
                prefsService.saveRates(base, rightMap)
                return@map rightMap
            }
    }

    private fun getSavedRates(base: Currency): Single<Map<Currency, BigDecimal>> {
        return Single.create { emitter ->
            if (prefsService.hasRates(base)) {
                prefsService.getRates(base).subscribe { res ->
                    val rates: EnumMap<Currency, BigDecimal> =
                        EnumMap<Currency, BigDecimal>(Currency::class.java)
                    rates.putAll(res)
                    cache[base] = rates
                    emitter.onSuccess(rates)
                }
            } else {
                emitter.onError(NullPointerException("no saved rates"))
            }
        }
    }

    private fun transformRawRates(it: Rates): EnumMap<Currency, BigDecimal> {
        val res = EnumMap<Currency, BigDecimal>(Currency::class.java)
        it.rates.forEach { rateEntry ->
            if (enumValues<Currency>().any { it.name == rateEntry.key}) {
                val cur = Currency.valueOf(rateEntry.key)
                res[cur] = rateEntry.value
            }
        }
        return res
    }

    fun getSavedInputBaseAmount() : Single<BigDecimal> =
        if (prefsService.hasInputBaseAmount()) prefsService.getInputBaseAmount() else Single.just(BigDecimal.ZERO)



    fun saveInputBaseAmount(amount: BigDecimal) {
        prefsService.saveInputBaseAmount(amount)
    }

}