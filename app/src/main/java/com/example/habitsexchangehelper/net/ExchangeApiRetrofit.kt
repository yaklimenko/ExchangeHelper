package com.example.habitsexchangehelper.net

import com.example.habitsexchangehelper.entity.DateFromMilliseconds
import com.example.habitsexchangehelper.entity.Rates
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.reactivex.rxjava3.core.Single
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Singleton

interface ExchangeApiRetrofit {

    //curl 'https://freecurrencyapi.net/api/v2/latest?apikey=03b94ce0-60fa-11ec-9daa-9561c48637db'
    @GET("latest")
    fun latest(@Query("base_currency") base: String) : Single<Rates>

    companion object Factory {

        fun create(): ExchangeApiRetrofit {

            val gson: Gson = GsonBuilder()
                .registerTypeAdapter(DateFromMilliseconds::class.java, DateFromMillisecondsAdapter())
                .create()

            val client = OkHttpClient.Builder()
                .addInterceptor(ApiKeyInterceptor())
                .addInterceptor(LoggingInterceptor())
                .build()

            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .baseUrl("https://freecurrencyapi.net/api/v2/")
                .build()

            return retrofit.create(ExchangeApiRetrofit::class.java);
        }
    }
}

@Singleton
class RatesApiService {

    private val exchangeApi = ExchangeApiRetrofit.create()

    fun getRates(base: String): Single<Rates> {
        return exchangeApi.latest(base)
    }

}

