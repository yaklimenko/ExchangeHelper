package com.example.habitsexchangehelper.di

import android.content.Context
import com.example.habitsexchangehelper.persist.PrefsService
import com.example.habitsexchangehelper.repository.RatesRepository
import com.example.habitsexchangehelper.view.ExchangeViewModel
import com.example.habitsexchangehelper.view.MainActivity
import dagger.Component

@Component(modules = arrayOf(AppModule::class, NetworkModule::class))
interface AppComponent {
    fun getAppContext(): Context
}

@Component(dependencies = arrayOf(AppComponent::class), modules = arrayOf(RatesModule::class))
interface ActivityComponent {
    fun inject(mainActivity: MainActivity)
    fun inject(viewModel: ExchangeViewModel)
}
