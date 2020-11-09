package com.example.habitsexchangehelper.net

import com.example.habitsexchangehelper.entity.Rates
import io.reactivex.rxjava3.core.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Singleton

interface ExchangeApi {

    //GET https://api.exchangeratesapi.io/latest?base=USD
    @GET("latest")
    fun latest(@Query("base") base: String) : Single<Rates>


    companion object Factory {
        fun create(): ExchangeApi {
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api.exchangeratesapi.io/")
                .build()

            return retrofit.create(ExchangeApi::class.java);
        }
    }
}

@Singleton
class RatesApiService {

    val exchangeApi = ExchangeApi.create()

    fun getRates(base: String): Single<Rates> {
        return exchangeApi.latest(base)
    }

}