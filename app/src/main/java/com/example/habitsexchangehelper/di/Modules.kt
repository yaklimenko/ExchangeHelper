package com.example.habitsexchangehelper.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.habitsexchangehelper.net.RatesApiService
import com.example.habitsexchangehelper.persist.PrefsService
import com.example.habitsexchangehelper.repository.RatesRepository
import dagger.Module
import dagger.Provides

@Module
class AppModule(val app: Application) {
    @Provides
    fun providesAppContext(): Context = app

    @Provides
    fun provideSharedPrefs(): SharedPreferences =
        app.getSharedPreferences("exchange-prefs", Context.MODE_PRIVATE)

    @Provides
    fun providesPrefsService(context: Context): PrefsService = PrefsService(context)
}

@Module
class NetworkModule {
    @Provides
    fun providesRatesApiService(): RatesApiService = RatesApiService()
}

@Module(includes = arrayOf(NetworkModule::class))
class RatesModule {
    @Provides
    fun providesRatesRepository(ratesApiService: RatesApiService): RatesRepository =
        RatesRepository(ratesApiService)
}