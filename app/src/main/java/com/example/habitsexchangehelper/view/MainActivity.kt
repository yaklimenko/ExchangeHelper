package com.example.habitsexchangehelper.view

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.habitsexchangehelper.ExchangeApp
import com.example.habitsexchangehelper.R
import com.example.habitsexchangehelper.databinding.ActivityMainBinding
import com.example.habitsexchangehelper.di.ActivityComponent
import com.example.habitsexchangehelper.di.AppModule
import com.example.habitsexchangehelper.di.DaggerActivityComponent
import com.example.habitsexchangehelper.di.NetworkModule
import com.example.habitsexchangehelper.entity.Currency
import com.example.habitsexchangehelper.repository.RatesRepository
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import javax.inject.Inject

class MainActivity : AppCompatActivity() {


    @Inject
    lateinit var ratesRepository: RatesRepository

    private lateinit var viewModel: ExchangeViewModel

    private val currencies = enumValues<Currency>()
    private val baseButtons: EnumMap<Currency, Button> = EnumMap<Currency, Button>(Currency::class.java)
    private val targetButtons:EnumMap<Currency, Button> = EnumMap<Currency, Button>(Currency::class.java)

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
        viewModel = vm
        viewModel.inject(getActivityComponent())
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.viewmodel = viewModel

        generateCurrencyButtons()
    }

    private fun generateCurrencyButtons() {
        currencies.forEach { c ->
            val btn = layoutInflater.inflate(R.layout.button_currency, null) as Button
            btn.text = c.name.toUpperCase(Locale.ROOT)
            btn.setOnClickListener {
                viewModel.onBaseCurrencySet(c)
                switchCurrencyBackToBtn(btn, baseButtons)
            }
            baseButtons[c]= btn
            buttonBarBase.addView(btn)
            val btnTarget = layoutInflater.inflate(R.layout.button_currency, null) as Button
            btnTarget.text = c.name.toUpperCase(Locale.ROOT)
            btnTarget.setOnClickListener {
                viewModel.onTargetCurrencySet(c)
                switchCurrencyBackToBtn(btnTarget, targetButtons)
            }
            targetButtons[c] = btnTarget
            buttonBarTarget.addView(btnTarget)
        }
    }

    private fun switchCurrencyBackToBtn(buttonToSelectd: Button, group: EnumMap<Currency, Button>) {
        for (button in group.values) {
            if (button == buttonToSelectd) {
                button.setBackgroundColor(SELECTED_BUTTON_BACKGROUND)
            } else {
                button.setBackgroundColor(UNSELECTED_BUTTON_BACKGROUND)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.baseCurrencySelected.observe(this, {
            baseButtons[it]!!.setBackgroundColor(SELECTED_BUTTON_BACKGROUND)
        })
        viewModel.targetCurrencySelected.observe(this, {
            targetButtons[it]!!.setBackgroundColor(SELECTED_BUTTON_BACKGROUND)
        })
        viewModel.targetAmountField.observe(this, {
            amountEditText2.setText(it)
        })
        viewModel.baseAmountFieldUp.observe(this, {
            amountEditText.setText(it)
        })
        viewModel.errorMsg.observe( this, {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })

        viewModel.onActivityStarted()
    }

    override fun onStop() {
        super.onStop()
        viewModel.saveState()
    }

    companion object {
        private var SELECTED_BUTTON_BACKGROUND = Color.RED
        private var UNSELECTED_BUTTON_BACKGROUND = Color.TRANSPARENT
    }
}