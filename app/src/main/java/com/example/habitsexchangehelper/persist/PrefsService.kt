package com.example.habitsexchangehelper.persist

import android.content.SharedPreferences
import com.example.habitsexchangehelper.entity.Currency
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class PrefsService @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {

    fun hasRates(base: Currency): Boolean =
        sharedPreferences.contains(base.name + "Rate")

    fun getRates(base: Currency): Single<HashMap<Currency, BigDecimal>> {
        return Single.create { emitter ->
            val json = sharedPreferences.getString(base.name + "Rate", "")
            if (json.isNullOrEmpty()) {
                emitter.onError(NullPointerException("no rates for ${base.name}"))
            } else {
                val type = object : TypeToken<HashMap<Currency, BigDecimal>>() {}.type
                emitter.onSuccess(Gson().fromJson(json, type))
            }
        }
    }

    fun saveRates(base: Currency, rates: Map<Currency, BigDecimal>): Completable {
        return Single.just(Gson().toJson(rates))
            .flatMapCompletable {
                saveString(base.name + "Rate", it)
            }
    }

    fun saveInputBaseAmount(amount: BigDecimal): Completable {
        return Single.just(amount.toEngineeringString())
            .flatMapCompletable {
                saveString(BASE_AMOUNT, it)
            }
    }

    fun hasBaseAmount(): Boolean {
        return sharedPreferences.contains(BASE_AMOUNT)
    }

    fun getInputBaseAmount(): Single<BigDecimal> {
        return Single.create { emitter ->
            val str = sharedPreferences.getString(BASE_AMOUNT, "")
            if (str.isNullOrEmpty()) {
                emitter.onError(NullPointerException("no INPUT_BASE_AMOUNT saved"))
            } else {
                emitter.onSuccess(BigDecimal(str))
            }
        }
    }

    private fun saveString(k: String, v: String): Completable {
        return Completable.fromAction {
            sharedPreferences.edit()
                .putString(k, v)
                .apply()
        }
    }

    fun saveBaseCurrency(base: Currency): Completable {
        return Completable.fromAction {
            sharedPreferences.edit()
                .putString(BASE_CURRENCY, base.name)
                .apply()
        }
    }

    fun hasBaseCurrency() : Boolean {
        return sharedPreferences.contains(BASE_CURRENCY)
    }

    fun hasTargetCurrency() : Boolean {
        return sharedPreferences.contains(TARGET_CURRENCY)
    }

    fun getBaseCurrency() : Single<Currency> {
        return Single.create { emitter ->
            val str = sharedPreferences.getString(BASE_CURRENCY, "")
            if (str.isNullOrEmpty()) {
                emitter.onError(NullPointerException("no BASE_CURRENCY saved"))
            } else {
                emitter.onSuccess(Currency.valueOf(str))
            }
        }
    }

    fun getTargetCurrency() : Single<Currency> {
        return Single.create { emitter ->
            val str = sharedPreferences.getString(TARGET_CURRENCY, "")
            if (str.isNullOrEmpty()) {
                emitter.onError(NullPointerException("no TARGET_CURRENCY saved"))
            } else {
                emitter.onSuccess(Currency.valueOf(str))
            }
        }
    }

    fun saveTargetCurrency(target: Currency): Completable {
        return Completable.fromAction {
            sharedPreferences.edit()
                .putString(TARGET_CURRENCY, target.name)
                .apply()
        }
    }

    companion object {
        const val BASE_AMOUNT = "inputBaseAmount"
        const val BASE_CURRENCY = "baseCurrency"
        const val TARGET_CURRENCY = "targetCurrency"
    }
}