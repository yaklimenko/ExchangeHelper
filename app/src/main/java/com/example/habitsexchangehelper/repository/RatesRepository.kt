package com.example.habitsexchangehelper.repository

import com.example.habitsexchangehelper.entity.Currency
import com.example.habitsexchangehelper.entity.Rates
import com.example.habitsexchangehelper.net.RatesApiService
import com.example.habitsexchangehelper.persist.PrefsService
import io.reactivex.rxjava3.core.Completable
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

    fun getRates(base: Currency): Single<Map<Currency, BigDecimal>> =
        if (cache.containsKey(base)) Single.just(cache[base])
        else ratesApiService.getRates(base.name)
            .map { transformRawRates(it) }
            .flatMap {
                prefsService.saveRates(base, it)
                    .andThen(Single.just(it))
            }


    fun getRate(base: Currency, target: Currency): Single<BigDecimal> =
        getRates(base).map { it[target] }

    fun getSavedRate(base: Currency, target: Currency): Single<BigDecimal> =
        getSavedRates(base).map { it[target] }

    fun saveRates(base: Currency, rates: Map<Currency, BigDecimal>) =
        prefsService.saveRates(base, rates)

    private fun getSavedRates(base: Currency): Single<Map<Currency, BigDecimal>> =
        Single.create { emitter ->
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


    private fun transformRawRates(it: Rates): EnumMap<Currency, BigDecimal> {
        val res = EnumMap<Currency, BigDecimal>(Currency::class.java)
        it.rates.forEach { rateEntry ->
            if (enumValues<Currency>().any { it.name == rateEntry.key }) {
                val cur = Currency.valueOf(rateEntry.key)
                res[cur] = rateEntry.value
            }
        }
        return res
    }

    fun getBaseAmount(): Single<BigDecimal> =
        if (prefsService.hasBaseAmount()) prefsService.getInputBaseAmount()
        else Single.just(BigDecimal.ZERO)

    fun saveBaseAmount(amount: BigDecimal) = prefsService.saveInputBaseAmount(amount)

    fun getSavedBaseCurrency(): Single<Currency> =
        if (prefsService.hasBaseCurrency()) prefsService.getBaseCurrency()
        else Single.just(Currency.RUB)

    fun getSavedTargetCurrency(): Single<Currency> =
        if (prefsService.hasTargetCurrency()) prefsService.getTargetCurrency()
        else Single.just(Currency.USD)

    fun saveCurrencies(base: Currency, target: Currency): Completable {
        return prefsService.saveBaseCurrency(base)
            .andThen(prefsService.saveTargetCurrency(target))
    }

}