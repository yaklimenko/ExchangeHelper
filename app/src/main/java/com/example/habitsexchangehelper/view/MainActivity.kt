package com.example.habitsexchangehelper.view

import android.graphics.Color
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.habitsexchangehelper.ExchangeApp
import com.example.habitsexchangehelper.R
import com.example.habitsexchangehelper.di.ActivityComponent
import com.example.habitsexchangehelper.di.AppModule
import com.example.habitsexchangehelper.di.DaggerActivityComponent
import com.example.habitsexchangehelper.di.NetworkModule
import com.example.habitsexchangehelper.entity.Currency
import com.example.habitsexchangehelper.repository.RatesRepository
import kotlinx.android.synthetic.main.activity_main.*
import java.text.DecimalFormat
import javax.inject.Inject

class MainActivity : AppCompatActivity() {


    @Inject
    lateinit var ratesRepository: RatesRepository

    private lateinit var viewModel: ExchangeViewModel
    private lateinit var activityComponent: ActivityComponent

    private fun getActivityComponent(): ActivityComponent {
        return DaggerActivityComponent
            .builder()
            .appComponent((application as ExchangeApp).appComponent)
            .networkModule(NetworkModule())
            .appModule(AppModule(application))
            .build()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        getActivityComponent().inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnCurRUR.setOnClickListener {
            viewModel.onBaseCurrencySet(Currency.RUB)
            btnCurRUR.setBackgroundColor(Color.RED)
        }
        btnCurCHF.setOnClickListener { viewModel.onBaseCurrencySet(Currency.CHF)
        }
        btnCurCNY.setOnClickListener { viewModel.onBaseCurrencySet(Currency.CNY) }
        btnCurEUR.setOnClickListener { viewModel.onBaseCurrencySet(Currency.EUR) }
        btnCurUSD.setOnClickListener { viewModel.onBaseCurrencySet(Currency.USD) }
        btnCurGBP.setOnClickListener { viewModel.onBaseCurrencySet(Currency.GPB) }

        btnCurRUR2.setOnClickListener { viewModel.onTargetCurrencySet(Currency.RUB) }
        btnCurCHF2.setOnClickListener { viewModel.onTargetCurrencySet(Currency.CHF) }
        btnCurCNY2.setOnClickListener { viewModel.onTargetCurrencySet(Currency.CNY) }
        btnCurEUR2.setOnClickListener { viewModel.onTargetCurrencySet(Currency.EUR) }
        btnCurUSD2.setOnClickListener { viewModel.onTargetCurrencySet(Currency.USD) }
        btnCurGBP2.setOnClickListener { viewModel.onTargetCurrencySet(Currency.GPB) }

        val vm: ExchangeViewModel by viewModels()
        viewModel = vm

    }

    override fun onStart() {
        super.onStart()
        viewModel.onViewStarted(getActivityComponent())
        viewModel.recoverSavedBaseAmount()

    }
}