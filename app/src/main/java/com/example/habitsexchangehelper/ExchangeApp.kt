package com.example.habitsexchangehelper

import android.app.Application
import com.example.habitsexchangehelper.di.AppComponent
import com.example.habitsexchangehelper.di.AppModule
import com.example.habitsexchangehelper.di.DaggerAppComponent

class ExchangeApp : Application() {


    lateinit var appComponent: AppComponent

    override fun onCreate() {
        initAppComponent()
        super.onCreate()
    }

    private fun initAppComponent() {
        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }

}