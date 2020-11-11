package com.example.habitsexchangehelper.persist

import android.content.SharedPreferences
import com.example.habitsexchangehelper.entity.Currency
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleOnSubscribe
import io.reactivex.rxjava3.functions.Action
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class PrefsService @Inject constructor(private val sharedPreferences: SharedPreferences) {



    fun hasRates(base: Currency): Boolean =
        sharedPreferences.contains(base.name)

    fun getRates(base: Currency): Single<HashMap<Currency, BigDecimal>> {
        return Single.create(SingleOnSubscribe { emitter ->
            val json = sharedPreferences.getString(base.name, "")
            if ("".equals(json)) {
                emitter.onError(NullPointerException("no rates for ${base.name}"))
            } else {
                val type = object : TypeToken<HashMap<Currency, BigDecimal>>() {}.type
                emitter.onSuccess(Gson().fromJson(json, type))
            }
        })
    }

    fun saveRates(base: Currency, rates: Map<Currency, BigDecimal>): Completable {
        return Single.just(Gson().toJson(rates))
            .flatMapCompletable {
                saveString(base.name, it)
            }
    }

    fun saveInputBaseAmount(amount: BigDecimal): Completable {
        return Single.just(amount.toEngineeringString())
            .flatMapCompletable {
                saveString(INPUT_BASE_AMOUNT, it)
            }
    }

    fun hasInputBaseAmount(): Boolean {
        return sharedPreferences.contains(INPUT_BASE_AMOUNT)
    }

    fun getInputBaseAmount(): Single<BigDecimal> {
        return Single.create(SingleOnSubscribe { emitter ->
            val str = sharedPreferences.getString(INPUT_BASE_AMOUNT, "")
            if ("".equals(str)) {
                emitter.onError(NullPointerException("no INPUT_BASE_AMOUNT"))
            } else {
                emitter.onSuccess(BigDecimal(str))
            }
        })
    }

    private fun saveString(k: String, v: String): Completable {
        return Completable.fromAction( Action {
            sharedPreferences.edit()
                .putString(k, v)
                .apply()
        })
    }

    companion object {
        const val INPUT_BASE_AMOUNT = "inputBaseAmount"
    }
}