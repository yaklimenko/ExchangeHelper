package com.example.habitsexchangehelper.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.habitsexchangehelper.ExchangeApp
import com.example.habitsexchangehelper.R
import com.example.habitsexchangehelper.di.ActivityComponent
import com.example.habitsexchangehelper.di.DaggerActivityComponent
import com.example.habitsexchangehelper.di.NetworkModule
import com.example.habitsexchangehelper.entity.Currency
import com.example.habitsexchangehelper.persist.PrefsService
import com.example.habitsexchangehelper.repository.RatesRepository
import io.reactivex.rxjava3.kotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.adapter.rxjava3.HttpException
import javax.inject.Inject

class MainActivity : AppCompatActivity() {


    @Inject
    lateinit var ratesRepository : RatesRepository

    @Inject
    lateinit var prefsService: PrefsService

    @Inject
    lateinit var ratesRepository2 : RatesRepository

    @Inject
    lateinit var prefsService2: PrefsService

    private lateinit var activityComponent: ActivityComponent

    fun getActivityComponent(): ActivityComponent {
        return DaggerActivityComponent
            .builder()
            .appComponent((application as ExchangeApp).appComponent)
            .networkModule(NetworkModule())
            .build()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        getActivityComponent().inject(this)
//        ratesRepository = (application as ExchangeApp).appComponent.getRatesRepository()
//        prefsService = (application as ExchangeApp).appComponent.getPrefsService()
//
//        val prefsService2 = (application as ExchangeApp).appComponent.getPrefsService()
//        val ratesRepository2 = (application as ExchangeApp).appComponent.getRatesRepository()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()

    }
}