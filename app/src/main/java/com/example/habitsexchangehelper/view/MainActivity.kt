package com.example.habitsexchangehelper.view

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
        val vm: ExchangeViewModel by viewModels()
        vm.checkSavedBaseAmount()

    }

    override fun onStart() {
        super.onStart()
        viewModel.onViewStarted(getActivityComponent())

    }
}